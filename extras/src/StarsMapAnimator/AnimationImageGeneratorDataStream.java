
package stars.ahc.plugins.map.mapanimator;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/*
 * Created on Nov 12, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */

/**
 * @author Steve
 *  
 */
public class AnimationImageGeneratorDataStream implements PullBufferStream
{
   private int           height             = 0;
   private int           width              = 0;
   private boolean       finished           = false;
   private Format        format             = null;
   private BufferedImage currentImage;
   private int           currentFrame;
   private int           framesPerImage     = 1;
   private int           currentImageNumber = 0;
   private byte[]		currentData = null;
   private int			currentDataSize = 0;
   private AnimationImageGenerator generator = null;

   public AnimationImageGeneratorDataStream(AnimationImageGenerator generator)
   {
      this.height = generator.getConfiguration().height;
      this.width = generator.getConfiguration().width;
      this.generator = generator;

      format = new VideoFormat(VideoFormat.JPEG, 
            new Dimension(width, height), 
            Format.NOT_SPECIFIED, Format.byteArray, generator.getConfiguration().frameRate);

      currentFrame = 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.media.protocol.PullBufferStream#willReadBlock()
    */
   public boolean willReadBlock()
   {
      return false;
   }

   /**
    * This is called from the Processor to read a frame worth of video data.
    */
   public void read(Buffer buf) throws IOException
   {
      if (finished)
      {
         buf.setEOM(true);
         buf.setOffset(0);
         buf.setLength(0);
      }
      else
      {
         System.out.println( "Frame: " + currentFrame + " of " + generator.getConfiguration().lastFrame );
         if ((currentFrame == 0) || (currentFrame % framesPerImage == 0))
         {
            generateAndLoadNextImage();
         }
         
         buf.setOffset(0);
         buf.setLength( currentDataSize );
         buf.setFormat( format );
         buf.setData( currentData );
         buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
         
         currentFrame++;
         if (currentFrame >= generator.getConfiguration().lastFrame)
         {
            System.out.println( "Finished" );
            finished = true;
         }
      }
   }
   
   private void generateAndLoadNextImage() throws ImageFormatException, IOException
   {
      BufferedImage img = generator.getNextImage(); 
      
      ByteArrayOutputStream s = new ByteArrayOutputStream( height * width * 4 * 2 );
      
      float quality = 0.75f;
      
      // Setup a jpeg encoder, outputting to the file
      JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(s);          
      JPEGEncodeParam params = jpeg.getDefaultJPEGEncodeParam(img);
      params.setQuality(quality,false);		
      jpeg.setJPEGEncodeParam(params);
      
      // Encode the jpeg image to the file
      jpeg.encode(img);
      
      currentDataSize = s.size();
      currentData = s.toByteArray();
   }
   


   /*
    * (non-Javadoc)
    * 
    * @see javax.media.protocol.PullBufferStream#getFormat()
    */
   public Format getFormat()
   {
      return format;
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.media.protocol.SourceStream#getContentDescriptor()
    */
   public ContentDescriptor getContentDescriptor()
   {
      return new ContentDescriptor(ContentDescriptor.RAW);
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.media.protocol.SourceStream#getContentLength()
    */
   public long getContentLength()
   {
      return 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.media.protocol.SourceStream#endOfStream()
    */
   public boolean endOfStream()
   {
      return finished;
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.media.Controls#getControls()
    */
   public Object[] getControls()
   {
      return new Object[0];
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.media.Controls#getControl(java.lang.String)
    */
   public Object getControl(String arg0)
   {
      return null;
   }

}