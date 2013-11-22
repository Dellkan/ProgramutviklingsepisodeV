package com.theforce.programutviklingsepisodeV;

import jerklib.Channel;

@SuppressWarnings("serial")
public class ChannelWindow extends Window {
	private Channel mChannel; 
	public ChannelWindow(Channel pChannel) {
		super(pChannel.getName(), true);
		this.mChannel = pChannel;
	}

	@Override
	public void commandParser() {
		if (this.mCli.getText().charAt(0) == '/') {
			//
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
	
	@Override
	protected void onClose() {
		this.mChannel.part("");
		super.onClose();
	}
}
