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

import junit.framework.TestCase;
import stars.ahc.ShipDesign;
import stars.ahc.Weapon;
import stars.ahc.plugins.battlesim.BattleSimulation;
import stars.ahc.plugins.battlesim.BattleSimulationError;
import stars.ahc.plugins.battlesim.BattleSimulationListener;
import stars.ahc.plugins.battlesim.BattleSimulationNotification;
import stars.ahc.plugins.battlesim.ShipStack;

/**
 * @author Steve Leach
 *
 */
public class BattleSimulatorTest extends TestCase
{
   private ShipDesign rabidDog, ccc, armBB, chaff;
   private ShipDesign cynicDD;
   private ShipDesign interceptor2;
   private ShipDesign armBB_2;
   
   private BattleSimulationListener consoleStatusListener = new BattleSimulationListener() {
      public void handleNotification(BattleSimulationNotification notification)
      {
         if (notification.round == 0)
         {
            System.out.println( notification.message );
         }
         else
         {
            System.out.println( "Round " + notification.round + " : " + notification.message );
         }
      }
   };
   
   protected void setUp() throws Exception
   {
      rabidDog = new ShipDesign( ShipDesign.HULLTYPE_BATTLECRUISER, "Rabid Dog" );
      rabidDog.setOwner( "EDog" );
      rabidDog.setMass( 174 );
      rabidDog.setArmour( 1000 );
      rabidDog.setShields( 240 );
      rabidDog.setBattleSpeed( 2.25 );
      rabidDog.setInitiative( 5 );
      rabidDog.addWeapon( Weapon.COLLOIDAL_PHASER, 3 );
      rabidDog.addWeapon( Weapon.PULSED_SAPPER, 3 );
      rabidDog.addWeapon( Weapon.COLLOIDAL_PHASER, 3 );
      
      ccc = new ShipDesign( ShipDesign.HULLTYPE_CRUISER, "CCC" );
      ccc.setOwner( "Staz" );
      ccc.setMass( 130 );
      ccc.setArmour( 700 );
      ccc.setShields( 560 );
      ccc.setRegenShields(true);
      ccc.setBattleSpeed( 1.25 );
      ccc.setInitiative( 5 );
      ccc.addWeapon( Weapon.COLLOIDAL_PHASER, 2 );
      ccc.addWeapon( Weapon.PULSED_SAPPER, 2 );
      ccc.addWeapon( Weapon.COLLOIDAL_PHASER, 2 );
      
      // BB, 4 x TDG, 4 x syncro sappers (wings), 16 x arms, 4 x BSC, 3 x jammer 20, no armour
      armBB = new ShipDesign( ShipDesign.HULLTYPE_BATTLESHIP, "ArmBB" );
      armBB.setMass(901);
      armBB.setArmour(2000);
      armBB.setShields(1400);
      armBB.setBattleSpeed( 1.0 );
      armBB.setInitiative( 18 );
      armBB.setJamming( 49 );
      armBB.addWeapon( Weapon.ARM, 6 );
      armBB.addWeapon( Weapon.ARM, 6 );
      armBB.addWeapon( Weapon.SYNCRO_SAPPER, 2 );
      armBB.addWeapon( Weapon.SYNCRO_SAPPER, 2 );
      armBB.addWeapon( Weapon.ARM, 4 );
      
      cynicDD = new ShipDesign( ShipDesign.HULLTYPE_DESTROYER, "cynic DD" );
      cynicDD.setMass(46);
      cynicDD.setArmour(200);
      cynicDD.setShields(0);
      cynicDD.setBattleSpeed( 1.25 );
      cynicDD.setInitiative( 3 );
      cynicDD.addWeapon( Weapon.YAKIMORA, 1 );
      cynicDD.addWeapon( Weapon.YAKIMORA, 1 );

      interceptor2 = new ShipDesign( ShipDesign.HULLTYPE_DESTROYER, "Interceptor 2" );
      interceptor2.setMass(57);
      interceptor2.setArmour(200);
      interceptor2.setShields(0);
      interceptor2.setBattleSpeed( 1.75 );
      interceptor2.setInitiative( 3 );
      interceptor2.addWeapon( Weapon.PHASER_BAZOOKER, 1 );
      interceptor2.addWeapon( Weapon.PHASER_BAZOOKER, 1 );

      // X-Ray chaff at near maxmimum minaturisation
      chaff = new ShipDesign();
      chaff.setName( "Chaff" );
      chaff.setMass( 13 );
      chaff.setArmour( 20 );
      chaff.setShields( 0 );
      chaff.setJamming( 0 );
      chaff.setInitiative( 1 );
      chaff.setBattleSpeed( 0.75 );
      chaff.setBoraniumCost( 2 );
      chaff.setResourceCost( 5 );
      chaff.setComputers( 0, 0, 0 );
      chaff.addWeapon( Weapon.X_RAY, 1 );

      // At max miniaturisation
      armBB_2 = new ShipDesign();
      armBB_2.setName( "Arm BB 2" );
      armBB_2.setMass( 845 );
      armBB_2.setArmour( 2000 );			// No additional armour
      armBB_2.setShields( 1400 );
      armBB_2.setJamming( 66 );
      armBB_2.setInitiative( 18 );
      armBB_2.setBattleSpeed( 1.25 );
      armBB_2.setBoraniumCost( 352 );
      armBB_2.setResourceCost( 684 );
      armBB_2.setComputers( 0, 4, 0 );
      armBB_2.addWeapon( Weapon.ARM, 6 );
      armBB_2.addWeapon( Weapon.ARM, 6 );
      armBB_2.addWeapon( Weapon.SYNCRO_SAPPER, 2 );
      armBB_2.addWeapon( Weapon.SYNCRO_SAPPER, 2 );
      armBB_2.addWeapon( Weapon.ARM, 4 );      
   }
   
   protected void tearDown() throws Exception
   {
      rabidDog = null;
      ccc = null;
   }
   
   public void testWeaponRangeMultiplier()
   {
      assertEquals( 1.000, BattleSimulation.getRangeMultiplier( 0, 3 ), 0.01 );
      assertEquals( 0.967, BattleSimulation.getRangeMultiplier( 1, 3 ), 0.01 );
      assertEquals( 0.933, BattleSimulation.getRangeMultiplier( 2, 3 ), 0.01 );
      assertEquals( 0.900, BattleSimulation.getRangeMultiplier( 3, 3 ), 0.01 );

      assertEquals( 1.000, BattleSimulation.getRangeMultiplier( 0, 2 ), 0.01 );
      assertEquals( 0.950, BattleSimulation.getRangeMultiplier( 1, 2 ), 0.01 );
      assertEquals( 0.900, BattleSimulation.getRangeMultiplier( 2, 2 ), 0.01 );
      
      assertEquals( 1.000, BattleSimulation.getRangeMultiplier( 0, 1 ), 0.01 );
      assertEquals( 0.900, BattleSimulation.getRangeMultiplier( 1, 1 ), 0.01 );
      
      assertEquals( 1.000, BattleSimulation.getRangeMultiplier( 0, 0 ), 0.01 );
   }
   
   public void testBattleOne() throws Exception
   {
      // Setup the battle simulator
      
      BattleSimulation battle = new BattleSimulation();
      
      battle.addNewStack( rabidDog, 12 );
      battle.addNewStack( ccc, 14 );
      
      battle.simulate();
      
      //
      // Test that we got the expected battle outcome
      //
      
      // CCC stack is wiped out
      assertEquals( 0, battle.getStack("CCC").shipCount );
      
      // Rabid Dog stack suffers no ship losses
      assertEquals( 12, battle.getStack("Rabid Dog").shipCount );
      
      // Damage won't always be exactly the same, but should be in the same region
      assertTrue( "Rabid Dogs about 50% damaged", battle.getStack("Rabid Dog").getDamagePercent() > 40 );
      assertTrue( "Rabid Dogs about 50% damaged", battle.getStack("Rabid Dog").getDamagePercent() < 60 );
   }

   /**
    */
   public void testBattleTwo() throws Exception
   {
      BattleSimulation battle = new BattleSimulation();
      
      battle.addNewStack( rabidDog, 7 );
      battle.addNewStack( ccc, 14 );

      //battle.addStatusListener( consoleStatusListener );
      battle.showStacksFull();
      
      battle.simulate();
      
      //
      // Test that we got the expected battle outcome
      //
      
      // CCC stack is undamaged
      assertEquals( 14, battle.getStack("CCC").shipCount );
      assertTrue( "CCC stack undamaged or lightly damaged", battle.getStack("CCC").getDamagePercent() < 5 );
      
      // Rabid Dog stack is wiped out
      assertEquals( 0, battle.getStack("Rabid Dog").shipCount );
      
   }
   
   public void testAccuracyJammersOnly()
   {      
      ShipDesign attacker = new ShipDesign();     
      ShipDesign defender = new ShipDesign();
      defender.setJamming( 49 );
      
      double accuracy = BattleSimulation.getFinalAccuracy( 0.75, attacker, defender );
      
      assertEquals( 0.38, accuracy, 0.01 );
   }

   public void testAccuracyComputersOnly()
   {      
      ShipDesign attacker = new ShipDesign();
      attacker.addComputer( ShipDesign.BATTLE_NEXUS, 1 );
      ShipDesign defender = new ShipDesign();
      
      double accuracy = BattleSimulation.getFinalAccuracy( 0.75, attacker, defender );
      
      assertEquals( 0.88, accuracy, 0.01 );
      
      attacker = new ShipDesign();
      attacker.addComputer( ShipDesign.SUPER_COMPUTER, 2 );
      
      accuracy = BattleSimulation.getFinalAccuracy( 0.75, attacker, defender );
      
      assertEquals( 0.88, accuracy, 0.01 );      
   }
   
   public void testAccuracyComputersBeatJammers()
   {
      ShipDesign attacker = new ShipDesign();
      attacker.addComputer( ShipDesign.SUPER_COMPUTER, 3 );
      ShipDesign defender = new ShipDesign();
      defender.setJamming( 49 );
      
      double accuracy = BattleSimulation.getFinalAccuracy( 0.75, attacker, defender );
      
      assertEquals( 0.79, accuracy, 0.01 );
   }

   public void testAccuracyJammersBeatComputers()
   {
      ShipDesign attacker = new ShipDesign();
      attacker.addComputer( ShipDesign.BATTLE_COMPUTER, 1 );
      ShipDesign defender = new ShipDesign();
      defender.setJamming( 49 );
      
      double accuracy = BattleSimulation.getFinalAccuracy( 0.75, attacker, defender );
      
      assertEquals( 0.53, accuracy, 0.01 );
   }
   
   public void testBattleThree() throws BattleSimulationError
   {
      BattleSimulation battle = new BattleSimulation();

      battle.addNewStack( rabidDog, 50 );
      battle.addNewStack( armBB, 4 );
      
      //battle.addStatusListener( consoleStatusListener );
      
      battle.simulate();
      
      //
      // Test that we got the expected battle outcome
      //

      // Missile battles with small numbers of ships are impossible to predict accurately
      
//      // Only 3 ArmBBs left, both heavily damaged
//      assertEquals( 3, battle.getStack("ArmBB").shipCount );
//
//      // Damage won't always be exactly the same, but should be in the same region
//      assertTrue( "ArmBBs about 65% damaged", battle.getStack("ArmBB").getDamagePercent() > 60 );
//      assertTrue( "ArmBBs about 65% damaged", battle.getStack("ArmBB").getDamagePercent() < 70 );
//            
//      // Rabid Dog stack is wiped out
//      assertEquals( 0, battle.getStack("Rabid Dog").shipCount );
      
   }
   
   /**
    * Test the two dimensional distance calculator
    */
   public void testDistanceBetween()
   {
      BattleSimulation battle = new BattleSimulation();
      
      ShipStack s1 = new ShipStack(null,1);
      ShipStack s2 = new ShipStack(null,2);

      s1.setPos( 3, 4 );
      s2.setPos( 3, 4 );      
      assertEquals( 0, battle.distanceBetween(s1,s2) );
      
      s1.setPos( 3, 4 );
      s2.setPos( 4, 4 );      
      assertEquals( 1, battle.distanceBetween(s1,s2) );

      s1.setPos( 3, 4 );
      s2.setPos( 3, 5 );      
      assertEquals( 1, battle.distanceBetween(s1,s2) );
      
      s1.setPos( 3, 4 );
      s2.setPos( 4, 5 );      
      assertEquals( 1, battle.distanceBetween(s1,s2) );
      
      s1.setPos( 3, 4 );
      s2.setPos( 4, 6 );      
      assertEquals( 2, battle.distanceBetween(s1,s2) );
      
      s1.setPos( 3, 4 );
      s2.setPos( 7, 5 );      
      assertEquals( 4, battle.distanceBetween(s1,s2) );

      s1.setPos( 5, 3 );
      s2.setPos( 8, 4 );      
      assertEquals( 3, battle.distanceBetween(s1,s2) );

      s1.setPos( 0, 4 );
      s2.setPos( 5, 7 );      
      assertEquals( 5, battle.distanceBetween(s1,s2) );
   }
   
   public void testDisengageOne() throws BattleSimulationError
   {
      ShipStack cynic = new ShipStack( cynicDD, 1 );
      cynic.battleOrders = ShipStack.ORDERS_DISENGAGE;
      cynic.side = 1;
      
      ShipStack staz = new ShipStack( interceptor2, 3 );
      staz.side = 2;
      
      BattleSimulation battle = new BattleSimulation(2);
      battle.addStack( cynic );
      battle.addStack( staz );

      //battle.addStatusListener( consoleStatusListener );
      
      battle.simulate();
   }

   public void testAttractivenessCalculator() throws BattleSimulationError
   {
      ShipStack armBBstack = new ShipStack(armBB_2,1);
      ShipStack chaffStack = new ShipStack(chaff,1);
      ShipStack cccStack = new ShipStack(ccc,1);
      
      double armBB_to_armBB = BattleSimulation.getAttractiveness( armBBstack, 0, armBBstack);
      double chaff_to_ArmBB = BattleSimulation.getAttractiveness( armBBstack, 0, chaffStack);
      double armBB_to_CCC   = BattleSimulation.getAttractiveness( cccStack, 0, armBBstack);
      double chaff_to_CCC   = BattleSimulation.getAttractiveness( cccStack, 0, chaffStack);
      
      assertTrue( "Chaff more attractive than Arm BB to another Arm BB", chaff_to_ArmBB > armBB_to_armBB );
      assertTrue( "Chaff more attractive than Arm BB to beamer", chaff_to_CCC > armBB_to_CCC );
   }
   
   public void testBattleFour() throws Exception
   {
      // Based on a battle in the duel "Shorts"
      ShipDesign borderPatrol = new ShipDesign(ShipDesign.HULLTYPE_DESTROYER,"Border Patrol");
      borderPatrol.setOwner("Twits");
      borderPatrol.setArmour(200);
      borderPatrol.setMass(53);
      borderPatrol.setBattleSpeed(1.25);
      borderPatrol.setInitiative(3);
      borderPatrol.addWeapon( Weapon.X_RAY, 1 );
      borderPatrol.addWeapon( Weapon.X_RAY, 1 );
      
      ShipDesign yankeeBlossom = new ShipDesign(ShipDesign.HULLTYPE_DESTROYER,"Yankee Blossom");
      yankeeBlossom.setOwner("Bloomers");
      yankeeBlossom.setArmour(420);
      yankeeBlossom.setMass(238);
      yankeeBlossom.setBattleSpeed(0.75);
      yankeeBlossom.setInitiative(4);
      yankeeBlossom.setComputers( 1, 0, 0 );
      yankeeBlossom.addWeapon( Weapon.BETA, 1 );
      yankeeBlossom.addWeapon( Weapon.BETA, 1 );
      yankeeBlossom.addWeapon( Weapon.BETA, 1 );
      
      ShipStack stack1a = new ShipStack( borderPatrol, 3, 1 );
      ShipStack stack1b = new ShipStack( borderPatrol, 4, 1 );
      ShipStack stack2a = new ShipStack( yankeeBlossom, 6, 2 );
      
      BattleSimulation sim = new BattleSimulation();
      sim.addStack( stack1a );
      sim.addStack( stack1b );
      sim.addStack( stack2a );
      
      //sim.addStatusListener( consoleStatusListener );
      
      sim.simulate();
   }
}
