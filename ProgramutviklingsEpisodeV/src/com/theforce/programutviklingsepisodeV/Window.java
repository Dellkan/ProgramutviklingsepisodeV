package com.theforce.programutviklingsepisodeV;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
class Window extends JInternalFrame {
	JTextArea chat;
	JList users;
	JTextField cli;
	public Window(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
		super(title, resizable, closable, maximizable, iconifiable);
		// Layout stuff
		this.setLayout (new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		// Create content area
		this.chat = new JTextArea();
		this.chat.setEditable(false);
		this.chat.append("\ntestfgfdgfdgfdhreftgjhfjkshgodhniusnfginbordhndouhbspgnoufhgpfdnogursngpbordsngofbprnfgousnpfgnsuogbndnbsgousbngsbf fioghfdpihgnfdgfdngofdngofdngf noifdhgfdjgoifdng iufdhgpossme535ngfd gog4 n fdGHFGHHFG");
		this.chat.setLineWrap(true);
		
		constraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		constraints.fill = java.awt.GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = java.awt.GridBagConstraints.WEST;
		
		JScrollPane chatScroller = new JScrollPane(this.chat);
		
		this.add(chatScroller, constraints);
		
		// Create user list
		this.users = new JList();
		this.users.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.users.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.users.setVisibleRowCount(-1);
		
		JScrollPane userScroller = new JScrollPane(this.users);
		userScroller.setPreferredSize(new Dimension(100, 150));

		constraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		constraints.fill = java.awt.GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.anchor = java.awt.GridBagConstraints.EAST;
		
		this.add(userScroller, constraints);
		
		// Create command line interface (textbox)
		constraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		constraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.gridwidth = 2;
		constraints.anchor = java.awt.GridBagConstraints.SOUTH;
		
		JTextField cli = new JTextField("Commands goes here, stupid");
		cli.setSize(0, 20);
		this.add(new JScrollPane (cli), constraints);
	}
}