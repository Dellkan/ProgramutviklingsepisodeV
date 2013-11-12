package com.theforce.programutviklingsepisodeV;

import java.util.Vector;

import jerklib.Profile;

public class Server {
	/*
	 * <random string which we do not care about>
	 * <timestamp>
	 * [networks]
	 * <String with number of network>=<name of network>
	 * <0>
	 * <7>
	 * [Servers]
	 * <networkNumber(string)>=<Continent(String)>,<countryCode(String)>,<Town+SERVER(string)>:(Fortsatt samme linje)
	 * <DNS(string)>:<port-port(int)><,port(int)><,port(int)>GROUP:<network(string)>
	 * 
	 */
	
	String networkNumber;
	String location;
	String dns;
	Vector<Integer>allowedPorts = new Vector<Integer>();
	String network;	//Kan gjøres om til enum
	
	Server(String srvInfo)	{
		int currentIndex = 0;
		networkNumber = srvInfo.substring(0,currentIndex = srvInfo.indexOf("="));
		location = srvInfo.substring(currentIndex+1, currentIndex = srvInfo.indexOf(":"));
		dns = srvInfo.substring(currentIndex+1, currentIndex = srvInfo.indexOf(":", currentIndex+1));

		String portInfo = srvInfo.substring(currentIndex+1, srvInfo.indexOf("ROUP", currentIndex+1));
		System.out.print(networkNumber + " ");
		
		if(portInfo.contains("+"))
		{
			portInfo = portInfo.replace("+", "");
		}
		
		int portIndex = 4;
		do {
			if(portInfo.charAt(portIndex) == 'G')	//Gets last port.
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
			//Legg til spesialtilfelle med +
			portIndex+=5;
		}while(portIndex <= portInfo.length());	
		network = srvInfo.substring(srvInfo.lastIndexOf(":")+1, srvInfo.length());
		System.out.println(network);
	}
}
