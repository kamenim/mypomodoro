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

    public void delete(Activity activity) {
        remove(activity);
        activity.databaseDelete();
    }

    public void move(Activity act) {
        ActivityList.getList().add(act); // set the priority and update the database
        remove(act);
    }

    public void moveAll() {
        for (Activity activity : activities) {
            ActivityList.getList().add(activity);
        }
        ActivitiesDAO.getInstance().moveAllTODOs();
        removeAll();
    }

    public void complete(Activity a) {
        ReportList.getList().add(a);
        remove(a);
    }

    public void completeAll() {
        for (Activity activity : activities) {
            ReportList.getList().add(activity);
        }
        ActivitiesDAO.getInstance().completeAllTODOs();
        removeAll();
    }

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
         System.err.println(activity.getPriority());
         }*/
    }
}
