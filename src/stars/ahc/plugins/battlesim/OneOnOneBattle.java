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
      
      // How many ships have we killed ?  Do floating point math then round down
      kills = (int)Math.floor( 1.0 * armourDamage / remainingArmourPerShip );
               
      // If this is a torp/missile salvo there may be a cap on the number of kills
      if (kills > maxKills) kills = maxKills;
      
      // We can't kill more ships than there are in the stack
      if (kills > target.shipCount) kills = target.shipCount;
      
      // How much damage is left over after killing ships
      int killDamage = kills * remainingArmourPerShip;         
      int damageRemainder = armourDamage - killDamage;
      
      target.shipCount -= kills;         
      target.damage = damagePerShip * target.shipCount + damageRemainder;
      
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
      
      int totalPower = numShots * design.getWeaponPower(slot);
      
      double accuracy = design.getWeaponAccuracy(slot);
      
      accuracy = getFinalAccuracy( accuracy, design, target.design );
      
      totalPower = (int)Math.round( totalPower * accuracy / 100.0 );
      
      if ((target.shields == 0) && (design.getWeaponType(slot) == Weapon.TYPE_MISSILE))
      {
         // Capital ship missile and target has no shields - double damage
         totalPower *= 2;
      }

      int shieldDamage = Math.min( target.shields, totalPower / 2 );
      int armourDamage = totalPower - shieldDamage;
      
      target.shields -= shieldDamage;
      
      int kills = applyArmourDamage( armourDamage, target, numShots );

      String status = stack.toString() + " fires " + stack.design.getWeaponName(slot) 
		+ " [" + (slot+1) + "] doing " + shieldDamage + " damage to shields and " 
		+ armourDamage + " to armour (" + kills + " kills)";
      
      statusUpdate( status );
   }
   
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

/*
Weapons and Battle Devices

More Battle Details

More Guts 

Read this section with the section on Armor, Shields and Damage for a fuller understanding of how armor, shields and specific weapon types 
interact in Stars!.

Weapons and Starbases

All weapons mounted on Starbases get +1 added to their range.

Beam Weapons

Beam weapons always hit their target, but decay in strength at a rate of 10% pro-rated over their maximum range. For example, a weapon that will 
do 100 dp to a target in the same square and has a maximum range of 3 will only do 94 dp to a target two squares away. All damage from beam 
weapons is applied to shields first. Any damage not absorbed by the shields is applied to armor.

If an attacking token has more than one ship and its beam weapon strike destroys the target token, then the remaining damage is applied to other 
tokens in the same square. The maximum number of tokens targeted is the number of ships in the attacking token.

The following beam weapon information is provided for comparison purposes. See the Beam Weapons section of the Technology browser for the 
exact statistics of a specific weapon.

Normal Beam Weapons

Damage: From 10 to 430 damage points
Range:  From 1 to 3 squares
Initiative: From 5 to 9

Range 0 Weapons

   Damage: From 90 to 600 damage points
   Range:  Same square only
   Initiative: 12

Gattling Weapons

Damage: From 13 to 200 damage points
Range:  2 squares
Initiative: 12 to 13

These are extremely powerful weapons that hit every enemy token in their range each time they fire. They also sweep minefields as if they were 
range 4.

Shield Sappers

Damage: From 82 to 541 damage points
Range:  3
Initiative: 14

These medium range weapons are very powerful but are only useful against shields. They have no effect on armor. They do have a higher initiative 
than any other weapon. This means that they will take out the enemy shields before your other weapons fire.
Shield Sappers cannot perform minesweeping.
Minesweeping

Each beam weapon automatically sweep up to (Damage x Range x Range) mines per year.

Torpedoes

Each torpedo fired has a chance of missing. For example: If a token has two ships, each with a weapon
slot holding 2 normal torpedoes, then a single shot fires all 4 torpedoes. Each torpedo has a chance 
to hit or miss according to its accuracy value. Normal torpedoes have an accuracy of 
75%, which means that it is likely that 3 of the 4 torpedoes would hit.
Torpedoes that hit their primary target apply half of their damage directly to the armor of the target 
token. The other half of the damage is applied to 
the shields. Any damage that isn't absorbed by the shields is applied to the armor.

The maximum number of ships that can be killed by a torpedo strike is the number of torpedoes that hit. 
So in the preceding example the strike can 
kill up to 3 ships. If the target token has one ship in it and the hits caused more damage than was necessary 
to destroy it, then the damage is 
applied to other tokens in the same square. This type of damage is applied first to the shields; any damage 
not absorbed by the shields is applied 
to the armor. In no case can the number of ships destroyed exceed the number of torpedoes that hit.

Torpedoes that miss do collateral damage to the target token only. Collateral damage is 1/8th of the normal 
damage of the torpedo and works much 
like a Shield Buster beam weapon. In other words it only affects shields.
Torpedo accuracy can be improved using Battle Computers. Jammers can decrease the accuracy of enemy torpedoes.
The following torpedo information is provided for comparison purposes. See the Torpedoes section of the 
Technology Browser for the exact 
statistics of a specific weapon.

Normal Torpedoes

Damage: From 5 to 300 damage points
Range: From 4 to 5 squares
Initiative: from 0 to 4
Accuracy: 35% to 80%

Capital Ship Missiles

Damage: From 85 to 525 damage points
Range: From 5 to 6 squares
Initiative: From 0 to 3
Accuracy: 20% to 30%

These powerful torpedoes do more damage than normal torpedoes and have a longer range than any other weapon. 
Due to the poor accuracy and 
the fact that a single torpedo can take out at most one enemy ship, these missiles are best mounted on 
starbases and battleships with a lot of 
Battle Computers. Their ideal use is against large ships and starbases.

Capital ship missiles do twice the stated damage if the enemy ship has no remaining shields.

Jammers

Jammers decrease torpedo accuracy. The Jammer 10 and 50 are available to Inner Strength players only. 
Jammer strength is additive. For 
example, a ship with three 20% Jammers reduces a normal torpedo's 75% accuracy by 20% three times: 

75 x .8 x .8 x .8 = 38% torpedo accuracy 

See the Electrical section of the Technology Browser for a descriptions of each Jammer. 

Battle Computers

These devices increase the initiative of all weapons on the ship. The three different devices range 
from +1 to +3 initiative. They also decrease 
torpedo inaccuracy by 20 to 50%. 

Decreasing the inaccuracy of a torpedo by a percentage is not the same as increasing the accuracy by that 
percentage. As torpedo accuracy 
becomes higher it becomes harder to improve it.

Computing the effect of a battle computer:
Example 1: A normal 75% accurate torpedo fired using a  50% battle computer.

Incorrect calculation: 75% x 1.5 = 112% accuracy.

Correct calculation: 100 - ((100 - 75) x .5) = 88% accuracy.

Example 2: A normal torpedo's 75% accuracy is modified by two 30% battle computers by decreasing its inaccuracy 30% twice. 100 - ((100 - 75) 
x .7 x .7) = %88 torpedo accuracy.
If the attacking token has battle computers and the target has jammers the devices cancel each other out on a 1% to 1% basis.

Further examples:

¨	Target token has Jammers totaling a 50% decrease in accuracy. Attacker's battle computers add up to a 45% decrease in inaccuracy. 
Result: 5% decrease in accuracy.

¨	Target token has Jammers totaling a 30% decrease in accuracy. Attacker's battle computers add up to a 40% decrease in inaccuracy. 
Result: 10% decrease in inaccuracy.

Energy Dampener

This device slows down ALL ships in the entire battle board by 1 square of movement per round, for the duration of the battle. This is true even if the 
ship carrying the Dampener is destroyed before the end of the battle (the device has a lasting affect). The effect is not additive, so there is no 
advantage or penalty for having more than one Dampener in a battle.

Capacitors

Increase the damage caused by all beam weapons on board by a percentage. Capacitor values run from 10% to 20%. The maximum additional 
percentage increase in damage caused by multiple capacitors is 250%.

Example:

A ship has a beam weapon capable of 100 damage and three 10% capacitors.

100dp x 1.1 x 1.1 x 1.1 = 133dp

Copyright 1998 Mare Crisium, LLC
*/