package com.theforce.programutviklingsepisodeV;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import jerklib.Session;

@SuppressWarnings("serial")
abstract class Window extends JInternalFrame {
	private JScrollPane mChatScroller;
	protected JTextPane mChat;
	protected String mChatLines = new String();
	protected JTextField mCli; // Command line interface
	@SuppressWarnings("rawtypes")
	protected JList mUsers;
	protected Component mUpper;
	protected JSplitPane mWindow;
	protected JButton mToolbarRef;
	private Session mSession;
	
	@SuppressWarnings("rawtypes")
	public Window(Session session, String title, boolean showUsersInterface) {
		super(title, true, true, true, true);
		this.mSession = session;
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				((Window) e.getInternalFrame()).onClose();
			}
		});
		// Create content area
		this.mChat = new JTextPane();
		
        try {
        	// Set up editor
        	this.mChat.setEditorKit(new ChatWindowEditorKit());
        	this.mChat.setEditable(false);
		
			this.mChatScroller = new JScrollPane(this.mChat);
			
	        // Create the split pane that will contain chat window, and user list
	        
	        // Userlist
	        if (showUsersInterface) {
				this.mUsers = new JList();
				this.mUsers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
				this.mUsers.setLayoutOrientation(JList.VERTICAL);
				this.mUsers.setVisibleRowCount(-1);
				this.mUsers.setMinimumSize(new Dimension(100, 100));
				this.mUsers.setPreferredSize(new Dimension(125, 0));
				
				JScrollPane userScroller = new JScrollPane(this.mUsers);
				userScroller.setMinimumSize(new Dimension(50, 150));
				userScroller.setPreferredSize(new Dimension(100, 150));
				userScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				
				this.mUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.mChatScroller, userScroller);
				((JSplitPane)this.mUpper).setResizeWeight(1);
	        }
	        
	        else {
	        	this.mUpper = this.mChatScroller;
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
	
	protected void commandParser() {
		if (this.mCli.getText().charAt(0) == '/') {
			try {
				List<String> cli = Arrays.asList(this.mCli.getText().split(" "));
				if (!cli.isEmpty()) {
					String command = cli.get(0);
					switch(command)
					{
						case "/join" :
							if(this.getSession().isChannelToken(cli.get(1))) { 
								this.getSession().join(cli.get(1));
							}
							
							else {
								this.appendToChat("Channel names must start with one of " + 
									Arrays.asList(this.getSession().getServerInformation().getChannelPrefixes()).toString()
								);
							}
							break;
						case "/msg":
							{
								// Find window if it exists
								QueryWindow window = Launcher.getManager().findQueryWindow(this.getSession(), cli.get(1));
								if (window == null) {
									// Create the window if it doesn't
									window = Launcher.getManager().createQueryWindow(this.getSession(), cli.get(1));
								}
								
								// Add the text
								window.appendToChat(this.getSession().getNick() + ": " + cli.get(2));
								
								// Send the text to server
								String msg = this.mCli.getText().split(" ", 3)[2];
								this.getSession().sayPrivate(cli.get(1), msg);
							}
							break;
						case "/nick":
							{
								this.getSession().changeNick(cli.get(1));
							}
							break;
						case "/help":
							this.appendToChat("The available commands are:\r\n"
									+ "/help\t\t-\tDisplays a list of avalible commands.\r\n"
									+ "/msg [USER][MESSAGE]\t-\tSends a private message to a user.\r\n"
									+ "/join [#CHANNEL]\t-\tJoins a channel, must start with #.");
							break;
						default : this.appendToChat("Error this command does not exist. For a list of commands type /help");
					}
				}
			}
			
			catch (Exception e) { }
		}
		
		else {
			this.appendToChat("Can't chat here!");
		}
		this.mCli.setText("");
	}
	
	protected void onClose() {
		// Remove toolbar button
		this.mToolbarRef.getParent().remove(this.mToolbarRef);
		Launcher.getManager().getWindowToolbar().revalidate();
		Launcher.getManager().getWindowToolbar().repaint();
		this.mToolbarRef = null;
		
		// Remove from window manager
		Launcher.getManager().RemoveWindow(this);
	};
	
	public void setToolbarReference(JButton ref) {
		this.mToolbarRef = ref;
	}
	
	public JTextField getCLI() {
		return this.mCli;
	}
	
	public void appendToChat(String line) {
		try {
			// Figure out if we should rescroll
			JScrollBar chatScroller = this.mChatScroller.getVerticalScrollBar();
			boolean rescroll = chatScroller.getValue() + chatScroller.getVisibleAmount() >= chatScroller.getMaximum();
			
			// Should sanitize line, and make sure its placed on a new line, before inserting it
			this.mChatLines += line + "\n";
			
	        SimpleAttributeSet attrs = new SimpleAttributeSet();
	        StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_LEFT);
			
			StyledDocument doc = (StyledDocument) this.mChat.getDocument();
			doc.insertString(doc.getLength(), "\n" + line, attrs);
			
			// Adjust scrollbar if applicable
			if (rescroll) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Window.this.mChatScroller.getVerticalScrollBar().setValue(Window.this.mChatScroller.getVerticalScrollBar().getMaximum());
					}
				});
			}
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getCommandLine() {
		return this.mCli.getText();
	}
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		this.mToolbarRef.setText(title);
	}
	
	public Session getSession() {
		return this.mSession;
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