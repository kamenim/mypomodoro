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
        act.setPriority(size() + 1);
        act.setIsCompleted(false);
        act.setDateCompleted(new Date(0));
        act.setDate(new Date());
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

    public void move(Activity act) {
        ActivityList.getList().add(act); // this sets the priority and update the database
        remove(act);
    }

    public void moveAll() {
        ActivityList.getList().addAll(activities);
        ActivitiesDAO.getInstance().moveAllTODOs();
        removeAll();
    }

    public void complete(Activity a) {
        ReportList.getList().add(a);
        remove(a);
    }

    public void completeAll() {
        ReportList.getList().addAll(activities);
        ActivitiesDAO.getInstance().completeAllTODOs();
        removeAll();
    }

    // set new priorities
    public void reorderByPriority() {
        sortByPriority();
        int increment = 1;
        for (Activity activity : activities) {
            activity.setPriority(increment);
            activity.databaseUpdate();
            increment++;
        }
    }

    /**
     * Promote the Activity at the desired index, by storing the value to be
     * replaced into a buffer and then switching the values. The root node
     * cannote be changed.
     *
     */
    /*public void promote(Activity a) {
     int index = activities.indexOf(a);
     if (index > 0) {
     Activity lower = a;
     Activity higher = activities.get(index - 1);
     activities.set(index - 1, lower);
     activities.set(index, higher);
     lower.setPriority(index - 1);
     lower.databaseUpdate();
     higher.setPriority(index);
     higher.databaseUpdate();
     }
     }*/
    /**
     * Demote the Activity at the desired index, by storing the value to be
     * replaced into a buffer and then switching the values. The root node
     * cannot be changed.
     *
     * @param index
     */
    /*public void demote(int index) {
     if (index < (this.size() - 1)) {
     Activity higher = activities.get(index);
     Activity lower = activities.get(index + 1);
     activities.set(index, lower);
     activities.set(index + 1, higher);
     lower.setPriority(index);
     lower.databaseUpdate();
     higher.setPriority(index + 1);
     higher.databaseUpdate();
     }
     }*/
}
