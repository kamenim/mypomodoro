package org.mypomodoro.gui.todo;

import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mypomodoro.model.AbstractActivities;

import org.mypomodoro.model.Activity;

public class ToDoMergingListListener implements ListSelectionListener {

    private final JTable table;
    private final MergingPanel mergingPanel;
    private final int idKey;
    private final AbstractActivities activities;
    private final Pomodoro pomodoro;

    public ToDoMergingListListener(AbstractActivities activities,
            JTable table, MergingPanel mergingPanel, int idKey, Pomodoro pomodoro) {
        this.activities = activities;
        this.table = table;
        this.mergingPanel = mergingPanel;
        this.idKey = idKey;
        this.pomodoro = pomodoro;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int[] rows = table.getSelectedRows();
        if (rows.length > 1) {
            ArrayList<Activity> selectedTodos = new ArrayList<Activity>();
            for (int row : rows) {
                Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), idKey);
                Activity selectedToDo = activities.getById(id);
                // excluding current running task and finished task
                if ((pomodoro.inPomodoro() && selectedToDo.getId() == pomodoro.getCurrentToDo().getId())
                        || selectedToDo.isFinished()) {
                    continue;
                }
                selectedTodos.add(selectedToDo);
            }
            if (selectedTodos.size() > 1) {
                mergingPanel.displaySelectedToDos(selectedTodos);
            } else {
                mergingPanel.clearForm();
            }
        } else {
            mergingPanel.clearForm();
        }
    }
}
