/*
 *  This file is part of Stars! Autohost Client
 *  Copyright (c) 2003 Jeffrey C. Hoyt
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package stars.ahcgui;

import java.util.Comparator;

import stars.ahc.Fleet;

/**
 *  Description of the Class
 *
 *@author     jchoyt
 *@created    February 23, 2004
 */
public class WarFleetComparator implements Comparator
{
    protected boolean includeWarship = true;
    protected boolean includeUtil = false;
    protected boolean includeScout = true;


    /**
     *  Constructor for the WarFleetComparator object
     *
     *@param  warship  Description of the Parameter
     *@param  util     Description of the Parameter
     *@param  scout    Description of the Parameter
     */
    public WarFleetComparator( boolean warship, boolean util, boolean scout )
    {
        includeScout = scout;
        includeUtil = util;
        includeWarship = warship;
    }


    /**
     *  Constructor for the WarFleetComparator object using the default values
     *  for what to include (warships and scouts)
     */
    public WarFleetComparator() { }


    /**
     *  Compares its two arguments for order. Returns a negative integer, zero,
     *  or a positive integer as the first argument is less than, equal to, or
     *  greater than the second. The implementor must ensure that sgn(compare(x,
     *  y)) == -sgn(compare(y, x)) for all x and y. (This implies that
     *  compare(x, y) must throw an exception if and only if compare(y, x)
     *  throws an exception.) The implementor must also ensure that the relation
     *  is transitive: ((compare(x, y)>0) && (compare(y, z)>0)) implies
     *  compare(x, z)>0. Finally, the implementer must ensure that compare(x,
     *  y)==0 implies that sgn(compare(x, z))==sgn(compare(y, z)) for all z. It
     *  is generally the case, but not strictly required that (compare(x, y)==0)
     *  == (x.equals(y)). Generally speaking, any comparator that violates this
     *  condition should clearly indicate this fact. The recommended language is
     *  "Note: this comparator imposes orderings that are inconsistent with
     *  equals."
     *
     *@param  o1                      the first object to be compared.
     *@param  o2                      the second object to be compared.
     *@return                         a negative integer, zero, or a positive
     *      integer as the first argument is less than, equal to, or greater
     *      than the second.
     *@exception  ClassCastException  thrown if the arguments' types prevent
     *      them from being compared by this Comparator.
     */
    public int compare( Object o1, Object o2 )
        throws ClassCastException
    {
        Fleet f1 = ( Fleet ) o1;
        Fleet f2 = ( Fleet ) o2;
        int count1 = 0;
        int count2 = 0;
        if ( includeWarship )
        {
            count1 += f1.getIntValue( Fleet.WARSHIP );
            count2 += f2.getIntValue( Fleet.WARSHIP );
        }
        if ( includeScout )
        {
            count1 += f1.getIntValue( Fleet.SCOUT );
            count2 += f2.getIntValue( Fleet.SCOUT );
        }
        if ( includeUtil )
        {
            count1 += f1.getIntValue( Fleet.UTILITY );
            count2 += f2.getIntValue( Fleet.UTILITY );
        }
        return new Integer( count2 ).compareTo( new Integer( count1 ) );
    }


    /**
     *  Indicates whether some other object is "equal to" this Comparator. This
     *  method must obey the general contract of Object.equals(Object).
     *  Additionally, this method can return true only if the specified Object
     *  is also a comparator and it imposes the same ordering as this
     *  comparator. Thus, comp1.equals(comp2) implies that sgn(comp1.compare(o1,
     *  o2))==sgn(comp2.compare(o1, o2)) for every object reference o1 and o2.
     *  Note that it is always safe not to override Object.equals(Object).
     *  However, overriding this method may, in some cases, improve performance
     *  by allowing programs to determine that two distinct Comparators impose
     *  the same order. <br>
     *  Overrides: equals in class Object <br>
     *  true only if the specified object is also a comparator and it imposes
     *  the same ordering as this comparator.
     *
     *@param  obj  the reference object with which to compare.
     *@return      true only if the specified object is also a comparator and it
     *      imposes the same ordering as this comparator.
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof WarFleetComparator )
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
}

