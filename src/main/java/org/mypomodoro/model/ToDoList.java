package org.mypomodoro.model;

import org.mypomodoro.Main;
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

    // This makes sure we have a list properly sorted by database
    public static ToDoList getListFromDB() {
        return new ToDoList();
    }

    public static int getListSize() {
        return getList().size();
    }

    @Override
    public void add(Activity act) {
        act.setPriority(size());
        act.setIsCompleted(false);
        act.databaseUpdate();
        super.add(act);
    }

    @Override
    public void remove(Activity a) {
        activities.remove(a);
        a.setPriority(-1);
        a.databaseUpdate();
    }

    public void update() {
        Main.updateLists();
        Main.updateView();
    }

    public void completeAll() {
        ActivitiesDAO.getInstance().completeAllTODOs();
        update();
    }
    
    // move from ToDO list to Activity list
    /*public void move(int id) {
        Activity act = getById(id);
        ActivityList.getList().add(act); // this sets the priority and update the database
        activities.remove(act);
    }*/
    
    public void move(Activity act) {         
        ActivityList.getList().add(act); // this sets the priority and update the database
        activities.remove(act);
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
