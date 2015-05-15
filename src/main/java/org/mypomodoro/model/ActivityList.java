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
 * Activity list
 *
 */
public class ActivityList extends AbstractActivities {

    private static final ActivityList list = new ActivityList();

    private ActivityList() {
        refresh();
    }

    @Override
    public void refresh() {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getActivities()) {
            super.add(act);
        }
    }

    // List of all tasks
    public static ActivityList getList() {
        return list;
    }

    // List of main tasks
    public static ActivityList getTaskList() {
        ActivityList tableList = new ActivityList();
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
    public static ActivityList getSubTaskList(int parentId) {
        ActivityList subTableList = new ActivityList();
        for (Activity a : list) {
            if (a.getParentId() != parentId) {
                subTableList.removeById(a.getId());                
            }
        }
        return subTableList;
    }

    public static boolean hasSubTasks(int activityId) {
        boolean hasSubTasks = false;
        for (Activity a : list) {
            if (a.getParentId() == activityId) {
                hasSubTasks = true;
                break;
            }
        }
        return hasSubTasks;
    }

    public static int getListSize() {
        return getList().size();
    }

    @Override
    public void add(Activity act) {
        add(act, act.getDate()); // date creation/schedule
    }

    public void add(Activity act, Date date) {
        add(act, date, act.getDateCompleted()); // date creation/schedule, date complete
    }

    // Create or update
    public void add(Activity act, Date date, Date dateReopen) {
        act.setPriority(-1);
        act.setIsCompleted(false);
        act.setDate(date);
        act.setDateCompleted(dateReopen);
        if (act.getId() == -1) { // add to the database (new activity)
            act.setId(act.databaseInsert());
        } else { // update in database (modified activity or moved from todo list / reopened from report list)
            act.databaseUpdate();
        }
        super.add(act); // add to the list
    }

    public Activity duplicate(Activity activity) throws CloneNotSupportedException {
        return duplicate(activity, activity.isSubTask() ? activity.getParentId() : -1);
    }

    public Activity duplicate(Activity activity, int parentId) throws CloneNotSupportedException {
        Activity clonedActivity = activity.clone(); // a clone is necessary to remove the reference/pointer to the original task        
        clonedActivity.setActualPoms(0);
        clonedActivity.setOverestimatedPoms(0);
        clonedActivity.setName("(D) " + clonedActivity.getName());
        if (activity.isSubTask()) {
            clonedActivity.setParentId(parentId);
            getList().add(clonedActivity, new Date(), new Date(0));            
        } else {
            getList().add(clonedActivity, new Date(), new Date(0)); // add task here to get the new Id to be the parentId of the subtasks
            ActivityList subList = getSubTaskList(activity.getId());
            for (Activity subTask : subList) {
                duplicate(subTask, clonedActivity.getId());
            }
        }
        return clonedActivity;
    }

    @Override
    public void delete(Activity activity) {
        if (!activity.isSubTask()) {
            ActivityList subList = getSubTaskList(activity.getId());
            for (Activity subTask : subList) {
                remove(subTask);
                subTask.databaseDelete();
            }
        }
        remove(activity);
        activity.databaseDelete();
    }

    public void deleteAll() {
        ActivitiesDAO.getInstance().deleteAllActivities();
        removeAll();
    }

    // Move a task and its subtasks to ToDoList
    // Move a subtask only will make it a task
    public void moveToTODOList(Activity activity) {
        if (activity.isSubTask()) {
            activity.setParentId(-1); // sub-task becomes task
        } else {
            ActivityList subList = getSubTaskList(activity.getId());
            for (Activity subTask : subList) {
                ToDoList.getList().add(subTask);
                remove(subTask);
            }
        }
        ToDoList.getList().add(activity); // this sets the priority and update the database
        remove(activity);
    }
}
