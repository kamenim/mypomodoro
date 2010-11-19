package org.mypomodoro.model;

import db.ActivitiesDAO;

/**
 * 
 * @author Brian Wetzel
 */
public class ReportList extends AbstractActivities {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static AbstractActivities list = new ReportList();

	public ReportList() {
		refresh();
	}

	@Override
	public void refresh() {
		activities.clear();
		for (Activity activity : ActivitiesDAO.getInstance()
				.getCompletedActivities()) {
			activities.add(activity);
		}
	}

	public static AbstractActivities getList() {
		return list;
	}
}
