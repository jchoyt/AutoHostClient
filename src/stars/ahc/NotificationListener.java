/*
 * Created on Oct 12, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc;

/**
 * Classes that listen for notifications  
 * 
 * @author Steve Leach
 */
public interface NotificationListener
{
   public static final int SEV_STATUS = 1;
   public static final int SEV_WARNING = 2;
   public static final int SEV_ERROR = 3;
   public static final int SEV_CRITICAL = 4;
   
   public void receiveNotification( Object source, int severity, String message );
}
