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
import java.util.Iterator;
import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.util.DateUtil;

/**
 * Report list
 *
 */
public final class ReportList extends AbstractActivities {

    private static final ReportList list = new ReportList();

    private ReportList() {
        refresh();
    }

    @Override
    public void refresh() {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getReports()) {
            super.add(act);
        }
    }

    public static ReportList getList() {
        return list;
    }

    public static int getListSize() {
        return getList().size();
    }

    @Override
    public void add(Activity act) {
        act.setPriority(-1);
        act.setIsCompleted(true);
        act.setDateCompleted(new Date());
        if (act.getId() == -1) {
            act.setId(act.databaseInsert());
        } else {
            act.databaseUpdate();
        }
        super.add(act);
    }

    public void delete(Activity activity) {
        remove(activity);
        activity.databaseDelete();
    }

    public void deleteAll() {
        ActivitiesDAO.getInstance().deleteAllReports();
        removeAll();
    }

    // move from Report list to Activity list
    public void reopen(Activity activity) {
        activity.setDateCompleted(new Date()); // 'complete date' becomes 'reopen date' (see ActivityInformationPanel)  
        ActivityList.getList().add(activity);
        remove(activity);
    }

    public void reopenAll() {
        for (Activity activity : activities) {
            activity.setDateCompleted(new Date()); // 'complete date' becomes 'reopen date' (see ActivityInformationPanel)            
            ActivityList.getList().add(activity);
        }
        ActivitiesDAO.getInstance().reopenAllReports();
        removeAll();
    }
    
    public int getAccuracy() {
        int estover = 0;
        int real = 0;
        for (Iterator<Activity> it = iterator(); it.hasNext();) {
            Activity a = it.next();
            estover += a.getEstimatedPoms() + a.getOverestimatedPoms();
            real += a.getActualPoms();
        }
        int accuracy = Math.round(((float) real / (float) estover) * 100);
        return accuracy;
    }
}
