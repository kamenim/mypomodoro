package org.mypomodoro.model;

import java.util.Date;
import org.mypomodoro.db.ActivitiesDAO;

/**
 * ToDo list
 *
 */
public final class ToDoList extends AbstractActivities {

    private static final ToDoList list = new ToDoList();

    private ToDoList() {
        refresh();
    }

    @Override
    final public void refresh() {
        removeAll();
        for (Activity act : ActivitiesDAO.getInstance().getTODOs()) {
            super.add(act);
        }
    }

    public static ToDoList getList() {
        return list;
    }

    public static int getListSize() {
        return getList().size();
    }

    @Override
    public void add(Activity act) {
        act.setPriority(size() + 1);
        act.setIsCompleted(false);
        act.setDateCompleted(new Date(0));
        act.setDate(new Date());
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

    public void move(Activity act) {
        ActivityList.getList().add(act); // this sets the priority and update the database
        remove(act);
    }

    public void moveAll() {
        for (Activity activity : activities) {
            ActivityList.getList().add(activity);
        }
        ActivitiesDAO.getInstance().moveAllTODOs();
        removeAll();
    }

    public void complete(Activity a) {
        ReportList.getList().add(a);
        remove(a);
    }

    public void completeAll() {
        for (Activity activity : activities) {
            ReportList.getList().add(activity);
        }
        ActivitiesDAO.getInstance().completeAllTODOs();
        removeAll();
    }

    // set new priorities
    public void reorderByPriority() {
        sortByPriority();
        int increment = 1;
        for (Activity activity : activities) {
            activity.setPriority(increment);
            update(activity);
            activity.databaseUpdate();
            increment++;
        }
    }
}
