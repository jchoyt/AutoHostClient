/*
 *  This file is part of the Stars! AutoHost Client - this class tests the Game class
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

import java.io.File;
import java.util.Properties;
import junit.framework.*;
import stars.ahc.Game;
import stars.ahc.Player;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 12, 2002
 */
public class GameTest extends TestCase
{
    Game game;
    String lineEnding = System.getProperty( "line.separator" );


    /**
     *  The main program for the GameTest class
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
        return new TestSuite( GameTest.class );
    }


    /**
     *  A unit test for JUnit
     */
    public void testGetPFileName()
    {
        String name = game.getPFileName( "1" );
        assertEquals( "mover.p1", name );
    }


    /**
     *  A unit test for JUnit
     */
    public void testGetFFileName()
    {
        String name = game.getFFileName( "1" );
        assertEquals( "mover.f1", name );
    }


    /**
     *  A unit test for JUnit
     */
    public void testGetReportFile()
    {
        int reportType = Game.REPORTTYPE_MAP;
        int player = 16;
        int year = 2012;
        File mapFile = game.getReportFile( reportType, player, year );
        assertTrue( mapFile.getAbsolutePath(), mapFile.getAbsolutePath().matches(".?:?[\\\\/]Jeff's~1[\\\\/]stars[\\\\/]mover[\\\\/]mover.map" ) );
        mapFile = game.getReportFile( reportType, player, 0 );
        assertTrue( mapFile.getAbsolutePath(), mapFile.getAbsolutePath().matches(".?:?[\\\\/]Jeff's~1[\\\\/]stars[\\\\/]mover[\\\\/]mover.map" ) );
        reportType = Game.REPORTTYPE_FLEET;
        mapFile = game.getReportFile( reportType, player, year );
        assertTrue( mapFile.getAbsolutePath(), mapFile.getAbsolutePath().matches(".?:?[\\\\/]Jeff's~1[\\\\/]stars[\\\\/]mover[\\\\/]backup[\\\\/]mover.2012.f16" ) );
        reportType = Game.REPORTTYPE_PLANET;
        mapFile = game.getReportFile( reportType, player, year );
        assertTrue( mapFile.getAbsolutePath(), mapFile.getAbsolutePath().matches(".?:?[\\\\/]Jeff's~1[\\\\/]stars[\\\\/]mover[\\\\/]backup[\\\\/]mover.2012.p16" ) ) ;
    }


    /**
     *  A unit test for JUnit
     */
    public void testFake()
    {
        assertTrue( true );
    }


    /**
     *  The JUnit setup method
     */
    protected void setUp()
    {
        game = new Game();
        Player[] players = new Player[2];
        Player player = new Player();
        player.setId( "1" );
        //player.setLastDownload( 1039721561160L );
        player.setStarsPassword( "" );
        player.setUploadPassword( "v0e4" );
        player.setGame( game );
        player.setToUpload( true );
        players[0] = player;
        player = new Player();
        player.setId( "2" );
        //player.setLastDownload( 1039721561160L );
        player.setStarsPassword( "" );
        player.setGame( game );
        player.setToUpload( false );
        players[1] = player;
        game.setPlayers( players );
        game.setName( "mover" );
        game.setDirectory( "/Jeff's~1/stars/mover" );
        Properties props = new Properties();
        props.setProperty( "game-year", "2012" );
        game.setAhStatus( props );
    }


    /**
     *  The JUnit teardown method
     */
    protected void tearDown()
    {
    }
}

