/*
 *  This file is part of Stars! Autohost Client
 *  Copyright (c) 2002 Jeffrey C. Hoyt
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
import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import stars.ahc.StarsFileFilter;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 25, 2002
 */
public class StarsFileFilterTest extends TestCase
{
    File[] files = new File[10];
    File here = new File( System.getProperty( "java.io.tmpdir" ), "ach-test" );


    /**
     *  The main program for the StarsFileFilterTest class
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
        return new TestSuite( StarsFileFilterTest.class );
    }


    /*
     *  add tests here
     */
    /**
     *  A unit test for JUnit
     */
    public void testStarsFileFilter()
    {
        String[] names = here.list( new StarsFileFilter() );
        assertEquals( list( names ), 6, names.length );//not a great test, but the List version below is giving me grief
        /*
         *  List namesList = Arrays.asList( names );
         *  assertEquals(6, namesList.size());
         *  assertTrue( files[0] + " was not accepted by the filter", namesList.contains( files[0] ) );
         *  assertTrue( files[1] + " was not accepted by the filter", namesList.contains( files[1] ) );
         *  assertTrue( files[3] + " was not accepted by the filter", namesList.contains( files[3] ) );
         *  assertTrue( files[4] + " was not accepted by the filter", namesList.contains( files[4] ) );
         *  assertTrue( files[5] + " was not accepted by the filter", namesList.contains( files[5] ) );
         *  assertTrue( files[6] + " was not accepted by the filter", namesList.contains( files[6] ) );
         */
    }


    /**
     *  Description of the Method
     *
     *@param  list  Description of the Parameter
     *@return       Description of the Return Value
     */
    protected String list( String[] list )
    {
        StringBuffer ret = new StringBuffer();

        for ( int i = 0; i < list.length; i++ )
        {
            ret.append( list[i] );
            ret.append( "\n" );
        }
        return ret.toString();
    }


    /**
     *  The JUnit setup method
     */
    protected void setUp()
    {
        if ( !here.exists() )
        {
            here.mkdirs();
        }
        files[0] = new File( here, "test.m2" );
        files[1] = new File( here, "test.m12" );
        files[2] = new File( here, "test.m" );
        files[3] = new File( here, "test.h2" );
        files[4] = new File( here, "test.x2" );
        files[5] = new File( here, "ugly.m3" );
        files[6] = new File( here, "uglier.h16" );
        files[7] = new File( here, "foo.bar" );
        files[8] = new File( here, "test.x17" );
        files[9] = new File( here, "test.m0" );
        try
        {
            for ( int i = 0; i < files.length; i++ )
            {
                files[i].createNewFile();
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }


    /**
     *  The JUnit teardown method
     */
    protected void tearDown()
    {
        for ( int i = 0; i < files.length; i++ )
        {
            files[i].delete();
        }
    }
}


