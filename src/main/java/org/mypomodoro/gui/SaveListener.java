package org.mypomodoro.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;

public class SaveListener implements ActionListener {
	private CreatePanel createPanel;

	public SaveListener(CreatePanel panel) {
		this.createPanel = panel;
	}

    @Override
	public void actionPerformed(ActionEvent event) {
		Activity newActivity = createPanel.getFormPanel().getActivityFromFields();
		if (newActivity != null) {
			createPanel.saveActivity(newActivity);
		}
	}
}