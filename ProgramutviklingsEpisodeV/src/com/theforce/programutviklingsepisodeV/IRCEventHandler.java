package com.theforce.programutviklingsepisodeV;
import jerklib.events.*;
import jerklib.events.modes.ModeEvent;
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
				System.out.println("AWAY_EVENT");
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
				this.mWindow.appendToChat("CTCP_EVENT");
				break;
			case DCC_EVENT:
				this.mWindow.appendToChat("DCC");
				break;
			case DEFAULT: // Unused
				break;
			case ERROR:
				{
					ErrorEvent event = (ErrorEvent) pEvent;
					this.mWindow.appendToChat("Error triggered: " + event.getErrorType().toString());
				}
				break;
			case EXCEPTION: // Unused
				break;
			case INVITE_EVENT:
				System.out.println("INVITE_EVENT");
				break;
			case JOIN:
				{
					JoinEvent event = (JoinEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window != null && !window.isClosed()) {
						window.appendToChat(event.getNick() + " joined the channel");
						window.updateUserList();
					}
				}
				break;
			case JOIN_COMPLETE:
				{
					JoinCompleteEvent event = (JoinCompleteEvent) pEvent;
					Launcher.getManager().createChannelWindow(event.getChannel());
				}
				break;
			case KICK_EVENT:
				{
					KickEvent event = (KickEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window != null && !window.isClosed()) {
						window.appendToChat(event.getUserName() + " was kicked from the channel (" + event.getMessage() + ")");
						window.updateUserList();
					}
				}
				break;
			case MODE_EVENT:
				{
					ModeEvent event = (ModeEvent) pEvent;
					if (event.getModeType() == ModeEvent.ModeType.USER) {
						if (event.setBy().length() > 0) {
							this.mWindow.appendToChat(
								event.setBy() + " sets mode: " + 
								event.getModeAdjustments().toString() + " " + 
								event.getSession().getNick()
							);
						}
						
						else {
							this.mWindow.appendToChat("Server sets mode: " + event.getModeAdjustments().toString());
						}
					}
					
					else {
						ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
						if (window != null && !window.isClosed()) {
							if (event.setBy().length() > 0) {
								window.appendToChat(event.setBy() + " sets mode: " + event.getModeAdjustments().toString());
							}
							
							else {
								window.appendToChat("Server sets mode: " + event.getModeAdjustments().toString());
							}
							window.updateUserList();
						}
					}
				}
				break;
			case MOTD:
				{
					MotdEvent event = (MotdEvent) pEvent;
					this.mWindow.appendToChat(event.getMotdLine());
				}
				break;
			case NICK_CHANGE:
				{
					NickChangeEvent event = (NickChangeEvent) pEvent;
					// Update channel windows
					for (ChannelWindow window : Launcher.getManager().findChannelWindows(event.getSession())) {
						if (window.getChannel().getNicks().contains(event.getNewNick())) {
							window.appendToChat(event.getOldNick() + " changed nick to " + event.getNewNick());
							window.updateUserList();
						}
					}
						
					// Update query windows (will only work if in the same channel too)
					QueryWindow window = Launcher.getManager().findQueryWindow(event.getSession(), event.getOldNick());
					if (window != null && !window.isClosed()) {
						window.setNick(event.getNewNick());
						window.appendToChat(event.getOldNick() + " changed nick to " + event.getNewNick());
					}
					
					// If we are changing nick ourselves, update server status window
					if (event.getSession().getNick().equals(event.getNewNick())) {
						this.mWindow.appendToChat("You are now known as " + event.getNewNick());
					}
				}
				break;
			case NICK_IN_USE:
				{
					NickInUseEvent event = (NickInUseEvent) pEvent; 
					this.mWindow.appendToChat("Error: The nick \"" + event.getInUseNick() + "\" is in use already");
				}
				break;
			case NICK_LIST_EVENT:
				System.out.println("NICK_LIST_EVENT");
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
						window.updateUserList();
					}
				}
				break;
			case PRIVATE_MESSAGE:
				{
					MessageEvent event = (MessageEvent) pEvent;
					QueryWindow window = Launcher.getManager().findQueryWindow(event.getSession(), event.getNick());
					if (window == null) {
						window = Launcher.getManager().createQueryWindow(event.getSession(), event.getNick());
					}
					window.appendToChat(event.getNick() + ": " + event.getMessage());
				}
				break;
			case QUIT:
				System.out.println("QUIT");
				break;
			case SERVER_INFORMATION:
				// Do nothing for now
				break;
			case SERVER_VERSION_EVENT:
				{
					ServerVersionEvent event = (ServerVersionEvent) pEvent; 
					this.mWindow.appendToChat(event.getComment());
				}
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
				System.out.println("UPDATE_HOST_NAME");
				break;
			case WHOIS_EVENT:
				System.out.println("WHOIS_EVENT");
				break;
			case WHOWAS_EVENT:
				System.out.println("WHOWAS_EVENT");
				break;
			case WHO_EVENT:
				System.out.println("WHO_EVENT");
				break;
			default:
				break;
		}
	}

}
