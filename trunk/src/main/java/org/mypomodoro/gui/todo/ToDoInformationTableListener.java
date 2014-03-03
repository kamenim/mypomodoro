package org.mypomodoro.gui.todo;

import org.mypomodoro.gui.*;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;

public class ToDoInformationTableListener implements ListSelectionListener {

    private final JTable table;
    private final ActivityInformation information;
    private final int idKey;
    private final AbstractActivities activities;
    private final Pomodoro pomodoro;

    public ToDoInformationTableListener(AbstractActivities activities,
            JTable table, ActivityInformation information, int idKey, Pomodoro pomodoro) {
        this.activities = activities;
        this.table = table;
        this.information = information;
        this.idKey = idKey;
        this.pomodoro = pomodoro;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int[] rows = table.getSelectedRows();
        if (rows.length > 1 && information.isMultipleSelectionAllowed()) { // multiple selection
            String info = "";
            for (int row : rows) {
                Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), idKey);
                Activity selectedToDo = activities.getById(id);
                // excluding current running task
                if (pomodoro.inPomodoro() && selectedToDo.getId() == pomodoro.getCurrentToDo().getId()) {
                    continue;
                }
                info += selectedToDo.getName() + "\n";
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
