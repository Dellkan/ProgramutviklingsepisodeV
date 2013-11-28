package com.theforce.programutviklingsepisodeV;

import java.util.List;

import jerklib.Session;

@SuppressWarnings("serial")
public class ServerWindow extends Window {
	public ServerWindow(Session pSession) {
		super(pSession, pSession.getRequestedConnection().getHostName() + " (Connecting...)", false);
		
		// Attach event handler to session
		this.getSession().addIRCEventListener(new IRCEventHandler(this));
		
		// Give user some feedback
		this.appendToChat(new ChatText().addSystemMessage("Connecting to " + pSession.getRequestedConnection().getHostName() + "..."));
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
