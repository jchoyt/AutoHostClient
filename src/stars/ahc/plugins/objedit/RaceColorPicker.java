/*
 * Created on Oct 21, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.objedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import stars.ahc.NotificationListener;
import stars.ahc.Race;
import stars.ahc.Utils;

/**
 * @author Steve Leach
 *
 */
public class RaceColorPicker extends JPanel
{
   private ColorPickerPanel colorPickerPanel;
   private JLabel colorLabel;
   private Race race;
   private ColorBlock colorBlock;
   private ArrayList notificationListeners = new ArrayList();
   
   public RaceColorPicker( Race race )
   {
      this.race = race;
      init();
   }

   /**
    * 
    */
   private void init()
   {
      setLayout( new BorderLayout() );
      
      colorPickerPanel = new ColorPickerPanel();
      add( colorPickerPanel, BorderLayout.CENTER );
      
      Box controlPanel = Box.createHorizontalBox();
      Border bevel = new BevelBorder(BevelBorder.LOWERED);
      Border insets = new EmptyBorder(2,2,2,2);
      controlPanel.setBorder( new CompoundBorder(bevel,insets) );
      
      colorBlock = new ColorBlock();
      controlPanel.add( colorBlock );

      colorLabel = new JLabel( Utils.getColorStr( race.getColor() ) );
      controlPanel.add( colorLabel );
      
      controlPanel.add( Box.createHorizontalGlue() );
      
      controlPanel.add( new JButton("Use this color") );
      add( controlPanel, BorderLayout.SOUTH );
      
      setColorLabel( race.getColor() );
   }
   
   private void setColorLabel( Color color )
   {
      colorLabel.setText( Utils.getColorStr(color) );
      colorLabel.setBackground( color );
      colorBlock.setColor( color );
   }
   
   private void setRaceColor( Color color )
   {
      System.out.println( "Setting race color for " + race.getRaceName() + " to " + Utils.getColorStr(color) );
      race.setColor( color );
      race.save();
      notify( "Race color changed" );
   }
   
   private class ColorBlock extends JComponent
   {
      private Color color = Color.WHITE;
      
      public ColorBlock()
      {
         Dimension size = new Dimension(24,24); 
         setMinimumSize( size );
         setPreferredSize( size );
         setMaximumSize( size );
         revalidate();
      }
      
      public void setColor( Color color )
      {
         this.color = color;
         repaint();
      }
      
      public void paint(Graphics g)
      {
         g.setColor( color );
         g.fillRect( 0, 0, getWidth(), getHeight() );
         g.setColor( Color.BLACK );
         g.drawRect( 0, 0, getWidth(), getHeight() );
      }
   }
   
   private class ColorPickerPanel extends JComponent implements MouseMotionListener, MouseListener
   {
      private static final float divisor255 = 1.0f / 255;
      private static final int cellSize = 1;
      private static final int baseSaturation = 40;

      public ColorPickerPanel()
      {
         setCursor( new Cursor(Cursor.CROSSHAIR_CURSOR) );
         addMouseMotionListener( this );
         addMouseListener( this );
      }
      
      public void paint(Graphics g)
      {         
         g.setColor( Color.LIGHT_GRAY );
         g.fillRect( 0, 0, getWidth(), getHeight() );
         
         for (int h = 0; h < 256; h++)
         {
            for (int s = baseSaturation; s < 256; s++)
            {
               int left = (256 - h) * cellSize;
               int top = (s-baseSaturation) * cellSize;
               
               Color color = Color.getHSBColor( divisor255 * h, divisor255 * s, 1.0f );
               
               g.setColor( color );
               
               g.fillRect( left, top, cellSize, cellSize );
            }
         }
      }

      /* (non-Javadoc)
       * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
       */
      public void mouseDragged(MouseEvent arg0)
      {
         // empty
      }

      private Color getColor( int mouseX, int mouseY )
      {
         int h = 256 - (mouseX / cellSize);
         int s = mouseY / cellSize + baseSaturation; 
         
         Color color = Color.getHSBColor( h * divisor255, s * divisor255, 1.0f );
         
         return color;
      }
      
      /* (non-Javadoc)
       * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
       */
      public void mouseMoved(MouseEvent event)
      {
         Color color = getColor( event.getX(), event.getY() );
         setColorLabel( color );
      }

      /* (non-Javadoc)
       * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
       */
      public void mouseClicked(MouseEvent event)
      {
         Color color = getColor( event.getX(), event.getY() );
         setRaceColor( color );
      }

      /* (non-Javadoc)
       * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
       */
      public void mouseEntered(MouseEvent arg0)
      {
         // empty
      }

      /* (non-Javadoc)
       * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
       */
      public void mouseExited(MouseEvent arg0)
      {
         // empty
      }

      /* (non-Javadoc)
       * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
       */
      public void mousePressed(MouseEvent arg0)
      {
         // empty
      }

      /* (non-Javadoc)
       * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
       */
      public void mouseReleased(MouseEvent arg0)
      {
         // empty
      }

   }

   /**
    */
   public void addNotifcationListener(NotificationListener listener)
   {
      notificationListeners.add( listener );
   }
   
   public void notify( String message )
   {
      for (int n = 0; n < notificationListeners.size(); n++)
      {
         NotificationListener listener = (NotificationListener)notificationListeners.get(n);
         listener.receiveNotification( this, NotificationListener.SEV_STATUS, message );
      }
   }
   
}

