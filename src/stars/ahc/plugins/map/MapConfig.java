/*
 * Created on Oct 6, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import stars.ahc.Planet;

/**
 * Map configuration
 * 
 * @author Steve Leach
 */
public class MapConfig
{
   public int mapXpos = 0;
   public int mapYpos = 0;
   public int gameMinX = 0;
   public int gameMinY = 0;
   public int gameMaxX = 0;
   public int gameMaxY = 0;
   public int centreX = 0;
   public int centreY = 0;
   public double mapScale = 1.0;
   
   private Vector changeListeners = new Vector();
   
   /**
    * Converts map coordinates into (untransformed) screen coordinates 
    */
   public Point mapToScreen( Point mapPos )
   {
      Point screenPos = new Point( mapPos.x - centreX, centreY - mapPos.y );

      return screenPos;
   }
   
   /**
    * Calculates the boundaries of the universe 
    */
   public void calcUniverseSize( ArrayList planets )
   {
      gameMinX = Integer.MAX_VALUE;
      gameMinY = Integer.MAX_VALUE;
      gameMaxX = 0;
      gameMaxY = 0;

      for (int n = 0; n < planets.size(); n++)
      {
         Planet planet = (Planet)planets.get( n );
         
         if (planet.x < gameMinX) gameMinX = planet.x;
         if (planet.y < gameMinY) gameMinY = planet.y;
         
         if (planet.x > gameMaxX) gameMaxX = planet.x;
         if (planet.y > gameMaxY) gameMaxY = planet.y;
      }
      
      int border = 20;
      
      gameMinX -= border;
      gameMinY -= border;
      gameMaxX += border;
      gameMaxY += border;
      
      centreX = (gameMinX + gameMaxX) / 2;
      centreY = (gameMinY + gameMaxY) / 2;
      mapXpos = centreX;      
      mapYpos = centreY;       
   }

   public void addChangeListener( MapConfigChangeListener listener )
   {
      changeListeners.add( listener );
   }
   
   public void notifyChangeListeners()
   {
      for (int n = 0; n < changeListeners.size(); n++)
      {
         MapConfigChangeListener listener = (MapConfigChangeListener)changeListeners.get(n);
         listener.mapConfigChanged(this);
      }
   }
}
