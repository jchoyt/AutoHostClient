/*
 * Created on Oct 6, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map;

/**
 * "Wrapper" exception for errors when displaying the map
 * 
 * @author Steve Leach
 */
public class MapDisplayError extends Exception
{
   public MapDisplayError( String msg )
   {
      super( msg );
   }

   public MapDisplayError( String msg, Throwable cause )
   {
      super( msg, cause );
   }
}
