/*
 * Created on Oct 9, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc;

/**
 * Interface that defines attributes common to both planets and fleets.
 * 
 * @author Steve Leach
 */
public interface MapObject
{
   public String getName();
   public String getOwner();
   public int getX();
   public int getY();
}
