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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JFrame;

import stars.ahc.Log;

/**
 * Finds, loads and manages AutoHost Client plugins.
 * <p><p>
 * By default, plugins are loaded from the plugins subdirectory of the working directory.  Other
 * directories can be added using the addPluginDirectory() method.
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
   /**
    * Hold a reference to the only permitted instance of this class
    */
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

   /**
    * Only constructor is private - other classes must use the static getPluginManager() method 
    */
   private PlugInManager()
   {
      addPluginDirectory( "./plugins" );
   }

   /**
    * List of directories to scan for plugins
    */
   private ArrayList directories = new ArrayList();
   
   /**
    * List of plugin loaders
    */
   private Vector loaders = new Vector();
   
   /**
    * List of all plugins that have been loaded
    */
   private ArrayList plugins = new ArrayList();


   /**
    * List of all the base plugins that have been loaded
    */
   private ArrayList basePlugins = new ArrayList();

   /**
    * List of all the specific instances of each plugin that have been created
    */
   private ArrayList instances = new ArrayList();
   
   /**
    * Registers a new plugin directory
    *  
    * @param directory
    */
   public void addPluginDirectory( String directory )
   {
      directories.add( directory );
   }

   /**
    * Searches the plugins directories for plugin files and loads all the plugins it finds in them.
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
    * Searches the specified directory for plugin files and loads all the plugins they contain
    */
   private void findAndLoadPluginsFromDirectory(String pluginDirectory) throws PluginLoadError
   {
      File dir = new File( pluginDirectory );

      if (dir.exists() == false)
      {
         throw new PluginLoadError( "Plug-in directory does not exist: " + pluginDirectory );
      }
      
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
    * Load all the plugins found in the specified jar file
    * 
    * @throws PluginLoadError
    * @throws Exception
    */
   private void processPluginFile(File file) throws PluginLoadError
   {
      try
      {
         JarFile jar = new JarFile( file );

         //
         // New method - look for entries with extension .ahplugin
         //
         
         Enumeration entries = jar.entries();
         while (entries.hasMoreElements())
         {
            JarEntry entry = (JarEntry)entries.nextElement();
            
            if (entry.getName().endsWith(".ahcplugin"))
            {
               processPluginDescriptor(jar, entry);
            }
         }
         
         //
         // Old method - look for explicit name plugin.data
         //
         
         JarEntry entry = jar.getJarEntry( "plugin.data" );

         if (entry != null)
         {
            processPluginDescriptor(jar, entry);
         }         
      }
      catch (IOException e)
      {
         throw new PluginLoadError( "IO error loading plugin: " + file.getName(), e );
      }
   }

   /**
    * Reads a plugin descriptor file
    */
   private void processPluginDescriptor(JarFile jar, JarEntry entry) throws IOException, PluginLoadError
   {
      BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(entry)));

      String line;

      while ((line = reader.readLine()) != null)
      {
         String[] tokens = line.split("=");
         if (tokens.length < 2)
         {
            throw new PluginLoadError( "Plugin data not valid: '" + line + "' in " + jar.getName() );
         }
         String key = tokens[0];
         String pluginClass = tokens[1];

         if (key.equals("plugin"))
         {
            try
            {
               processPlugIn( pluginClass );
            }
            catch (Throwable t)
            {
               // Don't re-throw this because we want to continue loading other classes 
               Log.log( Log.ERROR, this, "Could not load plugin: " + pluginClass );
            }
         }
      }
   }

   /**
    * Adds the specified plugin class to the list of loaded plugins
    * <p>
    * A test instance of the class will also be created to ensure that the plugin is valid.
    */
   private void processPlugIn(String className) throws PluginLoadError
   {
      try
      {
         // Get the class
         Class pluginClass = Class.forName( className, false, Thread.currentThread().getContextClassLoader() );

         // Create a new instance to check that it works OK
         PlugIn plugin = (PlugIn)pluginClass.newInstance();
         
         // Add it to the list of plugins (will not happen if the new instance could not be created)
         plugins.add( pluginClass );         
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

   /**
    * Returns all the plugin classes of the specified type 
    */
   public ArrayList getPlugins( Class pluginType )
   {
      ArrayList matchingPlugins = new ArrayList();
      
      for (int n = 0; n < plugins.size(); n++)
      {
         Class pluginClass = (Class)plugins.get(n);
         
         if (pluginType.isAssignableFrom(pluginClass))
         {
            matchingPlugins.add( pluginClass );
         }
      }
      
      return matchingPlugins;
   }
   
   /**
    * Returns all the loaded plugins 
    */
   public ArrayList getAllPlugins()
   {
      return getPlugins( PlugIn.class );
   }

   /**
    * Initialize all plugins that implement the BasePlugIn interface. 
    */
   public void installBasePlugins( JFrame appWindow )
   {
      ArrayList baseClasses = getPlugins( BasePlugIn.class );
      
      for (int n = 0; n < baseClasses.size(); n++)
      {
         Class c = (Class)baseClasses.get(n);
         
         try
         {
            BasePlugIn plugin = (BasePlugIn)c.newInstance();
            plugin.init(appWindow);
            basePlugins.add( plugin );
         }
         catch (Throwable t)
         {
            Log.log( Log.ERROR, this, "Error installing plugin: " + c.getName() );
         }
      }
   }
   
   /**
    * Gives all the base plugins a chance to clean up after themselves
    */
   public void cleanupBasePlugins()
   {
      for (int n = 0; n < basePlugins.size(); n++)
      {
         BasePlugIn plugin = (BasePlugIn)basePlugins.get(n);
         try
         {
            plugin.cleanup();
         }
         catch (Throwable t)
         {
            Log.log( Log.ERROR, this, "Error cleaning up plugin: " + plugin.getName() );
         }
      }
   }
   
   /**
    * Searches for, and returns, a BasePlugin with the specified name.
    */
   public BasePlugIn getBasePlugin( String name )
   {
      for (int n = 0; n < basePlugins.size(); n++)
      {
         BasePlugIn plugin = (BasePlugIn)basePlugins.get(n);

         if (name.equals( plugin.getName()))
         {
            return plugin;
         }
      }
      return null;
   }

   /**
    * Create a new instance of the specified plugin class.
    * <p>
    * The plugin manager keeps track of all the instances created; the full list
    * can be queried with getPluginInstances().
    * <p>
    * If there is an error creating the instance then null is returned, but no
    * exception is thrown.
    * 
    * @author Steve Leach
    */
   public PlugIn newInstance(Class pluginClass)
   {
      PlugIn plugin = null;
      try
      {
         plugin = (PlugIn)pluginClass.newInstance();
         
         instances.add( plugin );
      }
      catch (Throwable t)
      {
         Log.log( Log.ERROR, this, "Could not create new instance of " + pluginClass.getName() );
         t.printStackTrace();
      }
      return plugin;
   }
   
   /**
    * Returns a list of all the specific instances of plugins that have been created. 
    */
   public PlugIn[] getPluginInstances()
   {
      return (PlugIn[])instances.toArray( new PlugIn[0] );
   }
   
   /**
    * Returns true if the specified plugin class is available 
    */
   public boolean pluginAvailable( String className )
   {
      boolean result = false;
      
      for (int n = 0; n < plugins.size(); n++)
      {
         Class pluginClass = (Class)plugins.get(n);
         
         if (pluginClass.getName().equals( className ))
         {
            result = true;
            break;
         }
      }
      
      return result;
   }
}
