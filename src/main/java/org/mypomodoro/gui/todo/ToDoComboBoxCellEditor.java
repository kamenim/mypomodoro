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

import org.mypomodoro.gui.activities.*;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;

/**
 * Combo Box Cell Editor
 *
 */
class ToDoComboBoxCellEditor extends ComboBoxCellEditor {

    public <E> ToDoComboBoxCellEditor(E[] data, boolean editable) {
        super(data, editable);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        setBackground(table.getSelectionBackground());
        comboBox.setFont(getFont().deriveFont(Font.BOLD));
        label.setFont(getFont().deriveFont(Font.BOLD));
        int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ToDoPanel.ID_KEY);
        Activity activity = ToDoList.getList().getById(id);
        if (activity != null && activity.isFinished()) {
            comboBox.getEditor().getEditorComponent().setForeground(ColorUtil.GREEN); // editable combo box
            comboBox.setForeground(ColorUtil.GREEN);
            label.setForeground(ColorUtil.GREEN);
        } else if (id == Main.gui.getToDoPanel().getPomodoro().getCurrentToDo().getId() 
                && Main.gui.getToDoPanel().getPomodoro().inPomodoro()) {
            comboBox.getEditor().getEditorComponent().setForeground(ColorUtil.RED); // editable combo box
            comboBox.setForeground(ColorUtil.RED);
            label.setForeground(ColorUtil.RED);
        } else { // reset foreground (depends on the theme)
            comboBox.getEditor().getEditorComponent().setForeground(getForeground()); // editable combo box
            comboBox.setForeground(getForeground());
            label.setForeground(getForeground());
        }
        if (value != null) {
            comboBox.setSelectedItem(value);
        }
        return this;
    }    
}
