package stars.ahcgui;

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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import stars.ahc.*;

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
    String filesEvaluated = null;
    public final static String WARSHIP_INCLUDE = "Include warships";
    public final static String UTILITY_INCLUDE = "Include utility ships";
    public final static String SCOUT_INCLUDE = "Include scouts";
    public final static String OWNED_INCLUDE = "Include my fleets";


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
                filesEvaluated += fFile[i].getCanonicalPath() + " ";
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
        ret.setLayout( new BorderLayout() );
        ret.add( new JLabel( "<html><body>It is important to realize that since these analyses are based on the f-files, that the totals shown are only for those fleets you can actually see.  Also note that these are pages experimental and have not gone through vigorous testing.<br><br>" +
                "I would appreciate and feedback, positive or negative, at jchoyt@users.sourceforge.net.<br><br>" +
                "Evaluating " + filesEvaluated.toString() + "</body></html>" ), BorderLayout.CENTER );
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
        ret.setLayout( new BoxLayout( ret, BoxLayout.Y_AXIS ) );
        /*
         *  add table of largest warfleets
         *  get a copy of the fleets array and pass it to the table model.  The table model will take care of all sorting.
         */
        ArrayList thisTabFleets = new ArrayList();
        thisTabFleets.addAll( fleets );
        WarFleetTableModel model = new WarFleetTableModel( thisTabFleets );
        TableSorter sorter = new TableSorter( model );
        JTable table = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable( table );
        JScrollPane scrollpane = new JScrollPane( table );
        ret.add( scrollpane );

        /* add options */
        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
        JCheckBox warshipCheckbox = new JCheckBox( WARSHIP_INCLUDE, model.getIncludeWarship());
        warshipCheckbox.addItemListener(model);
        options.add(warshipCheckbox);
        JCheckBox utilCheckbox = new JCheckBox( UTILITY_INCLUDE, model.getIncludeUtil());
        utilCheckbox.addItemListener(model);
        options.add(utilCheckbox);
        JCheckBox scoutCheckbox = new JCheckBox( SCOUT_INCLUDE, model.getIncludeScout());
        scoutCheckbox.addItemListener(model);
        options.add(scoutCheckbox);
        ret.add(options);

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
         *  add table of largest bomber fleets
         *  get a copy of the fleets array and pass it to the table model.  The table model will take care of all sorting.
         */
        ArrayList thisTabFleets = new ArrayList();
        thisTabFleets.addAll( fleets );
        BomberFleetTableModel model = new BomberFleetTableModel( thisTabFleets );
        TableSorter sorter = new TableSorter( model );
        JTable table = new JTable( sorter );
        sorter.addMouseListenerToHeaderInTable( table );
        JScrollPane scrollpane = new JScrollPane( table );
        ret.add( scrollpane );
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
        table.getColumnModel().getColumn( 8 ).setPreferredWidth( 40 );
        JScrollPane scrollpane = new JScrollPane( table );
        ret.add( scrollpane, BorderLayout.CENTER );
        return ret;
    }
}

