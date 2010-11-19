package org.mypomodoro.gui;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;

public class ActivityInformationTableListener implements ListSelectionListener {

	private final JTable table;
	private final ActivityInformation information;
	private final int idKey;
	private final AbstractActivities activities;

	public ActivityInformationTableListener(AbstractActivities activities,
			JTable table, ActivityInformation information, int idKey) {
		this.activities = activities;
		this.table = table;
		this.information = information;
		this.idKey = idKey;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int row = 0;

		row = table.getSelectedRow();
		/*
		 * Added check to handle when the row is deleted. This results in
		 * getSelectedRow() returning -1 Other uses should return a valid row
		 * number. This was added to prevent an out of range exception
		 */
		if (row >= 0) {
			Integer id = (Integer) table.getModel().getValueAt(row, idKey);
			Activity activity = activities.getById(id);
			if (activity != null) {
				information.showInfo(activity);
			}
		} else {
			information.clearInfo();
		}
	}

}
