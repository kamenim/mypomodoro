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
package org.mypomodoro.gui.reports;

import java.util.Iterator;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;

/**
 * Table model for reports
 *
 */
public class ReportsTableModel extends AbstractTableModel {

    public ReportsTableModel() {
        setDataVector(ReportList.getTaskList());
    }

    @Override
    protected void setDataVector(final AbstractActivities list) {
        int rowIndex = list.size();
        int colIndex = COLUMN_NAMES.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = list.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Activity activity = iterator.next();
            tableData[i] = getRow(activity);
        }
        setDataVector(tableData, COLUMN_NAMES);
    }

    @Override
    protected Object[] getRow(Activity activity) {
        int colIndex = COLUMN_NAMES.length;
        Object[] rowData = new Object[colIndex];
        rowData[UNPLANNED_COLUMN_INDEX] = activity.isUnplanned();
        rowData[DATE_COLUMN_INDEX] = activity.getDateCompleted();
        rowData[TITLE_COLUMN_INDEX] = activity.getName();
        rowData[TYPE_COLUMN_INDEX] = activity.getType();
        Integer poms = new Integer(activity.getActualPoms());
        rowData[ESTIMATED_COLUMN_INDEX] = poms;
        Integer diffIPoms = new Integer(activity.getActualPoms() - activity.getEstimatedPoms());
        rowData[DIFFI_COLUMN_INDEX] = diffIPoms;
        Integer diffIIPoms = new Integer(activity.getActualPoms()
                - activity.getEstimatedPoms()
                - activity.getOverestimatedPoms());
        rowData[DIFFII_COLUMN_INDEX] = diffIIPoms;
        Float points = new Float(activity.getStoryPoints());
        rowData[STORYPOINTS_COLUMN_INDEX] = points;
        Integer iteration = new Integer(activity.getIteration());
        rowData[ITERATION_COLUMN_INDEX] = iteration;
        rowData[ACTIVITYID_COLUMN_INDEX] = activity.getId();
        return rowData;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == TITLE_COLUMN_INDEX;
    }
}
