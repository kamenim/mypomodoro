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
import javax.swing.SwingUtilities;
import org.mypomodoro.Main;
import org.mypomodoro.gui.burndownchart.CheckPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * Remove button for Chart list
 *
 */
public class RemoveButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension BUTTON_SIZE = new Dimension(100, 30);

    public RemoveButton(String label, final CheckPanel panel) {
        super(label);
        setMinimumSize(BUTTON_SIZE);
        setPreferredSize(BUTTON_SIZE);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                remove(panel);
            }
        });
    }

    public void remove(final CheckPanel panel) {
        final int selectedRowCount = panel.getTable().getSelectedRowCount();
        if (selectedRowCount > 0) {
            new Thread() { // This new thread is necessary for updating the progress bar
                @Override
                public void run() {
                    // Disable button
                    setEnabled(false);
                    // Set progress bar
                    Main.gui.getProgressBar().setVisible(true);
                    Main.gui.getProgressBar().getBar().setValue(0);
                    Main.gui.getProgressBar().getBar().setMaximum(selectedRowCount);
                    // Start wait cursor
                    WaitCursor.startWaitCursor();
                    int increment = 0;
                    int[] rows = panel.getTable().getSelectedRows();
                    for (int row : rows) {
                        // removing a row requires decreasing the row index number
                        row = row - increment;
                        Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                        Activity selectedActivity = panel.getActivityById(id);
                        ChartList.getList().remove(selectedActivity);
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
                                        // do nothing
                                    }
                                    // hide progress bar
                                    Main.gui.getProgressBar().getBar().setString(null);
                                    Main.gui.getProgressBar().setVisible(false);
                                }
                            }.start();
                        }
                    });
                    // Refresh panel border
                    panel.setPanelBorder();
                    // Enable button
                    setEnabled(true);
                    // Stop wait cursor
                    WaitCursor.stopWaitCursor();
                }
            }.start();
        }
    }
}
