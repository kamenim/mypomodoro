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
package org.mypomodoro.model;

import java.util.Date;
import org.mypomodoro.db.ActivitiesDAO;

/**
 * Chart list
 *
 */
public final class ChartList extends AbstractActivities {

    private static final ChartList list = new ChartList();

    private ChartList() {
        // no use
    }

    private ChartList(Date date) {
        refresh(date);
    }

    public void refresh() {
        //  no use
    }

    public void refresh(Date date) {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getToDosAndReportsForChart(date)) {
            super.add(act);
        }
    }

    public static ChartList getList() {
        return list;
    }

    public static int getListSize() {
        return getList().size();
    }
}
