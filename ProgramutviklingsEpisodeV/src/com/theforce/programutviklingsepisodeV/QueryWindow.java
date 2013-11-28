package com.theforce.programutviklingsepisodeV;

import jerklib.Session;

/**
 * a class specific for querying other users on the IRC server.
 * Makes it own window with a connection to the queried user.
 * Extends window.
 * @author Martin
 * @author Jehans
 * @author John
 */
@SuppressWarnings("serial")
public class QueryWindow extends Window {
	private String mNickname;
	public QueryWindow(Session pSession, String pNickname) {
		super(pSession, "PM: " + pNickname, true);
		this.mNickname = pNickname;
	}
	
	/**
	 * 
	 * @return The queryed users nickname.
	 */
	public String getNick() {
		return this.mNickname;
	}
	
	/**
	 * Sets the name for the window.
	 * @param pNick the name of the window.
	 */
	public void setNick(String pNick) {
		this.mNickname = pNick;
		this.setTitle("PM: " + pNick);
	}
	
	/**
	 * Closes the window.
	 */
	@Override
	protected void onClose() {
		super.onClose();
	}
	

	/**
	 * Writes text to the queryed user.
	 * @param String pText Is the text to be said to a user.
	 */
	public void say(String pText) {
		this.getSession().sayPrivate(this.getNick(), pText); 
		this.appendToChat(new ChatText() // TODO: Check that message is sent
			.addNickname(this.getSession().getNick())
			.addText(": " + pText)
		);
	}
}