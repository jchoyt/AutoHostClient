/*
 * Created on Oct 16, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahcgui.pluginmanager;

import javax.swing.JFrame;

/**
 * @author Steve Leach
 *
 */
public interface BasePlugIn extends PlugIn
{
   public void init( JFrame mainWindow );
   public void cleanup();
}
