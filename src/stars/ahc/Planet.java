/*
 * Created on Oct 7, 2004
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

import java.awt.Point;

/**
 * Encapulates information about a planet in a Stars! game
 * 
 * @author Steve Leach
 */
public class Planet implements MapObject
{
   private PlanetData data;
   private String name;
   private int year;
   private Point position;
   private Game game;
   
   //
   // Constants for field indexes
   //
   public static final int PLANET_NAME = 0;
   public static final int PLANET_OWNER = 1;
   public static final int PLANET_BASE = 2;
   public static final int PLANET_REPORTAGE = 3;
   public static final int PLANET_POPULATION = 4;
   public static final int PLANET_VALUE = 5;
   public static final int PLANET_QUEUE = 6;
   public static final int PLANET_MINES = 7;
   public static final int PLANET_FACTORIES = 8;
   public static final int PLANET_DEFENCE = 9;
   public static final int PLANET_SURF_I = 10;
   public static final int PLANET_SURF_B = 11;
   public static final int PLANET_SURF_G = 12;
   public static final int PLANET_RATE_I = 13;
   public static final int PLANET_RATE_B = 14;
   public static final int PLANET_RATE_G = 15;
   public static final int PLANET_CONC_I = 16;
   public static final int PLANET_CONC_B = 17;
   public static final int PLANET_CONC_G = 18;
   public static final int PLANET_RESOURCES = 19;
   public static final int PLANET_GRAV = 20; // Start of newreports
   public static final int PLANET_TEMP = 21;
   public static final int PLANET_RAD = 22;
   public static final int PLANET_GRAV_BASE = 23; 
   public static final int PLANET_TEMP_BASE = 24;
   public static final int PLANET_RAD_BASE = 25;
   public static final int PLANET_TERRA = 26;
   public static final int PLANET_CAPACITY = 27;
   public static final int PLANET_SCANRANGE = 28;
   public static final int PLANET_PENSCAN = 29;
   public static final int PLANET_GATERANGE = 33;
   public static final int PLANET_GATEMASS = 34;
   
   /**
    */
   public Planet(String name,int year,Point position,PlanetData data,Game game)
   {
      this.name = name;
      this.year = year;
      this.position = position;
      this.data = data;
      this.game = game;
   }

   public Point getPosition()
   {
      return position;
   }

   /* (non-Javadoc)
    * @see stars.ahc.MapObject#getName()
    */
   public String getName()
   {
      return name;
   }

   private String getValue( int index )
   {
      if (data == null) return null;
      if (data.values == null) return null;
      if (data.values.length < (index+1)) return null;
      
      return data.values[index];
   }
   
   private int getIntValue( int index, int defaultValue )
   {
      String value = getValue(index);
            
      if (value != null)
      {
         if (value.endsWith("%"))
         {
            value = value.substring(0,value.length()-1);
         }
         value.replaceAll( "[%]", "" ).trim();
      }
      return Utils.safeParseInt( value, defaultValue );
   }
   
   /* (non-Javadoc)
    * @see stars.ahc.MapObject#getOwner()
    */
   public String getOwner()
   {
      return getValue(PLANET_OWNER);
   }

   /* (non-Javadoc)
    * @see stars.ahc.MapObject#getX()
    */
   public int getX()
   {
      return position.x;
   }

   /* (non-Javadoc)
    * @see stars.ahc.MapObject#getY()
    */
   public int getY()
   {
      return position.y;
   }
   
   public String toString()
   {
      return name;
   }
   
   public boolean isUnoccupied()
   {
      return Utils.empty( getOwner() );
   }
   
   public boolean isOccupied()
   {
      return Utils.empty( getOwner() ) == false;
   }

   /**
    */
   public int getPopulation()
   {
      return getIntValue( PLANET_POPULATION, 0 );
   }
   
   public int getGateRange()
   {
      return getIntValue( PLANET_GATERANGE, 0 );
   }
   
   public int getGateMass()
   {
      return getIntValue( PLANET_GATEMASS, 0 );
   }
   
   public int distanceFrom( MapObject obj )
   {
      int dx = this.getX() - obj.getX();
      int dy = this.getY() - obj.getY();
      
      double distance = Math.sqrt( dx * dx + dy + dy );
      
      return (int)Math.round(distance);
   }
   
   public int getGerConc()
   {
      return getIntValue( PLANET_CONC_G, 0 );
   }
   
   public int getIroConc()
   {
      return getIntValue( PLANET_CONC_I, 0 );
   }
   
   public int getBorConc()
   {
      return getIntValue( PLANET_CONC_B, 0 );
   }

   public int getHabValue()
   {
      return getIntValue( PLANET_VALUE, 0 );
   }
   
   private String getUserDefinedPropertyFullName( String shortName )
   {
      return "Planets." + name.replaceAll(" ","_") + "." + shortName;
   }
   
   /**
    * Sets a user defined property for this planet 
    */
   public void setUserProperty( String propertyName, String value )
   {
      game.setUserDefinedProperty( getUserDefinedPropertyFullName( propertyName ), value );
   }
   
   /**
    * Gets a user defined property for this planet 
    */
   public String getUserProperty( String propertyName )
   {
      return game.getUserDefinedProperty( getUserDefinedPropertyFullName( propertyName ) );
   }
   
   public String getDebugString()
   {
      String str =  "Planet '" + getName() + "' ";
      if (getOwner() != null)
      {
         str += "(" + getOwner() + ") ";
      }
      str += year;
      return str;
   }

   /**
    */
   public int getReportAge()
   {
      return getIntValue( PLANET_REPORTAGE, 0 );
   }
   
   public String getStarBase()
   {
      return getValue( PLANET_BASE );
   }

   /**
    * Returns true if the planet's base hab values are known
    */
   public boolean habValuesKnown()
   {
      return Utils.empty( getValue( PLANET_GRAV_BASE ) ) == false;
   }

   /**
    * Returns -1 if the value is not known
    */
   public int getBaseGravClicks()
   {
      String baseGravStr = getValue( PLANET_GRAV_BASE );
      
      if (baseGravStr == null)
      {
         return -1;
      }

      double baseGrav = Utils.getLeadingFloat(baseGravStr,0);
      return StarsRuleSet.gravToClicks( baseGrav );
   }

   public int getBaseTempClicks()
   {
      String baseTempStr = getValue( PLANET_TEMP_BASE );
      
      if (baseTempStr == null)
      {
         return -1;
      }

      int baseTemp = Utils.getLeadingInt(baseTempStr,0);
      return StarsRuleSet.tempToClicks( baseTemp );
   }

   public int getBaseRadClicks()
   {
      String baseRadStr = getValue( PLANET_RAD_BASE );
      
      if (baseRadStr == null)
      {
         return -1;
      }

      int baseRad = Utils.getLeadingInt(baseRadStr,0);
      return StarsRuleSet.radToClicks( baseRad );
   }

   /**
    * @return
    */
   public int getCurrentHab()
   {
      return getIntValue(PLANET_VALUE, 0 );
   }
}
