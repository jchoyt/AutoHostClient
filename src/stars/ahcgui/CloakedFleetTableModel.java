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
public class CloakedFleetTableModel extends AbstractTableModel
{

    protected List fleets = new ArrayList();
    public static int cloakLimit = 75;
    public boolean showOwn = true;


    /**
     *  Constructor for the CloakedFleetTableModel object
     *
     *@param  allFleets  Description of the Parameter
     */
    public CloakedFleetTableModel( List allFleets )
    {
        Fleet temp;
        for ( int i = 0; i < allFleets.size(); i++ )
        {
            temp = ( Fleet ) allFleets.get( i );
            if ( temp.getIntValue( Fleet.CLOAK ) >= cloakLimit )
            {
                fleets.add( temp );
            }
        }
    }


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
                return "Ship Count";
            case 4:
                return "Speed (warp)";
            case 5:
                return "Warship Count";
            case 6:
                return "Utility Ship Count";
            case 7:
                return "Bomber Count";
            default:
                throw new RuntimeException( "Only 8 columns in the list" );
        }
    }


    /**
     *  Gets the columnClass attribute of the CloakedFleetTableModel object
     *
     *@param  col  Description of the Parameter
     *@return      The columnClass value
     */
    public Class getColumnClass( int col )
    {
        if ( col > 1 )
        {
            return Integer.class;
        }
        else
        {
            return String.class;
        }
    }


    /**
     *  Sets the cloakLimit attribute of the CloakedFleetTableModel object
     *
     *@param  cloakLimit  The new cloakLimit value
     */
    public void setCloakLimit( int cloakLimit )
    {
        if ( cloakLimit < 0 || cloakLimit > 98 )
        {
            throw new RuntimeException( "Cloak value must be between 0 and 98, inclusive" );
        }
        this.cloakLimit = cloakLimit;
        fireTableDataChanged();
    }


    /**
     *  Gets the cloakLimit attribute of the CloakedFleetTableModel object
     *
     *@return    The cloakLimit value
     */
    public int getCloakLimit()
    {
        return cloakLimit;
    }


    /**
     *  Gets the rowCount attribute of the EvalTableModel object
     *
     *@return    The rowCount value
     */
    public int getRowCount()
    {
        return fleets.size();
    }


    /**
     *  Gets the columnCount attribute of the EvalTableModel object
     *
     *@return    The columnCount value
     */
    public int getColumnCount()
    {
        return 8;
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

