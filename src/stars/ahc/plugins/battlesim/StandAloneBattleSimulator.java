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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
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

   public static void main( String[] args ) throws Exception
   {
      if (args.length == 1)
      {
         BattleSimulation sim = new BattleSimulation(args[0]);

         sim.addStatusListener( new ConsoleStatusListener() );
         
         sim.simulate();
      }
      else
      {
         // Create and show the GUI on the Swing event-dispatch thread
         javax.swing.SwingUtilities.invokeLater(new Runnable() 
         {
            public void run() 
            {
               StandAloneBattleSimulator simWin = new StandAloneBattleSimulator();         
            }
         });         
      }
   }
   
   public StandAloneBattleSimulator()
   {
      setupWindow();
      setupActions();
      setupMenu();
      setupResultsPanel();
      
      setVisible( true );
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

      saveAction = new AbstractAction("Save") {
         public void actionPerformed(ActionEvent event)
         {
         }
      };
      
      runSimAction = new AbstractAction("Run") {
         public void actionPerformed(ActionEvent event)
         {
            runSimulation();
         }
      };
      
      aboutAction = new AbstractAction("About") {
         public void actionPerformed(ActionEvent event)
         {
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
   
   private void exit()
   {
      this.dispose();
   }
   
   private void openFileGui()
   {
      JFileChooser chooser = new JFileChooser();
      
      chooser.setCurrentDirectory( new File(System.getProperty("java.io.tmpdir")) ); 
      
      int rc = chooser.showOpenDialog( this );
      
      if (rc == JFileChooser.APPROVE_OPTION)
      {
         try
         {
            sim = new BattleSimulation( chooser.getSelectedFile().getAbsolutePath() );
         }
         catch (Throwable t)
         {
            t.printStackTrace();
         }         
      }
   }
   
   private void runSimulation()
   {
      try
      {
         resultsArea.setText("");
         sim.addStatusListener( new TextAreaStatusListener(resultsArea) );
         sim.simulate();
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
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
      textArea.setText( textArea.getText() + "\n" + notification.message );
   }
   
}