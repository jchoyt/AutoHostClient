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

import java.beans.PropertyChangeListener;
import java.util.Properties;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    November 27, 2002
 */
public interface GameController
{
    /**
     *  Gets the currentYear attribute of the GameController object
     *
     *@return    The currentYear value
     */
    public String getCurrentYear();


    /**
     *  Gets the gameYear attribute of the GameController object
     *
     *@return    The gameYear value
     */
    public String getGameYear();


    /**
     *  Gets the longName attribute of the GameController object
     *
     *@return    The longName value
     */
    public String getLongName();


    /**
     *  Gets the nextGen attribute of the GameController object
     *
     *@return    The nextGen value
     */
    public String getNextGen();


    /**
     *  Gets the playerRaceName attribute of the GameController object
     *
     *@param  id  Description of the Parameter
     *@return     The playerRaceName value
     */
    public String getPlayerRaceName( String id );


    /**
     *  Gets the playersByStatus attribute of the GameController object
     *
     *@return    The playersByStatus value
     */
    public Properties getPlayersByStatus();


    /**
     *  Gets the status attribute of the GameController object
     *
     *@return    The status value
     */
    public String getStatus();


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public int poll();


    /**
     *  Sets the playerTurnStatus attribute of the GameController object
     *
     *@param  playerNum  The new playerTurnStatus value
     *@param  value      The new playerTurnStatus value
     */
    public void setPlayerTurnStatus( int playerNum, String value );


    /**
     *  Adds a feature to the PropertyChangeListener attribute of the
     *  GameController object
     *
     *@param  listener  The feature to be added to the PropertyChangeListener
     *      attribute
     */
    public void addPropertyChangeListener( PropertyChangeListener listener );

    public int getPollInterval();

    /**
     * Gets the status properties for the game
     */
    public void loadStatusProperties( Properties statusProperties );
    
    /**
     * @return the name of this controller
     */
    public String getControllerName();
    
    /**
     * Forces an immediate poll, if the controller supports this functionality 
     */
    public int pollNow();
}

