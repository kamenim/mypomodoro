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
public class MoveToDoButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension BUTTON_SIZE = new Dimension(100, 30);

    public MoveToDoButton(String label, final ToDoPanel panel) {
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
        if (panel.getTable().getSelectedRowCount() > 0) {
            if (!panel.getPomodoro().inPomodoro()
                    && panel.getTable().getSelectedRowCount() == panel.getTable().getRowCount()) { // complete all at once                       
                panel.moveAll();
                panel.refresh();
            } else {
                int increment = 0;
                int[] rows = panel.getTable().getSelectedRows();
                for (int row : rows) {
                    row = row - increment;
                    Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                    Activity selectedToDo = panel.getActivityById(id);
                    // excluding current running task
                    if (panel.getPomodoro().inPomodoro() && selectedToDo.getId() == panel.getPomodoro().getCurrentToDo().getId()) {
                        continue;
                    }
                    panel.move(selectedToDo);
                    // removing a row requires decreasing  the row index number
                    panel.removeRow(row);
                    increment++;
                }
            }
            // Refresh panel border
            panel.setPanelBorder();
            // reorder                            
            panel.reorderByPriority();
            for (int row = 0; row < panel.getTable().getModel().getRowCount(); row++) {
                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                Activity activity = panel.getActivityById(id);
                panel.getTable().getModel().setValueAt(activity.getPriority(), panel.getTable().convertRowIndexToModel(row), 0); // priority column index = 0
            }
        }
    }
}
