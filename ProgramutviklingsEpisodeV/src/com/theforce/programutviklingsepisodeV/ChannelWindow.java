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
			this.mChannel.action(this.mCli.getText().substring(1));
		}
		
		else {
			this.mChannel.say(this.mCli.getText());
		}
	}
}
