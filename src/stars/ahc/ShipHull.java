/*
 * Created on Nov 28, 2004
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
 *
 */
public class ShipHull
{
   public String name;
   public int weaponSlots;
   public int index;
   public boolean isBase;

   public ShipHull( int index, String name, int weaponSlots, boolean isBase )
   {
      this.index = index;
      this.name = name;
      this.weaponSlots = weaponSlots;
      this.isBase = isBase;
   }
   
   public static final ShipHull UNKNOWN 		= new ShipHull(  0, "Unknown", 0, false );
   public static final ShipHull SCOUT 			= new ShipHull(  1, "Scout", 1, false );
   public static final ShipHull FRIGATE 		= new ShipHull(  2, "Frigate", 1, false );
   public static final ShipHull DESTROYER 		= new ShipHull(  3, "Destroyer", 3, false );
   public static final ShipHull CRUISER 		= new ShipHull(  4, "Cruiser", 3, false );
   public static final ShipHull BATTLECRUISER 	= new ShipHull(  5, "Battle Cruiser", 3, false );
   public static final ShipHull BATTLESHIP 		= new ShipHull(  6, "Battleship", 5, false );
   public static final ShipHull DREADNAUGHT 	= new ShipHull(  7, "Dreadnaught", 7, false );
   public static final ShipHull NUBIAN 			= new ShipHull(  8, "Nubian", 12, false );
   public static final ShipHull PRIVATEER		= new ShipHull(  9, "Privateer", 2, false );
   public static final ShipHull ROGUE			= new ShipHull( 10, "Rogue", 2, false );
   public static final ShipHull GALLEON			= new ShipHull( 11, "Galleon", 2, false );
   public static final ShipHull MINI_COLONY		= new ShipHull( 12, "Mini Colony", 0, false );
   public static final ShipHull COLONY			= new ShipHull( 13, "Colony Ship", 0, false );
   public static final ShipHull MINI_BOMBER		= new ShipHull( 14, "Mini Bomber", 0, false );
   public static final ShipHull B17				= new ShipHull( 15, "B17 Bomber", 0, false );
   public static final ShipHull STEALTH_BOMBER	= new ShipHull( 16, "Stealth Bomber", 0, false );
   public static final ShipHull B52				= new ShipHull( 17, "B52 Bomber", 0, false );
   public static final ShipHull MIDGET_MINER	= new ShipHull( 18, "Midget Miner", 0, false );
   public static final ShipHull MINI_MINER		= new ShipHull( 19, "Mini Miner", 0, false );
   public static final ShipHull MINER			= new ShipHull( 20, "Miner", 0, false );
   public static final ShipHull MAXI_MINER		= new ShipHull( 21, "Maxi Miner", 0, false );
   public static final ShipHull ULTRA_MINER		= new ShipHull( 22, "Ultra Miner", 0, false );
   public static final ShipHull FUEL_TRANSPORT	= new ShipHull( 23, "Fuel Transport", 0, false );
   public static final ShipHull SFX				= new ShipHull( 24, "SFX", 0, false );
   public static final ShipHull MINI_MINELAYER	= new ShipHull( 25, "Mini Minelayer", 0, false );
   public static final ShipHull SUPER_MINELAYER	= new ShipHull( 26, "Super Minelayer", 0, false );
   public static final ShipHull METAMORPH		= new ShipHull( 27, "Metamorph", 0, false );
   public static final ShipHull SMALL_FRT		= new ShipHull( 28, "Small Freighter", 0, false );
   public static final ShipHull MEDIUM_FRT		= new ShipHull( 29, "Medium Freighter", 0, false );
   public static final ShipHull LARGE_FRT		= new ShipHull( 30, "Large Freighter", 0, false );
   public static final ShipHull SUPER_FRT		= new ShipHull( 31, "Super Freighter", 0, false );
   public static final ShipHull ORBITAL_FORT	= new ShipHull( 32, "Orbital Fort", 2, true );
   public static final ShipHull SPACE_DOCK		= new ShipHull( 33, "Space Dock", 3, true );
   public static final ShipHull SPACE_STATION	= new ShipHull( 34, "Space Station", 4, true );
   public static final ShipHull ULTRA_STATION	= new ShipHull( 35, "Ultra Station", 6, true );
   public static final ShipHull DEATH_STAR		= new ShipHull( 36, "Death Star", 4, true );
   
   public static final ShipHull[] hullTypes = { 
         UNKNOWN, 
         SCOUT, FRIGATE, DESTROYER, CRUISER, BATTLECRUISER, BATTLESHIP, DREADNAUGHT, NUBIAN,
         PRIVATEER, ROGUE, GALLEON,
         MINI_COLONY, COLONY,
         MINI_BOMBER, B17, STEALTH_BOMBER, B52,
         MIDGET_MINER, MINI_MINER, MINER, MAXI_MINER, ULTRA_MINER,
         FUEL_TRANSPORT, SFX,
         MINI_MINELAYER, SUPER_MINELAYER,
         METAMORPH,
         SMALL_FRT, MEDIUM_FRT, LARGE_FRT, SUPER_FRT,
         ORBITAL_FORT, SPACE_DOCK, SPACE_STATION, ULTRA_STATION, DEATH_STAR
   };

   private static String[] typeNames = null;
   
   public static String[] getTypeNames()
   {
      if (typeNames == null)
      {
         typeNames = new String[hullTypes.length];
         for (int n = 0; n < hullTypes.length; n++)
         {
            typeNames[n] = hullTypes[n].name;
         }
      }
      return typeNames;
   }
}
