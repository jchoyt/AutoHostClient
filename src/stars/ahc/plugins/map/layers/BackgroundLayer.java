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
import java.awt.Rectangle;

import javax.swing.JComponent;

import stars.ahc.Game;
import stars.ahc.plugins.map.MapConfig;
import stars.ahcgui.pluginmanager.MapLayer;

/**
 * Draws the map background.  Currently just fills it in black.
 * 
 * @author Steve Leach
 */
public class BackgroundLayer implements MapLayer
{
   private Game game = null;
   private MapConfig config = null;
     
   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#isVisible()
    */
   public boolean isEnabled()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#draw(java.awt.Graphics)
    */
   public void draw(Graphics2D g)
   {
      g.setPaint( Color.BLACK );
      
      Rectangle rect = g.getClipBounds(); 
      
      if (rect != null)
      {
         g.fill( rect );
      }      
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#getDescription()
    */
   public String getDescription()
   {
      return "Background";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#initialize(stars.ahc.Game, stars.ahcgui.map.MapConfig)
    */
   public void initialize(Game game, MapConfig config)
   {
      this.game = game;
      this.config = config;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Background layer";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
      // TODO Auto-generated method stub
      
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#isScaled()
    */
   public boolean isScaled()
   {
      return true;
   }

   public JComponent getControls()
   {
      return null;
   }
   
}
