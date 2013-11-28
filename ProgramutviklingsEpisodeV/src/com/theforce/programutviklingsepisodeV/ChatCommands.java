package com.theforce.programutviklingsepisodeV;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jerklib.Channel;

/**
 * Maintains an interface towards chat commands, making it possible to retrieve and add commands.
 * @author John
 * @author Jehans
 * @author Martin
 *
 */
class ChatCommands {
	private static Map<String, Command> mCommands = new HashMap<String, Command>();
	
	/**
	 * Attempts to parse a command. If it finds a local match for the command, executes the local
	 * action. Otherwise sends the command in raw format to the server.
	 * 	
	 * @param pCli
	 * @param pWindow 
	 */
	public static void parseCommand(List<String> pCli, Window pWindow) {
		Command command = mCommands.get(pCli.get(0));
		if (command != null && command.getAction() != null) {
			boolean success = false;
			try {
				success = command.Run(pCli, pWindow);
			} catch(Exception e) {}
			finally {
				if (!success) {
					pWindow.appendToChat(new ChatText().addHelp(command.getCommand() + " : " + command.getHelp()));
				}
			}
		}
		
		else {
			pWindow.getSession().sayRaw(pWindow.getCommandLine().substring(1));
		}
	}
	
	/**
	 * Creates a new chat command, and adds it to the global list.
	 * /Help, autocomplete, and other command related functions will automatically
	 * notice the new command.
	 * 
	 * @param pCommand
	 */
	public static void createCommand(Command pCommand) {
		ChatCommands.mCommands.put(pCommand.getCommand(), pCommand);
	}
	
	/**
	 * Fetches the global command list. Do not edit this directly.
	 * 
	 * @return Hashmap of commands
	 */
	public static Map<String, Command> getCommands() {
		return mCommands;
	}
	
	/**
	 * Adds our commands.
	 */
	static {
		// / TODO bugged
		ChatCommands.createCommand(new Command("/", "Recalls the last message you typed.", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				List<String> cliHistory = pWindow.getCliHistory();
				pWindow.setCommandLine(cliHistory.get(cliHistory.size()-1)); // The / got logged too, so we're guaranteed at least one history
				return true;
			}
		}));
		

		ChatCommands.createCommand(new Command("/!", "Recalls the last message you typed in any window.", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				Window lastEdited = pWindow;
				for (Window window : Launcher.getManager().getAllWindows()) {
					if (lastEdited.getTimestamp() < window.getTimestamp()) {
						lastEdited = window;
					}
				}
				List<String> cliHistory = lastEdited.getCliHistory();
				if (cliHistory.size() > 0) {
					pWindow.setCommandLine(cliHistory.get(cliHistory.size()-1));
				}
				return true;
			}
		}));
		
		// action
		ChatCommands.createCommand(new Command("/action", "Same as /me. /action <message>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				if (pWindow instanceof ChannelWindow) {
					String msg = pWindow.getCommandLine().split(" ", 2)[1];
					pWindow.getSession().action(((ChannelWindow)pWindow).getChannel().getName(), msg);
					pWindow.appendToChat(new ChatText()
						.addAction(" -> ")
						.addNickname(pWindow.getSession().getNick())
						.addAction(msg)
					);
				}
				
				else if (pWindow instanceof QueryWindow) {
					String msg = pWindow.getCommandLine().split(" ", 2)[1];
					pWindow.getSession().action(((QueryWindow)pWindow).getNick(), msg);
					pWindow.appendToChat(new ChatText()
						.addAction(" -> ")
						.addNickname(pWindow.getSession().getNick())
						.addAction(msg)
					);
				}
				
				else {
					pWindow.appendToChat(new ChatText().addError("/me doesn't work in this window."));
					return false;
				}
				return true;
			}
		}));
		
		// amsg
		ChatCommands.createCommand(new Command("/amsg", "Sends the specified message to all open channels. /amsg <message>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				String msg = pWindow.getCommandLine().split(" ", 2)[1];
				for (Channel channel : pWindow.getSession().getChannels()) {
					channel.say(msg);
				}
				return true;
			}
		}));
		
		// away
		ChatCommands.createCommand(new Command("/away", 
				"Mark yourself as away with the specified message. Run command without message to unset away. /away [<message>]", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				List<String> msg = Arrays.asList(pWindow.getCommandLine().split(" ", 2));
				if (msg.size() > 1) {
					pWindow.getSession().setAway(msg.get(1));
				}
				
				else {
					if (pWindow.getSession().isAway()) {
						pWindow.getSession().unsetAway();
					}
					
					else {
						pWindow.getSession().setAway("");
					}
				}
				return true;
			}
		}));
		
		// clear
		ChatCommands.createCommand(new Command("/clear", "Clear window. /clear", new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				pWindow.clear();
				return true;
			}
		}));
		
		// clearall
		ChatCommands.createCommand(new Command("/clearall", "Clear all windows. /clearall", new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				for (Window window : Launcher.getManager().getAllWindows()) {
					window.clear();
				}
				return true;
			}
		}));
		
		// closemsg
		ChatCommands.createCommand(new Command("/closemsg", "Close a query window with specified nickname. /closemsg <nickname>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				if (pCli.size() > 1) {
					QueryWindow window = Launcher.getManager().findQueryWindow(pWindow.getSession(), pCli.get(1));
					if (window != null && !window.isClosed()) {
						window.onClose();
						return true;
					}
				}
				return false;
			}
		}));
		
		// ctcp TODO
		// creq TODO
		// dcc TODO
		// describe
		ChatCommands.createCommand(new Command("/describe", "/me with a nick parameter. /describe <nickname> <message>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				String msg = pWindow.getCommandLine().split(" ", 3)[2];
				pWindow.getSession().action(pCli.get(1), msg);
				pWindow.appendToChat(new ChatText()
					.addAction(" -> ")
					.addNickname(pWindow.getSession().getNick())
					.addAction(msg)
				);
				return true;
			}
		}));
		// echo TODO
		
		// exit 
		ChatCommands.createCommand(new Command("/exit", "Closes all connections and exits the application. /exit [<exit message>]", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				// Get exit message
				List<String> cli = Arrays.asList(pWindow.getCommandLine().split(" ", 2));
				String exitMessage = cli.size() > 1 ? cli.get(1) : "";
				
				// Go through each session and close them
				for (ServerWindow window : Launcher.getManager().getAllServerWindows()) {
					window.getSession().close(exitMessage);
				}
				System.exit(0);
				return true;
			}
		}));
		
		// finger TODO
		
		// help
		ChatCommands.createCommand(new Command("/help", "Shows this help. /help [<command>]", new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				if (pCli.size() > 1) { // Show help for single command
					boolean foundCommand = false;
					for (Command command : ChatCommands.getCommands().values()) {
						if (command.getCommand().toLowerCase().contains( pCli.get(1).toLowerCase() )) {
							pWindow.appendToChat(new ChatText().addNotice(command.getCommand() + " : " + command.getHelp()));
							foundCommand = true;
						}
					}
					if (!foundCommand) {
						pWindow.appendToChat(new ChatText().addError("Unknown command. Try help without parameters for list"));
						return false;
					}
				}
				
				else { // Display all commands
					pWindow.appendToChat(new ChatText().addNotice("Displaying " + ChatCommands.getCommands().size() + " commands"));
					for (Command command : ChatCommands.getCommands().values()) {
						pWindow.appendToChat(new ChatText().addNotice(command.getCommand()));
					}
					pWindow.appendToChat(new ChatText().addNotice("Use the command /help <command> for directions with a specific command"));
				}
				return true;
			}
		}));
		
		// ignore TODO
		
		// info
		ChatCommands.createCommand(new Command("/info", "Returns some info about the ircd server software. /info"));
		
		// Invite
		ChatCommands.createCommand(new Command("/invite", "Invite someone to join the channel. /invite <nickname> <channelname>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				pWindow.getSession().invite(pCli.get(1), pWindow.getSession().getChannel(pCli.get(2)));
				pWindow.appendToChat(new ChatText()
					.addSystemMessage("Invited ")
					.addNickname(pCli.get(1))
					.addSystemMessage(" to join " + pWindow.getSession().getChannel(pCli.get(2)).getName())
				);
				return true;
			}
		}));
		
		// Is on
		ChatCommands.createCommand(new Command("/ison", "Returns those of the nicks specified that are online. /ison <nickname>[ <nickname> ...]"));
		
		// join
		ChatCommands.createCommand(new Command("/join", "Joins a channel. /join <channel>[,<channel>,<channel>..] [<pass>,<pass>,<pass>..]"));
		
		// Kick
		ChatCommands.createCommand(new Command("/kick", "Kick someone from a channel. /kick [<channel>] <nickname> [<reason>]", new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				Channel channel;
				// If request originates from within a channel window, and target channel isn't specified, use current channel
				if (pWindow instanceof ChannelWindow && !pWindow.getSession().isChannelToken(pCli.get(1))) {
					channel = ((ChannelWindow)pWindow).getChannel();
				}
				
				else {
					channel = pWindow.getSession().getChannel(pCli.get(1));
				}
				
				if (channel != null) {
					String msg = pCli.size() > 2 ? pWindow.getCommandLine().split(" ", 3)[2] : "";
					ChannelWindow window = (ChannelWindow) pWindow;
					window.getChannel().kick(pCli.get(1), msg);
				}
				
				else { // Couldn't find channel. Send the kick request as raw message to server, and let server handle it.
					pWindow.getSession().sayRaw(pWindow.getCommandLine().substring(1));
				}
				return true;
			}
		}));
		
		// list TODO
		// log TODO: Needs much more work
		ChatCommands.createCommand(new Command("/log", "Toggle log settings for current window. /log [<channelname>]", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				if (pCli.size() > 1) {
					
				}
				
				else { // User just want to know the status of logging for current window
					// Find global setting
					Boolean globalLogging = Launcher.getPreferences().getBoolean("logGlobal", false); // TODO: Add local logging
					pWindow.appendToChat(new ChatText()
						.addSystemMessage("Logging is " + (globalLogging ? "on" : "off"))
					);
				}
				return true;
			}
		}));
		
		// Lusers
		ChatCommands.createCommand(new Command("/lusers", "Show some information about server connections. /lusers"));
		
		// Me
		ChatCommands.createCommand(new Command("/me", "Describe yourself doing something. /me <message>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				if (pWindow instanceof ChannelWindow) {
					String msg = pWindow.getCommandLine().split(" ", 2)[1];
					((ChannelWindow)pWindow).getChannel().action(msg);
					pWindow.appendToChat(new ChatText()
						.addAction(" -> ")
						.addNickname(pWindow.getSession().getNick())
						.addAction(msg)
					);
				}
				
				else if (pWindow instanceof QueryWindow) {
					String msg = pWindow.getCommandLine().split(" ", 2)[1];
					pWindow.getSession().action(((QueryWindow)pWindow).getNick(), msg);
					pWindow.appendToChat(new ChatText()
						.addAction(" -> ")
						.addNickname(pWindow.getSession().getNick())
						.addAction(msg)
					);
				}
				
				else {
					pWindow.appendToChat(new ChatText().addError("/me doesn't work in this window."));
					return false;
				}
				return true;
			}
		}));
		
		// mode TODO: Expand
		ChatCommands.createCommand(new Command("/mode", "Set a mode on a user or channel./mode <target> <arguments>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				pWindow.getSession().mode(pCli.get(1), pCli.get(2));
				return true;
			}
		}));
		
		// Motd
		ChatCommands.createCommand(new Command("/motd", "Message of the day. /motd"));
		
		// msg
		ChatCommands.createCommand(new Command("/msg", "Sends a private message to someone. /msg <nickname> <message>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				// Find window if it exists
				QueryWindow window = Launcher.getManager().findQueryWindow(pWindow.getSession(), pCli.get(1));
				if (window == null) {
					// Create the window if it doesn't
					window = Launcher.getManager().createQueryWindow(pWindow.getSession(), pCli.get(1));
				}
				
				// Send the text to server
				if (pCli.size() > 2) {
					String msg = pWindow.getCommandLine().split(" ", 3)[2];
					window.say(msg);
				}
				return true;
			}
		}));
		
		// nick
		ChatCommands.createCommand(new Command("/nick", "Change your nick. /nick <new_nickname>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				pWindow.getSession().changeNick(pCli.get(1));
				return true;
			}
		}));
		
		// notice
		ChatCommands.createCommand(new Command("/notice", "Sends a notice message to someone. /notice <nickname> <message>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				String msg = pWindow.getCommandLine().split(" ", 3)[2];
				pWindow.getSession().notice(pCli.get(1), msg);
				pWindow.appendToChat(new ChatText()
					.addNotice("->")
					.addNickname(pWindow.getSession().getNick())
					.addNotice(msg)
				);
				return true;
			}
		}));
		
		// omsg TODO
		// part
		ChatCommands.createCommand(new Command("/part", "Leaves a channel. /part [<channel>] [<message>]", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				if (pWindow instanceof ChannelWindow && (pCli.size() == 1 || !(pWindow.getSession().isChannelToken(pCli.get(1))))) {
					String msg = pCli.size() > 1 ? pWindow.getCommandLine().split(" ", 2)[1] : "Leaving";
					((ChannelWindow) pWindow).getChannel().part(msg);
					pWindow.dispose();
				}
				
				else {
					String channelName = pCli.size() > 1 ? pCli.get(1) : "";
					if (!channelName.isEmpty()) {
						Channel channel = pWindow.getSession().getChannel(channelName);
						if (channel != null) {
							String msg = pCli.size() > 2 ? pWindow.getCommandLine().split(" ", 3)[2] : "Leaving";

							ChannelWindow window = Launcher.getManager().findChannelWindow(channel);
							if (window != null && !window.isClosed()) {
								window.getChannel().part(msg);
								window.dispose();
							}
						}
						
						else {
							return false;
						}
					}
				}
				return true;
			}
		}));
		
		// partall
		ChatCommands.createCommand(new Command("/partall", "Leaves all open channel for server. /partall [<message>]", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				String msg = pCli.size() > 1 ? pWindow.getCommandLine().split(" ", 2)[1] : "Leaving";
				for (Channel channel : pWindow.getSession().getChannels()) {
					channel.part(msg);
					
					ChannelWindow window = Launcher.getManager().findChannelWindow(channel);
					if (window != null && !window.isClosed()) {
						window.dispose();
					}
				}
				return true;
			}
		}));
		
		// ping
		ChatCommands.createCommand(new Command("/ping", "Pings a server. Expects a server address. /ping 127.0.0.1"));
		
		// query
		ChatCommands.createCommand(new Command("/query", 
				"Opens a private message window with someone. /query <nickname> [<message>]", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				// Find window if it exists
				QueryWindow window = Launcher.getManager().findQueryWindow(pWindow.getSession(), pCli.get(1));
				if (window == null) {
					// Create the window if it doesn't
					window = Launcher.getManager().createQueryWindow(pWindow.getSession(), pCli.get(1));
				}
				
				// Send the text to server
				if (pCli.size() > 2) {
					String msg = pWindow.getCommandLine().split(" ", 3)[2];
					window.say(msg);
				}
				return true;
			}
		}));
		
		// raw
		ChatCommands.createCommand(new Command("/raw", 
					"Sends a raw message to server. Circumvents all clientside handling. /raw <command> [<params> ..]", 
					new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				if (pWindow.getCommandLine().length() > 5) { // "/raw "
					pWindow.getSession().sayRaw(pWindow.getCommandLine().substring(6));
				}
				return true;
			}
		}));
		
		// say
		ChatCommands.createCommand(new Command("/say", "Well? What are you looking at me for? Say something!. /say <message>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				String msg = pWindow.getCommandLine().split(" ", 2)[1];
				if (pWindow instanceof ChannelWindow) {
					((ChannelWindow)pWindow).say(msg);
				}
				
				else if (pWindow instanceof QueryWindow) {
					((QueryWindow)pWindow).say(msg);
				}
				
				else {
					pWindow.appendToChat(new ChatText().addError("Can't chat here!"));
				}
				return true;
			}
		}));
		
		// time
		ChatCommands.createCommand(new Command("/time", "Display server time. /time"));
		
		// timestamp
		ChatCommands.createCommand(new Command("/timestamp", "Toggles timestamp on or off for conversations. /timestamp <on|off>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				Boolean toggle = pCli.get(1).equalsIgnoreCase("on") ? true : false;
				if (toggle != null) {
					Launcher.getPreferences().putBoolean("timestamp", toggle);
				}
				return true;
			}
		}));
		
		// whois
		ChatCommands.createCommand(new Command("/whois", "Shows information about someone. /whois <nickname>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				pWindow.getSession().whois(pCli.get(1));
				return true;
			}
		}));
		
		// whowas
		ChatCommands.createCommand(new Command("/whowas", "Shows information about someone who just left. /whowas <nickname>", 
				new CommandAction() {
			@Override
			boolean runCommand(List<String> pCli, Window pWindow) {
				pWindow.getSession().whoWas(pCli.get(1));
				return true;
			}
		}));
	}
}

/**
 * Class template for chat commands. Can be created without an action,
 * if desired to add help and auto complete without local action parsing
 * @author John
 * @author Jehans
 * @author Martin
 */
class Command {
	private String mCommand;
	private String mHelp;
	private CommandAction mAction;
	/**
	 * Creates new command without a callback. When this command is triggered, it'll be sent as raw to the server
	 * @see Command#Command(String, String, CommandAction)
	 * 
	 * @param pCommand
	 * @param pHelp
	 */
	public Command(String pCommand, String pHelp) {
		this.mCommand = pCommand;
		this.mHelp = pHelp;
	}
	
	/**
	 * Creates new command, with callback.
	 * @see Command#Command(String, String)
	 * 
	 * @param pCommand The actual chat command. Like "/time". 
	 * @param pHelp Short description of command. Used by /help [command]
	 * @param pAction Callback class, with action that runs when this command is triggered.
	 */
	public Command(String pCommand, String pHelp, CommandAction pAction) {
		this.mCommand = pCommand;
		this.mHelp = pHelp;
		this.mAction = pAction;
	}
	
	/**
	 * Returns the string used to trigger the command
	 * @return chat command that triggers this command. Example: /time
	 */
	public String getCommand() {
		return this.mCommand;
	}
	
	/**
	 * @return The help text used in /help
	 */
	public String getHelp() {
		return this.mHelp;
	}
	
	/**
	 * @return The executable callback class used when triggering the command
	 */
	public CommandAction getAction() {
		return this.mAction;
	}
	
	/**
	 * Triggers the command using the command line from window
	 * @param pCli
	 * @param pWindow
	 * @return 
	 */
	public boolean Run(List<String> pCli, Window pWindow) {
		return this.mAction.runCommand(Arrays.asList(pWindow.getCommandLine().split(" ")), pWindow);
	}
}

/**
 * Glorified callback pointer for chat commands.
 * @author Hans Martin Bragen
 * @author Jehans Jr. Storvik
 * @author John Høegh-Omdal
 *
 */
abstract class CommandAction {
	/**
	 * Must be overwritten. Callback for chat command.
	 * @param pCli
	 * @param pWindow
	 * @return TODO
	 */
	abstract boolean runCommand(List<String> pCli, Window pWindow);
}