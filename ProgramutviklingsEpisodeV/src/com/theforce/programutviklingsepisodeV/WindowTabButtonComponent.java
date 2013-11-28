package com.theforce.programutviklingsepisodeV;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
class WindowTabButtonComponent extends JPanel {
	private final JTabbedPane mTabPane;
	public WindowTabButtonComponent(final JTabbedPane pTabPane, final Icon icon) {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        // Check the tab pane
        if (pTabPane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.mTabPane = pTabPane;
        
        // Make transparent
        this.setOpaque(false);
        
        // Set icon
        JLabel iconLabel = new JLabel(icon);
        this.add(iconLabel);
        
        // Set some spacing
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        
        // Set label
        JLabel label = new JLabel() {
        	@Override
        	public String getText() {
                int index = WindowTabButtonComponent.this.mTabPane.indexOfTabComponent(WindowTabButtonComponent.this);
                if (index != -1) {
                    return WindowTabButtonComponent.this.mTabPane.getTitleAt(index);
                }
                return null;
        	}
        };
        
        this.add(label);
	}
}
