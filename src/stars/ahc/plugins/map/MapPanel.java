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

import javax.swing.JPanel;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.MapLayer;
import stars.ahcgui.pluginmanager.PlugInManager;

/**
 * @author Steve Leach
 *
 */
public class MapPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
   private Game game = null;
   private MapConfig config = null;
   private boolean dragging = false;
   private Point prevMousePos = null;
   private ArrayList layers = new ArrayList();
   
   public MapPanel( Game game, MapConfig config ) throws MapDisplayError
   {
      this.game = game;
      this.config = config;
      
      initializeLayers();
      
      addMouseListener( this );
      addMouseMotionListener( this );
      addMouseWheelListener( this );
   }

   /**
    * 
    */
   private void initializeLayers() throws MapDisplayError
   {
      ArrayList plugins = PlugInManager.getPluginManager().getPlugins( MapLayer.class );
      
      for (int n = 0; n < plugins.size(); n++)
      {
         try
         {
	         Class plugin = (Class)plugins.get(n);
	         MapLayer layer = (MapLayer)plugin.newInstance();
	         
	         layer.initialize( game, config );
	         
	         layers.add( layer );
         }
         catch (Exception e)
         {
            throw new MapDisplayError( "Error creating layer", e  );
         }
      }
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
    * @param layer
    * @param g2d
    */
   private void setupTransform(MapLayer layer, Graphics2D g2d)
   {
      AffineTransform xform = new AffineTransform();
      
      if (layer.isScaled())
      {
         xform.translate( this.getWidth() / 2, this.getHeight() / 2 );
         xform.scale( config.mapScale, config.mapScale );
         xform.translate( config.mapXpos - config.gameMinX, config.mapYpos - config.gameMinY );
      }

      g2d.setTransform( xform );      
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
    */
   public void mouseClicked(MouseEvent arg0)
   {
      // TODO Auto-generated method stub
      
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
    */
   public void mouseEntered(MouseEvent arg0)
   {
      // TODO Auto-generated method stub
      
   }

   /* (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
    */
   public void mouseExited(MouseEvent arg0)
   {
      // TODO Auto-generated method stub
      
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

}
