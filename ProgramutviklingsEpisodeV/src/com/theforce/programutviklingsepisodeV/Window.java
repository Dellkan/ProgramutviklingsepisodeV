package com.theforce.programutviklingsepisodeV;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;

@SuppressWarnings("serial")
class Window extends JInternalFrame {
	JTextPane chat;
	JList users;
	JTextField cli;
	public Window(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
		super(title, resizable, closable, maximizable, iconifiable);
		// Create content area
		this.chat = new JTextPane();
		
        try {
        	// Set up editor
        	this.chat.setEditorKit(new ChatWindowEditorKit());
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_LEFT);
            
            // Insert some content (debugging)
            StyledDocument doc = (StyledDocument) this.chat.getDocument();
            doc.insertString(0, "1", attrs);
            doc.insertString(0, "1\n", attrs);
            doc.insertString(0, "1", attrs);
            doc.insertString(0, "1", attrs);
            doc.insertString(0, "1", attrs);
            
            // ??
            doc.setParagraphAttributes(0, doc.getLength() - 1, attrs, false);
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
		
		//this.chat = new JTextArea();
		//this.chat.setEditable(false);
		//this.chat.setLineWrap(true);
		//this.chat.setMinimumSize(new Dimension(300, 100));
		//this.chat.setPreferredSize(new Dimension(500, 100));
		
		JScrollPane chatScroller = new JScrollPane(this.chat);
		
		// Create user list
		this.users = new JList();
		this.users.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.users.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.users.setVisibleRowCount(-1);
		this.users.setMinimumSize(new Dimension(75, 100));
		this.users.setPreferredSize(new Dimension(125, 0));
		
		JScrollPane userScroller = new JScrollPane(this.users);
		userScroller.setPreferredSize(new Dimension(100, 150));
		
        // Create the split pane that will contain chat window, and user list
        JSplitPane upperPart = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroller, userScroller);
        upperPart.setResizeWeight(1);
		
		// Create command line interface (textbox)
		this.cli = new JTextField("Commands goes here, stupid");
		this.cli.setMinimumSize(new Dimension(200, 30));
		this.cli.setPreferredSize(new Dimension(0, 30));
		
        //Create a split pane with the two scroll panes in it.
        JSplitPane whole = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPart, this.cli);
        whole.setResizeWeight(1);
        
        this.add(whole);
	}
}

@SuppressWarnings("serial")
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