/*
 * Created on Oct 9, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc;

/**
 * Exception thrown when loading Stars! game report files 
 * 
 * @author Steve Leach
 */
public class ReportLoaderException extends Exception
{
   private String fileName = null;
   
   public ReportLoaderException( String message, Throwable cause )
   {
      super( message, cause );
   }

   public ReportLoaderException( String message, Throwable cause, String fileName )
   {
      super( message, cause );
      this.fileName = fileName;
   }

   public ReportLoaderException( String message, String fileName )
   {
      super( message );
      this.fileName = fileName;
   }
   
   public String getFileName()
   {
      return fileName;
   }
}
