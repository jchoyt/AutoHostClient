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
   public static final int HULLTYPE_UNKNOWN = 0;
   public static final int HULLTYPE_SCOUT = 1;
   public static final int HULLTYPE_FRIGATE = 2;
   public static final int HULLTYPE_DESTROYER = 3;
   public static final int HULLTYPE_CRUISER = 4;
   public static final int HULLTYPE_BATTLECRUISER = 5;
   public static final int HULLTYPE_BATTLESHIP = 6;
   public static final int HULLTYPE_DREADNAUGHT = 7;
   public static final int HULLTYPE_NUBIAN = 8;
   
   private static final int[] hullWeaponSlots = { 1, 1, 3, 3, 6, 9, 20, 0, 0 };
   
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
   private int weaponsSlots = 0;
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
   
   public ShipDesign( int hullType, String name )
   {
      this.hullType = hullType;
      this.name = name;
      
      setupWeaponSlots();
   }
   
   /**
    * 
    */
   public ShipDesign()
   {
   }

   private void setupWeaponSlots()
   {
      weaponsSlots = hullWeaponSlots[ hullType ];
      
      if ((weaponCount == null) || (weaponCount.length != weaponsSlots))
      {
	      weaponCount = new int[weaponsSlots];
	      weaponPower = new int[weaponsSlots];
	      weaponRange = new int[weaponsSlots];
	      weaponCategory = new int[weaponsSlots];
	      weaponAccuracy = new int[weaponsSlots];
	      weaponInitiative = new int[weaponsSlots];
	      weaponName = new String[weaponsSlots];
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
   public int getWeaponRange(int slot)
   {
      return weaponRange[slot];
   }
   public String getOwner()
   {
      return owner;
   }
   public void setOwner(String owner)
   {
      this.owner = owner;
   }

   public static String[] getHullTypeNames()
   {
      String[] names = {
            "Unknown",
            "Scout", "Frigate", "Destroyer", "Cruiser", "BattleCruiser", "Battleship", "Dreadnaught", "Nubian"
      };
      return names;
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
         properties.setProperty( base+".hull", getHullTypeNames()[hullType] );
      }
      
      properties.setProperty( base+".mass", ""+mass );
      properties.setProperty( base+".armour", ""+armour );
      properties.setProperty( base+".shields", ""+shields );
      properties.setProperty( base+".speed4", ""+speed4 );
      properties.setProperty( base+".initiative", ""+initiative );
      properties.setProperty( base+".regenShields", ""+regenShields );
      
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
}