/*
 * Created on Nov 8, 2004
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
package stars.ahc;

/**
 * A definition of parts of the Stars! rule set
 *  
 * @author Steve Leach
 */
public class StarsRuleSet
{

   /**
    * Table for converting grav values to/from tick counts
    */
   public static final int[] grav_clicks = 
   {
       12,  
       12,  13,  13,  14,  14,  15,  15,  16,  17,  17,  
       18,  19,  20,  21,  22,  24,  25,  27,  29,  31,  
       33,  36,  40,  44,  50,  51,  52,  53,  54,  55,  
       56,  58,  59,  60,  62,  64,  65,  67,  69,  71,  
       73,  75,  78,  80,  83,  86,  89,  92,  96, 100, 
      104, 108, 112, 116, 120, 124, 128, 132, 136, 140, 
      144, 148, 152, 156, 160, 164, 168, 172, 176, 180, 
      184, 188, 192, 196, 200, 224, 248, 272, 296, 320, 
      344, 368, 392, 416, 440, 464, 488, 512, 536, 560, 
      584, 608, 632, 656, 680, 704, 728, 752, 776, 800
   };	// all multiplied by 100

   public static int gravToClicks(double grav)
   {
      int target = (int)Math.round(grav * 100);
      
      for (int n = 0; n < grav_clicks.length; n++)
      {
         if (grav_clicks[n] == target)
         {
            return n;
         }
      }      
      return -1;
   }

   public static float gravFromClicks( int clicks )
   {
      return grav_clicks[clicks] / 100f;
   }

   public static int tempToClicks(int temp)
   {
      int clicks = (temp + 200) / 4;
      return clicks;
   }

   public static int tempFromClicks(int clicks)
   {
      return clicks * 4 - 200;
   }

   public static int radToClicks(int baseRad)
   {
      return baseRad;
   }

   public static int radFromClicks(int clicks)
   {
      return clicks;
   }

}
