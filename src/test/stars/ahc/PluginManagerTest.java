/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package test.stars.ahc;

import java.util.ArrayList;

import junit.framework.TestCase;
import stars.ahcgui.pluginmanager.MapLayer;
import stars.ahcgui.pluginmanager.PlugInManager;
import stars.ahcgui.pluginmanager.PluginLoadError;

/**
 * @author Steve Leach
 *
 */
public class PluginManagerTest extends TestCase
{
   PlugInManager plugInManager;
   
   protected void setUp() throws Exception
   {
      super.setUp();
      
      plugInManager = PlugInManager.getPluginManager();
      plugInManager.findAndLoadPlugins();      
   }
   
   protected void tearDown() throws Exception
   {
      super.tearDown();
   }
   
   public void testGetPlugins() throws PluginLoadError
   {
      ArrayList mapLayers = plugInManager.getPlugins( MapLayer.class );
      
      assertTrue( "Got some map layer plugins", mapLayers.size() > 0 );
      
      for (int n = 0; n < mapLayers.size(); n++)
      {
         assertTrue( "Item " + n + " is maplayer", MapLayer.class.isAssignableFrom((Class)mapLayers.get(n)) );
      }
   }
}
