/*
 * Created on Oct 7, 2004
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
package stars.ahc.plugins.map;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.GamePanelButtonExecutionError;
import stars.ahcgui.pluginmanager.GamePanelButtonPlugin;

/**
 * @author Steve Leach
 *
 */
public class GamePanelViewMapButton implements GamePanelButtonPlugin
{
   private boolean enabled = true;
   private Game game = null;
   
   /* (non-Javadoc)
    * @see stars.ahcgui.GamePanelButtonPlugin#getButtonText()
    */
   public String getButtonText()
   {
      return "View Map";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.GamePanelButtonPlugin#execute()
    */
   public void execute() throws GamePanelButtonExecutionError
   {
      try
      {
         MapFrame.viewGameMap( game );
      }
      catch (MapDisplayError e)
      {
         throw new GamePanelButtonExecutionError( "Error displaying map", e );
      }      
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.PlugIn#getName()
    */
   public String getName()
   {
      return "View map button";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Game panel button to view the map";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return enabled;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean active)
   {
      this.enabled = active;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.GamePanelButtonPlugin#init(stars.ahc.Game)
    */
   public void init(Game game)
   {
      this.game = game;
   }

}
