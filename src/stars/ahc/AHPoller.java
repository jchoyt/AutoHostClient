/*
 *  This file is part of the Stars! AutoHost Client - This class can
 *  poll AutoHost to see if a specified file is newer than the last
 *  time it was downloaded.
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
package stars.ahc;
import java.io.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.net.UnknownHostException;
import java.util.*;

import stars.ahcgui.AhcGui;
/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 12, 2002
 */
public class AHPoller extends TimerTask
{

    /**
     *  Constructor for the AHPoller object
     */
    public AHPoller() { }


    /**
     *  Checks the year on the m-file on AH vs. the reported current year. Note
     *  if the user has downloaded a turn separately, this may incorrectly
     *  report that they need to download a new turn when, in fact, they already
     *  have it.
     *
     *@param  player           Description of the Parameter
     *@return                  Description of the Return Value
     *@exception  IOException  Description of the Exception
     */
    public static boolean mFileIsNewer( Player player )
        throws IOException
    {
        String dateOnAh = "";
        try
        {
            URL url = new URL( GamesProperties.AUTOHOST + player.getGame().getName() + "/" + player.getTurnFileName() );
            URLConnection connection = url.openConnection();
            dateOnAh = getYearOfFile( connection );
            return ( dateOnAh.compareTo( player.getGame().getCurrentYear() ) > 0 );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
        }
        return false;
    }


    /**
     *  Checks the date on the x-file on the user's hard drive vs. the file date
     *  reported as the last uploaded file. Note if the user has uploaded a turn
     *  separately, this may incorrectly report that they need to upload when,
     *  in fact, they already have.
     *
     *@param  player  Description of the Parameter
     *@return         Description of the Return Value
     */
    public static boolean xFileIsNewer( Player player )
    {
        long xFileDate = player.getXFileDate();
        if ( xFileDate == 0 )
        {
            return false;
        }
        else
        {
            return ( xFileDate > player.getLastUpload() );
        }
    }


    /**
     *  Uses an established connection to get the turn number off AH.
     *
     *@param  conn  Description of the Parameter
     *@return       The dateOfFile value
     */
    protected static String getYearOfFile( URLConnection conn )
    {
        try
        {
            InputStreamReader in = new InputStreamReader( conn.getInputStream() );
            String year = Utils.getTurnNumber( in );
            in.close();
            return year;
        }
        catch ( java.net.UnknownHostException e )
        {
            Log.log( Log.MESSAGE, AHPoller.class, "Couldn't connect to the AutoHost server.  Are you connected to the internet?" );
            AhcGui.setStatus( "Couldn't connect to the AutoHost server.  Are you connected to the internet?" );
        }
        catch ( Exception e )
        {
            Log.log( Log.WARNING, AHPoller.class, e );
        }
        return "0";
    }


    /**
     *  Main processing method for the AHPoller object
     */
    public void run()
    {
        Game[] games = GamesProperties.getGames();
        for ( int i = 0; i < games.length; i++ )
        {
            games[i].poll();
        }
        AhcGui.setStatus("All player stati updated.");
    }
}

