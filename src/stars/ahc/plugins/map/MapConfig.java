/*
 * Created on Oct 6, 2004
 *
 * Copyright (c) 2004, Steve Leach
 * 
 * Part of the Stars! AutoHost Client software.
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
 */
package stars.ahc.plugins.map;

import java.awt.Point;
import java.util.Vector;

import stars.ahc.Game;
import stars.ahc.Planet;

/**
 * Map configuration information
 * <p>
 * Includes details such as the scale and centre-point of the current map view.
 * 
 * @author Steve Leach
 */
public class MapConfig
{
   public int year = 0;			// the game year for which the map is being displayed 
   public int mapXpos = 0;		
   public int mapYpos = 0;
   public int gameMinX = 0;	
   public int gameMinY = 0;
   public int gameMaxX = 0;
   public int gameMaxY = 0;
   public int centreX = 0;
   public int centreY = 0;
   public int hoverX = 0;		// where the mouse is hovering (in game coords)
   public int hoverY = 0;
   public double mapScale = 1.0;
   
   private Vector changeListeners = new Vector();
   
   /**
    * Converts map co-ordinates into (untransformed) screen coordinates.
    * <p>
    * Note that the co-ordinates returned will not have been scaled and 
    * transformed, as this is done using the transform matrix of the graphics
    * device when rendering the map. 
    */
   public Point mapToScreen( Point mapPos )
   {
      Point screenPos = new Point( mapPos.x - centreX, centreY - mapPos.y );

      return screenPos;
   }
   
   public Point screenToMap( Point screenPos )
   {
      Point s = screenPos;
      Point m = new Point();
      
      m.x = s.x + centreX;
      m.y = centreY - s.y;
      
      return m;
   }
   
   /**
    * Calculates the boundaries of the universe 
    */
   public void calcUniverseSize( Game game )
   {
      gameMinX = Integer.MAX_VALUE;
      gameMinY = Integer.MAX_VALUE;
      gameMaxX = 0;
      gameMaxY = 0;

      for (int n = 1; n <= game.getPlanetCount(); n++)
      {
         Planet planet = game.getPlanet(n);
         
         if (planet.getX() < gameMinX) gameMinX = planet.getX();
         if (planet.getY() < gameMinY) gameMinY = planet.getY();
         
         if (planet.getX() > gameMaxX) gameMaxX = planet.getX();
         if (planet.getY() > gameMaxY) gameMaxY = planet.getY();
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

   /**
    * Registers another class to receive notifications when the map configuration changes 
    */
   public void addChangeListener( MapConfigChangeListener listener )
   {
      changeListeners.add( listener );
   }
   
   /**
    * Notify all registered change listeners that a change has occurred 
    */
   public void notifyChangeListeners()
   {
      for (int n = 0; n < changeListeners.size(); n++)
      {
         MapConfigChangeListener listener = (MapConfigChangeListener)changeListeners.get(n);
         listener.mapConfigChanged(this);
      }
   }

   /**
    * Returns the size of the universe (in Stars! light years)
    * <p>
    * This is the larger of the height or width.
    */
   public int getUniverseSize()
   {
      return Math.max( gameMaxX - gameMinX, gameMaxY - gameMinY );
   }

}
