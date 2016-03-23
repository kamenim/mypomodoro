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
package org.mypomodoro.gui.burndownchart.types;

import java.util.ArrayList;
import java.util.Date;
import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.Labels;

/**
 * Tasks chart type
 *
 */
public class SubtaskChart implements IChartType {

    private final String label = Labels.getString("Common.Subtasks");

    @Override
    public String getYLegend() {
        return label;
    }

    @Override
    public String getXLegend() {
        return label;
    }

    // A subtask DOESN'T NEED to be the subtask of completed/done task (= release backlog) as long as it is done
    @Override
    public float getValue(Activity activity) {
        return activity.isSubTask() && activity.isDoneDone() ? 1 : 0;
    }

    @Override
    public float getTotalForBurndown() {
        return ChartList.getList().getSubTasks().size();
    }

    @Override
    public float getTotalForBurnup() {
        /*int total = 0;
        for (Activity activity : ChartList.getList().getSubTasks()) {
            if (activity.isDoneDone()) {
                total++;
            }
        }
        return new Float(total);*/
        return getTotalForBurndown();
    }

    @Override
    public ArrayList<Float> getSumDateRangeForScope(ArrayList<Date> dates) {
        return ActivitiesDAO.getInstance().getSumOfSubtasksOfActivitiesDateRange(dates);
    }

    @Override
    public ArrayList<Float> getSumIterationRangeForScope(int startIteration, int endIteration) {
        return ActivitiesDAO.getInstance().getSumOfSubtasksOfActivitiesIterationRange(startIteration, endIteration);
    }

    @Override
    public String toString() {
        return label;
    }
}
