package com.theforce.programutviklingsepisodeV;

import java.util.Arrays;
import java.util.List;

import jerklib.Session;

@SuppressWarnings("serial")
public class QueryWindow extends Window {
	private String mNickname;
	public QueryWindow(Session pSession, String pNickname) {
		super(pSession, "PM: " + pNickname, true);
		this.mNickname = pNickname;
	}
	
	@Override
	public void commandParser() {
		if (this.mCli.getText().charAt(0) == '/') {
			List<String> cli = Arrays.asList(this.mCli.getText().split(" "));
			switch(cli.get(0)) {
				default:
					// Not a known command for QueryWindow.. Send up in hierarchy for processing.
					super.commandParser();
			}
		}
		
		else {
			this.getSession().sayPrivate(this.getNick(), this.mCli.getText()); 
			this.appendToChat(this.getSession().getNick() + " : " + this.mCli.getText());
		}
		this.mCli.setText("");
	}
	
	public String getNick() {
		return this.mNickname;
	}
	
	@Override
	protected void onClose() {
		super.onClose();
	}
}