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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 12, 2002
 */
public class AHPoller extends TimerTask
{
    //
    // Updated 12 Oct 2004, Steve Leach
    // Removed references to the GUI from this non-GUI code.  All references
    // were for status updating and error reporting, so I implemented a
    // listener system instead.  The GUI now registers itself as a listener
    // for notifications from the AHPoller.
    //
    // Also changed the static methods to non-static.
    //

    private ArrayList notificationListeners = new ArrayList();
    public final static int BALLOON_NOTIFICATION = 99;


    /**
     *  Constructor for the AHPoller object
     */
    public AHPoller() { }


    private Map nextPollTime = new HashMap();


    /**
     *  Uses an established connection to get the turn number off AH.
     *
     *@param  mFile  Description of the Parameter
     *@return        The dateOfFile value
     */
    protected String getYearOfFile( File mFile )
    {
        try
        {
            InputStreamReader in = new FileReader( mFile );
            String year = Utils.getTurnNumber( in );
            in.close();
            return year;
        }
        catch ( FileNotFoundException e )
        {
            Log.log( Log.MESSAGE, AHPoller.class, "Couldn't find file " + mFile.getAbsolutePath() );
            //AhcGui.setStatus( "Couldn't find file " + mFile.getAbsolutePath() );
            sendNotification( this, NotificationListener.SEV_ERROR, "Couldn't find file " + mFile.getAbsolutePath() );
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
        boolean success = true;
        sendNotification( this, NotificationListener.SEV_STATUS, "Polling AutoHost - wait for the update." );
        long now = System.currentTimeMillis();
        for ( int i = 0; i < games.length; i++ )
        {
            Calendar cal = ( Calendar ) nextPollTime.get( games[i] );
            if ( cal == null )
            {
                cal = new GregorianCalendar();
                nextPollTime.put( games[i], cal );
            }
            if ( cal.getTimeInMillis() < now )
            {
                success = success && games[i].poll();
                if ( success )
                {
                    cal.add( Calendar.MILLISECOND, games[i].getPollInterval() );
                    Player[] players = games[i].getPlayers();
                    for ( int j = 0; j < players.length; j++ )
                    {
                        players[j].pcs.firePropertyChange( "status updated", "checked", "not checked" );
                    }
                }
            }
        }
        if ( success )
        {
            sendNotification( this, NotificationListener.SEV_STATUS, "All player stati updated." );
            GamesProperties.UPTODATE = true;
            GamesProperties.writeProperties();
        }
        else
        {
            sendNotification( this, NotificationListener.SEV_ERROR, "There was a problem checking the stati.  Please check the log and and report any errors to jchoyt@users.sourceforge.net." );
        }
    }


    /**
     *  Register a class that wishes to be notified of events
     *
     *@param  listener  The feature to be added to the NotificationListener
     *      attribute
     *@author           Steve Leach
     */
    public void addNotificationListener( NotificationListener listener )
    {
        notificationListeners.add( listener );
    }


    /**
     *  Send a notification to all registered listeners
     *
     *@param  source    Description of the Parameter
     *@param  severity  Description of the Parameter
     *@param  message   Description of the Parameter
     *@author           Steve Leach
     */
    private void sendNotification( Object source, int severity, String message )
    {
        for ( int n = 0; n < notificationListeners.size(); n++ )
        {
            NotificationListener listener = ( NotificationListener ) notificationListeners.get( n );

            // Wrap call to listener in try/catch to prevent a buggy listener from killing the poll
            try
            {
                listener.receiveNotification( source, severity, message );
            }
            catch ( Throwable t )
            {
                Log.log( Log.ERROR, listener, message.toString() );
            }
        }
    }

}

