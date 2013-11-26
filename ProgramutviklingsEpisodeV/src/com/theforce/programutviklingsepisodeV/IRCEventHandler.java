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
			case AWAY_EVENT: // TODO
				System.out.println("AWAY_EVENT");
				break;
			case CHANNEL_LIST_EVENT: // TODO
				break;
			case CHANNEL_MESSAGE:
				{
					MessageEvent event = (MessageEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window == null) {
						window = Launcher.getManager().createChannelWindow(event.getChannel());
					}
					
					// Create output
					ChatText output = new ChatText();
					output.addNickname(event.getNick());
					output.addText(event.getMessage());
					
					// Trigger output
					window.appendToChat(output);
				}
				break;
			case CONNECTION_LOST: // TODO
				this.mWindow.appendToChat(new ChatText().addError("Connection lost!"));
				break;
			case CONNECT_COMPLETE:
				this.mWindow.setTitle(pEvent.getSession().getConnectedHostName());
				break;
			case CTCP_EVENT: // TODO
				this.mWindow.appendToChat(new ChatText().addNotice("CTCP_EVENT"));
				break;
			case DEFAULT: // Unused
				break;
			case ERROR: // TODO
				{
					switch(((ErrorEvent) pEvent).getErrorType()) {
						case NUMERIC_ERROR:
							{
								NumericErrorEvent error = (NumericErrorEvent) pEvent;
								if (error.command().equals("475")) { // Manual work-around for +k passworded channel error message, since jerklib borked it up
									this.mWindow.appendToChat(new ChatText().addError(error.arg(1) + ": " + error.arg(2)));
								}
								
								else {
									this.mWindow.appendToChat(new ChatText().addError(error.getErrorMsg()));
								}
							}
							break;
						case UNRESOLVED_HOSTNAME:
							{
								UnresolvedHostnameErrorEvent error = (UnresolvedHostnameErrorEvent) pEvent;
								this.mWindow.appendToChat(new ChatText()
									.addError(error.getException()
									.getMessage() + "[" + error.getHostName() + "]")
								);
							}
							break;
						case GENERIC:
						default:
							{
								GenericErrorEvent error = (GenericErrorEvent) pEvent;
								this.mWindow.appendToChat(new ChatText().addError(error.getException().getMessage()));
							}
							break;
						}
				}
				break;
			case EXCEPTION: // Unused
				break;
			case INVITE_EVENT:
				{
					InviteEvent event = (InviteEvent) pEvent;
					
					ChatText output = new ChatText()
						.addNickname(event.getNick())
						// Jerklib is bugged. event.getChannelName() returns event.getUserName(). Replace with arg(1).
						.addNotice(" invited you to join the " + event.arg(1) + " channel ")
						.addInviteAccept();
					
					// First send a message in server window
					
					this.mWindow.appendToChat(output);
					
					// Then into a query window
					QueryWindow window = Launcher.getManager().findQueryWindow(event.getSession(), event.getNick());
					if (window == null) {
						window = Launcher.getManager().createQueryWindow(event.getSession(), event.getNick());
					}
					window.appendToChat(output);
				}
				break;
			case JOIN:
				{
					JoinEvent event = (JoinEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window != null && !window.isClosed()) {
						// Trigger output
						window.appendToChat(new ChatText()
							.addNickname(event.getNick())
							.addSystemMessage(" joined the channel")
						);
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
						// Trigger output
						window.appendToChat(new ChatText()
							.addNickname(event.getNick())
							.addSystemMessage(" was kicked from the channel (" + event.getMessage() + ")")
						);
						window.updateUserList();
					}
				}
				break;
			case MODE_EVENT:
				{
					ModeEvent event = (ModeEvent) pEvent;
					
					// Create output
					ChatText output = new ChatText();
					
					// Find sender
					if (event.setBy().length() > 0) {
						output.addNickname(event.getNick());
					}
					
					else {
						output.addSystemMessage("System");
					}
					
					// Format according to target
					if (event.getModeType() == ModeEvent.ModeType.USER) {
						output.addSystemMessage(" sets mode: " + event.getModeAdjustments().toString() + " " + event.getSession().getNick());
						this.mWindow.appendToChat(output);
					}
					
					else {
						ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
						if (window != null && !window.isClosed()) {
							output.addSystemMessage(" sets mode: " + event.getModeAdjustments().toString());
							window.appendToChat(output);
							window.updateUserList();
						}
					}
				}
				break;
			case MOTD:
				{
					MotdEvent event = (MotdEvent) pEvent;
					this.mWindow.appendToChat(new ChatText().addMOTDLine(event.getMotdLine()));
				}
				break;
			case NICK_CHANGE:
				{
					NickChangeEvent event = (NickChangeEvent) pEvent;
					
					// Create output
					ChatText output = new ChatText()
						.addNickname(event.getOldNick())
						.addSystemMessage(" changed nick to ")
						.addNickname(event.getNewNick());
					
					// Update channel windows
					for (ChannelWindow window : Launcher.getManager().findChannelWindows(event.getSession())) {
						if (window.getChannel().getNicks().contains(event.getNewNick())) {
							window.appendToChat(output);
							window.updateUserList();
						}
					}
						
					// Update query windows (will only work if in the same channel too)
					QueryWindow window = Launcher.getManager().findQueryWindow(event.getSession(), event.getOldNick());
					if (window != null && !window.isClosed()) {
						window.setNick(event.getNewNick());
						window.appendToChat(output);
					}
					
					// If we are changing nick ourselves, update server status window too
					if (event.getSession().getNick().equals(event.getNewNick())) {
						this.mWindow.appendToChat(new ChatText().addSystemMessage("You are now known as ").addNickname(event.getNewNick()));
					}
				}
				break;
			case NICK_IN_USE:
				{
					NickInUseEvent event = (NickInUseEvent) pEvent; 
					this.mWindow.appendToChat(new ChatText()
						.addError("Error: The nick ")
						.addNickname(event.getInUseNick())
						.addError(" is in use already")
					);
				}
				break;
			case NICK_LIST_EVENT:
				System.out.println("NICK_LIST_EVENT");
				break;
			case NOTICE:
				{
					NoticeEvent event = (NoticeEvent) pEvent;
					if (event.getChannel() == null) { 
						this.mWindow.appendToChat(new ChatText()
							.addNickname(event.getNick())
							.addNotice(event.getNoticeMessage())
						);
					}
					
					else {
						ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
						if (window != null && !window.isClosed()) {
							window.appendToChat(new ChatText()
								.addNickname(event.getNick())
								.addNotice(event.getNoticeMessage())
							);
						}
					}
				}
				break;
			case PART:
				{
					PartEvent event = (PartEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window != null && !window.isClosed()) {
						window.appendToChat(new ChatText()
							.addNickname(event.getNick())
							.addSystemMessage(" left the channel (" + event.getPartMessage() + ")")
						);
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
					window.appendToChat(new ChatText()
						.addNickname(event.getNick())
						.addText(event.getMessage())
					);
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
					this.mWindow.appendToChat(new ChatText().addSystemMessage(event.getComment()));
				}
				break;
			case TOPIC:
				{
					TopicEvent event = (TopicEvent) pEvent;
					ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
					if (window != null) {
						window.appendToChat(new ChatText()
							.addMOTDLine("Topic set by ")
							.addNickname(event.getSetBy())
							.addMOTDLine(" - " + event.getSetWhen() + " : " + event.getTopic())
						);
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
