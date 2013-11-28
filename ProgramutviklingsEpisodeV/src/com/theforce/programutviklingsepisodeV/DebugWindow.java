package com.theforce.programutviklingsepisodeV;

@SuppressWarnings("serial")
public class DebugWindow extends Window {
	public DebugWindow() {
		super(null, "Debug window", false);
		this.getCLI().setEditable(false);
		this.getCLI().setEnabled(false);
		this.getCliSend().setEnabled(false);
	}
}
