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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JComponent;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.MapLayer;

/**
 * Panel on which the map is drawn.
 * <p>
 * FIXME: current implementation cannot handle partial repaint requests (ie. where there is a clip region)
 * <p>
 * @author Steve Leach
 */
public class MapPanel extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener, MapConfigChangeListener
{
   private Game game = null;
   private MapConfig config = null;
   private boolean dragging = false;
   private Point prevMousePos = null;
   private ArrayList layers = new ArrayList();
   
   /**
    * Default constructor for MapPanel.
    * 
    * @param game - the game for which a map is to be displayed
    * @param config - map configuration information
    * @throws MapDisplayError
    */
   public MapPanel( Game game, MapConfig config ) throws MapDisplayError
   {
      this.game = game;
      this.config = config;
      
      addMouseListener( this );
      addMouseMotionListener( this );
      addMouseWheelListener( this );
      
      config.addChangeListener( this );
      
      setBackground( Color.BLACK );
   }
   
   public void paint(Graphics g)
   {
      Graphics2D g2D = (Graphics2D)g;
      
      // Cycle through the layers
      for (int n = 0; n < layers.size(); n++)
      {
         MapLayer layer = (MapLayer)layers.get(n);
         
         // If the layer is enabled then draw it
         if ( layer.isEnabled() )
         {
            setupTransform( layer, g2D );

            if (g2D != null)
            {
               layer.draw( g2D );
            }
         }
      }
   }

   /**
    * Sets the transformation (movement and scaling) for the graphics device
    * 
    * @param layer - the MapLayer to which the tranform is to be applied
    * @param g2d - the Graphics device to create the transform for
    */
   private void setupTransform(MapLayer layer, Graphics2D g2d)
   {
      AffineTransform xform = new AffineTransform();
      
      if (layer.isScaled())
      {
         int mapSize = config.gameMaxX - config.gameMinX;
         
         xform.translate( this.getWidth() / 2, this.getHeight() / 2 );
         xform.scale( config.mapScale, config.mapScale );         
         xform.translate( config.mapXpos - config.gameMinX - mapSize/2, config.mapYpos - config.gameMinY - mapSize / 2 );
      }

      g2d.setTransform( xform );      
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
    */
   public void mouseClicked(MouseEvent arg0)
   {
      // empty
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
    */
   public void mouseEntered(MouseEvent arg0)
   {
      // empty
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
    */
   public void mouseExited(MouseEvent arg0)
   {
      // empty
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
    */
   public void mousePressed(MouseEvent event)
   {
      dragging = true;
      setCursor( new Cursor(Cursor.MOVE_CURSOR) );
      prevMousePos = event.getPoint();
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
    */
   public void mouseReleased(MouseEvent arg0)
   {
      setCursor( new Cursor(Cursor.DEFAULT_CURSOR) );
      dragging = false;
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
    */
   public void mouseDragged(MouseEvent event)
   {
      int dx = event.getPoint().x - prevMousePos.x;
      int dy = event.getPoint().y - prevMousePos.y;
      
      config.mapXpos += dx / config.mapScale;
      config.mapYpos += dy / config.mapScale;
      
      repaint();
      
      prevMousePos = event.getPoint();
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
    */
   public void mouseMoved(MouseEvent arg0)
   {
      // TODO Auto-generated method stub
      
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
    */
   public void mouseWheelMoved(MouseWheelEvent event)
   {      
      int amount = event.getWheelRotation();
      if (amount > 0)
      {
         config.mapScale *= 1.1;
      }
      else
      {
         config.mapScale /= 1.1;
      }
      config.notifyChangeListeners();
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.MapConfigChangeListener#mapConfigChanged(stars.ahc.plugins.map.MapConfig)
    */
   public void mapConfigChanged(MapConfig config)
   {
      repaint();
   }

   /**
    * Adds a set of map layers to the map
    */
   public void addMapLayers(ArrayList layers)
   {
      this.layers.addAll( layers );
   }

}
