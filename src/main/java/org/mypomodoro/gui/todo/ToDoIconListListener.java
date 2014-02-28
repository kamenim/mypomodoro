package org.mypomodoro.gui.todo;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Icon list listener
 *
 */
public class ToDoIconListListener implements ListSelectionListener {

    private final ToDoPanel panel;

    public ToDoIconListListener(ToDoPanel panel) {
        this.panel = panel;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        panel.refreshIconLabels();
    }
}
