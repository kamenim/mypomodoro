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
    public void remove(Activity activity) {
        activities.remove(activity);
        activity.databaseDelete();
    }
    
    // move from Report list to Activity list
    public void reopen(Activity activity) {        
        ActivityList.getList().add(activity); // this sets the priority to -1 and non conplete and updates the database
        activities.remove(activity);
    }
}
