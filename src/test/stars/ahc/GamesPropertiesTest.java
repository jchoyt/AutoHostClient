package test.stars.ahc;
import java.io.*;
import java.util.Properties;

import junit.framework.*;
import stars.ahc.*;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 29, 2002
 */
public class GamesPropertiesTest extends TestCase
{
    Properties props;
    String lineEnding = System.getProperty( "line.separator" );


    /**
     *  The main program for the GamesPropertiesTestTest class
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
        return new TestSuite( GamesPropertiesTest.class );
    }


    /**
     *  A unit test for JUnit
     */
    public void testParseProps()
    {
        GamesProperties.setProps( props );
        GamesProperties.parseProps();
        assertEquals( "Stars! Executable was not set up properly", "D:/jeff's~1/stars/stars.exe", GamesProperties.getStarsExecutable() );
        Game[] games = GamesProperties.getGames();
        assertNotNull( "No Games were created", games );
        Game buguni3 = games[0];
        assertEquals( "Game name not correct", "buguni3", buguni3.getName() );
        assertEquals( "Game directory not correct", "D:/Jeff's~1/stars/test", buguni3.getDirectory() );
        Player[] players = buguni3.getPlayers();
        assertEquals( "Number of players is not correct", 2, players.length );
        Player p10 = players[0];
        assertEquals( "Player's game name is not correct", "buguni3", p10.getGame().getName() );
        assertEquals( "Player id is not correct", "10", p10.getId() );
        assertEquals( "Player password is not correct", "Terrible", p10.getStarsPassword() );
        Player p11 = players[1];
        assertEquals( "Player's game name is not correct", "buguni3", p11.getGame().getName() );
        assertEquals( "Player id is not correct", "11", p11.getId() );
        assertEquals( "Player password is not correct", "Twosome", p11.getStarsPassword() );
    }


    /**
     *  A unit test for JUnit
     */
    public void testToString()
    {
        //fail( GamesProperties.dumpToString() );
        // same as writeProperties - no need to test the same thing twice
    }


    /**
     *  A unit test for JUnit
     */
    public void testWriteProperties()
    {
        String value = "StarsExecutable=D:/jeff's~1/stars/stars.exe" +
                lineEnding + "buguni3.player10.lastDownload=0" +
                lineEnding + "buguni3.player10.lastUpload=0" +
                lineEnding + "buguni3.player10.StarsPassword=Terrible" +
                lineEnding + "buguni3.player10.UploadPassword=null" +
                lineEnding + "buguni3.player10.upload=false" +
                lineEnding + "buguni3.player10.needsUpload=false" +
                lineEnding + "buguni3.player10.needsDownload=false" +
                lineEnding + "buguni3.player11.lastDownload=0" +
                lineEnding + "buguni3.player11.lastUpload=0" +
                lineEnding + "buguni3.player11.StarsPassword=Twosome" +
                lineEnding + "buguni3.player11.UploadPassword=null" +
                lineEnding + "buguni3.player11.upload=false" +
                lineEnding + "buguni3.player11.needsUpload=false" +
                lineEnding + "buguni3.player11.needsDownload=false" +
                lineEnding + "buguni3.GameDir=D:/Jeff's~1/stars/test" +
                lineEnding + "buguni3.PlayerNumbers=10,11" +
                lineEnding + "Games=buguni3";
        StringWriter out = new StringWriter();
        try
        {
            GamesProperties.writeProperties( out );
            GamesProperties.writeProperties( );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        assertEquals( value, out.toString() );
    }


    /**
     *  The JUnit setup method
     */
    protected void setUp()
    {
        //set up Properties
        props = new Properties();
        props.setProperty( "StarsExecutable", "D:/jeff's~1/stars/stars.exe" );
        props.setProperty( "Games", "buguni3" );
        props.setProperty( "buguni3.PlayerNumbers", "10,11" );
        props.setProperty( "buguni3.GameDir", "D:/Jeff's~1/stars/test" );
        props.setProperty( "buguni3.player10.StarsPassword", "Terrible" );
        props.setProperty( "buguni3.player11.StarsPassword", "Twosome" );
        GamesProperties.setPropsFile("D:/Jeff's~1/stars/mover/test.props");
    }


    /**
     *  The JUnit teardown method
     */
    protected void tearDown()
    {
    }
}


