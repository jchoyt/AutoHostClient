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
 */
package test.stars.ahc;

import stars.ahc.ShipDesign;
import stars.ahc.plugins.battlesim.BattleSimulation;
import stars.ahc.plugins.battlesim.OneOnOneBattle;
import junit.framework.TestCase;

/**
 * @author Steve Leach
 *
 */
public class BattleSimulatorTest extends TestCase
{
   public void testOneOnOne() throws Exception
   {
      ShipDesign design1 = new ShipDesign( ShipDesign.HULLTYPE_BATTLECRUISER, "Rabid Dog" );
      design1.setMass( 174 );
      design1.setArmour( 1000 );
      design1.setShields( 240 );
      design1.setBattleSpeed( 1.75 );
      design1.setInitiative( 5 );
      design1.addWeapon( 3, ShipDesign.WPNTYPE_BEAM, 26, 3, 5, 100, "Colloidal Phaser" );
      design1.addWeapon( 3, ShipDesign.WPNTYPE_SAPPER, 82, 3, 14, 100, "Pulsed Sapper" );
      design1.addWeapon( 3, ShipDesign.WPNTYPE_BEAM, 26, 3, 5, 100, "Colloidal Phaser" );
      
      ShipDesign design2 = new ShipDesign( ShipDesign.HULLTYPE_CRUISER, "CCC" );
      design2.setMass( 130 );
      design2.setArmour( 700 );
      design2.setShields( 560 );
      design2.setRegenShields(true);
      design2.setBattleSpeed( 1.25 );
      design2.setInitiative( 5 );
      design2.addWeapon( 2, ShipDesign.WPNTYPE_BEAM, 26, 3, 5, 100, "Colloidal Phaser" );
      design2.addWeapon( 2, ShipDesign.WPNTYPE_SAPPER, 82, 3, 14, 100, "Pulsed Sapper" );
      design2.addWeapon( 2, ShipDesign.WPNTYPE_BEAM, 26, 3, 5, 100, "Colloidal Phaser" );
      
      BattleSimulation battle = new OneOnOneBattle( design1, 10, design2, 14 );
      
      battle.setVerbose(true);
      
      while (battle.stillFighting())
      {
         battle.simulateNextRound();
      }
   }
}
