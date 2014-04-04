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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;

import org.mypomodoro.buttons.CompleteToDoButton;
import org.mypomodoro.buttons.MoveToDoButton;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.gui.activities.ActivityInformationPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays information on the selected Pomodoro
 *
 */
public class DetailsPanel extends ActivityInformationPanel implements ActivityInformation {

    private static final long serialVersionUID = 20110814L;
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final GridBagConstraints gbc = new GridBagConstraints();
    private MoveToDoButton moveButton;
    private CompleteToDoButton completeButton;

    public DetailsPanel(ToDoPanel todoPanel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addMoveButton(todoPanel);
        addInformationPanel();
        addCompleteButton(todoPanel);
    }

    private void addMoveButton(ToDoPanel todoPanel) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        moveButton = new MoveToDoButton("<<<", todoPanel);
        moveButton.setFont(new Font(Main.font.getName(), Font.BOLD, Main.font.getSize() + 4));
        add(moveButton, gbc);
    }

    private void addInformationPanel() {
        JPanel infoPanel = new JPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 2;

        GridBagConstraints igbc = new GridBagConstraints();
        infoPanel.setLayout(new GridBagLayout());
        addToDoIconPanel(infoPanel, igbc);
        addInformationArea(infoPanel, igbc);
        add(infoPanel, gbc);
    }

    private void addToDoIconPanel(JPanel infoPanel, GridBagConstraints igbc) {
        igbc.gridx = 0;
        igbc.gridy = 0;
        igbc.fill = GridBagConstraints.BOTH;
        igbc.weightx = 1.0;
        igbc.weighty = 0.1;
        igbc.gridheight = 1;
        igbc.insets = new Insets(0, 3, 0, 0); // margin left
        infoPanel.add(iconLabel, igbc);
        igbc.insets = new Insets(0, 0, 0, 0); // no margin anymore        
    }

    private void addInformationArea(JPanel infoPanel, GridBagConstraints igbc) {
        // add the information area
        igbc.gridx = 0;
        igbc.gridy = 1;
        igbc.fill = GridBagConstraints.BOTH;
        igbc.weightx = 1.0;
        igbc.weighty = 1.0;
        igbc.gridheight = GridBagConstraints.REMAINDER;
        informationArea.setEditable(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        infoPanel.add(new JScrollPane(informationArea), igbc);
    }

    private void addCompleteButton(ToDoPanel todoPanel) {
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        completeButton = new CompleteToDoButton(Labels.getString("ToDoListPanel.Complete ToDo"), Labels.getString("ToDoListPanel.Are you sure to complete those ToDo?"), todoPanel);
        add(completeButton, gbc);
    }

    @Override
    public void selectInfo(Activity activity) {
        super.selectInfo(activity);
        textMap.remove("date_reopened");        
        if (PreferencesPanel.preferences.getAgileMode()) {
            textMap.remove("storypoints");
            textMap.remove("iteration");
        }
        textMap.remove("date_completed");
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }

    public void disableButtons() {
        moveButton.setEnabled(false);
        moveButton.setOpaque(false);
        moveButton.setForeground(Color.GRAY);
        completeButton.setEnabled(false);
        completeButton.setOpaque(false);
        completeButton.setForeground(Color.GRAY);

    }

    public void enableButtons() {
        moveButton.setEnabled(true);
        moveButton.setOpaque(true);
        moveButton.setForeground(ColorUtil.BLACK);
        completeButton.setEnabled(true);
        completeButton.setOpaque(true);
        completeButton.setForeground(ColorUtil.BLACK);
    }
}
