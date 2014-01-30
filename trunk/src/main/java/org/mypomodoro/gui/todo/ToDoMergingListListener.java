package org.mypomodoro.gui.todo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
        // jdk 6
        Object objectArray[] = list.getSelectedValues();
        Activity[] selectedTodosArray = Arrays.copyOf(objectArray, objectArray.length, Activity[].class);
        List<Activity> selectedTodos = new ArrayList<Activity>();
        selectedTodos.addAll(Arrays.asList(selectedTodosArray)); // this way, selectedTodos doesn't have a fixed size and therefore objects may be removed while iterating        
        // jdk7
        //List<Activity> selectedTodos = list.getSelectedValuesList();

        if (selectedTodos.size() > 1) {
            for (Iterator<Activity> iter = selectedTodos.iterator(); iter.hasNext();) {
                Activity selectedToDo = iter.next();
                // excluding current running task and finished task
                if ((pomodoro.inPomodoro() && selectedToDo.getId() == pomodoro.getCurrentToDo().getId())
                        || selectedToDo.isFinished()) {
                    iter.remove(); // this will modify the list selectedTodos too
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
