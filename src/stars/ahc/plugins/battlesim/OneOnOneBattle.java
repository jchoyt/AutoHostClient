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
      
      pickTargets();
      
      moveStacks();
      
      fireWeapons();      
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
      
      debug( stacks[index].toString() + " moves to " + me.xpos + "," + me.ypos + " range " + distanceBetween(me,you));      
   }
   
   private int distanceBetween( ShipStack stack1, ShipStack stack2 )
   {
      // This only works because both stacks have same ypos
      return Math.abs( stack1.xpos - stack2.xpos );
   }
   
   private void fireWeapons()
   {
      // Count down from maximum initiative
      for (int initLevel = MAX_INITIATIVE; initLevel > 0; initLevel--)
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
               
               if (init == initLevel)
               {
                  fireWeapon( stack, slot );
               }
            }
         }
      }
   }
   
   private void fireWeapon( ShipStack stack, int slot )
   {
      int shieldDamage = 0;
      int armourDamage = 0;
      int kills = 0;
      
      ShipDesign design = stack.design;
      ShipStack target = stack.target;
      
      int power = design.getWeaponPower(slot) * design.getWeaponCount(slot) * stack.shipCount;
      
      // TODO: adjust for range
      
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
         
         // TODO: get this working
         
         int targetShipArmour = target.design.getArmour() * target.shipCount - target.damage;
         
         kills = (int)Math.floor( armourDamage / targetShipArmour );
         
         target.shipCount -= kills;
         
         int killDamage = kills * targetShipArmour;
         
         target.damage += (armourDamage - killDamage);
                  
      }
      
      debug( stack.toString() + " fires " + stack.design.getWeaponName(slot) + " [" + slot + "] doing "
            + shieldDamage + " damage to shields and " + armourDamage + " to armour (" + kills + " kills)" );      
   }
}
