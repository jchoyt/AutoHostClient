/*
 * Created on Oct 18, 2004
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.Race;
import stars.ahc.Utils;
import stars.ahc.plugins.analyzer.AbstractAnalyzerReport;
import stars.ahc.plugins.analyzer.AnalyzerReportError;
import stars.ahc.plugins.analyzer.ConfigurableReport;

/**
 * Shows how many colonists should be at each planet to keep it at it's preferred hold level.
 * <p>
 * The hold level must have been set for the planets using the Data Editor plugin.
 * 
 * @author Steve Leach
 */
public class PopulationHoldReport extends AbstractAnalyzerReport implements ConfigurableReport
{
   int maxPlanetPop = 1000000;
   private JPanel controlPanel = null;
   private Game game;
   private JTextField maxPopField;
   private JComboBox raceCombo;
   
   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.AnalyzerReport#run(stars.ahc.Game, java.util.Properties)
    */
   public String run(Game game, Properties properties) throws AnalyzerReportError
   {
      maxPlanetPop = Utils.safeParseInt( maxPopField.getText(), 1000000 );
      
      String raceName = raceCombo.getSelectedItem().toString();
      
      // Define a string to hold the text of the report
      String reportText = "";
      
      // Make sure we are reporting on the latest data
      loadReports(game);
      
      // Write the report title
      reportText += "Population Hold Report \n";
      reportText += "====================== \n\n";
      
      reportText += game.getName() + " (" + game.getYear() + "), " + raceName + "\n";      
      reportText += "Max planet population: " + maxPlanetPop + "\n\n";

      // Initialise the report columns infrastructure
      initColumns();
      
      // Define the report columns (title,width)
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
         
         String holdPctStr = planet.getUserProperty("PopHold");
         int holdPct = Utils.safeParseInt(holdPctStr,25);
         
         if ( raceName.equals( planet.getOwner()) )
         {
            int holdAt = maxPlanetPop * planet.getHabValue() / 100 * holdPct / 100;
            int ship = planet.getPopulation() - holdAt;
            
            // Set the value of all columns to blank (as a precaution)
            clearColumnValues();
         
            // Set the value of each column
            setColumnValue( "Planet", planet.getName() );
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

   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.ConfigurableReport#initControls(stars.ahc.Game)
    */
   public void initControls(Game game)
   {
      this.game = game;
      
      controlPanel = new JPanel();
      controlPanel.setBorder( new EtchedBorder() );
      
      controlPanel.setLayout( new GridBagLayout() );      
      GridBagConstraints gbc = new GridBagConstraints();
      
      gbc.gridy = 1;
      gbc.gridx = 1;
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.insets = new Insets(2,2,2,2);
      
      controlPanel.add( new JLabel("Race:"), gbc );
      
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
      
      gbc.gridx = 2;
      
      controlPanel.add( raceCombo, gbc );

      gbc.gridy ++;
      
      JLabel label = new JLabel("Max Pop:");
      gbc.gridx = 1;
      controlPanel.add( label, gbc );
      
      maxPopField = new JTextField( 10 );
      maxPopField.setText("1000000");
      gbc.gridx = 2;
      controlPanel.add( maxPopField, gbc );
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.analyzer.ConfigurableReport#getControls()
    */
   public JComponent getControls()
   {
      return controlPanel;
   }

}
