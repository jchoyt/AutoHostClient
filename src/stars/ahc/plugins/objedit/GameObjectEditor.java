/*
 * Created on Oct 17, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.objedit;

import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import stars.ahc.Game;

/**
 * @author Steve Leach
 *
 */
public class GameObjectEditor extends JFrame
{
   protected Game game;
   private static HashMap editors = new HashMap();

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
      
      JTabbedPane tabsPane = new JTabbedPane();
      
      tabsPane.addTab( "Planets", new PlanetEditorPanel(game) );
      tabsPane.addTab( "Fleets", new JPanel() );
      tabsPane.addTab( "Races", new RaceEditorPanel(game) );
      
      getContentPane().add( tabsPane );
   }
}
