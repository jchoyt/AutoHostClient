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

/**
 * @author Steve Leach
 */
public abstract class BattleSimulation
{
   protected int round = 0;
   protected boolean verbose = false;
   protected ShipStack[] stacks;
   protected int stackCount = 0;
   public static final int MAX_ROUNDS = 16;
   public static final int MAX_INITIATIVE = 40;

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
   
   protected int movesInRound( int speed4, int round )
   {
      if (round > 8) round -= 8;
      return movement[speed4-1][round-1];
   }

   public void simulate()
   {
      while (stillFighting())
      {
         simulateNextRound();
      }
   }
   
   public abstract void simulateNextRound();
   
   protected void debug(String message)
   {
      System.out.println( round + " : " + message );
   }

   public void setVerbose(boolean verbose)
   {
      this.verbose = verbose;
   }

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
    */
   public static double getRangeMultiplier(int distance, int weaponRange)
   {
      if (distance == 0) return 1.0;
      
      double n = 0.1 * distance / weaponRange;
      
      return 1.0 - n;
   }

}
