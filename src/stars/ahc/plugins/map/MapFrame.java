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
package stars.ahc.plugins.map;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import stars.ahc.Game;

/**
 * Swing frame in which the game map is displayed
 * 
 * @author Steve Leach
 */
public class MapFrame extends JFrame
{
   protected Game game = null;
   protected MapConfig config = new MapConfig();
   protected static Map mapFrames = new HashMap(); 
   
   /**
    * Other classes should use viewGameMap() instead of the constructor.
    * @param game
    */
   protected MapFrame( Game game ) throws MapDisplayError
   {
      this.game = game;
      
      config.mapScale = 0.6;
      
      setupMapFrame();
      setupMapControls();
   }
   
   /**
    * Factory method for MapFrames - called instead of the constructor.
    * 
    * Displays the map for the specified game.
    * 
    * @param game
    * @throws MapDisplayError
    */
   public static void viewGameMap( Game game ) throws MapDisplayError
   {
      if (game == null)
      {
         throw new MapDisplayError( "No game specified" );
      }
      
      MapFrame mf = (MapFrame)mapFrames.get( game.getName() );
      
      if (mf == null)
      {
         mf = new MapFrame( game );
         mapFrames.put( game.getName(), mf );
      }
      
      mf.show();
      
   }

   /**
    * Sets up the frame itself (borders, title, etc)
    */
   private void setupMapFrame()
   {
      setBounds( 20, 20, 500, 400 );
      setTitle( "Map for " + game.getName() + " (" + game.getCurrentYear() + ")" );
      
      getContentPane().setLayout(new BorderLayout());
   }
   
   /**
    * Sets up the controls within the frame
    */
   private void setupMapControls() throws MapDisplayError
   {
      MapPanel mapPanel = new MapPanel( game, config );

      ArrayList layers = new ArrayList();
      
      //
      // Load the default map layers.
      // Note that these are specified as class names as they will eventually
      // be loaded as part of a plugin infrastructure.
      //
      layers.add( mapPanel.addMapLayer( "stars.ahc.plugins.map.layers.BackgroundLayer" ) );
      layers.add( mapPanel.addMapLayer( "stars.ahc.plugins.map.layers.PlanetLayer" ) );
            
      getContentPane().add( mapPanel, BorderLayout.CENTER );
      
      JPanel controlPanel = new JPanel();
      controlPanel.setBorder( new BevelBorder(BevelBorder.LOWERED) );

      String[] layerNames = new String[ layers.size() ];
      for (int n = 0; n < layers.size(); n++)
      {
         layerNames[n] = ((MapLayer)layers.get(n)).getDescription();
      }
      
      JList layerList = new JList( layerNames );
      layerList.setSelectedIndex(0);
      
      controlPanel.add( layerList );
      
      getContentPane().add( controlPanel, BorderLayout.EAST );
   }
}


