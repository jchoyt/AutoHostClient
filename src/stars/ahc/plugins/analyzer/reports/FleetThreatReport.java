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

import java.util.ArrayList;
import java.util.Properties;

import stars.ahc.Fleet;
import stars.ahc.Game;
import stars.ahc.plugins.analyzer.AbstractAnalyzerReport;
import stars.ahc.plugins.analyzer.AnalyzerReportError;

/**
 * Shows the most dangerous fleets in the game.
 * 
 * @author Steve Leach
 */
public class FleetThreatReport extends AbstractAnalyzerReport
{
   // Eventually we will support fleet threat calculator plugins so that the
   // user can decide on one of a number of different ways to calculate the
   // threat level from a fleet.
   public interface FleetThreatCalculator
   {
      public double getFleetThreatLevel( Fleet fleet, Game game );
   }

   /**
    * Default fleet threat calculator.
    * 
    * Threat = bombers + warships + (scouts / 10) 
    * 
    * @author Steve Leach
    */
   private class DefaultFleetThreatCalculator implements FleetThreatCalculator
   {
      public double getFleetThreatLevel(Fleet fleet, Game game)
      {
         return fleet.getIntValue(Fleet.WARSHIP,0) + fleet.getIntValue(Fleet.BOMBER,0) + fleet.getIntValue(Fleet.SCOUT,0) / 10.0;
      }
   }
   
   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.AnalyzerReport#run(stars.ahc.Game, java.util.Properties)
    */
   public String run(Game game, Properties properties) throws AnalyzerReportError
   {
      FleetThreatCalculator ftc = new DefaultFleetThreatCalculator();
      
      // Make sure we are reporting on the latest data
      loadReports(game);
      
      String reportText = "";
      
      // Write the report title
      reportText += "Fleet Threat Report \n";
      reportText += "=================== \n\n";
      
      reportText += game.getName() + ", " + game.getYear() + "\n\n";

      // Initialise the report columns infrastructure
      initColumns();
      
      // Define the report columns (title,width)
      defineColumn( "Threat", 8, PAD_LEFT );
      defineColumn( "Fleet", 32 );
      defineColumn( "Location", 16 );
      defineColumn( "Mass", 8, PAD_LEFT );
      defineColumn( "Bomber", 8, PAD_LEFT );
      defineColumn( "Warship", 8, PAD_LEFT );
      defineColumn( "Scout", 8, PAD_LEFT );
      defineColumn( "Utility", 8, PAD_LEFT );
      defineColumn( "Unarmed", 8, PAD_LEFT );
      
      // Add the header text
      reportText += getHeaderText();
      
      // Set up an array to hold the detail lines so we can sort them later
      ArrayList reportLines = new ArrayList();
      
      int gateCount = 0;
      
      // Now loop through all the fleets in the game
      for (int n = 0; n < game.getFleetCount(); n++)
      {
         // Get the next fleet
         Fleet fleet = game.getFleet( n );
         
         // Set the value of all columns to blank (as a precaution)
         clearColumnValues();

         double threat = ftc.getFleetThreatLevel( fleet, game );
         
         if (threat > 0.1) // a single scout has a threat of 0.1, so only count if above that
         {
          	setColumnValue( "Threat", ""+threat );
            
            setColumnValue( "Fleet", fleet.getName() );
            setColumnValue( "Location", fleet.getNiceLocation() );            
            setColumnValue( "Mass", fleet.getIntValue(Fleet.MASS,0) );
            setColumnValue( "Warship", fleet.getIntValue(Fleet.WARSHIP,0) );
            setColumnValue( "Bomber", fleet.getIntValue(Fleet.BOMBER,0) );
            setColumnValue( "Scout", fleet.getIntValue(Fleet.SCOUT,0) );
            setColumnValue( "Utility", fleet.getIntValue(Fleet.UTILITY,0) );
            setColumnValue( "Unarmed", fleet.getIntValue(Fleet.UNARMED,0) );
         
            // Add the text of the report line to the array we made earlier
            reportLines.add( getReportLine() );
         }
      }
      
      // Sort the report lines
      sortLines( reportLines, SORT_ZA );
      
      // Then collapse the lines into a single string and add to the report text
      reportText += collapse( reportLines );
      
      return reportText;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Fleet threat calculator";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
   }

}
