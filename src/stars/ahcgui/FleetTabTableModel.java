package stars.ahcgui;

import java.util.*;
import stars.ahc.*;
import javax.swing.table.AbstractTableModel;
/**
 *  Gives a table of fleetname, location, cloak level, ship count, Speed,
 *  warship count, utility count, bomber count
 *
 *@author     jchoyt
 *@created    February 16, 2004
 */
public abstract class FleetTabTableModel extends AbstractTableModel
{

    protected List fleets = new ArrayList();
    protected int numberToShow = 20;


    /**
     *  Gets the columnName attribute of the CloakedFleetTableModel object
     *
     *@param  col  Description of the Parameter
     *@return      The columnName value
     */
    public String getColumnName( int col )
    {
        switch ( col )
        {
            case 0:
                return "Fleet Name";
            case 1:
                return "Location";
            case 2:
                return "Cloak";
            case 3:
                return "Total Ships";
            case 4:
                return "Speed (warp)";
            case 5:
                return "Warships";
            case 6:
                return "Utility Ships";
            case 7:
                return "Bombers";
            case 8:
                return "Scouts";
            default:
                throw new RuntimeException( "Only 9 columns in the list" );
        }
    }


    /**
     *  Gets the columnClass attribute of the ShipCountTableModel object
     *
     *@param  col  Description of the Parameter
     *@return      The columnClass value
     */
    public Class getColumnClass( int col )
    {
        if ( col == 0 )
        {
            return String.class;
        }
        else
        {
            return Integer.class;
        }
    }


    /**
     *  Gets the rowCount attribute of the EvalTableModel object
     *
     *@return    The rowCount value
     */
    public int getRowCount()
    {
        if ( numberToShow > fleets.size() )
        {
            return fleets.size();
        }
        else
        {
            return numberToShow;
        }
    }


    /**
     *  Gets the columnCount attribute of the EvalTableModel object
     *
     *@return    The columnCount value
     */
    public int getColumnCount()
    {
        return 9;
    }



    /**
     *  Description of the Method
     *
     *@param  columnNo  Description of the Parameter
     *@param  fleet     Description of the Parameter
     *@return           Description of the Return Value
     */
    private Object valueMapping( int columnNo, Fleet fleet )
    {
        switch ( columnNo )
        {
            case 0:
                return fleet.getValue( Fleet.FLEET_NAME );
            case 1:
                return fleet.getNiceLocation();
            case 2:
                return new Integer( fleet.getIntValue( Fleet.CLOAK ) );
            case 3:
                return new Integer( fleet.getIntValue( Fleet.SHIP_COUNT ) );
            case 4:
                return new Integer( fleet.getIntValue( Fleet.WARP ) );
            case 5:
                return new Integer( fleet.getIntValue( Fleet.WARSHIP ) );
            case 6:
                return new Integer( fleet.getIntValue( Fleet.UTILITY ) );
            case 7:
                return new Integer( fleet.getIntValue( Fleet.BOMBER ) );
            case 8:
                return new Integer( fleet.getIntValue( Fleet.SCOUT ) );
            default:
                throw new RuntimeException( "Only 8 columns in the list" );
        }
    }



    /**
     *  Gets the valueAt attribute of the EvalTableModel object
     *
     *@param  row     Description of the Parameter
     *@param  column  Description of the Parameter
     *@return         The valueAt value
     */
    public Object getValueAt( int row, int column )
    {
        Fleet fleet = ( Fleet ) fleets.get( row );
        return valueMapping( column, fleet );
    }
}

