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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import stars.ahc.AHPoller;
import stars.ahc.AutoHostClient;
import stars.ahc.Game;
import stars.ahc.GamesProperties;
import stars.ahc.Log;
import stars.ahcgui.pluginmanager.PlugInManager;
import stars.ahcgui.pluginmanager.PluginLoadError;

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
        
        setupGamesToolbar(ret);
        
        Game[] games = GamesProperties.getGames();
        /*
         *  set up the game pages
         */
        addBlankSpace(ret);
        for ( int i = 0; i < games.length; i++ )
        {
            ret.add( GamePanelFactory.createPanel( games[i] ), games[i].getName() );
            addBlankSpace(ret);
        }
        JPanel scrollRet = new JPanel();
        JScrollPane scrollPane = new JScrollPane(
                ret,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollRet.setLayout( new BoxLayout( scrollRet, BoxLayout.Y_AXIS ) );
        scrollRet.add( scrollPane );
        if ( Log.getLevel() == Log.DEBUG )
        {
            JButton but = new JButton( "Poll SAH" );
            but.addActionListener(
                new ActionListener()
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        new AHPoller().run();
                    }
                } );
            scrollRet.add( but );
        }
        AhcGui.setGameCards( ret );
        return scrollRet;
    }


    /**
     * Adds a toolbar to the Games tab 
     * 
     * @author Steve Leach
     */
   private void setupGamesToolbar( JPanel parent )
   {
      JPanel toolbar = new JPanel();
      toolbar.setBorder( BorderFactory.createEmptyBorder(2,2,2,2) );
      toolbar.setLayout( new BoxLayout(toolbar,BoxLayout.X_AXIS) );
      parent.add( toolbar );
      
      JButton newButton = new JButton( "New game" );
      newButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed( ActionEvent e )
               {
                  try
                  {
                     OptionPanelFactory.addNewGame();
                  }
                  catch (Throwable t)
                  {
                     t.printStackTrace();
                     JOptionPane.showMessageDialog(AhcFrame.this, "Error: " + t.getMessage() );                     
                  }
               }
            }
      );
      toolbar.add( newButton );
      toolbar.add( Box.createGlue() );
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
                    ( ( AbstractOptionPane ) optionPanes.get( ( String ) evt.getItem() ) ).refresh();
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
        JScrollPane scrollPane = new JScrollPane(
                cards,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        ret.add( scrollPane, BorderLayout.CENTER );
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
       setApplicationIcon();
       
        //AhcGui.setMainFrame( this );
        contentPane = getContentPane();
        
        JPanel bannerPanel = new JPanel();
        bannerPanel.add( addBanner() );
        bannerPanel.setBackground( Color.BLACK );
        contentPane.add( bannerPanel, BorderLayout.NORTH );
        
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
        
        loadPlugins();
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab( "Games", addGamesTab() );
        tabbedPane.addTab( "Options", addOptionTab() );
        tabbedPane.addTab( "Log", addLogTab() );
        if ( GamesProperties.getGames().length == 0 )
        {
            OptionPanelFactory.addNewGame();
        }
        contentPane.add( tabbedPane, BorderLayout.CENTER );
        contentPane.add( buildStatusPane(), BorderLayout.SOUTH );
        Log.log( Log.MESSAGE, this, "GUI built and ready." );
        AhcGui.setStatus( "GUI built and ready." );
        /*
         *  The line below sets how often AHC checks AH for updated files.
         *  DO NOT MODIFY THIS NUMBER.  Ron has graciously agreed to allow me to write this
         *  to make our lives a little easier.  If you abuse this, I will request that Ron
         *  shut down access by this application.  I will NOT be responsible for messing up
         *  AutoHost.
         */
        GamesProperties.UPTODATE = true;
        timer.schedule( new AHPoller(), 10 * 60 * 1000, 10 * 60 * 1000 ); //10 minutes
    }

    /**
     * Sets the icon that the application displays in the taskbar.
     *  
     * @author Steve Leach
     */
    private void setApplicationIcon()
    {
       URL iconURL = findImage("stars32.gif");
       Image img = Toolkit.getDefaultToolkit().createImage(iconURL);
       setIconImage( img );
    }


   /**
    * Finds and loads any plugins (extensions) that have been installed.
    * <p>
    * @author Steve Leach
    */
   private void loadPlugins()
   {
      try
      {
         PlugInManager.getPluginManager().findAndLoadPlugins();
      }
      catch (PluginLoadError e)
      {
         Log.log( Log.NOTICE, this, e );
      }
   }


   /**
     * Returns a URL representing a file name if the file exists
     * 
     * @param fileName
     * @return a URL, or null
     * @author Steve Leach
     */
    private static URL filenameToURL( String fileName )
    {
       File file = new File( fileName );
       
       if ( file.exists() )
       {
         try
         {
            return file.toURL();
         }
         catch (MalformedURLException e)
         {
            return null;
         }
       }
       else
       {
          return null;
       }
    }

    /**
     * Locates the specified image image 
     * @return the URL of the image if it could be found
     */    
    private static URL findImage( String imageName )
    {
       URL iconURL = ClassLoader.getSystemResource( "images/" + imageName );
       
       if ( iconURL == null )
       {
          iconURL = filenameToURL( "./" + imageName );
       }
       
       if ( iconURL == null )
       {
          iconURL = filenameToURL( "./images/" + imageName );
       }
       
       return iconURL;
    }
    
    /**
     *  Adds a feature to the Banner attribute of the AhcFrame object
     *
     *@return    Description of the Return Value
     *
     * Updated 5 Oct 2004, Steve Leach
     *    Now also looks for the image in the file system.
     *    Returns a valid label even if the image is not found.
     */
    protected JLabel addBanner()
    {
       	URL iconURL = findImage("ahc_sm.png");
       	
        if ( iconURL != null )
        {
            ImageIcon icon = new ImageIcon( iconURL );
            JLabel ret = new JLabel( icon, SwingConstants.CENTER );
            return ret;
        }
        else
        {
           Log.log( Log.ERROR, this.getClass(), "Image not found: ahc_sm.png" );
           return new JLabel("{Image not found}");
        }
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    protected JPanel buildStatusPane()
    {
        JPanel retPane = new JPanel();
        retPane.setLayout( new BoxLayout( retPane, BoxLayout.Y_AXIS ) );
        status.setBorder( BorderFactory.createTitledBorder( "Current Status" ) );
        retPane.add( status );
        retPane.add( new JLabel( "<html>Thanks to Ron Miller for AutoHost - http://starsautohost.org</html>" ) );
        return retPane;
    }


    /**
     *  Adds a feature to the BlankSpace attribute of the GamePanelFactory
     *  object
     *
     *@param  panel  The feature to be added to the BlankSpace attribute
     */
    private void addBlankSpace( JPanel panel )
    {
        Component blank = Box.createVerticalStrut( 20 );
        panel.add( blank );
    }
}

