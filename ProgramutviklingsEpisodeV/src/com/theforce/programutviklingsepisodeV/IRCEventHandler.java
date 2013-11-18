package com.theforce.programutviklingsepisodeV;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.MessageEvent;
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
	WindowManager WM;
	
	public IRCEventHandler(Session session, Profile profile)
	{
		this.session = session;
		this.profile = profile;
		this.session.addIRCEventListener(this);
		WM = new WindowManager();
	}

	/**
	 * Handles Handles different events received through the IRC session
	 */
	@Override
	public void receiveEvent(IRCEvent event) {
		// TODO Auto-generated method stub
		if (event.getType() == Type.CONNECT_COMPLETE)
		{
			(event.getSession()).join("#jerklibtest");
 
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
