/*
 * Created on Nov 22, 2004
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
package stars.ahc.plugins.battlesim;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import stars.ahc.Utils;


/**
 * A stand-alone GUI for the BattleSimulation class
 * 
 * @author Steve Leach
 */
public class StandAloneBattleSimulator extends JFrame
{
   private Action exitAction;
   private AbstractAction openAction;
   private AbstractAction runSimAction;
   private AbstractAction saveAction;
   private AbstractAction aboutAction;
   private JTextArea resultsArea;
   private BattleSimulation sim;
   private AbstractAction copyAction;
   private JLabel statusLabel;

   public static void main( String[] args ) throws Exception
   {      
      StandAloneBattleSimulator simWin = new StandAloneBattleSimulator();         
      
      if (args.length > 0)
      {
         simWin.openSimulation( args[0] );
      }
      
      simWin.setVisible( true );
   }
   
   public StandAloneBattleSimulator()
   {
      setupWindow();
      setupActions();
      setupMenu();
      setupResultsPanel();
      setupStatusBar();
   }
   
   private void setupWindow()
   {
      JFrame.setDefaultLookAndFeelDecorated(true);
      
      setTitle( "Stars! Battle Simulator" );
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocation( 20, 20 );
      setSize( 400, 400 );
      
      getContentPane().setLayout( new BorderLayout() );
   }
   
   private void setupActions()
   {
      exitAction = new AbstractAction("Exit") {
         public void actionPerformed(ActionEvent event)
         {
            exit();
         }
      };
      
      openAction = new AbstractAction("Open") {
         public void actionPerformed(ActionEvent event)
         {
            openFileGui();
         }
      };
      openAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK) );

      saveAction = new AbstractAction("Save") {
         public void actionPerformed(ActionEvent event)
         {
         }
      };
      saveAction.setEnabled(false);
      
      copyAction = new AbstractAction("Copy") {
         public void actionPerformed(ActionEvent event)
         {
            copyToClipboard();
         }
      };
      copyAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK) );
      
      
      runSimAction = new AbstractAction("Run") {
         public void actionPerformed(ActionEvent event)
         {
            runSimulation();
         }
      };
      runSimAction.setEnabled( false );
      runSimAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0) );
      
      aboutAction = new AbstractAction("About") {
         public void actionPerformed(ActionEvent event)
         {
            showAboutWindow();
         }
      };
      
   }
   
   private void setupMenu()
   {
      JMenuBar mainMenu = new JMenuBar();
      
      JMenu fileMenu = new JMenu("File");      
      mainMenu.add( fileMenu );

      fileMenu.add( new JMenuItem(openAction) );
      fileMenu.add( new JMenuItem(saveAction) );
      fileMenu.addSeparator();
      fileMenu.add( new JMenuItem(exitAction) );

      JMenu editMenu = new JMenu("Edit");
      mainMenu.add( editMenu );
      
      editMenu.add( new JMenuItem(copyAction) );
      
      JMenu simMenu = new JMenu("Simulation");
      mainMenu.add( simMenu );

      simMenu.add( new JMenuItem(runSimAction) );
      
      JMenu helpMenu = new JMenu("Help");
      mainMenu.add( helpMenu );
      
      helpMenu.add( new JMenuItem(aboutAction) );
      
      setJMenuBar( mainMenu );
   }
   
   private void setupResultsPanel()
   {
      resultsArea = new JTextArea();
      
      getContentPane().add( new JScrollPane(resultsArea), BorderLayout.CENTER );
   }
   
   private void setupStatusBar()
   {
      Box statusBar = Box.createHorizontalBox();
      
      statusLabel = new JLabel("Ready");
      
      statusBar.add( statusLabel );
      statusBar.add( Box.createHorizontalGlue() );
      
      getContentPane().add( statusBar, BorderLayout.SOUTH );
   }
   
   public void setStatus( String statusText )
   {
      statusLabel.setText( statusText );
   }
   
   private void exit()
   {
      this.dispose();
   }
   
   private void openFileGui()
   {
      JFileChooser chooser = new JFileChooser();

      chooser.setDialogTitle( "Open simulation" );
      chooser.addChoosableFileFilter( new SimFileFilter() );
      chooser.setCurrentDirectory( new File(System.getProperty("user.home")) ); 
      
      int rc = chooser.showOpenDialog( this );
      
      if (rc == JFileChooser.APPROVE_OPTION)
      {
         openSimulation( chooser.getSelectedFile().getAbsolutePath() );
         
         setStatus( "Simulation opened: " + chooser.getSelectedFile().getAbsolutePath() );
      }
   }

   public void openSimulation( String fileName )
   {
      try
      {
         sim = new BattleSimulation( fileName );
         sim.addStatusListener( new TextAreaStatusListener(resultsArea) );
         runSimAction.setEnabled( true );
      }
      catch (Throwable t)
      {
         t.printStackTrace();
         sim = null;
      }
   }
   
   private void runSimulation()
   {
      try
      {
         setStatus( "Running simulation..." );
         resultsArea.setText("Stars! Battle Simulator\n\n");
         
         sim.showFinalSummary = false;
         
         sim.simulate();
         
         showResults();
         setStatus( "Simulation complete" );
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
   }
   
   private void showResults()
   {
      String text = "\n\n";
      
      for (int n = 0; n < sim.stackCount; n++)
      {
         ShipStack stack = sim.getStack(n);
         text += stack.owner + " ";
         text += stack.design.getName() + " : ";
         
         if (stack.shipCount == 0)
         {
            text += "*dead*";
         }
         else
         {
            text += stack.shipCount + " left, ";
            text += stack.getDamagePercent() + "% damage, ";
            text += stack.shields + " shields left";
         }
         
         text += "\n";
      }
      
      resultsArea.setText( resultsArea.getText() + text );
   }
   
   private void copyToClipboard()
   {
      if (Utils.empty(resultsArea.getText()))
      {
         setStatus( "Nothing to copy" );
         return;
      }
      
      if (Utils.empty(resultsArea.getSelectedText()))
      {
         resultsArea.setSelectionStart(0);
         resultsArea.setSelectionEnd( resultsArea.getText().length() );
      }
      resultsArea.copy();
      setStatus( resultsArea.getSelectedText().length() + " characters copied to clipboard" );
   }
   
   private void showAboutWindow()
   {
      String title = "Stars! Battle Simulator";
      String text = "An Open Source Project\n" + 
      				"Lead developer: Steve Leach\n" +
      				"Assistance from: LEit, Kotk, mazda, Ptolemy, Micha, et.al.\n\n" +
      				"Visit the Academy forum at \n    http://starsautohost.org/sahforum/ \n\n" +
      				"Copyright (c) 2004, Steve Leach";   
      JOptionPane.showMessageDialog(this, text, title, JOptionPane.PLAIN_MESSAGE );
   }
}

class ConsoleStatusListener implements BattleSimulationListener
{
   public void handleNotification(BattleSimulationNotification notification)
   {
      if (notification.round == 0)
      {
         System.out.println( notification.message );
      }
      else
      {
         System.out.println( "Round " + notification.round + " : " + notification.message );
      }
   }   
}

class TextAreaStatusListener implements BattleSimulationListener
{   
   private JTextArea textArea;

   public TextAreaStatusListener( JTextArea textArea )
   {
      this.textArea = textArea;
   }
   
   /* (non-Javadoc)
    * @see stars.ahc.plugins.battlesim.BattleSimulationListener#handleNotification(stars.ahc.plugins.battlesim.BattleSimulationNotification)
    */
   public void handleNotification(BattleSimulationNotification notification)
   {
      textArea.setText( textArea.getText() + "\nRound " + notification.round + " : " + notification.message );
   }
   
}

class SimFileFilter extends FileFilter
{
   /* (non-Javadoc)
    * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
    */
   public boolean accept(File file)
   {
      return file.getName().toLowerCase().endsWith(".sim");
   }

   /* (non-Javadoc)
    * @see javax.swing.filechooser.FileFilter#getDescription()
    */
   public String getDescription()
   {
      return "Sim files";
   }
   
}