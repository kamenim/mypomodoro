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
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Effective Hours chart type
 *
 * Chart based on real pomodoros (similar to Pomodoro chart)
 *
 */
public class EffectiveHourChart implements IChartType {

    private final String label = Labels.getString("BurndownChartPanel.Effective Hours");

    @Override
    public String getYLegend() {
        return label;
    }

    @Override
    public String getXLegend() {
        return label;
    }

    @Override
    public float getValue(Activity activity, Date date) {
        boolean isComplete = (activity.isTask() && DateUtil.isEquals(activity.getDateCompleted(), date) && activity.isCompleted())
                || (activity.isSubTask() && DateUtil.isEquals(activity.getDateDoneDone(), date) && activity.isDoneDone());
        return TimeConverter.roundToHours(TimeConverter.convertPomodorosToEffectiveMinutes(isComplete ? activity.getActualPoms() : 0)); // real poms of the task = real poms of its subtasks
    }

    @Override
    public float getTotalForBurndown() {
        int total = 0;
        for (Activity activity : ChartList.getList().getTasks()) {
            total += activity.getEstimatedPoms() + activity.getOverestimatedPoms();
        }
        return TimeConverter.roundToHours(TimeConverter.convertPomodorosToEffectiveMinutes(total));
    }

    @Override
    public float getTotalForBurnup() {
        return getTotalForBurndown();
    }

    @Override
    public ArrayList<Float> getSumDateRangeForScope(ArrayList<Date> dates, boolean subtasks) {
        ArrayList<Float> sum = ActivitiesDAO.getInstance().getSumOfPomodorosOfActivitiesDateRange(dates, subtasks);
        for (int i = 0; i < sum.size(); i++) {
            sum.set(i, TimeConverter.roundToHours(TimeConverter.convertPomodorosToEffectiveMinutes(Math.round(sum.get(i)))));
        }
        return sum;
    }

    @Override
    public ArrayList<Float> getSumIterationRangeForScope(int startIteration, int endIteration) {
        ArrayList<Float> sum = ActivitiesDAO.getInstance().getSumOfPomodorosOfActivitiesIterationRange(startIteration, endIteration);
        for (int i = 0; i < sum.size(); i++) {
            sum.set(i, TimeConverter.roundToHours(TimeConverter.convertPomodorosToEffectiveMinutes(Math.round(sum.get(i)))));
        }
        return sum;
    }

    @Override
    public String toString() {
        return label;
    }
}
