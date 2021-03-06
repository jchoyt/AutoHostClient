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

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

//import stars.ahcgui.AhcGui;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 25, 2002
 */
public class Utils
{
    /**
     *  The path name of the Stars! executable file
     */
    public static File starsExecutable = null;
    private static Random random = new Random();

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
    * @throws AutoHostError
     */
    public static void setupDirs( Game currentGame ) throws AutoHostError
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
        catch ( AutoHostError e )
        {
           throw e;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    /**
     *  Gets the allyTurnsFromAutohost attribute of the Utils class
     *
     *@param  gameName     Description of the Parameter
     *@param  destination  Description of the Parameter
     *@param  fileName     Description of the Parameter
     */
    public static void downloadTurn( String fileName, String password, String destination ) throws AutoHostError
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
            URL servlet = new URL("https://starsautohost.org/cgi-bin/downloadturn.php?file=" + fileName );
            Log.log( Log.DEBUG, Utils.class, "Going to https://starsautohost.org/cgi-bin/downloadturn.php?file=" + fileName );
            URLConnection conn=servlet.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            String boundary = EssaiPostURLConnection.boundary;
            conn.setRequestProperty("Content-type","multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty( "Referer", "Stars!AutohostClient" );
            conn.setRequestProperty("Cache-Control", "no-cache");

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes("--" + boundary + "\r\n");
            // EssaiPostURLConnection.writeParam("file", fileName, out, boundary);
            // Log.log( Log.DEBUG, Utils.class, "file = " + fileName );
            EssaiPostURLConnection.writeParam("password", password, out, boundary);
            Log.log( Log.DEBUG, Utils.class, "password = "+ password );
            out.flush();
            out.close();

            InputStream stream = conn.getInputStream();
            BufferedInputStream in = new BufferedInputStream(stream);
            FileOutputStream file = new FileOutputStream( destination + "/" + fileName );
            BufferedOutputStream fout = new BufferedOutputStream( file );
            int loop;
            while ( ( loop = in.read() ) != -1 )
            {
                fout.write( loop );
            }
            fout.flush();
            fout.close();
            file.close();
            in.close();
            stream.close();
            Log.log( Log.DEBUG, Utils.class, "Done - successfully retrieved " + fileName );
        }
        catch ( IOException e )
        {
           throw new AutoHostError( fileName + " couldn't be retrieved", e );
        }
    }


    /**
     *  Gets the allyTurnsFromAutohost attribute of the Utils class
     *
     *@param  gameName     Description of the Parameter
     *@param  destination  Description of the Parameter
     *@param  fileName     Description of the Parameter
     */
    public static void getFileFromAutohost( String gameName, String fileName, String destination ) throws AutoHostError
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
           throw new AutoHostError( fileName + " couldn't be retrieved from AutoHost", e );
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
     *@param  year  Description of the Parameter
     *@return       Description of the Return Value
     */
    public static String createBackupFileName( File file, String year )
    {
        String filename = file.getName();
        int extensionPoint = filename.lastIndexOf( "." );
        return filename.substring( 0, extensionPoint ) + "." + year + filename.substring( extensionPoint );
    }

    /**
     * @param newExtension - the new file extension including the period
     */
    public static String changeFileExtension( File file, String newExtension )
    {
       String filename = file.getAbsolutePath();
       int extensionPoint = filename.lastIndexOf( "." );

       return filename.substring( 0, extensionPoint ) + newExtension;
    }

    /**
     *  Description of the Method
     *
     *@param  mfile  Description of the Parameter
     *@return        Description of the Return Value
     */
    public static String createBackupFileName( File mfile )
    {
        try
        {
            InputStream instream = new FileInputStream( mfile );
            Reader in = new InputStreamReader( instream );
            String year = Utils.getTurnNumber( in );
            in.close();
            instream.close();
            return createBackupFileName( mfile, year );
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
     *@param  game          Description of the Parameter
     *@param  playerNumber  Description of the Parameter
     */
    public static void backupPxxFiles( Game game, String playerNumber ) throws IOException
    {
       // TODO: check that NewReports=1, ie. it generated *.pnn rather than *.pla
       checkForNewReports( game, playerNumber );

        String gameDir = game.getDirectory();
        String backup = game.getDirectory() + "/backup";
        File fFile = new File( gameDir, game.getFFileName( playerNumber ) );
        File pFile = new File( gameDir, game.getPFileName( playerNumber ) );
        File backupFDest = new File( backup, createBackupFileName( fFile, game.getCurrentYear() ) );
        File backupPDest = new File( backup, createBackupFileName( pFile, game.getCurrentYear() ) );

        // Removed GUI code from this utility routine
        // Steve Leach, 22 Nov 2004

//        try
//        {
            Utils.fileCopy( fFile, backupFDest );
            Utils.fileCopy( pFile, backupPDest );
//        }
//        catch ( IOException e )
//        {
//            JOptionPane.showInternalMessageDialog(
//                    AhcGui.mainFrame.getContentPane(),
//                    "Couldn't backup .Pxx and .Fxx files",
//                    "File backup problem",
//                    JOptionPane.INFORMATION_MESSAGE );
//            return;
//        }
    }


    /**
    */
   private static void checkForNewReports(Game game, String playerNumber)
   {
      String gameDir = game.getDirectory();

      File pFile = new File( gameDir, game.getPFileName( playerNumber ) );

      String oldReportName = game.getDirectory() + File.separator + game.getName() + ".PLA";
      File oldReport = new File( oldReportName );

      if ((pFile.exists() == false) && (oldReport.exists()))
      {
         // User probably doesn't have NewReports = 1 in his Stars.ini

         // TODO: decide what to do here
      }
   }


   /**
     *  Description of the Method
     *
     *@param  playerNumber  Description of the Parameter
     *@param  password      Description of the Parameter
     *@param  workingDir    Description of the Parameter
     *@param  game          Description of the Parameter
     *@return               String containing any output from the process
     */
    public static String genPxxFiles( Game game, String playerNumber, String password, File workingDir ) throws IOException
    {
//        try
//        {
            String[] cmds = new String[5];
            cmds[0] = starsExecutable.getCanonicalPath();
            cmds[1] = "-dpfm";
            cmds[2] = new File(workingDir, game.getName() + ".m" + playerNumber).getCanonicalPath();
            cmds[3] = "-p";
            cmds[4] = password;
            StringBuffer commands = new StringBuffer();
            for (int i=0; i<cmds.length ; i++)
            {
                commands.append( cmds[i] );
                commands.append( " " );
            }
            Log.log( Log.WARNING, Utils.class, "Running: " + commands.toString() );
            Log.log( Log.WARNING, Utils.class, "     from " + workingDir.getCanonicalPath());
            Process proc = Runtime.getRuntime().exec( cmds, null, workingDir );

            try
            {
               proc.waitFor();
            }
            catch (InterruptedException e)
            {
               // ignore
            }

            backupPxxFiles( game, playerNumber );
            return "";

//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//            RuntimeException newex = new RuntimeException( e.getMessage() );
//            newex.fillInStackTrace();
//            throw newex;
//        }
    }

    /**
     * Returns true if the string is null, zero length or contains only whitespace
     *
     * @author Steve Leach
     */
    public static boolean empty( String str )
    {
       if (str == null) return true;
       else if (str.trim().equals("")) return true;
       else return false;
    }


   /**
    * Converts a string to an integer.  If the string does not contain a valid
    * integer then the default value is returned.
    *
    * @author Steve Leach
    */
   public static int safeParseInt(String value, int defaultValue)
   {
      return (int)safeParseLong(value,defaultValue);
   }

   /**
    * Converts a string to an integer.  If the string does not contain a valid
    * integer then 0 is returned.
    *
    * @author Steve Leach
    */
   public static int safeParseInt(String value)
   {
      return (int)safeParseLong(value,0);
   }

   public static long safeParseLong(String value)
   {
      return safeParseLong(value,0);
   }

   public static long safeParseLong(String value, long defaultValue)
   {
      try
      {
        // Converts to double then to long. This is good in case the stringed number was in decimal format.
      	return (long)Double.valueOf(value.trim()).doubleValue();
      }
      catch (Throwable t)
      {
         return defaultValue;
      }
   }

   /**
    * Returns a pseudo-random floating point value between 0 and 1.
    *
    * @author Steve Leach
    */
   public static float getRandomFloat()
   {
      return random.nextFloat();
   }

   public static int getRandomInt()
   {
      return random.nextInt();
   }

   public static Random getRandomGenerator()
   {
      return random;
   }

   /**
    * Returns a string representation of a color.
    * <p>
    * The colour string us in the format rr:bb:gg where rr, bb and gg are
    * red, blue and green values in the range 0 to 255.
    *
    * @see getColorFromString()
    * @author Steve Leach
    */
   public static String getColorStr(Color color)
   {
      return "" + color.getRed() + "," + color.getGreen() + "," + color.getBlue();
   }

   /**
    * Creates a Color object from a color string
    * <p>
    * See getColorStr() for details of the format.
    *
    * @see getColorStr()
    * @author Steve Leach
    */
   public static Color getColorFromString( String str ) throws ParseException
   {
      try
      {
         String[] tokens = str.split(",");

         int r = Integer.parseInt(tokens[0]);
         int g = Integer.parseInt(tokens[1]);
         int b = Integer.parseInt(tokens[2]);

         return new Color( r, g, b );
      }
      catch (Throwable t)
      {
         throw new ParseException( "String cannot be parsed as a color: '" + str + "'", 0 );
      }
   }

   /**
    * Adjusts the specified colour to the given brightness value
    * <p>
    * The brightness value should be between 0 (darkest) and 255 (lightest).
    * <p>
    * Works in HSV color space by adjusting V to the specified value.
    */
   public static Color adjustBrightness( Color baseColor, int value )
   {
      int r = baseColor.getRed();
      int b = baseColor.getBlue();
      int g = baseColor.getGreen();

      float[] vals = Color.RGBtoHSB( r, g, b, null );

      vals[2] = 1.0f * value / 255;

      Color result = Color.getHSBColor( vals[0], vals[1], vals[2] );

      return result;

   }


   /**
    * @param text
    * @return
    */
   public static double safeParseFloat(String text, double defaultValue)
   {
      try
      {
         return Float.parseFloat( text );
      }
      catch (Throwable t)
      {
         return defaultValue;
      }
   }

   public static boolean isIntDigit( char c )
   {
      if (c == '.')					return true;
      if (c == '-')					return true;
      if ((c >= '0') && (c<='9'))	return true;
      return false;
   }
   public static boolean isFloatDigit( char c )
   {
      if (c == '.')					return true;
      return isIntDigit(c);
   }

   public static int getLeadingInt( String text, int defaultValue )
   {
      char[] a = text.toCharArray();
      int digits = 0;
      for (int n = 0; n < a.length; n++)
      {
         if ( isIntDigit(a[n]) )
         {
            digits++;
         }
         else
         {
            break;
         }
      }
      text = new String( a, 0, digits );
      return safeParseInt( text, defaultValue );
   }

   /**
    * Gets a floating point number from the start of the string
    */
   public static double getLeadingFloat( String text, double defaultValue )
   {
      char[] a = text.toCharArray();
      int digits = 0;
      for (int n = 0; n < a.length; n++)
      {
         if ( isFloatDigit(a[n]) )
         {
            digits++;
         }
         else
         {
            break;
         }
      }
      text = new String( a, 0, digits );
      return safeParseFloat( text, defaultValue );
   }

   private static final String MANY_SPACES = "                                                                       ";

   public static String padRight( String text, int width )
   {
      if (text == null) text = "";
      return (text + MANY_SPACES).substring( 0, width );
   }

   public static String padLeft( String text, int width)
   {
      if (text == null) text = "";
      text = (MANY_SPACES + text);
      return text.substring( text.length() - width );
   }
}
