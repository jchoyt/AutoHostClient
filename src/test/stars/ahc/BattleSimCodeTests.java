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

import java.io.File;

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
public class BattleSimCodeTests extends TestCase
{
   private ShipDesign blueColloidalBC, redColloidalCC, armBB, chaff;
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
      blueColloidalBC = new ShipDesign( ShipDesign.HULLTYPE_BATTLECRUISER, "Colloidal BCC" );
      blueColloidalBC.setOwner( "Blue" );
      blueColloidalBC.setMass( 174 );
      blueColloidalBC.setArmour( 1000 );
      blueColloidalBC.setShields( 240 );
      blueColloidalBC.setBattleSpeed( 2.25 );
      blueColloidalBC.setInitiative( 5 );
      blueColloidalBC.addWeapon( Weapon.COLLOIDAL_PHASER, 3 );
      blueColloidalBC.addWeapon( Weapon.PULSED_SAPPER, 3 );
      blueColloidalBC.addWeapon( Weapon.COLLOIDAL_PHASER, 3 );
      
      redColloidalCC = new ShipDesign( ShipDesign.HULLTYPE_CRUISER, "Colloidal CC" );
      redColloidalCC.setOwner( "Red" );
      redColloidalCC.setMass( 130 );
      redColloidalCC.setArmour( 700 );
      redColloidalCC.setShields( 560 );
      redColloidalCC.setRegenShields(true);
      redColloidalCC.setBattleSpeed( 1.25 );
      redColloidalCC.setInitiative( 5 );
      redColloidalCC.setBoraniumCost( 35 );
      redColloidalCC.setResourceCost( 100 );
      redColloidalCC.addWeapon( Weapon.COLLOIDAL_PHASER, 2 );
      redColloidalCC.addWeapon( Weapon.PULSED_SAPPER, 2 );
      redColloidalCC.addWeapon( Weapon.COLLOIDAL_PHASER, 2 );
      
      // BB, 4 x TDG, 4 x syncro sappers (wings), 16 x arms, 4 x BSC, 3 x jammer 20, no armour
      armBB = new ShipDesign( ShipDesign.HULLTYPE_BATTLESHIP, "ArmBB" );
      armBB.setOwner( "Green" );
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
      blueColloidalBC = null;
      redColloidalCC = null;
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
      
      battle.addNewStack( blueColloidalBC, 12 );
      battle.addNewStack( redColloidalCC, 14 );
      
      //battle.addStatusListener( consoleStatusListener );
      
      battle.simulate();
      
      //
      // Test that we got the expected battle outcome
      //
      
      // CC stack is wiped out
      assertEquals( 0, battle.getStack("Colloidal CC").getShipCount() );
      
      // BCC stack suffers no ship losses
      assertEquals( 12, battle.getStack("Colloidal BCC").getShipCount() );
      
      // Damage won't always be exactly the same, but should be in the same region
      assertTrue( "BCCs about 50% damaged", battle.getStack("Colloidal BCC").getDamagePercent() > 40 );
      assertTrue( "BCCs about 50% damaged", battle.getStack("Colloidal BCC").getDamagePercent() < 60 );
   }

   /**
    */
   public void testBattleTwo() throws Exception
   {
      BattleSimulation battle = new BattleSimulation();
      
      ShipStack bccStack = battle.addNewStack( blueColloidalBC, 7 );
      ShipStack ccStack = battle.addNewStack( redColloidalCC, 14 );

      //battle.addStatusListener( consoleStatusListener );
      battle.showStacksFull();
      
      battle.simulate();
      
      //
      // Test that we got the expected battle outcome
      //
      
      // CCC stack is undamaged
      assertEquals( 14, ccStack.getShipCount() );
      assertTrue( "CCC stack undamaged or lightly damaged", ccStack.getDamagePercent() < 5 );
      
      // Rabid Dog stack is wiped out
      assertEquals( 0, bccStack.getShipCount() );
      
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

      battle.addNewStack( blueColloidalBC, 50 );
      battle.addNewStack( armBB, 4 );
      
      battle.randomSeed = 12345;
      
      //battle.addStatusListener( consoleStatusListener );
      
      battle.simulate();
      
      //
      // Test that we got the expected battle outcome
      //

      // Missile battles with small numbers of ships are impossible to predict accurately
      // However, we are using a fixed random seed
     
      // Only 3 ArmBBs left, both heavily damaged
      assertEquals( 2, battle.getStack("ArmBB").getShipCount() );

      // Damage won't always be exactly the same, but should be in the same region
      assertTrue( "ArmBBs about 60% damaged", battle.getStack("ArmBB").getDamagePercent() > 70 );
      assertTrue( "ArmBBs about 60% damaged", battle.getStack("ArmBB").getDamagePercent() < 75 );
            
      // Rabid Dog stack is wiped out
      assertEquals( 0, battle.getStack("Colloidal BCC").getShipCount() );
      
   }
   
   /**
    * Test the two dimensional distance calculator
    */
   public void testDistanceBetween()
   {
      BattleSimulation battle = new BattleSimulation();
      
      ShipDesign design = new ShipDesign();
      
      ShipStack s1 = new ShipStack(design,1);
      ShipStack s2 = new ShipStack(design,2);

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
      ShipStack cynic = new ShipStack( cynicDD, 1, 1 );
      cynic.battleOrders = ShipStack.ORDERS_DISENGAGE;
      
      ShipStack staz = new ShipStack( interceptor2, 1, 2 );
      staz.owner = "others";
      
      BattleSimulation battle = new BattleSimulation(2);
      battle.addStack( cynic );
      battle.addStack( staz );

      //battle.addStatusListener( consoleStatusListener );
      
      battle.simulate();
      
      assertEquals( 1, cynic.getShipCount() );
      assertEquals( 1, staz.getShipCount() );
   }

   public void testAttractivenessCalculator() throws BattleSimulationError
   {
      ShipStack armBBstack = new ShipStack(armBB_2,1);
      ShipStack chaffStack = new ShipStack(chaff,1);
      ShipStack cccStack = new ShipStack(redColloidalCC,1);
      
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
      
      sim.randomSeed = 12345;
      
      //sim.addStatusListener( consoleStatusListener );
      
      sim.simulate();
      
      assertEquals( 1, stack1a.getShipCount() );
      assertEquals( 3, stack1b.getShipCount() );
      assertEquals( 6, stack2a.getShipCount() );
   }
   
   public void testMultipleSimulations() throws BattleSimulationError
   {
      int klingons = 1;
      int borg = 2;
      ShipStack armBBstack = new ShipStack( armBB_2, 30, klingons );
      armBBstack.owner = "Klingons";
      ShipStack chaffStack = new ShipStack( chaff, 500, borg );
      chaffStack.owner = "Borg";
      ShipStack cccStack = new ShipStack( armBB_2, 20, borg );
      cccStack.owner = "Borg";
      
      BattleSimulation sim = new BattleSimulation();
      sim.addStack( armBBstack );
      sim.addStack( chaffStack );
      sim.addStack( cccStack );
      
      //System.out.println( armBBstack.getStackAsString() );
      
      //sim.addStatusListener( consoleStatusListener );
      
      int iterations = 100;
      
      long start = System.currentTimeMillis();
      
      sim.simulateRepeatedly( iterations );
      
      long duration = System.currentTimeMillis() - start;
      
      assertTrue( "100 simulations took > 0.5s", duration < 500 );
      
//      System.out.println( "" + iterations + " simulations in " + (duration/1000.0) + "s" );
//      
//      System.out.println( armBBstack.design.getName() + "... " + armBBstack.getCumulativeResults() );
//      System.out.println( chaffStack.design.getName() + "... " + chaffStack.getCumulativeResults() );
//      System.out.println( cccStack.design.getName() + "... " + cccStack.getCumulativeResults() );
   }

   /**
    * Test that shield damage is applied correctly when ships are killed
    * <p>
    * We are looking for: Shields = StartingShields / StartingShips * RemainingShips - ShieldDamage
    * as shown to be the case using BattleSim.
    */
   public void testShieldDamageCalculationOrder() throws Exception
   {
      ShipDesign epsilonBB = new ShipDesign( ShipDesign.HULLTYPE_BATTLESHIP, "Epsilon BB" );
      epsilonBB.setOwner( "Human" );
      epsilonBB.setArmour( 2000 );
      epsilonBB.setShields( 0 );
      epsilonBB.setBattleSpeed( 1 );
      epsilonBB.setComputers( 0, 0, 7 );
      epsilonBB.addWeapon( Weapon.EPSILON, 1 );
      
      ShipDesign shieldScout = new ShipDesign( ShipDesign.HULLTYPE_SCOUT, "Shield Scout" );
      shieldScout.setOwner( "Alien" );
      shieldScout.setArmour( 20 );
      shieldScout.setShields( 500 );
      shieldScout.setBattleSpeed( 1 );
      
      BattleSimulation sim = new BattleSimulation();
      sim.addNewStack( epsilonBB, 1 );
      sim.addNewStack( shieldScout, 10 );
      
      sim.randomSeed = 543634;
      
      sim.saveTo( System.getProperty("java.io.tmpdir") + File.separator +  "test.sim" );
      
      //sim.addStatusListener( consoleStatusListener );
      
      sim.simulate();
   }
   
   public void testLoadSimFromFile() throws Exception
   {
      String simFile = System.getProperty("java.io.tmpdir") + File.separator +  "test.sim";
      BattleSimulation sim = new BattleSimulation( simFile );
      
      //sim.addStatusListener( consoleStatusListener );
      
      sim.simulate();
   }
   
   public void testStarbaseCombat() throws Exception
   {
      ShipDesign hip = new ShipDesign();
      hip.setName("Hip To Be Square");
      hip.setOwner("Rush'n");
      hip.addComputer( ShipDesign.BATTLE_COMPUTER, 8 );
      hip.setBattleSpeed( 0 );
      hip.setInitiative(22);
      hip.setArmour( 1300 );
      hip.setShields( 1120 );
      hip.addWeapon( Weapon.BETA, 16 );
      hip.addWeapon( Weapon.BETA, 16 );
      hip.addWeapon( Weapon.LASER, 16 );
      hip.addWeapon( Weapon.LASER, 16 );
      
      
      ShipDesign ddx = new ShipDesign();
      ddx.setName("Destroyer X");
      ddx.setOwner("Drom");
      ddx.setBattleSpeed( 1.5 );
      ddx.setInitiative( 3 );
      ddx.setArmour( 200 );
      ddx.setShields( 0 );
      ddx.addWeapon( Weapon.X_RAY, 1 );
      ddx.addWeapon( Weapon.X_RAY, 1 );
      ddx.setRegenShields( true );
      ddx.setBoraniumCost(13);
      ddx.setResourceCost(74);
      
      ShipDesign grn = new ShipDesign();
      grn.setName( "Grn Destroyer" );
      grn.setOwner("Buccaneer");
      grn.setBattleSpeed( 1.5 );
      grn.setInitiative( 3 );
      grn.setArmour( 200 );
      grn.setShields( 0 );
      grn.addWeapon( Weapon.YAKIMORA, 1 );
      grn.addWeapon( Weapon.YAKIMORA, 1 );
      grn.addWeapon( Weapon.YAKIMORA, 1 );
      grn.setRegenShields( true );
      grn.setBoraniumCost(21);
      grn.setResourceCost(65);
      
      BattleSimulation sim = new BattleSimulation();
      ShipStack hipStack = sim.addNewStack( hip, 1, 1 );
      sim.addNewStack( ddx, 9, 2 );
      sim.addNewStack( ddx, 3, 2 );
      ShipStack grn1 = sim.addNewStack( grn, 8, 2 );
      ShipStack grn2 = sim.addNewStack( grn, 10, 2 );
      
      //sim.addStatusListener( consoleStatusListener );
      
      sim.simulate();
      
      assertEquals( "Starbase is dead", hipStack.getShipCount(), 0 );
      assertTrue( "Most Green Destroyers still alive", grn1.getShipCount()+grn2.getShipCount() > 15 );
   }
   
   public void testHulls() throws Exception
   {
      ShipDesign ff = new ShipDesign();
      ff.setName("FF");
      ff.setBattleSpeed(2);
      ff.setInitiative(4);
      ff.setArmour( 45 );
      ff.setShields( 1000 );
      ff.addWeapon( Weapon.AMP, 3 );
      
      ShipDesign cc = new ShipDesign();
      cc.setName("CC");
      cc.setBattleSpeed(2);
      cc.setInitiative(5);
      cc.setArmour( 700 );
      cc.setShields( 2000 );
      cc.addWeapon( Weapon.AMP, 2 );
      cc.addWeapon( Weapon.AMP, 2 );
      cc.addWeapon( Weapon.AMP, 2 );

      ShipDesign bb = new ShipDesign();
      bb.setName("BB");
      bb.setBattleSpeed(2);
      bb.setInitiative(10);
      bb.setArmour( 2000 );
      bb.setShields( 4000 );
      bb.setCapacitors(6);
//      bb.setDeflectors(6);
      bb.addWeapon( Weapon.AMP, 6 );
      bb.addWeapon( Weapon.AMP, 6 );
      
      BattleSimulation sim = new BattleSimulation();
      
      ShipStack ffs = sim.addNewStack( ff, 77 );
      ffs.owner = "A";
      ffs.side = 1;
      
//      ShipStack ccs = sim.addNewStack( cc, 34 );
//      ccs.owner = "B";
//      ccs.side = 2;
      
      ShipStack bbs = sim.addNewStack( bb, 16 );
      bbs.owner = "B";
      bbs.side = 2;
      
//      sim.addStatusListener( consoleStatusListener );

      sim.saveTo( System.getProperty("user.home") + File.separator +  "bb_cc_ff.sim" );
      
      sim.simulate();
      
   }
   
   public void testCapsAndDeflectors()
   {
      ShipDesign design1 = new ShipDesign();
      design1.setCapacitors(3*3);
      design1.setDeflectors(0);
      
      ShipDesign design2 = new ShipDesign();
      design2.setCapacitors(3*3);
      design2.setDeflectors(4*3);      
      
      double damageMultiplier1 = BattleSimulation.getDeflectorMultiplier(design2) * BattleSimulation.getCapacitorMultiplier(design1);
      
      assertAlmostEqual( damageMultiplier1, 0.66, 0.05 );

      double damageMultiplier2 = BattleSimulation.getDeflectorMultiplier(design1) * BattleSimulation.getCapacitorMultiplier(design2);
      
      assertAlmostEqual( damageMultiplier2, 2.35, 0.05 );
      
      double ratio = damageMultiplier2/damageMultiplier1;
      
      assertAlmostEqual( ratio, 3.57, 0.05 );
   }

   private void assertAlmostEqual(double value, double expected, double maxError)
   {
      assertTrue( Math.abs(value - expected) < maxError );
   }
   
   public void testEnergyDampener()
   {
      BattleSimulation sim = new BattleSimulation();

      ShipDesign design1 = new ShipDesign();
      design1.setBattleSpeed( 1.75 );
      sim.addNewStack( design1, 1 );
      
      sim.setDampenerPresent(false);
      
      // Base speed is 1.75
      // Normally this gives 2 moves in round 4.

      int moves = sim.movesInRound( design1.getSpeed4(), 4 );
      
      assertEquals( 2, moves );
      
      sim.setDampenerPresent(true);
      
      // With EDs present, speed is reduced to 0.75
      // This gives 1 move in round 4
      
      moves = sim.movesInRound( design1.getSpeed4(), 4 );
      
      assertEquals( 1, moves );
   }
   
   // Test remove weapon slot
   public void testRemoveWeaponSlot()
   {
      ShipDesign design = new ShipDesign();
      design.addWeapon( Weapon.ALPHA, 2 );
      design.addWeapon( Weapon.BETA, 2 );
      design.addWeapon( Weapon.DELTA, 2 );
      
      assertEquals( 3, design.getWeaponSlots() );
      assertEquals( "Alpha Torpedo", design.getWeaponName(0) );
      assertEquals( "Beta Torpedo", design.getWeaponName(1) );
      assertEquals( "Delta Torpedo", design.getWeaponName(2) );
      
      design.removeWeaponSlot( 1 );
      assertEquals( 2, design.getWeaponSlots() );
      assertEquals( "Alpha Torpedo", design.getWeaponName(0) );
      assertEquals( "Delta Torpedo", design.getWeaponName(1) );

      design.removeWeaponSlot( 0 );
      assertEquals( 1, design.getWeaponSlots() );
      assertEquals( "Delta Torpedo", design.getWeaponName(0) );
      
      design.removeWeaponSlot( 0 );
      assertEquals( 0, design.getWeaponSlots() );

      design.addWeapon( Weapon.ALPHA, 2 );
      design.addWeapon( Weapon.BETA, 2 );
      design.addWeapon( Weapon.DELTA, 2 );
   
      assertEquals( 3, design.getWeaponSlots() );
      assertEquals( "Alpha Torpedo", design.getWeaponName(0) );
      assertEquals( "Beta Torpedo", design.getWeaponName(1) );
      assertEquals( "Delta Torpedo", design.getWeaponName(2) );

      design.removeWeaponSlot( 2 );
      assertEquals( 2, design.getWeaponSlots() );
      assertEquals( "Alpha Torpedo", design.getWeaponName(0) );
      assertEquals( "Beta Torpedo", design.getWeaponName(1) );
   }
   
   public void testRegenShields() throws BattleSimulationError
   {
      ShipDesign attacker = new ShipDesign();
      attacker.setOwner( "Me" );
      attacker.setShields( 100 );
      attacker.setArmour( 1 );
      attacker.setBattleSpeed( 2.5 );
      attacker.addWeapon( Weapon.X_RAY, 1 );
      
      ShipDesign target = new ShipDesign();
      target.setOwner( "You" );
      target.setShields( 200 );
      target.setArmour( 20 );
      target.setRegenShields( true );
      target.setBattleSpeed( 1.0 );
      
      BattleSimulation sim = new BattleSimulation();
      ShipStack attackStack = sim.addNewStack( attacker, 1, 1 );
      ShipStack targetStack = sim.addNewStack( target, 1, 2 );
      
      sim.simulate();
      
      // Single x-ray laser against 200 dp of regen shields; the shields will regen
      // more damage than the laser can inflict.
      
      assertEquals( targetStack.getShipCount(), 1 );
      assertEquals( targetStack.shields, 200 );
      assertEquals( targetStack.getDamagePercent(), 0 );
      
      // But with only 20dp of shields, the shields drop and then the
      // target is killed.
      
      target.setShields( 20 );
      
      sim.simulate();
      
      assertEquals( targetStack.getShipCount(), 0 );
   }
   
   public void testBadSimulation()
   {
      // Try a simulation with only 1 ship stack
      BattleSimulation sim = new BattleSimulation();
      sim.addNewStack( armBB, 1, 1 );
      
      try
      {
         sim.simulate();
         
         assertTrue( "Should not get here because the sim should throw an error", false );
      }
      catch (BattleSimulationError e)
      {
         assertEquals( "There must be at least 2 ship stacks in a battle", e.getMessage() );
      }
   }
   
   public void testInitialDamage()
   {
      ShipDesign design = new ShipDesign();
      design.setArmour( 1024 );
      
      ShipStack stack = new ShipStack( design, 6 );      
      stack.setInitialDamagePercent( 25 );
      
      assertEquals( 1536, stack.getInitialDamageTotal() );
      
      stack = new ShipStack( design, 1 );
      stack.setInitialDamagePercent( 50 );
      
      assertEquals( 512, stack.getInitialDamageTotal() );
   }
}
