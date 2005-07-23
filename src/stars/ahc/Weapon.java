/*
 * Created on Oct 31, 2004
 *
 * Copyright (c) 2004, Steve Leach
 * 
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
 */
package stars.ahc;

/**
 * @author Steve Leach
 *
 */
public class Weapon 
{
   public static final int TYPE_NONE = 0;
   public static final int TYPE_BEAM = 1;
   public static final int TYPE_TORPEDO = 2;
   public static final int TYPE_MISSILE = 3;
   public static final int TYPE_GATTLING = 4;
   public static final int TYPE_SAPPER = 5;

   // Beam weapons
   public static final Weapon LASER = new Weapon("Laser", TYPE_BEAM, 10, 1, 9, 100 );
   public static final Weapon X_RAY = new Weapon("X-Ray Laser", TYPE_BEAM, 16, 1, 9, 100 );
   public static final Weapon MINIGUN = new Weapon("Mini Gun", TYPE_GATTLING, 13, 2, 12, 100 );
   public static final Weapon YAKIMORA = new Weapon("Yakimora Light Phaser", TYPE_BEAM, 26, 1, 9, 100 );
   public static final Weapon BLACKJACK = new Weapon("Blackjack", TYPE_BEAM, 90, 0, 10, 100 );
   public static final Weapon PHASER_BAZOOKER = new Weapon("Phaser Bazooker", TYPE_BEAM, 26, 2, 7, 100 );
   public static final Weapon PULSED_SAPPER = new Weapon("Pulsed Sapper", TYPE_SAPPER, 82, 3, 14, 100 );
   public static final Weapon COLLOIDAL_PHASER = new Weapon("Colloidal Phaser", TYPE_BEAM, 26, 3, 5, 100 );
   public static final Weapon GATTLING_GUN = new Weapon("Gattling Gun", TYPE_GATTLING, 31, 2, 12, 100 );
   public static final Weapon MINI_BLASTER = new Weapon("Mini Blaster", TYPE_BEAM, 66, 1, 9, 100 );
   public static final Weapon BLUDGEON = new Weapon("Bludgeon", TYPE_BEAM, 231, 0, 10, 100 );
   public static final Weapon MK_IV_BLASTER = new Weapon("Mark IV Blaster", TYPE_BEAM, 66, 2, 7, 100 );
   public static final Weapon PHASED_SAPPER = new Weapon("Phased Sapper", TYPE_SAPPER, 211, 3, 14, 100 );
   public static final Weapon HEAVY_BLASTER = new Weapon("Heavy Blaster", TYPE_BEAM, 66, 3, 5, 100 );
   public static final Weapon GATTLING_NEUTRINO = new Weapon("Gattling Neutrino Cannon", TYPE_GATTLING, 80, 2, 13, 100 );
   public static final Weapon MYOPIC_DISRUPTOR = new Weapon("Myopic Disruptor", TYPE_BEAM, 169, 1, 9, 100 );
   public static final Weapon BLUNDERBUSS = new Weapon("Blunderbuss", TYPE_BEAM, 592, 0, 11, 100 );   
   public static final Weapon DISRUPTOR = new Weapon("Disruptor", TYPE_BEAM, 169, 2, 8, 100 );
   public static final Weapon MCM = new Weapon("Multi Contained Munition", TYPE_BEAM, 140, 3, 6, 100 );
   public static final Weapon SYNCRO_SAPPER = new Weapon("Syncro Sapper", TYPE_SAPPER, 541, 3, 14, 100 );
   public static final Weapon MEGA_DISRUPTOR = new Weapon("Mega Disruptor", TYPE_BEAM, 169, 3, 6, 100 );
   public static final Weapon BIG_MUTHA = new Weapon("Big Mutha Cannon", TYPE_GATTLING, 204, 2, 13, 100 );
   public static final Weapon STREAMING_PULV = new Weapon("Streaming Pulverizer", TYPE_BEAM, 433, 1, 9, 100 );
   public static final Weapon AMP = new Weapon("Antimatter Pulverizer", TYPE_BEAM, 433, 2, 8, 100 );
   
   public static final Weapon ALPHA = new Weapon("Alpha Torpedo", TYPE_TORPEDO, 5, 4, 0, 35 );
   public static final Weapon BETA = new Weapon("Beta Torpedo", TYPE_TORPEDO, 12, 4, 1, 35 );
   public static final Weapon DELTA = new Weapon("Delta Torpedo", TYPE_TORPEDO, 26, 4, 1, 60 );
   public static final Weapon EPSILON = new Weapon("Epsilon Torpedo", TYPE_TORPEDO, 48, 5, 2, 65 );
   public static final Weapon RHO = new Weapon("Rho Torpedo", TYPE_TORPEDO, 90, 5, 2, 75 );
   public static final Weapon UPSILON = new Weapon("Upsilon Torpedo", TYPE_TORPEDO, 169, 5, 3, 75 );
   public static final Weapon OMEGA = new Weapon("Omega Torpedo", TYPE_TORPEDO, 316, 5, 4, 80 );
   public static final Weapon AM_TORP = new Weapon("Antimatter Torpedo", TYPE_TORPEDO, 60, 6, 0, 85 );
   
   public static final Weapon JIHAD = new Weapon("Jihad Missile", TYPE_MISSILE, 85, 5, 0, 20 );
   public static final Weapon JUGG = new Weapon("Juggernaught Missile", TYPE_MISSILE, 150, 5, 1, 20 );
   public static final Weapon DOOM = new Weapon("Doomsday Missile", TYPE_MISSILE, 280, 6, 2, 25 );
   public static final Weapon ARM = new Weapon("Armageddon Missile", TYPE_MISSILE, 525, 6, 3, 30 );
   
   private static final Weapon[] WEAPONS = 
   {
         LASER, X_RAY, MINIGUN, YAKIMORA, BLACKJACK, PHASER_BAZOOKER, PULSED_SAPPER, COLLOIDAL_PHASER,
         GATTLING_GUN, MINI_BLASTER, BLUDGEON, MK_IV_BLASTER, PHASED_SAPPER, HEAVY_BLASTER,
         GATTLING_NEUTRINO, MYOPIC_DISRUPTOR, BLUNDERBUSS, DISRUPTOR, MCM, SYNCRO_SAPPER, MEGA_DISRUPTOR,
         BIG_MUTHA, STREAMING_PULV, AMP, 
         ALPHA, BETA, DELTA, EPSILON, RHO, UPSILON, OMEGA, AM_TORP,
         JIHAD, JUGG, DOOM, ARM
   };
   
   public String name;
   public int category;
   public int power;
   public int range;
   public int initiative;
   public int accuracy;
   
   public Weapon( String name, int category, int power, int range, int initiative, int accuracy )
   {
      this.name = name;
      this.category = category;
      this.power = power;
      this.range = range;
      this.initiative = initiative;
      this.accuracy = accuracy;
   }   
   
   public static Weapon getWeaponByName( String name )
   {
      if (name == null) return null;
      
      for (int n = 0; n < WEAPONS.length; n++)
      {
         if (WEAPONS[n].name.equalsIgnoreCase(name))
         {
            return WEAPONS[n];
         }
      }
      return null;
   }
   
   public static Weapon[] getAllWeapons()
   {
      return WEAPONS;
   }
}
