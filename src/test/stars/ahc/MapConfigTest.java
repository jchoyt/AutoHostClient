/*
 * Created on Oct 11, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package test.stars.ahc;

import java.awt.Point;

import stars.ahc.plugins.map.MapConfig;
import junit.framework.TestCase;

/**
 * @author Steve Leach
 *
 */
public class MapConfigTest extends TestCase
{
   private MapConfig config;
   
   protected void setUp() throws Exception
   {
      super.setUp();
      
      config = new MapConfig();
      config.gameMinX = 1000;
      config.gameMaxX = 2000;
      config.gameMinY = 1000;
      config.gameMaxY = 2000;
      
      config.centreX = 1500;
      config.centreY = 1500;
   }
   
   protected void tearDown() throws Exception
   {      
      super.tearDown();
   }
   
   public void testMapToScreen()
   {
      Point mapPos = new Point( 1200, 1900 );
      
      Point screenPos = config.mapToScreen( mapPos );
      
      assertEquals( -300, screenPos.x );
      assertEquals( -400, screenPos.y );
   }
   
   public void testGetUniverseSize()
   {
      assertEquals( 1000, config.getUniverseSize() );
   }
   
}
