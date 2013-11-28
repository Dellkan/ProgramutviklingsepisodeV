package com.theforce.programutviklingsepisodeV;

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
	
	public Channel getChannel() {
		return this.mChannel;
	}
	
	public void updateUserList() {
		((UserList) this.mUsers.getModel()).update();
	}
	
	@Override
	protected void onClose() {
		if (this.getSession().getChannels().contains(this.getChannel())) {
			this.getChannel().part("Leaving");
		}
		super.onClose();
	}
	
	public void say(String pText) {
		this.getChannel().say(pText);
		this.appendToChat(new ChatText() // TODO: Check that message is sent
			.addNickname(this.getSession().getNick())
			.addText(": " + pText)
		);
	}
}
