/*
 *  This file is part of Stars! Autohost Client
 *  Copyright (c) 2003 Jeffrey C. Hoyt
 *  This program is free software{return null;} you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation{return null;} either version 2
 *  of the License, or any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY{return null;} without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program{return null;} if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package stars.ahc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public class AutoHostGameController implements GameController
{
    private PropertyChangeSupport pcs = new PropertyChangeSupport( new Object() );
    protected Game game;


    /**
     *  Constructor for the GameController object
     *
     *@param  game  Description of the Parameter
     */
    public AutoHostGameController( Game game )
    {
        this.game = game;
    }


    /**
     *  Gets the currentYear attribute of the AutoHostGameController object
     *
     *@return    The currentYear value
     */
    public String getCurrentYear()
    {
        return game.getAhStatus().getProperty( "game-year" );
    }


    /**
     *  Gets the gameYear attribute of the AutoHostGameController object
     *
     *@return    The gameYear value
     */
    public String getGameYear()
    {
        return game.getAhStatus().getProperty( "game-year" );
    }


    /**
     *  Gets the longName attribute of the AutoHostGameController object
     *
     *@return    The longName value
     */
    public String getLongName()
    {
        if ( game.getAhStatus() == null )
        {
            game.loadProperties();
        }
        return game.getAhStatus().getProperty( "game-name" );
    }


    /**
     *  Gets the nextGen attribute of the AutoHostGameController object
     *
     *@return    The nextGen value
     */
    public String getNextGen()
    {
        return game.getAhStatus().getProperty( "next-gen-time" );
    }


    /**
     *  Gets the playerRaceName attribute of the AutoHostGameController object
     *
     *@param  id  Description of the Parameter
     *@return     The playerRaceName value
     */
    public String getPlayerRaceName( String id )
    {
        return game.getAhStatus().getProperty( "player" + id + "-race" );
    }


    /**
     *  Gets the playersByStatus attribute of the AutoHostGameController object
     *
     *@return    The playersByStatus value
     */
    public Properties getPlayersByStatus()
    {
        StringBuffer in = new StringBuffer();
        StringBuffer out = new StringBuffer();
        StringBuffer dead = new StringBuffer();
        int in_lines = 1;
        int out_lines = 1;
        int dead_lines = 1;
        final int COLS = 65;
        String ret;
        for ( int i = 1; i <= 16; i++ )
        {
            ret = game.getAhStatus().getProperty( "player" + i + "-turn" );
            if ( ret == null )
            {
                continue;
            }
            if ( ret.equals( "waiting" ) )
            {
                if ( out.length() != 0 )
                {
                    out.append( ", " );
                    if ( out.length() > COLS * out_lines )
                    {
                        out.append( "<br>" );
                        out_lines++;
                    }
                }
                out.append( game.getAhStatus().getProperty( "player" + i + "-race" ) );
            }
            else if ( ret.startsWith( "skipped" ) )
            {
                if ( out.length() != 0 )
                {
                    out.append( ", " );
                    if ( out.length() > COLS * out_lines )
                    {
                        out.append( "<br>" );
                        out_lines++;
                    }
                }
                out.append( game.getAhStatus().getProperty( "player" + i + "-race" ) );
                out.append( " ( skipped " );
                out.append( ret.substring( 8 ) );
                out.append( " )" );
            }
            else if ( ret.equals( "inactive" ) || ret.startsWith( "dead" ) )
            {
                if ( dead.length() != 0 )
                {
                    dead.append( ", " );
                    if ( dead.length() > COLS * dead_lines )
                    {
                        dead.append( "<br>" );
                        dead_lines++;
                    }
                }
                dead.append( game.getAhStatus().getProperty( "player" + i + "-race" ) );
            }
            else if ( ret.startsWith( "in" ) )
            {
                if ( in.length() != 0 )
                {
                    in.append( ", " );
                    if ( in.length() > COLS * in_lines )
                    {
                        in.append( "<br>" );
                        in_lines++;
                    }
                }
                in.append( game.getAhStatus().getProperty( "player" + i + "-race" ) );
            }
        }
        Properties stati = new Properties();
        stati.setProperty( "in", String.valueOf( in ) );
        stati.setProperty( "out", String.valueOf( out ) );
        stati.setProperty( "dead", String.valueOf( dead ) );
        return stati;
    }


    /**
     *  Gets the status attribute of the AutoHostGameController object
     *
     *@return    The status value
     */
    public String getStatus()
    {
        return game.getAhStatus().getProperty( "status" );
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public boolean poll()
    {
       Log.log( Log.NOTICE, this, "Polling autohost for " + game.getName() );
        /*
         *  get status file from AH
         */
        try
        {
           if (Utils.empty( game.getName() ) == false)
           {
              Utils.getFileFromAutohost( game.getName(), game.getStatusFileName(), game.getDirectory() );
           }
        }
        catch ( AutoHostError e )
        {
            Log.log( Log.ERROR, this, e );
            return false;
        }
        /*
         *  load new status into game  GUI should be updated by reloading game.getAhStatus()
         */
        // don't do this because loadProperties() may lead back to poll() - infinite recursion
        // SL, 4 Nov 2004
        //game.loadProperties();
        return true;
    }


    /**
     *  Sets the playerTurnStatus attribute of the AutoHostGameController object
     *
     *@param  playerNum  The new playerTurnStatus value
     *@param  value      The new playerTurnStatus value
     */
    public void setPlayerTurnStatus( int playerNum, String value )
    {
        game.getAhStatus().setProperty( "player" + playerNum + "-turn", value );
    }


    /**
     *  Adds a feature to the PropertyChangeListener attribute of the
     *  AutoHostGameController object
     *
     *@param  listener  The feature to be added to the PropertyChangeListener
     *      attribute
     */
    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        pcs.addPropertyChangeListener( listener );
    }


    public int getPollInterval()
    {
        /*
         *  The line below sets how often AHC checks AH for updated files.
         *  DO NOT MODIFY THIS NUMBER.  Ron has graciously agreed to allow me to write this
         *  to make our lives a little easier.  If you abuse this, I will request that Ron
         *  shut down access by this application.  I will NOT be responsible for messing up
         *  AutoHost.
         */
         return 10*60*1000;  //10 minutes
    }


   /* (non-Javadoc)
    * @see stars.ahc.GameController#getStatusProperties()
    */
    public void loadStatusProperties( Properties ahStatus )
    {
       try
       {
          File statusFile = new File( game.getDirectory(), game.getStatusFileName() );
          if ( !statusFile.exists() )
          {
             poll();
             //return;
          }
          
          if (statusFile.exists())
          {
             InputStream in = new FileInputStream( statusFile );
             ahStatus.load( in );
             pcs.firePropertyChange( "gameStatus", 0, 1 );
          }
       }
       catch ( Exception e )
       {
          Log.log( Log.ERROR, this, e );
       }
    }
}

