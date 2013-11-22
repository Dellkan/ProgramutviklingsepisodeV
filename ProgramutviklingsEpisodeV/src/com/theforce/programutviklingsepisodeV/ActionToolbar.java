package com.theforce.programutviklingsepisodeV;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class ActionToolbar extends JToolBar {
	public ActionToolbar() {
		// Create buffer variables
		JButton button;
		
		// Create standard toolbar
		// New
		
        button = new JButton("Connect to server", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("connect.png")));
        button.setToolTipText("Create new window");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // New
            	new LoadServers();
            }
        });
        this.add(button);
	}
}
