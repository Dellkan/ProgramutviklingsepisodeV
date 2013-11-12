package com.theforce.programutviklingsepisodeV;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;

/**
 * 
 * @author Hans Martin Bragen
 * @author John Høeg-Omdal
 * @author Jehans JR Storvik
 *
 */
public class ConnectManager {

	Session session;
	ConnectionManager conManager;
	
	public ConnectManager(String serverName, Profile profile)
	{
		/**
		 * EMPTY CONSTRUCTOR
		 */
		
	}
	/**
	 * Opens a connection to an IRC server. And initiate an event handler.
	 * 
	 * @param serverName The name of the serverer to connect to.
	 * @param profile The profile used to connect to the server.
	 */
	public void addConnection(String serverName,Profile profile)
	{
		conManager = new ConnectionManager(profile);
		session = conManager.requestConnection(serverName);
		new IRCEventHandler(session, profile);
	}
	
	/**
	 *  Terminates the IRC connection.
	 *  @param exitMessage - Message displayed on server exit.
	 *  @return returns true if the connection was terminated, else if the connection is still active
	 **/
	public boolean killSession(String exitMessage)
	{
		session.close(exitMessage);
		
		return (!(session.isConnected()));
	}
	
}
