/*
 *  This file is part of the AutoHost Client - [What it does in brief]
 *  Copyright (c) 2003 Jeffrey Hoyt
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
import javax.swing.JPanel;

import stars.ahc.Game;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    December 25, 2002
 */
public class GamePanelFactory extends java.lang.Object
{
    /**
     * Creates a new Game Panel for the game.
     *
     * @author jchoyt
     */
    public static JPanel createPanel( Game game )
    {
        return new GamePanel( game );
    }
}

