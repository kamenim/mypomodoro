package org.mypomodoro.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JPanel;

public class WindowPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;

    public WindowPanel(JPanel iconBar, Container panel) {
        setLayout(new BorderLayout());
        setOpaque(true);
        add(iconBar, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }
}