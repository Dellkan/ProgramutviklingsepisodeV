package com.theforce.programutviklingsepisodeV;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Launcher {
	public static WindowManager mWindowManager;
	public static JMenuBar mMenu;
	public static Toolbar mToolBar;
	public static IRCEventHandler mIRCEventHandler;

	public static void main(String[] args) {
		// Set theme
		JFrame.setDefaultLookAndFeelDecorated(true);
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if (info.getName().equals("Nimbus")) { // Use Nimbus if available
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {	}
		
		// Create frame
		Launcher.mWindowManager = new WindowManager();
		
		// Create menu in frame
		
		// Create toolbar in frame
		Launcher.mToolBar = new Toolbar();
		Launcher.mToolBar.setFloatable(false);
		
		// Create event handler
		
	}
	
	public static WindowManager getManager() {
		return Launcher.mWindowManager;
	}
	
	public static JMenuBar getMenu() {
		return Launcher.mMenu;
	}
	
	public static Toolbar mToolBar() {
		return Launcher.mToolBar;
	}
	
	public static IRCEventHandler getEventHandler() {
		return Launcher.mIRCEventHandler;
	}
}
