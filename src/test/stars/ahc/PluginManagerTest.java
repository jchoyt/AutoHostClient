/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package test.stars.ahc;

import stars.ahcgui.pluginmanager.GamePanelButtonPlugin;
import stars.ahcgui.pluginmanager.PlugInManager;
import junit.framework.TestCase;

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
      
      GamePanelButtonPlugin[] plugins = plugInManager.getGamePanelButtons();
      
      for (int n = 0; n < plugins.length; n++)
      {
         System.out.println( plugins[n].getDescription() + " [" + plugins[n].getButtonText() + "]" );
      }
   }
}
