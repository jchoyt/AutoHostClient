/*
 * Created on 14-Jul-2005
 *
 * Copyright (c) 2004, Steve Leach
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
 * Test simulations of real battles from real games to make sure the simulator
 * gives similar results to the game battle engine.
 *  
 * @author Steve Leach
 */
public class BattleSimRealBattles extends TestCase
{
   private BattleSimulationListener consoleStatusListener = new BattleSimulationListener() 
   {
      int counter = 0; 
      
      public void handleNotification(BattleSimulationNotification notification)
      {
         if (notification.round == 0)
         {
            System.out.println( counter + ": " + notification.message );
         }
         else
         {
            System.out.println( counter + ": " + "Round " + notification.round + " : " + notification.message );
         }
         
         counter++;
      }
   };
   
   private ShipDesign realityStazBadBoy;
   private ShipDesign realityStazMainLine;
   private ShipDesign realityStazMainLine2;
   private ShipDesign realityStazB52;
   private ShipDesign realityHaniBB1;
   private ShipDesign realityHaniLittleSucker;
   private ShipDesign realityHaniSmallOne;
   private ShipDesign realityHaniLittleHelper;
   private ShipDesign realityStazMasterOfNone;
   
   {
      realityStazBadBoy = new ShipDesign( ShipDesign.HULLTYPE_NUBIAN, "Bad Boy" );
      realityStazBadBoy.setOwner("Staz");
      realityStazBadBoy.setMass( 867 );
      realityStazBadBoy.setArmour( 5195 );
      realityStazBadBoy.setShields( 2625, true );
      realityStazBadBoy.setBattleSpeed( 1.25 );
      realityStazBadBoy.setInitiative( 29 );
      realityStazBadBoy.setJamming( 71 );
      realityStazBadBoy.setCost( 399, 846 );
      realityStazBadBoy.addComputer( ShipDesign.BATTLE_NEXUS, 9 );
      realityStazBadBoy.addWeapon( Weapon.ARM, 3 );
      realityStazBadBoy.addWeapon( Weapon.ARM, 3 );
      realityStazBadBoy.addWeapon( Weapon.ARM, 3 );
      realityStazBadBoy.addWeapon( Weapon.ARM, 3 );
      realityStazBadBoy.addWeapon( Weapon.ARM, 3 );
      realityStazBadBoy.addWeapon( Weapon.ARM, 3 );
      
      realityStazMainLine = new ShipDesign( ShipDesign.HULLTYPE_NUBIAN, "MainLine" );
      realityStazMainLine.setOwner("Staz");
      realityStazMainLine.setMass( 235 );
      realityStazMainLine.setArmour( 5390 );
      realityStazMainLine.setShields( 1050, true );
      realityStazMainLine.setBattleSpeed( 1.75 );
      realityStazMainLine.setInitiative( 2 );
      realityStazMainLine.setJamming( 27 );
      realityStazMainLine.setCapacitors( 9 );
      realityStazMainLine.setDeflectors( 9 );
      realityStazMainLine.setCost( 324, 663 );
      realityStazMainLine.addWeapon( Weapon.MEGA_DISRUPTOR, 3 );
      realityStazMainLine.addWeapon( Weapon.MEGA_DISRUPTOR, 3 );
      realityStazMainLine.addWeapon( Weapon.MEGA_DISRUPTOR, 3 );
      realityStazMainLine.addWeapon( Weapon.MEGA_DISRUPTOR, 3 );

      realityStazMainLine2 = new ShipDesign( ShipDesign.HULLTYPE_NUBIAN, "MainLine 2" );
      realityStazMainLine2.setOwner("Staz");
      realityStazMainLine2.setMass( 178 );
      realityStazMainLine2.setArmour( 5000 );
      realityStazMainLine2.setShields( 2100, true );
      realityStazMainLine2.setBattleSpeed( 2 );
      realityStazMainLine2.setInitiative( 2 );
      realityStazMainLine2.setJamming( 66 );
      realityStazMainLine2.setCapacitors( 9 );
      realityStazMainLine2.setDeflectors( 12 );
      realityStazMainLine2.setCost( 216, 600 );
      realityStazMainLine2.addWeapon( Weapon.AMP, 3 );
      realityStazMainLine2.addWeapon( Weapon.AMP, 3 );
      realityStazMainLine2.addWeapon( Weapon.AMP, 3 );
      
      realityStazB52 = new ShipDesign( ShipDesign.HULLTYPE_B52, "B52" );
      realityStazB52.setOwner("Staz");
      realityStazB52.setMass( 1005 );
      realityStazB52.setArmour( 580 );
      realityStazB52.setShields( 350, true );
      realityStazB52.setBattleSpeed( 2 );
      realityStazB52.setInitiative( 0 );
      realityStazB52.setJamming( 10 );
      realityStazB52.setCost( 224, 363 );

      realityStazMasterOfNone = new ShipDesign( ShipDesign.HULLTYPE_GALLEON, "Master of None" );
      realityStazMasterOfNone.setOwner("Staz");
      realityStazMasterOfNone.setMass( 292 );
      realityStazMasterOfNone.setArmour( 1160 );
      realityStazMasterOfNone.setShields( 700, true );
      realityStazMasterOfNone.setBattleSpeed( 2.5 );
      realityStazMasterOfNone.setInitiative( 4 );
      realityStazMasterOfNone.setJamming( 19 );
      realityStazMasterOfNone.setCost( 101, 379 );
      realityStazMasterOfNone.addWeapon( Weapon.MEGA_DISRUPTOR, 3 );
      realityStazMasterOfNone.addWeapon( Weapon.SYNCRO_SAPPER, 3 );

      realityHaniBB1 = new ShipDesign( ShipDesign.HULLTYPE_BATTLESHIP, "BB1" );
      realityHaniBB1.setOwner("Hani");
      realityHaniBB1.setMass( 1012 );
      realityHaniBB1.setArmour( 6500 );
      realityHaniBB1.setShields( 1960, true );
      realityHaniBB1.setBattleSpeed( 0.5 );
      realityHaniBB1.setInitiative( 16 );
      realityHaniBB1.addComputer( ShipDesign.SUPER_COMPUTER, 3 );
      realityHaniBB1.setJamming( 66 );
      realityHaniBB1.addWeapon( Weapon.JUGG, 6 );
      realityHaniBB1.addWeapon( Weapon.JUGG, 6 );
      realityHaniBB1.addWeapon( Weapon.JUGG, 4 );
      realityHaniBB1.addWeapon( Weapon.GATTLING_GUN, 2 );
      realityHaniBB1.addWeapon( Weapon.GATTLING_GUN, 2 );

      realityHaniLittleSucker = new ShipDesign( ShipDesign.HULLTYPE_NUBIAN, "Little Sucker" );
      realityHaniLittleSucker.setOwner("Hani");
      realityHaniLittleSucker.setMass( 232 );
      realityHaniLittleSucker.setArmour( 5000 );
      realityHaniLittleSucker.setShields( 2520, true );
      realityHaniLittleSucker.setBattleSpeed( 2.5 );
      realityHaniLittleSucker.setInitiative( 8 );
      realityHaniLittleSucker.addComputer( ShipDesign.SUPER_COMPUTER, 3 );
      realityHaniLittleSucker.setDeflectors( 3 );
      realityHaniLittleSucker.setCapacitors( 3 );
      realityHaniLittleSucker.setJamming( 66 );
      realityHaniLittleSucker.addWeapon( Weapon.HEAVY_BLASTER, 3 );
      realityHaniLittleSucker.addWeapon( Weapon.HEAVY_BLASTER, 3 );
      realityHaniLittleSucker.addWeapon( Weapon.HEAVY_BLASTER, 3 );
      realityHaniLittleSucker.addWeapon( Weapon.PHASED_SAPPER, 3 );
      realityHaniLittleSucker.addWeapon( Weapon.PHASED_SAPPER, 3 );

      realityHaniSmallOne = new ShipDesign( ShipDesign.HULLTYPE_NUBIAN, "Small One" );
      realityHaniSmallOne.setOwner("Hani");
      realityHaniSmallOne.setMass( 229 );
      realityHaniSmallOne.setArmour( 5000 );
      realityHaniSmallOne.setShields( 4200, true );
      realityHaniSmallOne.setBattleSpeed( 2.5 );
      realityHaniSmallOne.setInitiative( 2 );
      realityHaniSmallOne.setDeflectors( 9 );
      realityHaniSmallOne.setCapacitors( 9 );
      realityHaniSmallOne.setJamming( 66 );
      realityHaniSmallOne.addWeapon( Weapon.AMP, 3 );
      realityHaniSmallOne.addWeapon( Weapon.AMP, 3 );

      realityHaniLittleHelper = new ShipDesign( ShipDesign.HULLTYPE_SCOUT, "Little Helper" );
      realityHaniLittleHelper.setOwner("Hani");
      realityHaniLittleHelper.setMass( 13 );
      realityHaniLittleHelper.setArmour( 20 );
      realityHaniLittleHelper.setShields( 0, true );
      realityHaniLittleHelper.setBattleSpeed( 0.75 );
      realityHaniLittleHelper.setInitiative( 1 );
      realityHaniLittleHelper.addWeapon( Weapon.X_RAY, 1 );
   }
      
   /**
    *  Game: Reality Sux
    *  Turn: 2503
    * 
    *  A Hani missile battleship is attacked by a Stazanoid fleet with one beam nubian
    *  and one missile nubian.  The BB is killed, and the beam nub lightly damaged. 
    */
   public void testRealityStazHani2503() throws BattleSimulationError
   {
      BattleSimulation sim = new BattleSimulation();
      
      ShipStack haniStack = sim.addNewStack( realityHaniBB1, 1, 1 );
      ShipStack badBoyStack = sim.addNewStack( realityStazBadBoy, 1, 2 );
      ShipStack mainLineStack = sim.addNewStack( realityStazMainLine, 1, 2 );
      
      //sim.addStatusListener( consoleStatusListener );
      
      sim.randomSeed = 12345;
      
      sim.simulate();
      
      assertEquals( 2, sim.getFinalRound() );               // Battle finishes on round 2
      assertEquals( 0, haniStack.getShipCount() );          // Hani BB is destroyed
      assertEquals( 1, badBoyStack.getShipCount() );        // Staz Bad Boy survives...
      assertEquals( 0, badBoyStack.getDamagePercent() );    // ...and is undamaged
      assertEquals( 1, mainLineStack.getShipCount() );      // Staz MainLine survives...
      assertTrue( mainLineStack.getDamagePercent() > 0 );   // ...but is damaged...
      assertTrue( mainLineStack.getDamagePercent() < 20 );  // ...a bit
   }

   /**
    *  Game: Reality Sux
    *  Turn: 2506
    * 
    *  A pair of Hani beam nubs attack a damaged Stazanoid beam nubian.
    *  The Hani fleet is destroyed, and the Staz nub left heavily damaged. 
    */
   public void testRealityStazHani2506() throws BattleSimulationError
   {
      BattleSimulation sim = new BattleSimulation();
      ShipStack haniStack = sim.addNewStack( realityHaniLittleSucker, 2, 1 );
      ShipStack mainLineStack = sim.addNewStack( realityStazMainLine, 1, 2 );
      mainLineStack.setInitialDamagePercent(15);
      
      //sim.addStatusListener( consoleStatusListener );
      
      sim.randomSeed = 12345;
      
      sim.simulate();
      
      assertEquals( 5, sim.getFinalRound() );               // Battle finishes on round 5
      assertEquals( 0, haniStack.getShipCount() );          // Hani Nub is destroyed
      assertEquals( 1, mainLineStack.getShipCount() );      // Staz MainLine survives...
      assertTrue( mainLineStack.getDamagePercent() > 60 );   // ...but is heavily damaged
   }

   /**
    *  Game: Reality Sux
    *  Turn: 2507
    * 
    *  A large (though mainly one-sided) fleet battle.
    *  The Staz fleet wipes out all defensive forces.
    *  
    *  In the real battle the Hani had a starbase as well, but that didn't really
    *  take part in the battle and died in round 6.
    */
   public void testRealityStazHani2507() throws BattleSimulationError
   {
      BattleSimulation sim = new BattleSimulation();
      
      final int HANI = 1;
      final int STAZ = 2;
      
      sim.addNewStack( realityStazMainLine,      28, STAZ, 26 );
      sim.addNewStack( realityStazMainLine,       4, STAZ, 26 );
      sim.addNewStack( realityStazB52,           48, STAZ, 57, ShipStack.ORDERS_DISENGAGE );
      sim.addNewStack( realityStazMainLine2,     45, STAZ,  1 );
      sim.addNewStack( realityStazBadBoy,         7, STAZ,  0 );
      sim.addNewStack( realityStazMasterOfNone,  15, STAZ,  0 );
      
      sim.addNewStack( realityHaniLittleSucker,   4, HANI, 11 );
      sim.addNewStack( realityHaniLittleSucker,   8, HANI, 0 );
      sim.addNewStack( realityHaniLittleSucker,   5, HANI, 0 );
      sim.addNewStack( realityHaniSmallOne,       2, HANI, 0 );
      sim.addNewStack( realityHaniSmallOne,       1, HANI, 0 );
      sim.addNewStack( realityHaniSmallOne,       3, HANI, 0 );
      sim.addNewStack( realityHaniSmallOne,      30, HANI, 0 );
      sim.addNewStack( realityHaniLittleHelper, 100, HANI, 0 );
      
      //sim.addStatusListener( consoleStatusListener );
      
      sim.randomSeed = 12345;
      
      sim.simulate();
      
      assertEquals( 4, sim.getFinalRound() );
      
      // Confirm that all the Hani ships are wiped out
      for (int n = 0; n < sim.getStackCount(); n++)
      {
         ShipStack stack = sim.getStack(n);
         
         if (stack.side == HANI)
         {
            assertEquals( 0, stack.getShipCount() );        
         }  
      }
      
   }

}
