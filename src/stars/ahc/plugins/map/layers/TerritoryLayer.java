/*
 * Created on Oct 9, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import stars.ahc.Planet;
import stars.ahc.plugins.map.AbstractCachedMapLayer;

/**
 * @author Steve
 *
 */
public class TerritoryLayer extends AbstractCachedMapLayer
{

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
   protected BufferedImage createLayerImage()
   {
      BufferedImage img = null;

      int size = mapConfig.getUniverseSize();
      
      if (size < 100) return null;			// safety check
         
      long start = System.currentTimeMillis();

      img = new BufferedImage( size, size, BufferedImage.TYPE_INT_RGB );
      
      Graphics2D g = img.createGraphics();
      
      int stepCount = 50;
      int planetCount = game.getPlanetCount();
      
      // arrays for caching values during calculation
      Point[] screenPos = new Point[planetCount+1];
      double[] rootPop = new double[planetCount+1];
      Color[] baseColor = new Color[planetCount+1];
      
      for (int step = 1; step <= stepCount; step++)
      {
         int saturation = 140 - (stepCount-step)*(100/stepCount);
         
         for (int n = 1; n <= planetCount; n++)
         {
            Planet planet = game.getPlanet(n);
            
            if (planet.isUnoccupied() == false)
            {
               if (step == 1)
               {
                  // first time round, cache values
                  screenPos[n] = mapConfig.mapToScreen( planet.getPosition() );
                  screenPos[n].x += mapConfig.getUniverseSize()/2;
                  screenPos[n].y += mapConfig.getUniverseSize()/2;
                  rootPop[n] = Math.sqrt( planet.getPopulation() );
                  baseColor[n] = game.getPlayerColor( planet.getOwner() );
               }
                
               float r = (float)( (stepCount - step) * (200 + rootPop[n]) * 0.0025 );
               
               Ellipse2D ellipse = new Ellipse2D.Float( screenPos[n].x-r, screenPos[n].y-r, r*2+1, r*2+1 );
                  
               g.setColor( setSaturation( baseColor[n], step+40 ) );
               g.fill( ellipse );               
            }
         }
      }      
      
      long elapsed = System.currentTimeMillis() - start;
      
      //System.out.println( "Territory drawn in " + elapsed + " millis" );
      
      return img;
   }

   /**
    */
   private Color setSaturation(Color base, int value)
   {
      int r = base.getRed();
      int b = base.getBlue();
      int g = base.getGreen();
      
      float[] vals = Color.RGBtoHSB( r, g, b, null );
      
      vals[1] = 1.0f * value / 255;
      
      Color result = Color.getHSBColor( vals[0], vals[1], vals[2] );
      
      return result;
   }

   
}
