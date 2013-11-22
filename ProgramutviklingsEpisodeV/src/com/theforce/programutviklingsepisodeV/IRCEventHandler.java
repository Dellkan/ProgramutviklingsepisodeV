package com.theforce.programutviklingsepisodeV;
import jerklib.events.*;
import jerklib.listeners.IRCEventListener;

/**
 * 
 * @author Hans Martin Bragen
 * @author John Høeg-Omdal
 * @author Jehans JR Storvik
 *
 */

public class IRCEventHandler implements IRCEventListener{
	private ServerWindow mWindow;
	
	public IRCEventHandler(ServerWindow pWindow) {
		this.mWindow = pWindow;
	}

	/**
	 * Handles Handles different events received through the IRC session
	 */
	@Override
	public void receiveEvent(IRCEvent pEvent) {
		switch(pEvent.getType()) {
			case AWAY_EVENT:
				break;
			case CHANNEL_LIST_EVENT:
				break;
			case CHANNEL_MESSAGE:
				{
					MessageEvent event = (MessageEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window == null) {
						window = Launcher.getManager().createChannelWindow(event.getChannel());
					}
					window.appendToChat(event.getNick() + ": " + event.getMessage());
				}
				break;
			case CONNECTION_LOST:
				this.mWindow.appendToChat("Connection lost!");
				break;
			case CONNECT_COMPLETE:
				this.mWindow.setTitle(pEvent.getSession().getConnectedHostName());
				break;
			case CTCP_EVENT:
				break;
			case DCC_EVENT:
				break;
			case DEFAULT: // Unused
				break;
			case ERROR:
				this.mWindow.appendToChat("Error event triggered!");
				break;
			case EXCEPTION: // Unused
				break;
			case INVITE_EVENT:
				break;
			case JOIN:
				this.mWindow.appendToChat("Someone joined...");
				break;
			case JOIN_COMPLETE:
				{
					JoinCompleteEvent event = (JoinCompleteEvent) pEvent;
					Launcher.getManager().createChannelWindow(event.getChannel());
					this.mWindow.getSession();
				}
				break;
			case KICK_EVENT:
				{
					KickEvent event = (KickEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window != null && !window.isClosed()) {
						window.appendToChat(event.getUserName() + " was kicked from the channel (" + event.getMessage() + ")");
					}
				}
				break;
			case MODE_EVENT:
				break;
			case MOTD:
				{
					MotdEvent event = (MotdEvent) pEvent;
					this.mWindow.appendToChat(event.getMotdLine());
				}
				break;
			case NICK_CHANGE:
				break;
			case NICK_IN_USE:
				{
					NickInUseEvent event = (NickInUseEvent) pEvent; 
					this.mWindow.appendToChat("Error: The nick \"" + event.getInUseNick() + "\" is in use already");
				}
				break;
			case NICK_LIST_EVENT:
				break;
			case NOTICE:
				{
					NoticeEvent event = (NoticeEvent) pEvent;
					if (event.getChannel() == null) { 
						this.mWindow.appendToChat(event.getNoticeMessage());
					}
				}
				break;
			case PART:
				{
					PartEvent event = (PartEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window != null && !window.isClosed()) {
						window.appendToChat(event.getUserName() + " left the channel (" + event.getPartMessage() + ")");
					}
				}
				break;
			case PRIVATE_MESSAGE:
				{
					MessageEvent event = (MessageEvent) pEvent;
					QueryWindow window = Launcher.getManager().createQueryWindow(event.getSession(), event.getNick());
					if (window == null) {
						window = Launcher.getManager().createQueryWindow(event.getSession(), event.getNick());
					}
					window.appendToChat(event.getNick() + ": " + event.getMessage());
				}
				break;
			case QUIT:
				break;
			case SERVER_INFORMATION:
				break;
			case SERVER_VERSION_EVENT:
				break;
			case TOPIC:
				{
					TopicEvent event = (TopicEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window != null) {
						window.appendToChat(event.getSetBy() + " - " + event.getSetWhen() + " : " + event.getTopic());
					}
				}
				break;
			case UPDATE_HOST_NAME:
				break;
			case WHOIS_EVENT:
				break;
			case WHOWAS_EVENT:
				break;
			case WHO_EVENT:
				break;
			default:
				break;
		}
	}

}
