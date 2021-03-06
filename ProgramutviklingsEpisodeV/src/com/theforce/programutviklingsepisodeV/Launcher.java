package com.theforce.programutviklingsepisodeV;

import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
/**
 * Containts public staic void main and starts the entire program.
 * @author Martin
 * @author Jehans
 * @author John
 */
public class Launcher {
	public static WindowManager mWindowManager;
	public static Preferences mPreferences;

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
		
		// Create preferences
		Launcher.mPreferences = Preferences.userNodeForPackage(Launcher.class);
		
		// Create frame
		Launcher.mWindowManager = new WindowManager();
	}
	
	/**
	 * 
	 * @return Gets the windowmanager which contains all the windows.
	 */
	public static WindowManager getManager() {
		return Launcher.mWindowManager;
	}
	
	/**
	 * 
	 * @return Gets stored window properties preferences.
	 */
	public static Preferences getPreferences() {
		return Launcher.mPreferences;
	}
}
