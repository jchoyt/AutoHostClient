/*
 * Created on Oct 13, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahcgui.pluginmanager;

import java.util.Properties;

/**
 * Plugins should implement this interface if they need to save/load their configuration.
 * <p>
 * Each plugin is responsible for ensuring that it saves its properties under a unique 
 * namespace.  The recommended format is:-
 * <p>
 * Plugins.tttt.nnnn.gggg.vvvv
 * <br>
 * where tttt is the plugin type, nnnn is the plugin name, gggg is the name of the game
 * and vvvv is the property key.
 * <p>
 * 
 * @author Steve Leach
 */
public interface ConfigurablePlugIn
{
   public void saveConfiguration( Properties properties );
   public void loadConfiguration( Properties properties );
}
