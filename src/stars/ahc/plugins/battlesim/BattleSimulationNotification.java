/*
 * Created on Nov 6, 2004
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
package stars.ahc.plugins.battlesim;

import java.awt.Point;

/**
 * Details of an event (movement or firing) within a battle simulation 
 * 
 * @author Steve Leach
 */
public class BattleSimulationNotification
{
   public static final int TYPE_OTHER = 0;
   public static final int TYPE_MOVE = 1;
   public static final int TYPE_FIRE = 2;
   
   public String message = null;
   public int round = 0;
   public int eventType = 0;
   public ShipStack activeStack = null;
   public ShipStack targetStack = null;
   public Point movedFrom = null;
   public boolean finished = false;
}
