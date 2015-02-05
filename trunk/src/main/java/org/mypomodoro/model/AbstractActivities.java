/* 
 * Copyright (C) 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public abstract class AbstractActivities implements Iterable<Activity> {

    protected ArrayList<Activity> activities = new ArrayList<Activity>();

    public abstract void refresh();

    public void add(Activity activity) {
        activities.add(activity);
    }

    public boolean isEmpty() {
        return activities.isEmpty();
    }

    // Update activity from database
    public void refreshById(int id) {
        for (int index = 0; index < size(); index++) {
            Activity act = get(index);
            if (act.getId() == id) {
                update(index, Activity.getActivity(id));
            }
        }
    }

    public void remove(Activity activity) {
        activities.remove(activity);
    }

    public void update(Activity activity) {
        update(getIndex(activity), activity);
    }

    public void update(int index, Activity activity) {
        activities.set(index, activity);
    }

    public int size() {
        return activities.size();
    }

    public Activity get(int index) {
        return activities.get(index);
    }

    public Object[] toArray() {
        return activities.toArray();
    }

    @Override
    public Iterator<Activity> iterator() {
        return activities.iterator();
    }

    public void sortByPriority() {
        Collections.sort(activities, new Comparator<Activity>() {

            @Override
            public int compare(Activity a1, Activity a2) {
                Integer p1 = (Integer) a1.getPriority();
                Integer p2 = (Integer) a2.getPriority();
                return p1.compareTo(p2);
            }
        });
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

    public int getNbOverestimatedPom() {
        int nbOvestimatedPom = 0;
        for (Iterator<Activity> it = iterator(); it.hasNext();) {
            nbOvestimatedPom += it.next().getOverestimatedPoms();
        }
        return nbOvestimatedPom;
    }

    public int getNbRealPom() {
        int nbRealPom = 0;
        for (Iterator<Activity> it = iterator(); it.hasNext();) {
            nbRealPom += it.next().getActualPoms();
        }
        return nbRealPom;
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

    public int getAccuracy() {
        int estover = 0;
        int real = 0;
        for (Iterator<Activity> it = iterator(); it.hasNext();) {
            Activity a = it.next();
            estover += a.getEstimatedPoms() + a.getOverestimatedPoms();
            real += a.getActualPoms();
        }
        int accuracy = Math.round(((float) real / (float) estover) * 100);
        return accuracy;
    }
}
