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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import javax.swing.JOptionPane;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import stars.ahcgui.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 25, 2002
 */
public class Utils
{
    /**
     *  Description of the Field
     */
    public static String mapNeededRegex = "[02468]";
    /**
     *  Description of the Field
     */
    public static File starsExecutable = null;


    /**
     *  Sets the mapNeededRegex attribute of the Utils class
     *
     *@param  regex  The new mapNeededRegex value
     */
    public static void setMapNeededRegex( String regex )
    {
        Utils.mapNeededRegex = regex;
    }


    /**
     *  Sets the starsExecutable attribute of the Utils object
     *
     *@param  starsExecutable  The new starsExecutable value
     */
    public static void setStarsExecutable( File starsExecutable )
    {
        Utils.starsExecutable = starsExecutable;
    }


    /**
     *  Sets up the backup and staging directories if they don't already exist.
     *
     *@param  currentGame  Description of the Parameter
     */
    public static void setupDirs( Game currentGame )
    {
        try
        {
            String stage = currentGame.getDirectory() + "/staging";
            String backup = currentGame.getDirectory() + "/backup";
            /*
             *  create staging and backup directories if they don't exist already
             */
            File gameDir = new File( currentGame.getDirectory() );
            File stageDir = new File( stage );
            File backupDir = new File( backup );
            if ( !gameDir.exists() )
            {
                gameDir.mkdir();
            }
            if ( !stageDir.exists() )
            {
                stageDir.mkdir();
            }
            if ( !backupDir.exists() )
            {
                backupDir.mkdir();
            }
            File xyFile = new File( gameDir, currentGame.getName() + ".xy" );
            if ( !xyFile.exists() )
            {
                Utils.getFileFromAutohost( currentGame.getName(), currentGame.getName() + ".xy", currentGame.getDirectory() );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    /**
     *  Gets the allyTurnsFromAutohost attribute of the Utils class
     *
     *@param  gameName         Description of the Parameter
     *@param  destination      Description of the Parameter
     *@param  fileName         Description of the Parameter
     */
    public static void getFileFromAutohost( String gameName, String fileName, String destination )
    {
        try
        {
            //create destination if it doesn't exist
            File dest = new File( destination );
            if ( !dest.exists() )
            {
                dest.mkdirs();
            }
            //go get the files
            URL url = new URL( GamesProperties.AUTOHOST + gameName + "/" + fileName );
            Log.log( Log.DEBUG, Utils.class, "Going to get " + url.getFile() );
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            BufferedInputStream in = new BufferedInputStream( stream );
            FileOutputStream file = new FileOutputStream( destination + "/" + fileName );
            BufferedOutputStream out = new BufferedOutputStream( file );
            int loop;
            while ( ( loop = in.read() ) != -1 )
            {
                out.write( loop );
            }
            out.flush();
            out.close();
            file.close();
            in.close();
            stream.close();
            Log.log( Log.DEBUG, Utils.class, "Done - successfully retrieved " + url.getFile() );
        }
        catch ( IOException e )
        {
            JOptionPane.showInternalMessageDialog(
                    AhcGui.mainFrame.getContentPane(),
                    fileName + " couldn't be retrieved from AutoHost",
                    "Retrieval problem",
                    JOptionPane.INFORMATION_MESSAGE );
            return;
        }
    }


    /**
     *  Gets the starsExecutable attribute of the Utils object
     *
     *@return    The starsExecutable value
     */
    public static File getStarsExecutable()
    {
        return starsExecutable;
    }


    /**
     *  Gets the time attribute of the Utils class
     *
     *@return    The time value
     */
    public static String getTime()
    {
        SimpleDateFormat format = new SimpleDateFormat( "(h:mm:ss) " );
        return format.format( new Date() );
    }


    /**
     *  Gets the turnNumber from a Stars! x, m, or h file
     *
     *@param  in  Description of the Parameter
     *@return     The turnNumber value
     */
    public static String getTurnNumber( Reader in )
    {
        //Year: Stored in bytes 13 and 14. 2400 + byte 13 (decimal value) + 256* byte 14.
        char[] cbuf = new char[14];
        try
        {
            in.read( cbuf );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Couldn't read the year from the file: " + e.getMessage() );
        }
        int year = 2400 + cbuf[12] + 256 * cbuf[13];
        return Integer.toString( year );
    }


    /**
     *  Description of the Method
     *
     *@param  file  Description of the Parameter
     *@return       Description of the Return Value
     */
    public static String createBackupFileName( File file )
    {
        try
        {
            String filename = file.getName();
            int extensionPoint = filename.lastIndexOf( "." );
            InputStream instream = new FileInputStream( file );
            Reader in = new InputStreamReader( instream );
            String year = Utils.getTurnNumber( in );
            in.close();
            instream.close();
            return filename.substring( 0, extensionPoint ) + "." + year + filename.substring( extensionPoint );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return "all f'd up.";
        }
    }


    /**
     *  Description of the Method
     *
     *@param  sourceFile                 Description of the Parameter
     *@param  destFile                   Description of the Parameter
     *@exception  FileNotFoundException  Description of the Exception
     *@exception  IOException            Description of the Exception
     */
    public static void fileCopy( File sourceFile, File destFile )
        throws FileNotFoundException, IOException
    {
        byte[] buffer = new byte[4096];
        FileInputStream in = new FileInputStream( sourceFile );
        FileOutputStream out = new FileOutputStream( destFile );
        int len;
        while ( ( len = in.read( buffer ) ) != -1 )
        {
            out.write( buffer, 0, len );
        }
        out.flush();
        out.close();
        in.close();
        in = null;
        out = null;
    }


    /**
     *  Description of the Method
     *
     *@param  playerNumber  Description of the Parameter
     *@param  password      Description of the Parameter
     *@param  gameName      Description of the Parameter
     *@param  workingDir    Description of the Parameter
     *@return               String containing any output from the process
     */
    public static String genPxxFiles( String gameName, String playerNumber, String password, File workingDir )
    {
        try
        {
            String[] cmds = new String[5];
            cmds[0] = starsExecutable.getCanonicalPath();
            cmds[1] = "-dpf";
            cmds[2] = gameName + ".m" + playerNumber;
            cmds[3] = "-p";
            cmds[4] = password;
            Process proc = Runtime.getRuntime().exec( cmds, null, workingDir );
            /* String line;
            BufferedReader input =
                    new BufferedReader( new InputStreamReader( proc.getInputStream() ) );
            StringBuffer ret = new StringBuffer();
            while ( ( line = input.readLine() ) != null )
            {
                ret.append( line );
            }
            proc.waitFor();
            input.close();
            return ret.toString(); */
            proc.waitFor();
            return "";
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            RuntimeException newex = new RuntimeException( e.getMessage() );
            newex.fillInStackTrace();
            throw newex;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  year  Description of the Parameter
     *@return       Description of the Return Value
     */
    public static boolean mapNeeded( String year )
    {
        return year.matches( mapNeededRegex );
    }
}

