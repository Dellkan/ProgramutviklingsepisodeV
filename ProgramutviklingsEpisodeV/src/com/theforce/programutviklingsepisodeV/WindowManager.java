package com.theforce.programutviklingsepisodeV;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jerklib.Channel;
import jerklib.Session;

@SuppressWarnings("serial")
public class WindowManager extends JFrame {
	private JDesktopPane mFrame;
	private ActionToolbar mActionToolbar;
	private WindowToolbar mWindowToolbar;
	private ArrayList<Window> mWindows = new ArrayList<Window>();
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
		
		// Add toolbar panel
		JPanel toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.Y_AXIS));
		
		// Add action toolbar
		this.mActionToolbar = new ActionToolbar();
		this.mActionToolbar.setFloatable(false);
		this.mActionToolbar.setAlignmentX(0);
		toolbarPanel.add(this.mActionToolbar);
		
		// Add window toolbar
		this.mWindowToolbar = new WindowToolbar();
		this.mWindowToolbar.setFloatable(false);
		this.mWindowToolbar.setAlignmentX(0);
		toolbarPanel.add(this.mWindowToolbar);
		
		// Add toolbar panel to layout
		this.add(toolbarPanel, BorderLayout.NORTH);
	}
	
	public ActionToolbar getActionToolbar() {
		return this.mActionToolbar;
	}
	
	public WindowToolbar getWindowToolbar() {
		return this.mWindowToolbar;
	}
	
	private void setupWindow(Window window) {
		// Add window to manager
		this.mWindows.add(window);
		
		// Put the window into the frame
		this.mFrame.add(window);
		
		// Set window position
		window.setLocation(100, 100);
		
		// Set window size
		window.setSize(400, 300);
		
		// Update and make visible
		window.setVisible(true);
	}
	
	public ServerWindow createServerWindow(Session pSession) {
		ServerWindow window = new ServerWindow(pSession);
		this.setupWindow(window);
		this.mWindowToolbar.addWindow(window);
		return window;
	}
	
	public ChannelWindow createChannelWindow(Channel pChannel) {
		ChannelWindow window = new ChannelWindow(pChannel);
		this.setupWindow(window);
		this.mWindowToolbar.addWindow(window);
		return window;
	}
	
	public QueryWindow createQueryWindow(Session pSession, String pNickname) {
		QueryWindow window = new QueryWindow(pSession, pNickname);
		this.setupWindow(window);
		this.mWindowToolbar.addWindow(window);
		return window;
	}
	
	public QueryWindow findQueryWindow(Session pSession, String pNick) {
		for (Window window : this.mWindows) {
			if (window instanceof QueryWindow) {
				QueryWindow queryWindow = (QueryWindow) window;  
				if (queryWindow.getSession().equals(pSession) && queryWindow.getNick().equals(pNick)) {
					return (QueryWindow)window;
				}
			}
		}
		return null;
	}
	
	public ChannelWindow findChannelWindow(Channel pChannel) {
		for (Window window : this.mWindows) {
			if (window instanceof ChannelWindow && ((ChannelWindow)window).getChannel().equals(pChannel)) {
				return (ChannelWindow)window;
			}
		}
		return null;
	}
	
	public List<ChannelWindow> findChannelWindows(Session pSession) {
		List<ChannelWindow> windows = new ArrayList<ChannelWindow>();
		for (Window window : this.mWindows) {
			if (window instanceof ChannelWindow && ((ChannelWindow)window).getChannel().getSession().equals(pSession)) {
				windows.add((ChannelWindow)window);
			}
		}
		return windows;
	}
	
	public ServerWindow findServerWindow(Session pSession) {
		for (Window window : this.mWindows) {
			if (window instanceof ServerWindow && ((ServerWindow)window).getSession().equals(pSession)) {
				return (ServerWindow)window;
			}
		}
		return null;
	}
	
	public void RemoveWindow(Window pWindow) {
		this.mWindows.remove(pWindow);
		if (!pWindow.isClosed()) { pWindow.dispose(); }
	}
}
