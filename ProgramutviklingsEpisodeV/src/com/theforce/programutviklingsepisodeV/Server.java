package com.theforce.programutviklingsepisodeV;

import java.io.Serializable;

import java.util.Vector;

/**
 * @author Jehans Storvik
 * @author John Høegh Omdal
 * @author Martin Bragen
 * 
 * Receives a string which contains a single server.
 * A server helperclass.
 */
public class Server  {
	private static final long serialVersionUID = 1L;
	private String mNetworkNumber;
	private String mLocation;
	private String mDns;
	private Vector<Integer>mAllowedPorts = new Vector<Integer>();
	private String mNetwork;	
	
	
	Server(String srvInfo)	{
		int currentIndex = 0;
		
		this.mNetworkNumber = srvInfo.substring(0,currentIndex = srvInfo.indexOf("="));
		this.mLocation = srvInfo.substring(currentIndex+1, srvInfo.indexOf("SERVER"));
		currentIndex = srvInfo.indexOf(":");
		this.mDns = srvInfo.substring(currentIndex+1, currentIndex = srvInfo.indexOf(":", currentIndex+1));

		String portInfo = srvInfo.substring(currentIndex+1, srvInfo.indexOf("ROUP", currentIndex+1));
		
		if(portInfo.contains("+"))
		{
			portInfo = portInfo.replace("+", "");
		}
		
		int portIndex = 4;
		do {
			if(portInfo.charAt(portIndex) == 'G')	
			{
				int port = Integer.parseInt(portInfo.substring(portIndex-4, portIndex));
				this.mAllowedPorts.add(port);
			}
			else if(portInfo.charAt(portIndex) == '-')
			{
				int startPort = Integer.parseInt(portInfo.substring(portIndex-4, portIndex));
				int endPort = Integer.parseInt(portInfo.substring(portIndex+1, portIndex+5));
				while(startPort <= endPort)
				{
					this.mAllowedPorts.add(startPort);
					startPort++;
				}
				portIndex+=5;
			}
			else if(portInfo.charAt(portIndex) == ',')
			{
				int port = Integer.parseInt(portInfo.substring(portIndex-4, portIndex));
				this.mAllowedPorts.add(port);
			}
			portIndex+=5;
		}while(portIndex <= portInfo.length());	
		this.mNetwork = srvInfo.substring(srvInfo.lastIndexOf(":")+1, srvInfo.length());
	}

	 /**
	  * Gets information about a server
	  * 
	  * @return Describes the server with location, network and dns
	  * 
	  */
	public String toString() {
		return this.mLocation + " " + this.mNetwork + " (" + this.mDns + ")";
	}


	/**
	 * @return dns
	 */
	public String getDns() {
		return this.mDns;
	}


	/**
	 * 
	 * @return a list over the set ports for a server
	 */
	public Vector<Integer> getAllowedPorts() {
		return this.mAllowedPorts;
	}

	/**
	 * 
	 * @return returns which network a server belongs to
	 */
	public String getNetwork() {
		return this.mNetwork;
	}
}
