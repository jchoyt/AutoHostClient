/*
 * Created on Oct 14, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.analyzer;

/**
 * @author Steve Leach
 *
 */
public class AnalyzerReportError extends Exception
{
   public AnalyzerReportError( String message )
   {
      super( message );
   }

   public AnalyzerReportError( String message, Throwable t )
   {
      super( message, t );
   }
}
