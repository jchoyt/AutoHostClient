/*
 * Created on Nov 27, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.objedit;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import stars.ahc.ShipDesign;
import stars.ahc.ShipHull;
import stars.ahc.Utils;
import stars.ahc.Weapon;

/**
 * @author Steve Leach
 *
 */
public class ShipDesignEditor extends JPanel
{
   private JTextField nameField;
   private JTextField ownerField;
   private JComboBox hullTypeField;
   private JTextField massField;
   private JTextField armourField;
   private JTextField shieldsField;
   private JCheckBox regenShieldsField;
   private JTextField moveField;
   private JTextField initiativeField;
   private JTextField capacitorsField;
   private JTextField deflectorsField;
   private JTextField jammersField;
   private JTextField bcompField;
   private JTextField bscField;
   private JTextField nexusField;
   private JTable weaponsTable;
   private ShipDesign design = new ShipDesign();
   private JTextField boraniumField;
   private JTextField resourcesField;

   public ShipDesignEditor()
   {
      setLayout( new BorderLayout() );
   }
   
   public void setDesign( ShipDesign design )
   {
      this.design = design;
      refreshFields();
      ((ShipWeaponsTableModel)weaponsTable.getModel()).setDesign(design);
   }
   
   public void setupControls()
   {
      JPanel fieldPanel = new JPanel( new GridBagLayout() );
      fieldPanel.setBorder( new EmptyBorder(3,3,3,3) ); 
      
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.insets = new Insets(2,2,2,2);
      
      gbc.gridy = 1;
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
      fieldPanel.add( new JLabel("Design:"), gbc );
      
      gbc.gridx++;
      gbc.gridwidth = 4;
      nameField = new JTextField(30);
      fieldPanel.add( nameField, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Hull Type:"), gbc );
      
      gbc.gridx++;     
      gbc.gridwidth = 4;
      hullTypeField = new JComboBox(ShipHull.getTypeNames());
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
      armourField.setToolTipText( "Total armour on a single undamaged ship, including any RS decrease" );
      fieldPanel.add( armourField, gbc );

      gbc.gridx++;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Shields:"), gbc );
      
      Box shieldsBox = Box.createHorizontalBox();      
      
      shieldsField = new JTextField(5);
      shieldsField.setToolTipText( "Total shields per ship, including any RS increase" );
      shieldsBox.add( shieldsField );
      
      regenShieldsField = new JCheckBox("Regen");
      regenShieldsField.setToolTipText( "Check if the owner of this design has regenerating shields" );
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
      moveField.setToolTipText( "The battle speed of the design, including any WM bonus (0.25 to 2.50)" );
      fieldPanel.add( moveField, gbc );

      gbc.gridx++;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Initiative:"), gbc );
      
      gbc.gridx++;      
      initiativeField = new JTextField(3);
      initiativeField.setToolTipText( "The initiative of the hull, including modifications from computers" );
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
      bcompField.setToolTipText( "The number of battle computers on the design" );
      computersPanel.add( bcompField, gbc );
      computersPanel.add( new JLabel(" BC ") );

      bscField = new JTextField(2);
      bscField.setToolTipText( "The number of battle super computers on the design" );
      computersPanel.add( bscField, gbc );
      computersPanel.add( new JLabel(" BSC ") );
      
      nexusField = new JTextField(2);
      nexusField.setToolTipText( "The number of battle nexi on the design" );
      computersPanel.add( nexusField, gbc );
      computersPanel.add( new JLabel(" Nexi ") );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      fieldPanel.add( computersPanel, gbc );

      gbc.gridy++;
      gbc.gridx = 1;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Boranium:"), gbc );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      boraniumField = new JTextField(4);
      boraniumField.setToolTipText( "Boranium cost of this design" );
      fieldPanel.add( boraniumField, gbc );

      gbc.gridx++;
      gbc.gridwidth = 1;
      fieldPanel.add( new JLabel("Resources:"), gbc );
      
      gbc.gridx++;      
      gbc.gridwidth = 1;
      resourcesField = new JTextField(4);
      resourcesField.setToolTipText( "Resource cost of this design" );
      fieldPanel.add( resourcesField, gbc );

      
      gbc.gridx = 20;     
      gbc.weightx = 1;
      fieldPanel.add( new JLabel(""), gbc );
      
      add( fieldPanel, BorderLayout.NORTH );
      
      
      add( getWeaponsBox(), BorderLayout.CENTER );      
   }
   
   private void refreshFields()
   {
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
      boraniumField.setText( ""+design.getBoraniumCost() );
      resourcesField.setText( ""+design.getResourceCost() );
                  
      weaponsTable.repaint();
   }
   
   private JComponent getWeaponsBox()
   {
      Box weaponsBox = Box.createVerticalBox();
      
      weaponsBox.setBorder( BorderFactory.createTitledBorder("Weapon Slots") );

      weaponsTable = new JTable( new ShipWeaponsTableModel(getCurrentDesign(true)) );
      weaponsTable.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED) );
      weaponsTable.setRowHeight( 22 );

      weaponsTable.setAutoResizeMode( JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS );
      weaponsTable.getColumnModel().getColumn(0).setWidth( 30 );
      weaponsTable.getColumnModel().getColumn(1).setWidth( 200 );
      
      JComboBox weaponTypeSelector = new JComboBox();
      for (int n = 0; n < Weapon.getAllWeapons().length; n++)
      {
         weaponTypeSelector.addItem( Weapon.getAllWeapons()[n].name );
      }
      
      weaponsTable.getColumnModel().getColumn(ShipWeaponsTableModel.COL_TYPE).setCellEditor( new DefaultCellEditor(weaponTypeSelector) );
      
      weaponsTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
      
      weaponsTable.getColumnModel().getColumn(0).setWidth( 200 );
      
      JScrollPane scroller = new JScrollPane(weaponsTable);
      
      weaponsBox.add(scroller);
      
      weaponsBox.add( Box.createVerticalStrut(2) );
      
      Box weaponsControls = Box.createHorizontalBox();

      Action addWeaponAction = new AbstractAction("Add Weapon") {
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
      
      design.addWeapon( Weapon.COLLOIDAL_PHASER, 1 );
      weaponsTable.revalidate();
   }
   
   public ShipDesign getCurrentDesign( boolean allowNull)
   {
      if ((design == null) && (allowNull == false))
      {
         design = new ShipDesign();
      }

      return design;
   }
   
   public void getFieldValues()
   {
      design.setName( nameField.getText() );
      design.setOwner( ownerField.getText() );
      design.setHullType( hullTypeField.getSelectedIndex() );
      design.setMass( Utils.safeParseInt(massField.getText()) );
      design.setArmour( Utils.safeParseInt(armourField.getText()) );
      design.setShields( Utils.safeParseInt(shieldsField.getText()) );
      design.setBattleSpeed( Utils.safeParseFloat(moveField.getText(),1.0) );
      design.setInitiative( Utils.safeParseInt(initiativeField.getText()) );
      design.setRegenShields( regenShieldsField.isSelected() );
      design.setCapacitors( Utils.safeParseInt(capacitorsField.getText()) );
      design.setDeflectors( Utils.safeParseInt(deflectorsField.getText()) );
      design.setJamming( Utils.safeParseInt(jammersField.getText()) );
      
      design.setBoraniumCost( Utils.safeParseInt(boraniumField.getText()) );
      design.setResourceCost( Utils.safeParseInt(resourcesField.getText()) );

      int bcomp = Utils.safeParseInt(bcompField.getText());
      int bsc = Utils.safeParseInt(bscField.getText());
      int nexus = Utils.safeParseInt(nexusField.getText());
      
      design.setComputers( bcomp, bsc, nexus );
   }
   
   private class ShipWeaponsTableModel extends AbstractTableModel
   {
      public static final int COL_SLOT = 0;
      public static final int COL_TYPE = 1;
      public static final int COL_COUNT = 2;
      private static final int COLCOUNT = 3;
      
      private ShipDesign design = new ShipDesign();

      public ShipWeaponsTableModel( ShipDesign design )
      {
         this.design = design;
      }
      
      public void setDesign( ShipDesign design )
      {
         this.design = design;
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
         return design.getWeaponSlots();
      }

      /* (non-Javadoc)
       * @see javax.swing.table.TableModel#getValueAt(int, int)
       */
      public Object getValueAt(int row, int col)
      {
         switch (col)
         {
            case COL_SLOT:		return "" + (row+1);
            case COL_TYPE:		return design.getWeaponName(row);
            case COL_COUNT:	return new Integer( design.getWeaponCount(row) );
            default:			return "";
         }
      }
      
      public Class getColumnClass(int col)
      {
         switch (col)
         {
            case COL_SLOT:		return String.class;
            case COL_TYPE: 	return String.class;
            case COL_COUNT: 	return Integer.class;
            default: 			return null;
         }
      }
      
      public boolean isCellEditable(int row, int col)
      {
         return (col >= COL_TYPE);
      }
      
      
      public void setValueAt(Object value, int row, int col)
      {
         if (design == null)
         {
            return;
         }
         
         switch (col)
         {
            case COL_TYPE:
               String newName = value.toString();
               Weapon wpn = Weapon.getWeaponByName(newName);
               design.setWeapon( row, design.getWeaponCount(row), wpn );
               break;
            case COL_COUNT:
               int newCount = ((Integer)value).intValue();
               design.setWeaponCount(row,newCount);
               break;
         }
      }
      public String getColumnName(int col)
      {
         switch (col)
         {
            case COL_SLOT:		return "Slot";
            case COL_TYPE: 	return "Weapon";
            case COL_COUNT: 	return "Count";
            default:			return null;
         }
      }
   }   
}

