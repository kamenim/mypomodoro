package org.mypomodoro.model;

import db.ActivitiesDAO;

/**
 * 
 * @author Brian Wetzel
 */
public class ToDoList extends AbstractActivities {
	private static ToDoList list = new ToDoList();

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
		return list;
	}

    public static int getListSize() {
		return getList().size();
	}

	@Override
	public void add(Activity act) {
		act.setPriority(size());
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
	 * replaced into a buffer and then switching the values. The root node
	 * cannot be changed.
	 * 
	 * @param index
	 */
	/*public void demote(int index) {
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
	}*/
}