/*
 * Created on Nov 13, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.mapanimator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import stars.ahc.Game;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapPanel;
import stars.ahc.plugins.map.layers.PlanetLayer;
import stars.ahc.plugins.map.layers.TerritoryLayer;
import stars.ahcgui.pluginmanager.MapLayer;

/**
 * @author Steve
 *
 */
public class StarsMapAnimationImageGenerator implements AnimationImageGenerator
{
   private AnimationConfiguration configuration = new AnimationConfiguration();
   private int currentImageNumber = 0;
   public Game game = null;
   public MapConfig mapConfig = null;
   
   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.mapanimator.AnimationImageGenerator#setConfiguration(stars.ahc.plugins.map.mapanimator.AnimationConfiguration)
    */
   public void setConfiguration(AnimationConfiguration config)
   {
      this.configuration = config;
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.mapanimator.AnimationImageGenerator#getConfiguration()
    */
   public AnimationConfiguration getConfiguration()
   {
      return configuration;
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.mapanimator.AnimationImageGenerator#getNextImage()
    */
   public BufferedImage getNextImage()
   {
      BufferedImage img = new BufferedImage( configuration.width, configuration.height, BufferedImage.TYPE_INT_RGB );
      Graphics2D g = (Graphics2D)img.getGraphics();

      g.setBackground( Color.BLACK );
      g.setColor( Color.YELLOW );
      g.drawString( ""+mapConfig.year, 10, 10 );
      
      AffineTransform xform = MapPanel.getMapTransform( mapConfig, configuration.height, configuration.width );
      
      g.setTransform( xform );
      
      try
      {
         TerritoryLayer territoryLayer = new TerritoryLayer();
         territoryLayer.initialize( game, mapConfig );
         BufferedImage layerImage = territoryLayer.createLayerImage();
         if (layerImage != null)
         {
            int offset = -mapConfig.getUniverseSize()/2;
            g.drawImage( layerImage, null, offset, offset );
         }
         else
         {
            System.out.println( "Null layer image!" );
         }
         
         MapLayer planetLayer = new PlanetLayer();
         planetLayer.initialize( game, mapConfig );
         planetLayer.draw( g );
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
      
      mapConfig.year++;
      
      return img;
   }

}
