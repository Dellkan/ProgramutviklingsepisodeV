package com.theforce.programutviklingsepisodeV;

import java.util.Arrays;
import java.util.List;

import jerklib.Channel;

@SuppressWarnings("serial")
public class ChannelWindow extends Window {
	private Channel mChannel; 
	@SuppressWarnings("unchecked")
	public ChannelWindow(Channel pChannel) {
		super(pChannel.getSession(), pChannel.getName(), true);
		this.mChannel = pChannel;
		
		// Set up user stuff
		this.mUsers.setModel(new UserList(this.getChannel()));
	}

	@Override
	protected void commandParser() {
		if (this.mCli.getText().charAt(0) == '/') {
			List<String> cli = Arrays.asList(this.mCli.getText().split(" "));
			switch (cli.get(0)) {
				case "/me":
					// Do "/me" stuff
					this.appendToChat("JEHANS R RETARD");
				default:
					// Not a known command for ChannelWindow.. Send up in hierarchy for processing.
					super.commandParser();
			}
		}
		
		else {
			this.mChannel.say(this.mCli.getText());
			this.appendToChat(this.mChannel.getSession().getNick() + " : " + this.mCli.getText());
		}
		this.mCli.setText("");
	}
	
	public Channel getChannel() {
		return this.mChannel;
	}
	
	public void updateUserList() {
		((UserList) this.mUsers.getModel()).update();
	}
	
	@Override
	protected void onClose() {
		this.mChannel.part("");
		super.onClose();
	}
}
