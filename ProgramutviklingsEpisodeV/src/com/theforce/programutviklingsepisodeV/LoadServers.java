package com.theforce.programutviklingsepisodeV;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;

public class LoadServers extends JFrame implements ActionListener {
	Vector<Server> serverList;
	Vector<String> networkList; 
	JComboBox serverBox = new JComboBox();
	JComboBox networkBox;

	
	LoadServers() 
	{
		super("Select server");
		this.setLayout(new GridBagLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(300,300);
		pack();
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
								if(!networkList.contains(network)) 	//There are some networks which are not listed in the networklist
								{		//This will add them to the network vectorlist.
									System.out.println("swag");
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
		showNetworks();
		//showServers("EFnet");
	}
	

	private void setMinimumSize(int i, int j) {
		// TODO Auto-generated method stub
		
	}


	private void setPreferredSize(int i, int j) {
		// TODO Auto-generated method stub
		
	}


	public void showServers(String network) {
		serverBox.removeAllItems();
		for(int i = 0; i < serverList.size();i++)
		{
			String serverNetwork = serverList.get(i).getNetwork();
			if(serverNetwork.contentEquals(network)) {
				serverBox.addItem(serverList.get(i).getLocation() + " " + serverNetwork);
			}
		}
		serverBox.addActionListener(this);
		serverBox.setSize(100,30);
		getContentPane().add(serverBox);
		pack();
	}
	
	public void showNetworks() {
		Vector<String> listModel = new Vector<String>();
		for(int i = 0; i < networkList.size();i++)
		{
			listModel.addElement(networkList.elementAt(i));
		}
		networkBox = new JComboBox(listModel);
		networkBox.addActionListener(this);
		networkBox.setSize(100,30);
		getContentPane().add(networkBox);
		pack();
		
	}
	
	public Vector<Server> getServerList() {
		return serverList;
	}
	public Vector<String> getNetworkList() {
		return networkList;
	}
	
	
	
	
	
	public static void main(String Args[]) {

		LoadServers servers = new LoadServers();
		//JFrame serverWindow = new JFrame("Pick a server")		
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	    JComboBox box = (JComboBox) e.getSource();	
		String picked = (String) box.getSelectedItem();
		if(box == serverBox) {
			System.out.println(picked);
			/*
			 * Do weird stuff with server here I guess ?
			 */
		} else if(box == networkBox) {
			showServers(picked);
		}
	}

}
