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
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.net.URL;

import java.util.HashMap;
import java.util.Timer;
import javax.swing.BoxLayout;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import stars.ahc.*;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 30, 2002
 */
public class AhcFrame extends javax.swing.JFrame
{
    static JLabel status = new JLabel();
    /**
     *  Description of the Field
     */
    protected JPanel cards;
    Container contentPane;

    HashMap optionPanes = new HashMap();
    Timer timer = new Timer( true );


    /**
     *  Constructor for the AhcFrame object
     */
    public AhcFrame()
    {
        init();
    }


    /**
     *  Adds a feature to the GamesTab attribute of the AhcFrame object
     *
     *@return    Description of the Return Value
     */
    public JPanel addGamesTab()
    {
        JPanel ret = new JPanel();
        ret.setLayout( new BoxLayout( ret, BoxLayout.Y_AXIS ) );
        Game[] games = GamesProperties.getGames();
        /*
         *  set up the game pages
         */
        for ( int i = 0; i < games.length; i++ )
        {
            ret.add( GamePanelFactory.createPanel( games[i] ), games[i].getName() );
        }
        JPanel scrollRet = new JPanel();
        JScrollPane scrollPane = new JScrollPane(
                ret,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        //scrollPane.setPreferredSize( new Dimension( 200, 150 ) );
        scrollRet.setLayout( new BoxLayout( scrollRet, BoxLayout.Y_AXIS ) );
        scrollRet.add( scrollPane );
        AhcGui.setGameCards( ret );
        return scrollRet;
    }


    /**
     *  Adds a feature to the LogTab attribute of the AhcFrame object
     *
     *@return    Description of the Return Value
     */
    public JPanel addLogTab()
    {
        JPanel ret = new JPanel();
        ret.setLayout( new BorderLayout() );
        JTextArea logBox = new JTextArea( Log.getLogDocument() );
        logBox.setEditable( false );
        logBox.setMargin( new Insets( 5, 5, 5, 5 ) );
        JScrollPane scrollPane = new JScrollPane(
                logBox,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        //scrollPane.setPreferredSize( new Dimension( 200, 150 ) );
        ret.add( scrollPane, BorderLayout.CENTER );
        return ret;
    }


    /**
     *  Adds a feature to the OptionTab attribute of the AhcFrame object
     *
     *@return    Description of the Return Value
     */
    public JPanel addOptionTab()
    {
        JPanel ret = new JPanel();
        ret.setLayout( new BorderLayout() );
        Game[] games = GamesProperties.getGames();
        /*
         *  set up the selector
         */
        JPanel selector = new JPanel();
        String[] cardNames = new String[games.length + 1];
        cardNames[0] = "Global Options";
        for ( int i = 0; i < games.length; i++ )
        {
            cardNames[i + 1] = games[i].getName() + " Options";
        }
        JComboBox c = new JComboBox( cardNames );
        c.setEditable( false );
        c.addItemListener(
            new ItemListener()
            {
                public void itemStateChanged( ItemEvent evt )
                {
                    CardLayout cl = ( CardLayout ) ( cards.getLayout() );
                    cl.show( cards, ( String ) evt.getItem() );
                    ( ( AbstractOptionPane ) optionPanes.get( ( String ) evt.getItem() ) )._refresh();
                }
            } );
        selector.add( c );
        AhcGui.setOptionSelector( c );
        ret.add( selector, BorderLayout.NORTH );
        /*
         *  set up the game pages
         */
        cards = new JPanel();
        cards.setLayout( new CardLayout() );
        AbstractOptionPane temp = OptionPanelFactory.createPanel();
        cards.add( temp, "Global Options" );
        optionPanes.put( "Global Options", temp );
        for ( int i = 0; i < games.length; i++ )
        {
            temp = OptionPanelFactory.createPanel( games[i] );
            cards.add( temp, games[i].getName() + " Options" );
            optionPanes.put( games[i].getName() + " Options", temp );
        }
        ret.add( cards, BorderLayout.CENTER );
        AhcGui.setGameOptionCards( cards );
        /*
         *  set up Player pages
         */
        return ret;
    }


    /**
     *  Description of the Method
     */
    public void init()
    {
        //AhcGui.setMainFrame( this );
        contentPane = getContentPane();
        contentPane.add( addBanner(), BorderLayout.NORTH );
        AhcGui.setMainFrame( this );
        AhcGui.setStatusBox( status );
        addWindowListener(
            new WindowAdapter()
            {
                public void windowClosing( WindowEvent e )
                {
                    timer.cancel();
                    System.exit( 0 );
                }
            } );
        setTitle( "AutoHost Client " + AutoHostClient.VERSION );
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab( "Games", addGamesTab() );
        tabbedPane.addTab( "Options", addOptionTab() );
        tabbedPane.addTab( "Log", addLogTab() );

        if ( GamesProperties.getGames().length == 0 )
        {
            OptionPanelFactory.addNewGame();
        }

        contentPane.add( tabbedPane, BorderLayout.CENTER );
        contentPane.add( status, BorderLayout.SOUTH );
        Log.log( Log.MESSAGE, this, "GUI built and ready." );
        AhcGui.setStatus( "GUI built and ready." );
        /*
         *  The line below sets how often AHC checks AH for updated files.
         *  DO NOT MODIFY THIS NUMBER.  Ron has graciously agreed to allow me to write this
         *  to make our lives a little easier.  If you abuse this, I will request that Ron
         *  shut down access by this application.
         */
        timer.schedule( new AHPoller(), 10 * 60 * 1000, 10 * 60 * 1000 );
    }


    /**
     *  Adds a feature to the Banner attribute of the AhcFrame object
     *
     *@return    Description of the Return Value
     */
    protected JLabel addBanner()
    {
        URL iconURL = ClassLoader.getSystemResource( "images/ahc_sm.png" );
        if ( iconURL != null )
        {
            ImageIcon icon = new ImageIcon( iconURL );
            JLabel ret = new JLabel( icon );
            return ret;
        }
        else
        {
            return null;
        }
    }
}

