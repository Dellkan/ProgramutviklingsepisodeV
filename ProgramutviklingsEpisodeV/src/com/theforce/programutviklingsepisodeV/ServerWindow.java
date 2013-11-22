package com.theforce.programutviklingsepisodeV;

import java.util.Arrays;
import java.util.List;

import jerklib.Session;

@SuppressWarnings("serial")
public class ServerWindow extends Window {
	private Session mSession;
	public ServerWindow(Session pSession) {
		super(pSession.getRequestedConnection().getHostName() + " (connecting...)", false);

		// Attach session to window
		this.mSession = pSession;
		
		// Attach event handler to session
		this.mSession.addIRCEventListener(new IRCEventHandler(this));
		
		// Give user some feedback
		this.appendToChat("Connecting to " + pSession.getRequestedConnection().getHostName() + "...");
	}

	@Override
	public void commandParser() {
		if (this.mCli.getText().charAt(0) == '/') {
			try {
				List<String> cli = Arrays.asList(this.mCli.getText().split(" "));
				if (!cli.isEmpty()) {
					if (cli.get(0).startsWith("/join")) {
						String channelName = cli.get(1);
						this.mSession.join(channelName);
					}
				}
			}
			
			catch (Exception e) { }
		}
		
		else {
			this.appendToChat("Can't chat here!");
		}
		this.mCli.setText("");
	}
	
	public Session getSession() {
		return this.mSession;
	}
	
	@Override
	protected void onClose() {
		List<ChannelWindow> channels = Launcher.getManager().findChannelWindows(this.mSession);
		for (ChannelWindow window : channels) {
			window.dispose();
		}
		ServerWindow.this.mSession.close("herp");
		super.onClose();
	}
}
