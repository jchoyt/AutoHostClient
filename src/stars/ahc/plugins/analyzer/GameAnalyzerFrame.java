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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.PlugInManager;

/**
 * @author Steve Leach
 *
 */
public class GameAnalyzerFrame extends JFrame implements ActionListener
{
   private static Map analyzers = new HashMap();
   private Game game;
   private JComboBox combo;
   private JButton runButton;
   private JButton saveButton;
   private JButton printButton;
   private JTextArea text;
   private ArrayList reports = new ArrayList();

   /**
    */
   public static JFrame showAnalyzer(Game game)
   {
      GameAnalyzerFrame result = (GameAnalyzerFrame)analyzers.get(game.getName());
      
      if (result == null)
      {
         result = new GameAnalyzerFrame( game );
         analyzers.put( game.getName(), result );
      }
      
      result.show();
      
      return result;
   }

   /**
    * Only constructor is private.
    * <p>
    * To create an instance, use the static showAnalyzer() method 
    */
   private GameAnalyzerFrame( Game game )
   {
      this.game = game;
      init();
   }
   
   /**
    * 
    */
   private void init()
   {
      initFrame();
      setupReports();
      initControls();
   }

   /**
    * 
    */
   private void initFrame()
   {
      setSize( 600, 400 );
      setLocation( 20, 20 );
      setTitle( "Analyzing " + game.getName() + " (" + game.getCurrentYear() + ") " );
      getContentPane().setLayout( new BorderLayout() );
   }

   private void setupReports()
   {
      PlugInManager manager = PlugInManager.getPluginManager();
      
      ArrayList plugins = manager.getPlugins( AnalyzerReport.class );
      
      for (int n = 0; n < plugins.size(); n++)
      {
         Class reportClass = (Class)plugins.get(n);
         
         try
         {
            AnalyzerReport report = (AnalyzerReport)reportClass.newInstance();
            reports.add( report );
         }
         catch (InstantiationException e)
         {
         }
         catch (IllegalAccessException e)
         {
         }
      }
   }
   
   private void initControls()
   {
      JPanel controlPanel = new JPanel();      
      
      //controlPanel.setLayout( new BoxLayout(controlPanel,BoxLayout.X_AXIS) );
      controlPanel.setLayout( new FlowLayout(FlowLayout.LEFT) );
      
      JLabel label = new JLabel( "Report:" );
      controlPanel.add( label );
      
      String[] reportNames = new String[ reports.size() ];
      for (int n = 0; n < reportNames.length; n++)
      {
         reportNames[n] = ((AnalyzerReport)reports.get(n)).getDescription();
      }
      
      combo = new JComboBox( reportNames );
      controlPanel.add( combo );
    
      runButton = new JButton( "Run" );
      runButton.addActionListener( this );
      controlPanel.add( runButton );
      
      saveButton = new JButton( "Save" );
      saveButton.addActionListener( this );
      saveButton.setEnabled( false );
      controlPanel.add( saveButton );

      printButton = new JButton( "Print" );
      printButton.addActionListener( this );
      printButton.setEnabled( false );
      controlPanel.add( printButton );
      
      getContentPane().add( controlPanel, BorderLayout.NORTH );
      
      text = new JTextArea();
      text.setFont( new Font("Courier",0,12) );
      JScrollPane scroller = new JScrollPane( text );
      
      text.setEditable( false );

      getContentPane().add( scroller, BorderLayout.CENTER );
   }
   
   /**
    * Load properties
    */
   public void loadProperties( Properties props )
   {
      
   }

   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent event)
   {
      if (event.getSource() == runButton)
      {
         runReport();
      }
   }

   /**
    * 
    */
   private void runReport()
   {
      int reportIndex = combo.getSelectedIndex();
      
      AnalyzerReport report = (AnalyzerReport)reports.get(reportIndex);
      
      try
      {
         String output = report.run( game, new Properties() );
         
         text.setText( output );
      }
      catch (AnalyzerReportError e)
      {
         text.setText( "Error: " + e.getMessage() );
      }
      
      text.setCaretPosition( 0 );
   }

}
