/*
 * Created on Nov 9, 2004
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
 * Encapsulates details of a game update.
 * <p>
 * The object is immutable (cannot be changed after construction) and so a single GameUpdateNotification
 * object can safely be sent to multiple listeners. 
 * 
 * @author Steve Leach
 */
public class GameUpdateNotification
{
   private Game game = null;
   private Object updatedObject = null;
   private String propertyName = null;
   private Object newValue = null;
   private Object oldValue = null;
   private long timeStamp = 0;

   /**
    * game and updatedObject cannot be null 
    */
   public GameUpdateNotification( Game game, Object updatedObject, String propertyName, Object oldValue, Object newValue )
   {
      if (game == null) throw new NullPointerException("Game object cannot be null");
      if (updatedObject == null) throw new NullPointerException("Game object cannot be null");
      
      this.game = game;
      this.updatedObject = updatedObject;
      this.propertyName = propertyName;
      this.oldValue = oldValue;
      this.newValue = newValue;
      this.timeStamp = System.currentTimeMillis();
   }

   public Object getNewValue()
   {
      return newValue;
   }
   public Object getOldValue()
   {
      return oldValue;
   }
   public String getPropertyName()
   {
      return propertyName;
   }
   public long getTimeStamp()
   {
      return timeStamp;
   }
   public Object getUpdatedObject()
   {
      return updatedObject;
   }
}
