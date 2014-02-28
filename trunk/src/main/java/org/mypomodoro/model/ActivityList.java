package org.mypomodoro.model;

import org.mypomodoro.db.ActivitiesDAO;

/**
 * Activity list
 *
 */
public class ActivityList extends AbstractActivities {

    private static ActivityList list = new ActivityList();

    private ActivityList() {
        refresh();
    }

    @Override
    public void refresh() {
        activities.clear();
        for (Activity act : ActivitiesDAO.getInstance().getActivities()) {
            activities.add(act);
        }
    }

    public static ActivityList getList() {
        return list;
    }

    // This makes sure we have a list properly sorted by database
    public static ActivityList getListFromDB() {
        return new ActivityList();
    }

    public static int getListSize() {
        return getList().size();
    }

    @Override
    public void add(Activity act) {
        act.setPriority(-1);
        act.setIsCompleted(false);
        act.databaseUpdate();
        super.add(act);
    }
    
    @Override
    public void remove(Activity activity) {
        activities.remove(activity);
        activity.databaseDelete();
    }
    
    // move from Activity list to ToDo list
    public void move(Activity activity) {
        ToDoList.getList().add(activity); // this sets the priority and update the database
        activities.remove(activity);
    }
}
