/*
 * Created on Oct 20, 2004
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import stars.ahc.Fleet;
import stars.ahc.Game;
import stars.ahc.plugins.analyzer.AbstractAnalyzerReport;
import stars.ahc.plugins.analyzer.AnalyzerReportError;

/**
 * @author Steve
 *
 */
public class PlayerShipTotals extends AbstractAnalyzerReport
{
   private static final int WARSHIP = 0;
   private static final int UTILITY = 1;
   private static final int BOMBER = 2;
   private static final int SCOUT = 3;
   private static final int UNARMED = 4;
   
   
   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.AnalyzerReport#run(stars.ahc.Game, java.util.Properties)
    */
   public String run(Game game, Properties properties) throws AnalyzerReportError
   {
      // Make sure we are reporting on the latest data
      loadReports(game);
      
      // Define a map in which to accumulate the totals
      Map totals = new HashMap();

      for (int n = 0; n < game.getFleetCount(); n++)
      {
         Fleet fleet = game.getFleet( n );
         
         String owner = fleet.getOwner();
         
         int[] playerTotals = (int[])totals.get(owner); 
         if (playerTotals == null)
         {
            playerTotals = new int[5];
            totals.put( owner, playerTotals );
         }
         
         playerTotals[WARSHIP] += fleet.getIntValue( Fleet.WARSHIP );
         playerTotals[UTILITY] += fleet.getIntValue( Fleet.UTILITY );
         playerTotals[BOMBER] += fleet.getIntValue( Fleet.BOMBER );
         playerTotals[SCOUT] += fleet.getIntValue( Fleet.SCOUT );
         playerTotals[UNARMED] += fleet.getIntValue( Fleet.UNARMED );
      }
      
      // Define a string to hold the text of the report
      String reportText = "";
      
      // Write the report title
      reportText += "Player Ship Totals \n";
      reportText += "================== \n\n";
      
      reportText += game.getName() + ", " + game.getYear() + "\n\n";

      // Initialise the report columns infrastructure
      initColumns();
      
      // Define the report columns (title,width)
      defineColumn( "Player", 20 );
      defineColumn( "Warship", 8, PAD_LEFT );
      defineColumn( "Utility", 8, PAD_LEFT );
      defineColumn( "Bomber", 8, PAD_LEFT );
      defineColumn( "Scout", 8, PAD_LEFT );
      defineColumn( "Unarmed", 8, PAD_LEFT );
      
      // Add the header text
      reportText += getHeaderText();
      
      // Set up an array to hold the detail lines so we can sort them later
      ArrayList reportLines = new ArrayList();
      
      int gateCount = 0;
      
      Iterator players = totals.keySet().iterator();
      
      while (players.hasNext())
      {
         String owner = (String)players.next();
         int[] playerTotals = (int[])totals.get(owner);
         
         setColumnValue( "Player", owner );
         setColumnValue( "Warship", playerTotals[WARSHIP] );
         setColumnValue( "Utility", playerTotals[UTILITY] );
         setColumnValue( "Bomber", playerTotals[BOMBER] );
         setColumnValue( "Scout", playerTotals[SCOUT] );
         setColumnValue( "Unarmed", playerTotals[UNARMED] );

         // Add the text of the report line to the array we made earlier
	     reportLines.add( getReportLine() );
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
      return "Player ship totals report";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
   }

}
