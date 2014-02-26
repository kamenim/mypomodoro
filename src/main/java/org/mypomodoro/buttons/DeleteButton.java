package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import org.mypomodoro.Main;

import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * Delete activity button
 *
 */
public class DeleteButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;

    public DeleteButton(final ActivitiesPanel activitiesPanel) {
        super(Labels.getString("Common.Delete"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = activitiesPanel.getTable().getSelectedRow();
                if (row > -1) {
                    String title = Labels.getString("ActivityListPanel.Delete activity");
                    String message = Labels.getString("ActivityListPanel.Are you sure to delete this activity?");
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        Integer id = (Integer) activitiesPanel.getTable().getModel().getValueAt(row, ActivitiesPanel.ID_KEY);
                        Activity act = Activity.getActivity(id);
                        ActivityList.getList().remove(act);
                        act.databaseDelete();
                        activitiesPanel.removeRow(row);
                    }
                }
            }
        });
    }
}
