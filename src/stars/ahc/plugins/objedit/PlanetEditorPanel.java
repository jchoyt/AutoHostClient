/*
 * Created on Oct 17, 2004
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import stars.ahc.Game;
import stars.ahc.Planet;
import stars.ahc.ReportLoaderException;
import stars.ahc.Utils;

/**
 * @author Steve Leach
 *
 */
public class PlanetEditorPanel extends JPanel implements ObjectEditorTab, ListSelectionListener, ActionListener, KeyListener
{
   private Game game;
   private JList planetList;
   private JLabel planetNameLabel;
   private JLabel planetOwnerLabel;
   private JTextField planetCategoryField;
   private JTextField popHoldField;
   private JButton saveButton;
   private boolean modified = false;
   private JTextField commentField;
   private Map fieldPropertyMap = new HashMap();
   
   public PlanetEditorPanel( Game game )
   {
      initialize( game );
   }
   
   public PlanetEditorPanel()
   {
   }
   
   /* (non-Javadoc)
    * @see stars.ahc.plugins.objedit.ObjectEditorTab#initialize(stars.ahc.Game)
    */
   public void initialize(Game game)
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
      
      JPanel listPanel = setupListPanel();
      JPanel editPanel = setupEditPanel();
      
      JSplitPane splitter = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, listPanel, editPanel );
      splitter.setOneTouchExpandable(true);
      splitter.setDividerLocation(150);
      
      add( splitter, BorderLayout.CENTER );
      
      refreshData();
   }

   /**
    */
   private JPanel setupListPanel()
   {
      JPanel listPanel = new JPanel();
      listPanel.setLayout( new BoxLayout(listPanel,BoxLayout.X_AXIS) );
      
      String[] names = new String[game.getPlanetCount()];
      
      for (int n = 0; n < game.getPlanetCount(); n++)
      {
         names[n] = game.getPlanet(n+1).getName();
      }
      
      Arrays.sort( names );
      
      planetList = new JList(names);
      
      planetList.getSelectionModel().addListSelectionListener( this );
      
      JScrollPane scroller = new JScrollPane( planetList );
      
      listPanel.add( scroller );
      
      return listPanel;
   }

   public JPanel setupEditPanel()
   {
      JPanel editPanel = new JPanel();
      editPanel.setLayout( new GridBagLayout() );

      int row = 0;
      
      // setup edit controls
      
      addEditLabel( editPanel, "Name: ", row, 1 );      
      planetNameLabel = addEditLabel( editPanel, "", row, 2 ); 
      row++;

      addEditLabel( editPanel, "Owner: ", row, 1 );      
      planetOwnerLabel = addEditLabel( editPanel, "", row, 2 ); 
      row++;

      addEditLabel( editPanel, "Category: ", row, 1 );      
      planetCategoryField = addEditField( editPanel, 20, row, 2,  "Category" ); 
      row++;

      addEditLabel( editPanel, "Pop Hold (%): ", row, 1 );      
      popHoldField = addEditField( editPanel, 5, row, 2, "PopHold" ); 
      row++;
      
      addEditLabel( editPanel, "Comment: ", row, 1 );      
      commentField = addEditField( editPanel, 20, row, 2, "Comment" ); 
      row++;
      
      saveButton = new JButton( "Save changes" );
      saveButton.addActionListener( this );
      saveButton.setEnabled( false );
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.gridx = 1;
      gbc.gridy = row;
      gbc.gridwidth = 2;
      gbc.insets = new Insets( 2, 2, 2, 2 ); 
      editPanel.add( saveButton, gbc );
      
      addEditPanelPadding(editPanel, row);
      
      return editPanel;
   }
   
   /**
    * Make sure all empty space is at the bottom right hand corner of the panel
    */
   private void addEditPanelPadding(JPanel editPanel, int row)
   {
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 2;
      gbc.gridy = row;
      gbc.weightx = 1;
      gbc.weighty = 1;
      editPanel.add( new JLabel(""), gbc );
   }

   /**
    */
   private JLabel addEditLabel( JPanel panel, String text, int row, int col )
   {
      JLabel label = new JLabel( text );
      
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.gridx = col;
      gbc.gridy = row;
      gbc.insets = new Insets( 2, 2, 2, 2 ); 
      
      panel.add( label, gbc );
      
      return label;
   }

   private JTextField addEditField( JPanel panel, int width, int row, int col, String propertyName)
   {
      JTextField field = new JTextField( width );

      fieldPropertyMap.put( propertyName, field );
      
      field.addKeyListener( this );

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.gridx = col;
      gbc.gridy = row;
      gbc.insets = new Insets( 2, 2, 2, 2 ); 
      
      panel.add( field, gbc );
      
      return field;
   }
   
   /* (non-Javadoc)
    * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
    */
   public void valueChanged(ListSelectionEvent event)
   {
      refreshData();
   }
   
   public void refreshData()
   {
      String planetName = (String)planetList.getSelectedValue();
      
      Planet planet = game.getPlanet( planetName );
      
      planetNameLabel.setText( planet.getName() );
      planetOwnerLabel.setText( planet.getOwner() == null ? "" : planet.getOwner() );

      if (Utils.empty(planet.getName()) == false)
      {
         Iterator properties = fieldPropertyMap.keySet().iterator();
         while (properties.hasNext())
         {
            String property = properties.next().toString();
            JTextField field = (JTextField)fieldPropertyMap.get(property);
            
            field.setText( planet.getUserProperty(property) );
         }
      }
      
      setModified( false );
   }

   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent event)
   {
      if (event.getSource() == saveButton)
      {
         saveData();
      }
   }

   /**
    * 
    */
   private void saveData()
   {
      Iterator properties = fieldPropertyMap.keySet().iterator();
      while (properties.hasNext())
      {
         String property = properties.next().toString();
         JTextField field = (JTextField)fieldPropertyMap.get(property);

         setUserProperty( property, field );
      }
      
      game.saveUserDefinedProperties();
      
      setModified( false );
   }
   
   private void setUserProperty( String propertyName, JTextField field )
   {
      String planetName = (String)planetList.getSelectedValue();      
      Planet planet = game.getPlanet( planetName );
      
      String newValue = field.getText(); 
      String oldValue = planet.getUserProperty(propertyName);
      
      if (Utils.empty(newValue))
      {
         if (Utils.empty(oldValue) == false)
         {
            // was not blank, now is, so clear the value
            planet.setUserProperty( propertyName, "" );
         }
      }
      else if (newValue.equals(oldValue) == false)
      {
         planet.setUserProperty( propertyName, newValue );
      }
      
   }

   /* (non-Javadoc)
    * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
    */
   public void keyPressed(KeyEvent arg0)
   {
      // empty
   }

   /* (non-Javadoc)
    * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
    */
   public void keyReleased(KeyEvent arg0)
   {
      // empty
   }

   /* (non-Javadoc)
    * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
    */
   public void keyTyped(KeyEvent event)
   {
      setModified(true);
   }

   /**
    */
   private void setModified(boolean newValue)
   {
      modified = newValue;
      saveButton.setEnabled( modified );
   }

}
