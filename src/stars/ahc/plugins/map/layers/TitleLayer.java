/*
 * Created on Oct 8, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.map.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import javax.swing.JComponent;

import stars.ahc.Game;
import stars.ahc.plugins.map.MapConfig;
import stars.ahc.plugins.map.MapDisplayError;
import stars.ahcgui.pluginmanager.MapLayer;

/**
 * @author Steve
 *
 */
public class TitleLayer implements MapLayer
{
   private boolean enabled = false;
   private Game game;
   private MapConfig config;

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return enabled;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "Title";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#initialize(stars.ahc.Game, stars.ahc.plugins.map.MapConfig)
    */
   public void initialize(Game game, MapConfig config) throws MapDisplayError
   {
      this.game = game;
      this.config = config;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#draw(java.awt.Graphics2D)
    */
   public void draw(Graphics2D g)
   {
      int offset = 20;
      String title = ""+config.year;
      
      if (game.getName() != null)
      {
         title = game.getName() + " - " + title;
      }

      g.setFont( new Font("Arial", 0, 12) );
      
      //
      // Draw the background
      //
      Rectangle2D.Float textBounds = (Float)g.getFontMetrics().getStringBounds( title, g );
      
      Rectangle2D.Float background = new Rectangle2D.Float();
      
      int border = 2;
      
      background.x = offset - border;
      background.y = offset - textBounds.height;
      background.height = textBounds.height + border * 2;
      background.width = textBounds.width + border * 2;
      
      g.setColor( Color.BLACK );
      g.fill( background );
      
      //
      // Draw the text
      //
      g.setColor( Color.WHITE );
      g.drawString( title, offset, offset );      
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "Title layer";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.MapLayer#isScaled()
    */
   public boolean isScaled()
   {
      return false;
   }

   public JComponent getControls()
   {
      return null;
   }
   
}
