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
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import stars.ahc.Game;
import stars.ahc.Log;
import stars.ahc.Utils;
import stars.ahcgui.AhcFrame;
import stars.ahcgui.pluginmanager.PlugInManager;

/**
 * @author Steve Leach
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
   private JButton copyButton;
   private JLabel statusBar;
   private JPanel blankControlPanel;
   private JComponent currentControlPanel = null;

   /**
    * Displays an Analyzer window for running reports about a specific Stars! game.
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
      
      AhcFrame.setWindowIcon(this);
   }

   private void setupReports()
   {
      PlugInManager manager = PlugInManager.getPluginManager();
      
      ArrayList plugins = manager.getPlugins( AnalyzerReport.class );
      
      for (int n = 0; n < plugins.size(); n++)
      {
         Class reportClass = (Class)plugins.get(n);

         AnalyzerReport report = (AnalyzerReport)PlugInManager.getPluginManager().newInstance(reportClass);
         reports.add( report );         
      }
   }
   
   private void initControls()
   {
      JPanel controlPanel = new JPanel();      
      
      controlPanel.setLayout( new FlowLayout(FlowLayout.LEFT) );
      
      JLabel label = new JLabel( "Report:" );
      controlPanel.add( label );
      
      String[] reportNames = new String[ reports.size() ];
      for (int n = 0; n < reportNames.length; n++)
      {
         reportNames[n] = ((AnalyzerReport)reports.get(n)).getDescription();
      }
      
      combo = new JComboBox( reportNames );
      combo.addActionListener( this );
      controlPanel.add( combo );
    
      runButton = new JButton( "Run" );
      runButton.addActionListener( this );
      runButton.setToolTipText( "Run the selected report" );
      controlPanel.add( runButton );
      
      copyButton = new JButton( "Copy" );
      copyButton.addActionListener( this );
      copyButton.setEnabled( false );
      copyButton.setToolTipText( "Copy report text to clipboard" );
      controlPanel.add( copyButton );
      
      saveButton = new JButton( "Save..." );
      saveButton.addActionListener( this );
      saveButton.setEnabled( false );
      saveButton.setToolTipText( "Save report text to disk" );
      controlPanel.add( saveButton );

      printButton = new JButton( "Print" );
      printButton.addActionListener( this );
      printButton.setEnabled( false );
      printButton.setToolTipText( "Print report to default printer" );
      controlPanel.add( printButton );
      
      getContentPane().add( controlPanel, BorderLayout.NORTH );
      
      text = new JTextArea();
      text.setFont( new Font("Courier",0,12) );
      JScrollPane scroller = new JScrollPane( text );
      
      text.setEditable( false );

      getContentPane().add( scroller, BorderLayout.CENTER );
      
      statusBar = new JLabel("Ready");
      getContentPane().add( statusBar, BorderLayout.SOUTH );
      
      showReportControls();
   }
   
   private AnalyzerReport getSelectedReport()
   {
      int reportIndex = combo.getSelectedIndex();
      return (AnalyzerReport)reports.get(reportIndex);      
   }
   
   /**
    * 
    */
   private void showReportControls()
   {
      if (currentControlPanel != null)
      {
         getContentPane().remove( currentControlPanel );
      }

      AnalyzerReport report = getSelectedReport();
      
      if (report instanceof ConfigurableReport)
      {
         ConfigurableReport rpt = (ConfigurableReport)report;
         rpt.initControls( game );
         currentControlPanel = rpt.getControls();
      }
      else
      {
         if (blankControlPanel == null)
         {
            blankControlPanel = new JPanel();
         }
         
         currentControlPanel = blankControlPanel;
      }
      

      getContentPane().add( currentControlPanel, BorderLayout.EAST );
      
      validate();
   }

   public void setStatus( String message )
   {
      statusBar.setText( message );
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
      if (event.getSource() == combo)
      {
         showReportControls();
      }
      else if (event.getSource() == runButton)
      {
         runReport();
      }
      else if (event.getSource() == copyButton)
      {
         copyReportToClipboard();
      }
      else if (event.getSource() == saveButton)
      {
         saveReportText();
      }
      else if (event.getSource() == printButton)
      {
         printReportText();
      }
   }

   /**
    * 
    */
   private void copyReportToClipboard()
   {
      text.setSelectionStart(0);
      text.setSelectionEnd( text.getText().length() );
      text.copy();
      setStatus( "Report text copied to clipboard." );
   }

   /**
    * 
    */
   private void runReport()
   {
      AnalyzerReport report = getSelectedReport();
      
      try
      {
         String output = report.run( game, new Properties() );
         
         text.setText( output );
         
         enableButtons();
         
         setStatus( "Report completed: " + report.getDescription() );
      }
      catch (AnalyzerReportError e)
      {
         text.setText( "Error: " + e.getMessage() );
      }
      
      text.setCaretPosition( 0 );
   }

   /**
    * 
    */
   private void enableButtons()
   {
      boolean enabled = Utils.empty( text.getText() ) == false;
      
      copyButton.setEnabled( enabled );
      saveButton.setEnabled( enabled );
      printButton.setEnabled( enabled );
   }

   private void saveReportText()
   {
      JFileChooser chooser = new JFileChooser( "." );
      chooser.setDialogTitle( "Save report text" );
      //chooser.setFileFilter( new FileExtensionFilter(".txt","Text files (*.txt)") );
      chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );      
      
      int result = chooser.showSaveDialog( this );
      
      if (result == JFileChooser.APPROVE_OPTION)
      {
         File file = chooser.getSelectedFile();
       
         saveReportText( file );
      }
   }      
   
   private void saveReportText( File file )
   {
      try
      {
         PrintStream ps = new PrintStream( new FileOutputStream( file ) );
         ps.print( text.getText() );
         ps.close();
      }
      catch (Throwable t)
      {
         Log.log( Log.ERROR, this, "An error occurred while saving the report" );               
         JOptionPane.showMessageDialog( this, "An error occurred while saving the report" );
      }      
   }

   private void printReportText()
   {
      Properties printProperties = new Properties();
      PrinterJob job = PrinterJob.getPrinterJob();

      if (job != null)
      {
	      ReportPrinter rp = new ReportPrinter();
	      rp.setReportText( text.getText() );
	      
	      try
	      {
	         rp.printReport( job );
	         
	         setStatus( "Report printed" );
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
      }
   }
   
}
