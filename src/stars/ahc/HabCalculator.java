/*
 * Created on Nov 4, 2004
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
 * Calculates hab values for planets after terraforming. 
 * 
 * @author Steve Leach
 */
public class HabCalculator
{
   private static int NULL_RACE = -2;
   private static int NO_HAB_RANGE = -3;
   private static int NO_HAB_VALUES = -4;
   private static int BAD_GRAV_VAL = -5;
   private static int BAD_TEMP_VAL = -6;
   private static int BAD_RAD_VAL = -7;
   
   public static final int GRAV = 1;
   public static final int TEMP = 2;
   public static final int RAD = 3;

   /**
    * Table for converting grav values to/from tick counts
    */
   private static final int[] grav_clicks = 
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
   
   
   /**
    * Calculates the maximum hab value for the specified race on the specified planet after terraforming.
    * <p>
    * The same value is used for max terraforming for grav, temp and rad.
    * <p>
    * Negative numbers indicate error codes. 
    */
   public static int calcHabValue( Planet planet, Race race, int terraFormLevel )
   {
      return calcHabValue( planet, race, terraFormLevel, terraFormLevel, terraFormLevel );
   }
   
   /**
    * Calculates the maximum hab value for the specified race on the specified planet after terraforming.
    * <p>
    * The maximum terraforming possible for the race is used (either 15% or 30%, depending on TT LRT)
    * <p>
    * Negative numbers indicate error codes. 
    */
   public static int calcHabValue( Planet planet, Race race )
   {
      return calcHabValue( planet, race, race.getMaxTerraForm() );
   }
   
   /**
    * Calculates the maximum hab value for the specified race on the specified planet after terraforming.
    * <p>
    * Different values can be specified for max terraforming for grav, temp and rad.
    * <p>
    * Negative numbers indicate error codes. 
    */
   public static int calcHabValue( Planet planet, Race race, int grav_level, int temp_level, int rad_level )
   {
      long habValue = 0;
      final double sqrt3 = 1.7320508075688772; // Math.sqrt(3.0)

      if (race == null)
      {
         return NULL_RACE;
      }
      
      if (race.habRangeKnown() == false)
      {
         return NO_HAB_RANGE;
      }
      
      if (planet.habValuesKnown() == false)
      {
         return NO_HAB_VALUES;
      }

      double ND_g = 0.0, ND_t = 0.0, ND_r = 0.0; // default values assume immune

      double grav_ave = (race.getMaxGravClicks() + race.getMinGravClicks()) / 2;
      double temp_ave = (race.getMaxTempClicks() + race.getMinTempClicks()) / 2;
      double rad_ave = (race.getMaxRadClicks() + race.getMinRadClicks()) / 2;

      double grav_diff = planet.getBaseGravClicks() - grav_ave;
      double temp_diff = planet.getBaseTempClicks() - temp_ave;
      double rad_diff = planet.getBaseRadClicks() - rad_ave;

      int grav_terra = (grav_diff > 0.0) ? grav_level : -grav_level;
      int temp_terra = (temp_diff > 0.0) ? temp_level : -temp_level;
      int rad_terra = (rad_diff > 0.0) ? rad_level : -rad_level;

      double mod_grav = (Math.abs(grav_diff) < grav_level) ? grav_ave : planet.getBaseGravClicks() - grav_terra;
      double mod_temp = (Math.abs(temp_diff) < temp_level) ? temp_ave : planet.getBaseTempClicks() - temp_terra;
      double mod_rad = (Math.abs(rad_diff) < rad_level) ? rad_ave : planet.getBaseRadClicks() - rad_terra;

      if (race.gravImmune() == false)
      {
         double grav_range = (race.getMaxGravClicks() - race.getMinGravClicks()) / 2;

         ND_g = Math.abs(mod_grav - grav_ave) / grav_range;
      }

      if (race.tempImmune() == false)
      {
         double temp_range = (race.getMaxTempClicks() - race.getMinTempClicks()) / 2;

         ND_t = Math.abs(mod_temp - temp_ave) / temp_range;
      }

      if (race.radImmune() == false)
      {
         double rad_range = (race.getMaxRadClicks() - race.getMinRadClicks()) / 2;

         ND_r = Math.abs(mod_rad - rad_ave) / rad_range;
      }

      //bodge
      if (ND_g > 1.0f)
         return BAD_GRAV_VAL;
      if (ND_t > 1.0f)
         return BAD_TEMP_VAL;
      if (ND_r > 1.0f)
         return BAD_RAD_VAL;

      double g1 = 1 - ND_g;
      double t1 = 1 - ND_t;
      double r1 = 1 - ND_r;

      double ND_0 = Math.sqrt((g1 * g1) + (t1 * t1) + (r1 * r1)) / sqrt3;

      double ND_g1 = (ND_g < 0.5) ? 0 : ND_g - 0.5;
      double ND_t1 = (ND_t < 0.5) ? 0 : ND_t - 0.5;
      double ND_r1 = (ND_r < 0.5) ? 0 : ND_r - 0.5;

      double f1 = ((1.0 - ND_g1) * (1.0 - ND_t1) * (1.0 - ND_r1));

      double value = ND_0 * f1;

      habValue = Math.round(value * 100);
      
      return (int)habValue;
   }

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
   
   private static int randIntUpTo( int max )
   {
      return (int)Math.round( Math.floor( (Math.random() * max) ) );
   }

   /**
    * Gets a random hab (grav, temp or rad) value
    * <p>
    * The spread of values matches that actually seen in the game
    * <p>
    * The result is in "clicks" rather than an actual environmental value 
    */
   private static int getRandomHabVal( int which )
   {
      // var planetval = (id==3) ? (Math.floor(Math.random()*99)+1) : (Math.floor(Math.random()*90) + Math.floor(Math.random()*10) + 1);   // Corrected formula - CR 26102004
      long val;
      
      if (which == 3)
      {
         val = randIntUpTo(99) + 1;         
      }
      else
      {
         val = randIntUpTo(90) + randIntUpTo(10) + 1;
      }
      
      return (int)val;
   }
   
   public static float getRandomGrav()
   {
      int clicks = getRandomHabVal(GRAV);
      return gravFromClicks(clicks);
   }
   
   public static int getRandomTemp()
   {
      int clicks = getRandomHabVal(TEMP);
      return tempFromClicks(clicks);
   }

   public static int getRandomRad()
   {
      int clicks = getRandomHabVal(RAD);
      return radFromClicks(clicks);
   }
}
