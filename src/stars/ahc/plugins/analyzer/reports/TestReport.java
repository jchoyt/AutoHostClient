/*
 * Created on Oct 14, 2004
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
*/
package stars.ahc.plugins.analyzer.reports;

import java.util.ArrayList;
import java.util.Properties;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.ReportLoaderException;
import stars.ahc.plugins.analyzer.AbstractAnalyzerReport;
import stars.ahc.plugins.analyzer.AnalyzerReportError;

/**
 * @author Steve Leach
 *
 */
public class TestReport extends AbstractAnalyzerReport
{

   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.AnalyzerReport#run(stars.ahc.Game, java.util.Properties)
    */
   public String run(Game game, Properties properties) throws AnalyzerReportError
   {
      // Define a string to hold the text of the report
      String reportText = "";
      
      // Make sure we are reporting on the latest data
      loadReports(game);
      
      // Write the report title
      reportText += "Test Report \n";
      reportText += "=========== \n\n";

      // Initialise the report columns infrastructure
      initColumns();
      
      // Define the report columns (title,width)
      defineColumn( "Planet", 20 );
      defineColumn( "Owner", 15 );
      defineColumn( "Population", 10, PAD_LEFT );
      
      // Add the header text
      reportText += getHeaderText();
      
      // Set up an array to hold the detail lines so we can sort them later
      ArrayList reportLines = new ArrayList();
      
      // Now loop through all the planets in the game
      for (int n = 1; n <= game.getPlanetCount(); n++)
      {
         // Get the next planet
         Planet planet = game.getPlanet( n );
         
         // Is it occupied ?  This is a very basic filter
         if (planet.isUnoccupied() == false)
         {
            // Set the value of all columns to blank (as a precaution)
            clearColumnValues();
         
            // Set the value of each column
            setColumnValue( "Planet", planet.getName() );
            setColumnValue( "Owner", planet.getOwner() );
            setColumnValue( "Population", planet.getPopulation() );
         
            // Add the text of the report line to the array we made earlier
            reportLines.add( getReportLine() );            
         }
      }
      
      // Sort the report lines
      sortLines( reportLines );
      
      // Then collapse the lines into a single string and add to the report text
      reportText += collapse( reportLines );
         
      // And return the report text to the analyzer for displaying to the user
      return reportText;
   }

   /**
    * @param game
    * @throws AnalyzerReportError
    */
   private void loadReports(Game game) throws AnalyzerReportError
   {
      try
      {
         game.loadReports();
      }
      catch (ReportLoaderException e)
      {
         throw new AnalyzerReportError( "Error loading game data", e );
      }
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Test report";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Test report";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
   }

}
