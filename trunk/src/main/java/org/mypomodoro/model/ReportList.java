package org.mypomodoro.model;

import org.mypomodoro.db.ActivitiesDAO;

/**
 * Report list
 *
 */
public class ReportList extends AbstractActivities {

    private static ReportList list = new ReportList();

    private ReportList() {
        refresh();
    }

    @Override
    public void refresh() {
        activities.clear();
        for (Activity act : ActivitiesDAO.getInstance().getReports()) {
            activities.add(act);
        }
    }

    public static ReportList getList() {
        return list;
    }

    // This makes sure we have a list properly sorted by database
    public static ReportList getListFromDB() {
        return new ReportList();
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
                break;
            }
        }
    }    

    public void removeAll() {
        ActivitiesDAO.getInstance().removeAllReports();
        activities.clear();
    }
    
    public void reopen(int id) {
        Activity act = getById(id);
        act.setPriority(-1);
        act.setIsCompleted(false);
        act.databaseUpdate();
        ActivityList.getList().add(act);
        activities.remove(act);
    }
}
