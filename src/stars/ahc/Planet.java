/*
 * Created on Oct 7, 2004
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

import java.awt.Point;

/**
 * @author Steve Leach
 *
 */
public class Planet implements MapObject
{
   private PlanetData data;
   private String name;
   private int year;
   private Point position;
   
   /**
    * @param data
    */
   public Planet(String name,int year,Point position,PlanetData data)
   {
      this.name = name;
      this.year = year;
      this.position = position;
      this.data = data;
   }

   public Point getPosition()
   {
      return position;
   }

   /* (non-Javadoc)
    * @see stars.ahc.MapObject#getName()
    */
   public String getName()
   {
      return name;
   }

   /* (non-Javadoc)
    * @see stars.ahc.MapObject#getOwner()
    */
   public String getOwner()
   {
      return null;
   }

   /* (non-Javadoc)
    * @see stars.ahc.MapObject#getX()
    */
   public int getX()
   {
      return position.x;
   }

   /* (non-Javadoc)
    * @see stars.ahc.MapObject#getY()
    */
   public int getY()
   {
      return position.y;
   }
}
