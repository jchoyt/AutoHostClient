/*
 * Created on Oct 9, 2004
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
package stars.ahc;

/**
 * @author Steve Leach
 */
public class PlanetData
{
   public int year = 0;
   public String name = null;
   public String[] values;
   /* Change made by Bryan WIegand */
   public int age = 0;
   
   /**
    * Returns true if this planet data is more reliable than planet data from another source 
    */
   public boolean isMoreReliableThan( PlanetData otherData )
   {
      if (otherData == null)
      {
         return true;
      }
      if (otherData.age > this.age)
      {
         return true;
      }
      if (otherData.age == this.age)
      {
         if (values.length > otherData.values.length)
         {
            return true;
         }
      }
      return false;
   }
}
