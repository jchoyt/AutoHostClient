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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import stars.ahc.ShipDesign;
import stars.ahc.Utils;
import stars.ahc.Weapon;

/**
 * 
 * @author Steve Leach
 */
public class BattleSimulation
{
   /**
    * Battles are divided into up to 16 rounds, with each weapon firing once per round
    */
   protected int round = 0;
   /**
    * Stores the ship stack details
    */
   protected ShipStack[] stacks;
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
   public static final int MAX_INITIATIVE = 40;
   /**
    * The number of moves a stack must make before disengaging
    */
   public static final int DISENGAGE_MOVES = 7;
   
   public static final int BBOARD_MIN_X = 1;
   public static final int BBOARD_MAX_X = 10;
   
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
   
   protected static int[][] distanceTable = null;
   
   /**
    * Creates a Battle Simulation object supporting up to 16 ship stacks 
    */
   public BattleSimulation()
   {
      initStacks( 16 );
   }
   
   public BattleSimulation( int maxStacks )
   {
      initStacks( maxStacks );
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
    * Adds a new ship stack, using the specified design, ship count and owner
    * <p>
    * @return a reference to the newly created stack 
    */
   public ShipStack addNewStack( ShipDesign design, int count, int ownerId )
   {
      ShipStack stack = new ShipStack(design,count);
      stack.ownerId = ownerId;
      
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
      
      while ( stillFighting() )
      {
         simulateNextRound();
      }
      
      // Show the results of the battle
      showStackDetails();
   }
   
   /**
    * Sets up the starting state of the battle  
    * @throws BattleSimulationError
    */
   protected void initialiseBattleBoard() throws BattleSimulationError
   {
      for (int n = 0; n < stackCount; n++)
      {
         setInitialPosition( stacks[n], n );
         
         initStackDamage( stacks[n] );
      }
   }
   
   protected void initStackDamage( ShipStack stack )
   {
      // Shields "stack" - the stack's shields are the sum of the shields of the ships
      stack.shields = stack.design.getShields() * stack.shipCount;

      // Initially the ships are all undamaged
      stack.damage = 0;
   }
   
   /**
    * Sets the initial position of the stacks 
    */
   protected void setInitialPosition( ShipStack stack, int index ) throws BattleSimulationError
   {
      if (stackCount == 2)
      {
         stack.ypos = 5;
         
         if (index == 0)
         {
            stack.xpos = BBOARD_MIN_X+1;
         }
         else
         {
            stack.xpos = BBOARD_MAX_X-1;
         }
      }
      else
      {
         throw new BattleSimulationError( "Cannot handle more than 2 stacks yet" );
      }
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
    */
   public void simulateNextRound()
   {
      round++;
      
      regenShields();
      
      pickTargets();
      
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
      for (int n = 0; n < statusListers.size(); n++)
      {
         try
         {
            BattleSimulationListener listener = (BattleSimulationListener)statusListers.get(n);
            
            BattleSimulationNotification notification = new BattleSimulationNotification();
            notification.round = round;
            notification.message = msg;
            
            listener.handleNotification( notification );
         }
         catch (Throwable t)
         {
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
            
            statusUpdate( stacks[n].toString() + " shields regenerate from " + beforeRegen + " to " + stacks[n].shields + " dp" );            
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
    * To give heavier ships a slight chance to move last, randomly adjust ship mass slightly (up to 15%). 
    */
   protected void randomizeMass()
   {
      for (int n = 0; n < stackCount; n++)
      {
         // Get a random number between 0.85 and 1.15
         float random = Utils.getRandomFloat() * 0.3f + 0.85f;
         
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
    */
   public int distanceBetween(ShipStack stack1, ShipStack stack2, int xOffset, int yOffset)
   {
      int dx = Math.abs( (stack1.xpos + xOffset) - stack2.xpos );
      int dy = Math.abs( (stack1.ypos + yOffset) - stack2.ypos );
   
      // Use a distance table lookup for distances.
      // This limits the number of expensive Math.sqrt calls to 100.
      // Will only make a difference in large battles.
      
      if (distanceTable == null)
      {
         distanceTable = new int[10][10];
         for (int x = 0; x < 10; x++)
         {
            for (int y = 0; y < 10; y++)
            {
               int d =  (int)Math.round( Math.floor( Math.sqrt( x * x + y * y ) ) );
               distanceTable[x][y] = d;
            }
         }
      }
      
      return distanceTable[dx][dy];
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
         
         return 1.0 - (1 - baseAccuracy) * (attacker.getComputing() / 100.0);
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
            moveStack(n);
         }
      }      
   }

   /**
    * Calculates the number of ships killed
    * <p>
    * Important - the target stack is updated (damage and shipcount) as a side effect 
    */
   protected int applyArmourDamage(int armourDamage, ShipStack target, int maxKills)
   {
      int kills;
      
      // How much damage has each ship sustained so far ?
      int damagePerShip = target.damage / target.shipCount;            
      int remainingArmourPerShip = target.design.getArmour() - damagePerShip;
      
      if (armourDamage > (remainingArmourPerShip * target.shipCount))
      {
         // Damage overwhelms all remaining ship armour
         kills = target.shipCount;
         target.shipCount = 0;
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
    */
   protected void fireWeapon(ShipStack stack, int slot)
   {
      // Stacks with no ships left can't fire
      if (stack.shipCount <= 0)
      {
         return;
      }
   
      // If the target stack has no ships left, don't fire
      if (stack.target.shipCount <= 0)
      {
         return;
      }
      
      switch (stack.design.getWeaponType(slot))
      {
         case Weapon.TYPE_BEAM:
         case Weapon.TYPE_GATTLING:
         case Weapon.TYPE_SAPPER:
            fireBeamWeapon( stack, slot );
         	break;
         	
         case Weapon.TYPE_MISSILE:
         case Weapon.TYPE_TORPEDO:
            fireTorpedo( stack, slot );
          	break;
      }
   }

   /**
    * Simulates firing beam weapons
    */
   private void fireBeamWeapon(ShipStack stack, int slot)
   {
      int shieldDamage = 0;		// initially there is no shield damage
      int armourDamage = 0; 	// initially there is no armour damage
      int kills = 0;			// initially there are no kills
      
      ShipDesign design = stack.design;		// for convenience      
      ShipStack target = stack.target;
   
      int range = distanceBetween( stack, target );
      
      // Are we out of range ?
      if (range > stack.design.getWeaponRange(slot))
      {
         return;
      }
      
      if (design.getWeaponType(slot) == Weapon.TYPE_SAPPER)
      {
         if (target.shields == 0)
         {
            // Don't bother firing sappers if the target has no shields left
            return;
         }
      }
      
      // Calculate the total firepower of the salvo 
      int power = design.getWeaponPower(slot) * design.getWeaponCount(slot) * stack.shipCount;
   
      // Reduce firepower if at more than minimal range
      double rangeMultipliyer = getRangeMultiplier( range, stack.design.getWeaponRange(slot) );
      
      power *= rangeMultipliyer;
      
      // TODO: handle capacitors and deflectors
      
      if (power < target.shields)
      {
         // Shields can handle the shot, so just reduce them accordingly
         
         target.shields = target.shields - power;
         shieldDamage = power;
         
         armourDamage = 0;
      }
      else
      {
         // The shields cannae take it cap'n
         // The armour is going to get a beating
         
         shieldDamage = target.shields;
         target.shields = 0;	// shields are gone
         
         // Armour takes whatever damage wasn't absorbed by the shields
         armourDamage = power - shieldDamage;
         
         if (design.getWeaponType(slot) == Weapon.TYPE_SAPPER)
         {
            // Of course, if the weapons are sappers then they can't hurt the armour
            armourDamage = 0;
         }
         
         kills = applyArmourDamage(armourDamage, target, target.shipCount);
      }
      
      String status = stack.toString() + " fires " + stack.design.getWeaponName(slot) 
      				+ " [" + (slot+1) + "] doing " + shieldDamage + " damage to shields and " 
      				+ armourDamage + " to armour (" + kills + " kills)";
      
      statusUpdate( status );
   }

   /**
    * Simulates firing torpedos and missiles 
    */
   private void fireTorpedo(ShipStack stack, int slot)
   {
      ShipDesign design = stack.design;		// for convenience      
      ShipStack target = stack.target;
   
      int range = distanceBetween( stack, target );
   
      // Are we out of range ?
      if (range > design.getWeaponRange(slot))
      {
         return;
      }
      int numShots = design.getWeaponCount(slot);
      
      double accuracy = design.getWeaponAccuracy(slot);
      
      accuracy = getFinalAccuracy( accuracy, design, target.design );
      
      int numHits = (int)Math.round( numShots * accuracy / 100.0 );
   
      int totalPower = numHits * design.getWeaponPower(slot);
      
      if ((target.shields == 0) && (design.getWeaponType(slot) == Weapon.TYPE_MISSILE))
      {
         // Capital ship missile and target has no shields - double damage
         totalPower *= 2;
      }
   
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
      
      String status = stack.toString() + " fires " + stack.design.getWeaponName(slot) 
				   	+ " [" + (slot+1) + "] doing " + shieldDamage + " damage to shields and " 
				   	+ armourDamage + " to armour (" + kills + " kills)";
      
      statusUpdate( status );
   }

   /**
    * Moves the specified stack
    * 
    * The stack will attempt to move to a new square that is as close as possible
    * to it's preferred distance from it's target. 
    */
   protected void moveStack(int index)
   {
      ShipStack mover = stacks[index];
      ShipStack target = mover.target;
   
      int originalDistance = distanceBetween(mover,target); 
   
      // These two arrays specify where to move relative to current location
      int[] yOffsets = { 0,  0, -1, -1, -1,  1, 1, 1 };
      int[] xOffsets = { 1, -1, -1,  0,  1, -1, 0, 1 };
      
      int dd = Integer.MAX_VALUE;		// best range found so far 
      int xOffset = 0;					// where to move for best range
      int yOffset = 0;
      
      for (int n = 0; n < 8; n++)
      {
         // Can't move off edge of battle board
         if (mover.xpos+xOffsets[n] < 1) continue;
         if (mover.xpos+xOffsets[n] > 10) continue;
         if (mover.ypos+yOffsets[n] < 1) continue;
         if (mover.ypos+yOffsets[n] > 10) continue;
         
         // Get the range if we moved here
         int dn = distanceBetween( mover, target, xOffsets[n], yOffsets[n] );
         
         // How far out is this from preferred range
         int ddn = Math.abs( dn - mover.preferredRange );
                  
         if (ddn < dd)
         {
            // This is the best we have found so far
            dd = ddn;
            xOffset = xOffsets[n];
            yOffset = yOffsets[n];
         }
      }
   
      // Move stack to new location
      mover.xpos += xOffset;
      mover.ypos += yOffset;
      
      // Count the move in case we are trying to disengage
      mover.movesMade++;
   
      // Have we moved towards or away from the target ?
      int newDistance = distanceBetween(mover,target);
      String directionStr = " ";
      if (newDistance > originalDistance) 
      {
         directionStr = " away ";
      }
      else if (newDistance < originalDistance)
      {
         directionStr = " forward ";
      }
   
      statusUpdate( stacks[index].toString() + " moves" + directionStr + "to " + mover.xpos + "," + mover.ypos + " (range " + distanceBetween(mover,target) + ")");
   }
   
   /**
    * All ships fire their weapons 
    */
   protected void fireWeapons()
   {
      sortStacksForFiring();
      
      // Count down from maximum initiative (max initiative fires first)
      for (int initLevel = MAX_INITIATIVE; initLevel > 0; initLevel--)
      {
         // Count up from range 0 to range 7 (shorter range first first)
         for (int range = 0; range <= 7; range++)
         {
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
                  
                  if ((init == initLevel) && (wpnRange == range))
                  {
                     fireWeapon( stack, slot );
                  }
               }
            }
         }
      }
   }

   /**
    * Sorts the stacks before firing
    * <p>
    * Randomly sort the stacks to give the stacks equal opportunity to fire first 
    * if they have the same initiative.
    */
   protected void sortStacksForFiring()
   {
      for (int n = 0; n < stackCount; n++)
      {
         stacks[n].sortValue = Utils.getRandomInt();
      }
      
      Arrays.sort( stacks, 0, stackCount, new Comparator() {
         public int compare(Object arg0, Object arg1)
         {
            ShipStack stack0 = (ShipStack)arg0;
            ShipStack stack1 = (ShipStack)arg1;
            return stack0.sortValue - stack1.sortValue;
         }
      });
   }

   /**
    * Each stack selects a target to attack.
    * <p>
    * For a one-on-one battle, each just selects the other 
    */
   protected void pickTargets()
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
    */
   protected void pickTarget(ShipStack stack)
   {
      for (int n = 0; n < stackCount; n++)
      {
         if (stacks[n] != stack)	// don't target self
         {
            if (stacks[n].targetedBy == null)	// don't target if already targeted
            {
               stack.target = stacks[n];
               stacks[n].targetedBy = stack.target;
            }
         }
      }
   }
}
