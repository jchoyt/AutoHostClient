/*
 * Created on Oct 31, 2004
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. * 
 */
package stars.ahc;

import java.util.ArrayList;

/**
 * @author Steve Leach
 *
 */
public class ShipDesignList
{
   private Game game;
   private ArrayList designs = new ArrayList();

   public ShipDesignList( Game game )
   {
      this.game = game;
   }
   
   public void addDesign( ShipDesign design )
   {
      designs.add( design );
   }
   
   public ShipDesign getDesign( String name, String owner )
   {
      return null;
   }

   public int getDesignCount()
   {
      return designs.size();
   }
   
   public ShipDesign getDesign( int index )
   {
      if ((index >= designs.size()) || (index < 0)) return null;
      return (ShipDesign)designs.get(index);
   }

/**
 * @param design
 */
   public void removeShipDesign(ShipDesign design) 
	{
   	designs.remove ( design );
	}
}
