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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Properties;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public class Game extends Object
{
    Properties ahStatus;
    String directory;
    String name;
    PropertyChangeSupport pcs = new PropertyChangeSupport( new Object() );
    ArrayList players;
    private PlanetList planets = new PlanetList();


    /**
     *  Constructor for the Game object
     */
    public Game()
    {
        name = "";
        players = new ArrayList();
        directory = "";
    }


    /**
     *  Constructor for the Game object
     *
     *@param  shortName  Description of the Parameter
     *@param  directory  Description of the Parameter
     */
    public Game( String shortName, String directory )
    {
        name = shortName.toString();
        players = new ArrayList();
        this.directory = directory;
        loadProperties();
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
        return ahStatus.getProperty( "game-year" );
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
        return ahStatus.getProperty( "game-year" );
    }


    /**
     *  Gets the longName attribute of the Game object
     *
     *@return    The longName value
     */
    public String getLongName()
    {
        if ( ahStatus == null )
        {
            loadProperties();
        }
        return ahStatus.getProperty( "game-name" );
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
        return ahStatus.getProperty( "next-gen-time" );
    }


    /**
     *  Gets the playerRaceName attribute of the Game object
     *
     *@param  id  Description of the Parameter
     *@return     The playerRaceName value
     */
    public String getPlayerRaceName( String id )
    {
        return ahStatus.getProperty( "player" + id + "-race" );
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
        StringBuffer in = new StringBuffer();
        StringBuffer out = new StringBuffer();
        StringBuffer dead = new StringBuffer();
        String ret;
        for ( int i = 1; i <= 16; i++ )
        {
            ret = ahStatus.getProperty( "player" + i + "-turn" );
            if ( ret == null )
            {
                continue;
            }
            if ( ret.equals( "waiting" ) )
            {
                if ( out.length() != 0 )
                {
                    out.append( ", " );
                }
                out.append( ahStatus.getProperty( "player" + i + "-race" ) );
            }
            else if ( ret.startsWith( "skipped" ) )
            {
                if ( out.length() != 0 )
                {
                    out.append( ", " );
                }
                out.append( ahStatus.getProperty( "player" + i + "-race" ) );
                out.append( " ( skipped " );
                out.append( ret.substring( 8 ) );
                out.append( " )" );
            }
            else if ( ret.equals( "inactive" ) || ret.startsWith( "dead" ) )
            {
                if ( dead.length() != 0 )
                {
                    dead.append( ", " );
                }
                dead.append( ahStatus.getProperty( "player" + i + "-race" ) );
            }
            else if ( ret.startsWith( "in" ) )
            {
                if ( in.length() != 0 )
                {
                    in.append( ", " );
                }
                in.append( ahStatus.getProperty( "player" + i + "-race" ) );
            }
        }
        Properties stati = new Properties();
        stati.setProperty( "in", String.valueOf( in ) );
        stati.setProperty( "out", String.valueOf( out ) );
        stati.setProperty( "dead", String.valueOf( dead ) );
        return stati;
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
        return ahStatus.getProperty( "status" );
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
    }


    /**
     *  Description of the Method
     */
    public void loadProperties()
    {
        ahStatus = new Properties();
        try
        {
            File statusFile = new File( directory, getStatusFileName() );
            if ( !statusFile.exists() )
            {
                poll();
                //return;
            }
            InputStream in = new FileInputStream( statusFile );
            ahStatus.load( in );
            pcs.firePropertyChange( "gameStatus", 0, 1 );
        }
        catch ( Exception e )
        {
            Log.log( Log.ERROR, this, e );
        }
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public boolean poll()
    {
        /*
         *  get status file from AH
         */
        try
        {
            Utils.getFileFromAutohost( name, getStatusFileName(), directory );
        }
        catch ( Exception e )
        {
            Log.log( Log.ERROR, this, e );
            return false;
        }
        /*
         *  load new status into game  GUI should be updated by reloading ahStatus
         */
        loadProperties();
        return true;
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
        try
        {
            writeProperties( out );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        ret.append( out.toString() );
        return ret.toString();
    }


    /**
     *  Description of the Method
     *
     *@param  out              Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    public void writeProperties( Writer out )
        throws IOException
    {
        String lineEnding = System.getProperty( "line.separator" );
        String playerNumbers = "";
        for ( int i = 0; i < players.size(); i++ )
        {
            playerNumbers += ( ( Player ) players.get( i ) ).getId();
            if ( i < players.size() - 1 )
            {
                playerNumbers += ",";
            }
            ( ( Player ) players.get( i ) ).writeProperties( out );
        }
        out.write( name + ".GameDir=" + directory + lineEnding );
        out.write( name + ".PlayerNumbers=" + playerNumbers + lineEnding );
    }


    /**
     *  Sets the playerTurnStatus attribute of the Game object
     *
     *@param  playerNum  The new playerTurnStatus value
     *@param  value      The new playerTurnStatus value
     */
    public void setPlayerTurnStatus( int playerNum, String value )
    {
        ahStatus.setProperty( "player" + playerNum + "-turn", value );
    }


    /**
     *  Gets the year attribute of the Game object
     *
     *@return    The year value
     */
    public int getYear()
    {
        return Integer.parseInt( getCurrentYear() );
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
     *  Description of the Method
     *
     *@exception  ReportLoaderException  Description of the Exception
     */
    public void loadMapFile() throws ReportLoaderException
    {
       File mapFile = getReportFile( REPORTTYPE_MAP, 0, 0 );       
       
       if (mapFile.exists())
       {
          planets.loadMapFile( mapFile );
       }
       else
       {
          throw new ReportLoaderException( "Map file not found", null  );
       }       
    }
    
    public void loadReports() throws ReportLoaderException
    {
       loadMapFile();
       
       for (int n = 1; n <= 16; n++)
       {
          loadReportFile( REPORTTYPE_PLANET, n, getYear() );
          loadReportFile( REPORTTYPE_FLEET, n, getYear() );
       }
    }


   /**
    */
   private void loadReportFile(int reportType, int player, int year) throws ReportLoaderException
   {
      File reportFile = getReportFile( reportType, player, year );
            
      if ((reportFile != null) && reportFile.exists())
      {
         planets.loadPlanetReport( reportFile, year );
      }
   }
   
   public Color getPlayerColor( String playerName )
   {
      for (int n = 0; n < players.size(); n++)
      {
         Player player = (Player)players.get(n);
         
         if (player.getRaceName().equals(playerName))
         {
            return player.getColor();
         }
      }
      
      return Color.MAGENTA;		// TODO: throw an error here instead
   }
}

