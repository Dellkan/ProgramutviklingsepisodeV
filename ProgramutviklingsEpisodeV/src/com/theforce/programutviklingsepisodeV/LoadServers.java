package com.theforce.programutviklingsepisodeV;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;

/**
 * 
 * @author Jehans
 * @author John
 * @author Martin
 *
 * Will load all servers and add to interface
 */

@SuppressWarnings("serial")
public class LoadServers extends JFrame {
	Vector<Server> serverList = new Vector<Server>();
	Vector<String> networkList = new Vector<String>();
	JTextField realName,userName,alternativeUserName;
	JLabel jt;
	JComboBox serverBox = new JComboBox();
	JComboBox networkBox;
	GridBagConstraints gbc = new GridBagConstraints();
	final static String FILE_NAME = "profile.data";

	LoadServers() {
		super("Select server");
		this.setMinimumSize(new Dimension(600,300));
		this.setLayout(new GridBagLayout());	
		setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		BufferedReader br = null;
		try 
		{
			String sCurrentLine;		
			br = new BufferedReader(new FileReader("mIRCServers.ini"));
			while(!(br.readLine().contains("[servers]")));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(sCurrentLine.length() > 1) 
				{
					String network = sCurrentLine.substring(sCurrentLine.indexOf("GROUP")+6, sCurrentLine.length());
					if(!networkList.contains(network)) {		
						networkList.add(network);
					}
					serverList.add(new Server(sCurrentLine));
				}
			}
		 } 
		
		catch (IOException e) {
			e.printStackTrace();
		} 
		
		finally {
			try {
				if (br != null) {
					br.close();
				}
			} 
			
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		showNetworks();
		showServers(networkList.firstElement());
		createAndShowGUI();
		loadProfileData();
		this.pack();
	}


	/**
	 * Loads data about a earlier stored used profile.
	 */
	public void loadProfileData() {		 
		try{
			File file = new File(FILE_NAME);
			if (file.exists()) {
				FileReader filereader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(filereader);
				String profile = bufferedReader.readLine(); 
				
				int start = 0;
				int end = profile.indexOf(",");
				
				realName.setText(profile.substring(start, end));
				start = end;
				end = profile.indexOf(",", start+1);
				userName.setText(profile.substring(start+1, end));
				start = end;
				end = profile.length();
				alternativeUserName.setText(profile.substring(start+1,end));
				bufferedReader.close();
			}
		} 
		
		catch(Exception e) {} 
	}
	
	/**
	 * Stores profile data in a .data file
	 * @param profile the profile to be saved in the file.
	 */
	public void storeProfileData(Profile profile) {
		FileOutputStream fop = null;
		File file;
		String content = profile.getName() + "," + profile.getFirstNick() + 
						 "," + profile.getSecondNick(); 
		try {
 
			file = new File(FILE_NAME);
			fop = new FileOutputStream(file);
			file.createNewFile();
			
			byte[] contentInBytes = content.getBytes("UTF-8");
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
  
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds all servers to a dropdown list from a set network
	 * @param network which network the servers are from.
	 */
	public void showServers(String network) {
		serverBox.removeAllItems();
		for(int i = 0; i < serverList.size();i++) {
			String serverNetwork = serverList.get(i).getNetwork();
			if(serverNetwork.contentEquals(network)) {
				serverBox.addItem(serverList.get(i));
			}
		}
		pack();
	}
	
	/**
	 * Will show all networks found in a dropdown list.
	 */
	public void showNetworks() {
		Vector<String> listModel = new Vector<String>();
		for(int i = 0; i < networkList.size();i++)
		{
			listModel.addElement(networkList.elementAt(i));
		}
		networkBox = new JComboBox(listModel);
		networkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showServers(networkBox.getSelectedItem().toString());
			}		
		});
		
	}
	
	/**
	 * Shows the user GUI.
	 */
	public void createAndShowGUI() {
		gbc.fill = gbc.HORIZONTAL;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		jt = new JLabel("Username: ");
		jt.setPreferredSize(new Dimension(100, 20));
		jt.setMinimumSize(new Dimension(100,20));
		this.add(jt,gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		userName = new JTextField();
		userName.setMinimumSize(new Dimension(100,20));
		userName.setPreferredSize(new Dimension(100, 20));
		userName.setMaximumSize(new Dimension(100,20));
		this.add(userName,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		jt = new JLabel("Alternative Username: ");
		jt.setPreferredSize(new Dimension(140, 20));
		jt.setMinimumSize(new Dimension(100,20));
		this.add(jt,gbc);		
		gbc.gridx = 1;
		gbc.gridy = 1;
		alternativeUserName = new JTextField();
		alternativeUserName.setMinimumSize(new Dimension(100,20));
		alternativeUserName.setMaximumSize(new Dimension(100,20));
		alternativeUserName.setPreferredSize(new Dimension(100, 20));
		this.add(alternativeUserName,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		jt = new JLabel("Real name: ");
		jt.setPreferredSize(new Dimension(100, 20));
		jt.setMinimumSize(new Dimension(100,20));
		this.add(jt,gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		realName = new JTextField();
		realName.setMinimumSize(new Dimension(100,20));
		realName.setMaximumSize(new Dimension(100,20));
		realName.setPreferredSize(new Dimension(100, 20));
		this.add(realName,gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		jt = new JLabel("Network: ");
		jt.setPreferredSize(new Dimension(100, 20));
		jt.setMinimumSize(new Dimension(100,20));
		this.add(jt,gbc);
		gbc.gridx = 3;
		gbc.gridy = 0;
		networkBox.setPreferredSize(new Dimension(200,20));
		networkBox.setMaximumSize(new Dimension(200,20));
		this.add(networkBox,gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		jt = new JLabel("Server: ");
		jt.setMinimumSize(new Dimension(100,20));
		this.add(jt,gbc);	
		gbc.gridx = 3;
		gbc.gridy = 1;
		serverBox.setMinimumSize(new Dimension(300,20));
		serverBox.setPreferredSize(new Dimension(200,20));
		serverBox.setMaximumSize(new Dimension(250,20));
		this.add(serverBox,gbc);
		
		JButton connectServer = new JButton("Connect");
		connectServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(userName.getText().length() >= 1) {
					Profile profile = getUserProfile();
					ConnectionManager conManager = new ConnectionManager(profile);
					
					Server server = LoadServers.this.getChosenServer();
					int port = server.getAllowedPorts().contains(6667) ? 6667 : server.getAllowedPorts().firstElement();
					
					Session session = conManager.requestConnection(server.getDns(), port);
					Launcher.getManager().createServerWindow(session);
					
					storeProfileData(profile);
				}
			}
		});
		gbc.gridx = 2;
		gbc.gridy = 5;
		this.add(connectServer,gbc);
	}
	
	/**
	 *  Will get the chosen server.
	 * @return Gets chosen server from serverlist
	 */
	public Server getChosenServer() {
		return (Server) serverBox.getSelectedItem();
	}
	
	/**
	 * Will get the profile names and such from the menu.
	 * Will also autogenerate alternate nicknames and realName
	 * @return Profile object
	 */
	public Profile getUserProfile() {
		String nick = this.userName.getText();
		String real = this.realName.getText();
		String alternate = this.alternativeUserName.getText();
		
		if(alternate.length() < 1 || nick.contentEquals(alternate))	{
			alternate = nick + "1";
		}
		
		if(real.length() < 1) {
			real = "realNameGenerated321";
		}
		return new Profile(real,nick, alternate, (alternate+"2"));
	}
}
