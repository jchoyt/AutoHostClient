/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahcgui.pluginmanager;

/**
 * Errors raised while loading plugins
 * 
 * @author Steve Leach
 */
public class PluginLoadError extends Exception
{
   public PluginLoadError( String message, Throwable cause )
   {
      super( message, cause );
   }

   public PluginLoadError( String message )
   {
      super( message );
   }
}
