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
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Manages a game that is not hosted on Autohost
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public class LocalGameController implements GameController
{
    private PropertyChangeSupport pcs = new PropertyChangeSupport( new Object() );
    protected Game game;
    protected long nextGen = 0;
    protected int prevYear = -1;


    /**
     *  Constructor for the GameController object
     *
     *@param  game  Description of the Parameter
     */
    public LocalGameController( Game game )
    {
        this.game = game;
        nextGen = System.currentTimeMillis();
    }

    /**
     * @param id - player number
     * @param fileType - either "m" or "x"
     */
    private String getFileYear( String id, String fileType )
    {
       String year = "";

       File file = new File( game.directory + File.separator + game.name + "." + fileType + id  );

       if (file.exists())
       {
          try
          {
            FileReader in = new FileReader(file);
            year = Utils.getTurnNumber( in );
          }
          catch (Throwable t)
          {
             //TODO: decide what to do with this
          }
       }
       else
       {
          Log.log( Log.WARNING, this, "File not found: " + file.getAbsolutePath() );
       }

       return year;
    }

    /**
     *  Gets the currentYear attribute of the LocalGameController object
     *
     *@return    The currentYear value
     */
    public String getCurrentYear()
    {
       String id = game.getPlayers()[0].id ;
       return getFileYear( id, "m" );
    }


    /**
     *  Gets the gameYear attribute of the LocalGameController object
     *
     *@return    The gameYear value
     */
    public String getGameYear()
    {
        return getCurrentYear();
    }


    /**
     *  Gets the longName attribute of the LocalGameController object
     *
     *@return    The longName value
     */
    public String getLongName()
    {
        return game.name;
    }


    /**
     *  Gets the nextGen attribute of the LocalGameController object
     *
     *@return    The nextGen value
     */
    public String getNextGen()
    {
        return "Manual generation";
    }


    /**
     *  Gets the playerRaceName attribute of the LocalGameController object
     *
     *@param  id  Description of the Parameter
     *@return     The playerRaceName value
     */
    public String getPlayerRaceName( String id )
    {
        return null;
    }


    /**
     *  Gets the playersByStatus attribute of the LocalGameController object
     *
     *@return    The playersByStatus value
     */
    public Properties getPlayersByStatus()
    {
        Properties ret = new Properties();

        String in = "";
        String out = "";
        String dead = "";

        Player[] players = game.getPlayers();

        for (int n = 0; n < players.length; n++)
        {
           String status = getPlayerStatus( players[n].id );

           if (status.equals("in"))
           {
              if (in.length() > 0)
              {
                 in += ",";
              }
              in += players[n].getRaceName();
           }
           else
           {
              if (out.length() > 0)
              {
                 out += ",";
              }
              out += players[n].getRaceName();
           }
        }

        ret.setProperty("in", in);
        ret.setProperty("out", out);
        ret.setProperty("dead", dead);
        return ret;
    }


    /**
     *  Gets the status attribute of the LocalGameController object
     *
     *@return    The status value
     */
    public String getStatus()
    {
        return "Local game";
    }


    /**
     *  Sets the ahStatus attribute of the LocalGameController object
     *
     *@param  props  The new ahStatus value
     */
    public void setAhStatus( Properties props ) { }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public int poll()
    {
        if( System.currentTimeMillis() > nextGen )
        {
            return pollNow();
        }
        else
        {
            return AHPoller.POLL_NOT_DONE;
        }
    }


    public int pollNow()
   {
      Log.log( Log.NOTICE, this, "Polling local game " + game.getName() );
      
      Player player = (Player)game.players.get(0);
      int year = Utils.safeParseInt( getFileYear( player.id, "m" ) );
      
      if ((year >= 2400) && (year != prevYear))
      {
         File gameDir = new File( game.getDirectory() );
         int playerCount = game.players.size();
         
         for (int n = 0; n < playerCount; n++)
         {
            player = (Player)game.players.get(n);
            
	         try
	         {
	            Utils.genPxxFiles( game, player.getId(), player.getStarsPassword(), gameDir );
	         }
	         catch (IOException e)
	         {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	         }
         }
         
         try
         {
            game.loadReports();
         }
         catch (ReportLoaderException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         
         prevYear = year;
      }
      
      nextGen = System.currentTimeMillis() + getPollInterval();
      
      return AHPoller.POLL_SUCCESSFUL;
   }

   /**
     *  Sets the playerTurnStatus attribute of the LocalGameController object
     *
     *@param  playerNum  The new playerTurnStatus value
     *@param  value      The new playerTurnStatus value
     */
    public void setPlayerTurnStatus( int playerNum, String value ) { }


    /**
     *  Adds a feature to the PropertyChangeListener attribute of the
     *  LocalGameController object
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
        return 30*1000;  //30 seconds
    }

    private String getPlayerStatus( String id )
    {
       String mYear = getFileYear( id, "m" );
       String xYear = getFileYear( id, "x" );

       String status = "";

       if (Utils.empty(xYear))
       {
          status = "waiting";
       }
       else if (xYear.equals(mYear))
       {
          status = "in";
       }
       else
       {
          status = "waiting";
       }

       return status;
    }

   /* (non-Javadoc)
    * @see stars.ahc.GameController#getStatusProperties()
    */
   public void loadStatusProperties( Properties props )
   {
      Player[] players = game.getPlayers();

      for (int n = 0; n < players.length; n++)
      {
         String id = game.getPlayers()[n].id ;

         String status = getPlayerStatus( id );

         props.setProperty( "player" + id + "-turn", status );
      }

      return;
   }

   /* (non-Javadoc)
    * @see stars.ahc.GameTurnGenerator#generateTurns(int)
    */
   public void generateTurns(int turnsToGenerate) throws TurnGenerationError
   {
      String starsExe = GamesProperties.getStarsExecutable();

      String hostFileName = game.directory + File.separator + game.name + ".hst";

      File gameDirectory = new File(game.getDirectory());

      try
      {
          String[] cmds = new String[3];
          cmds[0] = new File( starsExe ).getCanonicalPath();
          cmds[1] = game.name + ".hst";
          cmds[2] = "-g";
          Process proc = Runtime.getRuntime().exec( cmds, null, gameDirectory );
      }
      catch ( IOException ioe )
      {
          Log.log( Log.WARNING, this, ioe );
      }

      Player[] players = game.getPlayers();

      for (int n = 0; n < players.length; n++)
      {
         try
         {
            Utils.genPxxFiles( game, players[n].id, players[n].getStarsPassword(), gameDirectory );
         }
         catch (IOException e)
         {
            throw new TurnGenerationError( "Error generating report files", e );
         }
      }

      game.loadProperties();
   }

   /* (non-Javadoc)
    * @see stars.ahc.GameTurnGenerator#readyToGenerate()
    */
   public boolean readyToGenerate()
   {
      return new File(game.directory + File.separator + game.name + ".hst").exists();
   }

   /* (non-Javadoc)
    * @see stars.ahc.GameController#getControllerName()
    */
   public String getControllerName()
   {
      return "Local controller";
   }

}


