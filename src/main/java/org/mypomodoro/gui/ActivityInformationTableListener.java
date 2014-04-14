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
package org.mypomodoro.gui;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;

public class ActivityInformationTableListener implements ListSelectionListener {

    private final JTable table;
    private final ActivityInformation information;
    private final int idKey;
    private final AbstractActivities activities;

    public ActivityInformationTableListener(AbstractActivities activities,
            JTable table, ActivityInformation information, int idKey) {
        this.activities = activities;
        this.table = table;
        this.information = information;
        this.idKey = idKey;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (table.getSelectedRowCount() > 0) {
            if (!e.getValueIsAdjusting()) { // ignoring the deselection event
                if (table.getSelectedRowCount() > 1 && information.isMultipleSelectionAllowed()) { // multiple selection
                    String info = "";
                    int[] rows = table.getSelectedRows();
                    for (int row : rows) {
                        Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), idKey);
                        Activity selectedActivity = activities.getById(id);
                        info += selectedActivity.getName() + "\n";
                    }
                    information.showInfo(info);
                } else if (table.getSelectedRowCount() == 1) {
                    int row = table.getSelectedRow();
                    Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), idKey);
                    Activity activity = activities.getById(id);
                    information.selectInfo(activity);
                    information.showInfo();
                }
            }
        } else {
            information.clearInfo();
        }
    }
}
