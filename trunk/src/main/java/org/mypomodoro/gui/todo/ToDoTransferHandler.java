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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.MOVE;
import javax.swing.table.DefaultTableModel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;

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

    // TODO problem with dragging after manual sorting of one of the other columns
    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        JTable.DropLocation dropLocation = (JTable.DropLocation) info.getDropLocation();
        if (dropLocation.isInsertRow()) {
            if (info.getTransferable().isDataFlavorSupported(ToDoRowTransferable.DATA_ROW)) {
                try {
                    Object[] myObject = (Object[]) info.getTransferable().getTransferData(ToDoRowTransferable.DATA_ROW);
                    int fromRow = (Integer) myObject[0];
                    Object[] data = (Object[]) ((ArrayList<Object>) myObject[1]).toArray();
                    panel.removeRow(fromRow);
                    int toRow = dropLocation.getRow();
                    toRow = (toRow == 0) ? toRow : toRow - 1;
                    ((DefaultTableModel) panel.getTable().getModel()).insertRow(toRow, data);
                    panel.getTable().getSelectionModel().setSelectionInterval(toRow, toRow);
                    for (int row = 0; row < panel.getTable().getModel().getRowCount(); row++) {
                        Integer id = (Integer) panel.getTable().getModel().getValueAt(row, panel.getIdKey());
                        Activity activity = panel.getActivityById(id);
                        panel.getTable().getModel().setValueAt(row + 1, row, 0);
                        activity.setPriority(row + 1);
                        activity.databaseUpdate();
                        ToDoList.getList().update(activity);
                    }
                    return true;
                } catch (UnsupportedFlavorException e) {
                    // do nothing
                } catch (IOException e) {
                    // do nothing
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
}
