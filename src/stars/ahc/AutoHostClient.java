/*
 *  This file is part of Stars! AutoHost Client
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
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import javax.swing.filechooser.FileFilter;
import stars.ahcgui.*;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 26, 2002
 */
public class AutoHostClient extends java.lang.Object
{
    /**
     *  Description of the Field
     */
    public static final String VERSION = "v1.1";
    static String propsFile = "ahclient.props";


    /**
     *  Constructor for the AutoHostClient object
     */
    public AutoHostClient() { }


    /**
     *  The starting point for the AutoHostClient class
     *
     *@param  args  The command line arguments
     */
    public static void main( String[] args )
    {
        int logLevel = Log.NOTICE;
        //Log.init( true, Log.DEBUG );
        for ( int j = 0; j < args.length; j++ )
        {
            if ( args[j].startsWith( "-pf" ) )
            {
                propsFile = args[++j];
            }
            else if ( args[j].equals( "-usage" ) )
            {
                printUsage();
                return;
            }
            else if ( args[j].equals( "-?" ) )
            {
                printUsage();
                return;
            }
            if ( args[j].equals( "-d" ) )
            {
                downloadAll();
                return;
            }
            if ( args[j].equals( "-testing" ) || args[j].equals( "-t" ) )
            {
                GamesProperties.AUTOHOST = "file:///D|/devel/autohostclient/";
                logLevel=Log.DEBUG;
                System.out.println( GamesProperties.AUTOHOST );
            }
        }
        Log.init( true, logLevel );
        File file = new File( propsFile );
        if ( !file.exists() )
        {
            JOptionPane.showMessageDialog( null, "The AutoHost Client doesn't appear to be set up yet.  Click OK and we'll get the client and a game set up." );
            setUpRoutine();
        }

        GamesProperties.init( propsFile );
        Log.log( Log.MESSAGE, AutoHostClient.class, "Properties file loaded and parsed" );
        showGui();
        new AHPoller().run();
    }


    /**
     *  Sends a copy of the usage to the command line.
     */
    public static void printUsage()
    {
        //TODO:  fill this out
        StringBuffer ret = new StringBuffer();
        ret.append( "Usage: " );
        ret.append( "\njava[w] -jar ahclient.jar [-usage | -? | -d] [-pf propsfile] [-testing | -t]" );
        ret.append( "\n    -usage   prints out this message" );
        ret.append( "\n    -?       prints out this message" );
        ret.append( "\n    -d       downloads all files in the properties configuration file" );
        ret.append( "\n    -pf loc  use the configuration file specified by \"loc\"" );
        ret.append( "\n\nexamples:" );
        ret.append( "\n    ahclient.jar -pf buguni3.props" );
        ret.append( "\n    ahclient.jar -d -pf dtroubl1.props" );
        System.out.println( ret.toString() );
    }


    /**
     *  Description of the Method
     */
    public static void showGui()
    {
        AhcFrame mainFrame = new AhcFrame();
        mainFrame.pack();
        mainFrame.setVisible( true );
    }


    /**
     *  Constructor for the setUpRoutine object
     */
    protected static void setUpRoutine()
    {
        /*
         *  Set up the Stars! Executable - needed before we go on.
         */
        JFileChooser chooser = new JFileChooser( System.getProperty( "user.dir" ) );
        chooser.addChoosableFileFilter(
            new FileFilter()
            {
                public String getDescription()
                {
                    return "*.exe";
                }


                public boolean accept( File f )
                {
                    if ( f.getName().endsWith( ".exe" ) || f.isDirectory() )
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            } );
        chooser.setFileFilter(
            new FileFilter()
            {
                public String getDescription()
                {
                    return "stars.exe";
                }


                public boolean accept( File f )
                {
                    if ( f.getName().equalsIgnoreCase( "stars.exe" ) || f.isDirectory() )
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            } );
        int returnVal = chooser.showDialog( null, "Use this Stars! executable" );
        File executableFile = null;
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            executableFile = chooser.getSelectedFile();
        }
        try
        {
            String filename = executableFile.getAbsolutePath();
            GamesProperties.setStarsExecutable( executableFile.getAbsolutePath().replace( '\\', '/' ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        GamesProperties.setPropsFile( propsFile );
        GamesProperties.writeProperties();
    }


    /**
     *  Description of the Method
     */
    protected static void downloadAll()
    {
        GamesProperties.init( propsFile );
        Game game = GamesProperties.getGames()[0];
        String stage = game.getDirectory() + "/staging";
        Player[] players = game.getPlayers();
        try
        {
            for ( int i = 0; i < players.length; i++ )
            {
                Utils.getFileFromAutohost( game.getName(), players[i].getTurnFileName(), stage );
                File stagedSrc = new File( stage, players[i].getTurnFileName() );
                File playFile = new File( game.getDirectory(), players[i].getTurnFileName() );
                Utils.fileCopy( stagedSrc, playFile );
                players[i].setLastDownload( System.currentTimeMillis() );
                players[i].setNeedsDownload( false );
                Utils.genPxxFiles( game.getName(), players[i].getId(), players[i].getStarsPassword(), new File( game.getDirectory() ) );
                System.out.println( "Player " + players[i].getId() + " m-file downloaded from AutoHost" );
            }
        }
        catch ( IOException ioe )
        {
            ioe.printStackTrace();
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        return;
    }
}

