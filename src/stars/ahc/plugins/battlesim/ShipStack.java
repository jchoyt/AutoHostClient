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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
package stars.ahc.plugins.battlesim;

import stars.ahc.ShipDesign;

/**
 * @author Steve Leach
 *
 */
public class ShipStack
{
   public int shipCount = 0;
   public ShipDesign design = null;
   /**
    * total number of points of damage for the stack
    */
   public int damage = 0;
   public int shields = 0;
   public int xpos = 0;
   public int ypos = 0;
   public boolean escaped = false;
   public int randomMass = 0;
   public ShipStack target = null;
   
   public ShipStack( ShipDesign design, int count )
   {
      this.design = design;
      this.shipCount = count;
   }
   
   public boolean stillFighting()
   {
      return (shipCount > 0) && (escaped == false);
   }
   
   public String toString()
   {
      int damagePerShip = damage / shipCount;
      int damagePct = 100 * damagePerShip / design.getArmour();
      return design.getName() + " [" + shipCount + "@" + damagePct + "%,"+shields+"]";
   }
}
