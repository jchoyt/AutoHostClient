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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;

import stars.ahc.Utils;


/**
 * A stand-alone GUI for the BattleSimulation class
 * 
 * @author Steve Leach
 */
public class StandAloneBattleSimulator extends JFrame
{
   private static double version = 0.1;
   private Action exitAction;
   private AbstractAction openAction;
   private AbstractAction runSimAction;
   private AbstractAction saveAction;
   private AbstractAction aboutAction;
   private JTextArea resultsArea;
   private BattleSimulation sim;
   private AbstractAction copyAction;
   private JLabel statusLabel;
   private JTextField seedField;
   private String currentFileName = null;
   private StackTableModel stackTableModel;
   private JTable stacksTable;
   private AbstractAction addStackAction;
   private AbstractAction editStackAction;
   private AbstractAction removeStackAction;
   private Box controlPanel;

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
      setupControlPanel();
      setupResultsPanel();
      setupMainArea();
      setupStatusBar();      
      
      refreshControls();
   }
   
   private void setupWindow()
   {
      JFrame.setDefaultLookAndFeelDecorated(true);
      
      setTitle( "Stars! Battle Simulator" );
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocation( 20, 20 );
      
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      setSize( d.width - 40, d.height-100 );
      
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
      
      addStackAction = new AbstractAction("Add") {
         public void actionPerformed(ActionEvent event)
         {
            notImplemented();
         }
      };
      addStackAction.setEnabled(false);

      editStackAction = new AbstractAction("Edit") {
         public void actionPerformed(ActionEvent event)
         {
            notImplemented();
         }
      };
      editStackAction.setEnabled(false);
   
      removeStackAction = new AbstractAction("Remove") {
         public void actionPerformed(ActionEvent event)
         {
            notImplemented();
         }
      };
      removeStackAction.setEnabled(false);
      
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
   
   private void setupControlPanel()
   {
      controlPanel = Box.createVerticalBox();
      controlPanel.setBorder( new CompoundBorder( new EtchedBorder(), new EmptyBorder(4,4,4,4) ) );
      
      Box startBox = Box.createHorizontalBox();
      startBox.setBorder( new EmptyBorder(6,6,6,6) );
      startBox.add( new JButton(runSimAction) );
      startBox.add( Box.createHorizontalGlue() );
      controlPanel.add( startBox );
      
      Box seedBox = Box.createHorizontalBox();
      seedBox.setBorder( new EmptyBorder(6,6,6,6) );
      seedBox.add( new JLabel("Random Seed: ") );
      seedField = new JTextField(10);
      seedField.setMaximumSize( new Dimension(100,20) );
      seedField.setToolTipText( "Random seed, or 0 for different each time" );
      seedBox.add( seedField );
      seedBox.add( Box.createHorizontalGlue() );
      controlPanel.add( seedBox );
      
      controlPanel.add( Box.createVerticalStrut(8) );
      
      Box stacksPanel = Box.createVerticalBox();
      stacksPanel.setPreferredSize( new Dimension(200,200) );
      stacksPanel.setBorder( new BevelBorder(BevelBorder.LOWERED) );
      Box stacksTitleBox = Box.createHorizontalBox();
      stacksTitleBox.setBorder( new EmptyBorder(3,3,3,3) );
      stacksTitleBox.add( new JLabel("Ship Stacks (Tokens)") );
      stacksPanel.add( stacksTitleBox );
      stackTableModel = new StackTableModel();
      stacksTable = new JTable(stackTableModel);
      stacksTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent event)
         {
            if (event.getValueIsAdjusting() == false)
            {
               stackSelected();
            }
         }
      } );
      stacksPanel.add( new JScrollPane(stacksTable) );
      controlPanel.add( stacksPanel );
            
      Box stackButtonBox = Box.createHorizontalBox();
      stackButtonBox.setBorder( new EmptyBorder(3,3,3,3) );
      stackButtonBox.add( new JButton(addStackAction) );
      stackButtonBox.add( new JButton(editStackAction) );
      stackButtonBox.add( new JButton(removeStackAction) );
      stackButtonBox.add( Box.createHorizontalGlue() );
      stacksPanel.add( stackButtonBox );
      
      controlPanel.add( Box.createVerticalGlue() );
   }
   
   private void refreshControls()
   {
      BattleSimulation s = (sim == null) ? new BattleSimulation() : sim;
      
      seedField.setText( ""+s.randomSeed );
      
      stackSelected();
   }
   
   private void setupResultsPanel()
   {
      resultsArea = new JTextArea();
   }
   
   private void setupMainArea()
   {
      JSplitPane splitter = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, controlPanel, resultsArea );
      splitter.setOneTouchExpandable(true);
      
      getContentPane().add( splitter, BorderLayout.CENTER );
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

         currentFileName = chooser.getSelectedFile().getAbsolutePath(); 
         
         setTitle( "Stars! Battle Simulator - " + chooser.getSelectedFile().getName() );
         
         refreshControls();
         
         setStatus( "Simulation opened: " + chooser.getSelectedFile().getName() );
      }
   }

   public void openSimulation( String fileName )
   {
      try
      {
         sim = new BattleSimulation( fileName );
         sim.addStatusListener( new TextAreaStatusListener(resultsArea) );
         runSimAction.setEnabled( true );
         stackTableModel.setSimulation( sim );
         repaint();
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
         getFieldValues();
         
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
   
   private void getFieldValues()
   {
      if (sim != null)
      {
         sim.randomSeed = Utils.safeParseLong( seedField.getText() );
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
      String title = "Stars! Battle Simulator v" + version;
      String text = "An Open Source Project\n" + 
      				"Lead developer: Steve Leach\n" +
      				"Assistance from: LEit, Kotk, mazda, Ptolemy, Micha, et.al.\n\n" +
      				"Visit the Academy forum at \n    http://starsautohost.org/sahforum/ \n\n" +
      				"Copyright (c) 2004, Steve Leach";   
      JOptionPane.showMessageDialog(this, text, title, JOptionPane.PLAIN_MESSAGE );
   }
   
   private void stackSelected()
   {
      int index = stacksTable.getSelectedRow();
      
      if (sim == null) return;

      addStackAction.setEnabled( true );
      
      editStackAction.setEnabled( index < sim.stackCount );
      removeStackAction.setEnabled( index < sim.stackCount );
   }
   
   private void notImplemented()
   {
      JOptionPane.showMessageDialog(this, "Not yet implemented" );
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
      return file.isDirectory() || file.getName().toLowerCase().endsWith(".sim");
   }

   /* (non-Javadoc)
    * @see javax.swing.filechooser.FileFilter#getDescription()
    */
   public String getDescription()
   {
      return "Sim files";
   }   
}

class StackTableModel extends AbstractTableModel
{
   private final int OWNER_COL = 0;
   private final int NAME_COL = 1;
   private final int SHIPS_COL = 2;
   private final int SIDE_COL = 3;
   private final int COLCOUNT = 4;
   
   private BattleSimulation sim;
   
   public StackTableModel()
   {
      this.sim = new BattleSimulation();
   }
   
   public void setSimulation( BattleSimulation sim )
   {
      this.sim = sim;
      fireTableDataChanged();
   }
   
   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getColumnCount()
    */
   public int getColumnCount()
   {
      return COLCOUNT;
   }

   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getRowCount()
    */
   public int getRowCount()
   {
      return sim.stackCount;
   }

   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getValueAt(int, int)
    */
   public Object getValueAt(int row, int col)
   {
      if (row >= sim.stackCount) return "";
      
      ShipStack stack = sim.getStack(row);
      
      if (stack == null) return "";
      
      switch (col)
      {
         case OWNER_COL:	return stack.owner;
         case NAME_COL:		return stack.design.getName();
         case SHIPS_COL:	return new Integer(stack.shipCount);
         case SIDE_COL:		return new Integer(stack.side);
         default:			return "";
      }
   }
   
   public Class getColumnClass(int col)
   {
      switch (col)
      {
         case OWNER_COL:	return String.class;
         case NAME_COL:		return String.class;
         case SHIPS_COL:	return Integer.class;
         case SIDE_COL:		return Integer.class;
         default:			return String.class;
      }
   }
   
   public String getColumnName(int col)
   {
      switch (col)
      {
         case OWNER_COL:	return "Owner";
         case NAME_COL:		return "Name";
         case SHIPS_COL:	return "Ships";
         case SIDE_COL:		return "Side";
         default:			return "";
      }
   }
}
