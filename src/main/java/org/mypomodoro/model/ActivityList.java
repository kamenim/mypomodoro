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

import org.mypomodoro.db.ActivitiesDAO;

/**
 * Activity list
 *
 */
public final class ActivityList extends AbstractActivities {

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

    public static ActivityList getList() {
        return list;
    }

    public static int getListSize() {
        return getList().size();
    }

    @Override
    public void add(Activity act) {
        act.setPriority(-1);
        act.setIsCompleted(false);
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
        ActivitiesDAO.getInstance().deleteAllActivities();
        removeAll();
    }

    // move from Activity list to ToDo list
    public void move(Activity activity) {
        ToDoList.getList().add(activity); // this sets the priority and update the database
        remove(activity);
    }

    public void moveAll() {
        // no use
    }
}
