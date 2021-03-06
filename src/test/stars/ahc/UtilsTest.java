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
import java.awt.Color;
import java.io.*;
import java.io.File;
import java.io.StringReader;
import java.util.Properties;

import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import stars.ahc.Utils;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 26, 2002
 */
public class UtilsTest extends TestCase
{
    String fileHeader = " J3J3�*�`* ������MEΤ9p����A�f;j;�Dd전��?���b_�ʦ";
    Properties props;
    File srcFile;


    /**
     *  The main program for the UtsilsTest class
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
        return new TestSuite( UtilsTest.class );
    }


    /**
     *  A unit test for JUnit
     */
    public void testCreateBackupFileName()
    {
        String newName = Utils.createBackupFileName( srcFile );
        assertEquals( "Incorrect backup file name created", "test.2416.m11", newName );
    }


    /**
     *  A unit test for JUnit public void testFileBackup() { File dest = new
     *  File(props.getProperty("GameDir")+"/backup", "test.2416.m11");
     *  Utils.fileCopy(srcFile); assertTrue("Backup file does not exist",
     *  dest.exists()); dest.delete(); assertFalse("Backup file was not
     *  deleted", dest.exists());) }
     */
    public void testFileCopy()
    {
        try
        {
            File dest = new File( "dest" );
            long srcFileLength = srcFile.length();
            Utils.fileCopy( srcFile, dest );
            assertTrue( "Full destination file does not exist", dest.exists() );
            long destLength = dest.length();
            assertEquals( "Source and Destination are different sizes", srcFileLength, destLength );
            //clean up
            dest.delete();
            assertFalse( dest.exists() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    /**
     *  A unit test for JUnit
     */
    public void testGetTurnNumber()
    {
        StringReader in = new StringReader( fileHeader );
        String turnNumber = Utils.getTurnNumber( in );
        assertEquals( "2416", turnNumber );
        in.close();
    }

    public void testEmpty()
    {
       assertEquals( Utils.empty(null), true );
       assertEquals( Utils.empty(""), true );
       assertEquals( Utils.empty(" "), true );
       assertEquals( Utils.empty("\n"), true );
       assertEquals( Utils.empty("\t"), true );
       assertEquals( Utils.empty("\t   \n\n  \t"), true );
       assertEquals( Utils.empty("hello"), false );
    }

    public void testSafeParseInt()
    {
       assertEquals( Utils.safeParseInt("1",0), 1 );
       assertEquals( Utils.safeParseInt("2342",0), 2342 );
       assertEquals( Utils.safeParseInt("2342",2342), 2342 );
       assertEquals( Utils.safeParseInt("1.2",0), 0 );
       assertEquals( Utils.safeParseInt("",1), 1 );
       assertEquals( Utils.safeParseInt(null,2), 2 );
       assertEquals( Utils.safeParseInt("hello",3), 3 );
       assertEquals( Utils.safeParseInt("-1",4), -1 );
       assertEquals( Utils.safeParseInt("+2",5), 5 );
    }

    public void testColorString() throws Exception
    {
       Color color = Color.GREEN;

       String str = Utils.getColorStr(color);

       assertEquals( "0,255,0", str );

       Color color2 = Utils.getColorFromString(str);

       assertTrue( color.equals( color2 ) );
    }

    public void testRandomFloat()
    {
       // Important
       // It is possible (though very unlikely) that one of these may fail
       // due to the random number generator returning the same value twice
       // in succession.

       assertFalse( Utils.getRandomFloat() == Utils.getRandomFloat() );
       assertFalse( Utils.getRandomFloat() == Utils.getRandomFloat() );
       assertFalse( Utils.getRandomFloat() == Utils.getRandomFloat() );
    }

    /**
     *  The JUnit setup method
     */
    protected void setUp()
    {
        try
        {
            //create a dummy file
            srcFile = new File( "test.m11" );
            if ( !srcFile.exists() )
            {
                srcFile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream( srcFile );
            out.write( fileHeader.getBytes() );
            Random r = new Random();
            byte[] bytes = new byte[4096];
            for ( int i = 0; i < 10; i++ )
            {
                r.nextBytes( bytes );
                out.write( bytes );
            }
            out.flush();
            out.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    /**
     *  The JUnit teardown method
     */
    protected void tearDown()
    {
        srcFile.delete();
        assertFalse( srcFile.exists() );
    }
}
