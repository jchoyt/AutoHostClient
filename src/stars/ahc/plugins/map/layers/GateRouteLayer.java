/*
 * Created on Oct 12, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import stars.ahc.Planet;
import stars.ahc.plugins.map.AbstractMapLayer;

/**
 * @author Steve Leach
 *
 */
public class GateRouteLayer extends AbstractMapLayer
{

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Stargate routes";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      for (int n = 1; n <= game.getPlanetCount(); n++)
      {
         Planet planet1 = game.getPlanet(n);

         if (planet1.getGateRange() > 0)
         {           
            String owner = planet1.getOwner();
            
            g.setColor( Color.WHITE );
            Point screenPos1 = mapConfig.mapToScreen( planet1.getPosition() );
            
            for (int m = 1; m <= game.getPlanetCount(); m++)
            {
               Planet planet2 = game.getPlanet(m);
               
               if (planet2.getGateRange() > 0)
               {
                  if (planet2.getOwner().equals( owner ))
                  {
                     if (planet2.distanceFrom(planet1) <= planet1.getGateRange())
                     {
                        Point screenPos2 = mapConfig.mapToScreen( planet2.getPosition() );
                     
                        g.setColor( Color.GRAY );
                     
                        g.drawLine( screenPos1.x, screenPos1.y, screenPos2.x, screenPos2.y );
                     }
                  }
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
      return "Gate route map layer";
   }

}
