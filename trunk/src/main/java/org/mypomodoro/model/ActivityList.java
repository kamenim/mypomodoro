package org.mypomodoro.model;

import java.util.Date;
import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.gui.PreferencesPanel;

/**
 * Activity list
 *
 */
public final class ActivityList extends AbstractActivities {

    private static final ActivityList list = new ActivityList();

    private ActivityList() {
        refresh();
    }

    @Override
    public void refresh() {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getActivities()) {
            super.add(act);
        }
    }

    public static ActivityList getList() {
        return list;
    }

    public static int getListSize() {
        return getList().size();
    }

    @Override
    public void add(Activity act) {
        act.setPriority(-1);
        act.setIsCompleted(false);
        if (PreferencesPanel.preferences.getAgileMode()) {
            act.setDate(new Date(0));
        }
        act.setDateCompleted(new Date(0));
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
        ActivitiesDAO.getInstance().deleteAllActivities();
        removeAll();
    }

    // move from Activity list to ToDo list
    public void move(Activity activity) {
        ToDoList.getList().add(activity); // this sets the priority and update the database
        remove(activity);
    }

    public void moveAll() {
        // no use
    }
}
