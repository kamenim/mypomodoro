package org.mypomodoro.model;

import org.mypomodoro.Main;

import org.mypomodoro.db.ActivitiesDAO;

/**
 *
 * @author Brian Wetzel 
 * @author Phil Karoo
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

    public static int getListSize() {
        return getList().size();
    }

    public void removeById(final int id) {
        ActivitiesDAO.getInstance().removeById(id);
        update();
    }

    public void update() {
        Main.updateLists();
        Main.updateView();
    }
    
    public void removeAll() {
        ActivitiesDAO.getInstance().removeAllActivities();
        update();
    }
}