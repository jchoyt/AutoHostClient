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
package stars.ahcgui.pluginmanager;

import java.awt.Graphics2D;

import stars.ahc.Game;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapDisplayError;

/**
 * All map layers must implement this interface
 * 
 * @author Steve Leach
 */
public interface MapLayer extends PlugIn
{
   public boolean isEnabled();
   public String getDescription();
   
   /**
    * Initialize the map layer object.
    * <br><br>
    * Plugins must have no-argument constructors so this is called after the layer
    * object is created to give it the information required for it to work.
    */
   public void initialize( Game game, MapConfig config ) throws MapDisplayError;

   /**
    * Returns true if the layer should be scaled as the map is zoomed in and out.
    * Normally this should be true.
    */
   public boolean isScaled();
   
   /**
    * Draws the map layer details onto the supplied graphics device 
    */
   public void draw( Graphics2D g );
}
