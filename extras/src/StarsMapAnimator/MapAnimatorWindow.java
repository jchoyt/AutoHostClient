/*
 * Created on Nov 13, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.mapanimator;

import stars.ahc.Game;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapFrame;

/**
 * @author Steve
 *
 */
public interface MapAnimatorWindow
{
   void initialize(MapConfig mapConfig, Game game, MapFrame mapFrame);
}
