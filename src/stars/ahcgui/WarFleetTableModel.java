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
public class WarFleetTableModel extends FleetTabTableModel implements ItemListener
{

    protected boolean includeWarship = true;
    protected boolean includeUtil = false;
    protected boolean includeScout = true;
    protected WarFleetComparator comparator = new WarFleetComparator();


    /**
     *  Constructor for the ShipCountTableModel object
     *
     *@param  fleets  Description of the Parameter
     */
    public WarFleetTableModel( List fleets )
    {
        this.fleets = fleets;
        resort();
    }


    /**
     *  Constructor for the resort object
     */
    public void resort()
    {
        comparator.setIncludeScout( includeScout );
        comparator.setIncludeUtil( includeUtil );
        comparator.setIncludeWarship( includeWarship );
        Collections.sort( fleets, comparator );
        fireTableDataChanged();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    public void itemStateChanged( ItemEvent e )
    {
        JCheckBox checkBox = ( JCheckBox ) e.getItem();
        String label = checkBox.getText();
        if ( label.equals( Eval.WARSHIP_INCLUDE ) )
        {
            includeWarship = convertState( e );
        }
        else if ( label.equals( Eval.UTILITY_INCLUDE ) )
        {
            includeUtil = convertState( e );
        }
        else if ( label.equals( Eval.SCOUT_INCLUDE ) )
        {
            includeScout = convertState( e );
        }
        else
        {
            return;
        }
        resort();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     *@return    Description of the Return Value
     */
    public boolean convertState( ItemEvent e )
    {
        if ( e.getStateChange() == ItemEvent.SELECTED )
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     *  Sets the includeWarship attribute of the WarFleetComparator object
     *
     *@param  includeWarship  The new includeWarship value
     */
    public void setIncludeWarship( boolean includeWarship )
    {
        this.includeWarship = includeWarship;
    }


    /**
     *  Sets the includeUtil attribute of the WarFleetComparator object
     *
     *@param  includeUtil  The new includeUtil value
     */
    public void setIncludeUtil( boolean includeUtil )
    {
        this.includeUtil = includeUtil;
    }


    /**
     *  Sets the includeScout attribute of the WarFleetComparator object
     *
     *@param  includeScout  The new includeScout value
     */
    public void setIncludeScout( boolean includeScout )
    {
        this.includeScout = includeScout;
    }


    /**
     *  Gets the includeWarship attribute of the WarFleetTableModel object
     *
     *@return    The includeWarship value
     */
    public boolean getIncludeWarship()
    {
        return includeWarship;
    }


    /**
     *  Gets the includeUtil attribute of the WarFleetTableModel object
     *
     *@return    The includeUtil value
     */
    public boolean getIncludeUtil()
    {
        return includeUtil;
    }


    /**
     *  Gets the includeScout attribute of the WarFleetTableModel object
     *
     *@return    The includeScout value
     */
    public boolean getIncludeScout()
    {
        return includeScout;
    }
}

