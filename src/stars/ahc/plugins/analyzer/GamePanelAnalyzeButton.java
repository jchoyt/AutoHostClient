/*
 * Created on Oct 14, 2004
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
*/
package stars.ahc.plugins.analyzer;

import javax.swing.JFrame;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.GamePanelButtonExecutionError;
import stars.ahcgui.pluginmanager.GamePanelButtonPlugin;

/**
 * @author Steve Leach
 *
 */
public class GamePanelAnalyzeButton implements GamePanelButtonPlugin
{
   private Game game = null;
   private boolean enabled = true;
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.GamePanelButtonPlugin#getButtonText()
    */
   public String getButtonText()
   {
      return "Reports";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.GamePanelButtonPlugin#init(stars.ahc.Game)
    */
   public void init(Game game)
   {
      this.game = game;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.GamePanelButtonPlugin#execute()
    */
   public void execute() throws GamePanelButtonExecutionError
   {
      try
      {
         JFrame analyzer = GameAnalyzerFrame.showAnalyzer( game );
      }
      catch (Throwable t)
      {
         throw new GamePanelButtonExecutionError( "Error displaying analyzer", t );
      }      
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Analyze game panel button";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Analyze game panel button";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return enabled;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

}
