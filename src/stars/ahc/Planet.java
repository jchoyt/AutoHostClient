/*
 * Created on Oct 7, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc;

import java.awt.Point;

/**
 * @author Steve Leach
 *
 */
public class Planet
{
   public int x, y;
   public String name;
   
   public Point getPosition()
   {
      return new Point( x, y );
   }
}
