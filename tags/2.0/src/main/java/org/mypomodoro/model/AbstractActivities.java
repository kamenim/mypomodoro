package org.mypomodoro.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractActivities implements
        Iterable<Activity> {

    protected List<Activity> activities = new LinkedList<Activity>();

    public void add(Activity activity) {
        activities.add(activity);
    }

    public boolean isEmpty() {
        return activities.isEmpty();
    }

    public abstract void refresh();

    public void remove(Activity activity) {
        activities.remove(activity);
    }

    public int size() {
        return activities.size();
    }

    public Object[] toArray() {
        return activities.toArray();
    }

    @Override
    public Iterator<Activity> iterator() {
        return activities.iterator();
    }

    public Activity getById(int id) {
        for (Activity activity : activities) {
            if (activity.getId() == id) {
                return activity;
            }
        }
        return null;
    }

    public int getNbEstimatedPom() {
        int nbEstimatedPom = 0;
        for (Iterator<Activity> it = iterator(); it.hasNext();) {
            nbEstimatedPom += it.next().getEstimatedPoms();
        }
        return nbEstimatedPom;
    }

    public int getNbTotalEstimatedPom() {
        int nbEstimatedPom = 0;
        for (Iterator<Activity> it = iterator(); it.hasNext();) {
            Activity a = it.next();
            nbEstimatedPom += a.getEstimatedPoms() + a.getOverestimatedPoms();
        }
        return nbEstimatedPom;
    }
}