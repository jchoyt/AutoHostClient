/*
 * Created on Oct 15, 2004
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
package stars.ahc.plugins.analyzer;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Prints a simple multiple line text string.
 * <p>
 * The report is printed using the Courier fixed width font and so can be
 * used to print reports where columns are expected to line up.
 * 
 * @author Steve Leach
 */
public class ReportPrinter
{
   private double topMargin = 40;
   private double leftMargin = 40;
   private int linesPerPage = 52;
   private String reportText;
   private ArrayList lines = new ArrayList();      

   public void setReportText( String text )
   {
      this.reportText = text;
   }
   
   public void printReport( PrinterJob job ) throws PrinterException
   {
      StringTokenizer tokens = new StringTokenizer( reportText, "\n" );
      while (tokens.hasMoreTokens())
      {
         lines.add( tokens.nextToken() );
      }
      
      int pageCount = (int)Math.floor(lines.size() / linesPerPage) + 1;

      System.out.println( "Printing Pages = " + pageCount );
      
      PageFormat pageFormat = new PageFormat ();
      
      topMargin = pageFormat.getImageableY();
      leftMargin = pageFormat.getImageableX();
      
      
      Book book = new Book();
      
      for (int n = 0; n < pageCount; n++)
      {
         book.append( new PagePrinter(), pageFormat );
      }
      
      job.setPageable( book );
      
      job.print();
      
      job.cancel();      
   }
   
   class PagePrinter implements Printable
   {
      public int print(Graphics g, PageFormat fmt, int pageIndex) throws PrinterException
      {
         System.out.println( "Printing page " + pageIndex );
         
         Graphics2D g2d = (Graphics2D) g;
         g2d.translate( fmt.getImageableX(), fmt.getImageableY() );
                  
         Font f = new Font( "Courier", Font.PLAIN, 9);
         
         g2d.setFont( f );
         
         FontMetrics metrics = g2d.getFontMetrics( f );
         
         int lineHeight = metrics.getHeight();
         
         int firstLine = pageIndex * linesPerPage;
         int lastLine = Math.min( firstLine + linesPerPage, lines.size() );
         
         System.out.println( "Lines " + firstLine + " to " + lastLine );
         
         int xpos = 0;
                  
         for (int n = firstLine; n < lastLine; n++)
         {
            int ypos = (n-firstLine+1) * lineHeight;
            
            g.drawString( lines.get(n).toString(), xpos, ypos );
         }
         
         return Printable.PAGE_EXISTS;
      }
   }
   

}
