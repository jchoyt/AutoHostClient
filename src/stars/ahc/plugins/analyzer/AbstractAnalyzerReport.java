/*
 * Created on Oct 14, 2004
 *
 * Copyright (c) 2004, Steve Leach

 *  *  This program is free software; you can redistribute it and/or
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
 */
package stars.ahc.plugins.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * @author Steve Leach
 *
 */
public abstract class AbstractAnalyzerReport implements AnalyzerReport
{   
   private boolean enabled;
   private ArrayList columns = new ArrayList();
   private static final String MANY_SPACES = "                                                                       ";
   private static final String UNDERLINE = "========================================================================";
   public static final int PAD_LEFT = 1;
   public static final int PAD_RIGHT = 0;

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return enabled;
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   /**
    * Defines a new column for the report
    * 
    * @param title
    * @param width
    */
   public void defineColumn( String title, int width, int justification )
   {
      ReportColumn column = new ReportColumn();
      column.title = title;
      column.width = width;
      column.justification = justification;
      
      columns.add( column );
   }
   
   public void defineColumn( String title, int width )
   {
      defineColumn( title, width, PAD_RIGHT );
   }
   
   public String getHeaderText()
   {
      String text1 = "";
      String text2 = "";
      
      for (int n = 0; n < columns.size(); n++)
      {
         ReportColumn column = (ReportColumn)columns.get(n);
         text1 += pad( column.title, column ) + " ";
         text2 += UNDERLINE.substring( 0, column.width ) + " ";
      }
      
      return text1 + "\n" + text2 + "\n";
   }
   
   public String padRight( String text, int width )
   {
      return (text + MANY_SPACES).substring( 0, width );
   }
   
   public String padLeft( String text, int width)
   {
      if (text == null) text = "";
      text = MANY_SPACES + text;
      return text.substring( text.length() - width );
   }
   
   public String pad( String text, ReportColumn column )
   {
      switch (column.justification)
      {
         case PAD_LEFT:
            return padLeft( text, column.width );
         default:
            return padRight( text, column.width );
      }
   }
   
   public void clearColumnValues()
   {
      for (int n = 0; n < columns.size(); n++)
      {
         ReportColumn column = (ReportColumn)columns.get(n);
         column.value = "";
      }      
   }
   
   private ReportColumn getColumn( String title )
   {
      for (int n = 0; n < columns.size(); n++)
      {
         ReportColumn column = (ReportColumn)columns.get(n);
         if (column.title.equals(title))
         {
            return column;
         }
      }
      return null;
   }
   
   public void setColumnValue(String title, String value)
   {
      getColumn( title ).value = value;
   }
   
   public void setColumnValue(String title, int value)
   {
      setColumnValue( title, ""+value );
   }

   public String getReportLine()
   {
      String text = "";
      
      for (int n = 0; n < columns.size(); n++)
      {
         ReportColumn column = (ReportColumn)columns.get(n);
         text += pad( column.value, column ) + " ";
      }
      
      return text + "\n";
   }   
   
   public void sortLines( ArrayList lines )
   {
      Collections.sort( lines, new StringComparator(1) );
   }
   
   /**
    * Collapses an ArrayList of Strings down into a single string
    */
   public String collapse( ArrayList lines )
   {
      String text = "";
      for (int n = 0; n < lines.size(); n++)
      {
         text += lines.get(n).toString();
      }
      
      return text;
   }
   
   /**
    * Initialise the report columns data structures
    */
   public void initColumns()
   {
      columns.clear();
   }
}

class ReportColumn
{
   String title;
   int width;
   int justification;
   String value;
}

class StringComparator extends Object implements Comparator
{
   int direction = 1;
   
   public StringComparator()
   {
      direction = 1;
   }

   public StringComparator( int direction )
   {
      this.direction = direction;
   }
   
   public int compare(Object arg0, Object arg1)
   {
      return direction * arg0.toString().compareTo( arg1.toString() );
   }
}
