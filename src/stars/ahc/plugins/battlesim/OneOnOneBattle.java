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

   /**
    * Set the ship stacks to their starting configurations.
    */
   private void initStacks()
   {
      // Both stacks start half way down the battle board (y = 5)
      stacks[0].ypos = stacks[1].ypos = 5;
      
      // One stack starts on left, one on right (each 1 in from edge)
      stacks[0].xpos = 2;
      stacks[1].xpos = 9;
      
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
      
      fireWeapons();      
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
    * Calculate the range at which the ship would like to fight, based on battle orders 
    */
   private void calculatePreferredRange()
   {
      // TODO: implement this
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
            moveStack(n);
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
    * Move the specified stack towards it's target 
    */
   private void moveStack( int index )
   {
      ShipStack me = stacks[index];
      ShipStack you = me.target;

      // TODO: move away from target if under preferred range
      
      if (me.xpos == you.xpos)	 
      {
         return;
      }
      
      if (me.xpos > you.xpos)
      {
         me.xpos--;
      }
      else
      {
         me.xpos++;
      }            
      
      statusUpdate( stacks[index].toString() + " moves to " + me.xpos + "," + me.ypos + " (range " + distanceBetween(me,you) + ")");      
   }
   
   /**
    * Return the distance between two stacks
    */
   private int distanceBetween( ShipStack stack1, ShipStack stack2 )
   {
      // This only works because both stacks have same ypos
      return Math.abs( stack1.xpos - stack2.xpos );
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
         // Count up from range 0 to range 3 (shorter range first first)
         for (int range = 0; range <= 3; range++)
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
         
         // How much damage has each ship sustained so far ?
         int damagePerShip = target.damage / target.shipCount;
         int remainingArmourPerShip = target.design.getArmour() - damagePerShip;
         
         // How many ships have we killed ?  Do floating point math then round down
         kills = (int)Math.floor( 1.0 * armourDamage / remainingArmourPerShip );
                  
         // We can't kill more ships than there are in the stack
         if (kills > target.shipCount) kills = target.shipCount;
         
         // How much damage is left over after killing ships
         int killDamage = kills * remainingArmourPerShip;         
         int damageRemainder = armourDamage - killDamage;
         
         target.shipCount -= kills;         
         target.damage = damagePerShip * target.shipCount + damageRemainder;
      }
      
      String status = stack.toString() + " fires " + stack.design.getWeaponName(slot) 
      				+ " [" + (slot+1) + "] doing " + shieldDamage + " damage to shields and " 
      				+ armourDamage + " to armour (" + kills + " kills)";
      
      statusUpdate( status );
   }
   
   /**
    * Simulates firing torpedos and missiles 
    */
   private void fireTorpedo( ShipStack stack, int slot )
   {
      // TODO: implement this
      
      statusUpdate( stack.toString() + " would fire " + stack.design.getWeaponName(slot) + " if I could handle torps" );
   }
}