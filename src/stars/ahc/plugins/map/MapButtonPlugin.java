/*
 * Created on Nov 13, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.PlugIn;

/**
 * @author Steve
 *
 */
public interface MapButtonPlugin extends PlugIn
{
   public void initialize( MapConfig config, Game game, MapFrame mapFrame );
   public String getButtonText();
   public String getButtonToolTip();
   public void execute();
}
