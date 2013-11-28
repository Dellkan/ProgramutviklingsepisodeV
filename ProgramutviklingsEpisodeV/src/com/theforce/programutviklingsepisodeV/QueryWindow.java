package com.theforce.programutviklingsepisodeV;

import jerklib.Session;

@SuppressWarnings("serial")
public class QueryWindow extends Window {
	private String mNickname;
	public QueryWindow(Session pSession, String pNickname) {
		super(pSession, "PM: " + pNickname, true);
		this.mNickname = pNickname;
	}
	
	public String getNick() {
		return this.mNickname;
	}
	
	public void setNick(String pNick) {
		this.mNickname = pNick;
		this.setTitle("PM: " + pNick);
	}
	
	@Override
	protected void onClose() {
		super.onClose();
	}
	

	public void say(String pText) {
		this.getSession().sayPrivate(this.getNick(), pText); 
		this.appendToChat(new ChatText() // TODO: Check that message is sent
			.addNickname(this.getSession().getNick())
			.addText(": " + pText)
		);
	}
}