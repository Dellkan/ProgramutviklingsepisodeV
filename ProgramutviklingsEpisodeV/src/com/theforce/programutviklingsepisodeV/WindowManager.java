package com.theforce.programutviklingsepisodeV;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
		// Create window
		JInternalFrame window = new JInternalFrame (pChannelName, true, true, true, true);
		this.mWindows.add(window);
		
		// Create contents of window
		MyPanel panel = new MyPanel();
		window.add (panel);
		window.pack();
		
		// Add window to manager
		this.mFrame.add(window);
		
		// Set window position
		window.setLocation(100, 100);
		
		// Update and make visible
		window.setVisible(true);
		System.out.print(this.mWindows.size());
		return true;
	}
}

//Lazy lazy, we place the MyPanel class here to avoid extra files
@SuppressWarnings("serial")
class MyPanel extends JPanel {
	public MyPanel () {
		JLabel label = new JLabel ("Some text");
		setLayout (new BorderLayout ());
		add (new JScrollPane (label));
	}
}
