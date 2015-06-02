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
import javax.swing.JTable;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import static org.mypomodoro.util.TimeConverter.getLength;

/**
 *
 *
 */
class ActivitiesEstimatedComboBoxCellRenderer extends ActivitiesComboBoxCellRenderer {

    public <E> ActivitiesEstimatedComboBoxCellRenderer(E[] data, boolean editable) {
        super(data, editable);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), table.getModel().getColumnCount() - 1);
        Activity activity = ActivityList.getList().getById(id);
        if (activity != null) {
            int realpoms = activity.getActualPoms();
            int estimatedpoms = activity.getEstimatedPoms();
            int overestimatedpoms = activity.getOverestimatedPoms();
            // no subtask --> estimated may be changed
            //if (activity.isSubTask() || !ActivityList.hasSubTasks(activity.getId())) {
                labelBefore.setText(realpoms > 0 ? realpoms + " /" : "");
                comboBox.setVisible(true);
                comboBox.removeAllItems();
                comboBox.addItem(estimatedpoms);
                labelAfter.setText(overestimatedpoms > 0 ? "+ " + overestimatedpoms : "");
            /*} else { // has subtasks --> estimated cannot be changed
                labelBefore.setText(realpoms > 0 ? realpoms + " / " : "");
                comboBox.setVisible(false);
                labelAfter.setText(estimatedpoms + (overestimatedpoms > 0 ? " + " + overestimatedpoms : ""));
            }*/
            setToolTipText((realpoms > 0 ? getLength(realpoms) + " / " : "") + getLength(estimatedpoms) + (overestimatedpoms > 0 ? " + " + getLength(overestimatedpoms) : ""));
        }
        return this;
    }
}
