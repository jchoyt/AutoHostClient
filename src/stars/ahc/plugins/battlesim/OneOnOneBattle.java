/*
 * Created on Oct 31, 2004
 *
 * Copyright (c) 2004, Steve Leach
 * 
 * Part of the Stars! AutoHost Client
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

import java.util.Arrays;
import java.util.Comparator;

import stars.ahc.ShipDesign;
import stars.ahc.Utils;
import stars.ahc.Weapon;

/**
 * Simulates a battle between 2 stacks of ships (only)
 *
 * TODO: handle missiles and torpedos
 * TODO: handle computers and jammers
 * TODO: handle capacators and deflectors
 * TODO: get stacks to determine best range
 * TODO: different battle orders
 *  
 * @author Steve Leach
 */
public class OneOnOneBattle extends BattleSimulation
{
   /**
    * Create a one-one-one battle simulation between two ship stacks
    * <p>
    * Each stack is described by a ship design and the number of ships in the stack. 
    */
   public OneOnOneBattle( ShipDesign design1, int count1, ShipDesign design2, int count2 )
   {
      stackCount = 2;
      stacks = new ShipStack[stackCount];
      
      stacks[0] = new ShipStack(design1,count1);
      stacks[1] = new ShipStack(design2,count2);
            
      initStacks();
   }

   public OneOnOneBattle( ShipStack stack1, ShipStack stack2 )
   {
      stackCount = 2;
      stacks = new ShipStack[stackCount];

      stacks[0] = stack1;
      stacks[1] = stack2;
      
      initStacks();
   }
   
   public OneOnOneBattle()
   {
      stackCount = 0;
   }
   
   /**
    * Set the ship stacks to their starting configurations.
    */
   private void initStacks()
   {
      // Both stacks start half way down the battle board (y = 5)
      stacks[0].ypos = stacks[1].ypos = 5;
      
      // One stack starts on left, one on right (each 1 in from edge)
      stacks[0].xpos = BBOARD_MIN_X+1;
      stacks[1].xpos = BBOARD_MAX_X-1;
      
      // Initialise the total shields and damage for the stacks
      for (int n = 0; n < stackCount; n++)
      {
         // Shields "stack" - the stack's shields are the sum of the shields of the ships
         stacks[n].shields = stacks[n].design.getShields() * stacks[n].shipCount;
         // Initially the ships are all undamaged
         stacks[n].damage = 0;
      }
   }
   
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
    * For ships with regenerating shields, factor in the shield regeneration 
    */
   private void regenShields()
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
    * Each stack selects a target to attack.
    * <p>
    * For a one-on-one battle, each just selects the other 
    */
   private void pickTargets()
   {
      // This is all we need to do in this one-on-one battle simulator
      stacks[0].target = stacks[1];
      stacks[1].target = stacks[0];
   }
   
   /**
    * Calculate the range at which the ships would like to fight, based on battle orders 
    */
   private void calculatePreferredRange()
   {
      calculatePreferredRange( stacks[0] );
      calculatePreferredRange( stacks[1] );
   }
   
   /**
    * Calculate the range at which the stack would like to fight, based on battle orders 
    */
   private void calculatePreferredRange( ShipStack stack )
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
    * Move the ship stacks
    * <p>
    * Each ship design has a battle speed of between 0.25 and 2.5.  This speed determines how
    * many time the stack can move each round - between 1 and 3 moves.
    */
   private void moveStacks()
   {
      sortByShipMass();

      moveStacks( 3 );		// Move any stacks that have a movement of 3 in this round 
      moveStacks( 2 );		// Move any stacks that have a movement of at least 2 in this round
      moveStacks( 1 );		// Move any stacks that have a movement of at least 1 in this round
   }
   
   /**
    * Move any stacks that can move above the specified threshold number of squares per round 
    */
   private void moveStacks( int threshold )
   {
      for (int n = 0; n < stackCount; n++)
      {
         if (movesInRound(stacks[n].design.getSpeed4(),round) >= threshold)
         {
            moveStack2D(n);
         }
      }      
   }
   
   /**
    * To give heavier ships a slight chance to move last, randomly adjust ship mass slightly (up to 15%). 
    */
   private void randomizeMass()
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
   private void sortByShipMass()
   {
      randomizeMass();

      Arrays.sort( stacks, new Comparator() {
         public int compare(Object arg0, Object arg1)
         {
            ShipStack stack0 = (ShipStack)arg0;
            ShipStack stack1 = (ShipStack)arg1;
            return stack1.randomMass - stack0.randomMass;
         }
      } );
   }
   
   /**
    * Moves the specified stack
    * 
    * The stack will attempt to move to a new square that is as close as possible
    * to it's preferred distance from it's target. 
    */
   private void moveStack2D( int index )
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
    * Move the specified stack towards it's target
    * 
    * @deprecated useStack2D has replaced this  
    */
   private void moveStack( int index )
   {
      ShipStack mover = stacks[index];
      ShipStack target = mover.target;

      int currentRange = distanceBetween( mover, target );
      
      int direction = 1;
      
      if (currentRange == mover.preferredRange)
      {
         statusUpdate( stacks[index].toString() + " does not move" );
         return;
      }
      else if (currentRange < mover.preferredRange)
      {
         direction = -1;
      }
      
      if (mover.xpos == target.xpos)	 
      {
         return;
      }
      
      if (mover.xpos > target.xpos)
      {
         mover.xpos -= direction;
      }
      else
      {
         mover.xpos += direction;
      }            
      
      mover.movesMade++;
      
      String directionStr = (direction == -1) ? "away" : "closer";
      
      statusUpdate( stacks[index].toString() + " moves " + directionStr + " to " + mover.xpos + "," + mover.ypos + " (range " + distanceBetween(mover,target) + ")");      
   }
   
   /**
    * Return the distance between two stacks
    */
   public int distanceBetween( ShipStack stack1, ShipStack stack2 )
   {
      return distanceBetween( stack1, stack2, 0, 0 );
   }

   /**
    * Returns the distance between two stacks if one of them moves slightly  
    */
   public int distanceBetween( ShipStack stack1, ShipStack stack2, int xOffset, int yOffset )
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
    * Check whether any stacks have managed to disengage 
    */
   private void checkForDisengage()
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
    * All ships fire their weapons 
    */
   private void fireWeapons()
   {
      // Randomly arrange the fleets so that init+range ties can go either way.
      // TODO: Is this the correct thing to do ?
      randomSortFleets();
      
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
    * Randomly sort the stacks
    * <p>
    * This gives the stacks equal opportunity to fire first if they have the same initiative.
    */
   private void randomSortFleets()
   {
      // This implementation assumes that the fleets are initially in a pre-determined order
      if (Utils.getRandomFloat() > 0.5f)
      {
         // Half the time, swap the fleets
         ShipStack temp = stacks[0];
         stacks[0] = stacks[1];
         stacks[1] = temp;
      }
   }
   
   /**
    * Simulates the specified weapons slot of the specified ship stack firing
    */
   private void fireWeapon( ShipStack stack, int slot )
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
   private void fireBeamWeapon( ShipStack stack, int slot )
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
    * Calculates the number of ships killed
    * <p>
    * Important - the target stack is updated (damage and shipcount) as a side effect 
    */
   private int applyArmourDamage(int armourDamage, ShipStack target, int maxKills)
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
    * Simulates firing torpedos and missiles 
    */
   private void fireTorpedo( ShipStack stack, int slot )
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
    * Returns the adjusted accuracy level of a weapon
    * <p>
    * This takes into account and computers on the attacker and any jammers on the defender.
    * <p>
    * @param baseAccuracy is the basic accuracy level of the weapon
    */
   public static double getFinalAccuracy( double baseAccuracy, ShipDesign attacker, ShipDesign defender )
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
}

