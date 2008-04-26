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
package stars.ahcgui;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import stars.ahc.Utils;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    January 23, 2003
 */
public class AhcGui extends java.lang.Object
{

    /**
     *  Description of the Field
     */
    protected static JComboBox optionSelector;
    static JPanel gameCards;
    static JPanel gameOptionCards;
    public static AhcFrame mainFrame;
    static JLabel statusBox;



    /**
     *  Sets the gameCards attribute of the AhcGui object
     *
     *@param  _gameCards  The new gameCards value
     */
    public static void setGameCards( JPanel _gameCards )
    {
        gameCards = _gameCards;
    }



    /**
     *  Sets the gameOptionCards attribute of the AhcGui object
     *
     *@param  _gameOptionCards  The new gameOptionCards value
     */
    public static void setGameOptionCards( JPanel _gameOptionCards )
    {
        gameOptionCards = _gameOptionCards;
    }


    /**
     *  Sets the mainFrame attribute of the AhcGui object
     *
     *@param  _mainFrame  The new mainFrame value
     */
    public static void setMainFrame( AhcFrame _mainFrame )
    {
        mainFrame = _mainFrame;
    }


    /**
     *  Sets the optionSelector attribute of the AhcGui class
     *
     *@param  box  The new optionSelector value
     */
    public static void setOptionSelector( JComboBox box )
    {
        optionSelector = box;
    }


    /**
     *  Sets the status attribute of the AhcGui class
     *
     *@param  in  The new status value
     */
    public static void setStatus( String in )
    {
        statusBox.setText( Utils.getTime() + in );
    }


    /**
     *  Sets the statusBox attribute of the AhcGui class
     *
     *@param  in  The new statusBox value
     */
    public static void setStatusBox( JLabel in )
    {
        statusBox = in;
    }


    /**
     *  Gets the gameCards attribute of the AhcGui object
     *
     *@return    The gameCards value
     */
    public static JPanel getGameCards()
    {
        return gameCards;
    }


    /**
     *  Gets the gameOptionCards attribute of the AhcGui object
     *
     *@return    The gameOptionCards value
     */
    public static JPanel getGameOptionCards()
    {
        return gameOptionCards;
    }


    /**
     *  Gets the mainFrame attribute of the AhcGui object
     *
     *@return    The mainFrame value
     */
    public static AhcFrame getMainFrame()
    {
        return mainFrame;
    }


    /**
     *  Gets the optionSelector attribute of the AhcGui class
     *
     *@return    The optionSelector value
     */
    public static JComboBox getOptionSelector()
    {
        return optionSelector;
    }



    /**
     *  Adds a feature to the Option attribute of the AhcGui class
     *
     *@param  name     The feature to be added to the Option attribute
     *@param  newPane  The feature to be added to the Option attribute
     */
    public static void addOption( String name, JPanel newPane )
    {
        optionSelector.addItem( name );
        gameOptionCards.add( name, newPane );
    }
}

