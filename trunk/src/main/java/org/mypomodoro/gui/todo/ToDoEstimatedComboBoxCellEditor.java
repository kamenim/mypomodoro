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

import java.awt.Component;
import javax.swing.JTable;
import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;

/**
 *
 *
 */
class ToDoEstimatedComboBoxCellEditor extends ToDoComboBoxCellEditor {

    public <E> ToDoEstimatedComboBoxCellEditor(E[] data, boolean editable) {
        super(data, editable);

        // Custom display hovered item value
        comboBox.setRenderer(new ComboBoxEstimatedLengthRenderer());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), table.getModel().getColumnCount() - 1);
        Activity activity = ToDoList.getList().getById(id);
        if (activity != null) {
            if (activity.getEstimatedPoms() == 0) {
                comboBox.removeAllItems();
                for (int i = 0; i <= (activity.getEstimatedPoms() >= Main.preferences.getMaxNbPomPerActivity() ? activity.getEstimatedPoms() + Main.preferences.getMaxNbPomPerActivity() : Main.preferences.getMaxNbPomPerActivity()); i++) {
                    comboBox.addItem(i);
                }
                comboBox.setSelectedItem(0);
                if (!comboBox.isShowing()) {
                    add(comboBox);
                }
                remove(label);
            } else {
                remove(comboBox);
                if (!label.isShowing()) {
                    add(label);
                }
            }
        }
        return this;
    }
}
