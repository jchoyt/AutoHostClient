/*
 * Created on Oct 15, 2004
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
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.Race;
import stars.ahc.Utils;
import stars.ahc.plugins.analyzer.AbstractAnalyzerReport;
import stars.ahc.plugins.analyzer.AnalyzerReportError;
import stars.ahc.plugins.analyzer.ConfigurableReport;

/**
 * A report showing the planets that have the highest overall mineral levels.
 * 
 * @author Steve Leach
 */
public class MiningCapacityReport extends AbstractAnalyzerReport implements ConfigurableReport
{
   private JPanel controls = null;
   private Game game;
   private JComboBox raceCombo;
   
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
      reportText += "Mining Capacity Report \n";
      reportText += "====================== \n\n";
      
      String raceName = raceCombo.getSelectedItem().toString();
      
      reportText += game.getName() + ", " + raceName + ", " + game.getYear() + "\n\n";

      // Initialise the report columns infrastructure
      initColumns();
      
      // Define the report columns (title,width)
      defineColumn( "Min", 4, PAD_LEFT );
      defineColumn( "Planet", 20 );
      defineColumn( "Iro", 4, PAD_LEFT );
      defineColumn( "Bor", 4, PAD_LEFT );
      defineColumn( "Ger", 4, PAD_LEFT );
      
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
         
         if ( planet.isOccupied() && (planet.getOwner().equals(raceName)) )
         {
            gateCount++;
            
            // Set the value of all columns to blank (as a precaution)
            clearColumnValues();

            int min = Integer.MAX_VALUE;
            
            min = Math.min( min, planet.getIroConc() );
            min = Math.min( min, planet.getBorConc() );
            min = Math.min( min, planet.getGerConc() );
            
            if (min > 20)
            {
	            // Set the value of each column
               	setColumnValue( "Min", min );
               
	            setColumnValue( "Planet", planet.getName() );
	            
	            setColumnValue( "Iro", planet.getIroConc() );
	            setColumnValue( "Bor", planet.getBorConc() );
	            setColumnValue( "Ger", planet.getGerConc() );
            
	            // Add the text of the report line to the array we made earlier
	            reportLines.add( getReportLine() );
            }
         }
      }
      
      // Sort the report lines
      sortLines( reportLines, SORT_ZA );
      
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
      return "Mining capacity report";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.ConfigurableReport#getControls()
    */
   public JComponent getControls()
   {
      return controls;
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.ConfigurableReport#initControls(stars.ahc.Game)
    */
   public void initControls(Game game)
   {
      this.game = game;
      
      if (controls == null)
      {
         controls = new JPanel();
         controls.setBorder( new EtchedBorder() );
         
         controls.add( new JLabel("Player:") );
         
         Vector raceNames = new Vector();
         
         for (Iterator raceList = game.getRaces(); raceList.hasNext();)
         {
            Race race = (Race)raceList.next();
            
            if (Utils.empty(race.getRaceName()) == false)
            {
               raceNames.add( race.getRaceName() );
            }
         }
         
         raceCombo = new JComboBox( raceNames );
         
         controls.add( raceCombo );
      }
      
      
   }

}
