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
   
   public void setNewImage( BufferedImage img )
   {
      synchronized (imageCache)
      {
         imageCache.setNewImage( img );
      }
   }
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      synchronized (imageCache)
      {
         if (imageCache.currentImage == ImageCache.NO_IMAGE)
         {
            startDrawing();
         }
      }
      
      synchronized (imageCache)
      {
         if (imageCache.currentImage != ImageCache.NO_IMAGE)
         {
            BufferedImage img = imageCache.getCurrentImage(); 
            
            if (img != null)
            {
               int offset = -mapConfig.getUniverseSize()/2;
               
               g.drawImage( img, null, offset, offset );
            }
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
   
   public int currentImage = NO_IMAGE;
   public BufferedImage[] images = new BufferedImage[CACHE_SIZE];
   
   public ImageCache()
   {
      for (int n = 0; n < CACHE_SIZE; n++)
      {
         images[n] = null;
      }
   }
   
   public BufferedImage getCurrentImage()
   {
      return images[currentImage];
   }

   public void setNewImage(BufferedImage img)
   {
      if (currentImage == 0)
      {
         currentImage = 1;
      }
      else
      {
         currentImage = 0;
      }
      
      images[currentImage] = img;
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
      
      owner.setNewImage( img );
   }
}