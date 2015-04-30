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
package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.mypomodoro.Main;
import org.mypomodoro.gui.ImageIcons;
import org.mypomodoro.gui.MainPanel;
import org.mypomodoro.gui.todo.ToDoPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * Delete button
 *
 */
public class CompleteToDoButton extends TabPanelButton {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public CompleteToDoButton(final String title, final String message, final ToDoPanel panel) {
        super(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.Complete"));
        setToolTipText("SHIFT + >");
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final int selectedRowCount = panel.getCurrentTable().getSelectedRowCount();
                if (selectedRowCount > 0) {
                    new Thread() { // This new thread is necessary for updating the progress bar
                        @Override
                        public void run() {
                            int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ImageIcons.DIALOG_ICON);
                            if (reply == JOptionPane.YES_OPTION) {
                                if (!WaitCursor.isStarted()) {
                                    // Start wait cursor
                                    WaitCursor.startWaitCursor();
                                    // Disable button
                                    setEnabled(false);
                                    // Set progress bar
                                    MainPanel.progressBar.setVisible(true);
                                    MainPanel.progressBar.getBar().setValue(0);
                                    MainPanel.progressBar.getBar().setMaximum(panel.getPomodoro().inPomodoro() ? selectedRowCount - 1 : selectedRowCount);
                                    // SKIP optimisation -move all tasks at once- to take benefice of the progress bar; slower but better for the user)
                                    /*if (!panel.getPomodoro().inPomodoro() && panel.getCurrentTable().getSelectedRowCount() == panel.getCurrentTable().getRowCount()) { // complete all at once                       
                                     int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ImageIcons.DIALOG_ICON);
                                     if (reply == JOptionPane.YES_OPTION) {
                                     panel.completeAll();
                                     panel.refresh();
                                     }
                                     } else {*/
                                    int increment = 0;
                                    int[] rows = panel.getCurrentTable().getSelectedRows();
                                    for (int row : rows) {
                                        if (!MainPanel.progressBar.isStopped()) {
                                            // removing a row requires decreasing the row index number
                                            row = row - increment;
                                            Activity selectedActivity = panel.getCurrentTable().getActivityFromRowIndex(row);
                                            // excluding current running task
                                            if (panel.getPomodoro().inPomodoro() && selectedActivity.getId() == panel.getPomodoro().getCurrentToDo().getId()) {
                                                if (rows.length > 1) {
                                                    continue;
                                                } else {
                                                    break;
                                                }
                                            }
                                            panel.getCurrentTable().completeTask(row);
                                            Main.gui.getReportListPanel().getMainTable().insertRow(selectedActivity); // main table !
                                            increment++;
                                            final int progressValue = increment;
                                            SwingUtilities.invokeLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    MainPanel.progressBar.getBar().setValue(progressValue); // % - required to see the progress
                                                    MainPanel.progressBar.getBar().setString(Integer.toString(progressValue) + " / " + (panel.getPomodoro().inPomodoro() ? Integer.toString(selectedRowCount - 1) : Integer.toString(selectedRowCount))); // task
                                                }
                                            });
                                        }
                                    }
                                    //}
                                    // Indicate reordoring by priority in progress bar
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainPanel.progressBar.getBar().setValue(MainPanel.progressBar.getBar().getMaximum());
                                            MainPanel.progressBar.getBar().setString(Labels.getString("ProgressBar.Updating priorities"));
                                        }
                                    });
                                    // When the list has a lot of tasks, the reorderByPriority method is very slow (probably) because there are now gaps in the index of the ToDo list due to previous deletion (removal) of tasks                            
                                    panel.getCurrentTable().reorderByPriority();
                                    // Close progress bar
                                    final int progressCount = increment;
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainPanel.progressBar.getBar().setString(Labels.getString("ProgressBar.Done") + " (" + progressCount + ")");
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        sleep(1000); // wait one second before hiding the progress bar
                                                    } catch (InterruptedException ex) {
                                                        logger.error("", ex);
                                                    }
                                                    // hide progress bar
                                                    MainPanel.progressBar.getBar().setString(null);
                                                    MainPanel.progressBar.setVisible(false);
                                                    MainPanel.progressBar.setStopped(false);
                                                }
                                            }.start();
                                        }
                                    });
                                    // Enable button
                                    setEnabled(true);
                                    // Stop wait cursor
                                    WaitCursor.stopWaitCursor();
                                }
                            }
                        }
                    }.start();
                }
            }
        });
    }
}
