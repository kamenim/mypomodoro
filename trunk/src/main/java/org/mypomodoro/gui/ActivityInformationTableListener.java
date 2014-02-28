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
        int[] rows = table.getSelectedRows();
        if (rows.length > 1 && information.isMultipleSelectionAllowed()) { // multiple selection
            String info = "";
            for (int row : rows) {
                Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), idKey);
                Activity selectedActivity = activities.getById(id);
                info += selectedActivity.getName() + "\n";
            }
            information.showInfo(info);
        } else if (rows.length == 1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rows[0]), idKey);
            Activity activity = activities.getById(id);
            information.selectInfo(activity);
            information.showInfo();
        } else {
            information.clearInfo();
        }
    }
}
