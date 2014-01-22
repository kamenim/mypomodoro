package org.mypomodoro.gui.manager;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.mypomodoro.buttons.MoveButton;

public class ControlPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;

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
        MoveButton mRight = new MoveButton(">>>", activitiesPane, todoPane);
        mRight.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize() + 6));
        add(mRight, gbc);
        // Adding the remove button from todo to activities list
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        MoveButton mLeft = new MoveButton("<<<", todoPane, activitiesPane);
        mLeft.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize() + 6));
        add(mLeft, gbc);
    }
}
