/*
 * Created on Oct 7, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahcgui.pluginmanager;

/**
 * Root interface for all plugins
 * 
 * @author Steve Leach
 *
 */
public interface PlugIn
{
   public String getName();
   public String getDescription();
   public boolean isEnabled();
   public void setEnabled( boolean enabled );
}
