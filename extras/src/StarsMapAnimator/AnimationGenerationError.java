package stars.ahc.plugins.map.mapanimator;
/*
 * Created on Nov 13, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */

/**
 * @author Steve
 *
 */
public class AnimationGenerationError extends Exception
{
   public AnimationGenerationError( String message )
   {
      super( message );
   }
   
   public AnimationGenerationError( String message, Throwable cause )
   {
      super( message, cause );
   }
   
}
