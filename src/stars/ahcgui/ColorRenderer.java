/*
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
      g.setColor( color );
      g.fillOval( 4, 4, imgSize-8, imgSize-8 );
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