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

/**
 * Abstract class representing a battle simulation.
 * <p>
 * Concrete sub-classes implement the actual simulation code. 
 * 
 * @see OneOnOneBattle
 * @author Steve Leach
 */
public abstract class BattleSimulation
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
    */
   public void simulate()
   {
      while (stillFighting())
      {
         simulateNextRound();
      }
      
      // Show the results of the battle
      showStacks();
   }
   
   /**
    * Display the status of all the stacks 
    */
   public void showStacks()
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
   public abstract void simulateNextRound();
   
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
   public void addStatusListener( StatusListener listener )
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
            StatusListener listener = (StatusListener)statusListers.get(n);
            
            listener.battleStatusUpdate( round, msg );
         }
         catch (Throwable t)
         {
            // protect the simulation from buggy status listeners
         }
      }
   }
}
