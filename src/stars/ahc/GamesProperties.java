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
import java.beans.*;
import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Properties;
import stars.ahc.Game;
import stars.ahc.Player;

import stars.ahcgui.AhcGui;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public class GamesProperties extends Object
{
    /**
     *  Description of the Field
     */
    public static String AUTOHOST = "ftp://library.southern.edu/starout/";
    public static boolean UPTODATE = false;
    /**
     *  Description of the Field
     */
    //public static String AUTOHOST = "http://library.southern.edu/stars/starout/";
    //static GamesPropsChangeListener changeListener = new GamesPropsChangeListener();
    static Game currentGame;
    static ArrayList games = new ArrayList();
    static boolean initiated = false;
    static String lineEnding = System.getProperty( "line.separator" );
    static Properties props;
    static String propsFile;
    static String proxyHost;
    static String proxyPort;
    static String starsExecutable;
    static PropertyChangeSupport pcs = new PropertyChangeSupport( new Object() );


    /**
     *  Constructor for the GamesProperties object
     */
    public GamesProperties() { }

    /**
     *  Adds a feature to the PropertyChangeListener attribute of the Player
     *  object
     *
     *@param  listener  The feature to be added to the PropertyChangeListener
     *      attribute
     */
    public static void addPropertyChangeListener( PropertyChangeListener listener )
    {
        pcs.addPropertyChangeListener( listener );
    }
    /**
     *  Sets the changeListener attribute of the GamesProperties class
     *
     *@param  _currentGame  The new currentGame value
     */
    /*
     *  public static void setChangeListener( GamesPropsChangeListener _changeListener )
     *  {
     *  changeListener = _changeListener;
     *  }
     */
    /**
     *  Sets the currentGame attribute of the GamesProperties object
     *
     *@param  _currentGame  The new currentGame value
     */
    public static void setCurrentGame( Game _currentGame )
    {
        currentGame = _currentGame;
    }


    /**
     *  Sets the currentGame attribute of the GamesProperties object
     *
     *@param  gameName  The new currentGame value
     */
    public static void setCurrentGame( String gameName )
    {
        Game game;
        for ( int i = 0; i < games.size(); i++ )
        {
            game = ( Game ) games.get( i );
            if ( game.getName().equals( gameName ) )
            {
                currentGame = game;
                break;
            }
        }
    }


    /**
     *  Sets the props attribute of the GamesProperties object
     *
     *@param  _props  The new props value
     */
    public static void setProps( Properties _props )
    {
        props = _props;
    }


    /**
     *  Sets the propsFile attribute of the GamesProperties object
     *
     *@param  _propsFile  The new propsFile value
     */
    public static void setPropsFile( String _propsFile )
    {
        propsFile = _propsFile;
    }


    /**
     *  Sets the proxy attribute of the GamesProperties object
     */
    public static void setProxy()
    {
        if ( props.getProperty( "ProxyHost" ) != null )
        {
            System.getProperties().put( "http.proxyHost", props.getProperty( "ProxyHost" ) );
            System.getProperties().put( "http.proxyPort", props.getProperty( "ProxyPort" ) );
        }
    }


    /**
     *  Sets the proxyHost attribute of the GamesProperties object
     *
     *@param  proxyHost  The new proxyHost value
     */
    public static void setProxyHost( String proxyHost )
    {
        GamesProperties.proxyHost = proxyHost;
    }


    /**
     *  Sets the proxyPort attribute of the GamesProperties object
     *
     *@param  proxyPort  The new proxyPort value
     */
    public static void setProxyPort( String proxyPort )
    {
        GamesProperties.proxyPort = proxyPort;
    }


    /**
     *  Sets the starsExecutable attribute of the GamesProperties object
     *
     *@param  starsExecutable  The new starsExecutable value
     */
    public static void setStarsExecutable( String starsExecutable )
    {
        GamesProperties.starsExecutable = starsExecutable;
    }


    /**
     *  Gets the changeListener attribute of the GamesProperties class
     *
     *@return    The changeListener value
     */
    /*
     *  public static GamesPropsChangeListener getChangeListener()
     *  {
     *  return changeListener;
     *  }
     */
    /**
     *  Gets the currentGame attribute of the GamesProperties object
     *
     *@return    The currentGame value
     */
    public static Game getCurrentGame()
    {
        return currentGame;
    }


    /**
     *  Gets the currentPlayers attribute of the GamesProperties class
     *
     *@return    The currentPlayers value
     */
    public static Player[] getCurrentPlayers()
    {
        return currentGame.getPlayers();
    }


    /**
     *  Gets the games attribute of the GamesProperties object
     *
     *@return    The games value
     */
    public static Game[] getGames()
    {
        if ( games == null || games.size() == 0 )
        {
            return new Game[0];
        }
        Object[] gamesArray = games.toArray();
        Game[] ar = new Game[gamesArray.length];
        for ( int i = 0; i < ar.length; i++ )
        {
            ar[i] = ( Game ) gamesArray[i];
        }
        return ar;
    }


    /**
     *  Gets the initiated attribute of the GamesProperties class
     *
     *@return    The initiated value
     */
    public static boolean getInitiated()
    {
        return initiated;
    }


    /**
     *  Gets the propsFile attribute of the GamesProperties object
     *
     *@return    The propsFile value
     */
    public static String getPropsFile()
    {
        return propsFile;
    }


    /**
     *  Gets the proxyHost attribute of the GamesProperties object
     *
     *@return    The proxyHost value
     */
    public static String getProxyHost()
    {
        return proxyHost;
    }


    /**
     *  Gets the proxyPort attribute of the GamesProperties object
     *
     *@return    The proxyPort value
     */
    public static String getProxyPort()
    {
        return proxyPort;
    }


    /**
     *  Gets the starsExecutable attribute of the GamesProperties object
     *
     *@return    The starsExecutable value
     */
    public static String getStarsExecutable()
    {
        return starsExecutable;
    }


    /**
     *  Adds a feature to the Game attribute of the GamesProperties class
     *
     *@param  newGame  The feature to be added to the Game attribute
     */
    public static void addGame( Game newGame )
    {
        games.add( newGame );
        writeProperties();
    }

    public static void removeGame(Game game)
    {
        games.remove(game);
        pcs.firePropertyChange("game removed", game, null);
        writeProperties();
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public static String dumpToString()
    {
        StringBuffer ret = new StringBuffer();
        ret.append( "CurrentGame=" + currentGame + lineEnding );
        ret.append( "initialized=" + initiated + lineEnding );
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
     *@param  propsLocation  Description of the Parameter
     */
    public static void init( String propsLocation )
    {
        propsFile = propsLocation;
        loadProps( propsLocation );
        parseProps();
        initiated = true;
    }


    /**
     *  Description of the Method
     *
     *@param  propsLocation  Description of the Parameter
     */
    public static void loadProps( String propsLocation )
    {
        try
        {
            if ( propsLocation == null )
            {
                propsLocation = "ahclient.props";
            }
            props = new Properties();
            InputStream in = new FileInputStream( propsLocation );
            props.load( in );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    /**
     *  Description of the Method
     */
    public static void parseProps()
    {
        try
        {
            /*
             *  proxy info
             */
            proxyHost = props.getProperty( "ProxyHost" );
            if ( proxyHost != null )
            {
                proxyPort = props.getProperty( "ProxyPort" );
                System.getProperties().put( "http.proxyHost", proxyHost );
                System.getProperties().put( "http.proxyPort", proxyPort );
            }
            /*
             *  Games Properties info
             */
            starsExecutable = props.getProperty( "StarsExecutable" );
            if ( starsExecutable != "" )
            {
                Utils.setStarsExecutable( new File( starsExecutable ) );
            }
            /*
             *  set up the games
             */
            String gameList = props.getProperty( "Games" );
            if ( gameList != null )
            {
                String[] gameNames = gameList.split( "[, ]" );
                games = new ArrayList();
                for ( int i = 0; i < gameNames.length; i++ )
                {
                    String gameDirectory = props.getProperty( gameNames[i] + ".GameDir" );
                    currentGame = new Game( gameNames[i], gameDirectory );
                    currentGame.setDirectory( gameDirectory );
                    /*
                     *  for each game, set up the players
                     */
                    String playerList = props.getProperty( gameNames[i] + ".PlayerNumbers" );
                    String[] playerNumbers = playerList.split( "[, ]" );
                    Player[] players = new Player[playerNumbers.length];
                    for ( int j = 0; j < playerNumbers.length; j++ )
                    {
                        if ( playerNumbers[j].equals( "" ) )
                        {
                            continue;
                        }
                        Player newPlayer = new Player();
                        newPlayer.setGame( currentGame );
                        newPlayer.setStarsPassword( props.getProperty( gameNames[i] + ".player" + playerNumbers[j] + ".StarsPassword" ) );
                        newPlayer.setUploadPassword( props.getProperty( gameNames[i] + ".player" + playerNumbers[j] + ".UploadPassword" ) );
                        newPlayer.setId( playerNumbers[j] );
                        newPlayer.setToUpload( Boolean.valueOf( props.getProperty( gameNames[i] + ".player" + playerNumbers[j] + ".upload" ) ).booleanValue() );
                        newPlayer.setLastUpload( Long.valueOf( props.getProperty( gameNames[i] + ".player" + playerNumbers[j] + ".lastUpload" ) ).longValue() );
                        players[j] = newPlayer;
                    }
                    currentGame.setPlayers( players );
                    games.add( currentGame );
                }
                if ( gameNames.length == 1 )
                {
                    setCurrentGame( ( Game ) games.get( 0 ) );
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    /**
     *  Saves the properties to disk
     */
    public static void writeProperties()
    {
        /*
         *  set up out
         */
        try
        {
            FileWriter out = new FileWriter( propsFile );
            writeProperties( out );
            out.close();
        }
        catch ( Exception e )
        {
            Log.log( Log.MESSAGE, GamesProperties.class, e );
            AhcGui.setStatus( "Couldn't save configuration changes" );
        }
    }


    /**
     *  Description of the Method
     *
     *@param  out              Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    public static void writeProperties( Writer out )
        throws IOException
    {
        /*
         *  GamesProperties stuff
         */
        out.write( "StarsExecutable=" + starsExecutable + lineEnding );
        if ( proxyHost != null )
        {
            out.write( "ProxyHost=" + proxyHost + lineEnding );
            out.write( "ProxyPort=" + proxyPort + lineEnding );
        }
        /*
         *  write the properties for each game
         */
        if ( games != null && games.size() > 0 )
        {
            String gameNames = "";
            for ( int i = 0; i < games.size(); i++ )
            {
                gameNames += ( ( Game ) games.get( i ) ).getName();
                if ( i < games.size() - 1 )
                {
                    gameNames += ",";
                }
                ( ( Game ) games.get( i ) ).writeProperties( out );
            }
            out.write( "Games=" + gameNames );
        }
    }
}

/*
 *  class GamesPropsChangeListener extends Object implements PropertyChangeListener
 *  {
 *  public void propertyChange( PropertyChangeEvent evt )
 *  {
 *  /Log.log( Log.MESSAGE, this, "propertyChange received: " + evt.toString() );
 *  try
 *  {
 *  GamesProperties.writeProperties();
 *  }
 *  catch ( Exception e )
 *  {
 *  Log.log( Log.WARNING, this, e );
 *  }
 *  }
 *  }
 */

