package com.theforce.programutviklingsepisodeV;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import jerklib.Channel;
import jerklib.ModeAdjustment;

/**
 * Makes a list of of all the users on the right side of the chat window.
 * @author John
 * @author jehans
 * @author Martin
 */

@SuppressWarnings({ "serial", "rawtypes" })
public class UserList extends AbstractListModel {
	private Channel mChannel;
	public UserList(Channel pChannel) {
		this.mChannel = pChannel;
	}

	/**
	 * Gets usernick at the chosen index from the channel userlist.
	 * @param Gets user at the chosen index in the list.
	 * @return Will return the usernick at the sent index.
	 */
	@Override
	public Object getElementAt(int index) {
		String nick = this.mChannel.getNicks().get(index);
		List<ModeAdjustment> modes = this.mChannel.getUsersModes(nick);
		Map<String, String> prefixes = this.mChannel.getSession().getServerInformation().getNickPrefixMap();
		
		Iterator prefixIterator = prefixes.entrySet().iterator();
		while (prefixIterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) prefixIterator.next();
			for (ModeAdjustment mode : modes) {
				if (mapEntry.getValue().toString().charAt(0) == mode.getMode()) {
					nick = mapEntry.getKey() + nick;
				}
			}
		}
		
		return nick;
	}

	/**
	 * Gets current size of the user list.
	 * @return the amount of users in the channel.
	 */
	@Override
	public int getSize() {
		List<String> nicks = this.mChannel.getNicks(); 
		return nicks.size();
	}

	/**
	 * Updates the content in the the user list.
	 */
	public void update() {
		this.fireContentsChanged(this, 0, this.getSize());
	}
}
