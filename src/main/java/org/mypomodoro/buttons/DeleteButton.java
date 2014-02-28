package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import org.mypomodoro.Main;
import org.mypomodoro.gui.AbstractActivitiesPanel;

import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * Delete activity button
 *
 */
public class DeleteButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;

    public DeleteButton(final String label, final AbstractActivitiesPanel panel) {
        super(Labels.getString("Common.Delete"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] rows = panel.getTable().getSelectedRows();
                if (rows.length > 0) {
                    String title = Labels.getString("ActivityListPanel.Delete activity");
                    int reply = JOptionPane.showConfirmDialog(Main.gui, label, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        int increment = 0;
                        for (int row : rows) {
                            row = row - increment;
                            Integer id = (Integer) panel.getTable().getModel().getValueAt(row, panel.getIdKey());
                            Activity selectedActivity = panel.getActivityById(id);
                            panel.delete(selectedActivity);
                            // removing a row requires decreasing  the row index number
                            panel.removeRow(row);
                            increment++;
                            // Refresh panel border
                            panel.setPanelBorder();
                        }
                        // select following activity in the list only when all rows are removed
                        panel.selectActivity();
                    }
                }
            }
        });
    }
}
