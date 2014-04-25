/* 
 * Copyright (C) 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.activities.ActivityInformationPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays comment on the selected ToDo and allows editing it
 *
 */
public class CommentPanel extends ActivityInformationPanel {

    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final GridBagConstraints gbc = new GridBagConstraints();

    public CommentPanel(ToDoPanel todoPanel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addToDoIconPanel();
        addCommentArea();
        addSaveButton(todoPanel);
    }

    private void addSaveButton(final ToDoPanel todoPanel) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        // gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new AbstractButton(
                Labels.getString("Common.Save"));
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                todoPanel.saveComment(informationArea.getText());
            }
        });
        add(changeButton, gbc);
    }

    private void addToDoIconPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 3, 0, 0); // margin left
        add(iconLabel, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
    }

    private void addCommentArea() {
        // add the comment area
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        informationArea.setEditable(true);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        add(new JScrollPane(informationArea), gbc);
    }

    @Override
    public void selectInfo(Activity activity) {
        // template for user stories and epics
        if (PreferencesPanel.preferences.getAgileMode()
                && activity.getNotes().trim().length() == 0
                && activity.isStory()) {
            StringBuilder text = new StringBuilder();
            text.append("Story line" + "\n");
            text.append("-------------" + "\n");
            text.append("As a <user role>, I want to <action> in order to <purpose>" + "\n\n");
            text.append("User acceptance criteria" + "\n");
            text.append("----------------------------------" + "\n");
            text.append("* " + "\n");
            text.append("* " + "\n\n");
            text.append("Test cases" + "\n");
            text.append("----------------" + "\n");
            text.append("* " + "\n");
            text.append("* ");
            textMap.put("comment", text.toString());
        } else {
            textMap.put("comment", activity.getNotes());
        }
    }

    @Override
    public boolean isMultipleSelectionAllowed() {
        return false;
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }
}
