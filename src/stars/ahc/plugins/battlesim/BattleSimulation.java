/*
 * Created on Oct 31, 2004
 *
 * Copyright (c) 2004, Steve Leach
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
package stars.ahc.plugins.battlesim;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import stars.ahc.ShipDesign;
import stars.ahc.Utils;
import stars.ahc.Weapon;

/**
 * Simulate a Stars! battle
 * 
 * @author Steve Leach, with help from LEit, Kotk, mazda, Ptolemy, Micha, et.al. from Home World Forum
 */
public class BattleSimulation
{
   /**
    * Records the damage done by a weapons salvo 
    */
   public class DamageRecord
   {
      int shieldDamage = 0;
      int armourDamage = 0;
      int kills = 0;
   }
   
   /**
    * Battles are divided into up to 16 rounds, with each weapon firing once per round
    */
   protected int round = 0;
   /**
    * Stores the ship stack details
    */
   protected ShipStack[] stacks;
   /**
    * Maxmimum number of stacks that can participate in a battle
    */
   protected static final int MAX_STACKS = 256; 
   /**
    * The number of stacks of ships involved in this battle
    */
   protected int stackCount = 0;
   /**
    * The maximum number of rounds that a battle can have
    */
   public static final int MAX_ROUNDS = 16;
   /**
    * The maxmimum initiative (hull+computers+weapon) that a ship can have
    */
   public static final int MAX_INITIATIVE = 64;
   /**
    * The number of moves a stack must make before disengaging
    */
   // TODO: check whether this should be 7 or 8
   public static final int DISENGAGE_MOVES = 7;
   
   public static final int BBOARD_MIN_X = 1;
   public static final int BBOARD_MAX_X = 10;
   public static final int BBOARD_MIN_Y = 1;
   public static final int BBOARD_MAX_Y = 10;
   
   /**
    * The seed for the random number generator.
    * <p>
    * If set to 0 (the default) a random seed will be used and the results will be different
    * every time the simulation is run.  If a non-zero value is used then the simulation should
    * procede exactly the same every time, assuming initial starting conditions remain the same. 
    */
   public long randomSeed = 0L;
   
   /**
    * The random number generator for the simulation.
    */
   private Random randomNumberGenerator;
   
   private ArrayList statusListers = new ArrayList();

   // From the Stars! help file - may be slightly inaccurate
   protected static int[][] movement = {
         { 1,	0,	1,	0,	1,	0,	1,	0 },		// 0.5
         { 1,	1,	0,	1,	1,	1,	0,	1 },		// 0.75
         { 1,	1,	1,	1,	1,	1,	1,	1 },		// 1
         { 2,	1,	1,	1,	2,	1,	1,	1 },		// 1.25
         { 2,	1,	2,	1,	2,	1,	2,	1 },		// 1.5
         { 2,	2,	1,	2,	2,	2,	1,	2 },		// 1.75
         { 2,	2,	2,	2,	2,	2,	2,	2 },		// 2
         { 3,	2,	2,	2,	3,	2,	2,	2 },		// 2.25
         { 3,	2,	3,	2,	3,	2,	3,	2 }			// 2.5
   };
   
   /**
    * Damage is applied in units of totalArmour/500
    */
   private static final int DAMAGE_UNIT = 500;
   
   //protected static int[][] distanceTable = null;
   
   /**
    * Creates a Battle Simulation object supporting up to 16 ship stacks 
    */
   public BattleSimulation()
   {
      initStacks( MAX_STACKS );
   }
   
   public BattleSimulation( int maxStacks )
   {
      initStacks( maxStacks );
   }
   
   public BattleSimulation( String fileName ) throws IOException
   {
      loadFrom( fileName );
   }
   
   /**
    * Initialise the stacks array 
    */
   protected void initStacks( int maxStacks )
   {
      stacks = new ShipStack[maxStacks];  
   }
   
   /**
    * Adds a new ship stack to the battle simulation  
    */
   public void addStack( ShipStack stack )
   {
      stacks[stackCount++] = stack;
   }
   
   /**
    * Adds a new ship stack, using the specified design and ship count
    * <p>
    * Each stack added with this method is assumed to belong to a different owner
    * 
    * @return a reference to the newly created stack 
    */
   public ShipStack addNewStack( ShipDesign design, int count )
   {
      return addNewStack( design, count, stackCount+1 );
   }
   
   /**
    * Adds a new ship stack, using the specified design, ship count and side
    * <p>
    * @return a reference to the newly created stack 
    */
   public ShipStack addNewStack( ShipDesign design, int count, int side )
   {
      ShipStack stack = new ShipStack(design,count);
      stack.side = side;
      
      addStack( stack );
      
      return stack;
   }
   
   /**
    * Returns the number of times a ship with the specified speed can move in a particular round
    * <p>
    * @param speed4 is the battle speed multiplied by 4
    * @param round is the battle round concerned
    */
   protected int movesInRound( int speed4, int round )
   {
      if (speed4 == 0) return 0;
      if (round > 8) round -= 8;
      return movement[speed4-1][round-1];
   }

   /**
    * Run the simulation until one side is dead or 16 rounds are up
    * @throws BattleSimulationError
    */
   public void simulate() throws BattleSimulationError
   {
      initialiseBattleBoard();
      
      pickTargets();
      
      while ( stillFighting() )
      {
         simulateNextRound();
      }
      
      // Show the results of the battle
      showStackDetails();
   }

   /**
    * Run the simulation repeatedly, storing accumulated results so that statistics can be examined 
    */
   public void simulateRepeatedly( int iterations ) throws BattleSimulationError
   {
      for (int n = 0; n < stackCount; n++)
      {
         stacks[n].resetAccumulator();
      }
      
      for (int n = 0; n < iterations; n++)
      {
         simulate();
         
         accumulateResults();
      }
   }
   
   /**
    * Adds the results of the latest simulation into the accumulator
    */
   private void accumulateResults()
   {
      for (int n = 0; n < stackCount; n++)
      {
         stacks[n].accumulateResults();
      }
   }

   /**
    * Sets up the starting state of the battle  
    * 
    * @throws BattleSimulationError
    */
   protected void initialiseBattleBoard() throws BattleSimulationError
   {
      round = 0;
      
      initializeRandomNumberGenerator();
      
      for (int n = 0; n < stackCount; n++)
      {
         stacks[n].reset();
      }
      
      setInitialPositions();
   }
      
   /**
    * Initialises the random number generator for use in the battle
    * <p> 
    * If a random seed has been specified then it will be used so that the battle
    * can be reproduced.  Otherwise a new random seed will be picked (based on the
    * current system time in millisecond) which will cause the battle to have a
    * different result each time.
    */
   private void initializeRandomNumberGenerator()
   {
      long seed;
      
      if (randomSeed != 0L)
      {
         seed = randomSeed;
      }
      else
      {
         // Use a 24 bit value to make it easier for people to make a note of it and re-use it
         seed = System.currentTimeMillis() & 0x00FFFFFFL;          
      }
            
      randomNumberGenerator = new Random(seed);
      
      statusUpdate( "Random number generator initialised using seed: " + seed );
   }

   /**
    * Sets the initial position of the stacks 
    */
   protected void setInitialPositions() throws BattleSimulationError
   {
      int races = countRaces();
      
      Point[] positions = {};
      
      switch (races)
      {
         case 2:
            positions = new Point[] { new Point(2,5), new Point(8,5) };
            break;
         case 3:
            positions = new Point[] { new Point(5,2), new Point(2,9), new Point(9,9) };
            break;
         case 4:
            positions = new Point[] { new Point(2,2), new Point(9,9), new Point(2,9), new Point(9,2) };
            break;
         // TODO: complete for 5 to 16 races
         default:
            throw new BattleSimulationError( "Cannot handle more than 2 sides yet" );
      }

      for (int n = 0; n < stackCount; n++)
      {
         Point pos = positions[ stacks[n].raceIndex-1 ];
         stacks[n].xpos = pos.x;
         stacks[n].ypos = pos.y;
      }      
   }
   
   /**
    * Counts the races involved in the battle, and assigns each stack a race index number  
    */
   private int countRaces()
   {
      Map races = new HashMap();
      int raceCount = 0;
      
      for (int n = 0; n < stackCount; n++)
      {
         Integer index = (Integer)races.get(stacks[n].owner); 
         if (index == null)
         {
            raceCount++;
            index = new Integer(raceCount);
            races.put( stacks[n], index );            
         }
         stacks[n].raceIndex = index.intValue();
      }
      
      return raceCount;
   }
   
   /**
    * Returns the number of sides in the battle 
    */
   private int countSides()
   {
      int sides = 0;
      
      // TODO: remove assumption that side numbers are contiguous
      
      for (int n = 0; n < stackCount; n++)
      {
         if (stacks[n].side > sides)
         {
            sides++;
         }
      }
      
      return sides;
   }
   
   /**
    * Display the status of all the stacks 
    */
   public void showStackDetails()
   {
      for (int n = 0; n < stackCount; n++)
      {
         statusUpdate( stacks[n].toString() );
      }
   }
   
   /**
    * Simulate the next round of combat
    * <p>
    * This is implemented by the concrete subclasses 
    */
   /**
    * Simulate the next round of combat.  
    * <p>
    * Each round there are up to 3 movement phases and one firing phase.
    * @throws BattleSimulationError
    */
   public void simulateNextRound() throws BattleSimulationError
   {
      round++;
      
      regenShields();
      
      calculatePreferredRange();
      
      moveStacks();
      
      checkForDisengage();
      
      if (stillFighting())
      {
         fireWeapons();
      }
   }
   
   
   /**
    * Returns true if there are still at least 2 stacks fighting in the battle
    */
   public boolean stillFighting()
   {
      int stacksFighting = 0;
      
      for (int n = 0; n < stackCount; n++)
      {
         if (stacks[n].stillFighting())
         {
            stacksFighting++;
         }
      }
      
      return (round < MAX_ROUNDS) && (stacksFighting > 1);
   }

   /**
    * Damage done by beam weapons decreases with range. This calculates the reduction.
    * <p> 
    * Returns the percentage of maximum damage that is done at the specified distance.
    * 
    * @param distance is the distance from which the shot is fired
    * @param weaponRange is the maximum range of the weapon
    */
   public static double getRangeMultiplier(int distance, int weaponRange)
   {
      if (distance == 0) return 1.0;
      
      double n = 0.1 * distance / weaponRange;
      
      return 1.0 - n;
   }
   
   /**
    * Returns the specified ship stack 
    */
   public ShipStack getStack( String designName )
   {
      for (int n = 0; n < stacks.length; n++)
      {
         if (stacks[n].design.getName().equals(designName))
         {
            return stacks[n];
         }
      }
      return null;
   }
   
   /**
    * Registers an object is interested in changes to the status of the simulation 
    */
   public void addStatusListener( BattleSimulationListener listener )
   {
      if (listener != null)
      {
         statusListers.add( listener );
      }
   }
  
   /**
    * Records that the status of the simulation has changed
    * <p>
    * The new status is broadcast to all registered status listeners.
    * 
    * @see addStatusListener
    */
   public void statusUpdate( String msg )
   {
      BattleSimulationNotification notification = new BattleSimulationNotification();
      notification.round = round;
      notification.message = msg;      
      
      statusUpdate( notification );
   }
   
   public void statusUpdate( BattleSimulationNotification notification )
   {
      for (int n = 0; n < statusListers.size(); n++)
      {
         try
         {
            BattleSimulationListener listener = (BattleSimulationListener)statusListers.get(n);
            listener.handleNotification( notification );
         }
         catch (Throwable t)
         {
            // Catch any errors to prevent a buggy listener crashing the simulation
            t.printStackTrace();
         }
      }
   }

   /**
    * For ships with regenerating shields, factor in the shield regeneration 
    */
   protected void regenShields()
   {
      for (int n = 0; n < stackCount; n++)
      {
         if (stacks[n].design.isRegenShields())
         {
            int beforeRegen = stacks[n].shields;
            int originalShields = stacks[n].design.getShields() * stacks[n].shipCount;
            int regen = originalShields / 10;
            
            stacks[n].shields += regen;
            if (stacks[n].shields > originalShields)
            {
               stacks[n].shields = originalShields;
            }
            
            if (stacks[n].shields != originalShields)
            {
               statusUpdate( stacks[n].toString() + " shields regenerate from " + beforeRegen + " to " + stacks[n].shields + " dp" );
            }
         }
      }
   }

   /**
    * Calculate the range at which the stack would like to fight, based on battle orders 
    */
   protected void calculatePreferredRange(ShipStack stack)
   {
      ShipDesign design = stack.design;
      ShipDesign target = stack.target.design;
      
      // TODO: improve on this simplified system
      
      if (stack.battleOrders == ShipStack.ORDERS_DISENGAGE)
      {
         // Disengaging ships want to be as far as possible from their target
         stack.preferredRange = Integer.MAX_VALUE;
      }
      else if (design.getMaxRange() > target.getMaxRange())
      {
         // We have better range, so stay out of range of their weapons
         // Should this use "our max range", or "their max range + 1" ?
         stack.preferredRange = design.getMaxRange();
      }
      else
      {
         // They have equal or better range, so just close to range 0
         stack.preferredRange = 0;
      }
   }

   /**
    * Calculate the range at which the ships would like to fight, based on battle orders 
    */
   protected void calculatePreferredRange()
   {
      for (int n = 0; n < stackCount; n++)
      {
         calculatePreferredRange( stacks[n] );
      }         
   }
   
   /**
    * Returns a pseudo-random number 0.0 <= x < 1.0 
    */
   protected float getRandomFloat()
   {
      float f = randomNumberGenerator.nextFloat(); 
      System.out.println( "Next random: " + f );
      return f;
   }
   
   /**
    * Returns a pseudo-random number 0 <= x < max 
    */
   protected int getRandomInt( int max )
   {
      int i = randomNumberGenerator.nextInt(max);
      System.out.println( "Next random: " + i );
      return i;
   }

   /**
    * To give heavier ships a slight chance to move last, randomly adjust ship mass slightly (up to 15%). 
    */
   protected void randomizeMass()
   {
      for (int n = 0; n < stackCount; n++)
      {
         // Get a random number between 0.85 and 1.15
         float random = getRandomFloat() * 0.3f + 0.85f;
         
         // Mass for movement order is design mass times this random number
         stacks[n].randomMass = (int)( random * stacks[n].design.getMass() );
      }      
   }

   /**
    * Sort the stacks by randomized mass, heaviest to lightest
    * <p>
    * Heavier ships move first, so the lighter designs get to pick the range.
    */
   protected void sortByShipMass()
   {
      randomizeMass();
   
      Arrays.sort( stacks, 0, stackCount, new Comparator() {
         public int compare(Object arg0, Object arg1)
         {
            ShipStack stack0 = (ShipStack)arg0;
            ShipStack stack1 = (ShipStack)arg1;
            return stack1.randomMass - stack0.randomMass;
         }
      } );
   }

   /**
    * Returns the distance between two stacks if one of them moves slightly
    * <p>
    * @param xOffset, yOffset - changes to apply to stack 1's position before calculating the distance  
    */
   public int distanceBetween(ShipStack stack1, ShipStack stack2, int xOffset, int yOffset)
   {
      int dx = Math.abs( (stack1.xpos + xOffset) - stack2.xpos );
      int dy = Math.abs( (stack1.ypos + yOffset) - stack2.ypos );
   
      return Math.max( dx, dy );      
   }

   /**
    * Return the distance between two stacks
    */
   public int distanceBetween(ShipStack stack1, ShipStack stack2)
   {
      return distanceBetween( stack1, stack2, 0, 0 );
   }

   /**
    * Check whether any stacks have managed to disengage 
    */
   protected void checkForDisengage()
   {
      for (int n = 0; n < stackCount; n++)
      {
         checkForDisengage( stacks[n] );
      }
   }

   /**
    * Check whether a stack has managed to disengage
    * <p>
    * A stack disengages if it's orders are to disengage and if it has
    * moved a certain number of times  
    */
   private void checkForDisengage(ShipStack stack)
   {
      if (stack.battleOrders == ShipStack.ORDERS_DISENGAGE)
      {
         if (stack.movesMade >= DISENGAGE_MOVES)
         {
            stack.escaped = true;
            statusUpdate( stack.toString() + " escapes into hyperspace " );
         }
      }
   }

   /**
    * Returns the adjusted accuracy level of a weapon
    * <p>
    * This takes into account and computers on the attacker and any jammers on the defender.
    * <p>
    * @param baseAccuracy is the basic accuracy level of the weapon
    */
   public static double getFinalAccuracy(double baseAccuracy, ShipDesign attacker, ShipDesign defender)
   {
      int jamming = defender.getJamming();
      int computing = attacker.getComputing();
      
      if (jamming > computing)
      {
         jamming -= computing;
         
         return (1.0 - (jamming / 100.0)) * baseAccuracy;
      }
      else if (computing > jamming)
      {
         computing -= jamming;

         return 1.0 - (1 - baseAccuracy) * ((100 - computing) / 100.0);
      }
      else // computing == jamming
      {
         return baseAccuracy;
      }
   }
     
   /**
    * Move the ship stacks
    * <p>
    * Each ship design has a battle speed of between 0.25 and 2.5.  This speed determines how
    * many time the stack can move each round - between 1 and 3 moves.
    */
   protected void moveStacks()
   {
      sortByShipMass();
   
      moveStacks( 3 );		// Move any stacks that have a movement of 3 in this round 
      moveStacks( 2 );		// Move any stacks that have a movement of at least 2 in this round
      moveStacks( 1 );		// Move any stacks that have a movement of at least 1 in this round
   }
   
   /**
    * Move any stacks that can move above the specified threshold number of squares per round 
    */
   protected void moveStacks(int threshold)
   {
      for (int n = 0; n < stackCount; n++)
      {
         if (movesInRound(stacks[n].design.getSpeed4(),round) >= threshold)
         {
            if (stacks[n].battleOrders == ShipStack.ORDERS_DISENGAGE)
            {
               moveDisengagingStack( stacks[n] );               
            }
            else
            {
               moveStack( stacks[n] );
            }
         }
      }      
   }

   /**
    * Calculates the number of ships killed
    * <p>
    * Important - the target stack is updated (damage and shipcount) as a side effect
    * 
    */
   protected int applyArmourDamage(int armourDamage, ShipStack target, int maxKills)
   {
      int kills;
      
      // TODO: armour damage should be handled in units of 1/500 of the total for the stack
      
      // How much damage has each ship sustained so far ?
      int damagePerShip = target.damage / target.shipCount;            
      int remainingArmourPerShip = target.design.getArmour() - damagePerShip;
      
      if (armourDamage > (remainingArmourPerShip * target.shipCount))
      {
         // Damage overwhelms all remaining ship armour
         kills = target.shipCount;
         
         // We can't kill more ships than there are in the stack
         if (kills > maxKills) kills = maxKills;
         
         target.shipCount -= kills;
      }
      else
      {      
         // How many ships have we killed ?  Do floating point math then round down
         kills = (int)Math.floor( 1.0 * armourDamage / remainingArmourPerShip );
                  
         // If this is a torp/missile salvo there may be a cap on the number of kills
         if (kills > maxKills) kills = maxKills;
         
         // We can't kill more ships than there are in the stack
         if (kills > target.shipCount) kills = target.shipCount;
         
         if (kills < 0)
         {
            statusUpdate("Error - kills = " + kills);
         }
         
         // How much damage is left over after killing ships
         int killDamage = kills * remainingArmourPerShip;         
         int damageRemainder = armourDamage - killDamage;
         
         target.shipCount -= kills;         
         target.damage = damagePerShip * target.shipCount + damageRemainder;
      }
            
      return kills;
   }

   /**
    * Simulates the specified weapons slot of the specified ship stack firing
    * @throws BattleSimulationError
    */
   protected void fireWeapon(ShipStack stack, int slot) throws BattleSimulationError
   {
      // Stacks with no ships left can't fire
      if (stack.shipCount <= 0)
      {
         return;
      }
      
      // Empty weapon slots can't fire
      if (stack.design.getWeaponCount(slot) == 0)
      {
         return;
      }
   
      switch (stack.design.getWeaponType(slot))
      {
         case Weapon.TYPE_BEAM:
         case Weapon.TYPE_SAPPER:
            fireBeamWeapon( stack, slot );
         	break;

         case Weapon.TYPE_GATTLING:
            fireGattlingWeapon( stack, slot );
         	break;
         	
         case Weapon.TYPE_MISSILE:
         case Weapon.TYPE_TORPEDO:
            fireTorpedoKotk( stack, slot );
          	break;
      }
   }

   /**
    * Finds the most attractive target that is within range of the specified weapon slot
    * <p>
    * Returns null if no viable target is found.  If the weapon is a sapper then stacks without
    * shields are ignored. 
    */
   public ShipStack pickTargetInRange( ShipStack stack, int slot ) throws BattleSimulationError
   {
      ShipStack target = null;
      double mostAttractive = 0;
      
      int range = stack.design.getWeaponRange(slot);
      boolean isSapper = stack.design.getWeaponType(slot) == Weapon.TYPE_SAPPER;
      
      for (int n = 0; n < stackCount; n++)
      {
         if (stacks[n].side != stack.side)
         {
            if (stacks[n].shipCount > 0)
            {
	            if (distanceBetween(stack,stacks[n]) < range)
	            {
	               if ((isSapper == false) || (stacks[n].shields > 0))
	               {
	                  double attraction = getAttractiveness( stack, slot, stacks[n] );
	                  
	                  if (attraction > mostAttractive)
	                  {
	                     mostAttractive = attraction;
	                     target = stacks[n];
	                  }
	               }
	            }
            }
         }
      }
      
      return target;
   }

   private void sendShotNotification(ShipStack stack, int slot, DamageRecord damage, ShipStack target)
   {
      sendShotNotification( stack, slot, damage.shieldDamage, damage.armourDamage, damage.kills, target );
   }
   
   private void sendShotNotification(ShipStack stack, int slot, int shieldDamage, int armourDamage, int kills, ShipStack target)
   {
      BattleSimulationNotification notification = new BattleSimulationNotification();
      notification.round = round;
      notification.eventType = BattleSimulationNotification.TYPE_FIRE;
      notification.activeStack = stack;
      notification.targetStack = target;
      notification.message = stack.toString() + " fires " + stack.design.getWeaponName(slot) 
      				+ " [" + (slot+1) + "] at " + target.design.getName() + " doing " + shieldDamage + " damage to shields and " 
      				+ armourDamage + " to armour (" + kills + " kills)";
      statusUpdate( notification );
   }

   /**
    * Simulates firing torpedos and missiles 
    * 
    * @throws BattleSimulationError
    */
   private void fireTorpedo(ShipStack stack, int slot) throws BattleSimulationError
   {
      ShipDesign design = stack.design;		// for convenience      
      ShipStack target = stack.target;
   
      int range = distanceBetween( stack, target );
   
      // Are we out of range ?
      if (range > design.getWeaponRange(slot))
      {
         return;
      }

      // Is the target stack dead already ?
      // TODO: pick another target if it is
      if (stack.target.shipCount <= 0)
      {
         return;
      }
      
      int numShots = design.getWeaponCount(slot) * stack.shipCount;
      
      double accuracy = design.getWeaponAccuracy(slot);
      
      accuracy = getFinalAccuracy( accuracy, design, target.design );

      // How many actually hit ?
      int numHits = 0;
      for (int n = 0; n < numShots; n++)
      {
         if (getRandomFloat() < accuracy)
         {
            numHits++;
         }
      }
   
      int totalPower = numHits * design.getWeaponPower(slot);
      
      if ((target.shields == 0) && (design.getWeaponType(slot) == Weapon.TYPE_MISSILE))
      {
         // Capital ship missile and target has no shields - double damage
         totalPower *= 2;
      }
   
      // TODO: find out exactly what order the various affects are applied
      
      int shieldDamage = Math.min( target.shields, totalPower / 2 );
      int armourDamage = totalPower - shieldDamage;
      
      int kills = applyArmourDamage( armourDamage, target, numHits );
   
      // Torps that miss do 1/8 damage to shields only
      int numMisses = numShots - numHits;
      int missDamage = numMisses * design.getWeaponPower(slot) / 8;
      
      shieldDamage += missDamage;
      
      if (shieldDamage > target.shields)
      {
         shieldDamage = target.shields;
      }
      
      target.shields -= shieldDamage;

      if (target.shipCount == 0)
      {         
         pickTarget( stack );  // If we have killed all ships in the target stack, pick another
      }
      
      sendShotNotification( stack, slot, shieldDamage, armourDamage, kills, target );
   }
   
   /**
    * All ships fire their weapons 
    * @throws BattleSimulationError
    */
   protected void fireWeapons() throws BattleSimulationError
   {
      sortStacksForFiring();
      
      // Count down from maximum initiative (max initiative fires first)
      for (int initLevel = MAX_INITIATIVE; initLevel > 0; initLevel--)
      {
         // Originally we were checking weapon range here as specified in the 
         // Stars! manual, but it turns out that the manual is wrong.
         
         // Loop through stacks
         for (int n = 0; n < stackCount; n++)
         {
            ShipStack stack = stacks[n];
            ShipDesign design = stack.design;
            
            // Loop though weapons slots to find weapons with current initiative level & range
            for (int slot = 0; slot < design.getWeaponSlots(); slot++)
            {
               int init = design.getInitiative() + design.getWeaponInit(slot);
               int wpnRange = design.getWeaponRange(slot);
               
               if (init == initLevel)
               {
                  fireWeapon( stack, slot );
               }
            }
         }
      }
   }

   /**
    * Sorts the stacks before firing
    * <p>
    * Stacks are given a random firing order at the start of the battle. We
    * sort the stacks based on that value here.  This order only applies
    * as a tie-breaker when ships have equal initiative values.
    */
   protected void sortStacksForFiring()
   {
      Arrays.sort( stacks, 0, stackCount, new Comparator() {
         public int compare(Object arg0, Object arg1)
         {
            ShipStack stack0 = (ShipStack)arg0;
            ShipStack stack1 = (ShipStack)arg1;
            return stack0.firingOrderValue - stack1.firingOrderValue;
         }
      });
   }

   /**
    * Each stack selects a target to attack.
    * <p>
    * For a one-on-one battle, each just selects the other 
    * @throws BattleSimulationError
    */
   protected void pickTargets() throws BattleSimulationError
   {
      // Clear targeting
      for (int n = 0; n < stackCount; n++)
      {
         stacks[n].target = null;
         stacks[n].targetedBy = null;
      }
      
      for (int n = 0; n < stackCount; n++)
      {
         pickTarget( stacks[n] );
      }
   }

   /**
    * Asks the specified ship stack to select a target 
    * @throws BattleSimulationError
    */
   protected void pickTarget(ShipStack stack) throws BattleSimulationError
   {
      double bestAttractiveness = 0;
      int bestTarget = -1;

      // First, try selecting a target that has nothing targeting it already
      // TODO: confirm this is correct behaviour, and remove if not
      pickTarget( stack, false );
      
      // If that doesn't work, pick a target that is already targeted
      if (stack.target == null)
      {
         pickTarget( stack, true );
      }
      
      // If that doesn't work we have a problem
      if (stack.target == null)
      {
         throw new BattleSimulationError( "Could not select target for " + stack );
      }
   }
   
   protected void pickTarget(ShipStack stack, boolean includeAlreadyTargeted) throws BattleSimulationError
   {
      double bestAttractiveness = -1;
      int bestTarget = -1;
      
      for (int n = 0; n < stackCount; n++)
      {
         if (stacks[n].side != stack.side)	// don't target if on our side
         {
            if (stacks[n].shipCount > 0)	// don't target dead stacks
            {
	            if (includeAlreadyTargeted || (stacks[n].targetedBy == null))
	            {
	               int mainWeaponSlot = stack.design.getMainWeaponSlot();
	               double attractiveness = getAttractiveness( stack, mainWeaponSlot, stacks[n] );
	               
	               if (attractiveness > bestAttractiveness)
	               {
	                  bestTarget = n;
	                  bestAttractiveness = attractiveness;
	               }
	            }
            }
         }
      }
      
      if (bestTarget >= 0)
      {
         stack.target = stacks[bestTarget];
         stacks[bestTarget].targetedBy = stack;
      }
   }
   
   /**
    * Shows full details of all the stacks
    * <p>
    * Details are sent to all status listeners
    */
   public void showStacksFull()
   {
      for (int n = 0; n < stackCount; n++)
      {
         statusUpdate( stacks[n].getStackAsString() );
      }
   }
   
   /**
    * Returns the attractiveness of the specified target to the specified weapon on the attacker
    * <p>
    * Algorithm courtesy of Art Lathrop Stars! FAQ at http://www.starsfaq.com/advfaq/guts2.htm#4.14
    * 
    * @throws BattleSimulationError
    */
   public static double getAttractiveness( ShipStack attacker, int weaponSlot, ShipStack target ) throws BattleSimulationError
   {
      if (attacker.design.getWeaponSlots() == 0)
      {
         return 1.0;
      }
      
      // TODO: verify attractiveness algorithm
      
      double cost = target.design.getBoraniumCost() + target.design.getResourceCost();
      cost = (cost == 0) ? 1.0 : cost;
      
      double attackPowerNeeded;
      
      int armour = target.design.getArmour() * (100 - target.getDamagePercent()) / 100;
      int shields = target.shields / target.shipCount;
      
      double accuracy = attacker.design.getWeaponAccuracy(weaponSlot);
      accuracy = getFinalAccuracy( accuracy, attacker.design, target.design );
      
      // Algorithm for attackPowerNeeded depends on weapon type 
      switch (attacker.design.getWeaponType(weaponSlot))
      {
         case Weapon.TYPE_BEAM:
            attackPowerNeeded = getBeamAttackPowerNeeded( armour + shields, target.design.getDeflectors() );
            break;
         
         case Weapon.TYPE_SAPPER:
            attackPowerNeeded = getBeamAttackPowerNeeded( shields, target.design.getDeflectors() );
            break;
         
         case Weapon.TYPE_MISSILE:
            attackPowerNeeded = getMissileAttackPowerNeeded( armour, shields, accuracy, 2 );
            break;
         
         case Weapon.TYPE_TORPEDO:
            attackPowerNeeded = getMissileAttackPowerNeeded( armour, shields, accuracy, 1 );
            break;
         
         default:
            throw new BattleSimulationError( "Unknown weapon type for slot " + weaponSlot ); 
      }
      
      return cost / attackPowerNeeded;
   }

   /**
    * Gets an estimate of the defences of a design against beam attack
    */
   private static double getBeamAttackPowerNeeded(int defences, int deflectors)
   {
      double deflection = (deflectors == 0) ? 1.0 : Math.pow( 0.9, deflectors );
      return defences / deflection;
   }
   
   /**
    * Gets an estimate of the defences of a design against missile/torpedo attack
    */
   private static double getMissileAttackPowerNeeded( int armour, int shields, double accuracy, int typeModifier )
   {
      if (shields >= armour)
      {
         return armour * 2 / accuracy;
      }
      else
      {
         return (shields * 2 / accuracy) + (armour-shields) / (accuracy * typeModifier);
      }
   }
   
   /**
    * Moves the stack to a new location 
    */
   private void moveStack( ShipStack stack )
   {
      int bestRangeOffset = Integer.MAX_VALUE;
      
      // Store the original position
      int oldX = stack.xpos;
      int oldY = stack.ypos;
      
      ArrayList options = new ArrayList();	// List for storing possible movement options
      options.add( new Point(0,0) );		// Initial best option is to stay where we are
      
      for (int x = -1; x <= 1; x++)
      {
         for (int y = -1; y <= 1; y++)
         {
            // Don't consider squares that are off the edge of the board
            if ( validSquare(stack.xpos+x,stack.ypos+y) )
            {            
	            int range = distanceBetween( stack, stack.target, x, y );
	            
	            int rangeOffset = Math.abs( range - stack.preferredRange );
	            
	            if (rangeOffset < bestRangeOffset)
	            {
	               // This is a better option than any we have so far, so discard existing options
	               options.clear();
	               bestRangeOffset = rangeOffset;
	            }
	            
	            if (rangeOffset == bestRangeOffset)
	            {
	               // This is equal to the best so far, so add to list of options
	               options.add( new Point(x,y) );
	            }
            }
         }
      }
      
      // pick an option at random
      int optionCount = options.size();
      
      int optionChosen = getRandomInt(optionCount);	      
      Point offset = (Point)options.get(optionChosen);
      
      // and move the stack
      stack.xpos += offset.x;
      stack.ypos += offset.y;
      
      notifyStackMovement( stack, oldX, oldY );
   }
   
   /**
    * Tells all battle simulation listeners that a stack has moved  
    */
   private void notifyStackMovement( ShipStack mover, int oldX, int oldY )
   {
      BattleSimulationNotification event = new BattleSimulationNotification();
      event.eventType = BattleSimulationNotification.TYPE_MOVE;
      event.message = mover.toString() + " moves to " + mover.xpos + "," + mover.ypos + " (range " + distanceBetween(mover,mover.target) + ")";
      event.round = round;
      event.activeStack = mover;
      event.movedFrom = new Point(oldX,oldY);
      
      statusUpdate( event );      
   }
   
   /**
    * Returns true if the specified location is a valid square on the battle board  
    */
   private boolean validSquare( int x, int y )
   {
      return ((x >= BBOARD_MIN_X) && (x <= BBOARD_MAX_X) && (y >= BBOARD_MIN_Y) && (y <= BBOARD_MAX_Y));
   }
   
   /**
    * Move a ship stack that is attempting to disengage 
    */
   private void moveDisengagingStack( ShipStack stack )
   {
      // Currently just move randomly
      // TODO: figure out the correct implementation

      int oldx = stack.xpos;
      int oldy = stack.ypos;
      
      int x = getRandomInt(3) - 1;
      int y = getRandomInt(3) - 1;
      
      // Don't allow movement off the battle board, or no movement at all
      while ( ( validSquare(oldx+x,oldy+y) == false ) || ((x == 0) && (y == 0)))
      {
         x = getRandomInt(3) - 1;
         y = getRandomInt(3) - 1;         
      }
      
      stack.xpos += x;
      stack.ypos += y;
      
      stack.movesMade++;
      
      notifyStackMovement( stack, oldx, oldy );
   }
   
   /**
    * Simulates firing gattling beam weapons
    * <p>
    * These weapons hit all targets in range with each shot
    * 
    * @throws BattleSimulationError
    */
   private void fireGattlingWeapon( ShipStack stack, int slot ) throws BattleSimulationError
   {
      ShipDesign design = stack.design;
      
      // Get the base damage done by the weapon
      int baseDamage = design.getWeaponPower(slot) * design.getWeaponCount(slot) * stack.shipCount;

      for (int n = 0; n < stackCount; n++) // examine all stacks
      {         
         if (stacks[n].side != stack.side)	// don't fire on our own side
         {
            int distance = distanceBetween( stack, stacks[n] );
            
            if (distance < design.getWeaponRange(slot))	// are we in range ?
            {
               // This stack is an enemy and is in range, so apply the damage
               
               applyBeamDamage( stack, slot, baseDamage, stacks[n] );
            }
         }
      }
   }
   
   /**
    * Simulates firing beam weapons (except Gattlings)
    */
   private void fireBeamWeapon( ShipStack stack, int slot ) throws BattleSimulationError
   {
      ShipDesign design = stack.design;
      
      // Get the base damage done by the weapon
      int baseDamage = design.getWeaponPower(slot) * design.getWeaponCount(slot) * stack.shipCount;

      while (baseDamage > 0)	// while we have unallocated damage
      {
         ShipStack target = pickTargetInRange( stack, slot );

         if (target == null)
         {
            break; // No more targets in range
         }

         // "Spend" the remaining base damage          
         baseDamage = applyBeamDamage(stack, slot, baseDamage, target);
      }
   }

   /**
    * Applies the damage from a salvo by a beam weapon
    *  
    * @throws BattleSimulationError
    */
   private int applyBeamDamage(ShipStack stack, int slot, int baseDamage, ShipStack target) throws BattleSimulationError
   {
      ShipDesign design = stack.design;
      
      DamageRecord damageRecord = new DamageRecord();
      
      int range = distanceBetween( stack, target );

      // Calculate effects of range, energy capacitors and beam deflectors
      double rangeMultiplier = getRangeMultiplier( range, stack.design.getWeaponRange(slot) );
      double capacitorMultiplier = getCapacitorMultiplier( stack.design );        
      double deflectorMultiplier = getDeflectorMultiplier( target.design );
    
      double damageMultiplier = rangeMultiplier * deflectorMultiplier * capacitorMultiplier;

      if (target.shields > baseDamage * damageMultiplier)
      {
         // Shields can take all the damage
         damageRecord.shieldDamage = (int)(baseDamage * damageMultiplier);               
         target.shields -= baseDamage * damageMultiplier;
         baseDamage = 0;            
      }
      else
      {
         // Shields are blown away, so armour will take damage
         damageRecord.shieldDamage = target.shields;
      	baseDamage = (int)(baseDamage - target.shields / damageMultiplier);
      	target.shields = 0;

      	if (design.getWeaponType(slot) != Weapon.TYPE_SAPPER)
      	{
      	   baseDamage = applyArmourDamage( target, baseDamage, damageMultiplier, damageRecord );      		   
      	}
      }
      
      sendShotNotification(stack, slot, damageRecord, target);
      
      return baseDamage;
   }

   /**
    * Returns the modification to damage due to any energy capacitors on the specified ship design
    */
   private double getCapacitorMultiplier(ShipDesign design)
   {
      if (design.getCapacitors() == 0) return 1.0;
      
      // Use integer arithmatic so we get the same rounding errors as the Stars! battle engine
      int mod = 1000;
      for (int n = 0; n < design.getCapacitors(); n++)
      {
         mod = mod * 110 / 100;
      }
      mod = (int)Math.floor( mod / 10 );      

      return mod / 100;
   }

   /**
    * Returns the modification to damage due to any beam deflectors on the specified ship design
    */
   private double getDeflectorMultiplier(ShipDesign design)
   {
      if (design.getDeflectors() == 0) return 1.0;
      
      // Use integer arithmatic so we get the same rounding errors as the Stars! battle engine
      int mod = 1000;
      for (int n = 0; n < design.getDeflectors(); n++)
      {
         mod = mod * 90 / 100;
      }
      mod = (int)Math.floor( mod / 10 );      

      return mod / 100;
   }
   
   /**
    * Applies damage to the armour on a stack of ships
    * <p>
    * Some of the ships may be killed, and any excess damage is divided amongst the survivors
    * 
    * @return the amount of base damage not used if all ships in the target stack are killed
    *  
    * @throws BattleSimulationError
    */
   private int applyArmourDamage(ShipStack target, int baseDamage, double damageMultiplier, DamageRecord damageRecord) throws BattleSimulationError
   {
      // How much damage has each ship sustained so far ?
      int damagePerShip = target.damage / target.shipCount;            
      int remainingArmourPerShip = target.design.getArmour() - damagePerShip;
      
      int kills = (int)(baseDamage * damageMultiplier / remainingArmourPerShip);

      if (kills > target.shipCount)
      {
         kills = target.shipCount;
      }
      
      baseDamage -= kills * remainingArmourPerShip / damageMultiplier;
      
      damageRecord.armourDamage = kills * remainingArmourPerShip;
  			
      target.shipCount = target.shipCount - kills;

      damageRecord.kills = kills;

      if (target.shipCount > 0)
      {
         int damageUnits = Math.max( target.design.getArmour() / DAMAGE_UNIT, 1 );

         // Get the basic damage
         int damage = (int)(baseDamage * damageMultiplier);
         
         // Round up to units of 1/500
         int damage2 = (int)(Math.ceil(1.0 * damage / damageUnits) * damageUnits);
      
         int newDamagePerShip = damage2 / target.shipCount;
         
         // Apply damage to the target
         target.damage = (damagePerShip+newDamagePerShip) * target.shipCount;

         // There can't be any base damage left at this point as we have applied it all to armour
         baseDamage = 0;

         damageRecord.armourDamage += damage2;
      }
  		   
      return baseDamage;
   }

   /**
    * Simulates firing a missile or torpedo
    * <p>
    * Uses Kotk's (Vambola Kotkas) algorithm:
    * http://starsautohost.org/sahforum/index.php?t=tree&th=2065&mid=17793&rid=386&S=25d65ca246e6d7fb95dbe63d087bf5a7&rev=&reveal=
    */
   public void fireTorpedoKotk( ShipStack stack, int slot ) throws BattleSimulationError
   {
      ShipDesign design = stack.design;
      
      ShipStack target = pickTargetInRange( stack, slot );
      if (target == null) return;
      
      int damage = stack.design.getWeaponPower(slot);
      if ((target.shields == 0) && (design.getWeaponType(slot) == Weapon.TYPE_MISSILE))
      {
         damage *= 2;
      }
      
      boolean first_hit = true;
      int armourDamage = 0;
      int shieldDamage = 0;      

      DamageRecord damageRec = new DamageRecord();
      
      int weaponCount = design.getWeaponCount(slot);
      
      for (int n = 0; n < weaponCount; n++)
      {
         if (missileHits(stack,slot))
         {
            shieldDamage = Math.min( target.shields, damage / 2 );
            target.shields -= shieldDamage;
            damageRec.shieldDamage += shieldDamage;
            armourDamage += damage - shieldDamage;

            int damagePerShip = target.damage / target.shipCount;            
            int remainingArmourPerShip = target.design.getArmour() - damagePerShip;
            
            if (armourDamage >= remainingArmourPerShip)
            {
               target.shields -= target.shields / target.shipCount;
               armourDamage -= remainingArmourPerShip;
               target.shipCount -= 1;
               damageRec.kills++;
               damageRec.armourDamage += remainingArmourPerShip;
               
               if (first_hit)
               {
                  armourDamage = 0;
               }
               
               if (target.shipCount == 0)
               {
                  sendShotNotification( stack, slot, damageRec, target );
                  damageRec = new DamageRecord();
                  
                  target = pickTargetInRange( stack, slot );
                  if (target == null) return;
                  
                  damage = stack.design.getWeaponPower(slot);
                  if ((target.shields == 0) && (design.getWeaponType(slot) == Weapon.TYPE_MISSILE))
                  {
                     damage *= 2;
                  }
                  
                  first_hit = true;
               }
            }
            else //damage does not kill ships
            {
               first_hit = false;
            }
         }
         else // it did not hit
         {
            int dmg = Math.min( target.shields, damage / 8 );
            target.shields -= dmg;
            damageRec.shieldDamage += dmg;
         }
      }

      // apply any remaining armour damage after all missiles have fired
      if (armourDamage > 0)
      {
         // TODO: make sure target has at least damage_units armour left
         int damage_units = Math.max( target.design.getArmour() * target.shipCount / DAMAGE_UNIT, target.shipCount );
         int dmg = Math.max( armourDamage / damage_units, 1 ) * damage_units;
         target.damage += dmg;         
         damageRec.armourDamage += dmg;         
      }
      
      sendShotNotification( stack, slot, damageRec, target );      
   }

   /**
    * Tests whether a missile or torpedo hits it's target
    * <p>
    * The missile's accuracy is calculated, taking into account computers and jammers.  A 
    * random value is then generated and compared to the accuracy level to determine whether
    * the shot is a hit or a miss.
    */
   private boolean missileHits( ShipStack stack, int slot )
   {
      double accuracy = stack.design.getWeaponAccuracy(slot);
      
      accuracy = getFinalAccuracy( accuracy, stack.design, stack.target.design );
      
      return getRandomFloat() <= accuracy;
   }
   
   /**
    * Saves all the details of the battle set-up to the specified file 
    * 
    * @throws IOException
    */
   public void saveTo( String fileName ) throws IOException
   {
      Properties props = new Properties();
      
      props.setProperty( "StackCount", ""+stackCount );
      props.setProperty( "RandomSeed", Long.toString(randomSeed) );
      
      for (int n = 0; n < stackCount; n++)
      {
         stacks[n].storeProperties( props, n );
      }
      
      FileOutputStream s = new FileOutputStream(fileName);
      props.store( s, "Battle simulation" );
      s.close();
   }
   
   /**
    * Loads details of the battle simulation setup from the specified file 
    * 
    * @throws IOException
    */
   public void loadFrom( String fileName ) throws IOException
   {
      Properties props = new Properties();
      
      FileInputStream s = new FileInputStream(fileName);
      props.load( s );
      s.close();
      
      stackCount = Utils.safeParseInt( props.getProperty( "StackCount" ) );
      initStacks(stackCount);
      
      randomSeed = Utils.safeParseLong( props.getProperty( "RandomSeed" ) );
      
      for (int n = 0; n < stackCount; n++)
      {
         stacks[n] = new ShipStack( props, n );         
      }
   }
}
