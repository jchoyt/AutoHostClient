/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.ReportLoaderException;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapDisplayError;
import stars.ahcgui.pluginmanager.MapLayer;

/**
 * @author Steve Leach
 *
 */
public class PlanetNamesLayer implements MapLayer
{
   private boolean enabled = true;
   private Game game;
   private MapConfig config;
   private Box controls;
   private JRadioButton occupiedPlanetsButton;
   private JRadioButton allPlanetsButton;
   private static final int DEFAULT_TEXT_SIZE = 8;
   private JSpinner textSizeField;

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return enabled;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Planet names";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#initialize(stars.ahc.Game, stars.ahc.plugins.map.MapConfig)
    */
   public void initialize(Game game, MapConfig config) throws MapDisplayError
   {
      this.game = game;
      this.config = config;
      
      if (game.getPlanetCount() == 0)
      {
         try
         {
            game.loadReports();
         }
         catch (ReportLoaderException e)
         {
            throw new MapDisplayError( "Error loading map file", e );
         }
      
         config.calcUniverseSize( game );
      }
      
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#isScaled()
    */
   public boolean isScaled()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      g.setColor( Color.YELLOW );
      
      for (int n = 1; n <= game.getPlanetCount(); n++)
      {
         Planet planet = game.getPlanet(n,config.year);
         
         boolean drawName = true;
         
         if (controls != null)
         {
            drawName = 	allPlanetsButton.isSelected() ||
         				(occupiedPlanetsButton.isSelected() && planet.isOccupied());
         }
         
         if (drawName)
         {
	         Point screenPos = config.mapToScreen( planet.getPosition() );
	         
	         String text = planet.getName();

	         setupFont( g );
	         
	         int width = g.getFontMetrics().stringWidth( text );
	         int height = g.getFontMetrics().getHeight();
	         
	         g.drawString( text, screenPos.x-width/2, screenPos.y+height+2 );
         }
      }
   }

   private void setupFont(Graphics2D g)
   {
      int textSize = DEFAULT_TEXT_SIZE;
      
      if (controls != null)
      {
         textSize = ((Integer)textSizeField.getModel().getValue()).intValue(); 
      }
      
      Font font = new Font( "SansSerif", Font.PLAIN, textSize );
      
      g.setFont( font );
   }
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Planet names layer";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   public JComponent getControls()
   {
      if (controls == null)
      {
         controls = Box.createVerticalBox();
         
         // Default button action listener just tells the map config to notify it's listeners
         // that something has changed.  This will cause the map to be redrawn, and the 
         // PlanetNamesLayer will use the new control values.
         ActionListener defaultListener = new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
               config.notifyChangeListeners();
            }
         };
         
         Box whichPlanetsBox = Box.createVerticalBox();
         whichPlanetsBox.setBorder( BorderFactory.createEtchedBorder() );
         
         allPlanetsButton = new JRadioButton("All planets");
         allPlanetsButton.addActionListener( defaultListener );
         
         occupiedPlanetsButton = new JRadioButton("Occupied planets");
         occupiedPlanetsButton.addActionListener( defaultListener );

         ButtonGroup bgroup = new ButtonGroup();
         bgroup.add( allPlanetsButton );
         bgroup.add( occupiedPlanetsButton );
         
         whichPlanetsBox.add( allPlanetsButton );         
         whichPlanetsBox.add( occupiedPlanetsButton );

         controls.add( whichPlanetsBox );
         
         Box textSizePanel = Box.createHorizontalBox();
         textSizePanel.setBorder( new EtchedBorder() );
         textSizePanel.add( new JLabel("Text size:") );
         
         SpinnerModel textSizeFieldModel = new SpinnerNumberModel(DEFAULT_TEXT_SIZE,1,40,1);
         textSizeField = new JSpinner(textSizeFieldModel);
         
         // FIXME: for some reason this isn't having any effect
         textSizeField.setEditor( new JSpinner.NumberEditor(textSizeField,"#0") );
         
         textSizeField.getModel().addChangeListener( new ChangeListener() {
            public void stateChanged(ChangeEvent event)
            {
               // Tell the map config to notify all listeners that something has changed.
               // This will force a map redraw.
               config.notifyChangeListeners();
            }
         } );
         
         textSizePanel.add( textSizeField );
         textSizePanel.add( Box.createHorizontalGlue() );        
         
         controls.add( textSizePanel );
         
         allPlanetsButton.setSelected(true);
      }
      return controls;
   }

}
