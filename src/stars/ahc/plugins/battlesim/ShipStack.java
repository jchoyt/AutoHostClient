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

import java.util.Properties;

import stars.ahc.ShipDesign;
import stars.ahc.Utils;

/**
 * Represents a stack of ships on the battle board.
 * <p>
 * A stack constists of one or more ships of the same design that are part of the same fleet.
 * 
 * @author Steve Leach
 */
public class ShipStack
{
   public static final int ORDERS_MAX_RATIO = 1;	// Maximize damage ratio
   public static final int ORDERS_DISENGAGE = 2;	// Disengage
   public static final int ORDERS_MIN_TO_SELF = 3;	// Minimize damage to self
   public static final int ORDERS_MAX_NET = 4;		// Maximize net damage
   public static final int ORDERS_MAX = 5;			// Maximize damage
   public static final int ORDERS_DIS_CHAL = 6;		// Disengage if challenged
   
   private int shipCount = 0;
   public ShipDesign design = null;
   /**
    * total number of points of damage for the stack
    */
   public String owner = null;
   public int raceIndex = 0;
   /**
    * The total number of points of damage sustained by the target so far
    */
   public int naturalOrder = 0;
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
   //public int sortValue = 0;
   public int side = 0;
   public int originalShipCount = 0;
   public int firingOrderValue = 0;
   
   // For storing results of multiple runs by the simulator
   public int cumulativeSimulations = 0;
   public int cumulativeSurvivors = 0;
   public int minimumSurvivors = Integer.MAX_VALUE;
   public int maximumSurvivors = 0;
   
   public ShipStack( ShipDesign design, int count )
   {
      init( design, count, 0 );
   }
   
   public ShipStack( ShipDesign design, int count, int side )
   {
      init( design, count, side );
   }
   
   /**
    * Creates a new ShipStack with details taken from the specified properties object
    * 
    * @param index is the number of the stack within the properties object 
    */
   public ShipStack( Properties props, int index )
   {
      loadProperties( props, index );
   }
   
   public void init( ShipDesign design, int count, int side )
   {
      this.design = design;
      this.shipCount = this.originalShipCount = count;
      this.side = side;
      
      if (design != null)
      {
         this.shields = design.getShields() * count;
         
         this.owner = design.getOwner();
      }
   }
   
   /**
    * Re-initialises the stack
    * <p>
    * This should be called if the underlying ship design changes for an existing stack
    */
   public void reinit()
   {
      this.owner = design.getOwner();
   }
   
   /**
    * Reset the stack back to it's starting values 
    */
   public void reset()
   {
      shipCount = originalShipCount;
      damage = 0;		// TODO: ships don't necesarily start undamaged
      shields = design.getShields() * shipCount;
      firingOrderValue = Utils.getRandomInt();
      xpos = 0;
      ypos = 0;
      escaped = false;
      movesMade = 0;
      
      if (design.getWeaponSlots() == 0)
      {
         battleOrders = ORDERS_DISENGAGE;
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
      
      return owner + " " + design.getName() + " [" + shipCount + "@" + getDamagePercent() + "%,"+shields+"]";
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
   
   /**
    * Reset the "multiple simulation result" accumulator 
    */
   public void resetAccumulator()
   {
      cumulativeSimulations = 0;
      cumulativeSurvivors = 0;
      minimumSurvivors = Integer.MAX_VALUE;
      maximumSurvivors = 0;
   }
   
   /**
    * Add latest result to "multiple simulation result" accumulator 
    */
   public void accumulateResults()
   {
      cumulativeSimulations++;
      cumulativeSurvivors += shipCount;
      minimumSurvivors = Math.min(shipCount,minimumSurvivors);
      maximumSurvivors = Math.max(shipCount,maximumSurvivors);
   }

   /**
    * @return
    */
   public String getCumulativeResults()
   {
      int average = cumulativeSurvivors / cumulativeSimulations;
      return "Over "+ cumulativeSimulations + " runs: " + minimumSurvivors + ", " + average + ", " + maximumSurvivors + " survived";
   }
   
   /**
    * Stores details of this ship stack, and the underlying ship design, into the properties object  
    * @param index is the number of the stack within the properties object 
    */
   public void storeProperties( Properties props, int index )
   {
      String base = "Stacks." + index;

      props.setProperty( base+".shipCount", ""+originalShipCount );
      props.setProperty( base+".owner", owner );
      props.setProperty( base+".side", ""+side );
      props.setProperty( base+".orders", ""+battleOrders );
      props.setProperty( base+".naturalOrder", ""+naturalOrder );
      
      design.storeProperties( props, index );      
   }
   
   /**
    * Loads details of the ship stack from the specified properties object 
    * @param index is the number of the stack within the properties object 
    */
   public void loadProperties( Properties props, int index )
   {
      String base = "Stacks." + index;
      
      int count = Utils.safeParseInt( props.getProperty( base + ".shipCount" ) );
      String owner = props.getProperty( base+".owner" );
      int side = Utils.safeParseInt( props.getProperty( base + ".side" ) );
      
      ShipDesign design = new ShipDesign();
      design.loadProperties( props, index );
      
      init( design, count, side );
      
      naturalOrder = Utils.safeParseInt( props.getProperty( base + ".naturalOrder" ) );
      battleOrders = Utils.safeParseInt( props.getProperty( base + ".orders" ) );
   }
   
   public void setShipCount( int ships )
   {
      shipCount = ships;
      originalShipCount = ships;
   }
   
   public int getShipCount()
   {
      return shipCount;
   }

   public void killShips(int kills)
   {
      shipCount -= kills;
   }
}
