package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import org.mypomodoro.Main;
import org.mypomodoro.gui.AbstractActivitiesPanel;

import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Delete button
 *
 */
public class DeleteButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;

    public DeleteButton(final String title, final String message, final AbstractActivitiesPanel panel) {
        super(Labels.getString("Common.Delete"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] rows = panel.getTable().getSelectedRows();
                if (rows.length > 0) {
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply == JOptionPane.YES_OPTION) {
                        if (rows.length == panel.getTable().getRowCount()) { // delete all at once                        
                            panel.deleteAll();
                            panel.refresh();
                        } else {
                            int increment = 0;
                            for (int row : rows) {
                                row = row - increment;
                                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                                Activity selectedActivity = panel.getActivityById(id);
                                panel.delete(selectedActivity);
                                // removing a row requires decreasing  the row index number
                                panel.removeRow(row);
                                increment++;
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
