/*
 * Created on Oct 11, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map;

/**
 * Stores the configuration of a map layer
 * <p>
 * Configurations can be compared for use when determining whether a cache entry is stale.
 * Individual map layers can override this to add extra information.
 * 
 * @author Steve Leach
 */
public class MapLayerConfig
{
   public int year = 2400;
   
   public MapLayerConfig()
   {      
   }
   
   public MapLayerConfig( int year )
   {
      this.year = year;
   }
   
   public boolean equals( MapLayerConfig cfg )
   {
      return (cfg.year == this.year);
   }
   
   public String toString()
   {
      return "MapLayerConfig: " + year;
   }
}
