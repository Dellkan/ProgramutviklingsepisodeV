package com.theforce.programutviklingsepisodeV;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jerklib.Channel;
import jerklib.Session;

/**
 * This class is used to manage and contain all server, channel and PM windows 
 * @author Hans Martin Bragen
 * @author Jehans Jr. Storvik
 * @author John Høegh-Omdal
 */
@SuppressWarnings("serial")
public class WindowManager extends JFrame {
	private JDesktopPane mFrame;
	private ActionToolbar mActionToolbar;
	private JTabbedPane mTabs;
	private ArrayList<Window> mWindows = new ArrayList<Window>();
	private DebugWindow mDebugWindow;
	
	/**
	 * Constructor used to create the initial window
	 */
	public WindowManager() {
		super();
		// Create frame
		this.mFrame = new JDesktopPane();
		
		// Frame properties
		this.setTitle("Hypercomm");
		
		Preferences pref = Launcher.getPreferences();
		this.setSize(pref.getInt("WindowWidth", 800), pref.getInt("WindowHeight", 500));
		this.setExtendedState(pref.getBoolean("WindowMaximized", false) ? MAXIMIZED_BOTH : NORMAL);
		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		
		// Add state listeners, so we can store some preferences about window maximizing
		this.addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent pEvent) {
				if ((pEvent.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
					Launcher.getPreferences().putBoolean("WindowMaximized", true);
				}
				
				else if (pEvent.getNewState() == Frame.NORMAL) {
					Launcher.getPreferences().putBoolean("WindowMaximized", false);
				}
			}
		});
		
		// Add component listener so we can detect resizing
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent pEvent) {
				Preferences pref = Launcher.getPreferences();
				pref.putInt("WindowWidth", pEvent.getComponent().getWidth());
				pref.putInt("WindowHeight", pEvent.getComponent().getHeight());
			}
		});
		
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
		
		// Add tabs toolbar
		this.mTabs = new JTabbedPane(JTabbedPane.TOP);
		this.mTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.mTabs.setAlignmentX(0);
		this.mTabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent pEvent) {
				JTabbedPane tabPane = (JTabbedPane) pEvent.getSource();
				for (Window window : WindowManager.this.getAllWindows()) {
					if (window.getToolbarReference() == tabPane.getSelectedComponent()) {
						// Bring to front
						WindowManager.this.mFrame.getDesktopManager().activateFrame(window);
						
						// Select it
						try { window.setSelected(true); } catch (PropertyVetoException e) {	}
						break;
					}
				}
			}
		});
		toolbarPanel.add(this.mTabs);
		
		// Add toolbar panel to layout
		this.add(toolbarPanel, BorderLayout.NORTH);
	}
	
	public ActionToolbar getActionToolbar() {
		return this.mActionToolbar;
	}
	
	public JTabbedPane getWindowToolbar() {
		return this.mTabs;
	}
	
	public List<Window> getAllWindows() {
		return this.mWindows;
	}
	
	public List<ServerWindow> getAllServerWindows() {
		List<ServerWindow> windows = new ArrayList<ServerWindow>();
		for (Window window : this.getAllWindows()) {
			if (window instanceof ServerWindow) {
				windows.add((ServerWindow) window);
			}
		}
		return windows;
	}
	/**
	 * 
	 * @param window The target window object
	 * @param tabIconPath path to image to be used as icon
	 */
	
	private void setupWindow(Window window, String tabIconPath) {
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
		
		// Create tab
		JPanel dummy = new JPanel();
		dummy.setVisible(false);
		dummy.setSize(0, 0);
		this.mTabs.addTab(window.getTitle(), dummy);
		
		// Set tab reference
		window.setToolbarReference(dummy);
		
		// Set custom buttons on tab
		URL iconURL = Thread.currentThread().getContextClassLoader().getResource(tabIconPath);
		ImageIcon icon = null;
		if (iconURL != null) {
			icon = new ImageIcon(iconURL);
		}
		window.setFrameIcon(icon);
		this.mTabs.setTabComponentAt(window.getToolbarIndex(), new WindowTabButtonComponent(this.mTabs, icon));
	}
	/**
	 * Generates a debug window if one does not already exist
	 */
	
	public void createDebugWindow() {
		if (this.mDebugWindow == null || this.mDebugWindow.isClosed()) {
			this.mDebugWindow = new DebugWindow();
			this.setupWindow(this.mDebugWindow, "cog.png");
		}
	}
	/**
	 * 
	 * @return if a debugWindow exists it's returned else returns null
	 */
	
	public DebugWindow getDebugWindow() {
		if (this.mDebugWindow == null || this.mDebugWindow.isClosed()) {
			return null;
		}
		return this.mDebugWindow;
	}
	/**
	 * Generates a server window and associates it with an irc session
	 * @param pSession to associate with the server window
	 * @return returns a server window object
	 */
	public ServerWindow createServerWindow(Session pSession) {
		ServerWindow window = new ServerWindow(pSession);
		this.setupWindow(window, "server.png");
		
		// return the window
		return window;
	}
	/**
	 * Generates a channel window and associates it with an irc session.channel
	 * @param pChannel the channel to associate the window with
	 * @return returns a channel window object
	 */
	public ChannelWindow createChannelWindow(Channel pChannel) {
		ChannelWindow window = new ChannelWindow(pChannel);
		this.setupWindow(window, "channel.png");
		
		// return the window
		return window;
	}
	
	/**
	 * Generates a PM window
	 * @param pSession Session in which the MSG is
	 * @param pNickname Nick of the person the conversation is with
	 * @return returns a query window object
	 */
	public QueryWindow createQueryWindow(Session pSession, String pNickname) {
		QueryWindow window = new QueryWindow(pSession, pNickname);
		this.setupWindow(window, "query.png");
		
		// return the window
		return window;
	}
	
	/**
	 * Locates an open query window if it exists
	 * @param pSession Session which the conversation is
	 * @param pNick nick of the person the conversation is with
	 * @return returns the query window if found else returns null
	 */
	public QueryWindow findQueryWindow(Session pSession, String pNick) {
		for (Window window : this.mWindows) {
			if (window instanceof QueryWindow) {
				QueryWindow queryWindow = (QueryWindow) window;  
				if (queryWindow.getSession().equals(pSession) && queryWindow.getNick().equalsIgnoreCase(pNick)) {
					return (QueryWindow)window;
				}
			}
		}
		return null;
	}
	/**
	 * Locates an open channel window if it exists
	 * @param pChannel channel you're looking for
	 * @return returns the channel window if found else returns null
	 */
	public ChannelWindow findChannelWindow(Channel pChannel) {
		for (Window window : this.mWindows) {
			if (window instanceof ChannelWindow && ((ChannelWindow)window).getChannel().equals(pChannel)) {
				return (ChannelWindow)window;
			}
		}
		return null;
	}
	
	/**
	 * locates all open channel windows in a session
	 * @param pSession in which to locate open channels
	 * @return returns a list with all open channel windows in a session
	 */
	public List<ChannelWindow> findChannelWindows(Session pSession) {
		List<ChannelWindow> windows = new ArrayList<ChannelWindow>();
		for (Window window : this.mWindows) {
			if (window instanceof ChannelWindow && ((ChannelWindow)window).getChannel().getSession().equals(pSession)) {
				windows.add((ChannelWindow)window);
			}
		}
		return windows;
	}
	
	/**
	 * Finds the server window associated with a session
	 * @param pSession the session to locate the server window
	 * @return if found returns the server window else returns null
	 */
	public ServerWindow findServerWindow(Session pSession) {
		for (Window window : this.mWindows) {
			if (window instanceof ServerWindow && ((ServerWindow)window).getSession().equals(pSession)) {
				return (ServerWindow)window;
			}
		}
		return null;
	}
	
	/**
	 * Kills a window and disposes of it's body
	 * @param pWindow window to kill
	 */
	public void RemoveWindow(Window pWindow) {
		this.mWindows.remove(pWindow);
		if (!pWindow.isClosed()) { pWindow.dispose(); }
	}
}
