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
public class FleetLayer extends AbstractMapLayer
{

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Fleets";
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
	         Point screenPos = mapConfig.mapToScreen( fleet.getPosition() );
	         
	         g.setColor( game.getRaceColor( fleet.getOwner() ) );
	         
	         g.drawLine( screenPos.x-2, screenPos.y-2, screenPos.x+2, screenPos.y+2 );
	         g.drawLine( screenPos.x-2, screenPos.y+2, screenPos.x+2, screenPos.y-2 );
         }
      }
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Fleets layer";
   }

}
