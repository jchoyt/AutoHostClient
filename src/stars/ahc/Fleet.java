/**
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

 */
package stars.ahc;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    February 16, 2004
 */
public class Fleet implements MapObject
{
    protected String[] info;
    private int hashCache = 0;
    protected String owner;


    /**
     *  Constructor for the Fleet object
     */
    public Fleet()
    {
        info = new String[29];
    }


    /**
     *  Constructor for the Fleet object
     *
     *@param  values  Description of the Parameter
     */
    public Fleet( String[] values )
    {
        if ( values.length == 29 )
        {
            info = values;
            parse( values );
        }
        else
        {
            throw new RuntimeException( "The passed array to this constructor must have a length of 29" );
        }
    }


    /**
     *  Constructor for the Fleet object
     *
     *@param  tabSeparatedValues  Description of the Parameter
     */
    public Fleet( String tabSeparatedValues )
    {
        String[] values = tabSeparatedValues.split( "\t" );
        if ( values.length == 29 )
        {
            info = values;
            parse( values );
        }
        else
        {
            throw new RuntimeException( "The passed string to this constructor must be 29 values separated by tabs" );
        }
    }


    /**
     *  Description of the Method
     *
     *@param  values  Description of the Parameter
     */
    protected void parse( String[] values )
    {
        /*
         *  parse out the race name, accounting for race names that start with The
         */
        String[] parts = values[FLEET_NAME].split( " " );
        if ( parts[0].equals( "The " ) )
        {
            owner = parts[0] + " " + parts[1];
        }
        else
        {
            owner = parts[0];
        }
    }


    /**
     *  Gets the value attribute of the Fleet object
     *
     *@param  field  Description of the Parameter
     *@return        The value value
     */
    public String getValue( int field )
    {
        return info[field];
    }


    /**
     *  Gets the owner attribute of the Fleet object
     *
     *@return    The owner value
     */
    public String getOwner()
    {
        return owner;
    }


    /**
     *  Compares this object to another. Note hashCode is overwritten in the
     *  parent class.
     *
     *@param  o  Description of the Parameter
     *@return    Description of the Return Value
     */
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o instanceof Fleet )
        {
            return ( o.hashCode() == hashCode() );
        }
        return false;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public int hashCode()
    {
        if ( hashCache == 0 )
        {
            hashCache = getValue( FLEET_NAME ).hashCode();
        }
        return hashCache;
    }


    /**
     *  Gets the intValue attribute of the Fleet object
     *
     *@param  field  Description of the Parameter
     *@return        The intValue value
     */
    public int getIntValue( int field )
    {
        return Integer.parseInt( info[field] );
    }

    public int getIntValue( int field, int defaultValue )
    {
       return Utils.safeParseInt( info[field], defaultValue );
    }

    /**
     *  Gets the name attribute of the Fleet object
     *
     *@param  field  Description of the Parameter
     *@return        The name value
     */
    public String getName( int field )
    {
        return columnNames[field];
    }


    /**
     *  Gets the niceLocation attribute of the Fleet object
     *
     *@return    The niceLocation value
     */
    public String getNiceLocation()
    {
        if ( !getValue( PLANET ).equals( "" ) )
        {
            return getValue( PLANET );
        }
        else
        {
            return ( "( " + getValue( X ) + ", " + getValue( Y ) + " )" );
        }
    }


    public String[] columnNames = {"Fleet Name", "X", "Y", "Planet", "Destination", "Battle Plan", "Ship Cnt", "Iron", "Bora", "Germ", "Col", "Fuel", "Owner", "ETA", "Warp", "Mass", "Cloak", "Scan", "Pen", "Task", "Mining", "Sweep", "Laying", "Terra", "Unarmed", "Scout", "Warship", "Utility", "Bomber"};
    public static int FLEET_NAME = 0;
    public static int X = 1;
    public static int Y = 2;
    public static int PLANET = 3;
    public static int DESTINATION = 4;
    public static int BATTLE_PLAN = 5;
    public static int SHIP_COUNT = 6;
    public static int IRON = 7;
    public static int BORA = 8;
    public static int GERM = 9;
    public static int COL = 10;
    public static int FUEL = 11;
    public static int OWNER = 12;
    public static int ETA = 13;
    public static int WARP = 14;
    public static int MASS = 15;
    public static int CLOAK = 16;
    public static int SCAN = 17;
    public static int PEN = 18;
    public static int TASK = 19;
    public static int MINING = 20;
    public static int SWEEP = 21;
    public static int LAYING = 22;
    public static int TERRA = 23;
    public static int UNARMED = 24;
    public static int SCOUT = 25;
    public static int WARSHIP = 26;
    public static int UTILITY = 27;
    public static int BOMBER = 28;


   /* (non-Javadoc)
    * @see stars.ahc.MapObject#getName()
    */
   public String getName()
   {
      return info[FLEET_NAME];
   }


   public int getX()
   {
      return getIntValue(X);
   }
   public int getY()
   {
      return getIntValue(Y);
   }
}

