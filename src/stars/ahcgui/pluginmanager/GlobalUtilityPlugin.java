/*
 * Created on Nov 11, 2004
 *
 * Copyright (c) 2004, Steve Leach
 * 
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
 * 
 */
package stars.ahcgui.pluginmanager;

import javax.swing.JComponent;

/**
 * Global utility plugins are not specific to a particular game.  They are loaded into
 * the utilities tab of the main window and act completely independantly.
 * 
 * @author Steve Leach
 */
public interface GlobalUtilityPlugin extends PlugIn
{
   public JComponent getComponent();
}
