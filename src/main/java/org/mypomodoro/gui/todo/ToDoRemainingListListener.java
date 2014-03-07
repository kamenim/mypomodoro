package org.mypomodoro.gui.todo;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Icon list listener
 *
 */
public class ToDoRemainingListListener implements ListSelectionListener {

    private final ToDoPanel panel;

    public ToDoRemainingListListener(ToDoPanel panel) {
        this.panel = panel;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        panel.refreshRemaining();
    }
}
