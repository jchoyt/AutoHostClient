/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

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
   private JPanel controls;
   private JRadioButton occupiedPlanetsButton;
   private JRadioButton allPlanetsButton;

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
	
	         int width = g.getFontMetrics().stringWidth( text );
	         int height = g.getFontMetrics().getHeight();
	         
	         g.drawString( text, screenPos.x-width/2, screenPos.y+height+2 );
         }
      }
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
         controls = new JPanel();
         
         controls.setLayout( new GridBagLayout() );
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.anchor = GridBagConstraints.NORTHWEST;

         // Default button action listener just tells the map config to notify it's listeners
         // that something has changed.  This will cause the map to be redrawn, and the 
         // PlanetNamesLayer will use the new control values.
         ActionListener defaultListener = new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
               config.notifyChangeListeners();
            }
         };
         
         allPlanetsButton = new JRadioButton("All planets");
         allPlanetsButton.addActionListener( defaultListener );
         
         occupiedPlanetsButton = new JRadioButton("Occupied planets");
         occupiedPlanetsButton.addActionListener( defaultListener );

         ButtonGroup bgroup = new ButtonGroup();
         bgroup.add( allPlanetsButton );
         bgroup.add( occupiedPlanetsButton );
         
         gbc.gridx = 1;
         gbc.gridy = 1;         
         controls.add( allPlanetsButton, gbc );
         
         gbc.gridx = 1;
         gbc.gridy += 1;         
         controls.add( occupiedPlanetsButton, gbc );
         
         allPlanetsButton.setSelected(true);
      }
      return controls;
   }

}
