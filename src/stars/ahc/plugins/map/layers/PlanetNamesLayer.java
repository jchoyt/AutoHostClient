/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapDisplayError;
import stars.ahcgui.pluginmanager.MapLayer;

/**
 * @author Steve
 *
 */
public class PlanetNamesLayer implements MapLayer
{
   private boolean enabled = true;
   private Game game;
   private MapConfig config;
   private ArrayList planets;

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return enabled;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Planet names";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#initialize(stars.ahc.Game, stars.ahc.plugins.map.MapConfig)
    */
   public void initialize(Game game, MapConfig config) throws MapDisplayError
   {
      this.game = game;
      this.config = config;
      
      planets = PlanetLoader.loadPlanets( game );
      
      config.calcUniverseSize( planets );
      
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#isScaled()
    */
   public boolean isScaled()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      g.setColor( Color.YELLOW );
      
      for (int n = 0; n < planets.size(); n++)
      {
         Planet planet = (Planet)planets.get( n );
         
         Point screenPos = config.mapToScreen( planet.getPosition() );
         
         g.drawString( planet.name, screenPos.x-20, screenPos.y+16 );
      }
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Planet names layer";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

}
