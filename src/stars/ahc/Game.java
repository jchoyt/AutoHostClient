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

import java.util.*;
import java.awt.event.*;
import java.awt.*;import java.io.*;

import javax.swing.*;
import stars.ahc.Player;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public class Game extends Object
{
    String currentYear = "";
    String directory;
    String name;
    ArrayList players;


    public Game()
    {
        players = new ArrayList();
        name = "";
        directory = "";
        currentYear="";
    }

    /**
     *  Sets the currentYear attribute of the Game object
     *
     *@param  currentYear  The new currentYear value
     */
    public void setCurrentYear( String currentYear )
    {
        this.currentYear = currentYear;
    }


    public void poll()
    {
        try
        {
            for ( int i = 0; i < players.size(); i++ )
            {
                ((Player)players.get(i)).poll();
            }
        }
        catch ( Exception e )
        {
            Log.log(Log.WARNING,this,e);
        }
    }

    /**
     *  Sets the currentYear attribute of the Game object
     */
    public void setCurrentYear()
    {
        try
        {
            String greatestYear = "2400";
            for ( int i = 0; i < players.size(); i++ )
            {
                String year = ((Player)players.get(i)).getMFileYear();
                if ( year.compareTo( greatestYear ) > 0 )
                {
                    greatestYear = year;
                }
            }
            currentYear = greatestYear;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    /**
     *  Sets the directory attribute of the Game object
     *
     *@param  directory  The new directory value
     */
    public void setDirectory( String directory )
    {
        this.directory = directory;
    }


    /**
     *  Sets the name attribute of the Game object
     *
     *@param  name  The new name value
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     *  Sets the players attribute of the Game object
     *
     *@param  players  The new players value
     */
    public void setPlayers( Player[] players )
    {
        for( int i=0; i<players.length; i++)
        {
        this.players.add(players[i]);
        }
    }


    /**
     *  Gets the currentYear attribute of the Game object
     *
     *@return    The currentYear value
     */
    public String getCurrentYear()
    {
        if ( currentYear.equals( "" ) )
        {
            setCurrentYear();
        }
        return currentYear;
    }


    /**
     *  Gets the directory attribute of the Game object
     *
     *@return    The directory value
     */
    public String getDirectory()
    {
        return directory;
    }


    /**
     *  Gets the name attribute of the Game object
     *
     *@return    The name value
     */
    public String getName()
    {
        return name;
    }


    /**
     *  Gets the players attribute of the Game object
     *
     *@return    The players value
     */
    public Player[] getPlayers()
    {
        Object[] playersArray = players.toArray();
        Player[] ar = new Player[playersArray.length];
        for( int i=0; i<ar.length; i++)
        {
            ar[i] = (Player) playersArray[i];
        }
        return ar;
    }


    public void addPlayer(Player p)
    {
        p.setGame(this);
        players.add(p);
    }

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString()
    {
        String lineEnding = System.getProperty( "line.separator" );
        StringBuffer ret = new StringBuffer();
        ret.append( "Game=" + name + lineEnding );
        StringWriter out = new StringWriter();
        try
        {
            writeProperties( out );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        ret.append( out.toString() );
        return ret.toString();
    }


    /**
     *  Description of the Method
     *
     *@param  out              Description of the Parameter
     *@exception  IOException  Description of the Exception
     */
    public void writeProperties( Writer out )
        throws IOException
    {
        String lineEnding = System.getProperty( "line.separator" );
        String playerNumbers = "";
        for ( int i = 0; i < players.size(); i++ )
        {
            playerNumbers += ((Player)players.get(i)).getId();
            if ( i < players.size() - 1 )
            {
                playerNumbers += ",";
            }
            ((Player)players.get(i)).writeProperties( out );
        }
        out.write( name + ".GameDir=" + directory + lineEnding );
        out.write( name + ".PlayerNumbers=" + playerNumbers + lineEnding );
    }
}


