package com.theforce.programutviklingsepisodeV;

import java.util.List;

import jerklib.Session;

@SuppressWarnings("serial")
public class ServerWindow extends Window {
	public ServerWindow(Session pSession) {
		super(pSession, pSession.getRequestedConnection().getHostName() + " (connecting...)", false);
		
		// Attach event handler to session
		this.getSession().addIRCEventListener(new IRCEventHandler(this));
		
		// Give user some feedback
		this.appendToChat("Connecting to " + pSession.getRequestedConnection().getHostName() + "...");
	}
	
	@Override
	protected void commandParser() {
		// Only override for clarity and possible expansion.
		super.commandParser();
	}
	
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
