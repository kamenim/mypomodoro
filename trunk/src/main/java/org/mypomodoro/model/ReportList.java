package org.mypomodoro.model;

import java.util.Date;
import org.mypomodoro.db.ActivitiesDAO;

/**
 * Report list
 *
 */
public final class ReportList extends AbstractActivities {

    private static final ReportList list = new ReportList();

    private ReportList() {
        refresh();
    }

    @Override
    public void refresh() {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getReports()) {
            super.add(act);
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
        act.setPriority(-1);
        act.setIsCompleted(true);
        act.setDateCompleted(new Date());
        if (act.getId() == -1) {
            act.setId(act.databaseInsert());
        } else {
            act.databaseUpdate();
        }
        super.add(act);
    }

    public void delete(Activity activity) {
        remove(activity);
        activity.databaseDelete();
    }

    public void deleteAll() {
        ActivitiesDAO.getInstance().deleteAllReports();
        removeAll();
    }

    // move from Report list to Activity list
    public void reopen(Activity activity) {
        ActivityList.getList().add(activity);
        remove(activity);
    }

    public void reopenAll() {
        for (Activity activity : activities) {
            ActivityList.getList().add(activity);
        }
        ActivitiesDAO.getInstance().reopenAllReports();
        removeAll();
    }
}
