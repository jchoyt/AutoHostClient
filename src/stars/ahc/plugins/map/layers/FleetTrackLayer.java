/*
 * Created on Oct 27, 2004
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

import java.awt.Graphics2D;
import java.awt.Point;

import stars.ahc.Fleet;
import stars.ahc.Utils;
import stars.ahc.plugins.map.AbstractMapLayer;

/**
 * @author Steve Leach
 *
 */
public class FleetTrackLayer extends AbstractMapLayer
{

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Fleet tracks";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      int fleetCount = game.getFleetCount( mapConfig.year );
      
      for (int n = 0; n < fleetCount; n++)
      {
         Fleet fleet = game.getFleet( mapConfig.year, n );

         if (Utils.empty(fleet.getValue(Fleet.PLANET)))
         {
	         g.setColor( game.getRaceColor( fleet.getOwner() ) );
	         
	         Point prevPos = mapConfig.mapToScreen( fleet.getPosition() );
	         
	         for (int y = mapConfig.year-1; y > 2400; y--)
	         {
	            Fleet f = game.getFleetByID( y, fleet.getOwner(), fleet.getID() );
	            
	            if (f != null)
	            {
	               Point p = mapConfig.mapToScreen( f.getPosition() );
	               g.drawLine( prevPos.x, prevPos.y, p.x, p.y );
	               prevPos = p;
	            }
	         }
         }
      }
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Fleet tracks layer";
   }

}
