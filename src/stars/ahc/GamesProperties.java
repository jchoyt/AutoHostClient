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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Properties;

import stars.ahcgui.AhcGui;
import stars.ahcgui.pluginmanager.ConfigurablePlugIn;
import stars.ahcgui.pluginmanager.PlugIn;
import stars.ahcgui.pluginmanager.PlugInManager;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public class GamesProperties
{
    /**
     *  Description of the Field
     */
    public static String AUTOHOST = "ftp://starsautohost.org/starout/";
    public static boolean UPTODATE = false;
    /**
     *  Description of the Field
     */
    private static Game currentGame;
    private static ArrayList games = new ArrayList();
    private static ArrayList plugins = new ArrayList();
    private static boolean initiated = false;
    private static String lineEnding = System.getProperty( "line.separator" );
    private static Properties props;
    private static String propsFile;
    //private static String proxyHost;
    //private static String proxyPort;
    //private static String starsExecutable;
    private static PropertyChangeSupport pcs = new PropertyChangeSupport( new Object() );

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
        props.setProperty( "currentGame", currentGame.name );
        currentGame.setProperties( props );
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
                setCurrentGame( game );
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
       props.setProperty( "ProxyHost", proxyHost );
    }


    /**
     *  Sets the proxyPort attribute of the GamesProperties object
     *
     *@param  proxyPort  The new proxyPort value
     */
    public static void setProxyPort( String proxyPort )
    {
        props.setProperty( "ProxyPort", proxyPort );
    }


    /**
     *  Sets the starsExecutable attribute of the GamesProperties object
     *
     *@param  starsExecutable  The new starsExecutable value
     */
    public static void setStarsExecutable( String starsExecutable )
    {
       props.setProperty( "StarsExecutable", starsExecutable );
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
        return props.getProperty( "ProxyHost" );
    }


    /**
     *  Gets the proxyPort attribute of the GamesProperties object
     *
     *@return    The proxyPort value
     */
    public static String getProxyPort()
    {
        return props.getProperty("ProxyPort");
    }


    /**
     *  Gets the starsExecutable attribute of the GamesProperties object
     *
     *@return    The starsExecutable value
     */
    public static String getStarsExecutable()
    {
        return props.getProperty("StarsExecutable");
    }


    /**
     *  Adds a feature to the Game attribute of the GamesProperties class
     *
     *@param  newGame  The feature to be added to the Game attribute
     */
    public static void addGame( Game newGame )
    {
        games.add( newGame );
        newGame.setProperties( props );
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
           initProperties();
           
            if ( propsLocation == null )
            {
                propsLocation = "ahclient.props";
            }
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
            String proxyHost = props.getProperty( "ProxyHost" );
            if ( proxyHost != null )
            {
                String proxyPort = props.getProperty( "ProxyPort" );
                System.getProperties().put( "http.proxyHost", proxyHost );
                System.getProperties().put( "http.proxyPort", proxyPort );
            }
            /*
             *  Games Properties info
             */
            String starsExecutable = props.getProperty( "StarsExecutable" );
            if ( starsExecutable != "" )
            {
                Utils.setStarsExecutable( new File( starsExecutable ) );
            }
            /*
             *  set up the games
             */
            String gameList = props.getProperty( "Games" );
            if ( Utils.empty(gameList) == false )
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
                    if (playerList != null)
                    {
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
                    }
                    
                    currentGame.loadGameProperties( props );
                    
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
     * Ensures that the properties reflect the current status of all the games
     */
    private static void refreshGamesProperties()
    {
       String gameNames = "";
       
       for ( int i = 0; i < games.size(); i++ )
       {
          Game game = (Game)games.get(i);
       
          if (Utils.empty(gameNames) == false)
          {
             gameNames += ",";
          }
          gameNames += game.name;
          
          game.setProperties( props );
          
          game.saveUserDefinedProperties();
       }
       
       setProperty( "Games", gameNames );
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
            //FileWriter out = new FileWriter( propsFile );
            //writeProperties( out );
            //out.close();
           
           refreshGamesProperties();
           saveAllPluginSettings();
           //refreshPluginConfig();
           
           FileOutputStream fos = new FileOutputStream( propsFile );
           props.store( fos, "AutoHostClient properties" );
           fos.close();
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
//       
//        /*
//         *  GamesProperties stuff
//         */
//        out.write( "StarsExecutable=" + starsExecutable + lineEnding );
//        if ( proxyHost != null )
//        {
//            out.write( "ProxyHost=" + proxyHost + lineEnding );
//            out.write( "ProxyPort=" + proxyPort + lineEnding );
//        }
//        /*
//         *  write the properties for each game
//         */
//        if ( games != null && games.size() > 0 )
//        {
//            String gameNames = "";
//            for ( int i = 0; i < games.size(); i++ )
//            {
//                gameNames += ( ( Game ) games.get( i ) ).getName();
//                if ( i < games.size() - 1 )
//                {
//                    gameNames += ",";
//                }
//                ( ( Game ) games.get( i ) ).writeProperties( out );
//            }
//            out.write( "Games=" + gameNames + lineEnding );
//        }
//        
    }

    /**
     * Sets an ad-hoc property 
     */
    public static void setProperty( String name, Object value )
    {
       props.setProperty( name, value.toString() );
    }
    public static void setProperty( String name, int value )
    {
       props.setProperty( name, ""+value );
    }
    public static void setProperty( String name, boolean value )
    {
       props.setProperty( name, value ? "true" : "false" );
    }
    
    public static int getIntProperty( String key, int defaultValue )
    {
       String s = props.getProperty( key );
       return Utils.safeParseInt( s, defaultValue );
    }
    
    public static Properties getProperties()
    {
       return props;
    }
    
    /**
     * Register a configurable plugin.
     * <p>
     * Will also load any saved configuration for the plugin.
     * 
     * @deprecated - this is no longer required
     * @author Steve Leach 
     */
    public static void registerConfigurablePlugin( ConfigurablePlugIn plugin )
    {
       plugin.loadConfiguration( props );
       
       plugins.add( plugin );
    }
    
    /**
     * Tells each registered plugin to save it's properties
     * 
     * @deprecated - saveAllPluginSettings() is the new way of doing this
     * @author Steve Leach
     */
    private static void refreshPluginConfig()
    {
       for (int n = 0; n < plugins.size(); n++)
       {
          ConfigurablePlugIn plugin = (ConfigurablePlugIn)plugins.get(n);
          plugin.saveConfiguration( props );
       }
    }

   /**
    * 
    */
   public static void initProperties()
   {
      props = new Properties();
   }

   /**
    * Returns true if all player turns are in for all games.
    */
   public static boolean actionRequired()
   {
      boolean actionRequired = false;
      
      for (int n = 0; n < games.size(); n++)
      {
         Game game = (Game)games.get(n);
       
         if (game.actionRequired())
         {
            actionRequired = true;
         }
      }
      
      return actionRequired;
   }

   /**
    * @param string
    * @return
    */
   public static String getProperty(String key)
   {
      return props.getProperty(key);
   }
   
   /**
    * Save configuration of all plugin instances 
    */
   public static void saveAllPluginSettings()
   {
      PlugIn[] plugins = PlugInManager.getPluginManager().getPluginInstances();
      
      for (int n = 0; n < plugins.length; n++)
      {
         if (plugins[n] instanceof ConfigurablePlugIn)
         {
            ((ConfigurablePlugIn)plugins[n]).saveConfiguration( props );
         }
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

