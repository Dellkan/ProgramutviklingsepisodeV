package com.theforce.programutviklingsepisodeV;
/**
 * @author Jehans Storvik
 * @author John Høegh Omdal
 * @author Martin Bragen
 */
import java.io.Serializable;

import java.util.Vector;

/**
 * A server helperclass which will read one server from a file.
 */
public class Server implements Serializable {
	private static final long serialVersionUID = 1L;
	private String networkNumber;
	private String location;
	private String dns;
	private Vector<Integer>allowedPorts = new Vector<Integer>();
	private String network;	
	
	
	Server(String srvInfo)	{
		int currentIndex = 0;
		networkNumber = srvInfo.substring(0,currentIndex = srvInfo.indexOf("="));
		location = srvInfo.substring(currentIndex+1, srvInfo.indexOf("SERVER"));
		currentIndex = srvInfo.indexOf(":");
		dns = srvInfo.substring(currentIndex+1, currentIndex = srvInfo.indexOf(":", currentIndex+1));

		String portInfo = srvInfo.substring(currentIndex+1, srvInfo.indexOf("ROUP", currentIndex+1));
		
		if(portInfo.contains("+"))	//We do not care much about SSL crypt
		{
			portInfo = portInfo.replace("+", "");
		}
		
		int portIndex = 4;
		do {
			if(portInfo.charAt(portIndex) == 'G')	
			{
				int port = Integer.parseInt(portInfo.substring(portIndex-4, portIndex));
				allowedPorts.add(port);
			}
			else if(portInfo.charAt(portIndex) == '-')
			{
				int startPort = Integer.parseInt(portInfo.substring(portIndex-4, portIndex));
				int endPort = Integer.parseInt(portInfo.substring(portIndex+1, portIndex+5));
				while(startPort <= endPort)
				{
					allowedPorts.add(startPort);
					startPort++;
				}
				portIndex+=5;
			}
			else if(portInfo.charAt(portIndex) == ',')
			{
				int port = Integer.parseInt(portInfo.substring(portIndex-4, portIndex));
				allowedPorts.add(port);
			}
			portIndex+=5;
		}while(portIndex <= portInfo.length());	
		network = srvInfo.substring(srvInfo.lastIndexOf(":")+1, srvInfo.length());
	}

	public String toString() {
		return location + " " + network + " (" + dns + ")";
	}
	public String getNetworkNumber() {
		return networkNumber;
	}

	public void setNetworkNumber(String networkNumber) {
		this.networkNumber = networkNumber;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public Vector<Integer> getAllowedPorts() {
		return allowedPorts;
	}

	public void setAllowedPorts(Vector<Integer> allowedPorts) {
		this.allowedPorts = allowedPorts;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}
}
