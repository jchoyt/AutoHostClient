/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map;

/**
 * Classes that want to be notified of changes to the map configuration should
 * implement this interface and register themselves with the map config object.
 * 
 * @author Steve Leach
 *
 */
public interface MapConfigChangeListener
{
   public void mapConfigChanged( MapConfig config );
}
