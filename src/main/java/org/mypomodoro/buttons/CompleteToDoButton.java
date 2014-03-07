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
public class CompleteToDoButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;

    public CompleteToDoButton(final String title, final String message, final ToDoPanel panel) {
        super(Labels.getString("ToDoListPanel.Complete"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {                
                if (panel.getTable().getSelectedRowCount() > 0) {
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply == JOptionPane.YES_OPTION) {
                        if (!panel.getPomodoro().inPomodoro() && panel.getTable().getSelectedRowCount() == panel.getTable().getRowCount()) { // complete all at once                       
                            panel.completeAll();
                            panel.refresh();
                        } else {
                            int increment = 0;
                            int[] rows = panel.getTable().getSelectedRows();
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
                            // reorder                            
                            panel.reorderByPriority();
                            for (int row = 0; row < panel.getTable().getModel().getRowCount(); row++) {
                                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                                Activity activity = panel.getActivityById(id);
                                panel.getTable().getModel().setValueAt(activity.getPriority(), panel.getTable().convertRowIndexToModel(row), 0); // priority column index = 0
                            }
                        }
                        // Refresh panel border
                        panel.setPanelBorder();
                    }
                }
            }
        });
    }
}
