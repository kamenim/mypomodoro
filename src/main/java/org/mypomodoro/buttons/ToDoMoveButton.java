package org.mypomodoro.buttons;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mypomodoro.gui.todo.ToDoPanel;

import org.mypomodoro.model.Activity;

/**
 * Move button
 *
 */
public class ToDoMoveButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension BUTTON_SIZE = new Dimension(100, 30);

    public ToDoMoveButton(String label, final ToDoPanel panel) {
        super(label);
        setMinimumSize(BUTTON_SIZE);
        setPreferredSize(BUTTON_SIZE);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                move(panel);
            }
        });
    }

    public void move(final ToDoPanel panel) {
        int[] rows = panel.getTable().getSelectedRows();
        if (rows.length > 0) {
            boolean agreed = false;
            int increment = 0;
            for (int row : rows) {
                row = row - increment;
                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                Activity selectedToDo = panel.getActivityById(id);
                if (panel instanceof ToDoPanel) {
                    // excluding current running task
                    if (panel.getPomodoro().inPomodoro() && selectedToDo.getId() == panel.getPomodoro().getCurrentToDo().getId()) {
                        continue;
                    }
                }
                panel.move(selectedToDo);
                // removing a row requires decreasing  the row index number
                panel.removeRow(row);
                increment++;
            }
            // Refresh panel border
            panel.setPanelBorder();
            // select following activity in the list only when all rows are removed
            panel.selectActivity();
        }
    }
}
