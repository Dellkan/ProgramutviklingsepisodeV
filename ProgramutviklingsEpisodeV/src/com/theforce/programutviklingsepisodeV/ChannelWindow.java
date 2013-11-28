package com.theforce.programutviklingsepisodeV;

import jerklib.Channel;

/**
 * This class creates the window specific for a connected channel
 * <br> and handles the functions that are channel only.  Extends the window class.
 * @author Martin
 * @author Jehans
 * @author John
 *

 */
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
	
	/**
	 * Gets the channel that this window is connected to.
	 * @return a channel object.
	 */
	public Channel getChannel() {
		return this.mChannel;
	}
	
	/**
	 * Updates the userlist in the channel window.
	 */
	public void updateUserList() {
		((UserList) this.mUsers.getModel()).update();
	}
	
	/**
	 * This function closes the connection to a channel and sends 
	 * <br> a /quit message to the server.
	 */
	@Override
	protected void onClose() {
		if (this.getSession().getChannels().contains(this.getChannel())) {
			this.getChannel().part("Leaving");
		}
		super.onClose();
	}
	
	/**
	 * A function to send text to the channel in this window.
	 * @param String pText is the text to be said in the channel.
	 */
	public void say(String pText) {
		this.getChannel().say(pText);
		this.appendToChat(new ChatText() // TODO: Check that message is sent
			.addNickname(this.getSession().getNick())
			.addText(": " + pText)
		);
	}
}
