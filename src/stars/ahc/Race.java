/*
 * Created on Oct 11, 2004
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
package stars.ahc;

import java.awt.Color;
import java.text.ParseException;
import java.util.Properties;
import java.util.Random;

/**
 * Represents a race in a Stars! game, controlled by a Player 
 * 
 * @author Steve Leach
 * @see stars.ahc.Player
 */
public class Race
{
   private Game game;
   private String playerName = null;
   private Color color = null;
   private String raceName = null;
   private String racePlural = null;
   private String prt = "";
   private String lrts = "";
   private int grav_min_clicks = 0;
   private int grav_max_clicks = 0;
   private int temp_min_clicks = 0;
   private int temp_max_clicks = 0;
   private int rad_min_clicks = 0;
   private int rad_max_clicks = 0;
   
   public Race( Game game )
   {
      this.game = game;
   }
   
   public Color getColor()
   {
      if (color == null)
      {
         // Create new random colour

         pickRandomColor();         
      }
      
      return color;
   }
   
   /**
    * 
    */
   public void pickRandomColor()
   {
      Random rnd = new Random();
      
      float h = Utils.getRandomFloat();						// hue is totally random (0 to 1.0)
      float s = Utils.getRandomFloat() * 0.5f + 0.5f;		// saturation is random but high (0.5 to 1.0)
      float v = 1.0f;										// value is always maximum (1.0)
      
      color = Color.getHSBColor( h, s, v );
   }

   public void setColor(Color color)
   {
      this.color = color;
   }
   public String getPlayerName()
   {
      return playerName;
   }
   public void setPlayerName(String playerName)
   {
      this.playerName = playerName;
   }
   public String getRaceName()
   {
      return raceName;
   }
   public void setRaceName(String raceName)
   {
      this.raceName = raceName;
   }
   public String getRacePlural()
   {
      return racePlural;
   }
   public void setRacePlural(String racePlural)
   {
      this.racePlural = racePlural;
   }
   
   public boolean equals( Race race )
   {
      return this.raceName.equalsIgnoreCase( race.raceName );
   }
   
   public boolean equals( String raceName )
   {
      return this.raceName.equalsIgnoreCase( raceName );
   }
   
   private String propertiesBase()
   {
      return game.getName() + ".Races." + raceName.replaceAll(" ","_");
   }
   
   /**
    */
   public void setProperties(Properties props)
   {
      if (Utils.empty(raceName)) return;	// don't save properties for empty race
       props.setProperty( propertiesBase() + ".color", Utils.getColorStr(color) );
   }

   /**
    * @param props
    */
   public void getProperties(Properties props)
   {
      String s = props.getProperty( propertiesBase() + ".color" );
      
      try
      {
         color = Utils.getColorFromString( s );
      }
      catch (ParseException e)
      {
         pickRandomColor();
      }
   }

   private String getUserDefinedPropertyFullName( String shortName )
   {
      return "Races." + getRaceName().replaceAll(" ","_") + "." + shortName;
   }
   
   /**
    */
   public String getUserProperty(String property)
   {
      String propertyName = getUserDefinedPropertyFullName( property );
      return game.getUserDefinedProperty( propertyName );
   }
   
   public void setUserProperty( String property, String value )
   {
      String propertyName = getUserDefinedPropertyFullName( property );
      game.setUserDefinedProperty( propertyName, value );
   }
   
   public void save()
   {
      GamesProperties.writeProperties();
   }

   /**
    * Returns true if the hab range values for the race are known
    */
   public boolean habRangeKnown()
   {
      return true; // TODO: implement
   }
   
   public void setGravRange( double min, double max )
   {
      grav_min_clicks = HabCalculator.gravToClicks( min );
      grav_max_clicks = HabCalculator.gravToClicks( max );
   }
   
   public int getMinGravClicks()
   {
      return grav_min_clicks;
   }

   public int getMaxGravClicks()
   {
      return grav_max_clicks;
   }
   
   public void setTempRange( int min, int max )
   {
      temp_min_clicks = HabCalculator.tempToClicks(min);
      temp_max_clicks = HabCalculator.tempToClicks(max);
   }

   public int getMinTempClicks()
   {
      return temp_min_clicks;
   }

   public int getMaxTempClicks()
   {
      return temp_max_clicks;
   }

   public void setRadRange( int min, int max )
   {
      rad_min_clicks = HabCalculator.radToClicks(min);
      rad_max_clicks = HabCalculator.radToClicks(max);
   }
   
   public int getMinRadClicks()
   {
      return rad_min_clicks;
   }

   public int getMaxRadClicks()
   {
      return rad_max_clicks;
   }

   public boolean gravImmune()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean tempImmune()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean radImmune()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * Returns the maximum amount of terraforming that the race can perform.
    * <p>
    * This will be 30 for races with the Total Terraforming LRT, and 15 for others
    */
   public int getMaxTerraForm()
   {
      return (lrts.toUpperCase().indexOf("TT") >= 0) ? 30 : 15;
   }
}
