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
import java.awt.Insets;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mypomodoro.Main;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that allows creating unplanned activities and adding interruptions to
 * the current ToDo
 *
 */
public class UnplannedPanel extends CreatePanel {

    private UnplannedActivityInputForm unplannedInputFormPanel;
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final ToDoPanel panel;

    public UnplannedPanel(ToDoPanel todoPanel) {
        this.panel = todoPanel;
        unplannedInputFormPanel.setEstimatedPomodoro(0);
        setBorder(null); // remove create panel border
        addToDoIconPanel();
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
    
    @Override
    protected void addInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        unplannedInputFormPanel = new UnplannedActivityInputForm();
        unplannedInputFormPanel.getNameField().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableSaveButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (unplannedInputFormPanel.getNameField().getText().length() == 0) {
                    disableSaveButton();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                enableSaveButton();
            }
        });
        add(new JScrollPane(unplannedInputFormPanel), gbc);
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        //gbc.fill = GridBagConstraints.NONE;
        disableSaveButton();
        add(saveButton, gbc);
    }

    @Override
    protected void addClearButton() {
    }

    @Override
    protected void addValidation() {
    }

    @Override
    protected void validActivityAction(Activity newActivity) {
        // Interruptions : update current/running pomodoro
        Activity currentToDo = panel.getPomodoro().getCurrentToDo();
        if (currentToDo != null && panel.getPomodoro().inPomodoro()) {
            if (unplannedInputFormPanel.isSelectedInternalInterruption()) {
                currentToDo.incrementInternalInter();
                currentToDo.databaseUpdate();
            } else if (unplannedInputFormPanel.isSelectedExternalInterruption()) {
                currentToDo.incrementInter();
                currentToDo.databaseUpdate();
            }
        }
        newActivity.setIsUnplanned(true);
        String title = Labels.getString("ToDoListPanel.Add Unplanned activity");
        String message;
        if (unplannedInputFormPanel.isDateToday() || PreferencesPanel.preferences.getAgileMode()) {
            panel.addActivity(newActivity);
            // Select new created unplanned task at the bottom of the list before refresh
            panel.setCurrentSelectedRow(panel.getTable().getRowCount());
            // refresh the whole table
            panel.refresh();
            clearForm();
            message = Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.Unplanned task added to ToDo List");
        } else {
            validation.setVisible(false);
            super.validActivityAction(newActivity); // validation and clear form
            // refresh the whole table
            panel.refresh();
            message = Labels.getString("ToDoListPanel.Unplanned task added to Activity List");
        }
        JOptionPane.showConfirmDialog(Main.gui, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void invalidActivityAction() {
        String title = Labels.getString("Common.Error");
        String message = Labels.getString("Common.Title is mandatory");
        JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return unplannedInputFormPanel;
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }

    @Override
    public void clearForm() {
        unplannedInputFormPanel.setInterruption(0);
        unplannedInputFormPanel.setNameField("");
        unplannedInputFormPanel.setEstimatedPomodoro(0);
        if (PreferencesPanel.preferences.getAgileMode()) {
            unplannedInputFormPanel.setStoryPoints(0);
            unplannedInputFormPanel.setIterations(0);
        }
        unplannedInputFormPanel.setDescriptionField("");
        unplannedInputFormPanel.setTypeField("");
        unplannedInputFormPanel.setAuthorField("");
        unplannedInputFormPanel.setPlaceField("");
        unplannedInputFormPanel.setDate(new Date());
    }
}
