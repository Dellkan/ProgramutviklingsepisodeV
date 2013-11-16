package com.theforce.programutviklingsepisodeV;

import jerklib.Session;

@SuppressWarnings("serial")
public class QueryWindow extends Window {
	private Session mSession;
	private String mNickname;
	public QueryWindow(Session pSession, String pNickname) {
		super("PM: " + pNickname, true);
		this.mSession = pSession;
		this.mNickname = pNickname;
	}
	
	@Override
	public void commandParser() {
		// TODO Auto-generated method stub
		
	}
}
