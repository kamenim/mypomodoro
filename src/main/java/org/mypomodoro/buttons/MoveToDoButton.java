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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import javax.swing.SwingUtilities;
import org.mypomodoro.Main;
import org.mypomodoro.gui.todo.ToDoPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * Move button to move tasks back to Activity list
 *
 */
public class MoveToDoButton extends AbstractButton {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension BUTTON_SIZE = new Dimension(100, 30);

    public MoveToDoButton(String label, final ToDoPanel panel) {
        super(label);
        setMinimumSize(BUTTON_SIZE);
        setPreferredSize(BUTTON_SIZE);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                move(panel);
            }
        });
    }

    public void move(final ToDoPanel panel) {
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
                        Main.gui.getProgressBar().getBar().setMaximum(panel.getPomodoro().inPomodoro() ? selectedRowCount - 1 : selectedRowCount);
                        // SKIP optimisation -move all tasks at once- to take benefice of the progress bar; slower but better for the user)
                    /*if (!panel.getPomodoro().inPomodoro()
                         && panel.getTable().getSelectedRowCount() == panel.getTable().getRowCount()) { // complete all at once                       
                         panel.moveAll();
                         panel.refresh();
                         } else {*/
                        int increment = 0;
                        int[] rows = panel.getTable().getSelectedRows();
                        for (int row : rows) {
                            // removing a row requires decreasing the row index number
                            row = row - increment;
                            Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                            Activity selectedToDo = panel.getActivityById(id);
                            // excluding current running task
                            if (panel.getPomodoro().inPomodoro() && selectedToDo.getId() == panel.getPomodoro().getCurrentToDo().getId()) {
                                continue;
                            }
                            panel.move(selectedToDo);
                            panel.removeRow(row);
                            increment++;
                            final int progressValue = increment;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Main.gui.getProgressBar().getBar().setValue(progressValue); // % - required to see the progress
                                    Main.gui.getProgressBar().getBar().setString(Integer.toString(progressValue) + " / " + (panel.getPomodoro().inPomodoro() ? Integer.toString(selectedRowCount - 1) : Integer.toString(selectedRowCount))); // task
                                }
                            });
                        }
                        //}
                        // Indicate reordoring by priority in progress bar
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Main.gui.getProgressBar().getBar().setValue(Main.gui.getProgressBar().getBar().getMaximum());
                                Main.gui.getProgressBar().getBar().setString(Labels.getString("ProgressBar.Updating priorities"));
                            }
                        });
                        // When the list has a lot of tasks, the reorderByPriority method is very slow (probably) because there are now gaps in the index of the ToDo list due to previous deletion (removal) of tasks                            
                        panel.reorderByPriority();
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
                        // After cursor stops, refresh Activity List (target list) in case the user is waiting for the list to refresh
                        Main.gui.getActivityListPanel().refresh();
                    }
                }
            }.start();
        }
    }
}
