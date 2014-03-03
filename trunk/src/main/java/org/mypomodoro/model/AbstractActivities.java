package org.mypomodoro.model;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class AbstractActivities implements Iterable<Activity> {

    protected LinkedList<Activity> activities = new LinkedList<Activity>();

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

    public void update(Activity activity) {
        activities.set(getIndex(activity), activity);
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
        Activity activity = null;
        for (Activity act : activities) {
            if (act.getId() == id) {
                activity = act;
                break;
            }
        }
        return activity;
    }

    public void removeById(int id) {
        for (Activity activity : activities) {
            if (activity.getId() == id) {
                remove(activity);
                break;
            }
        }
    }

    public void removeAll() {
        activities.clear();
    }

    public int getIndex(Activity activity) {
        int index = 0;
        for (Activity act : activities) {
            if (act.getId() == activity.getId()) {
                break;
            }
            index++;
        }
        return index;
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

    public float getStoryPoints() {
        float storyPoints = 0;
        for (Iterator<Activity> it = iterator(); it.hasNext();) {
            storyPoints += it.next().getStoryPoints();
        }
        return storyPoints;
    }
}
