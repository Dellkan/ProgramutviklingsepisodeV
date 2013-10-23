package com.theforce.programutviklingsepisodeV;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class Toolbar extends JToolBar {
	public Toolbar() {
		Launcher.getManager().add(this, BorderLayout.NORTH);
		// Create buffer variables
		JButton button;
		
		// Create standard toolbar
		// New
        button = new JButton("Create new window");
        button.setToolTipText("Create new window");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                // New
            	Launcher.getManager().createChannel("test", false);
            }
        });
        this.add(button);
	}
}
