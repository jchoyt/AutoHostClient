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
 * Represents a stack of ships on the battle board.
 * <p>
 * A stack constists of one or more ships of the same design that are part of the same fleet.
 * 
 * @author Steve Leach
 */
public class ShipStack
{
   public static final int ORDERS_MAX_RATIO = 1;
   public static final int ORDERS_DISENGAGE = 2;
   
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
   public ShipStack targetedBy = null;
   public int preferredRange = 0;
   public int battleOrders = ORDERS_MAX_RATIO;
   public int movesMade = 0;
   public int sortValue = 0;
   public int side = 0;
   
   public ShipStack( ShipDesign design, int count )
   {
      init( design, count, 0 );
   }
   
   public ShipStack( ShipDesign design, int count, int side )
   {
      init( design, count, side );
   }
   
   public void init( ShipDesign design, int count, int side )
   {
      this.design = design;
      this.shipCount = count;
      this.side = side;
      
      if (design != null)
      {
         this.shields = design.getShields() * count;
      }
   }
   
   /**
    * Returns true if the stack is still fighting (has not been killed and hasn't escaped) 
    */
   public boolean stillFighting()
   {
      return (shipCount > 0) && (escaped == false);
   }
   
   public String toString()
   {
      if (shipCount == 0) return design.getName() + " [dead]";
      
      return design.getName() + " [" + shipCount + "@" + getDamagePercent() + "%,"+shields+"]";
   }
   
   public int getDamagePercent()
   {
      int damagePerShip = (shipCount == 0) ? 0 : damage / shipCount;
      return 100 * damagePerShip / design.getArmour();
   }

   /**
    */
   public void setPos(int x, int y)
   {
      xpos = x;
      ypos = y;
   }
   
   public String getStackAsString()
   {
      return shipCount + " x " + design.getDesignAsString();
   }
}
