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

import java.awt.datatransfer.Transferable;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.MOVE;
import javax.swing.table.DefaultTableModel;
import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * Transfer Handler
 *
 */
public class ToDoTransferHandler extends TransferHandler {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private final ToDoPanel panel;

    public ToDoTransferHandler(ToDoPanel panel) {
        this.panel = panel;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return true;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        final JTable.DropLocation dropLocation = (JTable.DropLocation) info.getDropLocation();
        if (dropLocation.isInsertRow()) {
            if (info.getTransferable().isDataFlavorSupported(ToDoRowTransferable.DATA_ROW)) {
                if (!isPriorityColumnSorted()) {
                    String title = Labels.getString("ToDoListPanel.Sort by priority");
                    String message = Labels.getString("ToDoListPanel.ToDos must first be sorted by priority. Sort now?");
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message, title,
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (reply == JOptionPane.OK_OPTION) {
                        /*
                         // sort programatically the priority column
                         panel.getTable().setAutoCreateRowSorter(true);
                         DefaultRowSorter sorter = ((DefaultRowSorter) panel.getTable().getRowSorter());
                         ArrayList<SortKey> list = new ArrayList<SortKey>();
                         list.add(new RowSorter.SortKey(ID_KEY - 6, SortOrder.ASCENDING));
                         sorter.setSortKeys(list);
                         sorter.sort(); // sort the view
                         */
                        panel.setCurrentSelectedRow(0);
                        panel.refresh();
                    }
                } else if (isContinuousSelection()) {
                    final int selectedRowCount = panel.getTable().getSelectedRowCount();
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
                                int[] fromRows = panel.getTable().getSelectedRows();
                                int toRow = dropLocation.getRow();
                                toRow = (toRow < fromRows[0]) ? toRow : toRow - fromRows.length;
                                ((DefaultTableModel) panel.getTable().getModel()).moveRow(fromRows[0], fromRows[fromRows.length - 1], toRow); // fires tableChanged event 
                                for (int row = 0; row < panel.getTable().getRowCount(); row++) {
                                    // Using convertRowIndexToModel is not important here as we force the user to sort the list by priority (view = model)
                                    Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                                    Activity activity = panel.getActivityById(id);
                                    int priority = row + 1;
                                    if (activity.getPriority() != priority) {
                                        activity.setPriority(priority);
                                        activity.databaseUpdate();
                                        ToDoList.getList().update(activity);
                                    }
                                }
                                // Indicate reordoring by priority in progress bar
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
                                // Stop wait cursor
                                WaitCursor.stopWaitCursor();
                                // After cursor stops, reset interval of selected row(s)                                
                                panel.getTable().getSelectionModel().setSelectionInterval(toRow, toRow + fromRows.length - 1);
                            }
                        }
                    }.start();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent cp) {
        int row = panel.getTable().getSelectedRow();
        int colCount = panel.getTable().getColumnCount();
        ArrayList<Object> a = new ArrayList<Object>(colCount);
        for (int i = 0; i < colCount; i++) {
            a.add(panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), i));
        }
        return new ToDoRowTransferable(a, row);
    }

    @Override
    public int getSourceActions(JComponent cp) {
        return MOVE;
    }

    private boolean isPriorityColumnSorted() {
        boolean sorted = true;
        for (int i = 0; i < panel.getTable().getRowCount() - 1; i++) {
            // Look for the value of the priority in the View while column priority might have been moved around                    
            if ((Integer) panel.getTable().getValueAt(i, panel.getTable().convertColumnIndexToView(0)) != (Integer) panel.getTable().getValueAt(i + 1, panel.getTable().convertColumnIndexToView(0)) - 1) {
                sorted = false;
                break;
            }
        }
        return sorted;
    }

    /**
     * Checks gaps in the selection
     *
     * @return true if selection is continuous
     */
    private boolean isContinuousSelection() {
        boolean continuous = true;
        int[] rows = panel.getTable().getSelectedRows();
        int row = rows[0];
        for (int i = 1; i < rows.length; i++) {
            if (row + 1 != rows[i]) {
                continuous = false;
                break;
            }
            row++;
        }
        return continuous;
    }
}
