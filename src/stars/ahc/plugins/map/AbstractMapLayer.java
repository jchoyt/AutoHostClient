/*
 * Created on Oct 11, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map;

import javax.swing.JComponent;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.MapLayer;

/**
 * Template implementation of MapLayer interface.
 * <p>
 * Extend this class to get basic MapLayer functionality.
 * 
 * @author Steve Leach
 */
public abstract class AbstractMapLayer implements MapLayer
{

   protected boolean enabled = true;
   protected Game game = null;
   protected MapConfig mapConfig = null;

   public boolean isEnabled()
   {
      return enabled;
   }

   public void initialize(Game game, MapConfig config) throws MapDisplayError
   {
      this.game = game;
      this.mapConfig = config;
   }

   public boolean isScaled()
   {
      return true;
   }

   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   public JComponent getControls()
   {
      return null;
   }
}
