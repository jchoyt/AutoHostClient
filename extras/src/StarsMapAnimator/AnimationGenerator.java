
package stars.ahc.plugins.map.mapanimator;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;

/*
 * Created on Nov 12, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */

/**
 * @author Steve
 *  
 */
public class AnimationGenerator implements ControllerListener, DataSinkListener
{
   String mutex0 = new String();
   String mutex1 = new String();

   /**
    *  
    */
   public void generateAnimation( AnimationImageGenerator generator, String outputFileName ) throws AnimationGenerationError
   {
      DataSource dataSource = new AnimationImageGeneratorDataSource(generator);

      Processor proc;
      try
      {
         proc = Manager.createProcessor(dataSource);
      }
      catch (Throwable t)
      {
         throw new AnimationGenerationError( "Error creating processor", t );
      }
      
      proc.addControllerListener(this);

      proc.configure();

      waitForState(proc, Processor.Configured);

      proc.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));

      TrackControl controls[] = proc.getTrackControls();
      Format formats[] = controls[0].getSupportedFormats();
      controls[0].setFormat(formats[0]);

      proc.realize();

      waitForState(proc, Processor.Realized);

      URL outputURL;
      try
      {
         File outputFile = new File(outputFileName);
         outputURL = outputFile.toURL();
      }
      catch (MalformedURLException e1)
      {
         throw new AnimationGenerationError( "Output file name is not valid", e1 ); 
      }
      
      DataSink dataSink;
      try
      {
         dataSink = Manager.createDataSink(proc.getDataOutput(), new MediaLocator(outputURL));
         dataSink.open();
      }
      catch (Throwable t)
      {
         throw new AnimationGenerationError( "Error creating data sink", t ); 
      }
      

      dataSink.addDataSinkListener(this);

      proc.start();
      try
      {
         dataSink.start();
      }
      catch (IOException e2)
      {
         throw new AnimationGenerationError( "Error starting data sink" );
      }

      synchronized (mutex1)
      {
         try
         {
            mutex1.wait();
         }
         catch (InterruptedException e)
         {
            // this isn't an error
         }
      }

      dataSink.close();
   }

   /**
    * @param configured
    * @throws Exception
    */
   private void waitForState(Processor proc, int state) throws AnimationGenerationError
   {
      synchronized (mutex0)
      {
         while (proc.getState() != state)
         {
            try
            {
               mutex0.wait(100);
            }
            catch (InterruptedException e)
            {
               // this isn't an error
            }
         }
      }

      if (proc.getState() != state)
      {
         throw new AnimationGenerationError("Not in correct state: " + proc.getState() + " != " + state);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.media.ControllerListener#controllerUpdate(javax.media.ControllerEvent)
    */
   public void controllerUpdate(ControllerEvent event)
   {
      if (event instanceof ConfigureCompleteEvent || 
            event instanceof RealizeCompleteEvent
            || event instanceof PrefetchCompleteEvent)
      {
         synchronized (mutex0)
         {
            mutex0.notifyAll();
         }
      }
      else if (event instanceof EndOfMediaEvent)
      {
         event.getSourceController().stop();
         event.getSourceController().close();
      }

   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.media.datasink.DataSinkListener#dataSinkUpdate(javax.media.datasink.DataSinkEvent)
    */
   public void dataSinkUpdate(DataSinkEvent event)
   {
      if (event instanceof EndOfStreamEvent)
      {
         synchronized (mutex1)
         {
            mutex1.notifyAll();
         }
      }
   }
}