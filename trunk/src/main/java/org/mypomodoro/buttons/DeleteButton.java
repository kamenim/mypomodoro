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
import static java.lang.Thread.sleep;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.mypomodoro.Main;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.MainPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * Delete button
 *
 */
public class DeleteButton extends TabPanelButton {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public DeleteButton(final String title, final String message, final IListPanel panel) {
        super(Labels.getString("Common.Delete"));
        setToolTipText("DEL");
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final int selectedRowCount = panel.getTable().getSelectedRowCount();
                if (selectedRowCount > 0) {
                    new Thread() { // This new thread is necessary for updating the progress bar
                        @Override
                        public void run() {
                            int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (reply == JOptionPane.YES_OPTION) {
                                // Disable button
                                setEnabled(false);
                                // Set progress bar
                                MainPanel.progressBar.setVisible(true);
                                MainPanel.progressBar.getBar().setValue(0);
                                MainPanel.progressBar.getBar().setMaximum(selectedRowCount);
                                /*if (panel.getTable().getSelectedRowCount() == panel.getTable().getRowCount()) { // delete all at once                        
                                 panel.deleteAll();
                                 panel.refresh();
                                 } else {*/
                                int increment = 0;
                                int[] rows = panel.getTable().getSelectedRows();
                                for (int row : rows) {
                                    // removing a row requires decreasing  the row index number
                                    row = row - increment;
                                    Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                                    Activity selectedActivity = panel.getActivityById(id);
                                    panel.delete(selectedActivity);
                                    panel.removeRow(row);
                                    increment++;
                                    final int progressValue = increment;
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainPanel.progressBar.getBar().setValue(progressValue); // % - required to see the progress
                                            MainPanel.progressBar.getBar().setString(Integer.toString(progressValue) + " / " + Integer.toString(selectedRowCount)); // task
                                        }
                                    });
                                }
                                //}
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
                    }.start();
                }
            }
        });
    }
}
