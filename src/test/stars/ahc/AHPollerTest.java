package test.stars.ahc;

import java.io.*;
import junit.framework.*;
import stars.ahc.AHPoller;
import stars.ahc.Game;
import stars.ahc.Player;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 15, 2002
 */
public class AHPollerTest extends TestCase
{
    String lineEnding = System.getProperty( "line.separator" );
    Player player;


    /**
     *  The main program for the AHPollerTest class
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
        return new TestSuite( AHPollerTest.class );
    }


    /**
     *  A unit test for JUnit
     */
    public void testMFileIsNewer()
    {
        /*
        try
        {
            player.getGame().setCurrentYear( "1400" );
            assertTrue( "set an obscenely low lastDownload",
                    AHPoller.mFileIsNewer( player ) );
            player.getGame().setCurrentYear( "3400" );
            assertFalse( "set an obscenely high lastDownload",
                    AHPoller.mFileIsNewer( player ) );
            player.getGame().setCurrentYear( "2400" );
            assertFalse( "Should be the same as on the server",
                    AHPoller.mFileIsNewer( player ) );
        }
        catch ( NullPointerException e )
        {
            System.out.println( "Can't test if the m file is newer - can't connect to URL ");
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        */
    }



    /**
     *  A unit test for JUnit
     */
    public void testXFileIsNewer()
    {
        player.setLastUpload( 10000L );
        assertTrue( "set an obscenely low lastDownload",
                AHPoller.xFileIsNewer( player ) );
        player.setLastUpload( 1139721561160L );
        assertFalse( "set an obscenely high lastDownload",
                AHPoller.xFileIsNewer( player ) );
    }


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
        player.setLastUpload( 1039721561160L );
        player.setLastDownload( 1039721561160L );
        player.setStarsPassword( "" );
        player.setUploadPassword( "v0e4" );
        player.setGame( game );
        player.setToUpload( true );
        Player[] players = new Player[1];
        players[0] = player;
    }


    /**
     *  The JUnit teardown method
     */
    protected void tearDown()
    {
    }
}


