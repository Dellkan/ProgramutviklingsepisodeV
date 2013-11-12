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
	
	public IRCEventHandler(Session session, Profile profile)
	{
		this.session = session;
		this.profile = profile;
		this.session.addIRCEventListener(this);
	}

	/**
	 * Handles Handles different events received through the IRC session
	 */
	@Override
	public void receiveEvent(IRCEvent event) {
		// TODO Auto-generated method stub
		if (event.getType() == Type.CONNECT_COMPLETE)
		{
			(event.getSession()).join("Default");
 
		}
		else if (event.getType() == Type.CHANNEL_MESSAGE)
		{
			MessageEvent me = (MessageEvent) event;
			/**
			 * HER MÅ DET KALLES EN FUNKSJON SOM SKRIVER DEN UT I TEXTAREAEN
			 * 
			 */
			
			System.out.println("<" + me.getNick() + ">"+ ":" + me.getMessage());
		}
		else if (event.getType() == Type.JOIN_COMPLETE)
		{
			JoinCompleteEvent jce = (JoinCompleteEvent) event;
 
			/* say hello and version number */
			jce.getChannel().say("Hello from " + profile.getName());
		}
		else
		{
			System.out.println(event.getType() + " " + event.getRawEventData());
		}
		
	}

}
