package stars.ahc;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.filechooser.FileFilter;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import stars.ahcgui.*;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    February 16, 2004
 */
public class Eval
{
    List fleets = new ArrayList();
    Map fleetsByOwner = new HashMap();


    /**
     *  Description of the Method
     *
     *@param  fFile  Description of the Parameter
     */
    public void loadFleets( File[] fFile )
    {
        for ( int i = 0; i < fFile.length; i++ )
        {
            try
            {
                BufferedReader in = new BufferedReader( new FileReader( fFile[i] ) );
                String line = in.readLine();//skip the first line - it has the titles in it
                while ( ( line = in.readLine() ) != null )
                {
                    line = line.trim();
                    Fleet fleet = new Fleet( line );
                    if ( !fleets.contains( fleet ) )
                    {
                        fleets.add( fleet );
                    }
                }
            }
            catch ( IOException e )
            {
                throw new RuntimeException( e );
            }
        }
    }


    /**
     *  Gets the filesToEvaluate attribute of the Eval object
     *
     *@param  game  Description of the Parameter
     *@return       The filesToEvaluate value
     */
    public File[] getFilesToEvaluate( Game game )
    {
        JFileChooser chooser = new JFileChooser( game.getDirectory() );
        chooser.addChoosableFileFilter(
            new FileFilter()
            {
                public String getDescription()
                {
                    return "F files";
                }


                public boolean accept( File f )
                {
                    if ( f.getName().matches( ".*\\.[fF]\\d{1,2}$" ) || f.isDirectory() )
                    {//ends with .f## or is a directory

                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            } );
        chooser.setMultiSelectionEnabled( true );
        int returnVal = chooser.showDialog( null, "Pick the f files to use for the analysis" );
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            return chooser.getSelectedFiles();
        }
        return null;
    }


    /**
     *  Description of the Method
     *
     *@param  game  Description of the Parameter
     */
    public void evaluate( Game game )
    {
        /*
         *  Load up info
         */
        File[] files = getFilesToEvaluate( game );
        loadFleets( files );
        createFleetsByOwner();
        //System.out.println("Total number of unique fleets = " + fleets.size());
        /*
         *  create the dialog box
         */
        JDialog dialog = new JDialog( AhcGui.mainFrame, "Evaluation for " + game.getLongName() );
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
        /*
         *  add the tabs
         */
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab( "Cloaked Fleets", createCloakTab( game ) );
        tabbedPane.addTab( "Ship Count", createShipCountTab( game ) );
        tabbedPane.addTab( "War Fleets", createWarFleetTab( game ) );
        tabbedPane.addTab( "Bomber Fleets", createBomberFleetTab( game ) );
        contentPane.add( tabbedPane );
        /*
         *  Add comments
         */
        contentPane.add( createCommentPane() );
        /*
         *  pack and show
         */
        dialog.pack();
        dialog.show();
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public JPanel createCommentPane()
    {
        JPanel ret = new JPanel();
        ret.setLayout(new BoxLayout(ret, BoxLayout.Y_AXIS));
        ret.add( new JLabel( "It is important to realize that since these analyses are based on the f-files " ) );
        ret.add( new JLabel( "that the totals show are only those files you can actually SEE." ) );
        ret.add( new JLabel( "Also note that these are experimental and have not gone through vigorous testing.  I would appreciate" ) );
        ret.add( new JLabel( "and feedback, positive or negative, at jchoyt@users.sourceforge.net." ) );
        return ret;
    }


    /**
     *  Description of the Method
     *
     *@param  game  Description of the Parameter
     *@return       Description of the Return Value
     */
    protected JPanel createShipCountTab( Game game )
    {
        JPanel ret = new JPanel();
        ret.setLayout( new BorderLayout() );
        /*
         *  get the counts
         */
        ShipCountTableModel model = new ShipCountTableModel();
        List fleetList;
        Iterator iter = fleetsByOwner.keySet().iterator();
        while ( iter.hasNext() )
        {
            fleetList = ( List ) fleetsByOwner.get( iter.next() );
            Fleet fleet = null;
            int utilityCount = 0;
            int scoutCount = 0;
            int unarmentCount = 0;
            int warshipCount = 0;
            int bomberCount = 0;
            for ( int i = 0; i < fleetList.size(); i++ )
            {
                fleet = ( Fleet ) fleetList.get( i );
                utilityCount += fleet.getIntValue( Fleet.UTILITY );
                scoutCount += fleet.getIntValue( Fleet.SCOUT );
                unarmentCount += fleet.getIntValue( Fleet.UNARMED );
                warshipCount += fleet.getIntValue( Fleet.WARSHIP );
                bomberCount += fleet.getIntValue( Fleet.BOMBER );
            }
            model.addRow( fleet.getOwner(), warshipCount, utilityCount, bomberCount, scoutCount, unarmentCount );
        }
        /*
         *  set up the table
         */
        TableSorter sorter = new TableSorter( model );
        JTable table = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable( table );
        /*
         *  table.getColumnModel().getColumn( 0 ).setPreferredWidth( 100 );
         *  table.getColumnModel().getColumn( 1 ).setPreferredWidth( 60 );
         *  table.getColumnModel().getColumn( 2 ).setPreferredWidth( 20 );
         *  table.getColumnModel().getColumn( 3 ).setPreferredWidth( 40 );
         *  table.getColumnModel().getColumn( 4 ).setPreferredWidth( 40 );
         *  table.getColumnModel().getColumn( 5 ).setPreferredWidth( 40 );
         */
        JScrollPane scrollpane = new JScrollPane( table );
        ret.add( scrollpane, BorderLayout.CENTER );
        return ret;
    }


    /**
     *  Constructor for the createFleetsByOwner object
     */
    public void createFleetsByOwner()
    {
        List rightList;
        Fleet fleet;
        for ( int i = 0; i < fleets.size(); i++ )
        {
            fleet = ( Fleet ) fleets.get( i );
            rightList = ( List ) fleetsByOwner.get( fleet.getOwner() );
            if ( rightList == null )
            {
                rightList = new ArrayList();
                fleetsByOwner.put( fleet.getOwner(), rightList );
            }
            rightList.add( fleet );
        }
    }


    /**
     *  Description of the Method
     *
     *@param  game  Description of the Parameter
     *@return       Description of the Return Value
     */
    protected JPanel createWarFleetTab( Game game )
    {
        JPanel ret = new JPanel();
        ret.setLayout( new BorderLayout() );
        /*
         *  add some stuff here
         */
        return ret;
    }


    /**
     *  Description of the Method
     *
     *@param  game  Description of the Parameter
     *@return       Description of the Return Value
     */
    protected JPanel createBomberFleetTab( Game game )
    {
        JPanel ret = new JPanel();
        ret.setLayout( new BorderLayout() );
        /*
         *  add some stuff here
         */
        return ret;
    }



    /**
     *  Creates a pane to show the Cloaked fleets. Note that loadFleets() must
     *  be run before this is called.
     *
     *@param  game  Description of the Parameter
     *@return       Description of the Return Value
     */
    protected JPanel createCloakTab( Game game )
    {
        JPanel ret = new JPanel();
        ret.setLayout( new BorderLayout() );
        CloakedFleetTableModel model = new CloakedFleetTableModel( fleets );
        TableSorter sorter = new TableSorter( model );
        JTable table = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable( table );
        table.getColumnModel().getColumn( 0 ).setPreferredWidth( 100 );
        table.getColumnModel().getColumn( 1 ).setPreferredWidth( 60 );
        table.getColumnModel().getColumn( 2 ).setPreferredWidth( 20 );
        table.getColumnModel().getColumn( 3 ).setPreferredWidth( 40 );
        table.getColumnModel().getColumn( 4 ).setPreferredWidth( 40 );
        table.getColumnModel().getColumn( 5 ).setPreferredWidth( 40 );
        table.getColumnModel().getColumn( 6 ).setPreferredWidth( 40 );
        table.getColumnModel().getColumn( 7 ).setPreferredWidth( 40 );
        JScrollPane scrollpane = new JScrollPane( table );
        ret.add( scrollpane, BorderLayout.CENTER );
        return ret;
    }
}

