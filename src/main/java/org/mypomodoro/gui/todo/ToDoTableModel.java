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

import java.util.Iterator;
import org.mypomodoro.gui.TableModel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;

/**
 * Table model for ToDos
 *
 */
public class ToDoTableModel extends TableModel {

    public ToDoTableModel() {
        setDataVector(ToDoList.getTaskList());
    }

    protected void setDataVector(final AbstractActivities list) {
        int rowIndex = list.size();
        int colIndex = COLUMN_NAMES.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = list.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Activity a = iterator.next();
            tableData[i][PRIORITY_COLUMN_INDEX] = a.getPriority();
            tableData[i][UNPLANNED_COLUMN_INDEX] = a.isUnplanned();
            tableData[i][TITLE_COLUMN_INDEX] = a.getName();
            Integer poms = new Integer(a.getEstimatedPoms());// sorting done on estimated pom (model). This is very important for tableChanged to manage estimation changes
            //Integer poms = new Integer(a.getActualPoms()); // can't do that cause tableChanged would replace estimation with real
            tableData[i][ESTIMATED_COLUMN_INDEX] = poms;
            Float points = new Float(a.getStoryPoints());
            tableData[i][STORYPOINTS_COLUMN_INDEX] = points;
            Integer iteration = new Integer(a.getIteration());
            tableData[i][ITERATION_COLUMN_INDEX] = iteration;
            tableData[i][ACTIVITYID_COLUMN_INDEX] = a.getId();
        }
        setDataVector(tableData, COLUMN_NAMES);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == TITLE_COLUMN_INDEX || columnIndex == ESTIMATED_COLUMN_INDEX || columnIndex == STORYPOINTS_COLUMN_INDEX || columnIndex == ITERATION_COLUMN_INDEX;
    }
}
