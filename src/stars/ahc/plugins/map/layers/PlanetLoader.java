/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.layers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.plugins.map.MapDisplayError;

/**
 * @author Steve
 *
 */
public class PlanetLoader
{
   public static ArrayList loadPlanets( Game game ) throws MapDisplayError
   {
      String mapName = game.getDirectory() + "/" + game.getName() + ".map";
      return loadPlanets( mapName );
   }
   
   private static ArrayList loadPlanets( String mapFileName ) throws MapDisplayError
   {
      File mapFile = new File( mapFileName );
      
      if (mapFile.exists())
      {
         return loadPlanets( mapFile );
      }
      else
      {
         throw new MapDisplayError( "File not found: " + mapFileName  );
      }
   }
   
   private static ArrayList loadPlanets( File mapFile ) throws MapDisplayError
   {
      ArrayList planets = new ArrayList();
      
      try
      {
         BufferedReader reader = new BufferedReader(new FileReader(mapFile));
         
         String titles = reader.readLine();
         
         String line;
         
         while ((line = reader.readLine()) != null)
         {
            String[] tokens = line.split("\t");
            
            Planet planet = new Planet();
            planet.name = tokens[3];
            planet.x = Integer.parseInt( tokens[1] );
            planet.y = Integer.parseInt( tokens[2] );
            
            planets.add( planet );
         }
         
         reader.close();
      }
      catch (FileNotFoundException e)
      {
         throw new MapDisplayError( "File not found: " + mapFile.getName() );
      }
      catch (IOException e)
      {
         throw new MapDisplayError( "Error reading file: " + mapFile.getName(), e );
      }
      
      return planets;
   }


}
