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
    
    /*private ActivityList(ActivityList list) {
        this.list = list;
    }*/

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
    public void removeById(final int id) {
        ActivitiesDAO.getInstance().removeById(id);
        for (Activity activity : activities) {
            if (activity.getId() == id) {
                activities.remove(activity);
                ActivityIterationList.getList().remove(activity);
                break;
            }
        }
    }

    public void removeAll() {
        ActivitiesDAO.getInstance().removeAllActivities();
        activities.clear();
        ActivityIterationList.getList().activities.clear();
    }
    
    @Override
    public void remove(Activity activity) {
        activities.remove(activity);
        ActivityIterationList.getList().remove(activity);
    }
}
