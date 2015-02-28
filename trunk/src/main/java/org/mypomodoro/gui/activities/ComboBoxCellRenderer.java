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
package org.mypomodoro.gui.activities;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;

/**
 * Combo Box Cell Renderer
 *
 */
class ComboBoxCellRenderer extends ActivitiesComboBoxPanel implements TableCellRenderer {

    public <E> ComboBoxCellRenderer(E[] data, boolean editable) {
        super(data, editable);

        // Custom display hovered item value
        comboBox.setRenderer(new AbstractComboBoxRenderer());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        comboBox.setFont(isSelected ? getFont().deriveFont(Font.BOLD) : getFont().deriveFont(Font.PLAIN));
        label.setFont(isSelected ? getFont().deriveFont(Font.BOLD) : getFont().deriveFont(Font.PLAIN));
        int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ActivitiesPanel.ID_KEY);
        Activity activity = ActivityList.getList().getById(id);
        if (activity != null && activity.isFinished()) {
            comboBox.getEditor().getEditorComponent().setForeground(ColorUtil.GREEN); // editable combo box
            comboBox.setForeground(ColorUtil.GREEN);
            label.setForeground(ColorUtil.GREEN);
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
