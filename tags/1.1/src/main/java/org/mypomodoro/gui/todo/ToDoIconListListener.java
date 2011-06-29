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
    private final Pomodoro pomodoro;

    public ToDoIconListListener(ToDoListPanel panel, Pomodoro pomodoro) {
        this.panel = panel;
        this.pomodoro = pomodoro;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList) e.getSource();
        Activity selectedToDo = (Activity) list.getSelectedValue();
        if (selectedToDo == null || 
               (selectedToDo != null && !pomodoro.inPomodoro())) {
            panel.setIconLabel(); // refresh ToDo Icon label when empty list OR no pomodoro in progress
        }
    }
}