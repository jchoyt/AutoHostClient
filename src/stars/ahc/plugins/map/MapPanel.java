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
package stars.ahc.plugins.map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import stars.ahc.Game;

/**
 * @author Steve Leach
 *
 */
public class MapPanel extends JPanel
{
   private ArrayList layers = new ArrayList();
   private Game game = null;
   private MapConfig config = null;
   
   public MapPanel( Game game, MapConfig config ) throws MapDisplayError
   {
      this.game = game;
      this.config = config;
      
   }

   /**
    * Creates, initializes and adds a new map layer
    * 
    * @param className
    * @throws MapDisplayError
    */
   public MapLayer addMapLayer( String className ) throws MapDisplayError
   {
      try
      {
         Class mapLayerClass = Class.forName( className );
         
         return addMapLayer( mapLayerClass );
      }
      catch (ClassNotFoundException e)
      {
         throw new MapDisplayError( "Class not found: " + className, e );
      }
   }
   
   /**
    * Creates, initializes and adds a new map layer
    * 
    * @param mapLayerClass
    * @throws MapDisplayError
    */
   public MapLayer addMapLayer( Class mapLayerClass ) throws MapDisplayError
   {
      try
      {
         MapLayer layer = (MapLayer)mapLayerClass.newInstance();
         
         layer.initialize( game, config );
         
         addMapLayer( layer );
         
         return layer;
      }
      catch (InstantiationException e)
      {
         throw new MapDisplayError( "Could not instantiate class: " + mapLayerClass.getName(), e );
      }
      catch (IllegalAccessException e)
      {
         throw new MapDisplayError( "Illegal access: " + mapLayerClass.getName(), e );
      }
   }
   
   /**
    * The layer should already have been initialized
    * @param layer
    */
   public void addMapLayer( MapLayer layer )
   {
      if (layer != null)
      {
         layers.add( layer );
      }
   }
   
   public void paint(Graphics g)
   {
      Graphics2D g2D = (Graphics2D)g;

      // Cycle through the layers
      for (int n = 0; n < layers.size(); n++)
      {
         MapLayer layer = (MapLayer)layers.get( n );
         
         g2D.scale( config.mapScale, config.mapScale );
         
         // If the layer is enabled then draw it
         if ( layer.isEnabled() )
         {
            layer.draw( g2D );
         }
      }
   }

}
