/*
 * Created on Oct 9, 2004
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
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import stars.ahc.GameUpdateListener;
import stars.ahc.GameUpdateNotification;
import stars.ahc.Planet;
import stars.ahc.Race;
import stars.ahc.Utils;
import stars.ahc.plugins.map.AbstractCachedMapLayer;

/**
 * @author Steve
 *
 */
public class TerritoryLayer extends AbstractCachedMapLayer implements GameUpdateListener
{
   private final int DEFAULT_SIZE_MULTIPLYER = 100;
   private Box controls = null;
   private JSpinner sizeMultiplyerField;
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Territory";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Territory layer";
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.AbstractCachedMapLayer#createLayerImage()
    */
   public BufferedImage createLayerImage()
   {
      BufferedImage img = null;

      int size = mapConfig.getUniverseSize();
      
      if (size < 10) return null;			// safety check
         
      long start = System.currentTimeMillis();

      img = new BufferedImage( size, size, BufferedImage.TYPE_INT_RGB );
      
      Graphics2D g = img.createGraphics();
      
      int stepCount = 50;
      int planetCount = game.getPlanetCount();
      
      // arrays for caching values during calculation
      Point[] screenPos = new Point[planetCount+1];
      double[] rootPop = new double[planetCount+1];
      Color[] baseColor = new Color[planetCount+1];
      
      double sizeFactor = getSizeFactor();
      
      for (int step = 1; step <= stepCount; step++)
      {
         int saturation = 140 - (stepCount-step)*(100/stepCount);
         
         for (int n = 1; n <= planetCount; n++)
         {            
            Planet planet = game.getPlanet(n,mapConfig.year);
            
            if (planet.isUnoccupied() == false)
            {
               if (screenPos[n] == null)
               {
                  // first time round, cache values
                  screenPos[n] = mapConfig.mapToScreen( planet.getPosition() );
                  screenPos[n].x += mapConfig.getUniverseSize()/2;
                  screenPos[n].y += mapConfig.getUniverseSize()/2;
                  rootPop[n] = Math.sqrt( planet.getPopulation() );
                  baseColor[n] = game.getRaceColor( planet.getOwner() );
               }
                
               float r = (float)( (stepCount - step) * (200 + rootPop[n]) * sizeFactor );
               
               if (screenPos[n] == null)
               {
                  System.out.println( "Null screen position: " + n ); 
               }
               else
               {
                  Ellipse2D ellipse = new Ellipse2D.Float( screenPos[n].x-r, screenPos[n].y-r, r*2+1, r*2+1 );
                  g.setColor( Utils.adjustBrightness( baseColor[n], step*3 ) );
                  g.fill( ellipse );               
               }                  
            }
         }
      }      
      
      long elapsed = System.currentTimeMillis() - start;
      
      return img;
   }
   
   
   /**
    * @return
    */
   private double getSizeFactor()
   {
      int multiplyer = DEFAULT_SIZE_MULTIPLYER;
      
      if (controls != null)
      {
         multiplyer = ((Integer)sizeMultiplyerField.getModel().getValue()).intValue(); 
      }
            
      return 0.0025 * multiplyer / 100;
   }

   public JComponent getControls()
   {
      if (controls == null)
      {
         initControls();
      }
      
      return controls;
   }

   /**
    * 
    */
   private void initControls()
   {
      controls = Box.createVerticalBox();

      Box sizePanel = Box.createHorizontalBox();
      sizePanel.setBorder( new EtchedBorder() );
      sizePanel.add( new JLabel("Size:") );
      
      SpinnerModel sizeFieldModel = new SpinnerNumberModel(DEFAULT_SIZE_MULTIPLYER,20,400,10);
      sizeMultiplyerField = new JSpinner(sizeFieldModel);
      
      // FIXME: for some reason this isn't having any effect
      sizeMultiplyerField.setEditor( new JSpinner.NumberEditor(sizeMultiplyerField,"#0") );
      
      sizeMultiplyerField.getModel().addChangeListener( new ChangeListener() {
         public void stateChanged(ChangeEvent event)
         {
            // Tell the map config to notify all listeners that something has changed.
            // This will force a map redraw.
            mapConfig.notifyChangeListeners();
         }
      } );
      
      sizeMultiplyerField.getModel().addChangeListener( new ChangeListener() {
         public void stateChanged(ChangeEvent event)
         {
            // Tell the map config to notify all listeners that something has changed.
            // This will force a map redraw.
            invalidateCurrentCache();
            mapConfig.notifyChangeListeners();
         }
      } );
            
      sizePanel.add( sizeMultiplyerField );
      sizePanel.add( Box.createHorizontalGlue() );        
      
      controls.add( sizePanel );
      
   }

   /* (non-Javadoc)
    * @see stars.ahc.GameUpdateListener#processGameUpdate(stars.ahc.GameUpdateNotification)
    */
   public void processGameUpdate(GameUpdateNotification notification)
   {
      if ((notification.getUpdatedObject() instanceof Race) || (notification.getUpdatedObject() instanceof Planet))
      {
         System.out.println( "Game updated - invalidating cache" );
         super.invalidateCurrentCache();
      }
   }

}
