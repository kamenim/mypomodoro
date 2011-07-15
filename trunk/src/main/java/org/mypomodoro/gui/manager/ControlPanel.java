package org.mypomodoro.gui.manager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.mypomodoro.buttons.MoveButton;

public class ControlPanel extends JPanel {

    public ControlPanel(ListPane activitiesPane, ListPane todoPane) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Adding the add button from activities to todo lists
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(new MoveButton(">>>", activitiesPane, todoPane), gbc);
        // Adding the remove button from todo to activities list
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(new MoveButton("<<<", todoPane, activitiesPane), gbc);
    }
}