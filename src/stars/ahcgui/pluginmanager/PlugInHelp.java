/*
 * Created on Oct 15, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahcgui.pluginmanager;

import java.util.Locale;

/**
 * Interface implemented by PlugIns that provide help text for the user.
 * 
 * @author Steve Leach
 */
public interface PlugInHelp
{
   public String getHelpText();
   public String getHelpText( Locale locale );
   public String getHelpText( String key );
   public String getHelpText( String key, Locale locale );
}
