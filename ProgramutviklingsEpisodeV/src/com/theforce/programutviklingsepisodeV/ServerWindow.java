package com.theforce.programutviklingsepisodeV;

import jerklib.Session;

@SuppressWarnings("serial")
public class ServerWindow extends Window {
	private Session mSession;
	public ServerWindow(Session pSession) {
		super(pSession.getConnectedHostName(), false);
		this.mSession = pSession;
	}

	@Override
	public void commandParser() {
		if (this.mCli.getText().charAt(0) == '/') {
			//this.mSession.action(this.mCli.getText().substring(1));
		}
		
		else {
			this.appendToChat("Can't chat here!");
		}
	}
}
