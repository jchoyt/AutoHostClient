/*
 *  AbstractOptionPane.java - Abstract option pane
 *  Copyright (C) 1998, 1999, 2000, 2001, 2002 Slava Pestov
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package stars.ahcgui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.border.EmptyBorder;

/**
 *  This provides a base for an option pane that lays out components in a
 *  vertical fashion.
 *
 *@author     Slava Pestov
 *@created    January 15, 2003
 */
public abstract class AbstractOptionPane extends JPanel implements ActionListener
{
    /**
     *  The layout manager.
     */
    GridBagConstraints c = new GridBagConstraints();
    GridBagLayout gridbag = new GridBagLayout();
    static String SAVE = "save";
    static String CANCEL = "refresh";
    /**
     *  Has the option pane been initialized?
     */
    boolean initialized;


    public void actionPerformed(ActionEvent evt)
    {
        if (evt.getActionCommand().equals(SAVE))
        {
            save();
        }
        else if (evt.getActionCommand().equals(CANCEL))
        {
            _refresh();
        }
    }

    /**
     *  Creates a new option pane.
     */
    public AbstractOptionPane()
    {
        setLayout( gridbag );
        c.gridx = 0;
        c.gridy = 0;
    }

    /**
     *  Adds a feature to the Buttons attribute of the GamePanelFactory class
     *
     *@param  game  The feature to be added to the Buttons attribute
     */
    protected void addButtons( )
    {
        /*
         *  Add Save Button
         */
        JButton b1 = new JButton("Save");
        b1.setMnemonic(KeyEvent.VK_S);
        b1.setActionCommand(SAVE);
        b1.addActionListener(this);
        c.gridx=0;
        c.gridwidth=1;
        c.gridy++;
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.NONE;
        gridbag.setConstraints( b1, c );
        add( b1 );
        /*
         *  Add Cancel button
         */
        b1 = new JButton( "Reset Values" );
        b1.setMnemonic(KeyEvent.VK_R);
        b1.setActionCommand(CANCEL);
        b1.addActionListener(this);
        c.gridx++;
        c.anchor=GridBagConstraints.CENTER;
        gridbag.setConstraints( b1, c );
        add( b1 );
    }
    /**
     *  Returns the component that should be displayed for this option pane.
     *  Because this class extends Component, it simply returns "this".
     *
     *@return    The component value
     */
    public Component getComponent()
    {
        return this;
    }



    /**
     *  Adds a feature to the BlankSpace attribute of the OptionPanelFactory
     *  object
     */
    public void addBlankSpace()
    {
        Component blank = Box.createVerticalStrut( 20 );
        c.gridx = 0;
        c.gridy++;
        gridbag.setConstraints( blank, c );
        add( blank );
    }


    /**
     *  Adds a labeled component to the option pane. Components are added in a
     *  vertical fashion, one per row. The label is displayed to the left of the
     *  component.
     *
     *@param  label  The label
     *@param  comp   The component
     */
    public void addComponent( String label, Component comp )
    {
        JLabel l = new JLabel( label );
        l.setBorder( new EmptyBorder( 0, 0, 0, 12 ) );
        addComponent( l, comp, GridBagConstraints.BOTH );
    }


    /**
     *  Adds a labeled component to the option pane. Components are added in a
     *  vertical fashion, one per row. The label is displayed to the left of the
     *  component.
     *
     *@param  label  The label
     *@param  comp   The component
     *@param  fill   Fill parameter to GridBagConstraints for the right
     *      component
     */
    public void addComponent( String label, Component comp, int fill )
    {
        JLabel l = new JLabel( label );
        l.setBorder( new EmptyBorder( 0, 0, 0, 12 ) );
        addComponent( l, comp, fill );
    }


    /**
     *  Adds a labeled component to the option pane. Components are added in a
     *  vertical fashion, one per row. The label is displayed to the left of the
     *  component.
     *
     *@param  comp1  The label
     *@param  comp2  The component
     *@since         jEdit 4.1pre3
     */
    public void addComponent( Component comp1, Component comp2 )
    {
        addComponent( comp1, comp2, GridBagConstraints.BOTH );
    }


    /**
     *  Adds a labeled component to the option pane. Components are added in a
     *  vertical fashion, one per row. The label is displayed to the left of the
     *  component.
     *
     *@param  comp1  The label
     *@param  comp2  The component
     *@param  fill   Fill parameter to GridBagConstraints for the right
     *      component
     *@since         jEdit 4.1pre3
     */
    public void addComponent( Component comp1, Component comp2, int fill )
    {
        c.gridy++;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.weightx = 0.0f;
        c.insets = new Insets( 1, 1, 1, 0 );
        c.fill = GridBagConstraints.WEST;

        gridbag.setConstraints( comp1, c );
        add( comp1 );

        c.fill = fill;
        c.gridx = 1;
        c.weightx = 1.0f;
        c.insets = new Insets( 1, 0, 1, 1 );
        gridbag.setConstraints( comp2, c );
        add( comp2 );
    }


    /**
     *  Adds a component to the option pane. Components are added in a vertical
     *  fashion, one per row.
     *
     *@param  comp  The component
     */
    public void addComponent( Component comp )
    {
        c.gridy++;
        c.gridheight = 1;
        //c.gridwidth = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 0;
        c.weightx = 0.0f;
        c.insets = new Insets( 1, 1, 1, 0 );
        c.fill = GridBagConstraints.CENTER;

        gridbag.setConstraints( comp, c );
        add( comp );
    }



    /**
     *  Description of the Method
     */
    public void init()
    {
        if ( !initialized )
        {
            initialized = true;
            _init();
        }
    }


    /**
     *  Description of the Method
     */
    public void save()
    {
        if ( initialized )
        {
            _save();
        }
    }

    public void refresh()
    {
        if ( initialized )
        {
            _refresh();
        }
    }



    /**
     *  This method should create the option pane's GUI.
     */
    protected abstract void _init();


    /**
     *  Description of the Method
     */
    protected abstract void _refresh();


    /**
     *  Called when the options dialog's "ok" button is clicked. This should
     *  save any properties being edited in this option pane.
     */
    protected abstract void _save();
}

