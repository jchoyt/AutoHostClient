/*
 * Created on 14-Jul-2005
 *
 * Copyright (c) 2004, Steve Leach
 */
package test.stars.ahc;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BattleSimTests
{

   public static Test suite()
   {
      TestSuite suite = new TestSuite("Test for test.stars.ahc");
      //$JUnit-BEGIN$
      suite.addTestSuite(BattleSimCodeTests.class);
      suite.addTestSuite(BattleSimRealBattles.class);
      //$JUnit-END$
      return suite;
   }

}
