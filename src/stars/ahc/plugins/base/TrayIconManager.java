/*
 * Created on Oct 15, 2004
 *
 * Copyright (c) 2004, Steve Leach
 */
package stars.ahc.plugins.base;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import stars.ahc.NotificationListener;
import stars.ahcgui.AhcFrame;
import stars.ahcgui.pluginmanager.BasePlugIn;

import com.jeans.trayicon.TrayIconException;
import com.jeans.trayicon.TrayIconPopup;
import com.jeans.trayicon.TrayIconPopupSimpleItem;
import com.jeans.trayicon.WindowsTrayIcon;

/**
 * @author Steve Leach
 *
 */
public class TrayIconManager implements BasePlugIn, NotificationListener
{
   private static TrayIconManager manager = null;
   private JFrame mainFrame;
   private String appName;
   private boolean trayIconSupported = false;
   private WindowsTrayIcon icon = null;
   
   public TrayIconManager()
   {
   }

  
   /**
    * @param mainFrame
    */
   public void init(JFrame mainFrame, String appName)
   {
      System.out.println( "Initialising TrayIconManager" );
      
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
	      ImageIcon img = new ImageIcon( AhcFrame.findImage("stars16.gif") );
	      icon = new WindowsTrayIcon(img.getImage(), 16, 16);
	      icon.setToolTipText("Stars! AutoHostClient");
	      icon.setPopup( makeTrayIconPopup() );
	      icon.setVisible(true);
	      icon.addActionListener( new RestoreListener(mainFrame,false) );
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
      System.out.println( "Cleaning up Tray Icon manager" );
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
      try
      {
         icon.showBalloon( message, "Stars! AutoHost Client", 10, WindowsTrayIcon.BALLOON_INFO );
      }
      catch (TrayIconException e)
      {
         e.printStackTrace();
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
      return true;
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

	public void actionPerformed(ActionEvent evt) {
		if (from_menu) System.out.println("Restore selected..");
		else
		{
		   System.out.println("Tray icon button pressed..");
		}
		
		// Make main window visible if it was hidden
		parent.setVisible(true);
		// Request input focus
		parent.requestFocus();
	}

}
