package com.theforce.programutviklingsepisodeV;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jerklib.ConnectionManager;
import jerklib.Profile;

public class LoadServers extends JFrame {
	Vector<Server> serverList;
	Vector<String> networkList;
	JTextField realName;
	JTextField userName;
	JTextField alternativeUserName;
	JLabel jt;
	JComboBox serverBox = new JComboBox();
	JComboBox networkBox;
	GridBagConstraints gbc = new GridBagConstraints();
	final static String FILE_NAME = "profile.data";



	
	LoadServers() 
	{
		super("Select server");
		this.setLayout(new GridBagLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		serverList = new Vector<Server>();
		networkList = new Vector<String>();
		
		BufferedReader br = null;
		try {
			String sCurrentLine;
			String type = null;
			
			br = new BufferedReader(new FileReader("mIRCServers.ini"));
 
			while ((sCurrentLine = br.readLine()) != null) 
			{
				
				if(sCurrentLine.length() > 1)		//In case of blank line
				{
					if(sCurrentLine.contains("[timestamp]")) {				
						type = "timestamp";
					} else if(sCurrentLine.contains("[networks]")) {
						type = "networks";
					} else if(sCurrentLine.contains("[servers]"))	{
						type = "servers";
					} else if(type != null)	//Type is set, and we are running through the serverlist, networklist, etc
					{
							switch(type) 
							{
							case "timestamp":
								break;
							case "networks":
								networkList.add(sCurrentLine.substring(3, sCurrentLine.length()));
								break;
							case "servers":
								String network = sCurrentLine.substring(sCurrentLine.indexOf("GROUP")+6, sCurrentLine.length());								
								if(!networkList.contains(network)) 
									    //There are some networks which are not listed in the networklist
								{		//This will add them to the network vectorlist.
									networkList.add(network);
								}
								serverList.add(new Server(sCurrentLine));
								break;
							}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		/*
		 * Bør nok muligens flyttes til GUI funksjonen.
		 */

		

		showUserProfileGUI();
		showNetworks();
		showServers(networkList.firstElement());
		loadProfileData();
		this.pack();
	}

	/*
	 * Loads earlier(if they exist) data about a user profile
	 */
	public void loadProfileData() {		 
		   try{
			   File file = new File(FILE_NAME);
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
		   }catch(Exception ex){
			   ex.printStackTrace();
		   } 
	}
	
	/*
	 * Stores profile data in a .data file
	 */
	public void storeProfileData(Profile profile) {
		/*
		 * Was supposed to write out the entire object, but
		 * then we would have to edit jerklibs Profile() 
		 */
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

	public void showServers(String network) {
		
		serverBox.removeAllItems();
		for(int i = 0; i < serverList.size();i++)
		{
			String serverNetwork = serverList.get(i).getNetwork();
			if(serverNetwork.contentEquals(network)) {
				serverBox.addItem(serverList.get(i));
			}
		}
		pack();
	}
	
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
		gbc.gridx = 3;
		gbc.gridy = 0;
		networkBox.setPreferredSize(new Dimension(100,30));
		this.add(networkBox,gbc);
		
		pack();
		
	}
	
	public void showUserProfileGUI(){
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		jt = new JLabel("Username: ");
		this.add(jt,gbc);
		
		gbc.gridy = 1;
		jt = new JLabel("Alternative Username: ");
		this.add(jt,gbc);
		
		gbc.gridy = 3;
		jt = new JLabel("Real name: ");
		this.add(jt,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.ipadx = 100;
		userName = new JTextField();
		this.add(userName,gbc);
		
		gbc.gridy = 1;
		alternativeUserName = new JTextField();
		this.add(alternativeUserName,gbc);

		
		gbc.gridy = 3;
		realName = new JTextField();
		this.add(realName,gbc);
		
		jt = new JLabel("Network: ");
		gbc.gridx = 2;
		gbc.gridy = 0;
		this.add(jt,gbc);
		
		jt = new JLabel("Server: ");
		gbc.gridy = 1;
		this.add(jt,gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 1;
		this.add(serverBox,gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 5;
		JButton connectServer = new JButton("Connect");
		connectServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * Get profile and connect to IRC yo
				 */
				System.out.println("Connect");
				if(userName.getText().length() >= 1)	//Will only connect if userName written
				{
					Profile profile = getUserProfile();
					ConnectionManager cm = new ConnectionManager(profile);
					IRCEventHandler irc = new IRCEventHandler(cm.requestConnection(getChosenServer().getDns()), profile);
					
					storeProfileData(profile);
				}
			}
		});
		this.add(connectServer,gbc);
	}
	
	public Server getChosenServer() {	//Returne DNS her, kanskje port og?
		return (Server) serverBox.getSelectedItem();
	}
	
	public Profile getUserProfile() {
		String nick = this.userName.getText();
		String real = this.realName.getText();
		String alternate = this.alternativeUserName.getText();
		
		/*
		 * If no alternative nick supplied, or equals mainNick
		 * Generates alternativeNickName
		 */
		if(alternate.length() < 1 || nick.contentEquals(alternate))	
		{
			alternate = nick + "1";
		}
		
		if(real.length() < 1) 
		{
			real = "hurradurradrp";
		}
		return new Profile(real,nick, alternate, (alternate+"2"));
	}
	public Vector<Server> getServerList() {
		return serverList;
	}
	public Vector<String> getNetworkList() {
		return networkList;
	}

	
	public static void main(String Args[]) {

		/*
		 * FOR TESTING PURPOSES
		 */
		LoadServers servers = new LoadServers();
		//JFrame serverWindow = new JFrame("Pick a server")		
		
	}

}
