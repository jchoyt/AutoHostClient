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
import java.beans.*;

import java.io.*;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import stars.ahcgui.AhcGui;
/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public class Player extends Object
{
    Game game;
    String id;
    long lastDownload;
    long lastUpload;
    boolean needsDownload;
    boolean needsUpload;
    PropertyChangeSupport pcs;
    String starsPassword;
    boolean toUpload;
    String uploadPassword;


    /**
     *  Constructor for the Player object
     */
    public Player()
    {
        pcs = new PropertyChangeSupport( this );
        id = "";
        starsPassword = "";
        uploadPassword = "";
        needsDownload = false;
        needsUpload = false;
        toUpload = false;
        lastDownload = 0;
        lastUpload = 0;
    }


    /**
     *  Sets the game attribute of the Player object
     *
     *@param  game  The new game value
     */
    public void setGame( Game game )
    {
        this.game = game;
    }


    /**
     *  Sets the id attribute of the Player object
     *
     *@param  id  The new id value
     */
    public void setId( String id )
    {
        String oldValue = this.id;
        this.id = id;
        //pcs.firePropertyChange( "id", oldValue, id );
    }


    /**
     *  Sets the lastDownload attribute of the Player object
     *
     *@param  lastDownload  The new lastDownload value
     */
    public void setLastDownload( long lastDownload )
    {
        long oldValue = this.lastDownload;
        this.lastDownload = lastDownload;
        pcs.firePropertyChange( "lastDownload", new Long( oldValue ), new Long( lastDownload ) );
    }


    /**
     *  Sets the lastUpload attribute of the Player object
     *
     *@param  lastUpload  The new lastUpload value
     */
    public void setLastUpload( long lastUpload )
    {
        long oldValue = this.lastUpload;
        this.lastUpload = lastUpload;
        pcs.firePropertyChange( "lastUpload", new Long( oldValue ), new Long( lastUpload ) );
    }


    /**
     *  Sets the needsDownload attribute of the Player object
     *
     *@param  needsDownload  The new needsDownload value
     */
    public void setNeedsDownload( boolean needsDownload )
    {
        boolean oldValue = this.getNeedsDownload();
        this.needsDownload = needsDownload;
        pcs.firePropertyChange( "needsDownload", oldValue, needsDownload );
    }


    /**
     *  Sets the needsUpload attribute of the Player object
     *
     *@param  needsUpload  The new needsUpload value
     */
    public void setNeedsUpload( boolean needsUpload )
    {
        boolean oldValue = this.getNeedsUpload();
        this.needsUpload = needsUpload;
        pcs.firePropertyChange( "needsUpload", oldValue, needsUpload );
    }


    /**
     *  Sets the starsPassword attribute of the Player object
     *
     *@param  starsPassword  The new starsPassword value
     */
    public void setStarsPassword( String starsPassword )
    {
        String oldValue = this.starsPassword;
        this.starsPassword = starsPassword;
        //pcs.firePropertyChange( "starsPassword", oldValue, starsPassword );
    }


    /**
     *  Sets the toUpload attribute of the Player object
     *
     *@param  toUpload  The new toUpload value
     */
    public void setToUpload( boolean toUpload )
    {
        boolean oldValue = this.toUpload;
        this.toUpload = toUpload;
        pcs.firePropertyChange( "toUpload", oldValue, toUpload );
    }


    /**
     *  Sets the uploadPassword attribute of the Player object
     *
     *@param  uploadPassword  The new uploadPassword value
     */
    public void setUploadPassword( String uploadPassword )
    {
        String oldValue = this.uploadPassword;
        this.uploadPassword = uploadPassword;
        //pcs.firePropertyChange( "uploadPassword", oldValue, uploadPassword );
    }


    /**
     *  Gets the ahStatus attribute of the Player object
     *
     *@return    The ahStatus value
     */
    public String getAhStatus()
    {
        String ret = game.ahStatus.getProperty( "player" + id + "-turn" );
        if ( ret == null )
        {
            Log.log( Log.NOTICE, this, "Player " + id + " could not be found.  Couldn't pull a property with the key \"player" + id + "-turn\"" );
            throw new NullPointerException( "Player " + id + " could not be found." );
        }
        if ( ret.equals( "waiting" ) )
        {
            ret = " <font color=\"blue\">";
            if ( !game.getGameYear().equals( getMFileYear() ) )
            {
                ret += "New turn available";
            }
            else if ( game.getGameYear().equals( getXFileYear() ) )
            {
                ret += "Out, x-file ready for upload";
            }
            else
            {
                ret += "Out, turn not done";
            }
            ret += "</font>";
        }
        else if ( ret.equals( "inactive" ) )
        {
            ret = " <font color=\"gray\">Inactive";
        }
        else if ( ret.startsWith( "in" ) )
        {
            ret = " <font color=\"green\">In";
            //+ ret.substring( 3 ) +
            if ( !toUpload )
            {
                ret += "; not uploading this player";
            }
            else if ( AHPoller.xFileIsNewer( this ) )
            {
                ret += ", <i>latest x-file NOT uploaded</i>";
            }
            else
            {
                ret += ", latest x-file uploaded";
            }
            ret += "</font>";
        }
        else if ( ret.startsWith( "dead" ) )
        {
            ret = " <font color=\"gray\">Dead or Banned</font>";
        }
        return ret;
    }


    /**
     *  Gets the game attribute of the Player object
     *
     *@return    The game value
     */
    public Game getGame()
    {
        return game;
    }


    /**
     *  Gets the id attribute of the Player object
     *
     *@return    The id value
     */
    public String getId()
    {
        return id;
    }


    /**
     *  Gets the lastDownload attribute of the Player object
     *
     *@return    The lastDownload value
     */
    public long getLastDownload()
    {
        return lastDownload;
    }


    /**
     *  Gets the lastUpload attribute of the Player object
     *
     *@return    The lastUpload value
     */
    public long getLastUpload()
    {
        return lastUpload;
    }


    /**
     *  Gets the localMfile attribute of the Player object
     *
     *@return    The localMfile value
     */
    public File getLocalMfile()
    {
        return new File( game.getDirectory(), game.getName() + ".m" + id );
    }


    /**
     *  Gets the localStatus attribute of the Player object
     *
     *@return    The localStatus value
     */
    public String getLocalStatus()
    {
        String ahStatus = getAhStatus();
        /*
         *  uploaded, skipped, Turn not submitted
         */
        return ahStatus;
    }


    /**
     *  Gets the mFileDate attribute of the Player object
     *
     *@return    The mFileDate value
     */
    public long getMFileDate()
    {
        File playerMFile = new File( game.getDirectory(), getTurnFileName() );
        return playerMFile.lastModified();
    }


    /**
     *  Gets the mFileYear attribute of the Player object
     *
     *@return    The mFileYear value
     */
    public String getMFileYear()
    {
        File playerMFile = new File( game.getDirectory(), getTurnFileName() );
        return getFileYear( playerMFile );
    }


    /**
     *  Gets the needsDownload attribute of the Player object
     *
     *@return    The needsDownload value
     */
    public boolean getNeedsDownload()
    {
        return needsDownload;
    }


    /**
     *  Gets the needsUpload attribute of the Player object
     *
     *@return    The needsUpload value
     */
    public boolean getNeedsUpload()
    {
        return needsUpload;
    }


    /**
     *  Gets the raceName attribute of the Player object
     *
     *@return    The raceName value
     */
    public String getRaceName()
    {
        String name = game.getPlayerRaceName( id );
        if ( name == null )
        {
            name = "Player " + id;
        }
        return name;
    }


    /**
     *  Gets the starsPassword attribute of the Player object
     *
     *@return    The starsPassword value
     */
    public String getStarsPassword()
    {
        return starsPassword;
    }


    /**
     *  Gets the toUpload attribute of the Player object
     *
     *@return    The toUpload value
     */
    public boolean getToUpload()
    {
        return toUpload;
    }


    /**
     *  Gets the turnFileName attribute of the Player object
     *
     *@return    The turnFileName value
     */
    public String getTurnFileName()
    {
        return game.getName() + ".m" + id;
    }


    /**
     *  Gets the uploadPassword attribute of the Player object
     *
     *@return    The uploadPassword value
     */
    public String getUploadPassword()
    {
        return uploadPassword;
    }


    /**
     *  Gets the xFileDate attribute of the Player object
     *
     *@return    The xFileDate value
     */
    public long getXFileDate()
    {
        File playerXFile = new File( game.getDirectory(), getXFileName() );
        return playerXFile.lastModified();
    }


    /**
     *  Gets the xFileName attribute of the Player object
     *
     *@return    The xFileName value
     */
    public String getXFileName()
    {
        return game.getName() + ".x" + id;
    }


    /**
     *  Gets the xFileYear attribute of the Player object
     *
     *@return    The xFileYear value
     */
    public String getXFileYear()
    {
        File playerXFile = new File( game.getDirectory(), getXFileName() );
        return getFileYear( playerXFile );
    }


    /**
     *  Adds a feature to the PropertyChangeListener attribute of the Player
     *  object
     *
     *@param  listener  The feature to be added to the PropertyChangeListener
     *      attribute
     */
    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        pcs.addPropertyChangeListener( listener );
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String memoryLoc()
    {
        return super.toString();
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
        ret.append( "Game=" + game.getName() + lineEnding );
        ret.append( "Id=" + id + lineEnding );
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
        StringBuffer ret = new StringBuffer();
        ret.append( game.getName() + ".player" + id + ".lastUpload=" + lastUpload + lineEnding );
        ret.append( game.getName() + ".player" + id + ".StarsPassword=" + starsPassword + lineEnding );
        ret.append( game.getName() + ".player" + id + ".UploadPassword=" + uploadPassword + lineEnding );
        ret.append( game.getName() + ".player" + id + ".upload=" + toUpload + lineEnding );
        out.write( ret.toString() );
    }


    /**
     *  Gets the fileYear attribute of the Player object
     *
     *@param  file  Description of the Parameter
     *@return       The fileYear value
     */
    protected String getFileYear( File file )
    {
        String year = "";
        try
        {
            InputStream instream = new FileInputStream( file );
            Reader in = new InputStreamReader( instream );
            year = Utils.getTurnNumber( in );
            in.close();
            instream.close();
        }
        catch ( IOException e )
        {
            Log.log( Log.MESSAGE, this, "Couldn't connect to AutoHost - returning nothing" );
            AhcGui.setStatus( "Couldn't get the file from AutoHost.  Are you connected to the internet?" );
            return year;
        }
        return year;
    }

}

