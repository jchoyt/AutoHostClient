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

import stars.ahc.ShipDesign;

/**
 * Simulates a battle between 2 stacks of ships (only)
 *  
 * @author Steve Leach
 * 
 * @deprecated
 * 		There is no need for OneOnOneBattle any more because the base BattleSimulation
 * 		class handles this situation.
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
    * Move the specified stack towards it's target
    * 
    * @deprecated the new moveStack() has replaced this  
    */
   private void moveStackOld( int index )
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

   /* (non-Javadoc)
    * @see stars.ahc.plugins.battlesim.BattleSimulation#setInitialPosition(stars.ahc.plugins.battlesim.ShipStack)
    */
   protected void setInitialPosition(ShipStack stack, int index)
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
}

