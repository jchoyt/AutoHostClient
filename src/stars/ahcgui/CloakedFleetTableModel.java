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
public class CloakedFleetTableModel extends FleetTabTableModel
{

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
}

