/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package test.stars.ahc;

import junit.framework.TestCase;
import stars.ahcgui.pluginmanager.PlugInManager;

/**
 * @author Steve Leach
 *
 */
public class PluginManagerTest extends TestCase
{
   public void testFindPlugins() throws Exception
   {
      PlugInManager plugInManager = PlugInManager.getPluginManager();
      
      plugInManager.findAndLoadPlugins();
      
   }
}
