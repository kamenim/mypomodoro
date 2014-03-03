package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import org.mypomodoro.Main;
import org.mypomodoro.gui.todo.ToDoPanel;

import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Delete button
 *
 */
public class CompleteButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;

    public CompleteButton(final String title, final String message, final ToDoPanel panel) {
        super(Labels.getString("ToDoListPanel.Complete"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] rows = panel.getTable().getSelectedRows();
                if (rows.length > 0) {
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply == JOptionPane.YES_OPTION) {
                        if (rows.length == panel.getTable().getRowCount()) { // complete all at once                        
                            panel.completeAll();
                            panel.refresh();
                        } else {
                            int increment = 0;
                            for (int row : rows) {
                                row = row - increment;
                                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                                Activity selectedActivity = panel.getActivityById(id);
                                // excluding current running task
                                if (panel.getPomodoro().inPomodoro() && selectedActivity.getId() == panel.getPomodoro().getCurrentToDo().getId()) {
                                    continue;
                                }
                                panel.complete(selectedActivity);
                                // removing a row requires decreasing  the row index number
                                panel.removeRow(row);
                                increment++;
                            }
                            // select following activity in the list only when all rows are removed
                            panel.selectActivity();
                        }
                        // Refresh panel border
                        panel.setPanelBorder();
                    }
                }
            }
        });
    }
}
