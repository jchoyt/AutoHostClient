/*
 * Created on Nov 13, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.mapanimator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import stars.ahc.Game;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapFrame;

/**
 * @author Steve
 *
 */
public class MapAnimatorWindowImplementation extends JFrame implements MapAnimatorWindow
{

   private MapConfig mapConfig;
   private Game game;
   private MapFrame mapFrame;
   private JButton startButton;
   private JTextArea messages;

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.mapanimator.MapAnimatorWindow#initialize(stars.ahc.plugins.map.MapConfig, stars.ahc.Game, stars.ahc.plugins.map.MapFrame)
    */
   public void initialize(MapConfig mapConfig, Game game, MapFrame mapFrame)
   {
      this.mapConfig = mapConfig;
      this.game = game;
      this.mapFrame = mapFrame;
      
      initWindow();
      initControls();
   }

   private void initWindow()
   {
      setSize( 780, 580 );
      setTitle( "Stars! AutoHost Client Map Animator" );
   }

   private void initControls()
   {
      getContentPane().setLayout( new BorderLayout() );
      
      startButton = new JButton( "Start" );
      startButton.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent event)
         {
            createAnimation();
         }         
      });
      getContentPane().add( startButton, BorderLayout.NORTH );
      
      messages = new JTextArea( 300, 300 );
      JScrollPane scroller = new JScrollPane( messages );
      getContentPane().add( scroller, BorderLayout.CENTER );
   }
   
   private void addMessage( String msg )
   {
      messages.setText( messages.getText() + "\n" + msg );
   }
   
   private void createAnimation()
   {
      int firstYear = 2400;
      //int lastYear = game.getYear();
      
      try
      {
         System.out.println( "Starting..." );
         addMessage( "Starting..." );

         AnimationGenerator ag = new AnimationGenerator();
         
         AnimationConfiguration config = new AnimationConfiguration();
         config.height = 400;
         config.width = 400;
         config.frameRate = 1;
         config.lastFrame = game.getYear() - 2400;         
         
         StarsMapAnimationImageGenerator generator = new StarsMapAnimationImageGenerator();
         
         generator.game = game;
         
         generator.mapConfig = new MapConfig();
         generator.mapConfig.mapScale = 0.4;
         generator.mapConfig.year = 2400;
         
         generator.setConfiguration( config );
         
         ag.generateAnimation( generator, "test.mov" );
         
         System.out.println( "Complete" );
         addMessage( "Complete" );
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
      
   }
}
