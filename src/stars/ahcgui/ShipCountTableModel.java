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
public class ShipCountTableModel extends AbstractTableModel
{

    protected List fleets = new ArrayList();
    public static int cloakLimit = 75;
    public boolean showOwn = true;


    /**
     *  Constructor for the ShipCountTableModel object
     */
    public ShipCountTableModel() { }


    /**
     *  Gets the columnName attribute of the ShipCountTableModel object
     *
     *@param  col  Description of the Parameter
     *@return      The columnName value
     */
    public String getColumnName( int col )
    {
        switch ( col )
        {
            case 0:
                return "Fleet Owner";
            case 1:
                return "Warship";
            case 2:
                return "Utility";
            case 3:
                return "Bomber";
            case 4:
                return "Scout";
            case 5:
                return "Unarmed";
            default:
                throw new RuntimeException( "Only 6 columns in the list" );
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
        return owners == null ? 0 : owners.length;
    }


    /**
     *  Gets the columnCount attribute of the EvalTableModel object
     *
     *@return    The columnCount value
     */
    public int getColumnCount()
    {
        return 6;
    }


    protected String[] owners = null;
    protected HashMap values = new HashMap();


    /**
     *  Adds a feature to the Row attribute of the ShipCountTableModel object
     *
     *@param  owner     The feature to be added to the Row attribute
     *@param  warships  The feature to be added to the Row attribute
     *@param  utility   The feature to be added to the Row attribute
     *@param  bomber    The feature to be added to the Row attribute
     *@param  scout     The feature to be added to the Row attribute
     *@param  unarmed   The feature to be added to the Row attribute
     */
    public void addRow( String owner, int warships, int utility, int bomber, int scout, int unarmed )
    {
        /*
         *  add the ship count data
         */
        Integer[] rowData = new Integer[5];
        rowData[0] = new Integer( warships );
        rowData[1] = new Integer( utility );
        rowData[2] = new Integer( bomber );
        rowData[3] = new Integer( scout );
        rowData[4] = new Integer( unarmed );
        values.put( owner, rowData );
        /*
         *  add the owner to the list
         */
        if ( owners == null )
        {
            owners = new String[1];
            owners[0] = owner;
        }
        else
        {
            String[] new_owners = new String[owners.length + 1];
            new_owners[new_owners.length - 1] = owner;
            System.arraycopy( owners, 0, new_owners, 0, owners.length );
            owners = new_owners;
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
        if ( column == 0 )
        {
            return owners[row];
        }
        else
        {
            Integer[] rowValues = ( Integer[] ) values.get( owners[row] );
            return rowValues[column - 1];
        }
    }
}

