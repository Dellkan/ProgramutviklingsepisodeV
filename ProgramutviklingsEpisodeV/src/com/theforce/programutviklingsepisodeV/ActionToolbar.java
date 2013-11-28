package com.theforce.programutviklingsepisodeV;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class ActionToolbar extends JToolBar {
	public ActionToolbar() {
		// Create buffer variable
		JButton button;
		
		// Connect to server
        button = new JButton("Connect to server", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("connect.png")));
        button.setToolTipText("Create new window");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // New
            	new LoadServers();
            }
        });
        this.add(button);
        
        // Open debug window
        button = new JButton("Debug", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("cog.png")));
        button.setToolTipText("Create new window");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	Launcher.getManager().createDebugWindow();
            }
        });
        this.add(button);
	}
}
