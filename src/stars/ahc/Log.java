/*
 *  Log.java - A class for logging events
 *  Copyright (C) 1999, 2000 Slava Pestov
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

import stars.ahc.Utils;
import java.util.Date;import java.io.*;
import java.util.StringTokenizer;

import javax.swing.text.*;

/**
 *  This class provides methods for logging events. It has the same purpose as
 *  System.out.println() and such, but more powerful. All events are logged to a
 *  Swing document and optionally a stream, and those with a high urgency
 *  (warnings and errors) are also printed to the standard error stream. This
 *  class can also optionally redirect standard output and error to the log.
 *
 *@author     Slava Pestov
 *@created    January 11, 2002
 *@version    $Id$
 */
public class Log
{

    // private members
    private static Object LOCK = new Object();
    private static int level;
    private static String lineSep;
    private static Document logDocument;
    private static PrintStream realErr;
    private static PrintStream realOut;
    private static Writer stream;

    /**
     *  Debugging message urgency. Should be used for messages only useful when
     *  debugging a problem.
     *
     *@since    jEdit 2.2pre2
     */
    public final static int DEBUG = 1;

    /**
     *  Error urgency. Should be used for messages that signal a failure.
     *
     *@since    jEdit 2.2pre2
     */
    public final static int ERROR = 9;
    /**
     *  The maximum number of log messages that will be kept in memory.
     *
     *@since    jEdit 2.6pre5
     */
    public final static int MAXLINES = 500;

    /**
     *  Message urgency. Should be used for messages which give more detail than
     *  notices.
     *
     *@since    jEdit 2.2pre2
     */
    public final static int MESSAGE = 3;

    /**
     *  Notice urgency. Should be used for messages that directly affect the
     *  user.
     *
     *@since    jEdit 2.2pre2
     */
    public final static int NOTICE = 5;

    /**
     *  Warning urgency. Should be used for messages that warrant attention.
     *
     *@since    jEdit 2.2pre2
     */
    public final static int WARNING = 7;

    static
    {
        level = MESSAGE;

        realOut = System.out;
        realErr = System.err;

        logDocument = new PlainDocument();
        lineSep = System.getProperty( "line.separator" );
    }


    /**
     *  Writes all currently logged messages to this stream if there was no
     *  stream set previously, and sets the stream to write future log messages
     *  to.
     *
     *@param  stream  The writer
     *@since          jEdit 3.2pre4
     */
    public static void setLogWriter( Writer stream )
    {
        if ( Log.stream == null && stream != null )
        {
            try
            {
                stream.write( logDocument.getText( 0,
                        logDocument.getLength() ) );

                stream.flush();
            }
            catch ( Exception e )
            {
                // do nothing, who cares
            }
        }

        Log.stream = stream;
    }


    /**
     *  Returns the document where the most recent messages are stored. The
     *  document of a Swing text area can be set to this to graphically view log
     *  messages.
     *
     *@return    The LogDocument value
     *@since     jEdit 2.2pre2
     */
    public static Document getLogDocument()
    {
        return logDocument;
    }


    /**
     *  Closes the log stream. Should be done before your program exits.
     *
     *@since    jEdit 2.6pre5
     */
    public static void closeStream()
    {
        if ( stream != null )
        {
            try
            {
                stream.close();
                stream = null;
            }
            catch ( IOException io )
            {
                io.printStackTrace( realErr );
            }
        }
    }


    /**
     *  Flushes the log stream.
     *
     *@since    jEdit 2.6pre5
     */
    public static void flushStream()
    {
        if ( stream != null )
        {
            try
            {
                stream.flush();
            }
            catch ( IOException io )
            {
                io.printStackTrace( realErr );
            }
        }
    }


    /**
     *  Initializes the log.
     *
     *@param  stdio  If true, standard output and error will be sent to the log
     *@param  level  Messages with this log level or higher will be printed to
     *      the system console
     *@since         jEdit 3.2pre4
     */
    public static void init( boolean stdio, int level )
    {
        if ( stdio )
        {
            if ( System.out == realOut && System.err == realErr )
            {
                System.setOut( createPrintStream( NOTICE, null ) );
                System.setErr( createPrintStream( ERROR, null ) );
            }
        }

        Log.level = level;

        // Log some stuff
        log( MESSAGE, Log.class, "When reporting bugs, please"
                 + " include the following information:" );
        String[] props = {
                "java.version", "java.vendor",
                "java.compiler", "os.name", "os.version",
                "os.arch", "user.home", "java.home",
                "java.class.path",
                };
        for ( int i = 0; i < props.length; i++ )
        {
            log( MESSAGE, Log.class,
                    props[i] + "=" + System.getProperty( props[i] ) );
        }
    }


    /**
     *  Logs a message. This method is threadsafe.
     *
     *@param  urgency  The urgency
     *@param  source   The object that logged this message.
     *@param  message  The message. This can either be a string or an exception
     *@since           jEdit 2.2pre2
     */
    public static void log( int urgency, Object source, Object message )
    {
        String _source;
        if ( source == null )
        {
            _source = Thread.currentThread().getName();
            if ( _source == null )
            {
                _source = Thread.currentThread().getClass().getName();
            }
        }
        else if ( source instanceof Class )
        {
            _source = ( ( Class ) source ).getName();
        }
        else
        {
            _source = source.getClass().getName();
        }
        int index = _source.lastIndexOf( '.' );
        if ( index != -1 )
        {
            _source = _source.substring( index + 1 );
        }

        if ( message instanceof Throwable )
        {
            _logException( urgency, source, ( Throwable ) message );
        }
        else
        {
            String _message = String.valueOf( message );
            // If multiple threads log stuff, we don't want
            // the output to get mixed up
            synchronized ( LOCK )
            {
                StringTokenizer st = new StringTokenizer(
                        _message, "\r\n" );
                while ( st.hasMoreTokens() )
                {
                    _log( urgency, _source, st.nextToken() );
                }
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  urgency  Description of Parameter
     *@param  source   Description of Parameter
     *@param  message  Description of Parameter
     *@since
     */
    private static void _log( int urgency, String source, String message )
    {
        String urgencyString = "[" + Utils.getTime() + urgencyToString( urgency ) + "] ";
        //String time = ( new Date() ).toString() + lineSep;
        String fullMessage = urgencyString + source + ": " + message;

        try
        {
            logDocument.insertString( logDocument.getLength(),
                    fullMessage, null );
            logDocument.insertString( logDocument.getLength(),
                    "\n", null );

            Element map = logDocument.getDefaultRootElement();
            int lines = map.getElementCount();
            if ( lines > MAXLINES )
            {
                Element first = map.getElement( 0 );
                Element last = map.getElement( lines - MAXLINES );
                logDocument.remove( first.getStartOffset(),
                        last.getEndOffset() );
            }

            if ( stream != null )
            {
                stream.write( fullMessage );
                stream.write( lineSep );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace( realErr );
        }

        message = urgencyString + message + '\n';

        if ( urgency >= level )
        {
            if ( urgency == ERROR )
            {
                realErr.print( message );
            }
            else
            {
                realOut.print( message );
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  urgency  Description of Parameter
     *@param  source   Description of Parameter
     *@param  message  Description of Parameter
     *@since
     */
    private static void _logException( final int urgency,
            final Object source,
            final Throwable message )
    {
        PrintStream out = createPrintStream( urgency, source );

        synchronized ( LOCK )
        {
            message.printStackTrace( out );
        }
    }


    /**
     *  Description of the Method
     *
     *@param  urgency  Description of Parameter
     *@param  source   Description of Parameter
     *@return          Description of the Returned Value
     *@since
     */
    private static PrintStream createPrintStream( final int urgency,
            final Object source )
    {
        return new PrintStream(
            new OutputStream()
            {
                public void write( int b )
                {
                    byte[] barray = {( byte ) b};
                    write( barray, 0, 1 );
                }


                public void write( byte[] b, int off, int len )
                {
                    String str = new String( b, off, len );
                    log( urgency, source, str );
                }
            } );
    }


    /**
     *  Description of the Method
     *
     *@param  urgency  Description of Parameter
     *@return          Description of the Returned Value
     *@since
     */
    private static String urgencyToString( int urgency )
    {
        switch ( urgency )
        {
            case DEBUG:
                return "debug";
            case MESSAGE:
                return "message";
            case NOTICE:
                return "notice";
            case WARNING:
                return "warning";
            case ERROR:
                return "error";
        }

        throw new IllegalArgumentException( "Invalid urgency: " + urgency );
    }
}

