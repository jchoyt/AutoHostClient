/*
 * Created on Oct 31, 2004
 *
 * Copyright (c) 2004, Steve Leach
 * 
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
package stars.ahc.plugins.objedit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import stars.ahc.Game;
import stars.ahc.ShipDesign;

/**
 * @author Steve Leach
 *
 */
public class ShipDesignEditorPanel extends JPanel
{
   private Game game;
   private Vector designNames;
   private JList designsList;
   private JTextField nameField;
   private JTextField ownerField;
   private JComboBox hullTypeField;

   public ShipDesignEditorPanel( Game game )
   {
      this.game = game;
      
      init();
   }

   /**
    * 
    */
   private void init()
   {
      setLayout( new BorderLayout() );
      
      add( createToolbar(), BorderLayout.NORTH ); 
      
      JPanel listPanel = createListPanel();
      JPanel editPanel = createEditPanel();
      
      JSplitPane splitter = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, listPanel, editPanel );
      splitter.setOneTouchExpandable(true);
      splitter.setDividerLocation(150);
      
      add( splitter, BorderLayout.CENTER );
   }

   private JComponent createToolbar()
   {
      JPanel toolbar = new JPanel();
      
      toolbar.setLayout( new FlowLayout() );
      
      Action newDesignAction = new AbstractAction("New Design") {
         public void actionPerformed(ActionEvent event)
         {
            newDesign();
         }
      };
      
      toolbar.add( new JButton(newDesignAction) );
      
      Action battleSimAction = new AbstractAction("Battle Sim") {
         public void actionPerformed(ActionEvent event)
         {
            notImplemented();
         }
      };

      toolbar.add( new JButton(battleSimAction) );
      
      return toolbar;
   }
   
   private void notImplemented()
   {
      JOptionPane.showMessageDialog( getParent(), "Not yet implemented" );
   }
   
   private JPanel createListPanel()
   {
      JPanel listPanel = new JPanel( new BorderLayout() );
      
      designsList = new JList();
      
      designsList.addListSelectionListener( new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent event)
         {
            refreshFields();
         }
      });
      
      refreshList();
      
      listPanel.add( designsList, BorderLayout.CENTER );
      
      return listPanel;
   }

   private JPanel createEditPanel()
   {
      JPanel editPanel = new JPanel( new GridBagLayout() );
      editPanel.setBorder( new EmptyBorder(3,3,3,3) ); 
      
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.insets = new Insets(2,2,2,2);
      
      gbc.gridy = 1;
      gbc.gridx = 1;
      editPanel.add( new JLabel("Design:"), gbc );

      gbc.gridx++;
      nameField = new JTextField(30);
      editPanel.add( nameField, gbc );
      
      
      gbc.gridy++;
      gbc.gridx = 1;
      editPanel.add( new JLabel("Owner:"), gbc );
      
      gbc.gridx++;
      ownerField = new JTextField(30);
      editPanel.add( ownerField, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      editPanel.add( new JLabel("Hull Type:"), gbc );
      
      gbc.gridx++;      
      hullTypeField = new JComboBox(ShipDesign.getHullTypeNames());
      editPanel.add( hullTypeField, gbc );
      
      Action saveAction = new AbstractAction("Save") {
         public void actionPerformed(ActionEvent event)
         {
            saveChanges();
         }
      };
      
      gbc.gridy++;
      gbc.gridx = 1;
      editPanel.add( new JButton(saveAction), gbc );
      
      gbc.gridy++;
      gbc.gridx = 1;
      gbc.weightx = 1;
      gbc.weighty = 1;
      editPanel.add( new JLabel(" "), gbc );
      
      refreshFields();
      
      return editPanel;
   }
   
   private void newDesign()
   {
      ShipDesign design = new ShipDesign( ShipDesign.HULLTYPE_SCOUT, "new design" );
      design.setOwner( "unknown" );
      
      game.addShipDesign( design );      
      
      refreshList();
      refreshFields();
   }

   /**
    * 
    */
   private void refreshList()
   {
      designNames = new Vector();

      designsList.removeAll();

      for (int n = 0; n < game.getShipDesignCount(); n++)
      {
         ShipDesign design = game.getShipDesign(n);
         designNames.add( design.getOwner() + " -- " + design.getName() );
      }
      
      designsList.setListData( designNames );

      if (designsList.getSelectedIndex() < 0)
      {
         designsList.setSelectedIndex(0);
      }
   }
   
   private void refreshFields()
   {
      if (nameField == null)
      {         
         return;	// edit panel hasn't been created yet
      }
      
      int index = designsList.getSelectedIndex();
      
      ShipDesign design = game.getShipDesign( index );
      
      if (design == null)
      {
         design = new ShipDesign(ShipDesign.HULLTYPE_SCOUT,"");
      }
      
      nameField.setText( design.getName() );
      ownerField.setText( design.getOwner() );
      
      hullTypeField.setSelectedIndex( design.getHullType() );
   }
   
   private void saveChanges()
   {
      int index = designsList.getSelectedIndex();
      
      ShipDesign design = game.getShipDesign( index );

      design.setName( nameField.getText().trim() );
      design.setOwner( ownerField.getText().trim() );
      design.setHullType( hullTypeField.getSelectedIndex() );
      
      game.saveUserDefinedProperties();
      
      refreshList();
   }
}
