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
import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;

/**
 *
 *
 */
class ActivitiesEstimatedComboBoxCellEditor extends ActivitiesComboBoxCellEditor {

    public <E> ActivitiesEstimatedComboBoxCellEditor(E[] data, boolean editable) {
        super(data, editable);

        // Custom display hovered item value
        comboBox.setRenderer(new ComboBoxEstimatedLengthRenderer());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), table.getModel().getColumnCount() - 1);
        Activity activity = ActivityList.getList().getById(id);
        if (activity != null) {
            int realpoms = activity.getActualPoms();
            int estimatedpoms = activity.getEstimatedPoms();
            int minimum = 0;
            int maximum = estimatedpoms;
            if (realpoms == 0
                    && (activity.isSubTask() || !ActivityList.hasSubTasks(activity.getId()))) { // estimated combo box only
                if (activity.isSubTask()) { // subtask - sum of estimated of subtasks can't be more than estimate of parent task 
                    ActivityList subList = ActivityList.getSubTaskList(activity.getParentId());
                    int subEstimated = 0;
                    for (Activity subActivity : subList) {
                        subEstimated += subActivity.getEstimatedPoms();
                    }
                    Activity parentActivity = ActivityList.getList().getById(activity.getParentId());
                    maximum += parentActivity.getEstimatedPoms() - subEstimated;
                } else {                    
                    maximum += Main.preferences.getMaxNbPomPerActivity();
                }
                comboBox.setVisible(true);
                comboBox.removeAllItems();
                for (int i = minimum; i <= maximum; i++) {
                    comboBox.addItem(i);
                }
                comboBox.setSelectedItem(activity.getEstimatedPoms());                                
                
            } else { // no change to the label set by the cell renderer
                comboBox.setVisible(false);
            }
        }
        return this;
    }
}

/*
int realpoms = activity.getActualPoms();
            int estimatedpoms = activity.getEstimatedPoms();
            if (realpoms == 0
                    && (activity.isSubTask() || !ActivityList.hasSubTasks(activity.getId()))) { // estimated combo box only
                int minimum = 0;
                if (activity.isSubTask()) { // subtask - sum of estimated of subtasks can't be more than estimate of parent task 
                    ActivityList subList = ActivityList.getSubTaskList(activity.getParentId());
                    int subEstimated = 0;
                    for (Activity subActivity : subList) {
                        subEstimated += subActivity.getEstimatedPoms();
                    }
                    Activity parentActivity = ActivityList.getList().getById(activity.getParentId());
                    minimum = estimatedpoms - parentActivity.getEstimatedPoms() + subEstimated;
                    minimum = minimum > 0 ? minimum : 0; 
                }
                int maximum = estimatedpoms + Main.preferences.getMaxNbPomPerActivity();                
                comboBox.setVisible(true);
                comboBox.removeAllItems();
                for (int i = minimum; i <= maximum; i++) {
                    comboBox.addItem(i);
                }
                comboBox.setSelectedItem(activity.getEstimatedPoms());                                
*/
