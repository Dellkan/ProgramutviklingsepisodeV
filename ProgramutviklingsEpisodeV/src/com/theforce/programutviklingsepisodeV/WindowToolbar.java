package com.theforce.programutviklingsepisodeV;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class WindowToolbar extends JToolBar {
	private class WindowButton extends JButton {
		private Window mWindow;
		public WindowButton(String pTitle, ImageIcon pIcon, Window pWindow) {
			super(pTitle, pIcon);
			this.mWindow = pWindow;
		}
		
		public Window getWindow() {
			return this.mWindow;
		}
	}
	
	private JButton setupWindow(Window pWindow, String title, String iconPath) {
		// Create icon
		URL iconURL = Thread.currentThread().getContextClassLoader().getResource(iconPath);
		ImageIcon icon = null;
		if (iconURL != null) {
			icon = new ImageIcon(iconURL);
		}
		
		// Create button
        JButton button = new WindowButton(title, icon, pWindow);
        button.setToolTipText(title);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	Window window = ((WindowButton)event.getSource()).getWindow();
                window.toFront();
                window.getCLI().requestFocusInWindow();
            }
        });
        this.add(button);
        
        // Let the window know of its button
        pWindow.setToolbarReference(button);
        
        return button;
	}
	
	public void addWindow(ServerWindow pWindow) {
		this.setupWindow(pWindow, pWindow.getTitle(), "server.png");
	}
	
	public void addWindow(ChannelWindow pWindow) {
		this.setupWindow(pWindow, pWindow.getTitle(), "channel.png");
	}
	
	public void addWindow(QueryWindow pWindow) {
		this.setupWindow(pWindow, pWindow.getTitle(), "query.png");
	}
}
