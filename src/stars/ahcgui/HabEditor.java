/*
 * Created on Nov 11, 2004
 *
 * Copyright (c) 2004, Steve Leach
 * 
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
 * 
 */
package stars.ahcgui;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
;

/**
 * A Swing UI panel that allows editing of a race design habitability setting
 * 
 * @author Steve Leach
 */
public class HabEditor extends JPanel
{
   public JCheckBox immuneField;
   public JTextField minField;
   public JTextField maxField;

   public HabEditor()
   {
      setLayout( new BoxLayout(this,BoxLayout.X_AXIS) );
      
      immuneField = new JCheckBox("Immune");
      add( immuneField );
      
      minField = new JTextField(5);
      add( minField );
      
      add( new JLabel( " to ") );
      
      maxField = new JTextField(5);
      add( maxField );
   }
   
   public String getMinText()
   {
      return minField.getText();
   }
   
   public String getMaxText()
   {
      return maxField.getText();
   }
   
   public boolean getImmune()
   {
      return immuneField.isSelected();
   }
   
   public void setImmune( boolean isImmune )
   {
      immuneField.setSelected( isImmune );
   }
   
   public void setMinText( String text )
   {
      minField.setText( text );
   }
   
   public void setMaxText( String text )
   {
      maxField.setText( text );
   }
}
