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
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mypomodoro.Main;
import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that allows the merging of ToDos
 *
 */
public class MergingPanel extends CreatePanel {

    private static final long serialVersionUID = 20110814L;
    private ActivityInputForm mergingInputFormPanel;
    private final ToDoPanel panel;

    public MergingPanel(ToDoPanel todoPanel) {
        this.panel = todoPanel;
        mergingInputFormPanel.setEstimatedPomodoro(1);
    }

    @Override
    protected void addInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        mergingInputFormPanel = new ActivityInputForm();
        mergingInputFormPanel.getNameField().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableSaveButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (mergingInputFormPanel.getNameField().getText().length() == 0) {
                    disableSaveButton();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                enableSaveButton();
            }
        });
        add(new JScrollPane(mergingInputFormPanel), gbc);
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
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
        newActivity.setIsUnplanned(true);
        String title = Labels.getString("ToDoListPanel.Merge ToDos");
        String message;
        StringBuilder comments = new StringBuilder();
        int actualPoms = 0;
        if (panel.getTable().getSelectedRowCount() > 0) {
            int[] rows = panel.getTable().getSelectedRows();
            int increment = 0;
            for (int row : rows) {
                row = row - increment;
                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                Activity selectedToDo = panel.getActivityById(id);
                if (panel.getPomodoro().inPomodoro() && selectedToDo.getId() == panel.getPomodoro().getCurrentToDo().getId()) {
                    continue;
                }
                // aggregate comments
                if (selectedToDo.getNotes() != null && selectedToDo.getNotes().length() > 0) {
                    comments.append(selectedToDo.getName());
                    comments.append(":\n");
                    comments.append(selectedToDo.getNotes());
                    comments.append("\n\n");
                }
                actualPoms += selectedToDo.getActualPoms();
                panel.delete(selectedToDo);
                // removing a row requires decreasing the row index number
                panel.removeRow(row);
                increment++;
            }
            // set comment
            newActivity.setNotes(comments.toString());
            // set estimate
            // make sure the estimate of the new activity is at least one pomodoro higher than the sum of pomodoros already done (if any)            
            if (actualPoms > 0 && newActivity.getEstimatedPoms() <= actualPoms) {
                newActivity.setEstimatedPoms(actualPoms + 1);
            }
            if (actualPoms > 0) {
                newActivity.setActualPoms(actualPoms);
            }
            if (mergingInputFormPanel.isDateToday()) {
                message = Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.Unplanned task added to ToDo List");
                panel.addActivity(newActivity);
                panel.reorderByPriority();
                // TODO insert row instead of refresh?
                panel.refresh();
                panel.refreshRemaining();
                clearForm();
            } else {
                message = Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.Unplanned task added to Activity List");
                validation.setVisible(false);
                super.validActivityAction(newActivity); // validation and clear form
            }
            JOptionPane.showConfirmDialog(Main.gui, message, title,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    protected void invalidActivityAction() {
        String title = Labels.getString("Common.Error");
        String message = Labels.getString("Common.Title is mandatory");
        JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return mergingInputFormPanel;
    }

    @Override
    public void clearForm() {
        mergingInputFormPanel.setNameField("");
        mergingInputFormPanel.setEstimatedPomodoro(1);
        if (PreferencesPanel.preferences.getAgileMode()) {
            mergingInputFormPanel.setStoryPoints(0);
            mergingInputFormPanel.setIterations(0);
        }
        mergingInputFormPanel.setDescriptionField("");
        mergingInputFormPanel.setTypeField("");
        mergingInputFormPanel.setAuthorField("");
        mergingInputFormPanel.setPlaceField("");
        mergingInputFormPanel.setDate(new Date());
    }
}
