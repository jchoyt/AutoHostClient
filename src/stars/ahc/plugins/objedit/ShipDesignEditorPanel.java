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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import stars.ahc.Game;
import stars.ahc.ShipDesign;
import stars.ahc.Utils;
import stars.ahc.Weapon;
import stars.ahc.plugins.battlesim.BattleSimulator;

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
   private JTextField massField;
   private JTextField armourField;
   private JTextField shieldsField;
   private JCheckBox regenShieldsField;
   private JTextField moveField;
   private JTextField initiativeField;
   private JList weaponsList;
   private JTextField weaponsCountField;
   private JComboBox weaponsTypeList;
   private JTable weaponsTable;

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
      Box toolbar = Box.createHorizontalBox();
      toolbar.setBorder( new EmptyBorder(2,2,2,2) );
      
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
            BattleSimulator battleSim = new BattleSimulator();
            battleSim.setGame( game );
            battleSim.show();
         }
      };

      toolbar.add( Box.createHorizontalStrut(2) );
      
      toolbar.add( new JButton(battleSimAction) );
      
      Action saveAction = new AbstractAction("Save") {
         public void actionPerformed(ActionEvent event)
         {
            saveChanges();
         }
      };
      
      toolbar.add( Box.createHorizontalStrut(2) );
      
      toolbar.add( new JButton(saveAction) );
      
      toolbar.add( Box.createHorizontalGlue() );
      
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
      JPanel editPanel = new JPanel( new BorderLayout() );
      
      JPanel fieldPanel = new JPanel( new GridBagLayout() );
      fieldPanel.setBorder( new EmptyBorder(3,3,3,3) ); 
      
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.insets = new Insets(2,2,2,2);
      
      gbc.gridy = 1;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Design:"), gbc );

      gbc.gridx++;
      gbc.gridwidth = 4;
      nameField = new JTextField(30);
      fieldPanel.add( nameField, gbc );
      
      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Owner:"), gbc );
      
      gbc.gridx++;
      gbc.gridwidth = 4;
      ownerField = new JTextField(30);
      fieldPanel.add( ownerField, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Hull Type:"), gbc );
      
      gbc.gridx++;     
      gbc.gridwidth = 4;
      hullTypeField = new JComboBox(ShipDesign.getHullTypeNames());
      fieldPanel.add( hullTypeField, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Mass:"), gbc );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      massField = new JTextField(5);
      fieldPanel.add( massField, gbc );
      
      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Armour:"), gbc );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      armourField = new JTextField(5);
      fieldPanel.add( armourField, gbc );

      gbc.gridx++;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Shields:"), gbc );
      
      gbc.gridx++;      
      shieldsField = new JTextField(5);
      fieldPanel.add( shieldsField, gbc );
      
      gbc.gridx++;      
      regenShieldsField = new JCheckBox("Regen");
      fieldPanel.add( regenShieldsField, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Move:"), gbc );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      moveField = new JTextField(5);
      fieldPanel.add( moveField, gbc );

      gbc.gridx++;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Initiative:"), gbc );
      
      gbc.gridx++;      
      initiativeField = new JTextField(5);
      fieldPanel.add( initiativeField, gbc );
            
      editPanel.add( fieldPanel, BorderLayout.NORTH );
      
      editPanel.add( getWeaponsBox(), BorderLayout.CENTER );
      
      refreshFields();
      
      return editPanel;
   }
   
   private JComponent getWeaponsBox()
   {
      Box weaponsBox = Box.createVerticalBox();
      
      weaponsBox.setBorder( BorderFactory.createTitledBorder("Weapons") );

      weaponsTable = new JTable( new WeaponsTableModel(this) );
      weaponsTable.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED) );
      weaponsTable.setRowHeight( 22 );

      JComboBox weaponTypeSelector = new JComboBox();
      for (int n = 0; n < Weapon.getAllWeapons().length; n++)
      {
         weaponTypeSelector.addItem( Weapon.getAllWeapons()[n].name );
      }
      
      weaponsTable.getColumnModel().getColumn(0).setCellEditor( new DefaultCellEditor(weaponTypeSelector) );
      
      weaponsTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
      
      weaponsTable.getColumnModel().getColumn(0).setWidth( 200 );
      
      JScrollPane scroller = new JScrollPane(weaponsTable);
      
      weaponsBox.add(scroller);
      
      weaponsBox.add( Box.createVerticalStrut(2) );
      
      Box weaponsControls = Box.createHorizontalBox();

      Action addWeaponAction = new AbstractAction("Add") {
         public void actionPerformed(ActionEvent event)
         {
            addWeapon();
         }
      };
      
      weaponsControls.add( new JButton(addWeaponAction) );
      
      weaponsControls.add( Box.createHorizontalGlue() );
      
      weaponsBox.add( weaponsControls );
      
      weaponsBox.add( Box.createVerticalGlue() );
      
      return weaponsBox;
   }


   private void addWeapon()
   {
      ShipDesign design = getCurrentDesign(true);
      
      if (design == null) return;
      
      if (design.getWeaponSlots() < design.getMaxSlots())
      {
         design.addWeapon( Weapon.COLLOIDAL_PHASER, 1 );
         weaponsTable.revalidate();
      }
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
      
      ShipDesign design = getCurrentDesign(false);
      
      nameField.setText( design.getName() );
      ownerField.setText( design.getOwner() );      
      hullTypeField.setSelectedIndex( design.getHullType() );
      massField.setText( ""+design.getMass() );
      armourField.setText( ""+design.getArmour() );
      shieldsField.setText( ""+design.getShields() );
      moveField.setText( ""+(design.getSpeed4()/4) );
      initiativeField.setText( ""+design.getInitiative() );
      regenShieldsField.setSelected( design.isRegenShields() );
      
      weaponsTable.repaint();
   }
   
   void saveChanges()
   {
      int index = designsList.getSelectedIndex();
      
      ShipDesign design = game.getShipDesign( index );

      design.setName( nameField.getText().trim() );
      design.setOwner( ownerField.getText().trim() );
      design.setHullType( hullTypeField.getSelectedIndex() );
      design.setMass( Utils.safeParseInt(massField.getText(),0) );
      design.setArmour( Utils.safeParseInt(armourField.getText(),0) );
      design.setShields( Utils.safeParseInt(shieldsField.getText(),0) );
      design.setInitiative( Utils.safeParseInt(initiativeField.getText(),0) );
      design.setBattleSpeed( Utils.safeParseFloat(moveField.getText(),0)  );
      design.setRegenShields( regenShieldsField.isSelected() );
      
      game.saveUserDefinedProperties();
      
      refreshList();
   }
   
   /**
    * Returns the currently selected ship design.
    * <p>
    * If allowNull is false and there is no current design then a new empty design is returned.  
    */
   public ShipDesign getCurrentDesign( boolean allowNull )
   {
      ShipDesign design;
      
      int index = designsList.getSelectedIndex();
      
      design = game.getShipDesign( index );

      if ((design == null) && (allowNull == false))
      {
         design = new ShipDesign();
      }

      return design;
   }
}

class WeaponsTableModel extends AbstractTableModel
{
   private ShipDesignEditorPanel editor;

   public WeaponsTableModel( ShipDesignEditorPanel editor )
   {
      this.editor = editor;
   }
   
   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getColumnCount()
    */
   public int getColumnCount()
   {
      return 2;
   }

   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getRowCount()
    */
   public int getRowCount()
   {
      ShipDesign design = editor.getCurrentDesign(false);
      return design.getWeaponSlots();
   }

   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getValueAt(int, int)
    */
   public Object getValueAt(int row, int col)
   {
      switch (col)
      {
         case 0:	return editor.getCurrentDesign(false).getWeaponName(row);
         case 1:	return new Integer( editor.getCurrentDesign(false).getWeaponCount(row) );
         default:	return "";
      }
   }
   
   public Class getColumnClass(int col)
   {
      switch (col)
      {
         case 0: 	return String.class;
         case 1: 	return Integer.class;
         default: 	return null;
      }
   }
   
   public boolean isCellEditable(int row, int col)
   {
      return true;
   }
   
   
   public void setValueAt(Object value, int row, int col)
   {
      ShipDesign design = editor.getCurrentDesign(true);
      if (design == null)
      {
         return;
      }
      
      switch (col)
      {
         case 0:
            String newName = value.toString();
            Weapon wpn = Weapon.getWeaponByName(newName);
            design.setWeapon( row, design.getWeaponCount(row), wpn );
            break;
         case 1:
            int newCount = ((Integer)value).intValue();
            design.setWeaponCount(row,newCount);
            break;
      }
   }
   public String getColumnName(int col)
   {
      switch (col)
      {
         case 0: 	return "Weapon";
         case 1: 	return "Count";
         default:	return null;
      }
   }
}