/*
 * Created on Nov 4, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package test.stars.ahc;

import java.awt.Point;

import stars.ahc.HabCalculator;
import stars.ahc.Planet;
import stars.ahc.PlanetData;
import stars.ahc.Race;
import junit.framework.TestCase;

/**
 * @author Steve
 *
 */
public class HabCalculatorTest extends TestCase
{
   
   private Planet createPlanet( String name, String grav, String temp, String rad )
   {
      PlanetData planetData = new PlanetData();
      planetData.values = new String[50];
      planetData.values[Planet.PLANET_GRAV_BASE] = grav;
      planetData.values[Planet.PLANET_TEMP_BASE] = temp;
      planetData.values[Planet.PLANET_RAD_BASE] = rad;
      
      Planet planet = new Planet( name, 2400, new Point(0,0), planetData, null );

      return planet;
   }
   
   public void testHabCalc() throws Exception
   {
      int habValue;
      
      Race humanoids = new Race(null);
      humanoids.setGravRange( 0.25, 3.92 );
      humanoids.setTempRange( -128, 128 );
      humanoids.setRadRange( 22, 82 );
      
      Planet DoReMi = createPlanet( "Do Re Mi", "2.24", "-124", "92" );
      habValue = HabCalculator.calcHabValue( DoReMi, humanoids, 7, 11, 11 );
      assertEquals( 14, habValue );

      Planet Strike3 = createPlanet( "Srike 3", "0.75", "144", "34" );
      habValue = HabCalculator.calcHabValue( Strike3, humanoids, 7, 11, 11 );
      assertEquals( 52, habValue );
   }
   
   public void testHabDistribution()
   {
      int totalHabitable0 = 0;
      int totalHabitable15 = 0;
      int testSize = 1000;
      
      Race humanoids = new Race(null);
      humanoids.setGravRange( 0.25, 3.92 );
      humanoids.setTempRange( -128, 128 );
      humanoids.setRadRange( 22, 82 );

      for (int n = 0; n < testSize; n++)
      {
         float grav = HabCalculator.getRandomGrav();
         int temp = HabCalculator.getRandomTemp();
         int rad = HabCalculator.getRandomRad();
         
         Planet planet = createPlanet( "x", ""+grav, ""+temp, ""+rad );
         
         // Is it habitable with no terraforming

         int hab0 = HabCalculator.calcHabValue( planet, humanoids, 0 );

         if (hab0 > 0)
         {
            totalHabitable0++;
         }

         // Is it habitable with 15% terraforming
         
         int hab15 = HabCalculator.calcHabValue( planet, humanoids, 15 );

         if (hab15 > 0)
         {
            totalHabitable15++;
         }
      }
      
      int percent0 = (int)(100f * totalHabitable0 / testSize);
      
      assertTrue( "without terraforming is about 33%", Math.abs(percent0-33) < 5 );

      int percent15 = (int)(100f * totalHabitable15 / testSize);
      
      assertTrue( "with 15% terraforming is about 90%", Math.abs(percent15-90) < 5 );
   }
}
