package stars.ahcgui;

import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *  LazyPanel is an abstract base class that provides functionality to defer
 *  populating a Panel object until it is actually viewed. This is extremely
 *  useful when using CardLayout and tab panel views because it allows the
 *  construction of the subviews to be done on a pay-as-you-go basis instead of
 *  absorbing all the cost of construction up front.
 *
 *@author     Mark Roulo, JavaWorld
 *@created    2003
 */
public abstract class LazyPanel extends JPanel
{
    // We want to call the lazyConstructor only once.
    private boolean lazyConstructorCalled = false;

    // We don't want to call lazyConstructor until
    // the components are actually visible.
    private boolean isConstructorFinished = false;


    /**
     *  Make a LazyPanel.
     */
    protected LazyPanel()
    {
        isConstructorFinished = true;
    }


    /**
     *  Overwritten class to take advantage of lazy constructor
     *
     *@param  g  Description of the Parameter
     */
    public void paint( Graphics g )
    {
        callLazyConstructor();

        super.paint( g );
    }


    /**
     *  Overwritten class to take advantage of lazy constructor
     *
     *@param  g  Description of the Parameter
     */
    public void paintAll( Graphics g )
    {
        callLazyConstructor();

        super.paintAll( g );
    }


    /**
     *  Overwritten class to take advantage of lazy constructor
     *
     *@param  g  Description of the Parameter
     */
    public void paintComponents( Graphics g )
    {
        callLazyConstructor();

        super.paintComponents( g );
    }


    /**
     *  Overwritten class to take advantage of lazy constructor
     */
    public void repaint()
    {
        callLazyConstructor();

        super.repaint();
    }


    /**
     *  Overwritten class to take advantage of lazy constructor
     *
     *@param  l  Description of the Parameter
     */
    public void repaint( long l )
    {
        callLazyConstructor();

        super.repaint( l );
    }


    /**
     *  Overwritten class to take advantage of lazy constructor
     *
     *@param  i1  Description of the Parameter
     *@param  i2  Description of the Parameter
     *@param  i3  Description of the Parameter
     *@param  i4  Description of the Parameter
     */
    public void repaint( int i1, int i2, int i3, int i4 )
    {
        callLazyConstructor();

        super.repaint( i1, i2, i3, i4 );
    }


    /**
     *  Overwritten class to take advantage of lazy constructor
     *
     *@param  l   Description of the Parameter
     *@param  i1  Description of the Parameter
     *@param  i2  Description of the Parameter
     *@param  i3  Description of the Parameter
     *@param  i4  Description of the Parameter
     */
    public void repaint( long l, int i1, int i2, int i3, int i4 )
    {
        callLazyConstructor();

        super.repaint( l, i1, i2, i3, i4 );
    }


    /**
     *  Overwritten class to take advantage of lazy constructor
     *
     *@param  g  Description of the Parameter
     */
    public void update( Graphics g )
    {
        callLazyConstructor();

        super.update( g );
    }


    /**
     *  Force the lazyConstructor() method implemented in the child class to be
     *  called. If this method is called more than once on a given object, all
     *  calls but the first do nothing.
     */
    public final synchronized void callLazyConstructor()
    {
        // The general idea below is as follows:
        //     1) See if this method has already been successfully called.
        //        If so, return without doing anything.
        //
        //     2) Otherwise ... call the lazy constructor.
        //     3) Call validate so that any components added are visible.
        //     4) Note that we have run.

        if ( ( lazyConstructorCalled == false ) && ( getParent() != null ) )
        {
            lazyConstructor();
            lazyConstructorCalled = true;
            validate();
        }
    }


    /**
     *  This method must be implemented by any child class. Most of the
     *  component creation code that would have gone in the constructor of the
     *  child goes here instead. See the example at the top.
     */
    protected abstract void lazyConstructor();
}

