/*
 * Created on Nov 16, 2004
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
package stars.ahc.plugins.battlesim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * @author Steve Leach
 *
 */
public class BattleBoard extends JComponent implements BattleSimulationListener
{
   private BattleSimulation simulation;
   private int cellSize = 20;
   private Color[] sideColors = { Color.GREEN, Color.CYAN, Color.YELLOW };
   private BattleSimulationNotification lastNotification = null;

   public BattleBoard()
   {
      init( null );
   }
   
   public BattleBoard( BattleSimulation sim )
   {
      init( sim );
   }

   public void setSimulation( BattleSimulation sim )
   {
      this.simulation = sim;
      
      if (sim != null)
      {
         sim.addStatusListener( this );
      }
   }
   
   /**
    * 
    */
   private void init( BattleSimulation sim )
   {
      setSimulation( sim );
      
      int size = cellSize*10+1;
      setPreferredSize( new Dimension(size,size) );
   }
   
   

   public void paint(Graphics graphics)
   {
      Graphics2D g = (Graphics2D)graphics;
      
      int boardSize = 10 * (cellSize+1);
      
      g.setBackground( Color.BLACK );
      g.setColor( Color.BLACK );
      g.fillRect( 0, 0, boardSize, boardSize );
      
      if (simulation == null)
      {
         return;
      }
      
      for (int n = 0; n < simulation.stackCount; n++)
      {
         ShipStack stack = simulation.stacks[n];
         
         if (stack.shipCount > 0)
         {
            drawStack(g, stack);
         }
      }
      
      if (lastNotification != null)
      {
         ShipStack stack = lastNotification.activeStack;
         
         drawStack( g, stack );		// redraw the active stack to make sure it is on top
         
         switch (lastNotification.eventType)
         {
            case BattleSimulationNotification.TYPE_MOVE:
               drawMove(g, stack);
               break;
            
            case BattleSimulationNotification.TYPE_FIRE:
               drawShot(g, stack);
               break;
         }
      }
   }
   
   private void drawStack(Graphics2D g, ShipStack stack)
   {
      if (stack == null) return;
      
      g.setColor( sideColors[stack.side] );
      int x = (stack.xpos-1) * cellSize;
      int y = (stack.ypos-1) * cellSize;            
      int size = cellSize / 2 - 2;
      g.fillOval( x-size, y-size, size*2+1, size*2+1 );
      g.setColor( Color.BLACK );
      
      String s = ""+stack.side;
      int width = g.getFontMetrics().stringWidth(s);
      int height = g.getFontMetrics().getHeight();
      g.drawString( ""+stack.side, x-width/2, y+height/2 - 3 );
   }

   private void drawMove(Graphics2D g, ShipStack stack)
   {
      g.setColor( sideColors[stack.side] );
      int x = (lastNotification.movedFrom.x-1) * cellSize;
      int y = (lastNotification.movedFrom.y-1) * cellSize;
      int size = cellSize / 2 - 2;
      g.drawOval( x-size, y-size, size*2+1, size*2+1 );
   }

   private void drawShot(Graphics2D g, ShipStack stack)
   {
      g.setColor( Color.RED );
        
      int x1 = (stack.xpos-1) * cellSize;
      int y1 = (stack.ypos-1) * cellSize;
      int x2 = (lastNotification.targetStack.xpos-1) * cellSize;
      int y2 = (lastNotification.targetStack.ypos-1) * cellSize;
      
      g.drawLine( x1, y1, x2, y2 );
      
      int bangSize = (lastNotification.targetStack.shipCount > 0 ) ? 3 : 5;
      
      g.fillOval( x2-bangSize, y2-bangSize, bangSize*2+1, bangSize*2+1 );
   }

   /* (non-Javadoc)
    * @see stars.ahc.plugins.battlesim.BattleSimulationListener#handleNotification(stars.ahc.plugins.battlesim.BattleSimulationNotification)
    */
   public void handleNotification(BattleSimulationNotification notification)
   {
      this.lastNotification = notification;
      repaint();      
      pause();
   }
   
   private void pause()
   {
      Object o = new Object();
      synchronized (o)
      {
         try
         {
            o.wait(500);
         }
         catch (InterruptedException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }
}
