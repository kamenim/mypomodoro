package org.mypomodoro.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;

public class SaveListener implements ActionListener {

    private CreatePanel panel;

    public SaveListener(CreatePanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Activity newActivity = panel.getFormPanel().getActivityFromFields();
        if (newActivity != null) {
            panel.saveActivity(newActivity);
        }
    }
}