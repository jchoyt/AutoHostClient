package stars.ahcgui;

import javax.swing.table.*;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
/**
 * In a chain of data manipulators some behaviour is common. TableMap
 * provides most of this behavour and can be subclassed by filters
 * that only need to override a handful of specific methods. TableMap
 * implements TableModel by routing all requests to its model, and
 * TableModelListener by routing all events to its listeners. Inserting
 * a TableMap which has not been subclassed into a chain of table filters
 * should have no effect.
 *
 * @version 1.4 12/17/97
 * @author Philip Milne */
public class TableMap extends AbstractTableModel
         implements TableModelListener
{
    protected TableModel model;


    /**
     *  Gets the model attribute of the TableMap object
     *
     *@return    The model value
     */
    public TableModel getModel()
    {
        return model;
    }


    /**
     *  Sets the model attribute of the TableMap object
     *
     *@param  model  The new model value
     */
    public void setModel( TableModel model )
    {
        this.model = model;
        model.addTableModelListener( this );
    }
    // By default, implement TableModel by forwarding all messages
    // to the model.
    /**
     *  Gets the valueAt attribute of the TableMap object
     *
     *@param  aRow     Description of the Parameter
     *@param  aColumn  Description of the Parameter
     *@return          The valueAt value
     */
    public Object getValueAt( int aRow, int aColumn )
    {
        return model.getValueAt( aRow, aColumn );
    }


    /**
     *  Sets the valueAt attribute of the TableMap object
     *
     *@param  aValue   The new valueAt value
     *@param  aRow     The new valueAt value
     *@param  aColumn  The new valueAt value
     */
    public void setValueAt( Object aValue, int aRow, int aColumn )
    {
        model.setValueAt( aValue, aRow, aColumn );
    }


    /**
     *  Gets the rowCount attribute of the TableMap object
     *
     *@return    The rowCount value
     */
    public int getRowCount()
    {
        return ( model == null ) ? 0 : model.getRowCount();
    }


    /**
     *  Gets the columnCount attribute of the TableMap object
     *
     *@return    The columnCount value
     */
    public int getColumnCount()
    {
        return ( model == null ) ? 0 : model.getColumnCount();
    }


    /**
     *  Gets the columnName attribute of the TableMap object
     *
     *@param  aColumn  Description of the Parameter
     *@return          The columnName value
     */
    public String getColumnName( int aColumn )
    {
        return model.getColumnName( aColumn );
    }


    /**
     *  Gets the columnClass attribute of the TableMap object
     *
     *@param  aColumn  Description of the Parameter
     *@return          The columnClass value
     */
    public Class getColumnClass( int aColumn )
    {
        return model.getColumnClass( aColumn );
    }


    /**
     *  Gets the cellEditable attribute of the TableMap object
     *
     *@param  row     Description of the Parameter
     *@param  column  Description of the Parameter
     *@return         The cellEditable value
     */
    public boolean isCellEditable( int row, int column )
    {
        return model.isCellEditable( row, column );
    }
//
// Implementation of the TableModelListener interface,
//
    // By default forward all events to all the listeners.
    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    public void tableChanged( TableModelEvent e )
    {
        fireTableChanged( e );
    }
}

