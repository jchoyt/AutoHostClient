/*
 * Created on Oct 21, 2004
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
package stars.ahc.plugins.objedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import stars.ahc.Game;
import stars.ahc.NotificationListener;
import stars.ahc.Race;
import stars.ahc.ReportLoaderException;
import stars.ahc.Utils;

/**
 * Panel for editing race details
 * 
 * @author Steve Leach
 */
public class RaceEditorPanel extends JPanel implements ListSelectionListener, ActionListener, NotificationListener
{
   private Game game;
   private JList raceList;
   private JTextField raceNameField;
   private Map propertyFieldMap = new HashMap();
   private JTextField prtField;
   private JTextField colorField;
   private JButton saveButton;
   private JButton pickColorButton;

   public RaceEditorPanel( Game game )
   {
      this.game = game;
      
      try
      {
         game.loadReports();
      }
      catch (ReportLoaderException e)
      {
         e.printStackTrace();
      }
      
      setLayout( new BorderLayout() );
      
      JPanel editPanel = setupEditPanel();
      JPanel listPanel = setupListPanel();
      
      JSplitPane splitter = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, listPanel, editPanel );
      splitter.setOneTouchExpandable(true);
      splitter.setDividerLocation(150);
      
      add( splitter, BorderLayout.CENTER );
      
   }
   
   private JPanel setupListPanel()
   {
      JPanel listPanel = new JPanel();
      listPanel.setLayout( new BoxLayout(listPanel,BoxLayout.X_AXIS) );
      
      Vector raceNames = new Vector();
      
      Iterator races = game.getRaces();
      
      while (races.hasNext())
      {
         Race race = (Race)races.next();
         if (Utils.empty(race.getRaceName()) == false)
         {
            raceNames.add( race.getRaceName() );
         }
      }
      
      raceList = new JList(raceNames);
      
      raceList.getSelectionModel().addListSelectionListener( this );

      raceList.setSelectedIndex( 0 );
      
      JScrollPane scroller = new JScrollPane( raceList );
      
      listPanel.add( scroller );
      
      return listPanel;
   }
   
   private JPanel setupEditPanel()
   {
      JPanel editPanel = new JPanel();
      
      editPanel.setLayout( new GridBagLayout() );
      
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.insets = new Insets( 2,2,2,2 );
      gbc.gridy = 1;
      gbc.gridx = 1;
      
      editPanel.add( new JLabel("Race name:"), gbc );
      
      raceNameField = new JTextField( 20 );
      raceNameField.setBackground( Color.LIGHT_GRAY );
      raceNameField.setEditable( false );
      gbc.gridx++;
      editPanel.add( raceNameField, gbc );
      
      gbc.gridy++;
      gbc.gridx = 1;
      editPanel.add( new JLabel("Color:"), gbc );
      
      colorField = new JTextField( 20 );
      colorField.setBackground( Color.LIGHT_GRAY );
      colorField.setEditable( false );
      gbc.gridx++;
      editPanel.add( colorField, gbc );
      
      pickColorButton = new JButton("Pick");
      pickColorButton.addActionListener( this );
      gbc.gridx++;
      editPanel.add( pickColorButton, gbc );
      
      gbc.gridy++;
      gbc.gridx = 1;
      editPanel.add( new JLabel("PRT:"), gbc );
      
      prtField = new JTextField( 12 );
      propertyFieldMap.put( "PRT", prtField );
      gbc.gridx++;
      editPanel.add( prtField, gbc );      
      
      saveButton = new JButton("Save changes");
      saveButton.addActionListener( this );
      gbc.gridy++;
      gbc.gridx = 1;
      editPanel.add( saveButton, gbc );
      
      gbc.gridy++;
      gbc.gridx = 3;
      gbc.weightx = 999;
      gbc.weighty = 999;
      editPanel.add( new JLabel(""), gbc );
      
      return editPanel;
   }

   /* (non-Javadoc)
    * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
    */
   public void valueChanged(ListSelectionEvent event)
   {
      refreshData();
   }
   
   private void refreshData()
   {
      Race race = getSelectedRace();
      
      raceNameField.setText( race.getRaceName() );
      colorField.setText( Utils.getColorStr(race.getColor()) );
      colorField.setBackground( race.getColor() );
      
      Iterator keys = propertyFieldMap.keySet().iterator();
      while (keys.hasNext())
      {
         String property = (String)keys.next();
         String value = race.getUserProperty( property ); 
         
         JComponent component = (JComponent)propertyFieldMap.get( property );
         if (component instanceof JTextField)
         {
            ((JTextField)component).setText( value );
         }
      }
   }
   
   private Race getSelectedRace()
   {
      String raceName = (String)raceList.getSelectedValue();
      return game.getRace( raceName, false );
   }

   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent event)
   {
      if (event.getSource() == saveButton)
      {
         saveChanges();
      }
      else if (event.getSource() == pickColorButton)
      {
         pickColor();
      }
   }
   
   private void saveChanges()
   {
      Race race = getSelectedRace();
      
      Iterator keys = propertyFieldMap.keySet().iterator();
      while (keys.hasNext())
      {
         String property = (String)keys.next();
         
         JComponent component = (JComponent)propertyFieldMap.get( property );
         if (component instanceof JTextField)
         {
            String value = ((JTextField)component).getText();
            
            race.setUserProperty( property, value );
         }
      }      
   }
   
   private void pickColor()
   {
      Race race = getSelectedRace();
      
      JFrame picker = new JFrame();
      picker.setTitle( "Race color picker - " + race.getRaceName() );
      picker.setSize( 300, 280 );
      picker.setLocation( 40, 60 );
      RaceColorPicker pickerPanel = new RaceColorPicker(race);
      pickerPanel.addNotifcationListener(this);
      picker.getContentPane().add(pickerPanel);
      picker.setVisible(true);
            
      refreshData();
   }

   /* (non-Javadoc)
    * @see stars.ahc.NotificationListener#receiveNotification(java.lang.Object, int, java.lang.String)
    */
   public void receiveNotification(Object source, int severity, String message)
   {
      if (source instanceof RaceColorPicker)
      {
         refreshData();
      }
   }
}
