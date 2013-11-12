package com.theforce.programutviklingsepisodeV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/*
 * <random string which we do not care about>
 * <timestamp>
 * [networks]
 * <String with number of network>=<name of network>
 * <0>
 * <7>
 * [Servers]
 * <networkNumber(string)>=<Continent(String)>,<countryCode(String)>,<Town+SERVER(string)>:(Fortsatt samme linje)
 * <DNS(string)>:<port-port(int)>GROUP:<network(string)>
 * 
 */
public class LoadServers {
	Vector<Server> serverList = new Vector<Server>();
	
	LoadServers() 
	{
		BufferedReader br = null;
		try {
			String sCurrentLine;
			String type = null;
			
			br = new BufferedReader(new FileReader("mIRCServers.ini"));
 
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(sCurrentLine.contains("[timestamp]")) {
					type = "timestamp";
				} else if(sCurrentLine.contains("[networks]")) {
					type = "networks";
				} else if(sCurrentLine.contains("[servers]"))	{
					type = "servers";
				} else if(type != null)	//Type is set
				{
						switch(type) 
						{
						case "timestamp":
							break;
						case "networks":
							break;
						case "servers":
							serverList.add(new Server(sCurrentLine));
							break;
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
 
	}

	public Vector<Server> getServerList() {
		return serverList;
	}
	public static void main(String Args[])
	{
		new LoadServers();
	}
}
