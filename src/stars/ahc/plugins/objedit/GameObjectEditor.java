/*
 * Created on Oct 17, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.objedit;

import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import stars.ahc.Game;
import stars.ahcgui.AhcFrame;

/**
 * @author Steve Leach
 *
 */
public class GameObjectEditor extends JFrame
{
   protected Game game;
   private static HashMap editors = new HashMap();
   private JTabbedPane tabsPane;

   /**
    */
   public static JFrame showEditor(Game game)
   {
      GameObjectEditor editor = null;
      
      editor = (GameObjectEditor)editors.get( game.getName() );
      
      if (editor == null)
      {
         editor = new GameObjectEditor( game );
         editors.put( game.getName(), editor );
      }
      
      return editor;
   }
   
   /**
    * Constructor is protected.  Use the static showEditor() method instead. 
    */
   protected GameObjectEditor( Game game )
   {
      this.game = game;
      init();
   }

   /**
    * 
    */
   private void init()
   {
      setTitle( "Planet/fleet editor for " + game.getName() );
      setLocation( 40, 40 );
      setSize( 620, 400 );
      
      AhcFrame.setWindowIcon(this);
      
      tabsPane = new JTabbedPane();

      addTabPane( "Planets", "stars.ahc.plugins.objedit.PlanetEditorPanel" );
      addTabPane( "Races", "stars.ahc.plugins.objedit.RaceEditorPanel" );
      addTabPane( "Designs", "stars.ahc.plugins.objedit.ShipDesignEditorPanel" );
      
      getContentPane().add( tabsPane );
   }

   /**
    * Adds a new tab pane to the editor window
    * <p>
    * If errors are thrown when creating a tab it is caught safely
    */
   private void addTabPane(String title, String editorClassName)
   {
      try
      {
         Class editorClass = this.getClass().getClassLoader().loadClass(editorClassName);
         
         ObjectEditorTab tab = (ObjectEditorTab)editorClass.newInstance();
         
         tab.initialize( game );
         
         tabsPane.add( title, (JComponent)tab );
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
   }

}
