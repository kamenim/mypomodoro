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
package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.mypomodoro.Main;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.reports.ReportsPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * Move button for activities and reports panels. For ToDo panel, see
 * MoveToDoButton.
 *
 */
public class MoveButton extends TabPanelButton {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public MoveButton(String label, final IListPanel panel) {
        super(label);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                move(panel);
            }
        });
    }

    public void move(final IListPanel panel) {
        final int selectedRowCount = panel.getTable().getSelectedRowCount();
        if (selectedRowCount > 0) {
            new Thread() { // This new thread is necessary for updating the progress bar
                @Override
                public void run() {
                    if (!WaitCursor.isStarted()) {
                        // Start wait cursor
                        WaitCursor.startWaitCursor();
                        // Disable button
                        setEnabled(false);
                        // Set progress bar
                        Main.gui.getProgressBar().setVisible(true);
                        Main.gui.getProgressBar().getBar().setValue(0);
                        Main.gui.getProgressBar().getBar().setMaximum(selectedRowCount);
                        // SKIP optimisation -move all tasks at once- to take benefice of the progress bar; slower but better for the user)
                    /*if (selectedRowCount == panel.getTable().getRowCount()
                         && panel instanceof ReportsPanel) { // reopen all at once                
                         panel.moveAll();
                         panel.refresh();
                         } else {*/
                        int increment = 0;
                        int[] rows = panel.getTable().getSelectedRows();
                        for (int row : rows) {
                            // removing a row requires decreasing the row index number
                            row = row - increment;
                            Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                            Activity selectedActivity = panel.getActivityById(id);
                            if (panel instanceof ActivitiesPanel && !PreferencesPanel.preferences.getAgileMode()) {
                                String activityName = selectedActivity.getName().length() > 25 ? selectedActivity.getName().substring(0, 25) + "..." : selectedActivity.getName();
                                if (!selectedActivity.isDateToday()) {
                                    String title = Labels.getString("ActivityListPanel.Add activity to ToDo List");
                                    String message = Labels.getString("ActivityListPanel.The date of activity {0} is not today. Proceed anyway?", activityName);
                                    int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                                            title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                    if (reply == JOptionPane.NO_OPTION) {
                                        continue; // go to the next one
                                    } else if (reply == JOptionPane.CLOSED_OPTION) {
                                        break;
                                    }
                                }
                                if (isMaxNbTotalEstimatedPomReached(selectedActivity)) {
                                    String title = Labels.getString("ActivityListPanel.Add activity to ToDo List");
                                    String message = Labels.getString(
                                            "ActivityListPanel.Max nb of pomodoros per day reached ({0}). Proceed anyway?",
                                            org.mypomodoro.gui.preferences.PreferencesPanel.preferences.getMaxNbPomPerDay());
                                    int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                                            title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                    if (reply != JOptionPane.YES_OPTION) {
                                        break; // get out of the loop
                                    }
                                }
                            }
                            panel.move(selectedActivity);
                            panel.removeRow(row);
                            increment++;
                            final int progressValue = increment;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Main.gui.getProgressBar().getBar().setValue(progressValue); // % - required to see the progress                                    
                                    Main.gui.getProgressBar().getBar().setString(Integer.toString(progressValue) + " / " + Integer.toString(selectedRowCount)); // task
                                }
                            });
                        }
                        //}
                        // Close progress bar
                        final int progressCount = increment;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Main.gui.getProgressBar().getBar().setString(Labels.getString("ProgressBar.Done") + " (" + progressCount + ")");
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
                        // Enable button
                        setEnabled(true);
                        // Stop wait cursor
                        WaitCursor.stopWaitCursor();
                        // After cursor stops, refresh Activity/Report List (target list) in case the user is waiting for the list to refresh
                        if (panel instanceof ActivitiesPanel) {
                            Main.gui.getToDoPanel().refresh();
                        } else if (panel instanceof ReportsPanel) { // reopen tasks
                            Main.gui.getActivityListPanel().refresh();
                        }
                    }
                }
            }.start();
        }
    }

    private boolean isMaxNbTotalEstimatedPomReached(Activity activity) {
        int nbTotalEstimatedPom = ToDoList.getList().getNbTotalEstimatedPom();
        int nbTotalEstimatedPomWithActivity = nbTotalEstimatedPom + activity.getEstimatedPoms() + activity.getOverestimatedPoms();
        return nbTotalEstimatedPom <= PreferencesPanel.preferences.getMaxNbPomPerDay() && nbTotalEstimatedPomWithActivity > PreferencesPanel.preferences.getMaxNbPomPerDay();
    }
}
