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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import stars.ahcgui.AhcGui;

/**
 *  Description of the Class
 *
 *@author
 *@created    December 3, 2002
 */
public class EssaiPostURLConnection
{
    /**
     *  Creates a new instance of EssaiPostURLConnection
     */
    public static final String boundary = "-----ThAnKs-To-Ron-4-AuToHoSt";
    public EssaiPostURLConnection() { }


    /**
     *@param  args  the command line arguments
     */
    public static boolean upload( File fileToUpload, String uploadPassword )
    {
        try
        {
            URL targetUrl = new URL( "https://starsautohost.org/cgi-bin/upload.pl" );
            URLConnection conn = targetUrl.openConnection();
            conn.setDoOutput( true );
            conn.setDoInput( true );
            conn.setUseCaches( false );
            conn.setRequestProperty( "Content-type", "multipart/form-data; boundary=" + boundary );
            conn.setRequestProperty( "Referer", "Stars!AutohostClient" );
            conn.setRequestProperty( "Cache-Control", "no-cache" );

            DataOutputStream out = new DataOutputStream( conn.getOutputStream() );
            out.writeBytes( "--" + boundary + "\r\n" );
            writeFile( "filename", fileToUpload, out, boundary );
            writeParam( "password", uploadPassword, out, boundary );
            out.flush();
            out.close();
            //get upload size and check against report from Autohost
            long uploadSize = fileToUpload.length();

            InputStream stream = conn.getInputStream();
            BufferedInputStream in = new BufferedInputStream( stream );
            StringWriter outString = new StringWriter( );
            int i = 0;
            while ( ( i = in.read() ) != -1 )
            {
                outString.write( i );
            }
            in.close();
            // note - don't need to close the StringWriter

            String returnMsg = outString.toString();
            Pattern p = Pattern.compile( "(\\d{2,5}) bytes" );
            Matcher m = p.matcher( returnMsg );
            boolean b = m.find();
            if ( b )
            {
                long reportedSize = Long.parseLong( m.group( 1 ) );
                if ( reportedSize == uploadSize )
                {
                    Log.log(Log.WARNING,EssaiPostURLConnection.class,fileToUpload.getName() + "uploaded.  The file size matches the upload size reported by AutoHost - your upload should be fine." );
                    AhcGui.setStatus( fileToUpload.getName()+" uploaded" );
                }
            }
            else
            {
                Log.log(Log.WARNING,EssaiPostURLConnection.class, "Something went horribly wrong!  Report from AutoHost:\n" + returnMsg );
                AhcGui.setStatus("Something went horribly wrong with the upload!  Report from AutoHost:\n" + returnMsg);
                JOptionPane.showInternalMessageDialog(
                    AhcGui.mainFrame.getContentPane(),
                    "<html><body>Something went horribly wrong with the upload!  Report from AutoHost:\n" + returnMsg +"</html>",
                    "Upload Results",
                    JOptionPane.INFORMATION_MESSAGE );
            }
            return true;
        }
        catch ( Exception e )
        {
            Log.log(Log.WARNING,EssaiPostURLConnection.class, e.toString() );
            return false;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name        Description of the Parameter
     *@param  out         Description of the Parameter
     *@param  boundary    Description of the Parameter
     *@param  uploadFile  Description of the Parameter
     */
    private static void writeFile( String name, File uploadFile, DataOutputStream out, String boundary )
    {
        try
        {
            /*
             *  TODO: figure out the year and bounce it if it's the wrong year
             */
            if ( !uploadFile.exists() )
            {
                System.out.println( "The x-file does not exist." );
            }
            long size = uploadFile.length();

            out.writeBytes( "content-disposition: form-data; name=\"" + name + "\"; filename=\""
                     + uploadFile.getName() + "\"\r\n" );
            out.writeBytes( "content-type: application/octet-stream" + "\r\n" );

            FileInputStream fis = new FileInputStream( uploadFile );
            FileOutputStream fout = new FileOutputStream( "copy.x1" );
            byte[] buffer = new byte[( int ) size];
            int amountRead = fis.read( buffer );
            out.write( buffer, 0, amountRead );
            fout.write( buffer, 0, amountRead );
            fout.close();
            fis.close();
            out.writeBytes( "\r\n" + "--" + boundary + "\r\n" );
        }
        catch ( Exception e )
        {
            System.out.print( e.toString() );
        }
    }


    /**
     *  Description of the Method
     *
     *@param  name      Description of the Parameter
     *@param  value     Description of the Parameter
     *@param  out       Description of the Parameter
     *@param  boundary  Description of the Parameter
     */
    public static void writeParam( String name, String value, DataOutputStream out, String boundary )
    {
        try
        {
            out.writeBytes( "content-disposition: form-data; name=\"" + name + "\"\r\n\r\n" );
            out.writeBytes( value );
            out.writeBytes( "\r\n" + "--" + boundary + "\r\n" );
        }
        catch ( Exception e )
        {
            System.out.print( e.toString() );
        }
    }
}
