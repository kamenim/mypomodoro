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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;

import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

/**
 * Panel that allows overestimating the number of pomodoros of the current ToDo
 *
 */
public class OverestimationPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    protected final OverestimationInputForm overestimationInputFormPanel = new OverestimationInputForm();
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final ToDoPanel panel;
    private final ActivityInformation detailsPanel;

    public OverestimationPanel(ToDoPanel panel, ActivityInformation detailsPanel) {
        this.panel = panel;
        this.detailsPanel = detailsPanel;

        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addToDoIconPanel();
        addOverestimationInputFormPanel();
        addSaveButton();
    }

    private void addSaveButton() {
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
                saveOverestimation();
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

    private void addOverestimationInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.9;
        gbc.gridheight = 1;
        //gbc.gridheight = GridBagConstraints.REMAINDER;
        add(overestimationInputFormPanel, gbc);
    }

    public void saveOverestimation() {
        int overestimatedPomodoros = overestimationInputFormPanel.getOverestimationPomodoros().getSelectedIndex() + 1;
        int row = panel.getTable().getSelectedRow();
        Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
        Activity selectedToDo = panel.getActivityById(id);
        // Overestimation
        selectedToDo.setOverestimatedPoms(selectedToDo.getOverestimatedPoms() + overestimatedPomodoros);
        ToDoList.getList().update(selectedToDo);
        selectedToDo.databaseUpdate();
        panel.getTable().getModel().setValueAt(selectedToDo.getEstimatedPoms(), panel.getTable().convertRowIndexToModel(row), panel.getIdKey() - 3); // update estimated colunm index = 3 (the renderer will do the rest)
        // update details panel
        detailsPanel.selectInfo(selectedToDo);
        detailsPanel.showInfo();
        panel.setPanelRemaining();
        panel.setIconLabels();
        overestimationInputFormPanel.reset();
        String title = Labels.getString("ToDoListPanel.Overestimate ToDo");
        String message = Labels.getString(
                "ToDoListPanel.Nb of estimated pomodoros increased by {0}",
                overestimatedPomodoros);
        JOptionPane.showConfirmDialog(Main.gui, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }
}
