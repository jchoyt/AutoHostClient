package stars.ahcgui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;
import javax.swing.JCheckBox;
import stars.ahc.*;
/**
 *  Gives a table of fleetname, location, cloak level, ship count, Speed,
 *  warship count, utility count, bomber count for the n biggest warfleets
 *
 *@author     jchoyt
 *@created    February 16, 2004
 */
public class BomberFleetTableModel extends FleetTabTableModel
{

    protected BomberFleetComparator comparator = new BomberFleetComparator();


    /**
     *  Constructor for the ShipCountTableModel object
     *
     *@param  fleets  Description of the Parameter
     */
    public BomberFleetTableModel( List fleets )
    {
        this.fleets = fleets;
        Collections.sort( fleets, comparator );
    }

    /**
     *  Gets the rowCount attribute of the EvalTableModel object
     *
     *@return    The rowCount value
     */
    public int getRowCount()
    {
        int otherwise = super.getRowCount();
        for( int i=0; i<otherwise; i++)
        {
            Fleet fleet = (Fleet) fleets.get(i);
            if(fleet.getIntValue(Fleet.BOMBER)==0)
            {
                otherwise = i-1;
                break;
            }
        }
        return otherwise;
    }
}

