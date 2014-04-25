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
import static java.lang.Thread.sleep;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mypomodoro.Main;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * Panel that allows the merging of ToDos
 *
 */
public class MergingPanel extends CreatePanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

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
    protected void validActivityAction(final Activity newActivity) {
        newActivity.setIsUnplanned(true);
        StringBuilder comments = new StringBuilder();
        int estimatedPoms = 0;
        int overestimatedPoms = 0;
        int actualPoms = 0;
        final int selectedRowCount = panel.getTable().getSelectedRowCount();
        if (selectedRowCount > 0) {
            int[] rows = panel.getTable().getSelectedRows();
            int increment = 0;
            for (int row : rows) {
                // removing a row requires decreasing the row index number
                row = row - increment;
                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                Activity selectedToDo = panel.getActivityById(id);
                if (panel.getPomodoro().inPomodoro() && selectedToDo.getId() == panel.getPomodoro().getCurrentToDo().getId()) {
                    continue;
                }
                // aggregate comments
                comments.append(selectedToDo.getName());
                if (selectedToDo.getNotes() != null && selectedToDo.getNotes().length() > 0) {
                    comments.append(":\n");
                    comments.append(selectedToDo.getNotes());
                    comments.append("\n\n");
                } else {
                    comments.append(": -");
                    comments.append("\n\n");
                }
                estimatedPoms += selectedToDo.getEstimatedPoms();
                overestimatedPoms += selectedToDo.getOverestimatedPoms();
                actualPoms += selectedToDo.getActualPoms();
                panel.delete(selectedToDo);
                panel.removeRow(row);
                increment++;
            }
            // set comment
            newActivity.setNotes(comments.toString());
            // set estimate
            // make sure the estimate of the new activity is at least equals to the sum of pomodoros already done
            if (actualPoms > 0) {
                if (newActivity.getEstimatedPoms() < actualPoms) {
                    newActivity.setOverestimatedPoms(actualPoms - newActivity.getEstimatedPoms());
                }
                newActivity.setActualPoms(actualPoms);
            }
            final String title = Labels.getString("ToDoListPanel.Merge ToDos");
            if (mergingInputFormPanel.isDateToday() || PreferencesPanel.preferences.getAgileMode()) {
                // we must reorder the priorities BEFORE adding the task to the ToDo list otherwise its priority will be wrong due to previous deletion of tasks
                new Thread() { // This new thread is necessary for updating the progress bar
                    @Override
                    public void run() {
                        if (!WaitCursor.isStarted()) {
                            // Start wait cursor
                            WaitCursor.startWaitCursor();
                            // Set progress bar
                            Main.gui.getProgressBar().setVisible(true);
                            Main.gui.getProgressBar().getBar().setValue(0);
                            Main.gui.getProgressBar().getBar().setMaximum(panel.getPomodoro().inPomodoro() ? selectedRowCount - 1 : selectedRowCount);
                            // Indicate reordoring by priority in progress bar
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Main.gui.getProgressBar().getBar().setValue(Main.gui.getProgressBar().getBar().getMaximum());
                                    Main.gui.getProgressBar().getBar().setString(Labels.getString("ProgressBar.Updating priorities"));
                                }
                            });
                            // When the list has a lot of tasks, the reorderByPriority method is very slow (probably) because there are now gaps in the index of the ToDo list due to previous deletion of tasks
                            panel.reorderByPriority();
                            // Close progress bar
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Main.gui.getProgressBar().getBar().setString(Labels.getString("ProgressBar.Done"));
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                sleep(1000); // wait one second before hiding the progress bar
                                            } catch (InterruptedException ex) {
                                                logger.error("", ex);
                                            }
                                            // hide progress bar
                                            Main.gui.getProgressBar().getBar().setString(null);
                                            Main.gui.getProgressBar().setVisible(false);
                                        }
                                    }.start();
                                }
                            });
                            panel.addActivity(newActivity);
                            // Select new created unplanned task at the bottom of the list before refresh
                            panel.setCurrentSelectedRow(panel.getTable().getRowCount());
                            // Stop wait cursor
                            WaitCursor.stopWaitCursor();
                            // After cursor stops, refresh ToDo List and clear the form
                            panel.refresh();
                            clearForm();
                            String message = Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.Unplanned task added to ToDo List");
                            JOptionPane.showConfirmDialog(Main.gui, message, title,
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }.start();
            } else {
                validation.setVisible(false);
                super.validActivityAction(newActivity); // validation and clear form
                // Indicate reordoring by priority in progress bar
                new Thread() { // This new thread is necessary for updating the progress bar
                    @Override
                    public void run() {
                        if (!WaitCursor.isStarted()) {
                            // Start wait cursor
                            WaitCursor.startWaitCursor();
                            // Set progress bar
                            Main.gui.getProgressBar().setVisible(true);
                            Main.gui.getProgressBar().getBar().setValue(0);
                            Main.gui.getProgressBar().getBar().setMaximum(panel.getPomodoro().inPomodoro() ? selectedRowCount - 1 : selectedRowCount);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Main.gui.getProgressBar().getBar().setValue(Main.gui.getProgressBar().getBar().getMaximum());
                                    Main.gui.getProgressBar().getBar().setString(Labels.getString("ProgressBar.Updating priorities"));
                                }
                            });
                            panel.reorderByPriority();
                            // Close progress bar
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Main.gui.getProgressBar().getBar().setString(Labels.getString("ProgressBar.Done"));
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                sleep(1000); // wait one second before hiding the progress bar
                                            } catch (InterruptedException ex) {
                                                logger.error("", ex);
                                            }
                                            // hide progress bar
                                            Main.gui.getProgressBar().getBar().setString(null);
                                            Main.gui.getProgressBar().setVisible(false);
                                        }
                                    }.start();
                                }
                            });
                            // refresh the whole table
                            panel.refresh();
                            String message = Labels.getString("ToDoListPanel.Unplanned task added to Activity List");
                            // Stop wait cursor
                            WaitCursor.stopWaitCursor();
                            // After cursor stops, refresh Activity List (target list) in case the user is waiting for the list to refresh
                            Main.gui.getActivityListPanel().refresh();
                            JOptionPane.showConfirmDialog(Main.gui, message, title,
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }.start();
            }
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
