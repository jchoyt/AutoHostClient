/*
 * Created on Oct 18, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.analyzer.reports;

import java.util.ArrayList;
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
public class PopulationHoldReport extends AbstractAnalyzerReport
{
   // TODO: get this from the race user-defined properties or from the report control panel 
   int maxPlanetPop = 1000000;
   
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
      reportText += "Population Hold Report \n";
      reportText += "====================== \n\n";
      
      reportText += game.getName() + ", " + game.getYear() + "\n\n";

      // Initialise the report columns infrastructure
      initColumns();
      
      // Define the report columns (title,width)
      defineColumn( "Owner", 15 );
      defineColumn( "Planet", 20 );
      defineColumn( "Value", 10, PAD_LEFT );
      defineColumn( "Curr.Pop.", 10, PAD_LEFT );
      defineColumn( "Hold %", 10, PAD_LEFT );
      defineColumn( "Hold At", 10, PAD_LEFT );
      defineColumn( "Ship", 10, PAD_LEFT );
      
      // Add the header text
      reportText += getHeaderText();
      
      // Set up an array to hold the detail lines so we can sort them later
      ArrayList reportLines = new ArrayList();
      
      int gateCount = 0;
      
      // Now loop through all the planets in the game
      for (int n = 1; n <= game.getPlanetCount(); n++)
      {
         // Get the next planet
         Planet planet = game.getPlanet( n );
         
         String holdPct = planet.getUserProperty("PopHold");
         
         if ( Utils.empty(holdPct) == false )
         {
            int holdAt = maxPlanetPop * planet.getHabValue() / 100 * Utils.safeParseInt(holdPct,0) / 100;
            int ship = planet.getPopulation() - holdAt;
            
            // Set the value of all columns to blank (as a precaution)
            clearColumnValues();
         
            // Set the value of each column
            setColumnValue( "Planet", planet.getName() );
            setColumnValue( "Owner", planet.getOwner() );
            setColumnValue( "Value", planet.getHabValue() + "%" );
            setColumnValue( "Curr.Pop.", planet.getPopulation() );
            setColumnValue( "Hold %", holdPct + "%" );            
            setColumnValue( "Hold At", holdAt );
            setColumnValue( "Ship", ship > 0 ? ""+ship : "" );
            
            
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

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Population hold report";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
   }

}
