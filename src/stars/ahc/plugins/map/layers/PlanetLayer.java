/*
 * Created on Oct 6, 2004
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
package stars.ahc.plugins.map.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import java.lang.Math;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.ReportLoaderException;
import stars.ahc.Utils;
import stars.ahc.plugins.map.AbstractMapLayer;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapDisplayError;

/**
 * @author Steve Leach
 *
 */
public class PlanetLayer extends AbstractMapLayer
{ 
   // Bryan Wiegand
   // Defence indication in the planet layer (by an arc)
   // Poplation incicated by size
   private JComponent controls = null;
   private JCheckBox defences;
   private JCheckBox population;
   private boolean defenceToggle;
   private boolean populationToggle;
   private static int DEFAULT_SIZE = 5;
   private Planet planet;
   private Color raceColor;
   private static int NO_DEFENCES = 101;
   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#getDescription()
    */
   public String getDescription()
   {
      return "Planets";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
   	  for (int n = 1; n <= game.getPlanetCount(); n++)
      {
   	    planet = game.getPlanet(n,mapConfig.year); 
   	  	String defenceValue = planet.getDefence();
   	    int arcSize = calculateArc(defenceValue);
        int populationValue = planet.getPopulation();
        raceColor = game.getRaceColor( planet.getOwner() );
        if (planet.isUnoccupied())
        {
         	drawPlanets(g, DEFAULT_SIZE, NO_DEFENCES);
        }
        else if(defenceToggle && populationToggle)
        {
         	int size = calculateSize(populationValue);
         	//int arcSize = calculateArc(defenceValue);
         	drawPlanets(g, size, arcSize);
        }
        else if(defenceToggle && (populationToggle == false) )
        {
         	//int arcSize = calculateArc(defenceValue);
         	drawPlanets(g, DEFAULT_SIZE, arcSize);
        }
        else if(populationToggle && (defenceToggle == false))
        {
         	int size = calculateSize(populationValue);
         	drawPlanets(g, size, NO_DEFENCES);
        }
        else if((defenceToggle != true) && (populationToggle != true)) 
        {
         	drawPlanets(g, DEFAULT_SIZE, NO_DEFENCES);
        }

      }
   }


   private void drawPlanets(Graphics2D g, int planetSize, int arcSize )
   {
   	//drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	Point screenPos = mapConfig.mapToScreen( planet.getPosition() );
	g.setColor( raceColor );
    g.fillOval( screenPos.x-2, screenPos.y-2, planetSize, planetSize );
    if ((arcSize == NO_DEFENCES) || (arcSize == 0))
    {
    	g.setColor( Color.BLACK );
        g.drawOval( screenPos.x-2, screenPos.y-2, planetSize, planetSize );
    }
    else
    {
    	g.setColor( Color.WHITE);
    	g.drawArc(screenPos.x-2, screenPos.y-2, planetSize, planetSize, 0, arcSize);
    }
    
   }
   
   private int calculateArc(String defenceValue)
   {
   	if (defenceValue == null) 
   	{
   		return NO_DEFENCES;
   	}
   	else
   	{
   		int defence = Utils.getLeadingInt(defenceValue, NO_DEFENCES); 
   		int finalArc = (defence * 360)/100;
   		return finalArc;
   	}
   }
   /**
    * @param populationValue
    * @return
    */
   private int calculateSize(int populationValue)
   {
	double size;
	// TODO Operations to determine the size based on population.
	if (populationValue <= 20000)
	{
		return 3;
	}
	else
	{
	size = (Math.log(populationValue)/Math.log(10) * 9.26) - 36;
	}
	int finalSize = (int)Math.round(size);
   	return finalSize;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.map.MapLayer#initialize(stars.ahc.Game, stars.ahcgui.map.MapConfig)
    */
   public void initialize(Game game, MapConfig config) throws MapDisplayError
   {
      this.mapConfig = config;
      this.game = game;
      
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
      }
      
      if (mapConfig.getUniverseSize() == 0)
      {
         mapConfig.calcUniverseSize( game );         
      }
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {      
      return "Planet layer";
   }
   
   public JComponent getControls()
   {
    if (controls == null)
    {
       controls = Box.createVerticalBox();
       
       ActionListener defaultActionListener = new ActionListener()
		 {
          public void actionPerformed(ActionEvent event)
          {
             getControlValues();
             mapConfig.notifyChangeListeners();
          }
       };
       
       defences = new JCheckBox("Defences", false);
       defences.addActionListener( defaultActionListener );
       controls.add (defences);
       
       population = new JCheckBox("Population", false);
       population.addActionListener( defaultActionListener );
       controls.add (population);
       
    }
   	return controls;
   }
   private boolean getControlValues()
   {
      defenceToggle = defences.isSelected();
      populationToggle = population.isSelected();
      return defenceToggle;
   }
}
