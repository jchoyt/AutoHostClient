/*
 * Created on Oct 9, 2004
 *
 * Copyright (c) 2004, Steve Leach
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
package stars.ahc;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Maintains a list of planets, optimized for both random access by planet name
 * and for sequential scan.
 * 
 * @author Steve Leach
 */
public class PlanetList
{
   private Game game;
   
   private int planetCount = 0;
   
   private Map reportsLoaded = new HashMap();
   
   // List of planet names, fast sequential scan
   private String[] planetNames = new String[1000];
   
   // List of planet positions, one per planet, fast retrieval by name
   private Map planetPositions = new HashMap();
   
   // All planet data, one entry per planet per year, fast retrieval by name and year
   private Map planetData = new HashMap();

   public PlanetList( Game game )
   {
      this.game = game;      
   }
   
   public Planet getPlanet( String planetName, int year )
   {
      String hashValue = planetDataHashValue( planetName, year );
      PlanetData data = (PlanetData)planetData.get( hashValue );
      Point position = (Point)planetPositions.get( planetName );
      
      return new Planet(planetName,year,position,data,game);
   }
   
   /**
    * Returns the latest planet data up to the specified year 
    */
   private PlanetData getLatestData( String planetName, int year )
   {
      for (int n = year; n >= 2400; n--)
      {
         String hashValue = planetDataHashValue( planetName, n );
         PlanetData data = (PlanetData)planetData.get( hashValue );

         if (data != null)
         {
            return data;
         }
      }
      return null;
   }
   
   /**
    * 
    * @param index (starts at 1)
    * @param year
    * @return
    */
   public Planet getPlanet( int index, int year )
   {
      String planetName = planetNames[index];
//      String hashValue = planetDataHashValue( planetName, year );
//      PlanetData data = (PlanetData)planetData.get( hashValue );
      PlanetData data = getLatestData( planetName, year );
      Point position = (Point)planetPositions.get( planetName );
      
      return new Planet(planetName,year,position,data,game);       
   }

   private String planetDataHashValue( String planetName, int year )
   {
      return planetName + " - " + year;
   }

   /**
    */
   public int getPlanetCount()
   {
      return planetCount;
   }

   /**
    */
   public void addPlanetData(String name, int year, String[] values)
   {      
      PlanetData data = new PlanetData();
      data.year = year;
      data.name = name;
      data.values = values;
      
      String hashString = planetDataHashValue( name, year );
      
      if (planetData.get( hashString ) == null)
      {
         planetData.put( hashString, data );
      }
   }

   /**
    */
   public void setPlanetPosition(String name, int x, int y, int id)
   {
      planetNames[id] = name;
      planetCount = Math.max( planetCount, id );
      
      planetPositions.put( name, new Point(x,y) );       
   }
   
   public void loadMapFile( File mapFile ) throws ReportLoaderException
   {
      try
      {
         BufferedReader reader = new BufferedReader(new FileReader(mapFile));
         
         String titles = reader.readLine();
         
         String line;
         
         while ((line = reader.readLine()) != null)
         {
            String[] tokens = line.split("\t");
            
            String name = tokens[3];
            int x = Integer.parseInt( tokens[1] );
            int y = Integer.parseInt( tokens[2] );
            int id = Integer.parseInt( tokens[0] );
            
            setPlanetPosition( name, x, y, id );
         }
         
         reader.close();
      }
      catch (FileNotFoundException e)
      {
         throw new ReportLoaderException( "File not found: " + mapFile.getName(), e, mapFile.getName() );
      }
      catch (IOException e)
      {
         throw new ReportLoaderException( "Error reading file: " + mapFile.getName(), e, mapFile.getName() );
      }      
   }

   /**
    * @param reportFile
    * @throws ReportLoaderException
    */
   public void loadPlanetReport(File reportFile, int year) throws ReportLoaderException
   {
      if (reportsLoaded.get( reportFile.getName()) != null)
      {
         // Have already loaded this report so don't do so again
         return;
      }
      
      try
      {
         BufferedReader reader = new BufferedReader(new FileReader(reportFile));
         
         String titles = reader.readLine();
         
         String line;
         
         while ((line = reader.readLine()) != null)
         {
            String[] tokens = line.split("\t");

            String name = tokens[0];
            
            addPlanetData( name, year, tokens );
         }
         
         reader.close();
         
         reportsLoaded.put( reportFile.getName(), reportFile.getName() );
      }
      catch (FileNotFoundException e)
      {
         throw new ReportLoaderException( "File not found: " + reportFile.getName(), e, reportFile.getName() );
      }
      catch (IOException e)
      {
         throw new ReportLoaderException( "Error reading file: " + reportFile.getName(), e, reportFile.getName() );
      }      
   }

   /**
    * Finds the closes planet to the given position.
    * <p>
    * No planet over the specified threshold distance will be considered. If 
    * threshold is 0 or less then a default of 10ly will be used. 
    */
   public Planet findClosestPlanet(Point mapPos, int threshold)
   {
      if (threshold <= 0)
      {
         threshold = 10;
      }
      
      String matchingName = null;
      Planet result = null;
      
      Iterator positions = planetPositions.keySet().iterator();

      double min = Double.MAX_VALUE;     

      while (positions.hasNext())
      {
         String name = (String)positions.next();
         
         Point planetPos = (Point)planetPositions.get(name);
         
         int dx = Math.abs(planetPos.x-mapPos.x);
         int dy = Math.abs(planetPos.y-mapPos.y);
         
         if ((dx <= threshold) && (dy <= threshold))
         {
            double dist = Math.sqrt( dx*dx + dy*dy );
            
            if (dist < threshold)
            {
               if (dist < min)
               {
                  min = dist;
                  matchingName = name;
               }
            }
         }
      }
      
      if (matchingName != null)
      {
         result = getPlanet( matchingName, game.getYear() );
      }
      
      return result;
   }
   
}

