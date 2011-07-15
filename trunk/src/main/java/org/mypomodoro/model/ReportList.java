package org.mypomodoro.model;

import org.mypomodoro.Main;

import org.mypomodoro.db.ActivitiesDAO;

/**
 *
 * @author Brian Wetzel
 */
public class ReportList extends AbstractActivities {

    private static ReportList list = new ReportList();

    private ReportList() {
        refresh();
    }

    @Override
    public void refresh() {
        activities.clear();
        for (Activity act : ActivitiesDAO.getInstance().getCompletedActivities()) {
            activities.add(act);
        }
    }

    public static ReportList getList() {
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
        ActivitiesDAO.getInstance().removeAllCompletedActivities();
        update();
    }
}