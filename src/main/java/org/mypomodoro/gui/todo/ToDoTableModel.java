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

import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;

/**
 * Table model for ToDos
 *
 */
public class ToDoTableModel extends AbstractTableModel {

    public ToDoTableModel() {
        setDataVector(ToDoList.getList().getTasks());
    }

    @Override
    protected Object[] getRow(Activity activity) {
        int colIndex = COLUMN_NAMES.length;
        Object[] rowData = new Object[colIndex];
        rowData[PRIORITY_COLUMN_INDEX] = activity.getPriority();
        rowData[UNPLANNED_COLUMN_INDEX] = activity.isUnplanned();
        rowData[TITLE_COLUMN_INDEX] = activity.getName();
        // sorting done on estimated pom (model). This is very important for tableChanged to manage estimation changes
        Integer poms = new Integer(activity.getEstimatedPoms()); // Estimated poms !        
        rowData[ESTIMATED_COLUMN_INDEX] = poms;
        Float points = new Float(activity.getStoryPoints());
        rowData[STORYPOINTS_COLUMN_INDEX] = points;
        Integer iteration = new Integer(activity.getIteration());
        rowData[ITERATION_COLUMN_INDEX] = iteration;
        rowData[ACTIVITYID_COLUMN_INDEX] = activity.getId();
        return rowData;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == TITLE_COLUMN_INDEX || columnIndex == ESTIMATED_COLUMN_INDEX || columnIndex == STORYPOINTS_COLUMN_INDEX || columnIndex == ITERATION_COLUMN_INDEX;
    }
}
