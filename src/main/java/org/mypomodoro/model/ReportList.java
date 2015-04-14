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

import java.util.Date;
import org.mypomodoro.db.ActivitiesDAO;

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

    // List of main tasks
    public static ReportList getTaskList() {
        ReportList tableList = new ReportList();
        for (Activity a : list) {
            if (a.isSubTask()) {
                tableList.removeById(a.getId());
            }
        }
        return tableList;
    }

    // List of sub tasks
    // The bigger the list the heavier this will be
    // May we use use Guava https://github.com/google/guava
    // OR have a specific list for subtasks ?...
    public static ReportList getSubTaskList(int parentId) {
        ReportList subTableList = new ReportList();
        for (Activity a : list) {
            if (a.getParentId() != parentId) {
                subTableList.removeById(a.getId());
            }
        }
        return subTableList;
    }

    public static int getListSize() {
        return getList().size();
    }

    @Override
    public void add(Activity act) {
        add(act, new Date());
    }

    public void add(Activity act, Date dateCompleted) {
        add(act, act.getDate(), dateCompleted);
    }

    public void add(Activity act, Date date, Date dateCompleted) {
        act.setPriority(-1);
        act.setIsCompleted(true);
        act.setDate(date);
        act.setDateCompleted(dateCompleted);
        if (act.getId() == -1) { // add to the database (new report)
            act.setId(act.databaseInsert());
        } else { // update in database (modified report or moved from todo list)
            act.databaseUpdate();
        }
        super.add(act); // add to the list
    }

    public void delete(Activity activity) {
        if (!activity.isSubTask()) {
            ReportList subList = getSubTaskList(activity.getId());
            for (Activity subTask : subList) {
                remove(subTask);
                subTask.databaseDelete();
            }
        }
        remove(activity);
        activity.databaseDelete();
    }

    public void deleteAll() {
        ActivitiesDAO.getInstance().deleteAllReports();
        removeAll();
    }

    // Reopen a task and its subtasks to ActivityList
    // Reopen a subtask only will make it a task
    public void reopen(Activity activity) {
        activity.setName("(R) " + activity.getName());
        activity.setDateCompleted(new Date()); // 'complete date' becomes 'reopen date' (see ActivityInformationPanel)
        activity.setIteration(-1); // reset iteration
        if (activity.isSubTask()) {
            activity.setParentId(-1); // make sure sub-task become task
        } else {
            ReportList subList = getSubTaskList(activity.getId());
            for (Activity subTask : subList) {
                ActivityList.getList().add(subTask);
                remove(subTask);
            }
        }
        ActivityList.getList().add(activity);
        remove(activity);
    }

    /*public void reopenAll() {
        for (Activity activity : activities) {
            activity.setDateCompleted(new Date()); // 'complete date' becomes 'reopen date' (see ActivityInformationPanel)            
            ActivityList.getList().add(activity);
        }
        ActivitiesDAO.getInstance().reopenAllReports();
        removeAll();
    }*/
}
