package stars.ahc.plugins.map.mapanimator;
import java.io.IOException;

import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

/*
 * Created on Nov 12, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */

/**
 * @author Steve Leach
 *
 */
public class AnimationImageGeneratorDataSource extends PullBufferDataSource
{
   private PullBufferStream[] streams = new PullBufferStream[1];

   public AnimationImageGeneratorDataSource( AnimationImageGenerator generator )
   {
     streams[0] = new AnimationImageGeneratorDataStream(generator);
   }
   
   /* (non-Javadoc)
    * @see javax.media.protocol.DataSource#getContentType()
    */
   public String getContentType()
   {
      return ContentDescriptor.RAW;
   }

   /* (non-Javadoc)
    * @see javax.media.protocol.DataSource#connect()
    */
   public void connect() throws IOException
   {
   }

   /* (non-Javadoc)
    * @see javax.media.protocol.DataSource#disconnect()
    */
   public void disconnect()
   {
   }

   /* (non-Javadoc)
    * @see javax.media.protocol.DataSource#start()
    */
   public void start() throws IOException
   {
   }

   /* (non-Javadoc)
    * @see javax.media.protocol.DataSource#stop()
    */
   public void stop() throws IOException
   {
   }

   /* (non-Javadoc)
    * @see javax.media.protocol.DataSource#getControl(java.lang.String)
    */
   public Object getControl(String arg0)
   {
      return null;
   }

   /* (non-Javadoc)
    * @see javax.media.protocol.DataSource#getControls()
    */
   public Object[] getControls()
   {
      return new Object[0];
   }

   /* (non-Javadoc)
    * @see javax.media.protocol.DataSource#getDuration()
    */
   public Time getDuration()
   {
      return DURATION_UNKNOWN;
   }

   /* (non-Javadoc)
    * @see javax.media.protocol.PullBufferDataSource#getStreams()
    */
   public PullBufferStream[] getStreams()
   {
      return streams;
   }

}
