/*
 * Created on Oct 17, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.objedit;

import javax.swing.JFrame;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.GamePanelButtonExecutionError;
import stars.ahcgui.pluginmanager.GamePanelButtonPlugin;

/**
 * @author Steve
 *
 */
public class ObjectEditorButton extends Object implements GamePanelButtonPlugin
{

   private Game game;

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.GamePanelButtonPlugin#getButtonText()
    */
   public String getButtonText()
   {
      return "Data Editor";
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
         JFrame editor = GameObjectEditor.showEditor( game );
         editor.show();
      }
      catch (Throwable t)
      {
         throw new GamePanelButtonExecutionError( "Error displaying object editor", t );
      }      
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Object editor";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return getName();
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return true;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
   }

}
