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
		// TODO Auto-generated method stub	
	}
}
