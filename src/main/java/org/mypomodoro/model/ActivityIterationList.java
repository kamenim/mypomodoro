package org.mypomodoro.model;

import org.mypomodoro.db.ActivitiesDAO;

/**
 * Activity Iteration list
 *
 */
public class ActivityIterationList extends AbstractActivities {

    private static ActivityIterationList list = new ActivityIterationList();

    private ActivityIterationList() {
        refresh();
    }

    @Override
    public void refresh() {
        activities.clear();
        for (Activity act : ActivitiesDAO.getInstance().getActivitiesByIteration(0)) {
            activities.add(act);
        }
    }

    public static ActivityIterationList getList() {
        return list;
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
                ActivityList.getList().remove(activity);
                break;
            }
        }
    }
    
    @Override
    public void add(Activity activity) {
        activities.add(activity);
        ActivityList.getList().add(activity);
    }

    @Override
    public void remove(Activity activity) {
        activities.remove(activity);
        //ActivityList.getList().remove(activity);
    }
}
