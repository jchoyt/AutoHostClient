/*
 *  This file is part of the Stars! AutoHost Client - this class tests the Player class
 *  Copyright (c) 2001 [Author]
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
package test.stars.ahc;
import java.io.*;
import junit.framework.*;

import stars.ahc.Game;
import stars.ahc.Player;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 12, 2002
 */
public class PlayerTest extends TestCase
{
    String lineEnding = System.getProperty( "line.separator" );
    Player player;


    /**
     *  The main program for the PlayerTest class
     *
     *@param  args  The command line arguments
     */
    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( suite() );
    }


    /**
     *  A unit test suite for JUnit
     *
     *@return    The test suite
     */
    public static Test suite()
    {
        /*
         *  the dynamic way
         */
        return new TestSuite( PlayerTest.class );
    }


    /**
     *  A unit test for JUnit
     */
    public void testGetMFileDate()
    {
    }


    /**
     *  A unit test for JUnit
     */
    public void testGetMFileName()
    {
        assertEquals( "mover.m1", player.getTurnFileName() );
    }


    /**
     *  A unit test for JUnit
     */
    public void testGetXFileDate()
    {
    }


    /**
     *  A unit test for JUnit
     */
    public void testGetXFileName()
    {
        assertEquals( "mover.x1", player.getXFileName() );
    }


    /**
     *  A unit test for JUnit
     */
    /* public void testToString()
    {
        String value = "Game=mover" +
                lineEnding + "Id=1" +
                lineEnding + "mover.player1.lastUpload=1039721561160" +
                lineEnding + "mover.player1.StarsPassword=" +
                lineEnding + "mover.player1.UploadPassword=v0e4" +
                lineEnding + "mover.player1.upload=true" +
                lineEnding;
        assertEquals( value, player.toString() );
    }*/


    /**
     *  A unit test for JUnit
     */
    /*public void testWriteProperties()
    {
        String value = "mover.player1.lastUpload=1039721561160" +
                lineEnding + "mover.player1.StarsPassword=" +
                lineEnding + "mover.player1.UploadPassword=v0e4" +
                lineEnding + "mover.player1.upload=true" +
                lineEnding;
        StringWriter out = new StringWriter();
        try
        {
            player.writeProperties( out );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        assertEquals( value, out.toString() );
    }*/


    /**
     *  The JUnit setup method
     */
    protected void setUp()
    {
        Game game = new Game();
        game.setName( "mover" );
        game.setDirectory( "D:/Jeff's~1/stars/mover" );
        player = new Player();
        player.setId( "1" );
        player.setLastDownload( 1039721561160L );
        player.setStarsPassword( "" );
        player.setUploadPassword( "v0e4" );
        player.setGame( game );
        player.setToUpload( true );
    }


    /**
     *  The JUnit teardown method
     */
    protected void tearDown()
    {
    }
}


