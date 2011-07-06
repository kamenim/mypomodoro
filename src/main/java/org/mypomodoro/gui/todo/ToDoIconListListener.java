package org.mypomodoro.gui.todo;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mypomodoro.model.Activity;

/**
 *
 * @author Phil Karoo
 */
public class ToDoIconListListener implements ListSelectionListener {

    private final ToDoListPanel panel;

    public ToDoIconListListener(ToDoListPanel panel) {
        this.panel = panel;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList) e.getSource();
        Activity selectedToDo = (Activity) list.getSelectedValue();
        if (selectedToDo != null && !panel.getPomodoro().inPomodoro()) {
            ToDoIconLabel.showIconLabel(panel.getIconLabel(), selectedToDo); // refresh ToDo Icon label when no pomodoro in progress
        }
    }
}