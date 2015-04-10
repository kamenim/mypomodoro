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
import javax.swing.JTable;
import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;

/**
 * Combo Box Cell Renderer
 *
 */
class ToDoComboBoxCellRenderer extends ComboBoxCellRenderer {

    public <E> ToDoComboBoxCellRenderer(E[] data, boolean editable) {
        super(data, editable);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), table.getModel().getColumnCount() - 1);
        Activity activity = ToDoList.getList().getById(id);
        if (activity != null && activity.isFinished()) {
            labelBefore.setForeground(ColorUtil.GREEN);
            comboBox.getEditor().getEditorComponent().setForeground(ColorUtil.GREEN); // editable combo box
            comboBox.setForeground(ColorUtil.GREEN);
            labelAfter.setForeground(ColorUtil.GREEN);
        } else if (Main.gui.getToDoPanel().getPomodoro().getCurrentToDo() != null
                && id == Main.gui.getToDoPanel().getPomodoro().getCurrentToDo().getId()
                && Main.gui.getToDoPanel().getPomodoro().inPomodoro()) {
            labelBefore.setForeground(ColorUtil.RED);
            comboBox.getEditor().getEditorComponent().setForeground(ColorUtil.RED); // editable combo box
            comboBox.setForeground(ColorUtil.RED);
            labelAfter.setForeground(ColorUtil.RED);
        } else { // reset foreground (depends on the theme)
            labelBefore.setForeground(ColorUtil.BLACK);
            comboBox.getEditor().getEditorComponent().setForeground(getForeground()); // editable combo box
            comboBox.setForeground(getForeground());
            labelAfter.setForeground(ColorUtil.BLACK); // we force color to be black (especially for JTatto Noire theme)
        }
        return this;
    }
}
