/*
 * Created on Nov 13, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.mapanimator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import stars.ahc.Game;
import stars.ahc.plugins.map.MapButtonPlugin;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapFrame;

/**
 * @author Steve
 *
 */
public class MapAnimatorPlugin implements MapButtonPlugin
{
   private MapConfig mapConfig;
   private Game game;
   private MapFrame mapFrame;

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.MapButtonPlugin#initialize(stars.ahc.plugins.map.MapConfig, stars.ahc.Game, stars.ahc.plugins.map.MapFrame)
    */
   public void initialize(MapConfig config, Game game, MapFrame mapFrame)
   {
      this.mapConfig = config;
      this.game = game;
      this.mapFrame = mapFrame;
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.MapButtonPlugin#getButtonText()
    */
   public String getButtonText()
   {
      return "Animate";
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.MapButtonPlugin#getButtonToolTip()
    */
   public String getButtonToolTip()
   {
      return "Create an animation of the map (Quicktime movie)";
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.MapButtonPlugin#execute()
    */
   public void execute()
   {
      if (JavaMediaFrameworkAvailable())
      {
         createAndShowMapAnimatorFrame();
      }
      else
      {
         showMessage( "Java Media Framework is not available" );
      }
   }
   

   /**
    * 
    */
   private void createAndShowMapAnimatorFrame()
   {
      //
      // Why do we jump through all these hoops ?
      // So that this class has absolutely no dependancies - either directly or indirectly -
      // on the Java Media Framework library.
      //
      JFrame frame;
      MapAnimatorWindow window;
      
      try
      {
         Class c = Class.forName( "stars.ahc.plugins.map.mapanimator.MapAnimatorWindowImplementation" );
         frame = (JFrame)c.newInstance();
         window = (MapAnimatorWindow)frame; 
      }
      catch (Exception e)
      {
         e.printStackTrace();
         showMessage( "Unable to create map animator window" );
         return;
      }
      
      window.initialize( mapConfig, game, mapFrame );
      frame.show();      
   }

   /**
    * @return true if the Java Media Framework is available
    */
   private boolean JavaMediaFrameworkAvailable()
   {
      try
      {
         Class c = Class.forName( "javax.media.Manager" );
         return true;
      }
      catch (ClassNotFoundException e)
      {
         return false;
      }
   }

   private void showMessage( String message )
   {
      JOptionPane.showMessageDialog( mapFrame, message );
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Map animator";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return false;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
      // not implemented
   }

}
