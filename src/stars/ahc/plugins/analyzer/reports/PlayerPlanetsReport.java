/*
 * Created on Oct 26, 2004
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
package stars.ahc.plugins.analyzer.reports;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.Utils;
import stars.ahc.plugins.analyzer.AbstractAnalyzerReport;
import stars.ahc.plugins.analyzer.AnalyzerReportError;

/**
 * @author Steve Leach
 *
 */
public class PlayerPlanetsReport extends AbstractAnalyzerReport
{
   private class PlayerStats
   {
      int planets = 0;
      int population = 0;
      int bases = 0;
      int totalAge = 0;
   }
   
   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.AnalyzerReport#run(stars.ahc.Game, java.util.Properties)
    */
   public String run(Game game, Properties properties) throws AnalyzerReportError
   {
      // Make sure we are reporting on the latest data
      loadReports(game);

      String reportText = "";
      
      Map allStats = new HashMap();
      
      for (int n = 0; n < game.getPlanetCount(); n++)
      {
         Planet planet = game.getPlanet(n);
         
         if (planet.isOccupied())
         {
            String owner = planet.getOwner();
            
            PlayerStats stats = (PlayerStats)allStats.get(owner);
            
            if (stats == null)
            {
               stats = new PlayerStats();
               allStats.put( owner, stats );
            }
            
            stats.planets += 1;
            stats.population += planet.getPopulation();
            stats.totalAge += planet.getReportAge();
            
            if (Utils.empty(planet.getStarBase()) == false)
            {
               stats.bases += 1;
            }
         }
      }
      
      // Write the report title
      reportText += "Player Planet Totals \n";
      reportText += "==================== \n\n";
      
      reportText += game.getName() + ", " + game.getYear() + "\n\n";

      // Initialise the report columns infrastructure
      initColumns();
      
      // Define the report columns (title,width)
      defineColumn( "Player", 20 );
      defineColumn( "Planets", 10, PAD_LEFT );
      defineColumn( "Population", 10, PAD_LEFT );
      defineColumn( "Bases", 10, PAD_LEFT );
      defineColumn( "Age", 5, PAD_LEFT );
      
      // Add the header text
      reportText += getHeaderText();
      
      // Set up an array to hold the detail lines so we can sort them later
      ArrayList reportLines = new ArrayList();
      
      NumberFormat ageFormat = new DecimalFormat( "0.0" );
      
      Iterator races = allStats.keySet().iterator();
      while (races.hasNext())
      {
         String raceName = (String)races.next();
         PlayerStats stats = (PlayerStats)allStats.get(raceName);
         
         setColumnValue( "Player", raceName );
         setColumnValue( "Planets", stats.planets );
         setColumnValue( "Population", stats.population );
         setColumnValue( "Bases", stats.bases );
         
         float age = 1.0f * stats.totalAge / stats.planets;
         setColumnValue( "Age", ageFormat.format( age ) );
         
         // Add the text of the report line to the array we made earlier
	     reportLines.add( getReportLine() );
      }
      
      // Sort the report lines
      sortLines( reportLines );
      
      // Then collapse the lines into a single string and add to the report text
      reportText += collapse( reportLines );
      
      reportText += "\n\n" 
         		 + "Notes:-\n"
         		 + "* Results based on scanning only\n"
         		 + "* Bases includes orbital forts\n"
         		 + "* Age shows the average age of reports for this player's planets\n";
      
      return reportText;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Player planets report";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
   }

}
