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
//import java.awt.*;
//import java.awt.event.*;
import java.io.File;
import java.io.IOException;
//import java.util.*;
import javax.swing.JOptionPane;
//import stars.ahc.*;
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
    public static String VERSION = "v1.0";
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
        //Log.init( true, Log.DEBUG );
        Log.init( true, Log.NOTICE );
        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[i].startsWith( "-pf" ) )
            {
                propsFile = args[i].substring( 3 );
            }
            else if ( args[i].equals( "-usage" ) )
            {
                printUsage();
            }
        }
        File file = new File( propsFile );
        if ( !file.exists() )
        {
            /*
             *  Set up the Stars! Executable - needed before we go on.
             */
            String executable = findStarsExecutable( null );
            executable = JOptionPane.showInputDialog( null, "Please enter the location of your Stars! Executable file", executable );
            File executableFile = new File( executable );
            while ( !executableFile.exists() )
            {
                executable = JOptionPane.showInputDialog( null, "I can't seem to be able to find your Stars! Executable file", executable );
                executableFile = new File( executable );
            }
            GamesProperties.setStarsExecutable( executable );
            GamesProperties.setPropsFile( propsFile );
            GamesProperties.writeProperties();
        }

        GamesProperties.init( propsFile );
        Log.log( Log.MESSAGE, AutoHostClient.class, "Properties file loaded and parsed" );
        showGui();
        new AHPoller().run();

    }


    /**
     *  Description of the Method
     *
     *@param  path  Description of the Parameter
     *@return       Description of the Return Value
     */
    public static String findStarsExecutable( String path )
    {
        if ( path == null )
        {
            path = System.getProperty( "user.dir" );
            ;
        }
        int i = 30;
        String oldPath = null;
        while ( path != null && path != oldPath )
        {
            File file = new File( path, "stars.exe" );
            System.out.println( file.getAbsolutePath() );
            try
            {
                if ( file.exists() )
                {
                    return file.getCanonicalPath();
                }
            }
            catch ( IOException e )
            {
                Log.log( Log.WARNING, AutoHostClient.class, "Couldn't return the path name:" + e.getMessage() );
            }
            oldPath = path;
            path = file.getParentFile().getParent();
            i--;
            if ( i == 0 )
            {
                return null;
            }
        }
        return null;
    }


    /**
     *  Sends a copy of the usage to the command line.
     */
    public static void printUsage()
    {
        //TODO:  fill this out
        Log.log( Log.MESSAGE, AutoHostClient.class, "Usage goes here." );
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
}

