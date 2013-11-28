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
		if (Launcher.getManager().getDebugWindow() != null) {
			Launcher.getManager().getDebugWindow().appendToChat(new ChatText()
				.addSystemMessage("Event [" + pEvent.getType() + "] " + pEvent.getRawEventData())
			);
		}
		switch(pEvent.getType()) {
			case AWAY_EVENT: // TODO
				{
					AwayEvent event = (AwayEvent) pEvent;
					if (!event.isYou()) {
						QueryWindow window = Launcher.getManager().findQueryWindow(event.getSession(), event.getNick());
						if (window != null && !window.isClosed()) {
							window.appendToChat(new ChatText().addNickname(event.getNick()).addSystemMessage(" is away: " + event.getAwayMessage()));
						}
					}
					
					else { // What to do?
						
					}
				}
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
			case CONNECTION_LOST:
				{
					ConnectionLostEvent event = (ConnectionLostEvent) pEvent;
					this.mWindow.appendToChat(new ChatText().addError("Connection lost! (" + event.getException().getMessage() + ")"));
					this.mWindow.setTitle(pEvent.getSession().getRequestedConnection().getHostName() + " (Disconnected)");
				}
				break;
			case CONNECTION_RETRY:
				{
					ConnectionRetryEvent event = (ConnectionRetryEvent) pEvent; 
					this.mWindow.appendToChat(new ChatText().addError(event.getMessage()));
					this.mWindow.setTitle(pEvent.getSession().getRequestedConnection().getHostName() + " (Connecting...)");
				}
				break;
			case CONNECT_COMPLETE:
				this.mWindow.setTitle(pEvent.getSession().getConnectedHostName());
				break;
			case CTCP_EVENT: // TODO
				{
					CtcpEvent event = (CtcpEvent) pEvent;
					if (event.getCtcpString().startsWith("ACTION ")) { // Action goes here
						if (event.getChannel() != null) {
							ChannelWindow window = Launcher.getManager().findChannelWindow(event.getChannel());
							if (window != null && !window.isClosed()) {
								window.appendToChat(new ChatText()
									.addAction(" - ")
									.addNickname(event.getNick())
									.addAction(event.getCtcpString().substring(7))
								);
							}
						}
						
						else {
							QueryWindow window = Launcher.getManager().findQueryWindow(this.mWindow.getSession(), event.getNick());
							if (window != null && !window.isClosed()) {
								window.appendToChat(new ChatText()
									.addAction(" - ")
									.addNickname(event.getNick())
									.addAction(event.getCtcpString().substring(7))
								);
							}							
						}
					}
					
					else { // Unhandled for now TODO
					}
				}
				break;
			case DEFAULT: // TODO - All unhandled messages go here
				{
					int command = 0;
					try {
						command = Integer.parseInt(pEvent.command());
					} catch(Exception e) {}
					
					if ((command >= 250 && command <= 255) || command == 265 || command == 266) { // LUSER
						ChatText buffer = new ChatText().addNotice(pEvent.arg(1));
						if (pEvent.arg(2) != null) { buffer.addNotice(" " + pEvent.arg(2)); }
						this.mWindow.appendToChat(buffer);
					}
					
					else if (command == 303) { // Is on (/ison)
						this.mWindow.appendToChat(new ChatText().addSystemMessage("Is on: " + pEvent.arg(1)));
					}
					
					else if (command == 311) { // whois
						this.mWindow.appendToChat(new ChatText()
							.addNickname(pEvent.arg(1))
							.addSystemMessage(" is " + pEvent.arg(2) + "@" + pEvent.arg(3) + " " + pEvent.arg(4) + " " + pEvent.arg(5))
						);
					}
					
					else if (command == 312) { // whois part two (server stuff)
						this.mWindow.appendToChat(new ChatText()
							.addNickname(pEvent.arg(1))
							.addSystemMessage(" is using" + pEvent.arg(2) + " " + pEvent.arg(3))
						);
					}
					
					else if (command == 317) { // whois part three (idle time)
						this.mWindow.appendToChat(new ChatText().addSystemMessage(pEvent.arg(1)));
					}
					
					else if (command == 319) { // whois part four (Channels)
						this.mWindow.appendToChat(new ChatText()
							.addNickname(pEvent.arg(1))
							.addSystemMessage(" is on " + pEvent.arg(2))
						);
					}
					
					else if (command == 369) { // who was end
						this.mWindow.appendToChat(new ChatText().addSystemMessage(pEvent.arg(2)));
					}
					
					else if (command == 371 || command == 374) { // ircd info (/info)
						this.mWindow.appendToChat(new ChatText().addSystemMessage(pEvent.arg(1)));
					}
					
					else if (command == 391) { // Time (/time)
						this.mWindow.appendToChat(new ChatText().addSystemMessage(pEvent.arg(4)));
					}
					
					else if (pEvent.command().equals("ERROR")) {
						this.mWindow.appendToChat(new ChatText().addSystemMessage(pEvent.arg(1)));
					}
					
					// If all else fails.. Dump the message in, so the user doesn't lose it, at least.
					else if (!pEvent.command().equals("PING") && !pEvent.command().equals("PONG")) {
						//this.mWindow.appendToChat(new ChatText().addSystemMessage(pEvent.arg(1)));
					}
				}
				break;
			case ERROR:
				{
					switch(((ErrorEvent) pEvent).getErrorType()) {
						case NUMERIC_ERROR:
							{
								NumericErrorEvent error = (NumericErrorEvent) pEvent;
								switch(error.command()) { // Unparsed by jerklib. Handle manually where needed
									case "401": // User not found
										{
											QueryWindow window = Launcher.getManager().findQueryWindow(error.getSession(), error.arg(1));
											if (window != null && !window.isClosed()) {
												window.appendToChat(new ChatText().addError("Error: " + error.arg(1) + ": " + error.arg(2)));
											}
										}
										break;
									case "404": // Cannot send to channel
										break;
									case "464": // Wrong password for channel
										break;
									case "465": // You are banned
										break;
									case "471": // Channel is full
										break;
									case "472": // Mode unknown
										break;
									case "475": // Channel requires password (+k)
										break;
									case "482": // Command requires +o (oper status)
										break;
									case "483": // Attempted to kill a server. Denied.
										break;
								}
								// Send error message to server console
								this.mWindow.appendToChat(new ChatText().addError("Error: " + error.arg(1) + ": " + error.arg(2)));
								
								// If detected as an error for a channel, try to find channel window and send the error there too
								if (error.getSession().isChannelToken(error.arg(1))) {
									ChannelWindow window = Launcher.getManager().findChannelWindow(
											error.getSession().getChannel(error.arg(1))
										);
									if (window != null && !window.isClosed()) {
										window.appendToChat(new ChatText().addError("Error: " + error.arg(2)));
									}
								}
							}
							break;
						case UNRESOLVED_HOSTNAME:
							{
								UnresolvedHostnameErrorEvent error = (UnresolvedHostnameErrorEvent) pEvent;
								this.mWindow.appendToChat(new ChatText()
									.addError("Unable to connect to " + "[" + error.getHostName() + "]. Perhaps the server isn't there anymore? ")
									.addError("(" + (error.getException().getMessage() != null ? error.getException().getMessage() : "No further information") + ")")
								);
								this.mWindow.setTitle(pEvent.getSession().getRequestedConnection().getHostName() + " (Disconnected)");
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
							.addNickname(event.getWho())
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
				//System.out.println("UPDATE_HOST_NAME");
				break;
			case WHOIS_EVENT:
				//System.out.println("WHOIS_EVENT");
				break;
			case WHOWAS_EVENT:
				{
					WhowasEvent event = (WhowasEvent) pEvent;
					this.mWindow.appendToChat(new ChatText()
						.addNickname(event.getNick())
						.addSystemMessage(" was " + event.getUserName())
					);
				}
				break;
			case WHO_EVENT:
				//System.out.println("WHO_EVENT");
				break;
			default:
				break;
		}
	}

}
