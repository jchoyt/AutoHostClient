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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
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
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;

import stars.ahc.ShipDesign;
import stars.ahc.Utils;
import stars.ahc.plugins.objedit.ShipDesignEditor;


/**
 * A stand-alone GUI for the BattleSimulation class
 * 
 * @author Steve Leach
 */
public class StandAloneBattleSimulator extends JFrame
{
   private static double version = 0.16;
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
   private StackTableModel stackTableModel;
   private JTable stacksTable;
   private AbstractAction addStackAction;
   private AbstractAction editStackAction;
   private AbstractAction removeStackAction;
   private Box controlPanel;
   private JFrame stackEditor = null;
   private ShipDesignEditor designEditor;
   private File currentSimFile = null;
   private JCheckBox showDesignsField;
   private AbstractAction newFileAction;
   private JFrame helpFrame = null;
   private AbstractAction helpAction;

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
      setupToolbar();
      setupControlPanel();
      setupResultsPanel();
      setupMainArea();
      setupStatusBar();      
      
      refreshControls();
   }
   
   private void setupWindow()
   {
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
      exitAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK) );

      newFileAction = new AbstractAction("New") {
         public void actionPerformed(ActionEvent event)
         {
            newFile();
         }
      };
      newFileAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK) );
      newFileAction.putValue( Action.SHORT_DESCRIPTION, "Start a new simulation" );
      
      openAction = new AbstractAction("Open") {
         public void actionPerformed(ActionEvent event)
         {
            openFileGui();
         }
      };
      openAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK) );
      openAction.putValue( Action.SHORT_DESCRIPTION, "Open a saved simulation" );

      saveAction = new AbstractAction("Save") {
         public void actionPerformed(ActionEvent event)
         {
            if (currentSimFile == null)
            {
               saveSimulationGui();
            }
            else
            {
               saveSimulation();
            }
         }
      };
      saveAction.setEnabled(false);
      saveAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK) );
      saveAction.putValue( Action.SHORT_DESCRIPTION, "Save simulation configuration" );
      
      copyAction = new AbstractAction("Copy") {
         public void actionPerformed(ActionEvent event)
         {
            copyToClipboard();
         }
      };
      copyAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK) );
      copyAction.putValue( Action.SHORT_DESCRIPTION, "Copy results to clipboard" );
      
      
      runSimAction = new AbstractAction("Run") {
         public void actionPerformed(ActionEvent event)
         {
            runSimulation();
         }
      };
      runSimAction.setEnabled( false );
      runSimAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0) );
      runSimAction.putValue( Action.SHORT_DESCRIPTION, "Run the simulation" );

      helpAction = new AbstractAction("Documentation") {
         public void actionPerformed(ActionEvent event)
         {
            showHelp();
         }
      };
      helpAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0) );
      
      aboutAction = new AbstractAction("About") {
         public void actionPerformed(ActionEvent event)
         {
            showAboutWindow();
         }
      };
      aboutAction.putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.ALT_MASK) );
      
      addStackAction = new AbstractAction("Add") {
         public void actionPerformed(ActionEvent event)
         {
            addStack();
         }
      };
      addStackAction.setEnabled(false);

      editStackAction = new AbstractAction("Edit") {
         public void actionPerformed(ActionEvent event)
         {
            editStack();
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

      fileMenu.add( new JMenuItem(newFileAction) );
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
      
      helpMenu.add( new JMenuItem(helpAction) );
      helpMenu.addSeparator();
      helpMenu.add( new JMenuItem(aboutAction) );
      
      setJMenuBar( mainMenu );
   }

   private void setupToolbar()
   {
      JToolBar toolbar = new JToolBar();
      
      toolbar.add( newFileAction );
      toolbar.add( openAction );
      toolbar.add( saveAction );
      toolbar.addSeparator();
      toolbar.add( copyAction );
      toolbar.addSeparator();
      toolbar.add( runSimAction );
      
      getContentPane().add( toolbar, BorderLayout.NORTH );
   }

   private void setupControlPanel()
   {
      controlPanel = Box.createVerticalBox();
      controlPanel.setBorder( new CompoundBorder( new EtchedBorder(), new EmptyBorder(4,4,4,4) ) );
      
//      Box startBox = Box.createHorizontalBox();
//      startBox.setBorder( new EmptyBorder(6,6,6,6) );
//      startBox.add( new JButton(runSimAction) );
//      startBox.add( Box.createHorizontalGlue() );
//      controlPanel.add( startBox );
      
      Box seedBox = Box.createHorizontalBox();
      seedBox.setBorder( new EmptyBorder(6,6,6,6) );
      seedBox.add( new JLabel("Random Seed: ") );
      seedField = new JTextField(10);
      seedField.setMaximumSize( new Dimension(100,20) );
      seedField.setToolTipText( "Random seed, or 0 for different each time" );
      seedBox.add( seedField );
      seedBox.add( Box.createHorizontalGlue() );
      controlPanel.add( seedBox );
      
      Box box = Box.createHorizontalBox();
      box.setBorder( new EmptyBorder(6,6,6,6) );
      showDesignsField = new JCheckBox("Show designs");
      showDesignsField.setSelected( true );
      box.add( showDesignsField );
      box.add( Box.createHorizontalGlue() );
      controlPanel.add( box );
      
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

      if (currentSimFile == null)
      {
         setTitle( "Stars! Battle Simulator" );
      }
      else
      {
         setTitle( "Stars! Battle Simulator - " + currentSimFile.getName() );
      }
      
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
   
   private void newFile()
   {      
      sim = new BattleSimulation();
      runSimAction.setEnabled( true );
      stackTableModel.setSimulation( sim );
      currentSimFile = null;
      saveAction.setEnabled( true );
      refreshControls();
      repaint();      
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

         refreshControls();
         
         setStatus( "Simulation opened: " + chooser.getSelectedFile().getName() );
      }
   }

   public void openSimulation( String fileName )
   {
      try
      {
         currentSimFile = new File(fileName);
         sim = new BattleSimulation( fileName );
         sim.addStatusListener( new TextAreaStatusListener(resultsArea) );
         runSimAction.setEnabled( true );
         stackTableModel.setSimulation( sim );
         saveAction.setEnabled( true );
         repaint();
      }
      catch (Throwable t)
      {
         logError( t );
         showError( t );
         sim = null;
      }
   }
   
   public void saveSimulationGui()
   {
      JFileChooser chooser = new JFileChooser();

      chooser.setDialogTitle( "Save simulation" );
      chooser.addChoosableFileFilter( new SimFileFilter() );
      chooser.setCurrentDirectory( new File(System.getProperty("user.home")) ); 
      
      int rc = chooser.showSaveDialog( this );
      
      if (rc == JFileChooser.APPROVE_OPTION)
      {
         saveSimulationAs( chooser.getSelectedFile() );
      }
   }
   
   public void saveSimulationAs( File newFile )
   {
      currentSimFile = newFile;
      saveSimulation();
   }
   
   public void saveSimulation()
   {
      try
      {
         backupSimFile();
         getFieldValues();
         sim.saveTo( currentSimFile.getAbsolutePath() );
         setStatus( "Simulation saved: " + currentSimFile.getName() );
      }
      catch (Throwable t)
      {
         logError( t );
         showError( t );
      }
   }
   
   private void backupSimFile()
   {
      if (currentSimFile == null)
      {
         return;
      }
      if (currentSimFile.exists())
      {
         String backupFileName = Utils.changeFileExtension( currentSimFile, ".bkp" );
         
         try
         {
            Utils.fileCopy( currentSimFile, new File(backupFileName) );
         }
         catch (IOException e)
         {
            logError( e );
         }
      }
   }
   
   private void runSimulation()
   {
      try
      {
         getFieldValues();

         resultsArea.setText("Stars! Battle Simulator v"+version+"\n\n");
         
         sim.reset();
         
         if (showDesignsField.isSelected())
         {
            showDesigns();
         }
         
         setStatus( "Running simulation..." );
         sim.showFinalSummary = false;
         
         sim.simulate();
         
         showResults();
         setStatus( "Simulation complete" );
      }
      catch (Throwable t)
      {
         logError( t );
         showError( t );
      }
   }
   
   public void showDesigns()
   {
      String text = "";
      
      for (int n = 0; n < sim.stackCount; n++)
      {
         ShipDesign design = sim.stacks[n].design;
         
         text += sim.stacks[n].owner + " ";
         text += design.getName() + " x ";
         text += sim.stacks[n].getShipCount();
         text += "\n";
         
         text += "   " + design.getHullName();
         text += ", Armour=" + design.getArmour();
         text += ", Shields=" + design.getShields();
         if (design.isRegenShields())
         {
            text += "(R)";
         }
         text += ", Speed=" + (design.getSpeed4() / 4.0);
         text += ", Initiative=" + design.getInitiative();
         text += ", Jamming=" + design.getJamming();
         text += ", Capacitors=" + design.getCapacitors();
         text += ", Deflectors=" + design.getDeflectors();
         text += ", Computers=";
         text += design.getComputers(ShipDesign.BATTLE_COMPUTER) + "/";
         text += design.getComputers(ShipDesign.SUPER_COMPUTER) + "/";
         text += design.getComputers(ShipDesign.BATTLE_NEXUS);
         
         text += "\n   ";
         if (design.getWeaponSlots() == 0)
         {
            text += "unarmed";
         }
         else
         {
            for (int s = 0; s < design.getWeaponSlots(); s++)
            {
               text += (s == 0) ? "" : ", ";
               text += design.getWeaponCount(s) + " x " + design.getWeaponName(s);
            }
         }
         
         text += "\n";
      }
      
      text += "\n";
      
      resultsArea.setText( resultsArea.getText() + text );
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
         
         if (stack.getShipCount() == 0)
         {
            text += "*dead*";
         }
         else
         {
            text += stack.getShipCount() + " left, ";
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
      				"Assistance from: LEit, Kotk, mazda, Ptolemy, Micha, et.al.\n" +
      				"Testing by: Fescar\n\n" +
      				"Visit the Academy forum at \n    http://starsautohost.org/sahforum/ \n\n" +
      				"Copyright (c) 2004, Steve Leach";   
      JOptionPane.showMessageDialog(this, text, title, JOptionPane.PLAIN_MESSAGE );
   }
   
   private void showHelp()
   {
      if (helpFrame == null)
      {
         helpFrame = new HelpFrame();
      }
      helpFrame.show();
   }
   
   private void stackSelected()
   {
      int index = stacksTable.getSelectedRow();
      
      if (sim == null) return;

      addStackAction.setEnabled( true );
      
      boolean stackSelected = (index >= 0) && (index < sim.stackCount);
      
      editStackAction.setEnabled( stackSelected );
      removeStackAction.setEnabled( stackSelected );
   }
   
   private void notImplemented()
   {
      JOptionPane.showMessageDialog(this, "Not yet implemented" );
   }
   
   private ShipStack getSelectedStack( boolean allowNull )
   {
      ShipStack stack = null;
      
      int row = stacksTable.getSelectedRow();
      
      if (row >= 0)
      {
         stack = sim.stacks[row];
      }
      
      if ((stack == null) && (allowNull == false))
      {
         stack = new ShipStack( new ShipDesign(), 0 );
      }
      
      return stack;
   }
   
   private void addStack()
   {
      ShipStack stack = sim.addNewStack( new ShipDesign(), 0 );
      stack.side = 1;
    
      int newRow = sim.stackCount - 1;
      stackTableModel.fireTableRowsInserted( newRow, newRow );
      stacksTable.getSelectionModel().setSelectionInterval( newRow, newRow );
      
      editStack();
   }

   private void logError( Throwable t)
   {
      PrintStream s;
      try
      {
         s = new PrintStream( new FileOutputStream("battlesim.err") );

         s.println( new Date() );
         s.println( t.getMessage() );
         
         s.println( "---------------------------------" );
         
         Properties props = System.getProperties();
         Enumeration keys = props.keys();
         while (keys.hasMoreElements())
         {
            String key = keys.nextElement().toString();
            String value = props.getProperty(key);
            s.println( key + "=" + value );
         }
         
         s.println( "---------------------------------" );
         t.printStackTrace(s);
         s.close();
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
   }
   
   private void showError( Throwable t )
   {      
      String title = "Stars! Battle Simulator v" + version;
      String text = "Error: " + t.getMessage() + "\n";
      
      StackTraceElement[] stackTrace = t.getStackTrace();
      for (int n = 0; n < stackTrace.length; n++)
      {
         if (stackTrace[n].getClassName().startsWith("stars."))
         {
            text += stackTrace[n].getClassName() + "." + stackTrace[n].getMethodName() +  
         		" [" + stackTrace[n].getFileName() + ":" + stackTrace[n].getLineNumber() + "]\n";
         }
      }
      
      JOptionPane.showMessageDialog(this, text, title, JOptionPane.PLAIN_MESSAGE );
   }
   
   private void editStack()
   {
      try
      {
	      if (stackEditor == null)
	      {
	         stackEditor = new JFrame();         
	         stackEditor.getContentPane().setLayout( new BorderLayout() );
	         stackEditor.setTitle( "Stack Editor" );
	         stackEditor.setLocation( 80, 80 );
	         stackEditor.setSize( 600, 400 );
	         
	         stackEditor.addWindowListener( new WindowAdapter() {
	            public void windowClosing(WindowEvent e)
	            {
	               getDesignChanges();
	            }
	         });

	         AbstractAction closeAction = new AbstractAction("Close") {
               public void actionPerformed(ActionEvent event)
               {
                  getDesignChanges();
                  stackEditor.setVisible(false);
               }
	         };
	         
	         JToolBar toolbar = new JToolBar();
	         toolbar.add( closeAction );
	         
	         stackEditor.getContentPane().add( toolbar, BorderLayout.NORTH );
	         
	         designEditor = new ShipDesignEditor();
	         designEditor.setupControls();
	         stackEditor.getContentPane().add( designEditor, BorderLayout.CENTER );
	      }
	      
	      designEditor.setDesign( getSelectedStack(false).design );
	      
	      stackEditor.show();
      }
      catch (Throwable t)
      {
         logError( t );
         showError( t );
      }
   }
   
   private void getDesignChanges()
   {
      if (designEditor != null)
      {
         designEditor.getFieldValues();               
         stackTableModel.fireTableDataChanged();
         stacksTable.repaint();
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
         case OWNER_COL:	return stack.design.getOwner();
         case NAME_COL:		return stack.design.getName();
         case SHIPS_COL:	return new Integer(stack.originalShipCount);
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
   public boolean isCellEditable(int row, int col)
   {
      switch (col)
      {
         case OWNER_COL:	return false;
         case NAME_COL:		return false;
         case SHIPS_COL:	return true;
         case SIDE_COL:		return true;
         default:			return false;
      }
   }
   
   public void setValueAt(Object obj, int row, int col)
   {
      ShipStack stack = sim.getStack(row);
      
      switch (col)
      {
         case SHIPS_COL:	
            stack.setShipCount( ((Integer)obj).intValue() );
            break;
         case SIDE_COL:		
            stack.side = ((Integer)obj).intValue();
            break;
      }
   }
}

class HelpFrame extends JFrame
{
   public HelpFrame()
   {
      setTitle( "Help for Battle Simulator" );
      setLocation( 20, 20 );
      setSize( 600, 600 );
      getContentPane().setLayout( new BorderLayout() );
      setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      
      JEditorPane editor = null;
      try
      {
         URL url = getClass().getClassLoader().getResource("battlesim.htm");
         if (url == null)
         {
            File helpFile = new File( "html/battlesim.htm" );
            url = helpFile.toURL();
         }
         editor = new JEditorPane(url);
         editor.setEditable( false );
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      
      getContentPane().add( new JScrollPane(editor), BorderLayout.CENTER );
   }
}

 