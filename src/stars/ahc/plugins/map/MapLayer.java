/*
 * Created on Oct 6, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map;

import java.awt.Graphics2D;

import stars.ahc.Game;

/**
 * All map layers must implement this interface
 * 
 * @author Steve Leach
 */
public interface MapLayer
{
   public boolean isEnabled();
   public String getDescription();
   
   public void initialize( Game game, MapConfig config ) throws MapDisplayError;

   public void draw( Graphics2D g );
}
