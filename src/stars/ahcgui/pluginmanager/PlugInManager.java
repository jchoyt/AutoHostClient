/*
 * Created on Oct 8, 2004
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
package stars.ahcgui.pluginmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import stars.ahc.Log;

/**
 * Finds and loads AutoHostClient plugins.
 * <p><p>
 * Plugins are JAR files containing classes that implement the plugin interfaces.  Each plugin file
 * also contains a file called "plugin.data" that lists the plugin classes.
 * <p><p>
 * This class follows the singleton pattern - there should only be one instance per JVM. To enforce
 * this the only constructor is private, and the only way to get access to the instance is through
 * the static method getPluginManager().
 *
 * @author Steve Leach
 */
public class PlugInManager
{
   private static PlugInManager singleton = null;

   /**
    * Get a reference to the system-wide plugin manager instance.
    */
   public static PlugInManager getPluginManager()
   {
      if (singleton == null)
      {
         singleton = new PlugInManager();
      }

      return singleton;
   }

   private PlugInManager()
   {
      directories.add( "./plugins" );
   }

   private ArrayList directories = new ArrayList();
   private Vector loaders = new Vector();
   private ArrayList gamePanelButtons = new ArrayList();
   private ArrayList mapLayers = new ArrayList();

   public void addPluginDirectory( String directory )
   {
      directories.add( directory );
   }

   /**
    * Searches the plugins directory for plugin files and loads all the plugins it finds in them.
    */
   public void findAndLoadPlugins() throws PluginLoadError
   {
      for (int n = 0; n < directories.size(); n++)
      {
         String directory = (String)directories.get(n);

         findAndLoadPluginsFromDirectory( directory );
      }
   }

   /**
    * @throws Exception
    *
    */
   private void findAndLoadPluginsFromDirectory(String pluginDirectory) throws PluginLoadError
   {
      File dir = new File( pluginDirectory );

      File[] files = dir.listFiles();

      // Scan through all the files in the directory looking for Jar files

      for (int n = 0; files != null && n < files.length; n++)
      {
         String name = files[n].getName();

         if (name.toLowerCase().endsWith(".jar"))
         {
            processPluginFile( files[n] );
         }
      }
   }

   /**
    * Load the plugins from the file
    * @throws PluginLoadError
    * @throws Exception
    */
   private void processPluginFile(File file) throws PluginLoadError
   {
      try
      {
         JarFile jar = new JarFile( file );

         JarEntry entry = jar.getJarEntry( "plugin.data" );

         if (entry == null)
         {
            throw new PluginLoadError( "Jar file contains no plugin data" );
         }

         BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)));

         URLClassLoader loader = registerClassLoader( file );

         String line;

         while ((line = reader.readLine()) != null)
         {
            String[] tokens = line.split("=");
            if (tokens.length < 2)
            {
               throw new PluginLoadError( "Plugin data not valid: '" + line + "' in " + file.getName() );
            }
            String key = tokens[0];
            String value = tokens[1];

            if (key.equals("plugin"))
            {
               processPlugIn( file, value, loader );
            }
         }
      }
      catch (IOException e)
      {
         throw new PluginLoadError( "IO error loading plugin: " + file.getName(), e );
      }
   }

   /**
    * Registers a new plugin class loader
    */
   private URLClassLoader registerClassLoader(File file) throws PluginLoadError
   {
      URL[] urls = new URL[1];

      try
      {
         urls[0] = file.toURL();
      }
      catch (MalformedURLException e)
      {
         throw new PluginLoadError( "Malformed URL loading plugin: " + file.getName(), e );
      }

      URLClassLoader loader = new URLClassLoader( urls );

      loaders.add( loader );

      return loader;
   }

   /**
    * @throws MalformedURLException
    */
   private void processPlugIn(File pluginFile, String className, URLClassLoader loader) throws PluginLoadError
   {
      Log.log( Log.NOTICE, this, "Loading plugin " + className + " from " + pluginFile.getName() );

      try
      {
         Class pluginClass = loader.loadClass( className );

         PlugIn plugin = (PlugIn)pluginClass.newInstance();

         if (plugin instanceof GamePanelButtonPlugin)
         {
            gamePanelButtons.add( plugin );
         }
         else if (plugin instanceof MapLayer)
         {
            mapLayers.add( plugin );
         }
      }
      catch (ClassNotFoundException e)
      {
         throw new PluginLoadError( "Plugin class not found: " + className, e );
      }
      catch (InstantiationException e)
      {
         throw new PluginLoadError( "Cannot create instance of plugin class: " + className, e );
      }
      catch (IllegalAccessException e)
      {
         throw new PluginLoadError( "Illegal access creating instance of plugin class: " + className, e );
      }
   }

   public GamePanelButtonPlugin[] getGamePanelButtons()
   {
      return (GamePanelButtonPlugin[])gamePanelButtons.toArray( new GamePanelButtonPlugin[0] );
   }

   public void addGamePanelButton( GamePanelButtonPlugin button )
   {
      gamePanelButtons.add( button );
   }

   public MapLayer[] getMapLayers()
   {
      return (MapLayer[])mapLayers.toArray( new MapLayer[0] );
   }
}
