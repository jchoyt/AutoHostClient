/*
 * Created on Oct 15, 2004
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
 */
package stars.ahcgui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import stars.ahc.AutoHostError;
import stars.ahc.EssaiPostURLConnection;
import stars.ahc.Game;
import stars.ahc.GamesProperties;
import stars.ahc.Log;
import stars.ahc.Player;
import stars.ahc.TurnGenerationError;
import stars.ahc.Utils;
import stars.ahcgui.pluginmanager.GamePanelButtonPlugin;
import stars.ahcgui.pluginmanager.PlugInManager;

/**
 * @author JCHOYT
 *
 */
public class GamePanel extends JPanel implements PropertyChangeListener
{
   Game game;
   JLabel statusLabel;
   private ArrayList buttonPlugins = new ArrayList();

   TitledBorder nameBorder;
   private GridBagConstraints c;
   private GridBagLayout gridbag;
   private int gridCols = 5;
   private StatusJLabel inLabel, outLabel, deadLabel;

   /**
    * Constructor has package-only visibility.
    * <p>
    * To create a GamePanel you should use the GamePanelFactory
    */
   GamePanel( Game game )
   {
       this.game = game;
       gridbag = new GridBagLayout();
       c = new GridBagConstraints();
       this.setLayout( gridbag );
       c.gridx = 0;
       c.gridy = 0;

       gridCols = 4 + countPluginButtons();

       addGameData( game );
       addBlankSpace();
       addPlayerList( game );
       addBlankSpace();
       addAllStati( game );
       addBlankSpace();
       addButtons( game );
       /*
        *  add border
        */
       nameBorder = BorderFactory.createTitledBorder( game.getLongName() + " ( year " + game.getGameYear() + " )" );
       Border spaceBorder = BorderFactory.createEmptyBorder( 5, 5, 5, 5 );
       this.setBorder( BorderFactory.createCompoundBorder( nameBorder, spaceBorder ) );
       /*
        *  set up the property change listener
        */
       game.addPropertyChangeListener( this );
       GamesProperties.addPropertyChangeListener( this );

   }

   /**
    * @return the number of GamePanel plugin buttons that are registered
    */
   private int countPluginButtons()
   {
      PlugInManager manager = PlugInManager.getPluginManager();
      ArrayList plugins = manager.getPlugins( GamePanelButtonPlugin.class );
      return plugins.size();
   }


   /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    public void propertyChange( PropertyChangeEvent evt )
    {
        if ( (evt != null) && evt.getPropertyName().equals( "game removed" ) )
        {
            Game p = ( Game ) evt.getOldValue();
            if ( p == game )
            {
                setVisible( false );
            }
        }
        Properties playersByStatus = game.getPlayersByStatus();
        inLabel.setText( playersByStatus.getProperty( "in" ) );
        outLabel.setText( playersByStatus.getProperty( "out" ) );
        deadLabel.setText( playersByStatus.getProperty( "dead" ) );

        statusLabel.setText( "<html>" + game.getStatus() + "</html>" );
        nameBorder.setTitle( game.getLongName() + " ( year " + game.getGameYear() + " )" );
    }

    /**
     *  Adds a feature to the AllStati attribute of the GamePanel object
     *
     *@param  game  The feature to be added to the AllStati attribute
     */
    private void addAllStati( Game game )
    {
        int oldGridwidth = c.gridwidth;
        c.gridwidth = gridCols;
        c.anchor = GridBagConstraints.CENTER;
        Properties playersByStatus = game.getPlayersByStatus();
        inLabel = new StatusJLabel( "In", "green" );
        c.gridx = 0;
        c.gridy++;
        gridbag.setConstraints( inLabel, c );
        add( inLabel );
        inLabel.setText( playersByStatus.getProperty( "in" ) );

        outLabel = new StatusJLabel( "Out", "blue" );
        c.gridy++;
        gridbag.setConstraints( outLabel, c );
        add( outLabel );
        outLabel.setText( playersByStatus.getProperty( "out" ) );

        deadLabel = new StatusJLabel( "Dead or Inactive", "gray" );
        c.gridy++;
        gridbag.setConstraints( deadLabel, c );
        add( deadLabel );
        deadLabel.setText( playersByStatus.getProperty( "dead" ) );

        c.gridwidth = oldGridwidth;
    }

    /**
     *  Adds a feature to the BlankSpace attribute of the GamePanelFactory
     *  object
     */
    private void addBlankSpace()
    {
        Component blank = Box.createVerticalStrut( 20 );
        c.gridwidth = gridCols;
        c.gridx = 0;
        c.gridy++;
        gridbag.setConstraints( blank, c );
        this.add( blank );
    }

    /**
     *  Adds a feature to the Buttons attribute of the GamePanelFactory class
     *
     *@param  game  The feature to be added to the Buttons attribute
     */
    private void addButtons( Game game )
    {
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        c.gridx = 0;

        JButton b1;
        c.gridy++;
        
        if (game.isAutohosted())
        {
	        /*
	         *  Add download button
	         */
	        b1 = new DownloadButton( game );
	        gridbag.setConstraints( b1, c );
	        this.add( b1 );
	        /*
	         *  Add upload button
	         */
	        b1 = new UploadButton( game );
	        c.gridx++;
	        gridbag.setConstraints( b1, c );
	        this.add( b1 );
        }
        else if (game.canGenerate())
        {
           Action generateAction = new AbstractAction("Generate") {
            public void actionPerformed(ActionEvent event)
            {
               generateNextTurn();
            }
           };
           
           b1 = new JButton(generateAction);
           gridbag.setConstraints( b1, c );
           add( b1 );
        }
        
        /*
         *  Add remove game button
         */
        b1 = new DeleteGameButton( game );
        c.gridx++;
        gridbag.setConstraints( b1, c );
        this.add( b1 );
        /*
         *  Add Analyze button
         */
        b1 = new AnalyzeGameButton( game );
        c.gridx++;
        gridbag.setConstraints( b1, c );
        this.add( b1 );

        addPluginButtons(game);

    }

    private void generateNextTurn()
    {
       try
      {
         game.generateNextTurn();
         propertyChange( null );
      }
      catch (TurnGenerationError e)
      {
         e.printStackTrace();
      }
    }
    
    /**
     * @param game
     */
    private void addPluginButtons(Game game)
    {
       JButton b1;
       ArrayList pluginButtons = PlugInManager.getPluginManager().getPlugins( GamePanelButtonPlugin.class );
       
       for (int n = 0; n < pluginButtons.size(); n++)
       {
          Class pluginClass = (Class)pluginButtons.get(n);
          GamePanelButtonPlugin plug = (GamePanelButtonPlugin)PlugInManager.getPluginManager().newInstance(pluginClass);
          
          plug.init( game );
          
          b1 = new PluginButton( plug );
          c.gridx++;
          gridbag.setConstraints( b1, c );
          
          this.add( b1 );
       }
    }

   /**
     *  Adds the game information to the top of the game this
     *
     *@param  game  The feature to be added to the GameData attribute
     */
    private void addGameData( Game game )
    {
        c.gridwidth = gridCols;
        /*
         *  titleYear = new JLabel( "<html><font size=+1><i>Game: " + game.getLongName() + " ( year " + game.getGameYear() + " )</i></html>", JLabel.RIGHT );
         *  gridbag.setConstraints( titleYear, c );
         *  /this.add( titleYear );
         */
        String label = "<html>" + game.getStatus() + "</html>";
        statusLabel = new JLabel( label );
        c.gridy++;
        gridbag.setConstraints( statusLabel, c );
        this.add( statusLabel );

        String ahTimeStr = game.getSahHosted().equals("true") ? " GMT (AutoHost time)" : "";
        
        label = "<html><font size=-2>" + game.getNextGen() + ahTimeStr + "</font></html>";//GamesProperties.UPTODATE ? "<html><font size=-2>"+game.getNextGen() + " GMT (AutoHost time)</font></html>" : "";
        JLabel nextGen = new JLabel( label );
        c.gridy++;
        gridbag.setConstraints( nextGen, c );
        this.add( nextGen );

    }

    /**
     *  Adds a feature to the PlayerLabel attribute of the GamePanel object
     *
     *@param  player  The feature to be added to the PlayerLabel attribute
     */
    private void addPlayerLabel( Player player )
    {
        /*
         *  add launch turn button
         */
        JButton b = new LaunchGameButton( player );
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy++;
        gridbag.setConstraints( b, c );
        this.add( b );
        /*
         *  add status JLabel
         */
        c.gridwidth = 3;
        PlayerJLabel playerLabel = new PlayerJLabel( player );
        c.gridx++;
        gridbag.setConstraints( playerLabel, c );
        this.add( playerLabel );
    }

    /**
     *  Adds a feature to the PlayerList attribute of the GamePanelFactory
     *  object
     *
     *@param  game  The feature to be added to the PlayerList attribute
     */
    private void addPlayerList( Game game )
    {
        /*
         *  Add player list
         */
        Player[] players = game.getPlayers();
        c.anchor = GridBagConstraints.WEST;
        int oldGridwidth = c.gridwidth;
        c.gridwidth = 2;
        for ( int i = 0; i < players.length; i++ )
        {
            addPlayerLabel( players[i] );
        }
        c.gridx = 0;
        c.gridwidth = oldGridwidth;
    }
}

/**
 *  Description of the Class
 *
 *@author     JCHOYT
 *@created    June 13, 2003
 */
class DeleteGameButton extends JButton implements ActionListener
{


    /**
     *  Description of the Field
     */
    protected Game game;


    /**
     *  Constructor for the DownloadButton object
     *
     *@param  game  Description of the Parameter
     */
    public DeleteGameButton( Game game )
    {
        this.game = game;
        setText( "Remove game" );
        addActionListener( this );
    }


    /**
     *
     */
    public void actionPerformed( ActionEvent e )
    {
       try
       {
          // Added confirmation dialog, 11 Oct 2004, Steve Leach
          int rc = JOptionPane.showConfirmDialog(
                getParent(),
                "Are you sure you want to remove this game ?",
                "Remove game",
                JOptionPane.YES_NO_OPTION);

          if (rc == JOptionPane.YES_OPTION)
          {
             GamesProperties.removeGame( game );
          }
       }
       catch (Throwable t)
       {
          t.printStackTrace();
          JOptionPane.showMessageDialog(this, "Error: " + t.getMessage() );
       }
    }
}
/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 25, 2002
 */
class DownloadButton extends JButton implements ActionListener
{


    Game game;


    /**
     *  Constructor for the DownloadButton object
     *
     *@param  game  Description of the Parameter
     */
    public DownloadButton( Game game )
    {
        this.game = game;
        setText( "Download Turns" );
        addActionListener( this );
    }


    /**
     *  Retrieves turns from Autohost, copies them into the game directory and
     *  backup directory. Sets the last download time and updates the properties
     *  file on disk.
     *
     *@param  e  Description of the Parameter
     */
    public void actionPerformed( ActionEvent e )
    {
        String stage = game.getDirectory() + "/staging";
        String backup = game.getDirectory() + "/backup";
        Player[] players = game.getPlayers();
        boolean success = true;
        try
        {
            for ( int i = 0; i < players.length; i++ )
            {
                Utils.getFileFromAutohost( game.getName(), players[i].getTurnFileName(), stage );
                File stagedSrc = new File( stage, players[i].getTurnFileName() );
                File backupDest = new File( backup, Utils.createBackupFileName( stagedSrc ) );
                File playFile = new File( game.getDirectory(), players[i].getTurnFileName() );
                Utils.fileCopy( stagedSrc, backupDest );
                Utils.fileCopy( stagedSrc, playFile );
                players[i].setLastUpload( System.currentTimeMillis() );
                Utils.genPxxFiles( game, players[i].getId(), players[i].getStarsPassword(), new File( game.getDirectory() ) );
                Log.log( Log.MESSAGE, this, "Player " + players[i].getId() + " m-file downloaded from AutoHost" );
                AhcGui.setStatus( "Player " + players[i].getId() + " m-file downloaded from AutoHost" );
            }
            GamesProperties.writeProperties();
        }
        catch ( IOException ioe )
        {
            success = false;
            Log.log( Log.MESSAGE, this, ioe );
            JOptionPane.showInternalMessageDialog(
                    AhcGui.mainFrame.getContentPane(),
                    "Couldn't complete download and report generation. See the log tab for details.",
                    "Connection Trouble",
                    JOptionPane.INFORMATION_MESSAGE );
        }
        catch ( AutoHostError ahe )
        {
            success = false;
            Log.log( Log.MESSAGE, this, ahe );
            JOptionPane.showInternalMessageDialog(
                    AhcGui.mainFrame.getContentPane(),
                    "Couldn't complete download and report generation. See the log tab for details.",
                    "Connection Trouble",
                    JOptionPane.INFORMATION_MESSAGE );
        }
        catch ( Exception ex )
        {
            success = false;
            Log.log( Log.WARNING, this, ex );
        }
        if ( success )
        {
            JOptionPane.showInternalMessageDialog(
                    AhcGui.mainFrame.getContentPane(),
                    "All turns from " + game.getName() + " downloaded",
                    "M-files downloaded",
                    JOptionPane.INFORMATION_MESSAGE );
        }

    }
}

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 25, 2002
 */
class AnalyzeGameButton extends JButton implements ActionListener
{


    Game game;


    /**
     *  Constructor for the DownloadButton object
     *
     *@param  game  Description of the Parameter
     */
    public AnalyzeGameButton( Game game )
    {
        this.game = game;
        setText( "Analyze Game" );
        addActionListener( this );
    }


    /**
     *@param  e  Description of the Parameter
     */
    public void actionPerformed( ActionEvent e )
    {
        new Eval().evaluate( game );
    }
}
/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 26, 2002
 */
class UploadButton extends JButton implements ActionListener
{

    final static String UPLOADED = "in via AHC - SAH will reflect this soon";
    Game game;


    /**
     *  Constructor for the DownloadButton object
     *
     *@param  game  Description of the Parameter
     */
    public UploadButton( Game game )
    {
        this.game = game;
        setText( "Upload Turns" );
        addActionListener( this );
    }


    /**
     *  Uploads turns to Autohost
     *
     *@param  e  Description of the Parameter
     */
    public void actionPerformed( ActionEvent e )
    {
        Player[] players = game.getPlayers();
        File playerXFile = null;
        boolean success = true;
        StringBuffer ret = new StringBuffer( "The following players have been uploaded for game " );
        ret.append( game.getName() );
        ret.append(":");
        for ( int i = 0; i < players.length; i++ )
        {
            if ( players[i].getToUpload() )
            {
                playerXFile = new File( players[i].getGame().getDirectory(), players[i].getXFileName() );
                success = success && EssaiPostURLConnection.upload(
                        playerXFile,
                        players[i].getUploadPassword() );
                players[i].setLastUpload( playerXFile.lastModified() );
                ret.append( " " );
                ret.append( players[i] );
                game.setPlayerTurnStatus(i, UPLOADED);
            }
        }
        GamesProperties.writeProperties();
        if ( success )
        {
            AhcGui.setStatus( "All players for " + game.getName() + " have been uploaded" );
            JOptionPane.showInternalMessageDialog(
                    AhcGui.mainFrame.getContentPane(),
                    ret.toString() +
                    ".  Autohost will reflect this in a few minutes.",
                    "Upload Results",
                    JOptionPane.INFORMATION_MESSAGE );
        }
    }
}
/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 26, 2002
 */
class LaunchGameButton extends JButton implements ActionListener
{


    Player player;


    /**
     *  Constructor for the DownloadButton object
     *
     *@param  player  Description of the Parameter
     */
    public LaunchGameButton( Player player )
    {
        this.player = player;
        setText( "Launch Stars!" );
        addActionListener( this );
    }


    /**
     *  Launches Stars! for a given player
     *
     *@param  e  Description of the Parameter
     */
    public void actionPerformed( ActionEvent e )
    {
        try
        {
           String starsExe = GamesProperties.getStarsExecutable();
            String[] cmds = new String[4];
            cmds[0] = new File( starsExe ).getCanonicalPath();
            cmds[1] = player.getTurnFileName();
            cmds[2] = "-p";
            cmds[3] = player.getStarsPassword();
            Process proc = Runtime.getRuntime().exec( cmds, null, new File( player.getGame().getDirectory() ) );
        }
        catch ( IOException ioe )
        {
            Log.log( Log.WARNING, this, ioe );
        }
    }
}

class PluginButton extends JButton implements ActionListener
{
   GamePanelButtonPlugin plugin = null;

   public PluginButton( GamePanelButtonPlugin plugin )
   {
      super( plugin.getButtonText() );
      this.plugin = plugin;
      addActionListener( this );
   }

   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent event)
   {
      try
      {
         plugin.execute();
      }
      catch (Throwable t)
      {
         t.printStackTrace();
         JOptionPane.showMessageDialog(this, "Error: " + t.getMessage() );
      }
   }
}


/**
 * Label that contains a fixed prompt in black followed a colored status string   
 *
 */
class StatusJLabel extends JLabel
{
    String color = null, status;

    public StatusJLabel(String status, String color)
    {
        this.status = status;
        this.color = color;        
    }

    public void setText(String list)
    {
        /*
         *  Check to see if there are any in the list, if not, return
         */
        if ( list.equals( "null" ) || list.length() == 0 )
        {
            super.setText("none");
        }

        StringBuffer ret = new StringBuffer();
        ret.append( "<html>" );
        ret.append( status );
        ret.append( ":  " );
        if (color != null)
        {
           ret.append( "<font color=\"" );
           	ret.append( color );
           	ret.append( "\">" );
        }
        
        ret.append( list );
        
        if (color != null)
        {
           ret.append( "</font>" );
        }
        ret.append( "</html>" );

        super.setText(  String.valueOf( ret ) );
    }

}

/**
 *  Label control for displaying the status of a Stars! player
 *
 *@author     jchoyt
 *@created    January 3, 2003
 */
class PlayerJLabel extends JLabel implements PropertyChangeListener
{
    private Player player = null;


    /**
     *  Constructor for the PlayerJLabel object
     *
     *@param  player  Description of the Parameter
     */
    public PlayerJLabel( Player player )
    {
        this.player = player;
        player.addPropertyChangeListener( this );
        setText();
    }


    /**
     * Reacts to property change events by updating the text
     *
     *@param  evt  Description of the event
     */
    public void propertyChange( PropertyChangeEvent evt )
    {
        setText();
    }


    /**
     *  Updates the text of the label
     */
    private void setText()
    {
       if (player == null)
       {
          return;
       }
       
       try
       {
          setText( "<html>" + player.getRaceName() + ": " + getStatusString( player ) + "</html>" );
       }
       catch (Throwable t)
       {
          Log.log( Log.ERROR, this.getClass(), "Error:" + t.getMessage() );
          setText( "<html>Error: " + t.getMessage() + "</html>" );
       }
    }


    /**
     */
    private String getStatusString( Player player )
    {
       if (player == null) return "";
       
        return player.getAhStatus();
    }
}



