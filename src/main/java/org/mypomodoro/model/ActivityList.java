package org.mypomodoro.model;

import org.mypomodoro.Main;

import db.ActivitiesDAO;

/**
 * 
 * @author Brian Wetzel
 */
public class ActivityList extends AbstractActivities {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// singleton object
	private static ActivityList list = null;

	// constructor is private, use getList method
	private ActivityList() {
		refresh();
	}

	@Override
	public void refresh() {
		activities.clear();
		for (Activity act : ActivitiesDAO.getInstance().getActivities()) {
			activities.add(act);
		}
	}

	public static ActivityList getList() {
		if (list == null)
			list = new ActivityList();
		return list;
	}

	public void removeById(final int id) {
		ActivitiesDAO.getInstance().removeById(id);
		Main.updateLists();
		Main.updateView();
	}

	@Override
	public Activity currentActivity() {
		if (size() == 0) {
			return null;
		}
		return activities.get(0);
	}
}
