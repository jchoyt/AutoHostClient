/*
 * Created on Oct 6, 2004
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
package stars.ahc.plugins.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import stars.ahc.Game;
import stars.ahc.GamesProperties;
import stars.ahc.Planet;
import stars.ahc.Race;
import stars.ahc.Utils;
import stars.ahcgui.AhcFrame;
import stars.ahcgui.ColorRenderer;
import stars.ahcgui.pluginmanager.ConfigurablePlugIn;
import stars.ahcgui.pluginmanager.MapLayer;
import stars.ahcgui.pluginmanager.PlugInManager;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Swing frame in which the game map is displayed
 * 
 * @author Steve Leach
 */
public class MapFrame extends JFrame implements MapConfigChangeListener, WindowListener, MapMouseMoveListener
{
   protected Game game = null;
   protected MapConfig config = null;
   protected static Map mapFrames = new HashMap();
   private JSlider scaleSlider;
   private MapPanel mapPanel;
   private JLabel scaleLabel; 
   private ArrayList layers = new ArrayList();
   private Properties savedProperties;
   private JButton prevYearButton;
   private JButton nextYearButton;
   private JLabel yearLabel;
   private JPanel layerControlParent;
   private JLabel layerControlEmptyLabel;
   private JTable layerTable;
   private JLabel statusLabel;
   
   /**
    * Other classes should use viewGameMap() instead of the constructor.
    * @param game
    */
   protected MapFrame( Game game, MapConfig config ) throws MapDisplayError
   {
      this.game = game;
      
      this.config = config;
      
      if (config.year < 2400)
      {
         config.year = game.getYear();
      }
      
      config.addChangeListener( this );
      addWindowListener( this );
     
      setupMapFrame();
      setupLayers();
      setupMapControls();
      
      //writeProperties();
   }
   
   /**
    * Factory method for MapFrames - called instead of the constructor.
    * 
    * Displays the map for the specified game.
    * 
    * @param game
    * @throws MapDisplayError
    */
   public static MapFrame viewGameMap( Game game, MapConfig config ) throws MapDisplayError
   {
      if (game == null)
      {
         throw new MapDisplayError( "No game specified" );
      }
      
      MapFrame mf = (MapFrame)mapFrames.get( game.getName() );
      
      if (mf == null)
      {
         mf = new MapFrame( game, config );
         mapFrames.put( game.getName(), mf );
      }
      
      mf.show();
      
      return mf;
   }
 
   
   /**
    */
   private void setupLayers() throws MapDisplayError
   {
      ArrayList plugins = PlugInManager.getPluginManager().getPlugins( MapLayer.class );
      
      for (int n = 0; n < plugins.size(); n++)
      {
         try
         {
	         Class plugin = (Class)plugins.get(n);	         
	         MapLayer layer = (MapLayer)PlugInManager.getPluginManager().newInstance( plugin );
	         
	         layer.initialize( game, config );
	         
	         if (layer instanceof ConfigurablePlugIn)
	         {
	            ConfigurablePlugIn cp = (ConfigurablePlugIn)layer;
	            
	            if (savedProperties != null)
	            {
	               cp.loadConfiguration( savedProperties );
	            }
	         }
	         
	         layers.add( layer );
         }
         catch (Exception e)
         {
            throw new MapDisplayError( "Error creating layer", e  );
         }
      }
   }

   
   /**
    * Sets up the frame itself (borders, title, etc)
    */
   private void setupMapFrame()
   {
      String base = "Plugins.MapFrame."+game.getName();
      int x = GamesProperties.getIntProperty( base+".xpos", 20 );
      int y = GamesProperties.getIntProperty( base+".ypos", 20 );
      int height = GamesProperties.getIntProperty( base+".height", 720 );
      int width = GamesProperties.getIntProperty( base+".width", 580 );
      setBounds( x, y, width, height );
      setTitle( "Map for " + game.getName() + " (" + game.getCurrentYear() + ")" );
      
      AhcFrame.setWindowIcon(this);
      
      getContentPane().setLayout(new BorderLayout());
   }
   
   /**
    * Sets up the controls within the frame
    */
   private void setupMapControls() throws MapDisplayError
   {
      JPanel toolbar = new JPanel( new FlowLayout(FlowLayout.LEFT) );
      getContentPane().add( toolbar, BorderLayout.NORTH );
      
      JButton btn = new JButton( "Save Image" );
      btn.setSelected( true );
      btn.setToolTipText( "Save map image to disk" );
      btn.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent event)
         {
            doSave();
         }
      });
      toolbar.add( btn );

      JButton refreshBtn = new JButton( "Refresh" );
      refreshBtn.setSelected( true );
      refreshBtn.setToolTipText( "Redraw the map" );
      refreshBtn.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent event)
         {
            redrawMap();
         }
      });
      toolbar.add( refreshBtn );      
      
      mapPanel = new MapPanel( game, config );
      
      mapPanel.setMapFrame( this );
      mapPanel.addMapLayers( layers );
      
      mapPanel.addMapMouseMoveListener( this );

      getContentPane().add( mapPanel, BorderLayout.CENTER );
      
      JPanel controlPanel = new JPanel();
      controlPanel.setLayout( new BoxLayout(controlPanel, BoxLayout.Y_AXIS) );
      //controlPanel.setBorder( new BevelBorder(BevelBorder.LOWERED) );

      
      //===============
      JPanel scalePanel = new JPanel();
      scalePanel.setLayout( new BoxLayout(scalePanel,BoxLayout.X_AXIS) );      
      scalePanel.setBorder( createStandardBorder("Scale") );
      
      scaleSlider = new JSlider();
      scaleSlider.setMinimum( 10 );
      scaleSlider.setMaximum( 400 );
      scaleSlider.setValue( 100 );
      
      scalePanel.add( scaleSlider );
      
      scaleLabel = new JLabel("100%");
      scalePanel.add( scaleLabel );
      
      controlPanel.add( scalePanel );
      
      scaleSlider.addChangeListener( new ChangeListener() {
         public void stateChanged(ChangeEvent event)
         {
            config.mapScale = 1.0 * scaleSlider.getValue() / 100;
            scaleLabel.setText( Math.round(config.mapScale * 100) + "%" );
            MapFrame.this.repaint();
         }
      });

      //===============

      Box yearPanel = Box.createHorizontalBox();
      yearPanel.setBorder( createStandardBorder("Year") );
      
      yearPanel.add( Box.createHorizontalGlue() );
      
      Action prevYearAction = new AbstractAction("<") 
      {
         public void actionPerformed(ActionEvent e)
         {
            moveYear(-1);
         }         
      };

      prevYearButton = new JButton( prevYearAction );
      prevYearButton.setMaximumSize( new Dimension(20,20) );
      prevYearButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP,0), "Previous year" );
      prevYearButton.getActionMap().put( "Previous year", prevYearAction );
      yearPanel.add( prevYearButton );
      
      yearLabel = new JLabel( " "+config.year+" " );
      yearPanel.add( yearLabel );

      Action nextYearAction = new AbstractAction(">") 
      {
         public void actionPerformed(ActionEvent e)
         {
            moveYear(1);
         }         
      };
      
      nextYearButton = new JButton( nextYearAction );
      nextYearButton.setMaximumSize( new Dimension(20,20) );
      prevYearButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN,0), "Next year" );
      prevYearButton.getActionMap().put( "Next year", nextYearAction );
      yearPanel.add( nextYearButton );

      yearPanel.add( Box.createHorizontalGlue() );
      
      controlPanel.add( yearPanel );
      
      //===============
      
      Box playersPanel = Box.createVerticalBox();
      playersPanel.setBorder( createStandardBorder("Races") );
      
      PlayerTableModel playerModel = new PlayerTableModel( game );
      JTable playerTable = new JTable( playerModel );
      playerTable.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED) );
      playerTable.setDefaultRenderer(Color.class, new ColorRenderer(true));
      playerTable.setRowHeight(20);
      playerTable.getColumnModel().getColumn(0).setMaxWidth( 20 );
      playerTable.setShowGrid(false);
      playerTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION  );
      playerTable.setBackground( controlPanel.getBackground() );
      
      playersPanel.add( playerTable );
      
      controlPanel.add( playersPanel );
      
      //===============
      
      Box layersPanel = Box.createVerticalBox();
      layersPanel.setBorder( createStandardBorder("Layers") );
      
      LayerTableModel layerModel = new LayerTableModel( this );
      layerTable = new JTable( layerModel );      
      layerTable.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED) );
      layerTable.setRowHeight(20);
      layerTable.getColumnModel().getColumn(0).setMaxWidth( 20 );
      layerTable.setShowGrid(false);
      layerTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION  );
      layerTable.setBackground( controlPanel.getBackground() );

      layerTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent event)
         {
            if (event.getValueIsAdjusting() == false)
            {
               layerSelected();
            }
         }
       });
      
      layersPanel.add( layerTable );
      controlPanel.add( layersPanel );
      
      //===============
      
      layerControlParent = new JPanel();
      layerControlParent.setBorder( createStandardBorder("Layer controls") );
      
      layerControlEmptyLabel = new JLabel("No controls for this layer");
      layerControlParent.add( layerControlEmptyLabel );
      
      controlPanel.add( layerControlParent );
      
      //===============
            
      controlPanel.add( Box.createGlue() );
      
      getContentPane().add( controlPanel, BorderLayout.EAST );
      
      //===============
      
      Box statusBar = Box.createHorizontalBox();
      statusBar.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED) );

      statusLabel = new JLabel("Ready");
      
      statusBar.add( statusLabel );
      statusBar.add( Box.createHorizontalGlue() );
      
      getContentPane().add( statusBar, BorderLayout.SOUTH );
   }
   
   /**
    * Sets the text in the status bar at the bottom of the map window
    */
   public void setStatus( String message )
   {
      statusLabel.setText( message );
   }
   
   /**
    */
   private Border createStandardBorder(String title)
   {
      Border stdBorder = new EtchedBorder();
      stdBorder = new CompoundBorder( stdBorder, new EmptyBorder(4,4,4,4) );
      stdBorder = new CompoundBorder( stdBorder, new TitledBorder(title) );
      return stdBorder;
   }

   private void moveYear( int movement )
   {
      config.year += movement;
      
      if (config.year < 2400) config.year = 2400;
      if (config.year > game.getYear()) config.year = game.getYear();
      
      config.notifyChangeListeners();
   }
   
   private void doSave()
   {
      JFileChooser chooser = new JFileChooser();
      int rc = chooser.showSaveDialog( this );
      
      if (rc != JFileChooser.APPROVE_OPTION )
      {
         return;
      }
      
      File file = chooser.getSelectedFile();
      
      BufferedImage img = new BufferedImage( mapPanel.getHeight(), mapPanel.getWidth(), BufferedImage.TYPE_INT_RGB );
      Graphics g = img.getGraphics();
      
      mapPanel.paint( g );

//    Encode as a JPEG
      try
      {
	      FileOutputStream fos = new FileOutputStream(file);
	      JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(fos);
	      jpeg.encode(img);
	      fos.close();
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
   }

   /**
    * Called when a map layer has been selected from the list 
    */
   private void layerSelected()
   {
      // Clear any existing controls in the layer control panel
      layerControlParent.removeAll();
      
      // Get the selected map layer
      int layerIndex = layerTable.getSelectedRow();      
      MapLayer layer = (MapLayer)layers.get(layerIndex);

      if (layer != null)
      {
         // Get the controls for the selected map layer
         Component controls = layer.getControls();
         
         if (controls == null)
         {
            // If there aren't any controls for the layer, display a simple message
            layerControlParent.add( layerControlEmptyLabel );
         }
         else
         {
            // Otherwise display the controls
            layerControlParent.add( controls );
         }
         
         // Make the new controls visible
         layerControlParent.revalidate();
      }
   }
   
   public void redrawMap()
   {
      mapPanel.repaint();
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.MapConfigChangeListener#mapConfigChanged(stars.ahc.plugins.map.MapConfig)
    */
   public void mapConfigChanged(MapConfig config)
   {
      scaleSlider.setValue( (int)Math.round(config.mapScale * 100) );
      yearLabel.setText( " " + config.year + " " );
      
      //mapPanel.repaint();
   }
   
   public MapLayer[] getMapLayers()
   {
      return (MapLayer[])layers.toArray( new MapLayer[0] );
   }
   
   private void writeProperties()
   {
      // This should all really happen in saveConfiguration()
      
      GamesProperties.setProperty( "Plugins.MapFrame."+game.getName()+".xpos", this.getX() );
      GamesProperties.setProperty( "Plugins.MapFrame."+game.getName()+".ypos", this.getY() );
      GamesProperties.setProperty( "Plugins.MapFrame."+game.getName()+".height", this.getHeight() );
      GamesProperties.setProperty( "Plugins.MapFrame."+game.getName()+".width", this.getWidth() );
      
      for (int n = 0; n < layers.size(); n++)
      {
         MapLayer layer = (MapLayer)layers.get(n);
         
         String layer_id = layer.getName().replaceAll( " ", "_" );
         GamesProperties.setProperty( "Plugins.MapLayers."+game.getName()+"." + layer_id + ".enabled", layer.isEnabled() );
      }
      
      GamesProperties.writeProperties();
   }

   /* (non-Javadoc)
    * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
    */
   public void windowActivated(WindowEvent arg0)
   {
      // empty
   }

   /* (non-Javadoc)
    * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
    */
   public void windowClosed(WindowEvent arg0)
   {
      // empty
   }

   /* (non-Javadoc)
    * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
    */
   public void windowClosing(WindowEvent arg0)
   {
      writeProperties();      
   }

   /* (non-Javadoc)
    * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
    */
   public void windowDeactivated(WindowEvent arg0)
   {
      writeProperties();
   }

   /* (non-Javadoc)
    * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
    */
   public void windowDeiconified(WindowEvent arg0)
   {
      // empty
   }

   /* (non-Javadoc)
    * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
    */
   public void windowIconified(WindowEvent arg0)
   {
      // empty
   }

   /* (non-Javadoc)
    * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
    */
   public void windowOpened(WindowEvent arg0)
   {
      // empty
   }

   /**
    */
   public void loadConfiguration(Properties properties)
   {
      this.savedProperties = properties;
      
      for (int n = 0; n < layers.size(); n++)
      {
         MapLayer layer = (MapLayer)layers.get(n);
         
         String key = "Plugins.MapLayers." + game.getName() + "." + layer.getName().replaceAll("_"," ")+".enabled";
         String enabled = savedProperties.getProperty( key );
         if (enabled != null)
         {
            layer.setEnabled( enabled == "true" );
         }
         
         if (layer instanceof ConfigurablePlugIn)
         {
            ((ConfigurablePlugIn)layer).loadConfiguration( properties );
         }
      }     
      
   }

   /**
    * @param properties
    */
   public void saveConfiguration(Properties properties)
   {
      properties.setProperty( "Plugins.MapFrame."+game.getName()+".xpos", ""+this.getX() );
      properties.setProperty( "Plugins.MapFrame."+game.getName()+".ypos", ""+this.getY() );
      properties.setProperty( "Plugins.MapFrame."+game.getName()+".height", ""+this.getHeight() );
      properties.setProperty( "Plugins.MapFrame."+game.getName()+".width", ""+this.getWidth() );
      
      for (int n = 0; n < layers.size(); n++)
      {
         MapLayer layer = (MapLayer)layers.get(n);
         
         String layer_id = layer.getName().replaceAll( " ", "_" );
         properties.setProperty( "Plugins.MapLayers."+game.getName()+"." + layer_id + ".enabled", ""+layer.isEnabled() );
         
         if (layer instanceof ConfigurablePlugIn)
         {
            ConfigurablePlugIn cp = (ConfigurablePlugIn)layer;
            cp.saveConfiguration( properties );
         }
      }      
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.map.MapMouseMoveListener#mouseMovedOverMap(java.awt.Point)
    */
   public void mouseMovedOverMap( Point screenPos, Point mapPos )
   {
      // The mouse has moved over the map, so update the status bar.
      // Always display the new co-ords (in map space).
      // If near a planet, display it's name as well.
      // If the planet is occupied, display it's owner.
      
      String posText = mapPos.x + "," + mapPos.y;
      
      Planet planet = game.findClosestPlanet( mapPos, 6 );

      if (planet != null)
      {
         posText += "  " + planet.getName();

         // Re-get the planet's details for the currently viewed year
         planet = game.getPlanet( planet.getName(), config.year );
         
         if (planet != null)
         {
            if (planet.isOccupied())
            {
               posText += " (" + planet.getOwner() + ")";
            }
         }
      }
      
      setStatus( posText );
   }

}


class LayerTableModel extends AbstractTableModel
{
   private MapLayer[] layers;
   private MapFrame mapFrame;
   
   public LayerTableModel( MapFrame mapFrame )
   {
      this.mapFrame = mapFrame;
      initialize();
   }
   
   /**
    * 
    */
   private void initialize()
   {
      this.layers = mapFrame.getMapLayers();
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
      return layers.length;
   }

   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getValueAt(int, int)
    */
   public Object getValueAt(int row, int col)
   {
      switch (col)
      {
         case 0:
            return new Boolean( layers[row].isEnabled() );
         case 1:
            return layers[row].getDescription();
      }
      return null;
   }
   
   public Class getColumnClass(int col)
   {
      switch (col)
      {
         case 0: return Boolean.class;
         default: return String.class;
      }
   }
   
   public boolean isCellEditable(int row, int col)
   {
      return (col == 0);
   }
   
   
   public void setValueAt(Object obj, int row, int col)
   {
      switch (col)
      {
         case 0:
            layers[row].setEnabled( ((Boolean)obj).booleanValue() );
            mapFrame.redrawMap();
            break;
      }
   }
}

class PlayerTableModel extends AbstractTableModel
{
   private Game game;
   private Race[] races;
   private int raceCount = 0;

   public PlayerTableModel( Game game )
   {
      this.game = game;
      races = new Race[game.getRaceCount()];
      
      Iterator raceList = game.getRaces();
      while (raceList.hasNext())
      {
         Race race = (Race)raceList.next();
         if (Utils.empty(race.getRaceName()) == false)
         {
            races[raceCount++] = race;
         }
      }
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
      return raceCount;
   }

   /* (non-Javadoc)
    * @see javax.swing.table.TableModel#getValueAt(int, int)
    */
   public Object getValueAt(int row, int col)
   {      
      switch (col)
      {
         case 0:
            return races[row].getColor();
         case 1:
            return races[row].getRaceName();
           
      }
      return null;
   }
   
   
   public Class getColumnClass(int col)
   {
      switch (col)
      {
         case 0: return Color.class;
         case 1: return String.class;
         default: return null;
      }
   }
}