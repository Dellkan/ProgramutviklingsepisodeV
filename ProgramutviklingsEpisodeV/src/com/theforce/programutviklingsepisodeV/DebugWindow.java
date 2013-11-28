package com.theforce.programutviklingsepisodeV;
/**
 * A window simply to use for debugging.
 * @author John
 * @author Jehans
 * @author Martin
 */
@SuppressWarnings("serial")
public class DebugWindow extends Window {
	public DebugWindow() {
		super(null, "Debug window", false);
		this.getCLI().setEditable(false);
		this.getCLI().setEnabled(false);
		this.getCliSend().setEnabled(false);
	}
}
