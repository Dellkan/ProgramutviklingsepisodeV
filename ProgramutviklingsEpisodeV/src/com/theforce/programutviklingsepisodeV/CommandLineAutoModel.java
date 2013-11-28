package com.theforce.programutviklingsepisodeV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

import jerklib.Channel;
/**
 * This class parses the command line, and finds suitable suggestions it can give
 * to the Command line
 * 
 * @author John
 * @author Martin
 * @author Jehans
 *
 */
@SuppressWarnings({ "serial" })
class CommandLineAutoModel extends AbstractListModel<String> implements ComboBoxModel<String> {
	private Window mWindow;
	private List<String> mCollection = new ArrayList<String>();
	private String mLastWord = "";
	private String mSelected = "";
	public CommandLineAutoModel(Window pWindow) {
		this.mWindow = pWindow;
	}
	
	/**
	 * Gets element at the chosen index
	 * @param int index
	 */
	@Override
	public String getElementAt(int index) {
		if (this.mWindow.getCommandLine().length() > 0) {
			return this.mCollection.get(index);
		}
		return "";
	}

	/**
	 * A function to get the size of the list of commands.
	 * @return int which is size of of the list
	 */
	@Override
	public int getSize() {
		if (this.mWindow.getCommandLine().length() > 0) {
			this.updateList();
		}
		
		else {
			JComboBox<String> box = this.mWindow.getCLIAuto(); 
			if (box != null) {
				box.hidePopup();
			}
		}
		return this.mCollection.size();
	}

	/**
	 * Gets selected command
	 * @return Selected chosen completed command.
	 */
	@Override
	public Object getSelectedItem() {
		return this.mSelected;
	}

	/**
	 * Sets the selected command to the line.
	 * @param Item to be set.
	 */
	@Override
	public void setSelectedItem(Object anItem) {
		this.mSelected = (String)anItem;
	}
	
	/**
	 * This function looks at the command line, and from it determines suitable suggestions.
	 * It caches its collection, so it only recalculates it when the command line has changed
	 */
	public void updateList() {
		int lastSpace = 0;
		String word = "";
		try {
			lastSpace = this.mWindow.getCommandLine().lastIndexOf(" ") + 1;
		} catch (Exception e) {}
		try {
			word = this.mWindow.getCommandLine().substring(lastSpace);
		} catch (Exception e) {}
		
		if (!this.mLastWord.equalsIgnoreCase(word)) {
			this.mLastWord = word;
			this.mCollection.clear();
			this.mWindow.getCLIAuto().hidePopup(); // Things have changed. Hide the popup, forcing it to redraw itself next time
			if (!word.isEmpty()) {
				if (lastSpace == 0) { // First word, likely a command.
					for (Command command : ChatCommands.getCommands().values()) { // Parse commands
						if (command.getCommand().startsWith(word)) {
							this.mCollection.add(command.getCommand());
							if (this.mCollection.size() >= 5) {	break; }
						}
					}
				}
				
				if (this.mWindow.getSession().isChannelToken(word)) {
					for (Channel channel : this.mWindow.getSession().getChannels()) {
						if (channel.getName().toLowerCase().contains(word.toLowerCase())) {
							this.mCollection.add(channel.getName());
							if (this.mCollection.size() >= 5) {	break; }
						}
					}
				}
				
				if (this.mWindow instanceof ChannelWindow) {
					ChannelWindow window = (ChannelWindow) mWindow;
					for (String nick : window.getChannel().getNicks()) {
						if (nick.toLowerCase().contains(word.toLowerCase())) {
							this.mCollection.add(nick);
							if (this.mCollection.size() >= 5) {	break; }
						}
					}
				}
			}
			Collections.sort(this.mCollection);
			this.fireContentsChanged(this, 0, this.mCollection.size()-1);
		}
	}
}