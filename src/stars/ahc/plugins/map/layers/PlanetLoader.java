/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.layers;

import java.io.File;

import stars.ahc.Game;
import stars.ahc.ReportLoaderException;

/**
 * @author Steve
 *
 * @deprecated - functionality moved into Game and PlanetList classes
 */
public class PlanetLoader
{
   public static void loadPlanets( Game game ) throws ReportLoaderException
   {
      String mapName = game.getDirectory() + "/" + game.getName() + ".map";
      loadPlanets( game, mapName );
   }
   
   private static void loadPlanets( Game game, String mapFileName ) throws ReportLoaderException
   {
      File mapFile = new File( mapFileName );
      
      if (mapFile.exists())
      {
         loadPlanets( game, mapFile );
      }
      else
      {
         throw new ReportLoaderException( "File not found: " + mapFileName, mapFileName  );
      }
   }
   
   private static void loadPlanets( Game game, File mapFile ) throws ReportLoaderException
   {
      game.getPlanets().loadMapFile( mapFile );
   }


}
