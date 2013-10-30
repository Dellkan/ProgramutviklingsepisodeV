package com.theforce.programutviklingsepisodeV;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

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
		
		// Add windowmanager to frame
		this.add(this.mFrame);
		
		// Create window container
		this.mWindows = new ArrayList<JInternalFrame>();
	}
	
	public Boolean createChannel(String pChannelName) {
		return this.createChannel(pChannelName, false);
	}
	
	public Boolean createChannel(String pChannelName, Boolean pIsPrivateMessage) {
		// Create contents of window
		Window window = new Window(pChannelName, true, true, true, true);
		this.mWindows.add(window);
		//window.pack();
		
		// Add window to manager
		this.mFrame.add(window);
		
		// Set window position
		window.setLocation(100, 100);
		
		// Set window size
		window.setSize(200, 200);
		
		// Update and make visible
		window.setVisible(true);
		System.out.print(this.mWindows.size());
		return true;
	}
}
