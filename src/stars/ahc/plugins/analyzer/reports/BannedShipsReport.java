/*
 * Created on Oct 30, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.analyzer.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;

import stars.ahc.Fleet;
import stars.ahc.Game;
import stars.ahc.plugins.analyzer.AbstractAnalyzerReport;
import stars.ahc.plugins.analyzer.AnalyzerReportError;

/**
 * Report showing (as far as possible) fleets of ships from a "banned list".
 * <p>
 * This report was written to help manage the "Shielded Chaff Challenge" game where
 * all armed ships must have shields.  The host is playing Warmonger PRT observer race
 * and so can see the designs of all other player's ships.  Any design which is banned
 * can be added to the banned ships file, and this report attempts to show any fleets
 * composed of one of those designs.
 * <p>
 * Note that the report determines ship type in each fleet by using the name of the fleet
 * which for other player's ships is based on the name of the most common design in the 
 * fleet.  It is therefore possible to hide a design by including it in a fleet with  
 * another, more numerous, design.  However, it is not possible to hide ships in this
 * way for the year after they are built, assuming the observer can scan the orbit of the 
 * planet that the ships are built at.  
 * 
 * @author Steve Leach
 */
public class BannedShipsReport extends AbstractAnalyzerReport
{
   String[] bannedShipNames = new String[0];
   
   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.AnalyzerReport#run(stars.ahc.Game, java.util.Properties)
    */
   public String run(Game game, Properties properties) throws AnalyzerReportError
   {
      // Make sure we are reporting on the latest data
      loadReports(game);
      
      String bannedShipsFileName = game.getDirectory() + File.separator + "bannedships.txt"; 

      loadBannedShipsList( bannedShipsFileName );
      
      String reportText = "";
      
      // Write the report title
      reportText += "Cloaked Fleet Report \n";
      reportText += "==================== \n\n";
      
      reportText += game.getName() + ", " + game.getYear() + "\n\n";

      reportText += "Banned ships file: " + bannedShipsFileName + "\n\n";
      
      // Initialise the report columns infrastructure
      initColumns();
      
      // Define the report columns (title,width)
      defineColumn( "Fleet", 32 );
      defineColumn( "Location", 16 );
      defineColumn( "Scout", 8, PAD_LEFT );
      defineColumn( "Warship", 8, PAD_LEFT );
      defineColumn( "Bomber", 8, PAD_LEFT );
      defineColumn( "Utility", 8, PAD_LEFT );
      defineColumn( "Unarmed", 8, PAD_LEFT );
      
      // Add the header text
      reportText += getHeaderText();
      
      // Set up an array to hold the detail lines so we can sort them later
      ArrayList reportLines = new ArrayList();
      
      // Now loop through all the fleets in the game
      for (int n = 0; n < game.getFleetCount(); n++)
      {
         // Get the next fleet
         Fleet fleet = game.getFleet( n );
         
         // Set the value of all columns to blank (as a precaution)
         clearColumnValues();

         if (bannedFleet(fleet))
         {
            setColumnValue( "Fleet", fleet.getName() );
            setColumnValue( "Location", fleet.getNiceLocation() );            
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

   /**
    */
   private boolean bannedFleet(Fleet fleet)
   {
      String name = fleet.getName();
      
      for (int n = 0; n < bannedShipNames.length; n++)
      {
         if (name.indexOf( bannedShipNames[n] ) >= 0)
         {
            return true;
         }
      }
      
      return false;
   }

   /**
    * 
    */
   private void loadBannedShipsList( String fileName )
   {
      ArrayList bannedShipsList = new ArrayList();
      
      File bannedShipsFile = new File(fileName);
      
      try
      {
	      if (bannedShipsFile.exists())
	      {
	         BufferedReader reader = new BufferedReader(new FileReader(bannedShipsFile));
	         
	         String line;
	         
	         while ((line = reader.readLine()) != null)
	         {
	            bannedShipsList.add( line.trim() );
	            System.out.println( line.trim() + " is banned" );
	         }
	         
	         reader.close();
	      }
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
      
      bannedShipNames = (String[])bannedShipsList.toArray(new String[0]);
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Banned ships report";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
   }

}
