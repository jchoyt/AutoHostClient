/*
 * Created on Oct 14, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.analyzer;

import java.util.Properties;

import stars.ahc.Game;
import stars.ahcgui.pluginmanager.PlugIn;

/**
 * @author Steve Leach
 *
 */
public interface AnalyzerReport extends PlugIn
{
   public String run( Game game, Properties properties ) throws AnalyzerReportError;
}
