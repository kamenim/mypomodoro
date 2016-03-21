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
package org.mypomodoro.model;

import java.util.ArrayList;
import java.util.Date;
import org.mypomodoro.db.ActivitiesDAO;

/**
 * Chart list
 *
 */
public class ChartList extends AbstractActivities {

    private static final ChartList list = new ChartList();

    @Override
    public void refresh() {
        removeAll();
    }

    @Override
    public void delete(Activity activity) {
        // not used
    }

    public void refreshDateRange(Date startDate, Date endDate, ArrayList<Date> datesToBeIncluded, boolean excludeToDos) {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getActivitiesForChartDateRange(startDate, endDate, datesToBeIncluded, excludeToDos)) {
            super.add(act);
        }
    }

    public void refreshDateRangeAndIteration(Date startDate, Date endDate, ArrayList<Date> datesToBeIncluded, int iteration) {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getActivitiesForChartDateRange(startDate, endDate, datesToBeIncluded, false, iteration)) {
            super.add(act);
        }
    }

    public void refreshIterationRange(int startIteration, int endIteration) {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getActivitiesForChartIterationRange(startIteration, endIteration)) {
            super.add(act);
        }
    }

    public static ChartList getList() {
        return list;
    }

    // List of main tasks
    public static ChartList getTaskList() {
        ChartList tableList = new ChartList();
        for (Activity a : list) {
            if (a.isTask()) {
                tableList.add(a);
            }
        }
        return tableList;
    }

    // Remove tasks AND subtasks
    @Override
    public void remove(Activity activity) {
        ArrayList<Activity> subList = getSubTasks(activity.getId());
        for (Activity subTask : subList) {
            super.remove(subTask);
        }
        super.remove(activity);
    }

    public static int getListSize() {
        return getList().size();
    }
}
