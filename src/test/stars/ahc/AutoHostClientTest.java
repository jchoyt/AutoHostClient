

package test.stars.ahc;

import junit.framework.*;
import stars.ahc.AutoHostClient;


public class AutoHostClientTest extends TestCase
{
    /*
     * add tests here
     */
     public void testFindStarsExecutable()
     {
         String path = "d:/Jeff's~1/stars";
         assertEquals (AutoHostClient.findStarsExecutable(path), path);
     }

    /**
     *  The JUnit setup method
     */
    protected void setUp()
    {
    }

    /**
     *  The JUnit teardown method
     */
    protected void tearDown()
    {
    }


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
        return new TestSuite( AutoHostClientTest.class );
    }
}


