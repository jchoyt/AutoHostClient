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

import java.util.Arrays;
import java.util.Comparator;

import stars.ahc.ShipDesign;
import stars.ahc.Utils;

/**
 * Simulates a battle between 2 stacks of ships (only)
 * 
 * @author Steve Leach
 *
 */
public class OneOnOneBattle extends BattleSimulation
{
   public OneOnOneBattle( ShipDesign design1, int count1, ShipDesign design2, int count2 )
   {
      stackCount = 2;
      stacks = new ShipStack[stackCount];
      
      stacks[0] = new ShipStack(design1,count1);
      stacks[1] = new ShipStack(design2,count2);
            
      initStacks();
   }

   private void initStacks()
   {
      // Both stacks start half way down the battle board
      stacks[0].ypos = stacks[1].ypos = 5;
      
      // One stack starts on left, one on right
      stacks[0].xpos = 2;
      stacks[1].xpos = 9;
      
      for (int n = 0; n < stackCount; n++)
      {
         stacks[n].shields = stacks[n].design.getShields() * stacks[n].shipCount;
         stacks[n].damage = 0;
      }
   }
   
   public void simulateNextRound()
   {
      round++;
      
      regenShields();
      
      pickTargets();
      
      moveStacks();
      
      fireWeapons();      
   }
   
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
            
            debug( stacks[n].toString() + " shields regenerate from " + beforeRegen + " to " + stacks[n].shields + " dp" );            
         }
      }
   }
   
   private void pickTargets()
   {
      // This is all we need to do in this one-on-one battle simulator
      stacks[0].target = stacks[1];
      stacks[1].target = stacks[0];
   }
   
   private void moveStacks()
   {
      randomizeMass();
      sortByShipMass();

      moveStacks( 3 );		// Move any stacks that have a movement of 3 in this round 
      moveStacks( 2 );		// Move any stacks that have a movement of at least 2 in this round
      moveStacks( 1 );		// Move any stacks that have a movement of at least 1 in this round
   }
   
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
   
   private void randomizeMass()
   {
      for (int n = 0; n < stackCount; n++)
      {
         // TODO: add the random element
         stacks[n].randomMass = stacks[n].design.getMass();
      }      
   }
   
   /**
    * Sort the stacks by randomized mass, heaviest to lightest
    */
   private void sortByShipMass()
   {
      Arrays.sort( stacks, new Comparator() {
         public int compare(Object arg0, Object arg1)
         {
            ShipStack stack0 = (ShipStack)arg0;
            ShipStack stack1 = (ShipStack)arg1;
            return stack1.randomMass - stack0.randomMass;
         }
      } );
   }

   private void moveStack( int index )
   {
      ShipStack me = stacks[index];
      ShipStack you = me.target;

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
      
      debug( stacks[index].toString() + " moves to " + me.xpos + "," + me.ypos + " (range " + distanceBetween(me,you) + ")");      
   }
   
   private int distanceBetween( ShipStack stack1, ShipStack stack2 )
   {
      // This only works because both stacks have same ypos
      return Math.abs( stack1.xpos - stack2.xpos );
   }
   
   private void fireWeapons()
   {
      // Randomly arrange the fleets so that init+range ties can go either way.
      // TODO: Is this the correct thing to do ?
      randomSortFleets();
      
      // Count down from maximum initiative
      for (int initLevel = MAX_INITIATIVE; initLevel > 0; initLevel--)
      {
         // Count up from range 0 to range 3
         for (int range = 0; range <= 3; range++)
         {
	         // Loop through stacks
	         for (int n = 0; n < stackCount; n++)
	         {
	            ShipStack stack = stacks[n];
	            ShipDesign design = stack.design;
	            
	            // Loop though weapons slots to find weapons with current initiative level
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
   
   private void randomSortFleets()
   {
      if (Utils.getRandomFloat() > 0.5f)
      {
         // Half the time, swap the fleets
         ShipStack temp = stacks[0];
         stacks[0] = stacks[1];
         stacks[1] = temp;
      }
   }
   
   /**
    * TODO: handle torps and missiles 
    */
   private void fireWeapon( ShipStack stack, int slot )
   {
      if (stack.shipCount <= 0)
      {
         return;
      }
      
      int shieldDamage = 0;
      int armourDamage = 0;
      int kills = 0;
      
      ShipDesign design = stack.design;
      
      ShipStack target = stack.target;
      
      if (target.shipCount <= 0)
      {
         return;
      }
      
      int range = distanceBetween( stack, target );
      
      if (range > stack.design.getWeaponRange(slot))
      {
         return;
      }
      
      int power = design.getWeaponPower(slot) * design.getWeaponCount(slot) * stack.shipCount;

      double rangeMultipliyer = getRangeMultiplier( range, stack.design.getWeaponRange(slot) );

      power *= rangeMultipliyer;
      
      if (power < target.shields)
      {
         target.shields = target.shields - power;
         shieldDamage = power;
         
         armourDamage = 0;
      }
      else
      {
         shieldDamage = target.shields;
         target.shields = 0;
         
         armourDamage = power - shieldDamage;
         
         if (design.getWeaponType(slot) == ShipDesign.WPNTYPE_SAPPER)
         {
            armourDamage = 0;
         }
         
         int damagePerShip = target.damage / target.shipCount;
         int remainingArmourPerShip = target.design.getArmour() - damagePerShip;
         
         kills = (int)Math.floor( armourDamage / remainingArmourPerShip );
                  
         if (kills > target.shipCount) kills = target.shipCount;
         
         int killDamage = kills * remainingArmourPerShip;
         
         int damageRemainder = armourDamage - killDamage;
         
         target.shipCount -= kills;         
         target.damage = damagePerShip * target.shipCount + damageRemainder;
      }
      
      debug( stack.toString() + " fires " + stack.design.getWeaponName(slot) + " [" + (slot+1) + "] doing "
            + shieldDamage + " damage to shields and " + armourDamage + " to armour (" + kills + " kills)" );      
   }
}
