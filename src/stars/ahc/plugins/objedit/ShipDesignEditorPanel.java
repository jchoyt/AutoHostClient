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
public class ShipDesignEditorPanel extends JPanel implements ObjectEditorTab
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
   private JTextField capacitorsField;
   private JTextField deflectorsField;
   private JTextField jammersField;
   private JTextField bcompField;
   private JTextField bscField;
   private JTextField nexusField;

   public ShipDesignEditorPanel( Game game )
   {
      initialize( game );
   }

   public ShipDesignEditorPanel()
   {      
   }
   
   /**
    * 
    */
   public void initialize( Game game )   
   {
      this.game = game;
      
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
      
      Action deleteAction = new AbstractAction("Delete") 
	  {
        public void actionPerformed(ActionEvent event)
        {
        	removedesign();
        }
      };

      toolbar.add( Box.createHorizontalStrut(2) );
     
      toolbar.add( new JButton(deleteAction) );
	  
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
      massField = new JTextField(4);
      massField.setToolTipText( "Mass of a single ship in kt" );
      fieldPanel.add( massField, gbc );
      
      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Armour:"), gbc );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      armourField = new JTextField(5);
      armourField.setToolTipText( "Total armour on a single undamaged ship" );
      fieldPanel.add( armourField, gbc );

      gbc.gridx++;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Shields:"), gbc );
      
      Box shieldsBox = Box.createHorizontalBox();      
      
      shieldsField = new JTextField(5);
      shieldsBox.add( shieldsField );
      
      regenShieldsField = new JCheckBox("Regen");
      shieldsBox.add( regenShieldsField);

      shieldsBox.add( Box.createHorizontalGlue() );

      gbc.gridx++;
      fieldPanel.add( shieldsBox, gbc );
      
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
      initiativeField = new JTextField(3);
      fieldPanel.add( initiativeField, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Capacitors:"), gbc );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      capacitorsField = new JTextField(3);
      capacitorsField.setToolTipText( "Number of energy capacitors on the design" );
      fieldPanel.add( capacitorsField, gbc );

      gbc.gridx++;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Deflectors:"), gbc );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      deflectorsField = new JTextField(3);
      deflectorsField.setToolTipText( "Number of beam deflectors on the design" );
      fieldPanel.add( deflectorsField, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Jamming:"), gbc );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      jammersField = new JTextField(3);
      jammersField.setToolTipText( "Total amount of jamming for the design (not number of jammers)" );
      fieldPanel.add( jammersField, gbc );

      gbc.gridx++;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Computers:"), gbc );

      Box computersPanel = Box.createHorizontalBox();
      
      bcompField = new JTextField(2);
      computersPanel.add( bcompField, gbc );
      computersPanel.add( new JLabel(" BC ") );

      bscField = new JTextField(2);
      computersPanel.add( bscField, gbc );
      computersPanel.add( new JLabel(" BSC ") );
      
      nexusField = new JTextField(2);
      computersPanel.add( nexusField, gbc );
      computersPanel.add( new JLabel(" Nexi ") );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      fieldPanel.add( computersPanel, gbc );
      
      editPanel.add( fieldPanel, BorderLayout.NORTH );
      
      editPanel.add( getWeaponsBox(), BorderLayout.CENTER );
      
      refreshFields();
      
      return editPanel;
   }
   
   private JComponent getWeaponsBox()
   {
      Box weaponsBox = Box.createVerticalBox();
      
      weaponsBox.setBorder( BorderFactory.createTitledBorder("Weapon Slots") );

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
      
      int current = designsList.getSelectedIndex();

      designsList.removeAll();

      for (int n = 0; n < game.getShipDesignCount(); n++)
      {
         ShipDesign design = game.getShipDesign(n);
         designNames.add( design.getOwner() + " -- " + design.getName() );
      }
      
      designsList.setListData( designNames );

      designsList.setSelectedIndex( current );
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
      moveField.setText( ""+(design.getSpeed4()/4.0) );
      initiativeField.setText( ""+design.getInitiative() );
      regenShieldsField.setSelected( design.isRegenShields() );
      capacitorsField.setText( ""+design.getCapacitors() );
      deflectorsField.setText( ""+design.getDeflectors() );
      jammersField.setText( ""+design.getJamming() );
      bcompField.setText( ""+design.getComputers(ShipDesign.BATTLE_COMPUTER) );
      bscField.setText( ""+design.getComputers(ShipDesign.SUPER_COMPUTER) );
      nexusField.setText( ""+design.getComputers(ShipDesign.BATTLE_NEXUS) );
      
      weaponsTable.revalidate();
      weaponsTable.repaint();
   }
   
   /**
    * @author Bryan Wiegand
    */
   void removedesign()
	{
   		if (getCurrentDesign(true) == null)
   		{
   			JOptionPane.showMessageDialog(this, "You Must Select a Design!");
   		}	
   		else
   		{	
   			game.removeShipDesign( getCurrentDesign( false ) );
   			game.saveUserDefinedProperties();
   			refreshList();
   			refreshFields();
   		}
	}
   
   void saveChanges()
   {
      int index = designsList.getSelectedIndex();
      /* designsList is the list in the GUI of designs
       * getselectedIndex ?returns information about selected index?
       * where index is the selected design
       */
      ShipDesign design = game.getShipDesign( index );
      
      /*
       * ShipDesign is a Class
       * Design is a var of type ShipDesign
       * Game is the Game being edited
       * GetShipDesign returns desgn parameters of ship in ()
       * index is the ship selected in the list
       * 
       * Therefore the above line saves the currently selected design into the Var design
       * 
       */

      design.setName( nameField.getText().trim() );
      design.setOwner( ownerField.getText().trim() );
      design.setHullType( hullTypeField.getSelectedIndex() );
      design.setMass( Utils.safeParseInt(massField.getText(),0) );
      design.setArmour( Utils.safeParseInt(armourField.getText(),0) );
      design.setShields( Utils.safeParseInt(shieldsField.getText(),0) );
      design.setInitiative( Utils.safeParseInt(initiativeField.getText(),0) );
      design.setBattleSpeed( Utils.safeParseFloat(moveField.getText(),0)  );
      design.setRegenShields( regenShieldsField.isSelected() );
      design.setJamming( Utils.safeParseInt(jammersField.getText(),0) );
      design.setCapacitors( Utils.safeParseInt(capacitorsField.getText(),0) );
      design.setDeflectors( Utils.safeParseInt(deflectorsField.getText(),0) );
      
      int bc = Utils.safeParseInt(bcompField.getText());
      int bsc = Utils.safeParseInt(bscField.getText());
      int nexus = Utils.safeParseInt(nexusField.getText());      
      design.setComputers( bc, bsc, nexus );
      
      /* 
       * The above lines save the various design specs into memory
       * 
       * design (What to perform action upon
       * setName (What to do; here SetsThe Ship Name into this designs specs)
       * design.setName( What name to set)
       * nameField (Calls the nameField object)
       * getText (Returns the Text in the NameField Object)
       **** What is TRIM?
       *
       *The next few line have other call that perform other actions 
       *to the values before they are input into thier respective design.x vars
       *
       */
      
      
      
      game.saveUserDefinedProperties();
      
      /* This calls the Game Class and tells it to run the function that saves
       * the user props to file of the game selected in the <game> var.
       */
      
      refreshList();
      /* refreshes the screen and all its feilds ** I wiil need this for the delete function)
       * 
       */
      
   }
   
   /**
    * Returns the currently selected ship design.
    * <p>
    * If allowNull is false and there is no current design then a new empty design is returned.  
    */
   public ShipDesign getCurrentDesign( boolean allowNull)
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