package org.mypomodoro.model;

import db.ActivitiesDAO;

/**
 * 
 * @author Brian Wetzel
 */
public class ToDoList extends AbstractActivities {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * cannot add more pomodoros to a list than can be completed in 24hrs. This
	 * is based on the estimated pomodoros. TODO: Use this field to by
	 * calculating the estimated poms for every task when added to this list
	 */
	@SuppressWarnings("unused")
	private static final int MAXIMUM_POMS = 40;

	// singleton object
	private static ToDoList list = null;

	// constructor is private, use getList method
	private ToDoList() {
		refresh();
	}

	@Override
	public void refresh() {
		activities.clear();
		for (Activity act : ActivitiesDAO.getInstance().getTodoList()) {
			activities.add(act);
		}
	}

	public static ToDoList getList() {
		if (list == null)
			list = new ToDoList();
		return list;
	}

	@Override
	public void add(Activity act) {
		act.setPriority(size() - 1);
		super.add(act);
	}

	@Override
	public void remove(Activity a) {
		int index = activities.indexOf(a);
		activities.remove(a);
		a.setPriority(-1);

		for (int j = index; j < size(); j++) {
			Activity currentAct = activities.get(j);
			currentAct.setPriority(j);
		}
	}

	/**
	 * Promote the Activity at the desired index, by storing the value to be
	 * replaced into a buffer and then switching the values. The root node
	 * cannote be changed.
	 * 
	 * @param index
	 */
	public void promote(Activity a) {
		int index = activities.indexOf(a);
		if (index > 0) {
			Activity lower = a;
			Activity higher = activities.get(index - 1);
			activities.set(index - 1, lower);
			activities.set(index, higher);
			lower.setPriority(index - 1);
			higher.setPriority(index);
		}
	}

	/**
	 * Demote the Activity at the desired index, by storing the value to be
	 * replaced into a buffer adn then switching the values. The root node
	 * cannot be changed.
	 * 
	 * @param index
	 */
	public void demote(int index) {
		if (index < (this.size() - 1)) {
			Activity higher = activities.get(index);
			Activity lower = activities.get(index + 1);
			activities.set(index, lower);
			activities.set(index + 1, higher);
			lower.setPriority(index);
			higher.setPriority(index + 1);
		}
	}

	public void complete() {
		Activity activity = activities.remove(0);
		activity.setIsCompleted(true);

		for (int j = 0; j < (this.size() - 1); j++) {
			Activity currentAct = activities.get(j);
			currentAct.setPriority(j);
		}

		ReportList.getList().add(activity);
	}
}
