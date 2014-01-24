package org.mypomodoro.gui.todo;

import java.util.List;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mypomodoro.model.Activity;

public class ToDoMergingListListener implements ListSelectionListener {

    private final MergingPanel mergingPanel;
    private final Pomodoro pomodoro;

    public ToDoMergingListListener(MergingPanel mergingPanel, Pomodoro pomodoro) {
        this.mergingPanel = mergingPanel;
        this.pomodoro = pomodoro;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList) e.getSource();
        List<Activity> selectedTodos = list.getSelectedValuesList();
        if (selectedTodos.size() > 1) {
            for (Activity selectedToDo : selectedTodos) {
                if (selectedToDo != null) {
                    if (pomodoro.inPomodoro() && selectedToDo.getId() == pomodoro.getCurrentToDo().getId()) {
                        selectedTodos.remove(selectedToDo);
                    }
                }
            }
        }
        if (selectedTodos.size() > 1) {
            mergingPanel.displaySelectedToDos(selectedTodos);
        } else if (selectedTodos.size() == 1) {
            mergingPanel.clearForm();
        }
    }
}
