package com.theforce.programutviklingsepisodeV;
import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.listeners.IRCEventListener;

/**
 * 
 * @author Hans Martin Bragen
 * @author John Høeg-Omdal
 * @author Jehans JR Storvik
 *
 */

public class IRCEventHandler implements IRCEventListener{
	Session session;
	Profile profile;
	Window window;
	
	public IRCEventHandler(Session session, Profile profile, Window window)
	{
		this.session = session;
		this.profile = profile;
		this.session.addIRCEventListener(this);
		this.window = window;
	}
	/**
	 * Kicks a user from the IRC channel.
	 * @param channel object with target channel
	 * @param username string containing nick of person to be kicked
	 * @param reason String where the user can specify reason for kick
	 */
	public void kickUser(Channel channel, String username, String reason)
	{
		/*
		 * Implementer sjekk for rettigheter ?
		 */
		channel.kick(username, reason);
	}
	/**
	 * Joins a new channel in the current session
	 * @param channel String containing name of channel to join.
	 */
	public void joinChannel(String channel)
	{
		session.join(channel);
	}
	/**
	 * Prints to session channel.	 
	 * @param channel object of the channel to print to
	 * @param out String containing what to print
	 */
	public void say( Channel channel, String out)
	{
		channel.say(out);
	}
	/**
	 * Prevents a user from speaking in the channel
	 * @param channel object containing the target channel
	 * @param user string containing nick of user to mute
	 */
	public void mute(Channel channel, String user)
	{
		channel.deVoice(user);
	}
	/**
	 * Allows a user to speaking in the channel
	 * @param channel object containing the target channel
	 * @param user string containing nick of user to unmute
	 */
	public void unMute(Channel channel, String user)
	{
		channel.voice(user);
	}

	/**
	 * Handles Handles different events received through the IRC session
	 */
	@Override
	
	public void receiveEvent(IRCEvent event) {
		// TODO Auto-generated method stub
		if (event.getType() == Type.CONNECT_COMPLETE)
		{
			//(event.getSession()).join("#jerklibtest");
 
		}
		else if (event.getType() == Type.CHANNEL_MESSAGE)
		{
			MessageEvent me = (MessageEvent) event;
			me.getChannel();
			/**
			 * HER MÅ DET KALLES EN FUNKSJON SOM SKRIVER DEN UT I TEXTAREAEN
			 * 
			 */
			
			System.out.println("<" + me.getNick() + ">"+ ":" + me.getMessage());
		}
		else if (event.getType() == Type.JOIN_COMPLETE)
		{
			JoinCompleteEvent jce = (JoinCompleteEvent) event;
			jce.getChannel().say(profile.getName() + " has joined the channel.");
		}
		else if (event.getType() == Type.CONNECTION_LOST)
		{
			/* Serverwindow + channel window skriv ut error ... */
			
			/* eventuelt en reconnect algoritme */
		}
		
		else if (event.getType() == Type.KICK_EVENT)
		{
			/* Disconect ting */
		}
		else if (event.getType() == Type.MOTD)
		{
			/* Channel skriv ut MOTD; */
		}
		else if (event.getType() == Type.PRIVATE_MESSAGE)
		{
			/* Åpne nytt vindu med privchat */
		}
		else if (event.getType() == Type.CTCP_EVENT)
		{
			/* Åpne nytt vindu med privchat */
		}
		//if (event.getType() == Type.
	}

}
