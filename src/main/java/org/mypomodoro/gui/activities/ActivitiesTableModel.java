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

import java.util.Date;
import java.util.Iterator;
import javax.swing.table.DefaultTableModel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * Table model for the activities table
 *
 */
public class ActivitiesTableModel extends DefaultTableModel {

    public static final String[] COLUMN_NAMES = {"U",
        Labels.getString("Common.Date"),
        Labels.getString("Common.Title"),
        Labels.getString("Common.Type"),
        Labels.getString("Common.Estimated"),
        Labels.getString("Agile.Common.Story Points"),
        Labels.getString("Agile.Common.Iteration"),
        "ID"};
    
    public static final int UNPLANNED_COLUMN_INDEX = 0;
    public static final int DATE_COLUMN_INDEX = 1;
    public static final int TITLE_COLUMN_INDEX = 2;
    public static final int TYPE_COLUMN_INDEX = 3;
    public static final int ESTIMATED_COLUMN_INDEX = 4;
    public static final int STORYPOINTS_COLUMN_INDEX = 5;
    public static final int ITERATION_COLUMN_INDEX = 6;
    public static final int ACTIVITYID_COLUMN_INDEX = 7;
    
    public ActivitiesTableModel() {
        setDataVector(ActivityList.getTableList());
    }

    protected void setDataVector(final ActivityList list) {
        int rowIndex = list.size();
        int colIndex = COLUMN_NAMES.length;        
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = list.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Activity a = iterator.next();
            tableData[i][0] = a.isUnplanned();
            tableData[i][1] = a.getDate();
            tableData[i][2] = a.getName();
            tableData[i][3] = a.getType();
            Integer poms = new Integer(a.getEstimatedPoms());
            tableData[i][4] = poms;
            Float points = new Float(a.getStoryPoints());
            tableData[i][5] = points;
            Integer iteration = new Integer(a.getIteration());
            tableData[i][6] = iteration;
            tableData[i][7] = a.getId();
        }
        setDataVector(tableData, COLUMN_NAMES);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == TITLE_COLUMN_INDEX || columnIndex == TYPE_COLUMN_INDEX || columnIndex == ESTIMATED_COLUMN_INDEX || columnIndex == STORYPOINTS_COLUMN_INDEX || columnIndex == ITERATION_COLUMN_INDEX;
    }

    // this is mandatory to get columns with integers properly sorted
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case UNPLANNED_COLUMN_INDEX:
                return Boolean.class;
            case DATE_COLUMN_INDEX:
                return Date.class;
            case ESTIMATED_COLUMN_INDEX:
                return Integer.class;
            case STORYPOINTS_COLUMN_INDEX:
                return Float.class;
            case ITERATION_COLUMN_INDEX:
                return Integer.class;
            default:
                return String.class;
        }
    }
}
