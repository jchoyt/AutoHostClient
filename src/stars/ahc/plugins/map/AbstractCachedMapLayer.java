/*
 * Created on Oct 11, 2004
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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Base class for map layers that use caching to speed redraws.
 * 
 * @author Steve Leach
 */
public abstract class AbstractCachedMapLayer extends AbstractMapLayer
{
   protected ImageCache imageCache = new ImageCache();
      
   public void setNewImage( BufferedImage img, MapLayerConfig cfg )
   {
      synchronized (imageCache)
      {
         imageCache.setNewImage( img, cfg );
      }
      
      mapConfig.notifyChangeListeners();     
   }

   /**
    * Returns the current map layer configuration.
    * <p>
    * Subclasses should override this. 
    */
   protected MapLayerConfig getLayerConfig()
   {
      return new MapLayerConfig( mapConfig.year );
   }
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      // Step 1. Find appropriate image in cache, or kick off a thread to create one
      synchronized (imageCache)
      {
         if (imageCache.findAndSelectImage( getLayerConfig()) == false)
         {
            startDrawing();
         }
      }
      
      // Step 2. Draw the image from the cache
      synchronized (imageCache)
      {
         BufferedImage img = imageCache.getCurrentImage(); 
         
         if (img != null)
         {
            int offset = -mapConfig.getUniverseSize()/2;
            
            g.drawImage( img, null, offset, offset );
         }
      }
            
   }
   
   /**
    * 
    */
   private void startDrawing()
   {
      DrawingThread t = new DrawingThread( this );
      t.start();
   }

   /**
    * Create the image that will be used for the map layer
    * <p>
    * Individual implementations will override this as appropriate. 
    */
   protected abstract BufferedImage createLayerImage();
   
   public void invalidateCurrentCache()
   {
      synchronized (imageCache)
      {
         imageCache.invalidateCurrent();
      }
   }   
}

/**
 * The image cache is where the images are stored. Access should be synchronized.
 * 
 * @author Steve Leach
 */
class ImageCache
{
   public static final int NO_IMAGE = -1;
   public static final int CACHE_SIZE = 2;
   public static final int NULL_YEAR = -9999;
   
   public int currentImage = NO_IMAGE;
   public BufferedImage[] images = new BufferedImage[CACHE_SIZE];
   public MapLayerConfig[] configs = new MapLayerConfig[CACHE_SIZE];

   public ImageCache()
   {
      for (int n = 0; n < CACHE_SIZE; n++)
      {
         images[n] = null;
         configs[n] = new MapLayerConfig(NULL_YEAR);
      }
   }
   
   /**
    * Searches the cache for an image matching the specified config.
    * <p>
    * Returns true if one is found and selected, false otherwise.
    */
   public boolean findAndSelectImage(MapLayerConfig desiredConfig)
   {
      debug( "Searching for image with config: " + desiredConfig );
      
      for (int n = 0; n < CACHE_SIZE; n++)
      {
         if (configs[n].equals(desiredConfig))
         {
            debug( "Found at index " + n );
            currentImage = n;
            return true;
         }
      }
      
      return false;
   }

   /**
    * @return
    */
   public MapLayerConfig getActiveLayerConfig()
   {
      return configs[currentImage];
   }

   /**
    * 
    */
   public void invalidateCurrent()
   {
      debug( "Invalidating current (" + configs[currentImage].toString() );
      configs[currentImage].year = NULL_YEAR;
   }

   /**
    * Returns the currently active image, or null if there is no active image. 
    */
   public BufferedImage getCurrentImage()
   {
      if (currentImage == NO_IMAGE) return null;
      return images[currentImage];
   }

   public boolean isCurrent( MapLayerConfig config )
   {
      if (currentImage == NO_IMAGE) return false;
      if (configs[currentImage] == null) return false;
      
      return config.equals( configs[currentImage] );
   }
   
   public void setNewImage(BufferedImage img, MapLayerConfig config)
   {
      if (currentImage == 0)
      {
         currentImage = 1;
      }
      else
      {
         currentImage = 0;
      }
      
      debug( "Setting current image index to " + currentImage + ", year = " + config.year );
      
      images[currentImage] = img;
      configs[currentImage] = config;
   }
   
   private void debug( String msg )
   {
      //System.out.println( msg );
   }
}

/**
 * Background thread on which to do the drawing
 * 
 * 
 * @author Steve Leach
 *
 */
class DrawingThread extends Thread
{
   private AbstractCachedMapLayer owner;
   
   public DrawingThread( AbstractCachedMapLayer owner )
   {
      this.owner = owner;
   }
   
   public void run()
   {
      BufferedImage img = owner.createLayerImage();
      
      MapLayerConfig cfg = owner.getLayerConfig();
      
      owner.setNewImage( img, cfg );
   }
   
}