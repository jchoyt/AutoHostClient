package test.stars.ahc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
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


