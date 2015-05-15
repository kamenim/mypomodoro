/* 
 * Copyright (C) 
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.buttons.TabPanelButton;
import org.mypomodoro.gui.IActivityInformation;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

/**
 * Panel that allows overestimating the number of pomodoros of the current ToDo
 *
 */
public class OverestimationPanel extends JPanel {

    protected final OverestimationInputForm overestimationInputFormPanel = new OverestimationInputForm();
    private final JPanel iconPanel = new JPanel();
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final ToDoPanel panel;
    private final IActivityInformation detailsPanel;

    public OverestimationPanel(ToDoPanel panel, IActivityInformation detailsPanel) {
        this.panel = panel;
        this.detailsPanel = detailsPanel;

        setLayout(new GridBagLayout());
        setBorder(null);

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
        JButton changeButton = new TabPanelButton(
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
        gbc.gridheight = 1;
        iconPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        add(iconPanel, gbc);
    }

    private void addOverestimationInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(overestimationInputFormPanel, gbc);
    }

    public void saveOverestimation() {
        int overestimatedPoms = overestimationInputFormPanel.getOverestimationPomodoros().getSelectedIndex() + 1;
        overestimateTask(overestimatedPoms);
        overestimationInputFormPanel.reset();
    }

    // Overestimation only when estimated != 0 and real >= estimated
    public void overestimateTask(int overestimatedPoms) {
        Activity selectedToDo = panel.getCurrentTable().getActivityFromSelectedRow();
        if (selectedToDo.getEstimatedPoms() != 0 
                && selectedToDo.getActualPoms() >= selectedToDo.getEstimatedPoms()
                && (selectedToDo.isSubTask() || !ToDoList.hasSubTasks(selectedToDo.getId()))) {
            // Overestimation
            selectedToDo.setOverestimatedPoms(selectedToDo.getOverestimatedPoms() + overestimatedPoms);
            ToDoList.getList().update(selectedToDo);
            selectedToDo.databaseUpdate();
            if (selectedToDo.isSubTask()) {
                panel.getMainTable().addPomsToSelectedRow(0, 0, overestimatedPoms);
                panel.getMainTable().setTitle();
            }
            panel.getCurrentTable().repaint();
            panel.getCurrentTable().setTitle();
            // update details panel
            detailsPanel.selectInfo(selectedToDo);
            detailsPanel.showInfo();
            //panel.setPanelRemaining();
            panel.getCurrentTable().setIconLabels();
        }
    }

    public JPanel getIconPanel() {
        return iconPanel;
    }
}
