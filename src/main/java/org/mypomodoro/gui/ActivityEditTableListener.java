package org.mypomodoro.gui;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mypomodoro.gui.create.EditPanel;

import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;

/**
 *
 * @author Phil Karoo
 */
public class ActivityEditTableListener implements ListSelectionListener {

    private final JTable table;
    private final EditPanel information;
    private final int idKey;
    private final AbstractActivities activities;

    public ActivityEditTableListener(AbstractActivities activities,
            JTable table, EditPanel information, int idKey) {
        this.activities = activities;
        this.table = table;
        this.information = information;
        this.idKey = idKey;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = table.getSelectedRow();

        if (row >= 0) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), idKey);
            Activity activity = activities.getById(id);
            if (activity != null) {
                information.fillOutInputForm(activity);
            }
        }
    }
}