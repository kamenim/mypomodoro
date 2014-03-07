package org.mypomodoro.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.mypomodoro.db.ActivitiesDAO;

/**
 * Report list/**

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

    @Override
    public void add(Activity act) {
        act.setIsCompleted(true);
        act.setPriority(-1);
        act.setDate(new Date());
        if (act.getId() == -1) {
            act.databaseInsert();
        } else {
            act.databaseUpdate();
        }
        super.add(act);
    }

    public void delete(Activity activity) {
        activities.remove(activity);
        activity.databaseDelete();
    }

    public void deleteAll() {
        activities.clear();
        ActivitiesDAO.getInstance().deleteAllReports();
    }

    // move from Report list to Activity list
    public void reopen(Activity activity) {
        ActivityList.getList().add(activity);
        activities.remove(activity);
    }

    public void reopenAll() {        
        ActivityList.getList().addAll(activities);
        ActivitiesDAO.getInstance().reopenAllReports();
        activities.clear();
    }
}
