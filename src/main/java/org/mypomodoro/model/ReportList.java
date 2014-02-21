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

    public static int getListSize() {
        return getList().size();
    }

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
}
