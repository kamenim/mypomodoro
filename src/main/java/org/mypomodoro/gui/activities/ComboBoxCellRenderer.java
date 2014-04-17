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
package org.mypomodoro.gui.activities;

import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
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
        comboBox.setRenderer(new DefaultListCellRenderer());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        setForeground(ColorUtil.BLACK);
        comboBox.setFont(isSelected ? getFont().deriveFont(Font.BOLD) : getFont().deriveFont(Font.PLAIN));
        comboBox.setForeground(ColorUtil.BLACK);
        label.setFont(isSelected ? getFont().deriveFont(Font.BOLD) : getFont().deriveFont(Font.PLAIN));
        label.setForeground(ColorUtil.BLACK);
        int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ActivitiesPanel.ID_KEY);
        Activity activity = ActivityList.getList().getById(id);
        if (activity != null && activity.isFinished()) {
            /*if (comboBox.isEditable()) { // this won't work to get the editable combo box (types) painted in green when activity is finished
             comboBox.getEditor().getEditorComponent().setForeground(ColorUtil.GREEN);                
             } else {*/
            comboBox.setForeground(ColorUtil.GREEN);
            //}
            label.setForeground(ColorUtil.GREEN);
        }
        if (value != null) {
            comboBox.setSelectedItem(value);
        }
        return this;
    }
}
