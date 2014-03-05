package org.mypomodoro.model;

import java.util.Date;
import org.mypomodoro.db.ActivitiesDAO;

/**
 * ToDo list
 *
 */
public class ToDoList extends AbstractActivities {

    final private static ToDoList list = new ToDoList();

    private ToDoList() {
        refresh();
    }

    @Override
    final public void refresh() {
        activities.clear();
        for (Activity act : ActivitiesDAO.getInstance().getTODOs()) {
            activities.add(act);
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
        act.setPriority(size() + 1); // starting the priority at 1 (not 0)
        act.setIsCompleted(false);
        act.databaseUpdate();
        super.add(act);
    }

    @Override
    public void remove(Activity activity) {
        activities.remove(activity);        
    }
        
    public void delete(Activity activity) {        
        activities.remove(activity);
        activity.databaseDelete();
    }
    
    public void moveAll() {
        for (Activity activity : activities) {
            ActivityList.getList().add(activity); // this sets the priority and update the database
        }
        activities.clear();
        ActivitiesDAO.getInstance().moveAllTODOs();        
    }

    public void move(Activity act) {
        ActivityList.getList().add(act); // this sets the priority and update the database
        activities.remove(act);
    }

    public void complete(Activity a) {
        a.setIsCompleted(true);
        a.setDate(new Date());
        a.databaseUpdate();
        ReportList.getList().add(a);
        activities.remove(a);
    }

    public void completeAll() {
        for (Activity activity : activities) {
            activity.setIsCompleted(true);
            activity.setDate(new Date());
            ReportList.getList().add(activity);
        }
        activities.clear();
        ActivitiesDAO.getInstance().completeAllTODOs();
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
