/*
 * Created on Oct 26, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map;

import java.awt.Point;

/**
 * Classes implement this interface and register themselves with the map panel if they
 * want to be informed when the mouse moves over the map.
 * 
 * @author Steve Leach
 */
public interface MapMouseMoveListener
{
   public void mouseMovedOverMap( Point screenPos, Point mapPos );
}
