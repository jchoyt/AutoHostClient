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
   private Color color = null;
   private String raceName = null;
   
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
      return getUserProperty("playerName");
   }
   public void setPlayerName(String playerName)
   {
      setUserProperty("playerName",playerName);
   }
   public String getRaceName()
   {
      return raceName;
   }
   public void setRaceName(String raceName)
   {
      this.raceName = raceName;
      setUserProperty( "raceName", raceName );
   }
   public String getRacePlural()
   {
      return getUserProperty("racePlural");
   }
   public void setRacePlural(String racePlural)
   {
      setUserProperty("racePlural",racePlural);
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
   
   public void setUserProperty( String property, int value )
   {
      setUserProperty( property, ""+value );
   }

   public void setUserProperty( String property, double value )
   {
      setUserProperty( property, ""+value );
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
      if ((getMaxGravClicks() == getMinGravClicks()) && (gravImmune() == false))
      {
         return false;
      }
      return true; 
   }
   
   public void setGravRange( double min, double max, boolean immune )
   {
      setUserProperty( "gravMinClicks", StarsRuleSet.gravToClicks( min ) );
      setUserProperty( "gravMaxClicks", StarsRuleSet.gravToClicks( max ) );
      setUserProperty( "gravImmune", ""+immune );
   }
   
   public int getUserIntProperty( String name, int defaultValue )
   {
      String s = getUserProperty( name );
      return Utils.safeParseInt( s, defaultValue );      
   }

   public boolean getUserBoolProperty( String name, boolean defaultValue )
   {
      String s = getUserProperty( name );
      return "true".equals(s);      
   }
   
   public int getMinGravClicks()
   {
      return getUserIntProperty( "gravMinClicks", 0 );
   }

   public int getMaxGravClicks()
   {
      return getUserIntProperty( "gravMaxClicks", 0 );
   }
   
   public void setTempRange( int min, int max, boolean immune )
   {
      setUserProperty( "tempMinClicks", StarsRuleSet.tempToClicks(min) );
      setUserProperty( "tempMaxClicks", StarsRuleSet.tempToClicks(max) );
      setUserProperty( "tempImmune", ""+immune );
   }

   public int getMinTempClicks()
   {
      return getUserIntProperty( "tempMinClicks", 0 );
   }

   public int getMaxTempClicks()
   {
      return getUserIntProperty( "tempMaxClicks", 0 );
   }

   public void setRadRange( int min, int max, boolean immune )
   {
      setUserProperty( "radMinClicks", StarsRuleSet.radToClicks(min) );
      setUserProperty( "radMaxClicks", StarsRuleSet.radToClicks(max) );
      setUserProperty( "radImmune", ""+immune );
   }
   
   public int getMinRadClicks()
   {
      return getUserIntProperty( "radMinClicks", 0 );
   }

   public int getMaxRadClicks()
   {
      return getUserIntProperty( "radMaxClicks", 0 );
   }
   
   public float getGravMin()
   {
      return StarsRuleSet.gravFromClicks( getMinGravClicks() );
   }
   
   public float getGravMax()
   {
      return StarsRuleSet.gravFromClicks( getMaxGravClicks() );
   }

   public int getTempMin()
   {
      return StarsRuleSet.tempFromClicks( getMinTempClicks() );
   }

   public int getTempMax()
   {
      return StarsRuleSet.tempFromClicks( getMaxTempClicks() );
   }

   public int getRadMin()
   {
      return StarsRuleSet.radFromClicks( getMinRadClicks() );
   }

   public int getRadMax()
   {
      return StarsRuleSet.radFromClicks( getMaxRadClicks() );
   }
   
   public boolean gravImmune()
   {
      return getUserBoolProperty("gravImmune",false);
   }

   public boolean tempImmune()
   {
      return getUserBoolProperty("tempImmune",false);
   }

   public boolean radImmune()
   {
      return getUserBoolProperty("radImmune",false);
   }

   /**
    * Returns the maximum amount of terraforming that the race can perform.
    * <p>
    * This will be 30 for races with the Total Terraforming LRT, and 15 for others
    */
   public int getMaxTerraForm()
   {
      String lrts = getUserProperty( "lrts" );
      if (lrts == null) return 15;
      return (lrts.toUpperCase().indexOf("TT") >= 0) ? 30 : 15;
   }
}
