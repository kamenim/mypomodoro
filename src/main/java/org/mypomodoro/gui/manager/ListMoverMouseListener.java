package org.mypomodoro.gui.manager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.mypomodoro.model.Activity;

public class ListMoverMouseListener extends MouseAdapter {
	private final ListPane from;
	private final ListPane to;
	public ListMoverMouseListener(ListPane from, ListPane to) {
		this.from = from;
		this.to = to;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() >= 2) {
			Activity selectedActivity = from.getSelectedActivity();
			if (selectedActivity != null) {
				to.addActivity(selectedActivity);
				from.removeActivity(selectedActivity);
			}
		}
	}
}
