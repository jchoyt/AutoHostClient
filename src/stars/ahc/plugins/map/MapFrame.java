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
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import stars.ahc.Game;
import stars.ahc.GamesProperties;
import stars.ahc.Race;
import stars.ahc.Utils;
import stars.ahcgui.pluginmanager.ConfigurablePlugIn;
import stars.ahcgui.pluginmanager.MapLayer;
import stars.ahcgui.pluginmanager.PlugInManager;
import stars.ahcgui.ColorRenderer;

/**
 * Swing frame in which the game map is displayed
 * 
 * @author Steve Leach
 */
public class MapFrame extends JFrame implements MapConfigChangeListener, WindowListener
{
   protected Game game = null;
   protected MapConfig config = null;
   protected static Map mapFrames = new HashMap();
   private JSlider scaleSlider;
   private MapPanel mapPanel;
   private JLabel scaleLabel; 
   private ArrayList layers = new ArrayList();
   private Properties savedProperties;
   
   /**
    * Other classes should use viewGameMap() instead of the constructor.
    * @param game
    */
   protected MapFrame( Game game, MapConfig config ) throws MapDisplayError
   {
      this.game = game;
      
      this.config = config;
      
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
	         MapLayer layer = (MapLayer)plugin.newInstance();
	         
	         layer.initialize( game, config );
	         
	         if (layer instanceof ConfigurablePlugIn)
	         {
	            ConfigurablePlugIn cp = (ConfigurablePlugIn)layer;
	            cp.loadConfiguration( savedProperties );
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
      btn.addActionListener( new ActionListener() {
         public void actionPerformed(ActionEvent event)
         {
            doSave();
         }
      });
      toolbar.add( btn );
      
      mapPanel = new MapPanel( game, config );
      
      mapPanel.addMapLayers( layers );

      getContentPane().add( mapPanel, BorderLayout.CENTER );
      
      JPanel controlPanel = new JPanel();
      controlPanel.setLayout( new BoxLayout(controlPanel, BoxLayout.Y_AXIS) );
      controlPanel.setBorder( new BevelBorder(BevelBorder.LOWERED) );

      //===============
      JPanel scalePanel = new JPanel();
      scalePanel.setLayout( new BoxLayout(scalePanel,BoxLayout.X_AXIS) );
      scalePanel.setBorder( BorderFactory.createEmptyBorder(4,4,4,4) );
      
      JLabel label = new JLabel( "Scale: " );
      scalePanel.add( label );
      
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
      
      JPanel layersPanel = new JPanel();
      layersPanel.setLayout( new BoxLayout(layersPanel,BoxLayout.Y_AXIS) );
      layersPanel.setBorder( BorderFactory.createEmptyBorder(4,4,4,4) );
      
      layersPanel.add( new JLabel("Layers") );
      LayerTableModel layerModel = new LayerTableModel( this );
      JTable layerTable = new JTable( layerModel );      
      layerTable.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED) );      
      
      layersPanel.add( layerTable );
      controlPanel.add( layersPanel );
      
      //===============
      
      Box playersPanel = Box.createVerticalBox();
      playersPanel.setBorder( BorderFactory.createEmptyBorder(2,2,2,2) );
      playersPanel.add( new JLabel("Players") );
      
      PlayerTableModel playerModel = new PlayerTableModel( game );
      JTable playerTable = new JTable( playerModel );
      playerTable.setBorder( BorderFactory.createBevelBorder(BevelBorder.LOWERED) );
      playerTable.setDefaultRenderer(Color.class, new ColorRenderer(true));
      
      playersPanel.add( playerTable );
      
      controlPanel.add( playersPanel );
      
      //===============
      
      controlPanel.add( Box.createGlue() );
      
      getContentPane().add( controlPanel, BorderLayout.EAST );
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
      mapPanel.repaint();
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
            return layers[row].getName();
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