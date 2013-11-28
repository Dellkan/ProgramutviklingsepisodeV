package com.theforce.programutviklingsepisodeV;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

class ChatText {
	public enum Type {NICKNAME, URL, INVITE_ACCEPT, NOTICE, SYSTEM, MOTD, ERROR, NORMAL, HELP, ACTION}
	private List<ExtendedText> mLine = new ArrayList<ExtendedText>();
	
	public ChatText addNickname(String pNick) {
		this.mLine.add(new ExtendedText(Type.NICKNAME, " <" + pNick + "> ", pNick));
		return this;
	}
	
	public ChatText addText(String pText) {
		this.mLine.add(new ExtendedText(Type.NORMAL, pText));
		return this;
	}
	
	public ChatText addInviteAccept() {
		this.mLine.add(new ExtendedText(Type.INVITE_ACCEPT, "Accept"));
		return this;
	}
	
	public ChatText addNotice(String pText) {
		this.mLine.add(new ExtendedText(Type.NOTICE, pText));
		return this;
	}
	
	public ChatText addSystemMessage(String pText) {
		this.mLine.add(new ExtendedText(Type.SYSTEM, pText));
		return this;
	}
	
	public ChatText addMOTDLine(String pText) {
		this.mLine.add(new ExtendedText(Type.MOTD, pText));
		return this;
	}
	
	public ChatText addError(String pText) {
		this.mLine.add(new ExtendedText(Type.ERROR, pText));
		return this;
	}
	
	public ChatText addHelp(String pText) {
		this.mLine.add(new ExtendedText(Type.HELP, pText));
		return this;
	}
	
	public ChatText addAction(String pText) {
		this.mLine.add(new ExtendedText(Type.ACTION, pText));
		return this;
	}
	
	public void output(StyledDocument pDoc) {		
		try {
			if (Launcher.getPreferences().getBoolean("timestamp", false)) {
				pDoc.insertString(pDoc.getLength(), "\n[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] ", new SimpleAttributeSet());
			}
			
			else {
				pDoc.insertString(pDoc.getLength(), "\n", new SimpleAttributeSet());
			}
			for (ExtendedText text : this.mLine) {
				// Create attribute set
				Style style = pDoc.getStyle("IRCChatStyle");
		        
		        // Mark the text with the appropriate attribute
				style.addAttribute("type", text.getType());
				
				// Pack the actual object in too
				style.addAttribute("ExtendedText", text);
				
				// Reset style
				StyleConstants.setBold(style, false);
				StyleConstants.setUnderline(style, false);
				
				// Set style
				switch(text.getType()) {
					case ACTION: // Purple
						StyleConstants.setForeground(style, new Color(100, 60, 255));
						break;
					case INVITE_ACCEPT:
						StyleConstants.setForeground(style, Color.BLUE);
						StyleConstants.setUnderline(style, true);
						break;
					case ERROR: // Dark red
						StyleConstants.setForeground(style, new Color(200, 0, 0));
						StyleConstants.setBold(style, true);
						break;
					case HELP: // Green
						StyleConstants.setForeground(style, new Color(0, 220, 0));
						break;
					case MOTD: // Dark green
						StyleConstants.setForeground(style, new Color(0, 100, 0));
						break;
					case NICKNAME:
						StyleConstants.setForeground(style, Color.GRAY);
						StyleConstants.setBold(style, true);
						break;
					case NOTICE: // Dark red
						StyleConstants.setForeground(style, new Color(150, 25, 10));
						break;
					case SYSTEM:
						StyleConstants.setForeground(style, Color.DARK_GRAY);
						break;
					case URL:
						StyleConstants.setForeground(style, Color.BLUE);
						StyleConstants.setUnderline(style, true);
						break;
					case NORMAL:
					default:
						StyleConstants.setForeground(style, Color.BLACK);
						break;
				}
				// Insert the string
				pDoc.insertString(pDoc.getLength(), text.getText(), style);
			}
		} catch (BadLocationException e) {}
	}
	
	public boolean isEmpty() {
		return this.mLine.size() == 0;
	}
}

class ExtendedText {
	private ChatText.Type mType;
	private String mText;
	private String mRaw;
	public ExtendedText(ChatText.Type pType, String pText) {
		this.mType = pType;
		this.mText = pText;
		this.mRaw = pText;
	}
	
	public ExtendedText(ChatText.Type pType, String pText, String pRaw) {
		this.mType = pType;
		this.mText = pText;
		this.mRaw = pRaw;
	}
	
	public ChatText.Type getType() {
		return this.mType;
	}
	
	public String getText() {
		return this.mText;
	}
	
	public String getRaw() {
		return this.mRaw;
	}
}