/*
 *  This file is part of Stars! Autohost Client
 *  Copyright (c) 2003 Jeffrey C. Hoyt
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
import java.awt.Color;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public class Game extends Object
{
    private final static int MAX_RACES = 16;
    Properties ahStatus;
    String directory;
    String name;
    PropertyChangeSupport pcs = new PropertyChangeSupport( new Object() );
    ArrayList players;
    private PlanetList planets;
    private FleetList fleets;
    private ShipDesignList shipDesigns;
    private ArrayList races = new ArrayList();
    private Properties userDefinedProperties = new Properties();
    protected String sahHosted = "true";
    private GameController controller;
    
    /**
     * A list of objects (all implementing GameUpdateListener) that wish to be notified
     * when the game details change
     */
    private Vector updateListeners = new Vector();	// use a vector as it is synchronized


    /**
     *  Constructor for the Game object
     */
    public Game()
    {
        init( "", "" );
    }


    /**
     *  Constructor for the Game object
     *
     *@param  shortName  Description of the Parameter
     *@param  directory  Description of the Parameter
     */
    public Game( String shortName, String directory )
    {
        init( shortName, directory );
    }


    /**
     *  Description of the Method
     *
     *@param  shortName  Description of the Parameter
     *@param  directory  Description of the Parameter
     */
    private void init( String shortName, String directory )
    {
        name = shortName.toString();
        players = new ArrayList();
        planets = new PlanetList( this );
        fleets = new FleetList( this );
        shipDesigns = new ShipDesignList( this );
        this.directory = directory;
        loadProperties();
        sahHosted = GamesProperties.getProperty( shortName + ".sahHosted", "true" );
        initController();
        loadProperties();
        loadUserDefinedProperties();
    }


    private void initController()
   {
      if ( (sahHosted != null) && sahHosted.equals( "true" ) )
        {
            Log.log( Log.DEBUG, this, name + ": SahHosted game" );
            controller = new AutoHostGameController( this );
        }
        else
        {
            Log.log( Log.DEBUG, this, name + ": local game" );
            controller = new LocalGameController( this );
        }
   }


   /**
     *  Sets the directory attribute of the Game object
     *
     *@param  directory  The new directory value
     */
    public void setDirectory( String directory )
    {
        this.directory = directory;
    }


    /**
     *  Sets the name attribute of the Game object
     *
     *@param  name  The new name value
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     *  Sets the players attribute of the Game object
     *
     *@param  players  The new players value
     */
    public void setPlayers( Player[] players )
    {
        for ( int i = 0; i < players.length; i++ )
        {
            this.players.add( players[i] );
        }
    }


    /**
     *  Gets the currentYear attribute of the Game object
     *
     *@return    The currentYear value
     */
    public String getCurrentYear()
    {
        return controller.getCurrentYear();
    }


    /**
     *  Gets the directory attribute of the Game object
     *
     *@return    The directory value
     */
    public String getDirectory()
    {
        return directory;
    }


    /**
     *  Gets the gameYear attribute of the Game object
     *
     *@return    The gameYear value
     */
    public String getGameYear()
    {
        return controller.getGameYear();
    }


    /**
     *  Gets the longName attribute of the Game object
     *
     *@return    The longName value
     */
    public String getLongName()
    {
        return controller.getLongName();
    }


    /**
     *  Gets the name attribute of the Game object
     *
     *@return    The name value
     */
    public String getName()
    {
        return name;
    }


    /**
     *  Gets the nextGen attribute of the Game object
     *
     *@return    The nextGen value
     */
    public String getNextGen()
    {
        return controller.getNextGen();
    }


    /**
     *  Gets the playerRaceName attribute of the Game object
     *
     *@param  id  Description of the Parameter
     *@return     The playerRaceName value
     */
    public String getPlayerRaceName( String id )
    {
        return controller.getPlayerRaceName( id );
    }


    /**
     *  Gets the fFileName attribute of the Game object
     *
     *@param  playerNumber  Description of the Parameter
     *@return               The fFileName value
     */
    public String getFFileName( String playerNumber )
    {
        return getName() + ".f" + playerNumber;
    }


    /**
     *  Gets the fFileName attribute of the Game object
     *
     *@param  playerNumber  Description of the Parameter
     *@return               The fFileName value
     */
    public String getFFileName( int playerNumber )
    {
        return getName() + ".f" + playerNumber;
    }


    /**
     *  Gets the pFileName attribute of the Game object
     *
     *@param  playerNumber  Description of the Parameter
     *@return               The pFileName value
     */
    public String getPFileName( String playerNumber )
    {
        return getName() + ".p" + playerNumber;
    }


    /**
     *  Gets the pFileName attribute of the Game object
     *
     *@param  playerNumber  Description of the Parameter
     *@return               The pFileName value
     */
    public String getPFileName( int playerNumber )
    {
        return getName() + ".p" + playerNumber;
    }


    /**
     *  Gets the players attribute of the Game object
     *
     *@return    The players value
     */
    public Player[] getPlayers()
    {
        Object[] playersArray = players.toArray();
        Player[] ar = new Player[playersArray.length];
        for ( int i = 0; i < ar.length; i++ )
        {
            ar[i] = ( Player ) playersArray[i];
        }
        return ar;
    }


    /**
     *  Gets the playersByStatus attribute of the Game object
     *
     *@return    A Properties object with three properties containing a list of
     *      the races with a given status (in a StringBuffer). The three keys
     *      are in, out, and dead.
     */
    public Properties getPlayersByStatus()
    {
        return controller.getPlayersByStatus();
    }


    /**
     *  Gets the stagingDirectoryPath attribute of the Game object
     *
     *@return    The stagingDirectoryPath value
     */
    public String getStagingDirectoryPath()
    {
        return directory + "/staging";
    }


    /**
     *  Gets the status attribute of the Game object
     *
     *@return    The status value
     */
    public String getStatus()
    {
        return controller.getStatus();
    }


    /**
     *  Sets the ahStatus attribute of the Game object
     *
     *@param  props  The new ahStatus value
     */
    public void setAhStatus( Properties props )
    {
        ahStatus = props;
    }


    /**
     *  Gets the statusFileName attribute of the Game object
     *
     *@return    The statusFileName value
     */
    public String getStatusFileName()
    {
        return getName() + ".status";
    }


    /*
     *  Constants for use with getReportFile()
     */
    public final static int REPORTTYPE_MAP = 1;
    public final static int REPORTTYPE_PLANET = 2;
    public final static int REPORTTYPE_FLEET = 3;


    /**
     *@param  reportType  Description of the Parameter
     *@param  player      Description of the Parameter
     *@param  year        Description of the Parameter
     *@return             The reportFile value
     */
    public File getReportFile( int reportType, int player, int year )
    {
        if ( year == 0 )
        {
            year = Integer.parseInt( getGameYear() );
        }
        File ret;
        switch ( reportType )
        {
            case REPORTTYPE_MAP:
                ret = new File( getDirectory(), getName() + ".map" );
                break;
            case REPORTTYPE_PLANET:
                ret = new File( getDirectory() + "/backup", Utils.createBackupFileName( new File( getPFileName( player ) ), Integer.toString( year ) ) );
                break;
            case REPORTTYPE_FLEET:
                ret = new File( getDirectory() + "/backup", Utils.createBackupFileName( new File( getFFileName( player ) ), Integer.toString( year ) ) );
                break;
            default:
                return null;//not sure what you want to do here
        }
        return ret;
    }

    /**
     * Used to notify other objects that something has changed in the game 
     */
    public void firePropertyChange( String propertyName, Object oldValue, Object newValue )
    {
       pcs.firePropertyChange( propertyName, oldValue, newValue );
    }

    /**
     *  Adds a feature to the Player attribute of the Game object
     *
     *@param  p  The feature to be added to the Player attribute
     */
    public void addPlayer( Player p )
    {
        p.setGame( this );
        players.add( p );
        pcs.firePropertyChange( "player added", null, p );
    }


    /**
     *  Adds a feature to the PropertyChangeListener attribute of the Player
     *  object
     *
     *@param  listener  The feature to be added to the PropertyChangeListener
     *      attribute
     */
    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        pcs.addPropertyChangeListener( listener );
        controller.addPropertyChangeListener( listener );
    }


    /**
     * Loads the game status properties
     */
    public void loadProperties()
    {
       ahStatus = new Properties();
       if (controller != null)
       {
          controller.loadStatusProperties( ahStatus );
       }

       pcs.firePropertyChange( "gameStatus", 0, 1 );

//        ahStatus = new Properties();
//        try
//        {
//            File statusFile = new File( getDirectory(), getStatusFileName() );
//            if ( !statusFile.exists() )
//            {
//                poll();
//                //return;
//            }
//            InputStream in = new FileInputStream( statusFile );
//            ahStatus.load( in );
//            pcs.firePropertyChange( "gameStatus", 0, 1 );
//        }
//        catch ( Exception e )
//        {
//            Log.log( Log.ERROR, this, e );
//        }
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public boolean poll()
    {
        return (controller == null) ? false : controller.poll();
    }


    /**
     *  Description of the Method
     *
     *@param  p  Description of the Parameter
     */
    public void removePlayer( Player p )
    {
        players.remove( p );
        pcs.firePropertyChange( "player removed", 1, 0 );
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString()
    {
        String lineEnding = System.getProperty( "line.separator" );
        StringBuffer ret = new StringBuffer();
        ret.append( "Game=" + name + lineEnding );
        StringWriter out = new StringWriter();
        ret.append( name + ".GameDir=" + directory + lineEnding );
        ret.append( name + ".sahHosted=" + sahHosted );
        return ret.toString();
    }


    /**
     *  Sets the playerTurnStatus attribute of the Game object
     *
     *@param  playerNum  The new playerTurnStatus value
     *@param  value      The new playerTurnStatus value
     */
    public void setPlayerTurnStatus( int playerNum, String value )
    {
        controller.setPlayerTurnStatus( playerNum, value );
    }


    /**
     *  Gets the year attribute of the Game object
     *
     *@return    The year value
     */
    public int getYear()
    {
        return Utils.safeParseInt( getCurrentYear() );
    }

    // Pass-through functions for planet list

    /**
     *  Gets the planet attribute of the Game object
     *
     *@param  planetName  Description of the Parameter
     *@return             The planet value
     */
    public Planet getPlanet( String planetName )
    {
        return planets.getPlanet( planetName, getYear() );
    }


    /**
     *  Gets the planet attribute of the Game object
     *
     *@param  planetName  Description of the Parameter
     *@param  year        Description of the Parameter
     *@return             The planet value
     */
    public Planet getPlanet( String planetName, int year )
    {
        return planets.getPlanet( planetName, year );
    }


    /**
     *  Gets the planetCount attribute of the Game object
     *
     *@return    The planetCount value
     */
    public int getPlanetCount()
    {
        return planets.getPlanetCount();
    }


    /**
     *@param  index  (starts at 1)
     *@return
     */
    public Planet getPlanet( int index )
    {
        return planets.getPlanet( index, getYear() );
    }


    /**
     *  Gets the planet attribute of the Game object
     *
     *@param  index  Description of the Parameter
     *@param  year   Description of the Parameter
     *@return        The planet value
     */
    public Planet getPlanet( int index, int year )
    {
        if ( year == 0 )
        {
            year = getYear();
        }
        return planets.getPlanet( index, year );
    }


    /**
     *  Returns the planets list
     *
     *@return    The planets value
     */
    public PlanetList getPlanets()
    {
        return planets;
    }


    /**
     *  Loads the contents of the Stars! map file into the planets list
     *
     *@exception  ReportLoaderException  Description of the Exception
     *@author                            Steve Leach
     */
    public void loadMapFile()
        throws ReportLoaderException
    {
        File mapFile = getReportFile( REPORTTYPE_MAP, 0, 0 );

        if ( mapFile.exists() )
        {
            planets.loadMapFile( mapFile );
        }
        else
        {
            throw new ReportLoaderException( "Map file not found", mapFile.getName() );
        }
    }


    /**
     *  Loads all Stars! reports for the game
     *
     *@author                         Steve Leach
     *@throws  ReportLoaderException
     */
    public void loadReports()
        throws ReportLoaderException
    {
        loadMapFile();

        for ( int y = 2400; y <= getYear(); y++ )
        {
            for ( int n = 1; n <= MAX_RACES; n++ )
            {
                loadReportFile( REPORTTYPE_PLANET, n, y );
                loadReportFile( REPORTTYPE_FLEET, n, y );
            }
        }
    }


    /**
     *@param  reportType                 Description of the Parameter
     *@param  player                     Description of the Parameter
     *@param  year                       Description of the Parameter
     *@exception  ReportLoaderException  Description of the Exception
     */
    private void loadReportFile( int reportType, int player, int year )
        throws ReportLoaderException
    {
        File reportFile = getReportFile( reportType, player, year );

        if ( ( reportFile != null ) && reportFile.exists() )
        {
            switch ( reportType )
            {
                case REPORTTYPE_PLANET:
                    planets.loadPlanetReport( reportFile, year );
                    break;
                case REPORTTYPE_FLEET:
                    fleets.loadFleetReport( reportFile, year );
                    break;
            }

        }
    }


    /**
     *  Returns the color associated with the race for display on the map, etc.
     *
     *@param  raceName  Description of the Parameter
     *@return           The raceColor value
     *@author           Steve Leach
     */
    public Color getRaceColor( String raceName )
    {
    	if( Utils.empty(raceName))
    	{
    		return Color.WHITE;
    	}
    	else
    	{
    	Race race = getRace( raceName, true );
    	return race.getColor();
    	}
    }


    /**
     *  Returns a race object matching the specified race (not player) name. <p>
     *
     *  If no matching race is found and create == true then a new race is
     *  created and added to the list.
     *
     *@param  raceName  Description of the Parameter
     *@param  create    Description of the Parameter
     *@return           The race value
     *@author           Steve Leach
     */
    public Race getRace( String raceName, boolean create )
    {
        // First, try and find the race in the list
        for ( int n = 0; n < races.size(); n++ )
        {
            Race race = ( Race ) races.get( n );

            if ( race.equals( raceName ) )
            {
                return race;
            }
        }

        // Next, if it has not been found then create it (if this is what is requested)
        if ( create )
        {
            Race newRace = new Race( this );
            newRace.setRaceName( raceName );
            races.add( newRace );
            return newRace;
        }

        // Otherwise, return null
        return null;
    }


    /**
     *  Updates all the properties for this game
     *
     *@param  props  The new properties value
     */
    public void setProperties( Properties props )
    {
        String playerNumbers = "";

        for ( int i = 0; i < players.size(); i++ )
        {
            Player player = ( Player ) players.get( i );

            playerNumbers += player.getId();

            if ( i < players.size() - 1 )
            {
                playerNumbers += ",";
            }

            player.setProperties( props );
        }

        props.setProperty( name + ".RaceCount", "" + races.size() );

        String raceNames = "";

        for ( int n = 0; n < races.size(); n++ )
        {
            Race race = ( Race ) races.get( n );

            if ( n > 0 )
            {
                raceNames += ",";
            }
            raceNames += race.getRaceName().replaceAll( " ", "_" );

            race.setProperties( props );
        }

        props.setProperty( name + ".RaceNames", raceNames );
        props.setProperty( name + ".sahHosted", sahHosted );
        props.setProperty( name + ".GameDir", directory );
        props.setProperty( name + ".PlayerNumbers", playerNumbers );
    }


    /**
     * Sets the game's internal state based on the specified properties list
     */
    public void parseProperties( Properties props )
    {
        String[] races = props.getProperty( name + ".RaceNames" ).split( "," );

        for ( int n = 0; n < races.length; n++ )
        {
            races[n] = races[n].replaceAll( "_", " " );

            Race race = getRace( races[n], true );

            race.getProperties( props );
        }
        
        setSahHosted( props.getProperty( name + ".sahHosted" ) );
    }


    /**
     *  Returns an iterator to iterate over all the known races in the game
     *
     *@return    The races value
     *@author    Steve Leach
     */
    public Iterator getRaces()
    {
        return races.iterator();
    }


    /**
     *  Returns true if player action is required for this game
     *
     *@return    Description of the Return Value
     *@author    Steve Leach
     */
    public boolean actionRequired()
    {
        boolean actionRequired = false;

        Player[] players = getPlayers();

        for ( int m = 0; m < players.length; m++ )
        {
            if ( players[m].actionRequired() )
            {
                actionRequired = true;
            }
        }
        return actionRequired;
    }


    /**
     *  Gets the userDefinedPropertiesFile attribute of the Game object
     *
     *@return    The userDefinedPropertiesFile value
     */
    private File getUserDefinedPropertiesFile()
    {
        return new File( directory + File.separator + name + ".userprops" );
    }


    /**
     *  Loads user defined properties from the game's user properties file <p>
     *
     *  Also sets up any ship designs that are included in the properties file.
     *
     *@author    Steve Leach
     */
    public void loadUserDefinedProperties()
    {
        File userPropertiesFile = getUserDefinedPropertiesFile();

        if ( userPropertiesFile.exists() )
        {
            try
            {
                FileInputStream s = new FileInputStream( userPropertiesFile );
                userDefinedProperties.load( s );
            }
            catch ( FileNotFoundException e )
            {
                // This should never happen as we have already checked that the file exists
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }

        }

        setupShipDesigns( userDefinedProperties );
    }


    /**
     *  Adds any ship designs that are stored in the specified properties list
     *
     *@param  props  Description of the Parameter
     *@author        Steve Leach
     */
    private void setupShipDesigns( Properties props )
    {
        int designCount = Utils.safeParseInt( props.getProperty( "ShipDesigns.count" ), 0 );

        for ( int n = 0; n < designCount; n++ )
        {
            ShipDesign design = new ShipDesign();
            design.loadProperties( props, n + 1 );
            shipDesigns.addDesign( design );
        }
    }


    /**
     *  Support function for use by Planet, Fleet and Race classes
     *
     *@param  propertyName  Description of the Parameter
     *@return               The userDefinedProperty value
     */
    String getUserDefinedProperty( String propertyName )
    {
        return userDefinedProperties.getProperty( propertyName );
    }


    /**
     *  Support function for use by Planet, Fleet and Race classes
     *
     *@param  propertyName  The new userDefinedProperty value
     *@param  value         The new userDefinedProperty value
     */
    void setUserDefinedProperty( String propertyName, String value )
    {
        userDefinedProperties.setProperty( propertyName, value );
    }


    /**
     *  Saves all user defined properties to the game's user properties file
     *
     *@author    Steve Leach
     */
    public void saveUserDefinedProperties()
    {
        getShipDesignProperties( userDefinedProperties );
        
        /* Not sure yet
         * Theory-- Causes the ShipDesigns to be saved into the userdefs...
         * 
         */

        File userPropertiesFile = getUserDefinedPropertiesFile();
        
        /* the var userPropertiesFile to the File type
         * This will contain the file name the data will be put in
         * 
         *getUserDefinedPropertiesFile() function determines the file
         *name that the game settings will be saved in.
         *
         *Therefore
         *
         *File <Var> "userProp..." = <value> (Function called -- "getUser...")
         *
         *This <Var> will be used for the FSObject
         */

        try
        {
            FileOutputStream s = new FileOutputStream( userPropertiesFile );
            
            /* Opens a stream to the file
             * 
             */

            userDefinedProperties.store( s, "User defined properties for " + name );
            
            /* Stores all Settings into the file from the <var> userDefinedProperties
             * The First line will be User defined properties for " + name 
             * 's' is the Stream to Use
             * store must be a FSO causing the data in the userDef... <var> to be saved in 's'
             * 
             */
        }
        catch ( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }


    /**
     *@param  properties  Description of the Parameter
     */
    private void getShipDesignProperties( Properties properties )
    {
        properties.setProperty( "ShipDesigns.count", "" + shipDesigns.getDesignCount() );

        for ( int n = 0; n < shipDesigns.getDesignCount(); n++ )
        {
            ShipDesign design = shipDesigns.getDesign( n );
            design.storeProperties( properties, n + 1 );
        }
    }


    /**
     *  Returns details of the specified fleet in the specified year
     *
     *@param  year   Description of the Parameter
     *@param  index  Description of the Parameter
     *@return        The fleet value
     *@author        Steve Leach
     */
    public Fleet getFleet( int year, int index )
    {
        return fleets.getFleet( year, index );
    }


    /**
     *  Returns details of the specified fleet in the current year
     *
     *@param  index  Description of the Parameter
     *@return        The fleet value
     *@author        Steve Leach
     */
    public Fleet getFleet( int index )
    {
        return fleets.getFleet( index );
    }


    /**
     *  Gets the fleetByID attribute of the Game object
     *
     *@param  year   Description of the Parameter
     *@param  owner  Description of the Parameter
     *@param  id     Description of the Parameter
     *@return        The fleetByID value
     */
    public Fleet getFleetByID( int year, String owner, int id )
    {
        return fleets.getFleetByID( year, owner, id );
    }


    /**
     *  Returns the number of fleets known in the specified year
     *
     *@param  year  Description of the Parameter
     *@return       The fleetCount value
     *@author       Steve Leach
     */
    public int getFleetCount( int year )
    {
        return fleets.getFleetCount( year );
    }


    /**
     *  Returns the number of fleets known in the current year
     *
     *@return    The fleetCount value
     *@author    Steve Leach
     */
    public int getFleetCount()
    {
        return fleets.getFleetCount( getYear() );
    }


    /**
     *  Returns the number of races in the game
     *
     *@return    The raceCount value
     */
    public int getRaceCount()
    {
        return races.size();
    }


    /**
     *  Finds the closes planet to the given position. <p>
     *
     *  No planet over the specified threshold distance will be considered.
     *
     *@param  mapPos     Description of the Parameter
     *@param  threshold  Description of the Parameter
     *@return            Description of the Return Value
     */
    public Planet findClosestPlanet( Point mapPos, int threshold )
    {
        return planets.findClosestPlanet( mapPos, threshold );
    }


    /**
     *  Gets the sahHosted attribute of the Game object
     *
     *@return    The sahHosted value
     */
    public String getSahHosted()
    {
        return sahHosted;
    }


    /**
     *  Sets the sahHosted attribute of the Game object
     *
     *@param  sahHosted  The new sahHosted value
     */
    public void setSahHosted( String sahHosted )
    {
        this.sahHosted = sahHosted;
                
        boolean isSAHcontroller = (controller instanceof AutoHostGameController);
        
        if (isAutohosted() != isSAHcontroller)
        {
           initController();
        }
    }


    /**
     *  Adds a feature to the ShipDesign attribute of the Game object
     *
     *@param  design  The feature to be added to the ShipDesign attribute
     */
    public void addShipDesign( ShipDesign design )
    {
        shipDesigns.addDesign( design );
    }


    /**
     * Gets the specified ship design.
     * <p>
     * @param index is the number of the ship design to retrieve, from 0 to getShipDesignCount()-1
     */
    public ShipDesign getShipDesign( int index )
    {
        return shipDesigns.getDesign( index );
    }


    /**
     *  Gets the shipDesignCount attribute of the Game object
     *
     *@return    The shipDesignCount value
     */
    public int getShipDesignCount()
    {
        return shipDesigns.getDesignCount();
    }


    /**
     *  Gets the ahStatus attribute of the Game object
     *
     *@return    The ahStatus value
     */
    public Properties getAhStatus()
    {
        return ahStatus;
    }

    /**
     *  Gets the pollInterval attribute of the Game object
     *
     *@return    The pollInterval value - number of milliseconds between polling
     */
    public int getPollInterval()
    {
        return controller.getPollInterval();
    }

   /**
    */
   public boolean isAutohosted()
   {
      return "true".equals( getSahHosted() );
   }


   /**
    * Returns true if game turns can be generated locally
    */
   public boolean canGenerate()
   {
      if (controller instanceof GameTurnGenerator)
      {
         return true;
      }

      return false;
   }

   /**
    * Generate the next game turn
    */
   public void generateNextTurn() throws TurnGenerationError
   {
      if (controller instanceof GameTurnGenerator)
      {
          GameTurnGenerator gen = (GameTurnGenerator)controller;

          if (gen.readyToGenerate())
          {
             gen.generateTurns(1);
          }
          else
          {
             throw new TurnGenerationError( "Not ready to generate" );
          }
      }
   }
   
   /**
    * Registers an object to receive notifications when details of the game change 
    */
   public void addUpdateListener( GameUpdateListener listener )
   {
      updateListeners.add( listener );
   }
   
   /**
    * Notifies all registered listeners that an update to the game data has occurred
    * <p>
    * Designed to be called from User Interface code 
    */
   public void notifyUpdateListeners( Object updatedObject, String propertyName, Object oldValue, Object newValue )
   {
      GameUpdateNotification notification = new GameUpdateNotification( this, updatedObject, propertyName, newValue, oldValue );
      
      for (int n = 0; n < updateListeners.size(); n++)
      {
         // Do the notification inside a try block because we cannot trust the listener code
         try
         {
            GameUpdateListener listener = (GameUpdateListener)updateListeners.get(n);
            listener.processGameUpdate( notification );
         }
         catch (Throwable t)
         {
            Log.log( Log.ERROR, updateListeners.get(n), "Error notifying game update listener" );
         }
      }
   }
   
   /**
    * Notifies registered update listeners that the specified object has been updated
    * <p>
    * The listeners are not informed what property of the object has changed, or what the
    * new and old values are.
    * <p> 
    * Designed to be called from User Interface code 
    */
   public void notifyUpdateListeners( Object updatedObject )
   {
      notifyUpdateListeners( updatedObject, null, null, null );
   }


/**
 * @param currentDesign
 */
   public void removeShipDesign(ShipDesign Design) 
   {
   	shipDesigns.removeShipDesign(Design);
   }
   
   public Properties getUserDefinedProperties()
   {
      return userDefinedProperties;
   }
}

