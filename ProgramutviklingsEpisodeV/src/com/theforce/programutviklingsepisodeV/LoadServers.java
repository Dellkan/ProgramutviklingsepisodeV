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
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
/**
 * Will show the connectionWindow frame once
 * the "Connect to server" from the main window is pressed.
 * @author Jehans
 * @author John
 * @author Martin
 */
@SuppressWarnings("serial")
public class LoadServers extends JFrame {
	private Preferences mPreferences;
	private Vector<Server> mServerList = new Vector<Server>();
	private Vector<String> mNetworkList = new Vector<String>();
	private JTextField mRealName,mUserName,mAlternativeName;
	private JComboBox mServerBox = new JComboBox();
	private JComboBox mNetworkBox = new JComboBox();

	LoadServers() {
		super("Select server");
		this.pack();
		this.setMinimumSize(new Dimension(400,220));
		this.setLayout(new GridBagLayout());	
		setVisible(true);

		BufferedReader br = null;
		try 
		{
			String sCurrentLine;		
			br = new BufferedReader(new FileReader("/mIRCServers.ini"));
			while(!(br.readLine().contains("[servers]")));
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(sCurrentLine.length() > 1) 
				{
					String network = sCurrentLine.substring(sCurrentLine.indexOf("GROUP")+6, sCurrentLine.length());
					if(!mNetworkList.contains(network)) {		
						mNetworkList.add(network);
					}
					mServerList.add(new Server(sCurrentLine));
				}
			}
		 } 
		
		catch (IOException e) {
			e.printStackTrace();
			} 

		showNetworks();
		showServers(mNetworkList.firstElement());
		createAndShowGUI();
		getPreferences();
		this.pack();
	}


	/**
	 * Loads data about a earlier stored used profile from stored preferences
	 */
	public void getPreferences() {	
	    this.mPreferences = Preferences.userRoot().node(this.getClass().getName());
	    
	    this.mUserName.setText(mPreferences.get("userName","userName"));
	    this.mRealName.setText(mPreferences.get("realName", "realName")); 
	    this.mAlternativeName.setText(mPreferences.get("alternate", "alternate"));
	}
	
	/**
	 * Stores preferenced usernames typed in the connection window
	 * @param profile the profile to be saved in the file.
	 */
	public void setPreferences(Profile profile) {
		this.mPreferences = Preferences.userRoot().node(this.getClass().getName());
		this.mPreferences.put("realName", profile.getName());
		this.mPreferences.put("userName", profile.getFirstNick());
		this.mPreferences.put("alternate", profile.getSecondNick());
	}

	/**
	 * Adds all servers to a dropdown list from a set network
	 * @param network which network the servers are from.
	 */
	public void showServers(String network) {
		this.mServerBox.removeAllItems();
		
		for(int i = 0; i < mServerList.size();i++) {
			String serverNetwork = mServerList.get(i).getNetwork();
			if(serverNetwork.contentEquals(network)) {
				this.mServerBox.addItem(mServerList.get(i));
			}
		}
	}
	
	/**
	 * Shows all the networks currently in the list of found networks.
	 * <br>Will show the in a dropdown menu.
	 */
	public void showNetworks() {
		for(int i = 0; i < mNetworkList.size();i++) {
			this.mNetworkBox.addItem(mNetworkList.elementAt(i));
		}
		
		mNetworkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showServers(mNetworkBox.getSelectedItem().toString());
			}		
		});
	}
	
	/**
	 * Shows the user GUI for the connectionWindow
	 * with profile information and serverinformation
	 * <br> once connect to server is pressed.
	 */
	public void createAndShowGUI() {
		GridBagConstraints gbc = new GridBagConstraints();
		JLabel dText;
		gbc.fill = gbc.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		dText = new JLabel("Username: ");
		this.add(dText,gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		this.mUserName = new JTextField();
		this.mUserName.setMinimumSize(new Dimension(300,300));
		this.mUserName.setMaximumSize(new Dimension(300,300));
		this.add(mUserName,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		dText = new JLabel("Alternative Username: ");
		this.add(dText,gbc);		
		gbc.gridx = 1;
		gbc.gridy = 1;
		this.mAlternativeName = new JTextField();
		this.mAlternativeName.setMinimumSize(new Dimension(300,30));
		this.mAlternativeName.setMaximumSize(new Dimension(300,30));
		this.add(this.mAlternativeName,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		dText = new JLabel("Real name: ");
		this.add(dText,gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		this.mRealName = new JTextField();
		this.mRealName.setMinimumSize(new Dimension(200,30));
		this.mRealName.setMaximumSize(new Dimension(200,30));
		this.add(this.mRealName,gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		this.mNetworkBox.setMinimumSize(new Dimension(350,30));
		this.mNetworkBox.setPreferredSize(new Dimension(350,30));
		this.mNetworkBox.setMaximumSize(new Dimension(350,30));
		this.add(this.mNetworkBox,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		this.mServerBox.setMinimumSize(new Dimension(350,30));
		this.mServerBox.setMaximumSize(new Dimension(350,30));
		this.add(this.mServerBox,gbc);
		
		gbc.fill = gbc.NONE;
		JButton connectServer = new JButton("Connect");
		connectServer.setMaximumSize(new Dimension(50,30));
		connectServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(mUserName.getText().length() >= 1) {
					Profile profile = getUserProfile();
					ConnectionManager conManager = new ConnectionManager(profile);
					
					Server server = LoadServers.this.getChosenServer();
					int port = server.getAllowedPorts().contains(6667) ? 6667 : server.getAllowedPorts().firstElement();
					
					Session session = conManager.requestConnection(server.getDns(), port);
					Launcher.getManager().createServerWindow(session);
					
					setPreferences(profile);
					setVisible(false);
					dispose();
				}
			}
		});
		gbc.gridx = 1;
		gbc.gridy = 5;
		this.add(connectServer,gbc);
	}

	/**
	 *  Will get the server that is chosen from the dropdown menu
	 *  <br> shown in the connection window
	 * @return Gets chosen server from mServerList
	 */
	public Server getChosenServer() {
		return (Server) this.mServerBox.getSelectedItem();
	}
	
	/**
	 * Will get the profile information that the user typed into 
	 * <br> the connection window and return it as a profile object.
	 * Will also autogenerate alternate nicknames and real name if not supplied
	 * <br> or if they are alike.
	 * @return Profile object
	 */
	public Profile getUserProfile() {
		String nick = this.mUserName.getText();
		String real = this.mRealName.getText();
		String alternate = this.mAlternativeName.getText();
		
		if(alternate.length() < 1 || nick.contentEquals(alternate))	{
			alternate = nick + "1";
		}
		
		if(real.length() < 1) {
			real = "mRealNameGenerated321";
		}
		return new Profile(real,nick, alternate, (alternate+"2"));
	}
}
