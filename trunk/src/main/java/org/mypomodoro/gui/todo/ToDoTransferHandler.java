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

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import javax.swing.DefaultRowSorter;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.MOVE;
import javax.swing.table.DefaultTableModel;
import org.mypomodoro.Main;
import static org.mypomodoro.gui.todo.ToDoPanel.ID_KEY;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

/**
 * Transfer Handler
 *
 */
public class ToDoTransferHandler extends TransferHandler {

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
        JTable.DropLocation dropLocation = (JTable.DropLocation) info.getDropLocation();
        if (dropLocation.isInsertRow()) {
            if (info.getTransferable().isDataFlavorSupported(ToDoRowTransferable.DATA_ROW)) {
                if (!isPriorityColumnSorted()) {
                    String title = Labels.getString("ToDoListPanel.Sort by priority");
                    String message = Labels.getString("ToDoListPanel.ToDos must first be sorted by priority. Sort now?");
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message, title,
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (reply == JOptionPane.OK_OPTION) {
                        // sort programatically the priority column
                        panel.getTable().setAutoCreateRowSorter(true);
                        DefaultRowSorter sorter = ((DefaultRowSorter) panel.getTable().getRowSorter());
                        ArrayList<SortKey> list = new ArrayList<SortKey>();
                        list.add(new RowSorter.SortKey(ID_KEY - 6, SortOrder.ASCENDING));
                        sorter.setSortKeys(list);
                        sorter.sort(); // sort the view
                    }
                } else if (isContinuousSelection()) {
                    try {
                        int[] fromRows = panel.getTable().getSelectedRows();
                        int toRow = dropLocation.getRow();
                        toRow = (toRow < fromRows[0]) ? toRow : toRow - fromRows.length;
                        ((DefaultTableModel) panel.getTable().getModel()).moveRow(fromRows[0], fromRows[fromRows.length - 1], toRow); // fires tableChanged event
                        // TODO selection interval wrong after manual sorting
                        panel.getTable().getSelectionModel().setSelectionInterval(toRow, toRow + fromRows.length - 1);
                        for (int row = 0; row < panel.getTable().getModel().getRowCount(); row++) {
                            Integer id = (Integer) panel.getTable().getModel().getValueAt(row, panel.getIdKey());
                            Activity activity = panel.getActivityById(id);
                            panel.getTable().getModel().setValueAt(row + 1, row, 0); // set the value in the model
                            if (activity.getPriority() != row + 1) { // optimization
                                activity.setPriority(row + 1);
                                activity.databaseUpdate();
                                ToDoList.getList().update(activity);
                            }
                        }
                        return true;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // do nothing. This should never happen as the TableChanged method in ToDoPanel already handles the problem of column been equals to -1 while moving
                    }
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
