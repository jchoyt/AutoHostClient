/*
 * Created on Oct 31, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.battlesim;

/**
 * @author Steve Leach
 *
 * @deprecated - use BattleSimulationListener instead
 */
public interface StatusListener
{
   public void battleStatusUpdate( int round, String message );
}
