/*
 * Created on Oct 7, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahcgui.pluginmanager;

import stars.ahc.Game;

/**
 * Plugins that add a button to the game information panel
 * 
 * @author Steve Leach
 *
 */
public interface GamePanelButtonPlugin extends PlugIn
{
   public String getButtonText();
   public void init( Game game );
   public void execute() throws GamePanelButtonExecutionError;
}
