/*
 * Created on Oct 16, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahcgui.pluginmanager;

import javax.swing.JFrame;

/**
 * Base plugins are loaded when the application starts.
 * <p>
 * There is only one copy of each base plugin, as opposed to other plugins where there is one
 * instance per game.
 * 
 * @author Steve Leach
 */
public interface BasePlugIn extends PlugIn
{
   public void init( JFrame mainWindow );
   public void cleanup();
}
