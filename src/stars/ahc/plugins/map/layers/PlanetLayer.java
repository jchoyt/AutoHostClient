/*
 * Created on Oct 6, 2004
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
package stars.ahc.plugins.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapDisplayError;
import stars.ahc.plugins.map.MapLayer;

/**
 * @author Steve
 *
 */
public class PlanetLayer implements MapLayer
{ 
   private ArrayList planets = new ArrayList();
   private MapConfig mapConfig = null;
      
   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#isEnabled()
    */
   public boolean isEnabled()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#getDescription()
    */
   public String getDescription()
   {
      return "Planets";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      g.setColor( Color.WHITE );
      
      for (int n = 0; n < planets.size(); n++)
      {
         Planet planet = (Planet)planets.get( n );
         
         Point screenPos = mapConfig.mapToScreen( planet.getPosition() );
         
         g.fillOval( screenPos.x, screenPos.y, 5, 5 );
      }
   }

   private void loadPlanets( Game game ) throws MapDisplayError
   {
      String mapName = game.getDirectory() + "/" + game.getName() + ".map";
      loadPlanets( mapName );
   }
   
   private void loadPlanets( String mapFileName ) throws MapDisplayError
   {
      File mapFile = new File( mapFileName );
      
      if (mapFile.exists())
      {
         loadPlanets( mapFile );
      }
      else
      {
         throw new MapDisplayError( "File not found: " + mapFileName  );
      }
   }
   
   private void loadPlanets( File mapFile ) throws MapDisplayError
   {
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
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#initialize(stars.ahc.Game, stars.ahcgui.map.MapConfig)
    */
   public void initialize(Game game, MapConfig config) throws MapDisplayError
   {
      this.mapConfig = config;
      
      loadPlanets( game );
      
      mapConfig.calcUniverseSize( planets );
   }
   
}
