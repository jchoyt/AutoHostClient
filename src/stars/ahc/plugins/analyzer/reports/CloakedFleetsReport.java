/*
 * Created on Oct 27, 2004
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
 * @author Steve Leach
 *
 */
public class CloakedFleetsReport extends AbstractAnalyzerReport
{

   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.AnalyzerReport#run(stars.ahc.Game, java.util.Properties)
    */
   public String run(Game game, Properties properties) throws AnalyzerReportError
   {
      // Make sure we are reporting on the latest data
      loadReports(game);
      
      String reportText = "";
      
      // Write the report title
      reportText += "Cloaked Fleet Report \n";
      reportText += "==================== \n\n";
      
      reportText += game.getName() + ", " + game.getYear() + "\n\n";

      // Initialise the report columns infrastructure
      initColumns();
      
      // Define the report columns (title,width)
      defineColumn( "Fleet", 32 );
      defineColumn( "Location", 16 );
      defineColumn( "Cloak", 8, PAD_LEFT );
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

         if (fleet.getIntValue(Fleet.CLOAK,0) > 0)
         {
            setColumnValue( "Fleet", fleet.getName() );
            setColumnValue( "Location", fleet.getNiceLocation() );            
            setColumnValue( "Cloak", fleet.getIntValue(Fleet.CLOAK,0) );            
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
      sortLines( reportLines, SORT_AZ );
      
      // Then collapse the lines into a single string and add to the report text
      reportText += collapse( reportLines );
      
      return reportText;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Cloaked fleet report";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
   }

}
