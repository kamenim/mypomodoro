package org.mypomodoro.gui;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mypomodoro.model.Activity;

public class ActivityInformationListListener implements ListSelectionListener {

    private final ActivityInformation information;

    public ActivityInformationListListener(ActivityInformation information) {
        this.information = information;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList) e.getSource();
        Activity activity = (Activity) list.getSelectedValue();
        if (activity != null) {
            information.showInfo(activity);
        } else {
            information.clearInfo();
        }
    }
}
