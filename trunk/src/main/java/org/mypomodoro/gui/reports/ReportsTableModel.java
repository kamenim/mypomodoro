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
import org.mypomodoro.gui.TableModel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;

/**
 * Table model for reports
 *
 */
public class ReportsTableModel extends TableModel {

    public ReportsTableModel() {
        setDataVector(ReportList.getTaskList());
    }

    protected void setDataVector(final AbstractActivities list) {
        int rowIndex = list.size();
        int colIndex = COLUMN_NAMES.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = list.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Activity a = iterator.next();
            tableData[i][UNPLANNED_COLUMN_INDEX] = a.isUnplanned();
            tableData[i][DATE_COLUMN_INDEX] = a.getDateCompleted(); // date completed formated via custom renderer (DateRenderer)
            //tableData[i][2] = DateUtil.getFormatedTime(a.getDate());
            tableData[i][TITLE_COLUMN_INDEX] = a.getName();
            tableData[i][TYPE_COLUMN_INDEX] = a.getType();
            Integer poms = new Integer(a.getActualPoms()); // sorting done on real pom
            tableData[i][ESTIMATED_COLUMN_INDEX] = poms;
            Integer diffIPoms = new Integer(a.getActualPoms() - a.getEstimatedPoms());
            tableData[i][DIFFI_COLUMN_INDEX] = diffIPoms; // Diff I
            Integer diffIIPoms = new Integer(a.getActualPoms()
                    - a.getEstimatedPoms()
                    - a.getOverestimatedPoms());
            tableData[i][DIFFII_COLUMN_INDEX] = diffIIPoms; // Diff II
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
        return columnIndex == TITLE_COLUMN_INDEX;
    }
}
