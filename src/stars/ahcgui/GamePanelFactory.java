/*
 *  This file is part of the AutoHost Client - [What it does in brief]
 *  Copyright (c) 2003 Jeffrey Hoyt
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import stars.ahc.*;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 25, 2002
 */
public class GamePanelFactory extends java.lang.Object
{
    /**
     *  Description of the Method
     *
     *@param  game  Description of the Parameter
     *@return       Description of the Return Value
     */
    public static JPanel createPanel( Game game )
    {
        return new GamePanel( game );
    }
}

/**
 *  Description of the Class
 *
 *@author     JCHOYT
 *@created    June 13, 2003
 */
class GamePanel extends JPanel implements PropertyChangeListener
{


    Game game;
    JLabel statusLabel;

    TitledBorder nameBorder;
    private GridBagConstraints c;
    private GridBagLayout gridbag;


    /**
     *  Constructor for the GamePanel object
     *
     *@param  game  Description of the Parameter
     */
    public GamePanel( Game game )
    {
        this.game = game;
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        this.setLayout( gridbag );
        c.gridx = 0;
        c.gridy = 0;
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
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    public void propertyChange( PropertyChangeEvent evt )
    {
        if ( evt.getPropertyName().equals( "game removed" ) )
        {
            Game p = ( Game ) evt.getOldValue();
            if ( p == game )
            {
                setVisible( false );
            }
        }
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
        c.gridwidth = 4;
        c.anchor = GridBagConstraints.CENTER;
        Properties playersByStatus = game.getPlayersByStatus();
        addStatusLabel( "In", playersByStatus.getProperty( "in" ), "green" );
        addStatusLabel( "Out", playersByStatus.getProperty( "out" ), "blue" );
        addStatusLabel( "Dead or Inactive", playersByStatus.getProperty( "dead" ), "gray" );
        c.gridwidth = oldGridwidth;
    }


    /**
     *  Adds a feature to the BlankSpace attribute of the GamePanelFactory
     *  object
     */
    private void addBlankSpace()
    {
        Component blank = Box.createVerticalStrut( 20 );
        c.gridwidth = 4;
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

        /*
         *  Add download button
         */
        JButton b1 = new DownloadButton( game );
        c.gridy++;
        gridbag.setConstraints( b1, c );
        this.add( b1 );
        /*
         *  Add upload button
         */
        b1 = new UploadButton( game );
        c.gridx++;
        gridbag.setConstraints( b1, c );
        this.add( b1 );
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
    }


    /**
     *  Adds the game information to the top of the game this
     *
     *@param  game  The feature to be added to the GameData attribute
     */
    private void addGameData( Game game )
    {
        c.gridwidth = 4;
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

        label = "<html><font size=-2>" + game.getNextGen() + " GMT (AutoHost time)</font></html>";//GamesProperties.UPTODATE ? "<html><font size=-2>"+game.getNextGen() + " GMT (AutoHost time)</font></html>" : "";
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


    /**
     *  Adds a feature to the StatusLabel attribute of the GamePanel object
     *
     *@param  status  The feature to be added to the StatusLabel attribute
     *@param  list    The feature to be added to the StatusLabel attribute
     *@param  color   The feature to be added to the StatusLabel attribute
     */
    private void addStatusLabel( String status, String list, String color )
    {
        /*
         *  Check to see if there are any in the list, if not, return
         */
        if ( list.equals( "null" ) || list.length() == 0 )
        {
            return;
        }
        /*
         *  add status label
         */
        StringBuffer ret = new StringBuffer();
        ret.append( "<html>" );
        ret.append( status );
        ret.append( ":  " );
        ret.append( "<font color=\"" );
        ret.append( color );
        ret.append( "\">" );
        ret.append( list );
        ret.append( "</font>" );
        ret.append( "</html>" );

        JLabel b = new JLabel( String.valueOf( ret ) );
        c.gridx = 0;
        c.gridy++;
        gridbag.setConstraints( b, c );
        this.add( b );
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
     *  Retrieves turns from Autohost, copies them into the game directory and
     *  backup directory. Sets the last download time and updates the properties
     *  file on disk.
     *
     *@param  e  Description of the Parameter
     */
    public void actionPerformed( ActionEvent e )
    {
        GamesProperties.removeGame( game );
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
                players[i].setLastDownload( System.currentTimeMillis() );
                players[i].setNeedsDownload( false );
                Utils.genPxxFiles( game.getName(), players[i].getId(), players[i].getStarsPassword(), new File( game.getDirectory() ) );
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
                players[i].setNeedsUpload( false );
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
            String[] cmds = new String[4];
            cmds[0] = new File( GamesProperties.getStarsExecutable() ).getCanonicalPath();
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

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    January 3, 2003
 */
class PlayerJLabel extends JLabel implements PropertyChangeListener
{


    Player player;


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
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    public void propertyChange( PropertyChangeEvent evt )
    {
        setText();
    }


    /**
     *  Constructor for the setText object
     */
    private void setText()
    {
        setText( "<html>" + player.getRaceName() + ": " + getStatusString( player ) + "</html>" );
    }


    /**
     *  Gets the attribute of the GamePanelFactory object
     *
     *@param  player  Description of the Parameter
     *@return         The String value
     */
    private String getStatusString( Player player )
    {
        return player.getAhStatus();
    }
}


