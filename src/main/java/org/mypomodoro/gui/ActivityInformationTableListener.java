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
        if (table.getSelectedRowCount() > 1 && information.isMultipleSelectionAllowed()) { // multiple selection
            String info = "";
            int[] rows = table.getSelectedRows();
            for (int row : rows) {
                Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), idKey);
                Activity selectedActivity = activities.getById(id);
                info += selectedActivity.getName() + "\n";
            }
            information.showInfo(info);
        } else if (table.getSelectedRowCount() == 1) {
            int row = table.getSelectedRow();
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), idKey);
            Activity activity = activities.getById(id);
            information.selectInfo(activity);
            information.showInfo();
        } else {
            information.clearInfo();
        }
    }
}
