/*
 * Created on Oct 12, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc;

/**
 * Errors relatating to the AutoHost server. 
 * 
 * @author Steve Leach
 */
public class AutoHostError extends Exception
{
   public AutoHostError( String message )
   {
      super( message );
   }

   public AutoHostError( String message, Throwable cause )
   {
      super( message, cause );
   }
}
