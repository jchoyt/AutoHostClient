/*
 * Created on Nov 11, 2004
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
package stars.ahc.plugins.utilities.racedesign;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import stars.ahc.Planet;
import stars.ahc.PlanetData;
import stars.ahc.Race;
import stars.ahc.Utils;
import stars.ahc.plugins.base.HabCalculator;
import stars.ahcgui.HabEditor;
import stars.ahcgui.pluginmanager.GlobalUtilityPlugin;

/**
 * A set of tools to assist in race design
 * 
 * @author Steve Leach
 */
public class RaceDesignAssistant implements GlobalUtilityPlugin
{
   private JComponent panel = null;
   private Properties properties;
   private RaceConfigEditor editPanel1;
   private RaceConfigEditor editPanel2; 
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.GlobalUtilityPlugin#getComponent()
    */
   public JComponent getComponent()
   {
      if (panel == null)
      {
         panel = new JPanel( new BorderLayout() );

         JPanel outerPanel = new JPanel();
         
         Box toolbar = Box.createHorizontalBox();
         
         JButton saveButton = new JButton("Save");
         saveButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
               saveConfig();
            }
         });
         toolbar.add( saveButton );
         
         JButton loadButton = new JButton("Load");
         loadButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
               loadConfig();
            }
         });
         toolbar.add( loadButton );
         
         
         toolbar.add( Box.createHorizontalGlue() );
         
         panel.add( toolbar, BorderLayout.NORTH );
         
         JPanel listPanel = createListPanel();
         
         properties = new Properties();
         
         Box editPanels = Box.createVerticalBox();
         editPanel1 = new RaceConfigEditor("Design 1",properties);
         editPanel2 = new RaceConfigEditor("Design 2",properties);
         editPanels.add( editPanel1 );
         editPanels.add( editPanel2 );
         editPanels.add( Box.createVerticalGlue() );
         
         JSplitPane mainSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, listPanel, editPanels );
         
         outerPanel.add( mainSplit );
         
         panel.add( outerPanel, BorderLayout.CENTER );
      }
      
      return panel;
   }

   /**
    */
   private JPanel createListPanel()
   {
      JPanel listPanel = new JPanel();
      listPanel.add( new JLabel("List goes here") );
      return listPanel;
   }

   private void saveConfig()
   {
      editPanel1.getFieldValues();
      editPanel2.getFieldValues();
      
      try
      {
         FileOutputStream stream = new FileOutputStream( "racedesign.props" );
         properties.store( stream, "Race design assistant properties" );
         stream.close();
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      
   }
   
   private void loadConfig()
   {
      try
      {
         FileInputStream stream = new FileInputStream( "racedesign.props" );
         properties.load( stream );
         
         editPanel1.refreshFields();
         editPanel2.refreshFields();
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
   }
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Race Design Assistant";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
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
      // not implemented
   }

}

class RaceConfig
{
   public Race race;
   
   public RaceConfig( String designName, Properties props )
   {
      race = new Race( props );
      race.setRaceName( designName );
   }
   
   public void initHabs()
   {
      race.setGravRange( 0.22, 4.40, false );
      race.setTempRange( -140, 140, false );
      race.setRadRange( 15, 85, false );
   }
}

class RaceConfigEditor extends JPanel
{
   private RaceConfig raceConfig;
   private HabEditor gravEditor;
   private HabEditor tempEditor;
   private HabEditor radEditor;
   private JTextArea habDetails;
   private JCheckBox obrmField;
   private JCheckBox joatField;
   private JComboBox gravTerraLevel;
   private JComboBox tempTerraLevel;
   private JComboBox radTerraLevel;
   private JTextField testSizeField;
   
   public RaceConfigEditor( String designName, Properties props )
   {
      raceConfig = new RaceConfig( designName, props );
      raceConfig.initHabs();
      initPanel();
      initControls();
      refreshFields();
   }

   private void initPanel()
   {
      setBorder( BorderFactory.createEtchedBorder() );
      setLayout( new BoxLayout(this,BoxLayout.Y_AXIS) );
   }

   private void initControls()
   {
      gravEditor = new HabEditor();
      tempEditor = new HabEditor();
      radEditor = new HabEditor();
      
      JPanel habRangesPanel = new JPanel(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      
      Vector terraLevels = new Vector();
      terraLevels.add( "0" );
      terraLevels.add( "3" );
      terraLevels.add( "5" );
      terraLevels.add( "7" );
      terraLevels.add( "11" );
      terraLevels.add( "15" );
      terraLevels.add( "30" );
      
      gbc.gridx = 1;
      gbc.gridy = 1;
      habRangesPanel.add( new JLabel("Grav: "), gbc );
      gbc.gridx++;
      habRangesPanel.add( gravEditor, gbc );
      gbc.gridx++;
      habRangesPanel.add( new JLabel("Terraform: "), gbc );
      gbc.gridx++;
      gravTerraLevel = new JComboBox(terraLevels);
      habRangesPanel.add( gravTerraLevel, gbc );
      
      gbc.gridx = 1;
      gbc.gridy++;
      habRangesPanel.add( new JLabel("Temp: "), gbc );
      gbc.gridx++;
      habRangesPanel.add( tempEditor, gbc );     
      gbc.gridx++;
      habRangesPanel.add( new JLabel("Terraform: "), gbc );
      gbc.gridx++;
      tempTerraLevel = new JComboBox(terraLevels);
      habRangesPanel.add( tempTerraLevel, gbc );
      
      gbc.gridx = 1;
      gbc.gridy++;
      habRangesPanel.add( new JLabel("Rad: "), gbc );
      gbc.gridx++;
      habRangesPanel.add( radEditor, gbc );     
      gbc.gridx++;
      habRangesPanel.add( new JLabel("Terraform: "), gbc );
      gbc.gridx++;
      radTerraLevel = new JComboBox(terraLevels);
      habRangesPanel.add( radTerraLevel, gbc );
      
      add( habRangesPanel );
      
      JPanel panel2 = new JPanel();
      obrmField = new JCheckBox( "OBRM" );
      joatField = new JCheckBox( "JOAT" );
      panel2.add( obrmField );
      panel2.add( joatField );
      add( panel2 );
      
      JPanel calcPanel = new JPanel();
      
      JButton calcHabButton = new JButton( "Calculate habs" );
      calcHabButton.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent arg0)
         {
            getFieldValues();
            calcHabs();
         }
      });
      calcPanel.add( calcHabButton );
      
      testSizeField = new JTextField(6);
      testSizeField.setText( "1000" );
      calcPanel.add( testSizeField );
      
      add( calcPanel );
      
      habDetails = new JTextArea(5,40);
      add( habDetails );
   }
   
   public void getFieldValues()
   {
      double gravMin = Utils.safeParseFloat( gravEditor.getMinText(), raceConfig.race.getGravMin() );
      double gravMax = Utils.safeParseFloat( gravEditor.getMaxText(), raceConfig.race.getGravMax() );
      boolean gravImmune = gravEditor.getImmune();
      raceConfig.race.setGravRange( gravMin, gravMax, gravImmune );
      
      int tempMin = Utils.safeParseInt( tempEditor.getMinText(), raceConfig.race.getTempMin() );
      int tempMax = Utils.safeParseInt( tempEditor.getMaxText(), raceConfig.race.getTempMax() );
      boolean tempImmune = tempEditor.getImmune();
      raceConfig.race.setTempRange( tempMin, tempMax, tempImmune );
      
      int radMin = Utils.safeParseInt( radEditor.getMinText(), raceConfig.race.getRadMin() );
      int radMax = Utils.safeParseInt( radEditor.getMaxText(), raceConfig.race.getRadMax() );
      boolean radImmune = radEditor.getImmune();
      raceConfig.race.setRadRange( radMin, radMax, radImmune );
   }
   
   public void refreshFields()
   {
      gravEditor.setMinText( ""+raceConfig.race.getGravMin() );
      gravEditor.setMaxText( ""+raceConfig.race.getGravMax() );
      gravEditor.setImmune( raceConfig.race.gravImmune() );
      tempEditor.setMinText( ""+raceConfig.race.getTempMin() );
      tempEditor.setMaxText( ""+raceConfig.race.getTempMax() );
      tempEditor.setImmune( raceConfig.race.tempImmune() );
      radEditor.setMinText( ""+raceConfig.race.getRadMin() );
      radEditor.setMaxText( ""+raceConfig.race.getRadMax() );
      radEditor.setImmune( raceConfig.race.radImmune() );
   }
   
   private Planet createPlanet( String name, String grav, String temp, String rad )
   {
      PlanetData planetData = new PlanetData();
      planetData.values = new String[50];
      planetData.values[Planet.PLANET_GRAV_BASE] = grav;
      planetData.values[Planet.PLANET_TEMP_BASE] = temp;
      planetData.values[Planet.PLANET_RAD_BASE] = rad;
      
      Planet planet = new Planet( name, 2400, new Point(0,0), planetData, null );

      return planet;
   }
   
   private void calcHabs()
   {
      int testSize = Utils.safeParseInt( testSizeField.getText(), 1000 );
      int numRed = 0;
      int numGreen = 0;
      int numPerfect = 0;
      int totalHab = 0;
      int totalGrowth = 0;
      
      HabCalculator calc = new HabCalculator();
      
      int maxPop = 1000000;
      
      if (joatField.isSelected()) maxPop *= 1.2;
      if (obrmField.isSelected()) maxPop *= 1.1;
          
      int gravTerra = Utils.safeParseInt(gravTerraLevel.getSelectedItem().toString());
      int tempTerra = Utils.safeParseInt(tempTerraLevel.getSelectedItem().toString());
      int radTerra = Utils.safeParseInt(radTerraLevel.getSelectedItem().toString());
      
      for (int n = 0; n < testSize; n++)
      {
         float grav = calc.getRandomGrav();
         int temp = calc.getRandomTemp();
         int rad = calc.getRandomRad();

         Planet planet = createPlanet( "x", ""+grav, ""+temp, ""+rad );
         
         int hab = calc.calcHabValue( planet, raceConfig.race, gravTerra, tempTerra, radTerra );
         
         if (hab <= 0)
         {
            numRed++;
         }
         else 
         {
            if (hab == 100)
            {
               numPerfect++;
            }
            numGreen++;
            totalHab += hab;
            
            int pop33 = maxPop * hab / 100 / 3;
            totalGrowth += getGrowth( pop33, 19, hab, maxPop );
         }
      }
      
      String text = "Out of " + testSize + " planets, " + numGreen * 100 / testSize + "% (" + numGreen + ") will be green\n";
      //if (numPerfect > 0)
      //{
      //   text += numPerfect + " of these will be perfect\n";
      //}
      text += "Of the green planets, the average value will be " + (totalHab / numGreen) + "%\n";
      text += "The overall average planet value will be " + (totalHab / testSize) + "%\n";
      text += "Population growth factor is " + totalGrowth / testSize;
      //text += "The total value of all the habitable planets is " + totalHab + "\n";
      //text += "Maximum supported population is " + ((long)totalHab * maxPop / 100);
      
      habDetails.setText( text );
   }
   
   /**
    * 
    * @param pop - the current population of the planet
    * @param pgr - the race's population growth rate (up to 20%, or 40% for HE)
    * @param value - the habitability value for the planet (0% to 100%)
    * @param maxPop - the maximum population that a 100% value planet can hold for this race (1M to 1.32M) 
    * @return
    */
   private int getGrowth( int pop, int pgr, int value, int maxPop )   
   {
      long growth = 0;
      long capacity = pop * 100 / maxPop;
      
      growth = (long)pop * pgr * value / (100*100);
      
      if (capacity > 25)
      {
         double c = (100.0 - capacity) / 100.0;
         double crowdingFactor = 16.0 / 9.0 * c * c;
         
         growth *= crowdingFactor;
      }
      
      return (int)growth;
   }
}