/*
 * Created on Oct 15, 2004
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
package stars.ahc.plugins.base;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import stars.ahc.AHPoller;
import stars.ahc.GamesProperties;
import stars.ahc.Log;
import stars.ahc.NotificationListener;
import stars.ahcgui.AhcFrame;
import stars.ahcgui.pluginmanager.BasePlugIn;

import com.jeans.trayicon.TrayIconException;
import com.jeans.trayicon.TrayIconPopup;
import com.jeans.trayicon.TrayIconPopupSimpleItem;
import com.jeans.trayicon.WindowsTrayIcon;

/**
 * Plugin to manage interactions with the Windows system tray
 * 
 * @author Steve Leach
 */
public class TrayIconManager implements BasePlugIn, NotificationListener
{
   //
   // IMPORTANT
   // In order for this file to compile you will need the trayicon.jar file
   // from the plugins directory in your CLASSPATH.
   //
   // trayicon.jar is the Java component of the Windows Tray Icon library by Jan Struyf.
   // To run the plugin you also need the compiled C component, trayicon12.dll.
   //
   // Jan's details are...
   // jan.struyf@cs.kuleuven.ac.be
   // http://jeans.studentenweb.org/java/trayicon/trayicon.html
   //
   // Steve Leach, October 2004
   //
   
   private static TrayIconManager manager = null;
   private JFrame mainFrame;
   private String appName;
   private boolean trayIconSupported = false;
   private WindowsTrayIcon icon = null;
   private ImageIcon someOutIcon = null;
   private ImageIcon allInIcon = null;
   
   public TrayIconManager()
   {
   }

  
   /**
    * @param mainFrame
    */
   public void init(JFrame mainFrame, String appName)
   {
      this.mainFrame = mainFrame;
      this.appName = appName;
            
      loadLibraries();
      
      createIcon();
   }
   
   /**
    * 
    */
   private void createIcon()
   {
      try
      {
         if (trayIconSupported())
         {
		      someOutIcon = new ImageIcon( AhcFrame.findImage("stars16.gif") );
		      allInIcon = new ImageIcon( AhcFrame.findImage("allInIcon.gif") );
		      
		      icon = new WindowsTrayIcon(allInIcon.getImage(), 16, 16);
		      icon.setToolTipText("Stars! AutoHost Client");
		      icon.setPopup( makeTrayIconPopup() );
		      icon.setVisible(true);
		      icon.addActionListener( new RestoreListener(mainFrame,false) );
         }
      }
      catch (Throwable t)
      {
         t.printStackTrace();
         trayIconSupported = false;
      }      
   }

   /**
    * @return
    */
   private TrayIconPopup makeTrayIconPopup()
   {
      TrayIconPopup popup = new TrayIconPopup();
      
      TrayIconPopupSimpleItem item = new TrayIconPopupSimpleItem("&Show");
      item.setDefault(true);
      // Each menu item can have it's own ActionListener		
      item.addActionListener(new RestoreListener(mainFrame,true));
      popup.addMenuItem(item);
      
      return popup;
   }

   /**
    */
   private void loadLibraries()
   {
      File dll = findDLL();
      
      if (dll == null)
      {
         // Library not found, abort
         return;
      }
      
      System.load( dll.getAbsolutePath() );
      
      try
      {
			long result = WindowsTrayIcon.sendWindowsMessage(appName, 1234);
			if (result != -1) 
			{
				System.out.println("Already running other instance of "+appName+" (returns: "+result+")");
				return;
			}
			WindowsTrayIcon.initTrayIcon(appName);
			
			trayIconSupported = true;         
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
   }

   public boolean trayIconSupported()
   {
      return trayIconSupported;
   }
   
   public void cleanup()
   {
      try
      {
         if (icon != null)
         {
            icon.setVisible( false );
         }
         
         WindowsTrayIcon.cleanUp();
         
         trayIconSupported = false;
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }

   }
   
   private File findDLL()
   {
      File file = new File( "plugins/TrayIcon12.dll" );
      
      if (file.exists())
      {
         return file;
      }
      else
      {
         return null;
      }
   }

   /* (non-Javadoc)
    * @see stars.ahc.NotificationListener#receiveNotification(java.lang.Object, int, java.lang.String)
    */
   public void receiveNotification(Object source, int severity, String message)
   {
      final int MIN_TIMEOUT = 10; // 10 seconds is the minimum timeout specified by the Windows API
      
      if (trayIconSupported() == false)
      {
         return;
      }
      

      setIconAndTooltip();
      
      try
      {
         if ((source instanceof AHPoller))
         {
            // If the notification is from the AutoHost poller, only display the message as a balloon
            // if action is required by the user.
            if (GamesProperties.actionRequired())
            {
               icon.showBalloon( message, "Stars! AutoHost Client", MIN_TIMEOUT, WindowsTrayIcon.BALLOON_INFO );
            }
         }
         else
         {
            icon.showBalloon( message, "Stars! AutoHost Client", MIN_TIMEOUT, WindowsTrayIcon.BALLOON_INFO );
         }
      }
      catch (TrayIconException e)
      {
         Log.log( Log.ERROR, this, e.getMessage() );
      }
   }

   /**
    * 
    */
   private void setIconAndTooltip()
   {
      if (trayIconSupported() == false)
      {
         return;
      }
      
      try
      {
	      if (GamesProperties.actionRequired())
	      {
	         icon.setImage( someOutIcon.getImage(), 16, 16 );
	         icon.setToolTipText( "Stars! AutoHost Client\nPlayer action is required" );
	      }
	      else
	      {
	         icon.setImage( allInIcon.getImage(), 16, 16 );
	         icon.setToolTipText( "Stars! AutoHost Client\nNo action required" );
	      }
      }
      catch (Exception e)
      {
         Log.log( Log.ERROR, this, e.getMessage() );
      }
   }


   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.BasePlugIn#init()
    */
   public void init()
   {
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getName()
    */
   public String getName()
   {
      return "System tray icon manager";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#getDescription()
    */
   public String getDescription()
   {
      return "System tray icon manager";
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#isEnabled()
    */
   public boolean isEnabled()
   {
      return trayIconSupported();
   }

   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.PlugIn#setEnabled(boolean)
    */
   public void setEnabled(boolean enabled)
   {
   }


   /* (non-Javadoc)
    * @see stars.ahcgui.pluginmanager.BasePlugIn#init(javax.swing.JFrame)
    */
   public void init(JFrame mainWindow)
   {
      init( mainWindow, "AHClient" );
   }
}

//Callback listener handles restore (click left on any icon / show popup menu)
class RestoreListener implements ActionListener 
{

    protected boolean from_menu;
    private JFrame parent;

    public RestoreListener(JFrame parent, boolean fromMenu) 
    {
        from_menu = fromMenu;
        this.parent = parent;
    }

	public void actionPerformed(ActionEvent evt) 
	{
	   if (parent.isVisible())
	   {
	      parent.setVisible(false);
	   }
	   else
	   {
	      // Make main window visible if it was hidden
	      parent.setVisible(true);
	      // Bring to front
	      parent.toFront();
	      // Request input focus
	      parent.requestFocus();
	   }
	}

}
