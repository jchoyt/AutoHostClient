/*
 * Created on Oct 6, 2004
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
package stars.ahc.plugins.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.ReportLoaderException;
import stars.ahc.plugins.map.AbstractMapLayer;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapDisplayError;

/**
 * @author Steve
 *
 */
public class PlanetLayer extends AbstractMapLayer
{ 
   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#getDescription()
    */
   public String getDescription()
   {
      return "Planets";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      for (int n = 1; n <= game.getPlanetCount(); n++)
      {
         Planet planet = game.getPlanet(n);
         
         Point screenPos = mapConfig.mapToScreen( planet.getPosition() );
         
         if (planet.isUnoccupied())
         {
            g.setColor( Color.WHITE );
         }
         else
         {
            g.setColor( game.getRaceColor( planet.getOwner() ) );
         }
         
         g.fillOval( screenPos.x-2, screenPos.y-2, 5, 5 );
         
         g.setColor( Color.BLACK );
         g.drawOval( screenPos.x-2, screenPos.y-2, 5, 5 );
      }
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#initialize(stars.ahc.Game, stars.ahcgui.map.MapConfig)
    */
   public void initialize(Game game, MapConfig config) throws MapDisplayError
   {
      this.mapConfig = config;
      this.game = game;
      
      if (game.getPlanetCount() == 0)
      {
         try
         {
            game.loadReports();
         }
         catch (ReportLoaderException e)
         {
            throw new MapDisplayError( "Error loading map file", e );
         }
      
         mapConfig.calcUniverseSize( game );
      }
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {      
      return "Planet layer";
   }
   
}
