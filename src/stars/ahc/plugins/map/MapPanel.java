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
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

import javax.swing.JComponent;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.MapLayer;

/**
 * Panel on which the map is drawn.
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
   private ArrayList mapMouseMoveListeners = new ArrayList();
   private MapFrame mapFrame = null;
   
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
   
   public void setMapFrame( MapFrame mapFrame )
   {
      this.mapFrame = mapFrame;
   }
   
   public void paint(Graphics g)
   {
      Graphics2D g2D = (Graphics2D)g;

      // TODO: fix this
      // Nasty hack to remove corruption when the UI wants to repaint just part of the map.
      // Currently we ignore the request but schedule a full repaint instead.
      // Eventually we want to handle partial redraws properly.
      // Steve Leach (ashamed), 26 Oct 2004
      if (g2D.getClipBounds().x + g2D.getClipBounds().y > 0)
      {
         repaint();
         return;
      }
      
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
               try
               {
                  layer.draw( g2D );
               }
               catch (Throwable t)
               {
                  // TODO: handle this elegantly
                  // We want to continue drawing the map (graceful degredation).
                  // We also want to inform the user and/or log the error.
                  // But we don't want thousands of messages if there are lots of redraws.
               }
            }
         }
      }
   }

   /**
    * Returns an Affine Transform applying the current map configuration (pan and zoom) 
    */
   private AffineTransform getMapTransform()
   {
      AffineTransform xform = new AffineTransform();

      int mapSize = config.gameMaxX - config.gameMinX;
      
      xform.translate( this.getWidth() / 2, this.getHeight() / 2);
      xform.scale( config.mapScale, config.mapScale );         
      xform.translate( config.mapXpos - config.gameMinX - mapSize/2, config.mapYpos - config.gameMinY - mapSize / 2 );
      
      return xform;
   }
   
   /**
    * Sets the transformation (movement and scaling) for the graphics device
    * 
    * @param layer - the MapLayer to which the tranform is to be applied
    * @param g2d - the Graphics device to create the transform for
    */
   private void setupTransform(MapLayer layer, Graphics2D g2d)
   {
      AffineTransform xform;
      
      if (layer.isScaled())
      {
         xform = getMapTransform();
      }
      else
      {
         xform = new AffineTransform();
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
   public void mouseMoved(MouseEvent event)
   {
      try
      {
	      Point screenPos = event.getPoint();
	
	      Point mapPos = screenToMap(screenPos);
	      
	      notifyMapMouseMoveListeners( screenPos, mapPos );
      }
      catch (Throwable t)
      {
         // TODO: need to decide what to do with this.
         // We can't ignore it, but we don't want to pass it on to the user
         // or even log it indescriminantly because if it happens it will
         // happen repeatedly.
      }
   }

   private Point screenToMap(Point screenPos) throws NoninvertibleTransformException
   {
      // First, apply the reverse of the current transform
      
      AffineTransform xform = getMapTransform().createInverse();
            
      Point mapPos = new Point();
      xform.transform( screenPos, mapPos );
      
      // Then do the basic screen to map conversion on the detransformed co-ords
      
      mapPos = config.screenToMap( mapPos );
      
      return mapPos;
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

   /**
    * Registers an object that wants to be informed when the mouse moves over the map 
    * @param listener
    */
   public void addMapMouseMoveListener( MapMouseMoveListener listener )
   {
      mapMouseMoveListeners.add( listener );
   }

   /**
    * Notify all listeners that the mouse has been moved to the specified coordinates 
    */
   private void notifyMapMouseMoveListeners( Point screenPos, Point mapPos )
   {
      for (int n = 0; n < mapMouseMoveListeners.size(); n++)
      {
         MapMouseMoveListener listener = (MapMouseMoveListener)mapMouseMoveListeners.get(n);
         listener.mouseMovedOverMap( screenPos, mapPos );
      }
   }
}
