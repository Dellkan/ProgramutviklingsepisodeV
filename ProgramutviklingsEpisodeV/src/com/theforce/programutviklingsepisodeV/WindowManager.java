package com.theforce.programutviklingsepisodeV;

import java.util.ArrayList;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import jerklib.Channel;
import jerklib.Session;

@SuppressWarnings("serial")
public class WindowManager extends JFrame {
	private JDesktopPane mFrame;
	private ArrayList<JInternalFrame> mWindows;
	public WindowManager() {
		super();
		// Create frame
		this.mFrame = new JDesktopPane();
		
		// Frame properties
		this.setTitle("Hypercomm");
		this.setSize(800, 500);
		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		// Add window manager to frame
		this.add(this.mFrame);
		
		// Create window container
		this.mWindows = new ArrayList<JInternalFrame>();
	}
	
	private void setupWindow(Window window) {
		// Create contents of window
		this.mWindows.add(window);
		//window.pack();
		
		// Add window to manager
		this.mFrame.add(window);
		
		// Set window position
		window.setLocation(100, 100);
		
		// Set window size
		window.setSize(400, 300);
		
		// Update and make visible
		window.setVisible(true);
	}
	
	public void createServerWindow(Session pSession) {
		ServerWindow window = new ServerWindow(pSession);
		this.setupWindow(window);
	}
	
	public void createChannelWindow(Channel pChannel) {
		ChannelWindow window = new ChannelWindow(pChannel);
		this.setupWindow(window);
	}
	
	public void createQueryWindow(Session pSession, String pNickname) {
		QueryWindow window = new QueryWindow(pSession, pNickname);
		this.setupWindow(window);
	}
}
