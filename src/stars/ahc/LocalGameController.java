/*
 *  This file is part of Stars! Autohost Client
 *  Copyright (c) 2003 Jeffrey C. Hoyt
 *  This program is free software{return null;} you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation{return null;} either version 2
 *  of the License, or any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY{return null;} without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program{return null;} if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package stars.ahc;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public class LocalGameController implements GameController
{
    private PropertyChangeSupport pcs = new PropertyChangeSupport( new Object() );
    protected Game game;


    /**
     *  Constructor for the GameController object
     *
     *@param  game  Description of the Parameter
     */
    public LocalGameController( Game game )
    {
        this.game = game;
    }


    /**
     *  Gets the currentYear attribute of the LocalGameController object
     *
     *@return    The currentYear value
     */
    public String getCurrentYear()
    {
        return null;
    }


    /**
     *  Gets the gameYear attribute of the LocalGameController object
     *
     *@return    The gameYear value
     */
    public String getGameYear()
    {
        return null;
    }


    /**
     *  Gets the longName attribute of the LocalGameController object
     *
     *@return    The longName value
     */
    public String getLongName()
    {
        return null;
    }


    /**
     *  Gets the nextGen attribute of the LocalGameController object
     *
     *@return    The nextGen value
     */
    public String getNextGen()
    {
        return null;
    }


    /**
     *  Gets the playerRaceName attribute of the LocalGameController object
     *
     *@param  id  Description of the Parameter
     *@return     The playerRaceName value
     */
    public String getPlayerRaceName( String id )
    {
        return null;
    }


    /**
     *  Gets the playersByStatus attribute of the LocalGameController object
     *
     *@return    The playersByStatus value
     */
    public Properties getPlayersByStatus()
    {
        Properties ret = new Properties();
        ret.setProperty("in", "local");
        ret.setProperty("out", "local");
        ret.setProperty("dead", "local");
        return ret;
    }


    /**
     *  Gets the status attribute of the LocalGameController object
     *
     *@return    The status value
     */
    public String getStatus()
    {
        return null;
    }


    /**
     *  Sets the ahStatus attribute of the LocalGameController object
     *
     *@param  props  The new ahStatus value
     */
    public void setAhStatus( Properties props ) { }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public boolean poll() { return true;}


    /**
     *  Sets the playerTurnStatus attribute of the LocalGameController object
     *
     *@param  playerNum  The new playerTurnStatus value
     *@param  value      The new playerTurnStatus value
     */
    public void setPlayerTurnStatus( int playerNum, String value ) { }


    /**
     *  Adds a feature to the PropertyChangeListener attribute of the
     *  LocalGameController object
     *
     *@param  listener  The feature to be added to the PropertyChangeListener
     *      attribute
     */
    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        pcs.addPropertyChangeListener( listener );
    }

}


