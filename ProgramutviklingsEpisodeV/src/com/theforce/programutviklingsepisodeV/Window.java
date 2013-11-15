package com.theforce.programutviklingsepisodeV;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
abstract class Window extends JInternalFrame {
	protected JTextPane mChat;
	protected JTextField mCli; // Command line interface
	protected JList mUsers;
	protected String mChatLines = new String();
	protected Component mUpper;
	protected JSplitPane mWindow;
	
	public Window(String title, boolean showUsersInterface) {
		super(title, true, true, true, true);
		// Create content area
		this.mChat = new JTextPane();
		
        try {
        	// Set up editor
        	this.mChat.setEditorKit(new ChatWindowEditorKit());
        	this.mChat.setEditable(false);
		
			JScrollPane chatScroller = new JScrollPane(this.mChat);
			
	        // Create the split pane that will contain chat window, and user list
	        
	        // Userlist
	        if (showUsersInterface) {
				this.mUsers = new JList();
				this.mUsers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
				this.mUsers.setLayoutOrientation(JList.HORIZONTAL_WRAP);
				this.mUsers.setVisibleRowCount(-1);
				this.mUsers.setMinimumSize(new Dimension(75, 100));
				this.mUsers.setPreferredSize(new Dimension(125, 0));
				
				JScrollPane userScroller = new JScrollPane(this.mUsers);
				userScroller.setPreferredSize(new Dimension(100, 150));
				
				this.mUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroller, userScroller);
				((JSplitPane)this.mUpper).setResizeWeight(1);
	        }
	        
	        else {
	        	this.mUpper = chatScroller;
	        }
			
			// Create command line interface (textbox)
	        JPanel panel = new JPanel();
	        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	        
			this.mCli = new JTextField();
			this.mCli.setMinimumSize(new Dimension(200, 30));
			
			panel.add(this.mCli);
			
			// Create CLI send button
			JButton send = new JButton("Send");
			
			panel.add(send);
			
			ActionListener eventHandler = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					Window.this.commandParser();
				}
			};
			this.mCli.addActionListener(eventHandler);
			send.addActionListener(eventHandler);
			
	        //Create a split pane with the two scroll panes in it.
	        this.mWindow = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.mUpper, panel);
	        this.mWindow.setResizeWeight(1);
	        
	        this.add(this.mWindow);
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	abstract public void commandParser();
	
	public void appendToChat(String line) {
		try {
			// Should sanitize line, and make sure its placed on a new line, before inserting it
			this.mChatLines += line;
			
	        SimpleAttributeSet attrs = new SimpleAttributeSet();
	        StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_LEFT);
			
			StyledDocument doc = (StyledDocument) this.mChat.getDocument();
			doc.insertString(0, this.mChatLines, attrs);
			doc.setParagraphAttributes(0, doc.getLength() - 1, attrs, false);
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getCommandLine() {
		return this.mCli.getText();
	}
}

@SuppressWarnings("serial")
/**
 * Part of the hack necessary to make chat lines appear at bottom
 * 
 * @author John
 *
 */
class ChatWindowEditorKit extends StyledEditorKit {

    public ViewFactory getViewFactory() {
        return new StyledViewFactory();
    }
 
    static class StyledViewFactory implements ViewFactory {

        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {

                    return new LabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {

                    return new ChatWindowBoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {

                    return new IconView(elem);
                }
            }
 
            return new LabelView(elem);
        }

    }
}
 
/**
 * This class is part of the hack necessary to make chat lines appear at bottom
 * 
 * @author John
 *
 */
class ChatWindowBoxView extends BoxView {
    public ChatWindowBoxView(Element elem, int axis) {
        super(elem,axis);
    }
    protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {

        super.layoutMajorAxis(targetSpan, axis, offsets, spans);
        int textBlockHeight = 0;
        int offset = 0;

        for (int i = 0; i < spans.length; i++) {
            textBlockHeight += spans[i];
        }
        
        offset = (targetSpan - textBlockHeight);
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] += offset;
        }
    }
}   