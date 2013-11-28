package com.theforce.programutviklingsepisodeV;

import java.util.List;

import jerklib.Session;
/**
 * Parent class.
 * @author Hans Martin Bragen
 * @author Jehans Jr. Storvik
 * @author John Høegh-Omdal
 *
 */
@SuppressWarnings("serial")
public class ServerWindow extends Window {
	/**
	 * adds an event listener to the connect to the server
	 * @param pSession the session of the server connection.
	 */
	public ServerWindow(Session pSession) {
		super(pSession, pSession.getRequestedConnection().getHostName() + " (Connecting...)", false);
		
		// Attach event handler to session
		this.getSession().addIRCEventListener(new IRCEventHandler(this));
		
		// Give user some feedback
		this.appendToChat(new ChatText().addSystemMessage("Connecting to " + pSession.getRequestedConnection().getHostName() + "..."));
	}
	/**
	 * Disposes itself from the channel managers list of open windows before it dies.
	 */
	@Override
	protected void onClose() {
		List<ChannelWindow> channels = Launcher.getManager().findChannelWindows(this.getSession());
		for (ChannelWindow window : channels) {
			window.dispose();
		}
		ServerWindow.this.getSession().close("herp");
		super.onClose();
	}
}
