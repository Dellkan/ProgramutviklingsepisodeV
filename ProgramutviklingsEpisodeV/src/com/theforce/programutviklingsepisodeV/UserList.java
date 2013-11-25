package com.theforce.programutviklingsepisodeV;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import jerklib.Channel;
import jerklib.events.modes.ModeAdjustment;

@SuppressWarnings({ "serial", "rawtypes" })
public class UserList extends AbstractListModel {
	private Channel mChannel;
	public UserList(Channel pChannel) {
		this.mChannel = pChannel;
	}

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

	@Override
	public int getSize() {
		List<String> nicks = this.mChannel.getNicks(); 
		return nicks.size();
	}

	public void update() {
		this.fireContentsChanged(this, 0, this.getSize());
	}
}
