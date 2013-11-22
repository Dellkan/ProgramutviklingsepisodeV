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
		if (this.mCli.getText().charAt(0) == '/') {
			//
		}
		
		else {
			this.mSession.sayPrivate(this.getNick(), this.mCli.getText()); 
			this.appendToChat(this.getSession().getNick() + " : " + this.mCli.getText());
		}
		this.mCli.setText("");
	}
	
	public String getNick() {
		return this.mNickname;
	}
	
	public Session getSession() {
		return this.mSession;
	}
	
	@Override
	protected void onClose() {
		super.onClose();
	}
}