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
 * ToDo list
 *
 */
public final class ToDoList extends AbstractActivities {

    private static final ToDoList list = new ToDoList();

    private ToDoList() {
        refresh();
    }

    @Override
    final public void refresh() {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getTODOs()) {
            super.add(act);
        }
    }

    public static ToDoList getList() {
        return list;
    }

    // List of main tasks
    public static ToDoList getTaskList() {
        ToDoList tableList = new ToDoList();
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
    public static ToDoList getSubTaskList(int parentId) {
        ToDoList subTableList = new ToDoList();
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
        add(act, act.getDate());
    }

    public void add(Activity act, Date date) {
        add(act, date, new Date(0)); // reset date reopen to avoid any confusion with date completed
    }

    public void add(Activity act, Date date, Date dateCompleted) {
        act.setPriority(size() + 1);
        act.setIsCompleted(false);
        act.setDate(date);
        act.setDateCompleted(dateCompleted);
        if (act.getId() == -1) { // add to the database (new todo)
            act.setId(act.databaseInsert());
        } else { // update in database (modified todo or moved from activity list)
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
        if (!activity.isSubTask()) {
            clonedActivity.setName("(D) " + clonedActivity.getName());
            getList().add(clonedActivity, new Date(), new Date(0)); // add task here to get the new Id to be the parentId of the subtasks
            ToDoList subList = getSubTaskList(activity.getId());
            for (Activity subTask : subList) {
                duplicate(subTask, clonedActivity.getId());
            }
        } else {
            clonedActivity.setParentId(parentId);
            getList().add(clonedActivity, new Date(), new Date(0));
        }
        return clonedActivity;
    }

    // Move a task and its subtasks to ActivityList
    // Move a subtask only will make it a task
    public void move(Activity activity) {
        if (activity.isSubTask()) {
            activity.setParentId(-1); // make sure sub-task become task
        } else {
            ToDoList subList = getSubTaskList(activity.getId());
            for (Activity subTask : subList) {
                ActivityList.getList().add(subTask);
                remove(subTask);
            }
        }
        ActivityList.getList().add(activity); // set the priority and update the database
        remove(activity);
    }

    /*public void moveAll() {
        for (Activity activity : activities) {
            ActivityList.getList().add(activity);
        }
        ActivitiesDAO.getInstance().moveAllTODOs();
        removeAll();
    }*/

    // Complete a task and its subtasks to ReportList
    // Complete a subtask only will make it a task
    public void complete(Activity activity) {
        if (activity.isSubTask()) {
            activity.setParentId(-1); // make sure sub-task become task
        } else {
            ToDoList subList = getSubTaskList(activity.getId());
            for (Activity subTask : subList) {
                ActivityList.getList().add(subTask);
                remove(subTask);
            }            
        }        
        ReportList.getList().add(activity);
        remove(activity);
    }

    /*public void completeAll() {
        for (Activity activity : activities) {
            ReportList.getList().add(activity);
        }
        ActivitiesDAO.getInstance().completeAllTODOs();
        removeAll();
    }*/

    // set new priorities
    public void reorderByPriority() {
        sortByPriority(); // sort what is left of the ToDos (after delete, complete...)        
        // Very slow because of the per-row database update
        // Very QUICK with drag and drop
        int increment = 1;
        for (Activity activity : activities) {
            if (activity.getPriority() != increment) { // optimization
                activity.setPriority(increment);
                update(activity);
                activity.databaseUpdate();
            }
            increment++;
        }
        /*
         // As slow as previous algo
         // Very slow with drag and drop
         ArrayList<Activity> alist;
         alist = clone();        
         removeAll();
         for (Activity activity : alist) {            
         add(activity);
         }*/
    }
}
