package com.theforce.programutviklingsepisodeV;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jerklib.Session;

/**
 * 
 * @author Hans Martin Bragen
 * @author Jehans Jr. Storvik
 * @author John Høegh-Omdal
 *
 */
@SuppressWarnings({"serial", "rawtypes"})
abstract class Window extends JInternalFrame {
	private JScrollPane mChatScroller;
	protected JTextPane mChat;
	protected JTextField mCli; // Command line interface
	protected JComboBox<String> mCliAuto;
	protected List<String> mCliHistory = new ArrayList<String>();
	protected JButton mCliSend;
	protected long mTimestamp; // Used for /! command
	protected JList mUsers;
	protected Component mUpper;
	protected JSplitPane mWindow;
	protected Component mToolbarRef;
	private Session mSession;
	
	/**
	 * 
	 * @param session session to associate with the window
	 * @param title string containing the title to give the window
	 * @param showUsersInterface bool, if true shows user interface; else hides it
	 */
	public Window(Session session, String title, boolean showUsersInterface) {
		super(title, true, true, true, true);
		this.mSession = session;
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				((Window) e.getInternalFrame()).onClose();
				Integer ref = ((Window) e.getInternalFrame()).getToolbarIndex();
				if (ref != null && ref >= 0 && Launcher.getManager().getWindowToolbar().getTabCount() > 0) {
					Launcher.getManager().getWindowToolbar().removeTabAt(ref);
				}
			}
			
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				Integer ref = ((Window) e.getInternalFrame()).getToolbarIndex();
				if (ref != null && ref >= 0 && Launcher.getManager().getWindowToolbar().getTabCount() > 0) {
					Launcher.getManager().getWindowToolbar().setSelectedIndex(ref);
				}
			}
		});
		// Create content area
		this.mChat = new JTextPane();
		
        try {
        	// Set up editor
        	this.mChat.setEditorKit(new ChatWindowEditorKit());
        	this.mChat.setEditable(false);
        	
        	// Add style we can later change around a bit
        	this.mChat.addStyle("IRCChatStyle", null);
        	
        	// Set default font. IRC servers kind of rely on monotone fonts
            this.mChat.setFont(new Font("monospaced", Font.PLAIN, 12));
        	
        	// Add actionListener to chat window
        	this.mChat.addMouseListener(new MouseAdapter() {
				@Override
        		public void mouseClicked(MouseEvent e) {
    				// Get document attached to the clicked point
    				DefaultStyledDocument doc = (DefaultStyledDocument) ((JTextPane) e.getSource()).getDocument();
    				// Get element
    				Element element = doc.getCharacterElement(
    					// Search for the element at the x, y coordinates of the click
    					((JTextPane) e.getSource()).viewToModel(
    						new Point(e.getX(), e.getY())
    					)
    				);
    				// Get the attributes of that element
    				AttributeSet attributes = element.getAttributes();
        			ChatText.Type textType = (ChatText.Type) attributes.getAttribute("type");
        			ExtendedText textRaw = (ExtendedText) attributes.getAttribute("ExtendedText");
        			if (textType != null) {
        				if (textType == (ChatText.Type.NICKNAME)) {
        					Window.this.setCommandLine("/msg " + textRaw.getRaw());
        				}
        				
        				else if (textType == (ChatText.Type.INVITE_ACCEPT)) {
        					//textRaw.g
        				}
        			}
        		}
        	});

        	// Add scroller for chat
			this.mChatScroller = new JScrollPane(this.mChat);
			
			// Add auto-scroll for chat
			this.mChatScroller.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			     private BoundedRangeModel brm = Window.this.mChatScroller.getVerticalScrollBar().getModel();
			     private boolean wasAtBottom = true;

			     @Override
			     public void adjustmentValueChanged(AdjustmentEvent e) {
			        if (!brm.getValueIsAdjusting()) {
			           if (wasAtBottom)
			              brm.setValue(brm.getMaximum());
			        } else
			           wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());

			     }
			});
			
	        // Create the split pane that will contain chat window, and user list
	        
	        // Userlist
	        if (showUsersInterface) {
				this.mUsers = new JList();
				this.mUsers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
				this.mUsers.setLayoutOrientation(JList.VERTICAL);
				this.mUsers.setVisibleRowCount(-1);
				
				JScrollPane userScroller = new JScrollPane(this.mUsers);
				userScroller.setMinimumSize(new Dimension(50, 150));
				userScroller.setPreferredSize(new Dimension(125, 150));
				userScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				userScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				
				this.mUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.mChatScroller, userScroller);
				((JSplitPane)this.mUpper).setResizeWeight(1);
	        }
	        
	        else {
	        	this.mUpper = this.mChatScroller;
	        }
			
			// Create command line interface (textbox)
	        JPanel panel = new JPanel();
	        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	        
	        JLayeredPane cliHolder = new JLayeredPane();
	        cliHolder.addComponentListener(new ComponentAdapter() {
	        	@Override
	        	public void componentResized(ComponentEvent pEvent) {
	        		JLayeredPane pane = (JLayeredPane)pEvent.getComponent();
	        		Window.this.mCli.setBounds(0, 0, pane.getWidth(), pane.getHeight());
	        		Window.this.mCliAuto.setBounds(0, 0, pane.getWidth(), pane.getHeight());
	        	}
	        });
	        
			this.mCli = new JTextField();
			this.mCliAuto = new JComboBox<String>(new CommandLineAutoModel(this));
			this.mCliAuto.setMaximumRowCount(5);
			
			cliHolder.add(this.mCliAuto, 1);
			cliHolder.add(this.mCli, 0);
			cliHolder.setMinimumSize(new Dimension(200, 30));
			
			panel.add(cliHolder);
			
			// Create CLI send button
			this.mCliSend = new JButton("Send");
			
			panel.add(this.mCliSend);
			
			// Add key listener to CLIAuto
			this.mCliAuto.addKeyListener(new KeyListener() { // CLI is our master. Let CLI handle the events.
				@Override
				public void keyPressed(KeyEvent pKeyEvent) {
					Window.this.getCLI().dispatchEvent(pKeyEvent);
				}

				@Override
				public void keyReleased(KeyEvent pKeyEvent) {
					Window.this.getCLI().dispatchEvent(pKeyEvent);
				}

				@Override
				public void keyTyped(KeyEvent pKeyEvent) {
					Window.this.getCLI().dispatchEvent(pKeyEvent);
				}
			});
			
			// Remove traversal stuff from CLI, so it can process the TAB key for itself
			this.mCli.setFocusTraversalKeysEnabled(false);
			
			// Add key listener to CLI
			this.mCli.addKeyListener(new KeyListener() {
				private int mHistory = 0;
				private boolean isTyping = false;
				@Override
				public void keyPressed(KeyEvent pKeyEvent) {
				}

				@Override
				public void keyReleased(KeyEvent pKeyEvent) {
					if (pKeyEvent.getKeyCode() == KeyEvent.VK_ENTER) { // Send
						Window.this.mCliHistory.add(Window.this.getCommandLine());
						Window.this.commandParser();
						Window.this.mCliAuto.hidePopup();
						Window.this.mTimestamp = System.currentTimeMillis();
					}
					
					else if (pKeyEvent.getKeyCode() == KeyEvent.VK_TAB) { // AutoComplete
						String auto = (String)Window.this.mCliAuto.getSelectedItem();
						if (auto != null && !auto.isEmpty()) {
							int lastSpace = 0;
							try {
								lastSpace = Window.this.getCommandLine().lastIndexOf(" ") + 1;
							} catch (Exception e) {}
							
							String original = Window.this.getCommandLine();
							Window.this.setCommandLine(original.substring(0, lastSpace) + auto + " ");
							
							// Reset the CLIAuto, so pressing tab multiple times doesn't spam the CLI
							Window.this.getCLIAuto().setSelectedIndex(-1);
							
							// We're done with this suggestion now. The list is no longer relevant. Hide it.
							Window.this.getCLIAuto().hidePopup();
						}
					}
					
					else {
						if (Window.this.getCommandLine().isEmpty()) {
							this.isTyping = false;
							Window.this.mCliAuto.hidePopup();
						}
						if (pKeyEvent.getKeyCode() == KeyEvent.VK_UP || pKeyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
							if (Window.this.getCommandLine().length() == 0) { this.mHistory = 0; }
							if (pKeyEvent.getKeyCode() == KeyEvent.VK_UP) {
								if (!this.isTyping) { // History stuff
									this.mHistory = this.mHistory == 0 ? Window.this.getCliHistory().size() : --this.mHistory;
									if (this.mHistory > 0) { // Use 0 for ""
										Window.this.setCommandLine(Window.this.getCliHistory().get(this.mHistory - 1));
									}
									
									else {
										Window.this.setCommandLine("");
									}
								}
								
								else { // Suggestions stuff
									int selected = Window.this.getCLIAuto().getSelectedIndex();
									if (selected <= 0) { // No suggestion is selected. Since we're going up, choose the one at bottom 
										Window.this.mCliAuto.setSelectedIndex(Window.this.mCliAuto.getItemCount() - 1);
									}
									
									else {
										Window.this.mCliAuto.setSelectedIndex(selected - 1);
									}
								}
							}
							
							else if (pKeyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
								if (!this.isTyping) { // History stuff
									this.mHistory = this.mHistory == Window.this.getCliHistory().size() ? 0 : ++this.mHistory; 
									if (this.mHistory > 0) { // Use 0 for ""
										Window.this.setCommandLine(Window.this.getCliHistory().get(this.mHistory - 1));
									}
									
									else {
										Window.this.setCommandLine("");
									}
								}
								
								else { // Suggestions stuff
									int selected = Window.this.getCLIAuto().getSelectedIndex();
									if ( selected >= (Window.this.mCliAuto.getItemCount() -1) ) { 
										Window.this.mCliAuto.setSelectedIndex(0);
									}
									
									else {
										Window.this.mCliAuto.setSelectedIndex(selected + 1);
									}
								}
							}
						}
						
						else {
							if (Window.this.getCommandLine().length() > 0) {
								this.isTyping = true;
								if (Window.this.getCLIAuto().getItemCount() > 0) {
									// Typing stuff. Show suggestions
									Window.this.getCLIAuto().showPopup();
									
									// Make the first choice become selected, for faster typing
									Window.this.getCLIAuto().setSelectedIndex(0);
								}
								
								else {
									Window.this.getCLIAuto().hidePopup();
								}
							}
						}
					}
				}

				@Override
				public void keyTyped(KeyEvent pKeyEvent) {
				}
			});
			
			// Add action listener to CLI send button
			this.mCliSend.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					Window.this.commandParser();
				}
			});
			
	        //Create a split pane with the two scroll panes in it.
	        this.mWindow = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.mUpper, panel);
	        this.mWindow.setResizeWeight(1);
	        
	        this.add(this.mWindow);
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	/**
	 * function to handle chat commands.
	 */
	protected void commandParser() {
		if (!this.getCommandLine().isEmpty()) {
			if (this.getCommandLine().charAt(0) == '/') {
				try {
					List<String> cli = Arrays.asList(this.getCommandLine().split(" "));
					if (!cli.isEmpty()) {
						ChatCommands.parseCommand(cli, this);
					}
				}
				
				catch (Exception e) { }
			}
			
			else {
				this.say(this.getCommandLine());
			}
			this.setCommandLine("");
		}
	}
	/**
	 * removes itself from window manager when closed
	 */
	protected void onClose() {
		// Remove from window manager
		Launcher.getManager().RemoveWindow(this);
	};
	
	/**
	 * Sets references to toolbar component
	 * @param ref The component object to references
	 */
	public void setToolbarReference(Component ref) {
		this.mToolbarRef = ref;
	}
	/**
	 * Gets the toolbar reference in the window
	 * @return a reference to the windows toolbar
	 */
	public Component getToolbarReference() {
		return this.mToolbarRef;
	}
	
	/**
	 * gets the index of the windows toolbar
	 * @return the index of the windows toolbar
	 */
	public int getToolbarIndex() {
		return Launcher.getManager().getWindowToolbar().indexOfComponent(this.mToolbarRef);
	}
	
	/**
	 * fetches the CLI of the window
	 * @return the windows CLI
	 */
	public JTextField getCLI() {
		return this.mCli;
	}
	
	/**
	 * 
	 * @return
	 */
	public JComboBox<String> getCLIAuto() {
		return this.mCliAuto;
	}
	/**
	 * Adds text to the string.
	 * @param pText string with text to append to chat
	 */
	
	public void appendToChat(ChatText pText) {
		// Don't bother if text is empty (it'll create random, pointless newlines all over)
		if (pText.isEmpty()) { return; }
		
		// Add the text
		pText.output((StyledDocument) this.mChat.getDocument());
	}
	
	/**
	 * Fetches the text in the chat text input box.
	 * @return a string with the input text.
	 */
	public String getCommandLine() {
		return this.getCLI().getText();
	}
	
	public void setCommandLine(String pText) {
		this.getCLI().setText(pText);
	}
	/**
	 * Sets the title of the window to the string parameter
	 * @param title String containing the title to set
	 */
	@Override
	public void setTitle(String title) {
		if (!this.isClosed()) {
			super.setTitle(title);
			int ref = this.getToolbarIndex();
			if (ref >= 0 && ref <= Launcher.getManager().getWindowToolbar().getTabCount()) {
				Launcher.getManager().getWindowToolbar().setTitleAt(ref, title);
			}
		}
	}
	
	/**
	 * Gets the session the window is associated with.
	 * @return the session object the window is associated with.
	 */
	public Session getSession() {
		return this.mSession;
	}
	
	/**
	 * clears the window for text
	 */
	public void clear() {
		this.mChat.setText("");
	}
	/**
	 * Sends a chat message if you're in a window you can chat.
	 * @param pText String containing the text to add to chat.
	 */
	public void say(String pText) {
		this.appendToChat(new ChatText().addError("You can't chat here!"));
	}
	/**
	 * Fetches earlier input from the Cli
	 * @return a list containing strings with earlier user input.
	 */
	public List<String> getCliHistory() {
		return this.mCliHistory;
	}
	/**
	 * Gets the CliSend
	 * @return JButton
	 */
	public JButton getCliSend() {
		return this.mCliSend;
	}
	/**
	 * Gets the timestamp
	 * @return long timestamp
	 */
	public long getTimestamp() {
		return this.mTimestamp;
	}
}

@SuppressWarnings("serial")
/**
 * Part of the what's necessary to make chat lines appear at bottom
 * 
 * @author Hans Martin Bragen
 * @author Jehans Jr. Storvik
 * @author John Høegh-Omdal
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
 * This class is part of what's necessary to make chat lines appear at bottom
 * 
 * @author Hans Martin Bragen
 * @author Jehans Jr. Storvik
 * @author John Høegh-Omdal
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