/*
 * :mode=java:tabSize=4:indentSize=4:noTabs=true:
 * :folding=indent:collapseFolds=0:wrap=none:maxLineLen=0:
 *
 * $Source$
 * Copyright (C) 2004 jchoyt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package stars.ahcgui;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import stars.ahc.AutoHostError;
import stars.ahc.Game;
import stars.ahc.GamesProperties;
import stars.ahc.Log;
import stars.ahc.Player;
import stars.ahc.Utils;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 25, 2002
 */
public class OptionPanelFactory extends Object
{
    private static GridBagConstraints c;
    private static GridBagLayout gridbag;
    private static AbstractOptionPane panel;


    /**
     *  Adds a feature to the NewGame attribute of the OptionPanelFactory class
     */
    public static void addNewGame()
    {
        JDialog dialog = new JDialog( AhcGui.getMainFrame(), "Add a new game", true );
        dialog.getContentPane().add( NewGamePane.createPane( dialog ) );
        dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        dialog.pack();
        dialog.show();
    }


    /**
     *  Adds a feature to the NewGame attribute of the OptionPanelFactory class
     *
     *@param  game  The feature to be added to the Player attribute
     */
    public static void addPlayer( Game game )
    {
        JDialog dialog = new JDialog( AhcGui.getMainFrame(), "Add player", true );
        dialog.getContentPane().add( NewPlayerPane.createPane( dialog, game ) );
        dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        dialog.pack();
        dialog.show();
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public static AbstractOptionPane createPanel()
    {
        return new GlobalOptionPane();
    }


    /**
     *  Description of the Method
     *
     *@param  game  Description of the Parameter
     *@return       Description of the Return Value
     */
    public static AbstractOptionPane createPanel( Game game )
    {
        return new GameOptionPane( game );
    }
}

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    January 15, 2003
 */
class GameOptionPane extends AbstractOptionPane implements PropertyChangeListener
{


    Game game;
    JTextField gameFileLocation = new JTextField();
    JTextField gameName = new JTextField();
    String playerList;
    ArrayList playerPanesList = new ArrayList();
    JPanel playerPanesPanel = new JPanel();


    /**
     *  Constructor for the GameOptionPane object
     *
     *@param  game  Description of the Parameter
     */
    public GameOptionPane( Game game )
    {
        super();
        this.game = game;
        init();
        refresh();
    }


    /**
     *  Constructor for the GameOptionPane object
     */
    public GameOptionPane()
    {
        super();
        this.game = new Game();
        init();
    }


    /**
     *  Description of the Method
     */
    protected void _init()
    {
        //add the change listener to the associated game.
        game.addPropertyChangeListener( this );
        GamesProperties.addPropertyChangeListener(this);
        //set layout of player pane to be vertical
        playerPanesPanel.setLayout( new BoxLayout( playerPanesPanel, BoxLayout.Y_AXIS ) );
        //int height = ( int ) gameName.getSize().getHeight();
        gameName.setMinimumSize( new Dimension( 200, 5 ) );
        addComponent( "Game name: ", gameName );
        addComponent( "Game files location: ", gameFileLocation );
        addBlankSpace();

        Player[] players = game.getPlayers();
        for ( int i = 0; i < players.length; i++ )
        {
            PlayerPane pane = new PlayerPane( players[i] );
            addPlayerPane( pane );
        }
        c.gridy++;
        c.gridheight = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1.0f;
        c.insets = new Insets( 1, 0, 1, 0 );
        gridbag.setConstraints( playerPanesPanel, c );
        add( playerPanesPanel );
        addButtons();
    }


    /**
     *  Description of the Method
     */
    protected void _refresh()
    {
        String gameLoc = game.getDirectory();
        if ( gameLoc.equals( "" ) )
        {
            try
            {
                gameLoc = GamesProperties.getStarsExecutable();
                File file = new File( gameLoc );
                file = file.getParentFile();
                gameLoc = file.getCanonicalPath();
                gameFileLocation.setText( gameLoc.replace( '\\', '/' ) );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
        else
        {
            gameFileLocation.setText( game.getDirectory() );
        }
        gameName.setText( game.getName() );
        for ( int i = 0; i < playerPanesList.size(); i++ )
        {
            ( ( PlayerPane ) playerPanesList.get( i ) )._refresh();
        }
    }


    /**
     *  Description of the Method
     */
    protected void _save()
    {
        game.setDirectory( gameFileLocation.getText() );
        game.setName( gameName.getText() );
        for ( int i = 0; i < playerPanesList.size(); i++ )
        {
            ( ( PlayerPane ) playerPanesList.get( i ) )._save();
        }
        try
        {
            GamesProperties.writeProperties();
            Log.log( Log.MESSAGE, this, "Game information saved." );
            AhcGui.setStatus( "Game information saved." );
        }
        catch ( Exception e )
        {
            Log.log( Log.WARNING, this, e );
        }
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    public void propertyChange( PropertyChangeEvent evt )
    {
        if ( evt.getPropertyName().equals( "player added" ) )
        {
            Player p = ( Player ) evt.getNewValue();
            PlayerPane pane = new PlayerPane( p );
            addPlayerPane( pane );
            pane.refresh();
            validate();
        }
        else if(evt.getPropertyName().equals("game removed"))
        {
            setVisible(false);
            AhcGui.getOptionSelector().removeItem(((Game)evt.getOldValue()).getName());
        }
    }


    /**
     *  Adds a feature to the Buttons attribute of the GamePanelFactory class
     */
    protected void addButtons()
    {
        addBlankSpace();
        JButton b1 = new JButton( "Add Player" );
        b1.setMnemonic( KeyEvent.VK_A );
        b1.addActionListener(
            new ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    OptionPanelFactory.addPlayer( game );
                }
            }
                 );
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints( b1, c );
        add( b1 );
        addBlankSpace();
        super.addButtons();
    }


    /**
     *  Adds a feature to the PlayerPane attribute of the GameOptionPane object
     *
     *@param  pane  The feature to be added to the PlayerPane attribute
     */
    protected void addPlayerPane( PlayerPane pane )
    {
        playerPanesList.add( pane );
        playerPanesPanel.add( pane );
    }
}

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    January 31, 2003
 */
class PlayerPane extends AbstractOptionPane
{


    Player player;
    JTextField playerNumber = new JTextField();
    JButton removePlayerButton = new JButton();
    JTextField starsPassword = new JTextField();
    JTextField uploadPassword = new JTextField();
    JCheckBox uploadTurn = new JCheckBox();


    /**
     *  Constructor for the PlayerPane object
     *
     *@param  player  Description of the Parameter
     */
    public PlayerPane( Player player )
    {
        this.player = player;
        removePlayerButton.addActionListener(
            new ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    removePlayer();
                }
            }
                 );
        init();
        refresh();
    }


    /**
     *  Description of the Method
     */
    protected void _init()
    {
        addComponent( "Player Number: ", playerNumber );
        addComponent( "Stars Password: ", starsPassword );
        addComponent( "Upload Turn: ", uploadTurn );
        addComponent( "Upload Password: ", uploadPassword );
        addComponent( removePlayerButton );
        /*
         *  add border
         */
        Border etched = BorderFactory.createEtchedBorder();
        setBorder( etched );
    }


    /**
     *  Description of the Method
     */
    protected void _refresh()
    {
        playerNumber.setText( player.getId() );
        starsPassword.setText( player.getStarsPassword() );
        uploadPassword.setText( player.getUploadPassword() );
        uploadTurn.setSelected( player.getToUpload() == true );
        removePlayerButton.setText( "Remove Player " + player.getId() );
    }


    /**
     *  Description of the Method
     */
    protected void _save()
    {
        player.setId( playerNumber.getText() );
        player.setStarsPassword( starsPassword.getText() );
        player.setUploadPassword( uploadPassword.getText() );
        if ( uploadTurn.isSelected() )
        {
            player.setToUpload( true );
        }
        else
        {
            player.setToUpload( false );
        }
        Log.log( Log.NOTICE, this, player.toString() );
    }


    /**
     *  Description of the Method
     */
    public void removePlayer()
    {
        final Game game = player.getGame();
        /*
         *  remove player from game object
         */
        game.removePlayer( player );
        /*
         *  remove player from GUI
         */
        GamesProperties.writeProperties();
        setVisible( false );
    }

}

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    January 15, 2003
 */
class GlobalOptionPane extends AbstractOptionPane
{


    JTextField proxyHost = new JTextField();
    JTextField proxyPort = new JTextField();
    JTextField starsLocation = new JTextField();


    /**
     *  Constructor for the GlobalOptionPane object
     */
    public GlobalOptionPane()
    {
        super();
        init();
        refresh();
    }


    /**
     *  Description of the Method
     */
    protected void _init()
    {
        addComponent( "Location of stars.exe: ", starsLocation );
        addComponent( "Proxy Host (if needed): ", proxyHost );
        addComponent( "Proxy Port (if needed): ", proxyPort );
        addBlankSpace();
        JButton b1 = new JButton( "Create New Game" );
        b1.setMnemonic( KeyEvent.VK_N );
        b1.addActionListener(
            new ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    OptionPanelFactory.addNewGame();
                }
            }
                 );
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints( b1, c );
        add( b1 );
        addBlankSpace();
        addButtons();
    }


    /**
     *  Description of the Method
     */
    protected void _refresh()
    {
        starsLocation.setText( GamesProperties.getStarsExecutable() );
        proxyHost.setText( GamesProperties.getProxyHost() );
        proxyPort.setText( GamesProperties.getProxyPort() );
    }


    /**
     *  Description of the Method
     */
    protected void _save()
    {
        GamesProperties.setStarsExecutable( starsLocation.getText() );
        GamesProperties.setProxyHost( proxyHost.getText() );
        GamesProperties.setProxyPort( proxyPort.getText() );
        try
        {
            GamesProperties.writeProperties();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

}

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    January 30, 2003
 */
class NewGamePane extends GameOptionPane
{


    /**
     *  Description of the Field
     */
    protected JDialog mainParent;


    /**
     *  Constructor for the NewGamePane object
     *
     *@param  game  Description of the Parameter
     */
    public NewGamePane( Game game )
    {
        super( game );
        setPreferredSize( new Dimension( 600, 250 ) );
    }


    /**
     *  Description of the Method
     *
     *@param  parent  Description of the Parameter
     *@return         Description of the Return Value
     */
    public static NewGamePane createPane( JDialog parent )
    {
        Game game = new Game();
        game.addPlayer( new Player() );
        NewGamePane newPane = new NewGamePane( game );
        newPane.mainParent = parent;
        return newPane;
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    public void actionPerformed( ActionEvent evt )
    {
        if ( evt.getActionCommand().equals( SAVE ) )
        {
            save();
            try
            {
               addGame();            
                GamesProperties.writeProperties();
            }
            catch (AutoHostError e)
            {
             JOptionPane.showInternalMessageDialog(
                   AhcGui.mainFrame.getContentPane(),
                   "Error contacting AutoHost",
                   "Retrieval problem",
                   JOptionPane.INFORMATION_MESSAGE );
            }
            catch ( Exception e )
            {
                Log.log( Log.WARNING, this, e );
            }
        }
        else if ( evt.getActionCommand().equals( CANCEL ) )
        {
            cancel();
        }
    }


    /**
     *  Adds a feature to the Buttons attribute of the GamePanelFactory class
     */
    protected void addButtons()
    {
        /*
         *  Add Save Button
         */
        JButton b1 = new JButton( "Add Game" );
        b1.setMnemonic( KeyEvent.VK_A );
        b1.setActionCommand( SAVE );
        b1.addActionListener( this );
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints( b1, c );
        add( b1 );
        /*
         *  Add Cancel button
         */
        b1 = new JButton( "Cancel" );
        b1.setMnemonic( KeyEvent.VK_C );
        b1.setActionCommand( CANCEL );
        b1.addActionListener( this );
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints( b1, c );
        add( b1 );
    }


    /**
     *  Adds a feature to the Game attribute of the NewGamePane object
    * @throws AutoHostError
     */
    protected void addGame() throws AutoHostError
    {
        game.setDirectory( gameFileLocation.getText() );
        game.setName( gameName.getText() );
        game.loadProperties();
        GamesProperties.addGame( game );
        AhcGui.getGameCards().add( GamePanelFactory.createPanel( game ), game.getName() );
        AhcGui.addOption( game.getName(), OptionPanelFactory.createPanel( game ) );
        mainParent.dispose();
        
        Utils.setupDirs( game );
    }


    /**
     *  Description of the Method
     */
    protected void cancel()
    {
        mainParent.dispose();
    }
}

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    January 30, 2003
 */
class NewPlayerPane extends PlayerPane
{


    /**
     *  Description of the Field
     */
    protected JDialog mainParent;
    Game game;


    /**
     *  Constructor for the NewGamePane object
     *
     *@param  player  Description of the Parameter
     */
    public NewPlayerPane( Player player )
    {
        super( player );
        game = player.getGame();
        setPreferredSize( new Dimension( 600, 250 ) );
    }


    /**
     *  Description of the Method
     *
     *@param  parent  Description of the Parameter
     *@param  game    Description of the Parameter
     *@return         Description of the Return Value
     */
    public static NewPlayerPane createPane( JDialog parent, Game game )
    {
        Player player = new Player();
        player.setGame( game );
        NewPlayerPane newPane = new NewPlayerPane( player );
        newPane.mainParent = parent;
        return newPane;
    }


    /**
     *  Description of the Method
     */
    protected void _init()
    {
        super._init();
        removePlayerButton.setVisible( false );
        addButtons();
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    public void actionPerformed( ActionEvent evt )
    {
        if ( evt.getActionCommand().equals( SAVE ) )
        {
            save();
            mainParent.dispose();
            game.addPlayer( player );
            try
            {
                GamesProperties.writeProperties();
            }
            catch ( Exception e )
            {
                Log.log( Log.WARNING, this, e );
                return;
            }
        }
        else if ( evt.getActionCommand().equals( CANCEL ) )
        {
            cancel();
        }
    }


    /**
     *  Description of the Method
     */
    public void updateGui()
    {
        //AhcGui.getMainFrame().repaint();
        AhcGui.getGameOptionCards().getLayout().layoutContainer( AhcGui.getGameOptionCards() );
    }


    /**
     *  Adds a feature to the Buttons attribute of the GamePanelFactory class
     */
    protected void addButtons()
    {
        /*
         *  Add Save Button
         */
        JButton b1 = new JButton( "Add Player" );
        b1.setMnemonic( KeyEvent.VK_A );
        b1.setActionCommand( SAVE );
        b1.addActionListener( this );
        c.gridx = 0;
        c.gridy++;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        gridbag.setConstraints( b1, c );
        add( b1 );
        /*
         *  Add Cancel button
         */
        b1 = new JButton( "Cancel" );
        b1.setMnemonic( KeyEvent.VK_C );
        b1.setActionCommand( CANCEL );
        b1.addActionListener( this );
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints( b1, c );
        add( b1 );
    }


    /**
     *  Description of the Method
     */
    protected void cancel()
    {
        mainParent.dispose();
    }
}

