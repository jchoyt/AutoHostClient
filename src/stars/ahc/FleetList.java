/*
 * Created on Oct 20, 2004
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steve Leach
 */
public class FleetList
{
   private Game game;
   
   private static final int MAX_YEARS = 300;
   private static final int BASE_YEAR = 2400;
   
   private Map importedReports = new HashMap();
   
   FleetData[] fleetDataArray = new FleetData[MAX_YEARS];
   
   public FleetList( Game game )
   {
      this.game = game;
   }
   
   public void loadFleetReport( File reportFile, int year ) throws ReportLoaderException
   {
      if (importedReports.get(reportFile.getName()) != null)
      {
         // we have already loaded this report file, so return
         return;
      }
      
      try
      {
         BufferedReader reader = new BufferedReader(new FileReader(reportFile));
         
         String titles = reader.readLine();
         
         String line;
         
         while ((line = reader.readLine()) != null)
         {
            String[] tokens = line.split("\t");

            addFleetData( year, tokens );
         }
         
         importedReports.put( reportFile.getName(), reportFile.getName() );
         
         reader.close();
      }
      catch (FileNotFoundException e)
      {
         throw new ReportLoaderException( "File not found: " + reportFile.getName(), e, reportFile.getName() );
      }
      catch (IOException e)
      {
         throw new ReportLoaderException( "Error reading file: " + reportFile.getName(), e, reportFile.getName() );
      }            
   }

   /**
    */
   private void addFleetData(int year, String[] tokens)
   {
      FleetData fleetData = getFleetData( year, true );
      
      fleetData.addFleet( tokens );
   }
   
   private FleetData getFleetData( int year, boolean create )
   {
      int index = year - BASE_YEAR;
      
      if (fleetDataArray[index] == null)
      {
         fleetDataArray[index] = new FleetData( year );
      }
   
      return fleetDataArray[index];
   }
   
   public Fleet getFleet( int index )
   {
      return getFleet( game.getYear(), index );
   }
   
   public Fleet getFleet( int year, int index )
   {
      FleetData fleetData = getFleetData( year, true );
      
      return new Fleet( fleetData.getValues( index ) );
   }

   /**
    * @return
    */
   public int getFleetCount(int year)
   {
      FleetData fleetData = getFleetData( year, true );
      return fleetData.getFleetCount();
   }
}

/**
 * Stores data about all fleets for a particular year
 *  
 * @author Steve Leach
 */
class FleetData
{
   int year;
   
   ArrayList fleets = new ArrayList();
   
   public FleetData( int year )
   {
      this.year = year;
   }

   /**
    * @return
    */
   public int getFleetCount()
   {
      return fleets.size();
   }

   /**
    */
   public String[] getValues(int index)
   {
      return (String[])fleets.get(index);
   }

   /**
    */
   public void addFleet(String[] tokens)
   {
      fleets.add( tokens );
   }
}
