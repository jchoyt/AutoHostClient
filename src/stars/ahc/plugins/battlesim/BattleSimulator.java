/*
 * Created on Nov 1, 2004
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
 * 
 */
package stars.ahc.plugins.battlesim;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import stars.ahc.Game;
import stars.ahc.ShipDesign;
import stars.ahc.Utils;
import stars.ahcgui.pluginmanager.PlugIn;

/**
 * @author Steve Leach
 *
 */
public class BattleSimulator extends JFrame implements PlugIn, BattleSimulationListener
{
   private Game game;
   private JTextArea results;
   private JComboBox design1chooser;
   private JComboBox design2chooser;
   private JTextField design1count;
   private JTextField design2count;
   private BattleBoard battleBoard;

   public BattleSimulator()
   {
      setupWindow();
      
      setupControls();
   }
   
   public void setGame( Game game )
   {
      this.game = game;
      
      refreshDesignList();
      
      setTitle( "Battle simulator - " + game.getName() );
   }

   private void setupWindow()
   {
      setTitle( "Battle simulator" );
      setLocation( 20, 20 );
      setSize( 800, 600 );
   }
   
   private void setupControls()
   {
      getContentPane().setLayout( new BorderLayout() );
      
      JPanel controlPanel = new JPanel( new GridBagLayout() );
      controlPanel.setBorder(
            BorderFactory.createCompoundBorder(
                  BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                  BorderFactory.createEmptyBorder(2,2,2,2)
                  )
      );
      
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.insets = new Insets(2,2,2,2);
      
      gbc.gridy = 0;
      
      String[] noDesigns = { "No designs" };
      
      gbc.gridy++;
      gbc.gridx = 1;
      controlPanel.add( new JLabel("Design 1:"), gbc );
      
      gbc.gridx = 2;
      design1chooser = new JComboBox(noDesigns);
      controlPanel.add( design1chooser, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      controlPanel.add( new JLabel("Quantity:"), gbc );

      gbc.gridx = 2;
      design1count = new JTextField( 5 );
      design1count.setText( "10" );
      controlPanel.add( design1count, gbc );
      
      gbc.gridy++;
      gbc.gridx = 1;
      controlPanel.add( new JLabel("Design 2:"), gbc );

      gbc.gridx = 2;
      design2chooser = new JComboBox(noDesigns);
      controlPanel.add( design2chooser, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      controlPanel.add( new JLabel("Quantity:"), gbc );

      gbc.gridx = 2;
      design2count = new JTextField( 5 );
      design2count.setText( "10" );
      controlPanel.add( design2count, gbc );
      
      
      Action runAction = new AbstractAction( "Run simulation" ) {
         public void actionPerformed(ActionEvent event)
         {
            runSimulation();
         }
      };
      
      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 2;
      
      controlPanel.add( new JButton(runAction), gbc );

      gbc.gridy++;
      gbc.weighty = 1;
      controlPanel.add( new JLabel(" "), gbc );
      
      getContentPane().add( controlPanel, BorderLayout.WEST );
      
      results = new JTextArea();
      results.setEditable( false );
      results.setBorder( new BevelBorder(BevelBorder.LOWERED) );
      
      JScrollPane scroller = new JScrollPane(results);
      
      getContentPane().add( scroller, BorderLayout.CENTER );
      
      battleBoard = new BattleBoard();
      
      getContentPane().add( battleBoard, BorderLayout.EAST );
   }
   
   private void refreshDesignList()
   {
      design1chooser.removeAllItems();
      design2chooser.removeAllItems();
      
      int designCount = game.getShipDesignCount();
      
      for (int n = 0; n < designCount; n++)
      {
         ShipDesign design = game.getShipDesign(n);
         
         String s = design.getName() + " [" + design.getOwner() + " ]";
         
         design1chooser.addItem( s );
         design2chooser.addItem( s );
      }
   }
   
   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Battle simulator";
   }
   
   public void runSimulation()
   {      
      results.setText( "" );
      
      int choice1 = design1chooser.getSelectedIndex();
      int choice2 = design2chooser.getSelectedIndex();
      
      int qty1 = Utils.safeParseInt( design1count.getText(), 0 );
      int qty2 = Utils.safeParseInt( design2count.getText(), 0 );
      
      BattleSimulation sim = new BattleSimulation();
      
      sim.addNewStack( game.getShipDesign(choice1), qty1, 1 );
      sim.addNewStack( game.getShipDesign(choice2), qty2, 2 );
      
      sim.addStatusListener( this );
      
      battleBoard.setSimulation( sim );
      
      BattleSimThread thread = new BattleSimThread( sim, this );
            
      thread.start();
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.battlesim.StatusListener#battleStatusUpdate(int, java.lang.String)
    */
   public void battleStatusUpdate(int round, String message)
   {
      results.setText( results.getText() + "Round " + round + " : " + message + "\n"); 
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.battlesim.BattleSimulationListener#handleNotification(stars.ahc.plugins.battlesim.BattleSimulationNotification)
    */
   public void handleNotification(BattleSimulationNotification notification)
   {
      results.setText( results.getText() + "Round " + notification.round + " : " + notification.message + "\n"); 
   }
   
   public void addResult( String s )
   {
      results.setText( results.getText() + s );
      //results.setCaretPosition( results.getText().length() );
   }
}

class BattleSimThread extends Thread
{
   private BattleSimulation simulation;
   private BattleSimulator simulator;

   public BattleSimThread( BattleSimulation sim, BattleSimulator simulator )
   {
      this.simulation = sim;
      this.simulator = simulator;
   }
   
   public void run()
   {
      try
      {
         simulation.simulate();
      }
      catch (BattleSimulationError e)
      {
         simulator.addResult( "Error: " + e.getMessage() + "\n" );         
      }
   }
   
}
