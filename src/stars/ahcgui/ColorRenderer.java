/*
 * Copyright (c) 2004, Steve Leach
 * 
 * Part of the Stars! AutoHost Client software.
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

package stars.ahcgui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import stars.ahc.Utils;

/**
 * Renderer to show a color in a table
 *  
 */
public class ColorRenderer extends JLabel implements TableCellRenderer
{
   Border  unselectedBorder = null;
   Border  selectedBorder   = null;
   boolean isBordered       = true;

   public ColorRenderer(boolean isBordered)
   {
      this.isBordered = isBordered;
      setOpaque(true); //MUST do this for background to show up.
   }

   public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus,
         int row, int column)
   {
      return new RaceColorSwatch( (Color)color );
      
//      * This code comes directly from Sun's Swing tutorial examples at
//      * 
//      * http://java.sun.com/docs/books/tutorial/uiswing/components/example-1dot4/ColorRenderer.java
//      *
//      * The code there does not include a copyright notice.
      
//      Color newColor = (Color)color;
//      setBackground(newColor);
//      if (isBordered)
//      {
//         if (isSelected)
//         {
//            if (selectedBorder == null)
//            {
//               selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
//            }
//            setBorder(selectedBorder);
//         }
//         else
//         {
//            if (unselectedBorder == null)
//            {
//               unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
//            }
//            setBorder(unselectedBorder);
//         }
//      }
//
//      setToolTipText("RGB value: " + newColor.getRed() + ", " + newColor.getGreen() + ", " + newColor.getBlue());
//      return this;
   }
}

class RaceColorSwatch extends JComponent
{
   private Color color = Color.BLACK;
   private BufferedImage img = null;
   private int imgSize = 20;

   public RaceColorSwatch( Color color )
   {
      this.color = color;
      
      Dimension size = new Dimension(imgSize,imgSize);
      setMinimumSize( size );
      setPreferredSize( size );
      setMaximumSize( size );
      
      img = new BufferedImage( imgSize, imgSize, BufferedImage.TYPE_INT_RGB );
      Graphics g = img.getGraphics();

      Color c;
      
      for (int n = 1; n < 5; n++)
      {
         c = Utils.adjustBrightness( color, n * 40 );      
         g.setColor( c );
         int dim = 10-n;
         g.fillOval( n+1, n+1, imgSize-n*2-1, imgSize-n*2-1 );
      }
      
      c = Utils.adjustBrightness( color, 255 );      
      g.setColor( c );
      g.fillOval( 7, 7, 5, 5 );

      g.setColor( Color.BLACK );
      g.drawOval( 7, 7, 5, 5 );
      
   }
   
   public void paint(Graphics g)
   {
      g.setColor( Color.BLACK );
      g.fillRect( 0, 0, imgSize, imgSize );
      if (img != null)
      {
         g.drawImage( img, 0, 0, null );
      }
   }
}