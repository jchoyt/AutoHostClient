/*
 * Created on Oct 27, 2004
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import stars.ahc.Fleet;
import stars.ahc.Race;
import stars.ahc.Utils;
import stars.ahc.plugins.map.AbstractMapLayer;

/**
 * @author Steve Leach
 *
 */
public class FleetTrackLayer extends AbstractMapLayer
{
   private boolean enabled = false;
   private JComponent controls = null;
   private String selectedRace = null;
   private String selectedFleet = null;
   private String selectedType = null;
   private JComboBox raceList;
   private JComboBox fleetList;
   private JComboBox typeList;
   
   
   /**
    * Cache of line segments used to draw the fleet track
    */
   private class LineSegmentCache extends ArrayList
   {
      public String description = "empty";
      public LineSegment getSegment( int index )
      {
         return (LineSegment)get( index );
      }
   }
   private LineSegmentCache lineSegmentCache = new LineSegmentCache();
   
   /**
    * Describes a line in the fleet track 
    */
   private class LineSegment
   {
      public Color color;
      public Point from, to;
      
      /**
       * Draws the line segment onto the specified graphics device 
       */
      public void draw( Graphics2D g )
      {
         g.setColor( color );         
         g.drawLine( from.x, from.y, to.x, to.y );
      }
   }
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Fleet tracks";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      // Build a string describing what we want to draw 
      String required = "" + mapConfig.year + "::" + selectedRace + "::" + selectedFleet + "::" + selectedType;
   
      // Check to see whether we already have what we want in the cache
      if (lineSegmentCache.description.equals(required) == false)
      {
         rebuildCache();
         lineSegmentCache.description = required;
      }
      
      drawFromCache( g );      
   }

   /**
    * Rebuild the line segment cache 
    */
   private void rebuildCache()
   {
      lineSegmentCache.clear();
      
      int fleetCount = game.getFleetCount( mapConfig.year );
      
      // Loop through all the fleets
      for (int n = 0; n < fleetCount; n++)
      {
         Fleet fleet = game.getFleet( mapConfig.year, n );

         // If it is a fleet we want to show...
         if (raceTest(fleet) && typeTest(fleet) && fleetTest(fleet))
         {
            Color currentColor = game.getRaceColor( fleet.getOwner() );
	         
	         Point prevPos = mapConfig.mapToScreen( fleet.getPosition() );
	         
	         // Loop back through time building the fleet track
	         for (int y = mapConfig.year-1; y > 2400; y--)
	         {
	            Fleet f = game.getFleetByID( y, fleet.getOwner(), fleet.getID() );
	            
	            if (f != null)
	            {
	               LineSegment seg = new LineSegment();
	               seg.color = currentColor;
	               seg.from = prevPos;
	               seg.to = mapConfig.mapToScreen( f.getPosition() );

	               lineSegmentCache.add( seg );
	               
	               prevPos = seg.to;
	            }
	         }
         }
      }
      
   }

   /**
    * Draw the lines from the line segment cache 
    */
   private void drawFromCache( Graphics2D g )
   {
      for (int n = 0; n < lineSegmentCache.size(); n++)
      {
         lineSegmentCache.getSegment(n).draw( g );
      }
   }
   
   /**
    */
   private boolean raceTest( Fleet fleet )
   {
      return (selectedRace == null) || (selectedRace.equals(fleet.getOwner()));
   }

   private boolean typeTest( Fleet fleet )
   {
      if (selectedType == null)
      {
         return true;
      }
      
      int fieldNum = getFleetTypeField();
      
      int shipCount = fleet.getIntValue( fieldNum, 0 );
      
      return (shipCount > 0);
   }
   
   private int getFleetTypeField()
   {
      int fieldNum = 0;
      
      if (selectedType.equals("Scout"))
      {
         fieldNum = Fleet.SCOUT;
      }
      else if (selectedType.equals("Warship"))
      {
         fieldNum = Fleet.WARSHIP;
      }
      else if (selectedType.equals("Bomber"))
      {
         fieldNum = Fleet.BOMBER;
      }
      else if (selectedType.equals("Utility"))
      {
         fieldNum = Fleet.UTILITY;
      }
      else if (selectedType.equals("Unarmed"))
      {
         fieldNum = Fleet.UNARMED;
      }
      return fieldNum;
   }

   private boolean fleetTest( Fleet fleet )
   {
      return (selectedFleet == null) || (selectedFleet.equals(fleet.getName()));
   }
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Fleet tracks layer";
   }

   public boolean isEnabled()
   {
      return enabled;
   }
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }
   
   
   public JComponent getControls()
   {
      if (controls == null)
      {
         controls = Box.createVerticalBox();
         
         Vector races = new Vector();
         races.add( "All races" );
         
         Iterator r = game.getRaces();
         while (r.hasNext())
         {
            Race race = (Race)r.next();
            if (Utils.empty( race.getRaceName() ) == false)
            {
               races.add( race.getRaceName() );
            }
         }
         
         ActionListener defaultActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent event)
            {
               getControlValues();
               updateControls( (JComponent)event.getSource() );
               mapConfig.notifyChangeListeners();
            }
         };
         
         raceList = new JComboBox(races);
         raceList.setSelectedIndex(0);
         raceList.addActionListener( defaultActionListener );
         
         controls.add( raceList );
        
         String[] shipTypes = {"All types","Scout","Warship","Bomber","Utility","Unarmed"}; 
         typeList = new JComboBox(shipTypes);
         typeList.setSelectedIndex(0);
         typeList.addActionListener( defaultActionListener );
         
         controls.add( typeList );
         
         String[] fleets = new String[1];
         fleets[0] = "All fleets";
         fleetList = new JComboBox( fleets );
         
         fleetList.addActionListener( defaultActionListener );
         
         rebuildFleetList();
         
         controls.add( fleetList );
         
         controls.add( Box.createVerticalGlue() );
      }
      
      return controls;
   }
   
   private void getControlValues()
   {
      selectedRace = raceList.getSelectedItem().toString();
      
      if (selectedRace.startsWith("All"))
      {
         selectedRace = null;
      }
      
      if (typeList.getSelectedItem() == null)
      {
         selectedType = null;
      }
      else
      {
         selectedType = typeList.getSelectedItem().toString();
         
         if (selectedType.startsWith("All"))
         {
            selectedType = null;
         }
      }
      
      if (fleetList.getSelectedItem() == null)
      {
         selectedFleet = null;
      }
      else
      {
         selectedFleet = fleetList.getSelectedItem().toString();
      
	      if (selectedFleet.startsWith("All"))
	      {
	         selectedFleet = null;
	      }
      }
   }
   
   private void updateControls( JComponent originator )
   {
      if ((originator == raceList) || (originator == typeList))
      {
         rebuildFleetList();
      }
   }

   /**
    * 
    */
   private void rebuildFleetList()
   {
      fleetList.removeAllItems();
      
      fleetList.addItem( "All fleets" );
      
      int fleetCount = game.getFleetCount( mapConfig.year );
      
      for (int n = 0; n < fleetCount; n++)
      {
         Fleet fleet = game.getFleet( mapConfig.year, n );
         
         if (raceTest(fleet) && typeTest(fleet))
         {
            fleetList.addItem( fleet.getName() );
         }
      }      
      
      fleetList.setSelectedIndex(0);
   }
}
