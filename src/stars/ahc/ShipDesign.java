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
package stars.ahc;

import java.util.Properties;

/**
 * Represents a ship design from a Stars! game.
 * <p>
 * Each player may have up to 16 ship designs at any one time.
 * 
 * @author Steve Leach
 */
public class ShipDesign
{
	/* 
	 * This is the function of the entire file shipdesign.java
	 * Below are set the constants and variables to be used for the Function
	 */
   public static final int HULLTYPE_UNKNOWN = 0;
   public static final int HULLTYPE_SCOUT = 1;
   public static final int HULLTYPE_FRIGATE = 2;
   public static final int HULLTYPE_DESTROYER = 3;
   public static final int HULLTYPE_CRUISER = 4;
   public static final int HULLTYPE_BATTLECRUISER = 5;
   public static final int HULLTYPE_BATTLESHIP = 6;
   public static final int HULLTYPE_DREADNAUGHT = 7;
   public static final int HULLTYPE_NUBIAN = 8;
   
   public static final int BATTLE_COMPUTER = 101;
   public static final int SUPER_COMPUTER = 102;
   public static final int BATTLE_NEXUS = 103;

   private static final int MAX_WEAPON_SLOTS = 20;
   
   private static final int[] hullWeaponSlots = { 1, 1, 3, 3, 6, 9, 20, 0, 12 };

//   private static String[] names = {
//         "Unknown",
//         "Scout", "Frigate", "Destroyer", "Cruiser", "BattleCruiser", "Battleship", "Dreadnaught", "Nubian"
//   };
   
   private int hullType = HULLTYPE_UNKNOWN;
   private String name = "";
   private String owner = "";
   private int speed4 = 0;			// battle speed multiplied by 4
   private int mass = 0;
   private int initiative = 0;
   private int armour = 0;
   private int shields = 0;
   private int cloak = 0;
   private int scan = 0;
   private int penscan = 0;
   private int weaponSlotsUsed = 0;
   private int[] weaponCount;
   private int[] weaponPower;
   private int[] weaponRange;
   private int[] weaponCategory;
   private int[] weaponAccuracy;
   private int[] weaponInitiative;
   private String[] weaponName;
   private String imageFileName = null;
   private boolean regenShields = false;
   private boolean warmonger = false;
   private int jamming = 0;
   private int computing = 0;
   private int capacitors = 0;
   private int deflectors = 0;
   private int bc = 0;
   private int bsc = 0;
   private int nexus = 0;
   private boolean isStarbase = false;
   private int boraniumCost = 1;		// for attractiveness calculations
   private int resourceCost = 1;		// for attractiveness calculations
   
   public ShipDesign( int hullType, String name )
   {   
      this.hullType = hullType;
      this.name = name;
      
      setupWeaponSlots();
   }
   
   public ShipDesign( ShipHull hullType, String name )
   {
      this.hullType = hullType.index;
      this.name = name;
      
      setupWeaponSlots();
   }

   public ShipDesign()
   {
      setupWeaponSlots();
   }

   private void setupWeaponSlots()
   {
      if ((weaponCount == null) || (weaponCount.length != MAX_WEAPON_SLOTS))
      {
	      weaponCount = new int[MAX_WEAPON_SLOTS];
	      weaponPower = new int[MAX_WEAPON_SLOTS];
	      weaponRange = new int[MAX_WEAPON_SLOTS];
	      weaponCategory = new int[MAX_WEAPON_SLOTS];
	      weaponAccuracy = new int[MAX_WEAPON_SLOTS];
	      weaponInitiative = new int[MAX_WEAPON_SLOTS];
	      weaponName = new String[MAX_WEAPON_SLOTS];
      }
   }
   
   public void setWeapon( int slot, int count, int category, int power, int range, int initiative, int accuracy, String name )
   {
      weaponCount[slot] = count;
      weaponCategory[slot] = category;
      weaponPower[slot] = power;
      weaponRange[slot] = range;
      weaponInitiative[slot] = initiative;
      weaponAccuracy[slot] = accuracy;
      weaponName[slot] = name;
   }
   
   public void setWeapon( int slot, int count, Weapon wpn )
   {
      setWeapon( slot, count, wpn.category, wpn.power, wpn.range, wpn.initiative, wpn.accuracy, wpn.name );
   }
   
   public void addWeapon( int count, int category, int power, int range, int initiative, int accuracy, String name )
   {
      setWeapon( weaponSlotsUsed, count, category, power, range, initiative, accuracy, name );
      weaponSlotsUsed++;
   }
   
   public void addWeapon( Weapon weapon, int count )
   {
      addWeapon( count, weapon.category, weapon.power, weapon.range, weapon.initiative, weapon.accuracy, weapon.name );
   }
   
   public void setBattleSpeed( double speed )
   {
      this.speed4 = (int)Math.round(speed * 4);
   }
   public int getArmour()
   {
      return armour;
   }
   public void setArmour(int armour)
   {
      this.armour = armour;
   }
   public int getInitiative()
   {
      return initiative;
   }
   public void setInitiative(int initiative)
   {
      this.initiative = initiative;
   }
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   public int getShields()
   {
      return shields;
   }
   public void setShields(int shields)
   {
      this.shields = shields;
   }
   public int getMass()
   {
      return mass;
   }
   public void setMass(int mass)
   {
      this.mass = mass;
   }
   public boolean isRegenShields()
   {
      return regenShields;
   }
   public void setRegenShields(boolean regenShields)
   {
      this.regenShields = regenShields;
   }
   public boolean isWarmonger()
   {
      return warmonger;
   }
   public void setWarmonger(boolean warmonger)
   {
      this.warmonger = warmonger;
   }
   /**
    * Returns the design's battle speed multiplied by 4 (eg. speed 0.75 returns 3)
    */  
   public int getSpeed4()
   {
      return speed4;
   }
   public int getWeaponSlots()
   {
      return weaponSlotsUsed;
   }
   public int getWeaponInit(int slot)
   {
      return weaponInitiative[slot];
   }
   public String getWeaponName(int slot)
   {
      return weaponName[slot];
   }
   public int getWeaponPower(int slot)
   {
      return weaponPower[slot];
   }
   public int getWeaponCount(int slot)
   {
      return weaponCount[slot];
   }
   public int getWeaponType(int slot)
   {
      return weaponCategory[slot];
   }
   /**
    * Returns the range of the weapon in the specified slot
    * <p>
    * If the design is a starbase then the range is automatically increased by 1
    */
   public int getWeaponRange(int slot)
   {
      int range = weaponRange[slot];
      if (isStarbase)
      {
         range += 1;
      }
      return range;
   }
   public double getWeaponAccuracy(int slot)
   {
      return weaponAccuracy[slot] / 100.0;
   }
   public String getOwner()
   {
      return owner;
   }
   public void setOwner(String owner)
   {
      this.owner = owner;
   }

   /**
    * Stores properties of the design
    */
   public void storeProperties(Properties properties, int index)
   {
      String base = "ShipDesigns." + index;
      
      properties.setProperty( base+".name", getName() );
      properties.setProperty( base+".owner", getOwner() );
      properties.setProperty( base+".hullCode", ""+hullType );
      
      if (hullType > HULLTYPE_UNKNOWN)
      {
         properties.setProperty( base+".hull", getHullName() );
      }
      
      properties.setProperty( base+".mass", ""+mass );
      properties.setProperty( base+".armour", ""+armour );
      properties.setProperty( base+".shields", ""+shields );
      properties.setProperty( base+".speed4", ""+speed4 );
      properties.setProperty( base+".initiative", ""+initiative );
      properties.setProperty( base+".regenShields", ""+regenShields );
      properties.setProperty( base+".capacitors", ""+capacitors );
      properties.setProperty( base+".deflectors", ""+deflectors );
      properties.setProperty( base+".computing", ""+computing );
      properties.setProperty( base+".jamming", ""+jamming );
      properties.setProperty( base+".resourceCost", ""+resourceCost );
      properties.setProperty( base+".boraniumCost", ""+boraniumCost );
      
      properties.setProperty( base+".weaponSlots.count", ""+getWeaponSlots() );
      
      for (int n = 0; n < getWeaponSlots(); n++)
      {
         properties.setProperty( base+".weaponSlots." + (n+1) + ".name", ""+getWeaponName(n) );
         properties.setProperty( base+".weaponSlots." + (n+1) + ".count", ""+getWeaponCount(n) );
      }
   }
   
   public void loadProperties(Properties properties, int index)
   {
      String base = "ShipDesigns." + index;
      
      name = properties.getProperty( base + ".name" );
      owner = properties.getProperty( base + ".owner" );
      
      hullType = Utils.safeParseInt( properties.getProperty( base + ".hullCode" ), 0 );
      
      setupWeaponSlots();
      
      mass = Utils.safeParseInt( properties.getProperty( base + ".mass" ), 0 );
      armour = Utils.safeParseInt( properties.getProperty( base + ".armour" ), 0 );
      shields = Utils.safeParseInt( properties.getProperty( base + ".shields" ), 0 );
      initiative = Utils.safeParseInt( properties.getProperty( base + ".initiative" ), 0 );
      speed4 = Utils.safeParseInt( properties.getProperty( base + ".speed4" ), 0 );
      capacitors = Utils.safeParseInt( properties.getProperty( base + ".capacitors" ), 0 );
      deflectors = Utils.safeParseInt( properties.getProperty( base + ".deflectors" ), 0 );
      computing = Utils.safeParseInt( properties.getProperty( base + ".computing" ), 0 );
      jamming = Utils.safeParseInt( properties.getProperty( base + ".jamming" ), 0 );
      resourceCost = Utils.safeParseInt( properties.getProperty( base + ".resourceCost" ), 0 );
      boraniumCost = Utils.safeParseInt( properties.getProperty( base + ".boraniumCost" ), 0 );
      
      String s = ""+properties.getProperty( base + ".regenShields" );
      regenShields = s.equals("true");

      weaponSlotsUsed = Utils.safeParseInt( properties.getProperty(base+".weaponSlots.count"), 0 );
      
      for (int n = 0; n < weaponSlotsUsed; n++)
      {
         String name = properties.getProperty( base+".weaponSlots." + (n+1) + ".name" );
         Weapon wpn = Weapon.getWeaponByName(name);
         
         int count = Utils.safeParseInt( properties.getProperty( base+".weaponSlots." + (n+1) + ".count" ), 0 );
         
         if (wpn != null)
         {
            setWeapon( n, count, wpn );
         }
      }
   }

   public void setHullType(int typeCode)
   {
      this.hullType = typeCode;
      this.isStarbase = ShipHull.hullTypes[typeCode].isBase;
      setupWeaponSlots();
   }   
   
   public void setHullType( ShipHull hullType )
   {
      this.hullType = hullType.index;
      this.isStarbase = hullType.isBase;
      setupWeaponSlots();
   }
   
   public int getHullType()
   {
      return hullType;
   }

   public void setWeaponCount(int slot, int newCount)
   {
      weaponCount[slot] = newCount;
   }

   public int getMaxSlots()
   {
      return hullWeaponSlots[ hullType ];
   }

   public int getJamming()
   {
      return jamming;
   }
   
   public void setJamming( int jamming )
   {
      this.jamming = jamming;
   }

   /**
    * @return
    */
   public int getComputing()
   {
      return this.computing;
   }

   public void setComputers( int bc, int bsc, int nexus )
   {
      this.bc = bc;
      this.bsc = bsc;
      this.nexus = nexus;
      
      computing = 0;
      addComputer( BATTLE_COMPUTER, bc );
      addComputer( SUPER_COMPUTER, bsc );
      addComputer( BATTLE_NEXUS, nexus );
   }
   
   /**
    * Returns the number of computers of the specified type on the design 
    */
   public int getComputers( int type )
   {
      switch (type)
      {
         case BATTLE_COMPUTER: 
            return bc;
         case SUPER_COMPUTER:
            return bsc;
         case BATTLE_NEXUS:
            return nexus;
      }
      return 0;
   }
   
   public void addComputer( int type, int quantity )
   {
      double typeModifier = 1.0;
      
      switch (type)
      {
         case BATTLE_COMPUTER: 
            typeModifier = 0.2;
            break;
         case SUPER_COMPUTER:
            typeModifier = 0.3;
            break;
         case BATTLE_NEXUS:
            typeModifier = 0.5;
            break;
      }
      
      int comp = 100 - computing;
      
      for (int n = 0; n < quantity; n++)
      {
         comp *= (1.0 - typeModifier);
      }
      
      computing = 100 - comp;
   }
   
   /**
    * Gets the maximum range of any weapon on this design 
    */
   public int getMaxRange()
   {
      int maxRange = 0;
      
      for (int n = 0; n < weaponSlotsUsed; n++)
      {
         if (weaponRange[n] > maxRange)
         {
            maxRange = getWeaponRange(n);
         }
      }
      
      return maxRange;
   }

   
   public int getCapacitors()
   {
      return capacitors;
   }
   public void setCapacitors(int capacitors)
   {
      this.capacitors = capacitors;
   }
   public int getDeflectors()
   {
      return deflectors;
   }
   public void setDeflectors(int deflectors)
   {
      this.deflectors = deflectors;
   }
   public String getDesignAsString()
   {
      String s = "";
      
      if (Utils.empty(owner) == false)
      {
         s += owner + " ";
      }
      s += name + " "; 
      s += "(" + getHullName() + ")\n";
      s += "amour=" + armour + ", shields=" + shields;      
      s += (regenShields) ? " (regen)\n" : "\n";
      s += "speed=" + speed4/4.0 + ", initative=" + initiative + "\n";
      s += "capacitors=" + capacitors + ", deflectors=" + deflectors + "\n";
      s += "jamming=" + jamming + "%, computers=" + bc + "/" + bsc + "/" + nexus + "\n";
      
      for (int n = 0; n < weaponSlotsUsed; n++)
      {
         s += "Slot " + (n+1) + ": " + weaponCount[n] + " x " + weaponName[n] + "\n";
      }
         
      return s;
   }
   
   
   public int getBoraniumCost()
   {
      return boraniumCost;
   }
   public void setBoraniumCost(int boraniumCost)
   {
      this.boraniumCost = boraniumCost;
   }
   public int getResourceCost()
   {
      return resourceCost;
   }
   public void setResourceCost(int resourceCost)
   {
      this.resourceCost = resourceCost;
   }
   
   /**
    * Returns the main weapon slot for the design.
    * <p>
    * This is the slot that has the most weapons in it.  If there are several
    * slots with the same number of weapons, the first is used.
    * <p>
    * This method is intended for use in selecting targets, as different 
    * weapon types will prefer different targets.
    */
   public int getMainWeaponSlot()
   {
      int max = 0;
      int slot = 0;
      for (int n = 0; n < weaponSlotsUsed; n++)
      {
         if (weaponCount[n] > max)
         {
            max = weaponCount[n];
            slot = n;
         }
      }
      return slot;
   }


   /**
    * Returns the range of the shortest range weapon on the design
    */
   public int getShortestRange()
   {
      if (weaponSlotsUsed == 0) return 0;
      
      int shortest = Integer.MAX_VALUE;
      
      for (int n = 0; n < weaponSlotsUsed; n++)
      {
         if (weaponRange[n] < shortest)
         {
            shortest = getWeaponRange(n);
         }
      }
      
      return shortest;
   }


   /**
    * @return
    */
   public String getHullName()
   {
      return ShipHull.hullTypes[hullType].name;
   }
   
   public void setStarbase( boolean isStarbase )
   {
      this.isStarbase = isStarbase;
   }
   
   public boolean isStarbase()
   {
      return isStarbase();
   }
}
