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
import org.mypomodoro.Main;
import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Effective Hours chart type
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
    public float getValue(Activity activity) {
        return TimeConverter.roundToHours(activity.getActualPoms() * Main.preferences.getPomodoroLength());
    }

    @Override
    public float getTotalForBurndown() {
        float total = 0;
        for (Activity activity : ChartList.getList()) {
            total += (activity.getEstimatedPoms() + activity.getOverestimatedPoms()) * Main.preferences.getPomodoroLength();
        }
        return TimeConverter.roundToHours(total);
    }

    @Override
    public float getTotalForBurnup() {
        float total = 0;
        for (Activity activity : ChartList.getList()) {
            if (activity.isCompleted()) {
                total += activity.getActualPoms() * Main.preferences.getPomodoroLength();
            }
        }
        return TimeConverter.roundToHours(total);
    }

    @Override
    public ArrayList<Float> getSumDateRangeForScope(ArrayList<Date> dates) {
        ArrayList<Float> sum = ActivitiesDAO.getInstance().getSumOfPomodorosOfActivitiesDateRange(dates);
        for (int i = 0; i < sum.size(); i++) {
            sum.set(i, TimeConverter.roundToHours(sum.get(i) * Main.preferences.getPomodoroLength()));
        }
        return sum;
    }

    @Override
    public ArrayList<Float> getSumIterationRangeForScope(int startIteration, int endIteration) {
        ArrayList<Float> sum = ActivitiesDAO.getInstance().getSumOfPomodorosOfActivitiesIterationRange(startIteration, endIteration);
        for (int i = 0; i < sum.size(); i++) {
            sum.set(i, TimeConverter.roundToHours(sum.get(i) * Main.preferences.getPomodoroLength()));
        }
        return sum;
    }

    @Override
    public String toString() {
        return label;
    }
}
