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

package org.mypomodoro.gui.burndownchart.types;

import java.util.ArrayList;
import java.util.Date;
import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.Labels;

/**
 * Story points chart type
 * 
 */
public class StoryPointChart implements IChartType {
    
    private final String label = Labels.getString("BurndownChartPanel.Story Points");
    
    @Override
    public String getYLegend() {
        return label;
    }
    
    @Override
    public String getXLegend() {
        return label;
    }
    
    @Override
    public float getTotalForBurndown() {
        int total = 0;
        for (Activity activity : ChartList.getList()) {
            total += activity.getStoryPoints();
        }
        return new Float(total);
    }
    
    @Override
    public float getTotalForBurnup() {
        int total = 0;
        for (Activity activity : ChartList.getList()) {
            if (activity.isCompleted()) {
                total += activity.getStoryPoints();
            }
        }
        return new Float(total);
    }
    
    @Override
    public ArrayList<Float> getSumDateRange(ArrayList<Date> dates) {
        return ActivitiesDAO.getInstance().getSumOfStoryPointsOfActivitiesDateRange(dates);
    }
    
    @Override
    public ArrayList<Float> getSumIterationRange(int startIteration, int endIteration) {
        return ActivitiesDAO.getInstance().getSumOfStoryPointsOfActivitiesIterationRange(startIteration, endIteration);
    }
    
    @Override
    public  String toString() {
        return label;
    }
}
