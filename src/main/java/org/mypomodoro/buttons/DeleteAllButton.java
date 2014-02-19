package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.ActivitiesPanel;

import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * Delete all activities button
 *
 */
public class DeleteAllButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;

    public DeleteAllButton(final ActivitiesPanel activitiesPanel) {
        super(Labels.getString("Common.Delete all"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = activitiesPanel.getTable().getRowCount();
                if (rowCount > 0) {
                    String title = Labels.getString("ActivityListPanel.Delete all activities");
                    String message = Labels.getString("ActivityListPanel.Are you sure to delete all activities?");
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                            title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        ActivityList.getList().removeAll();
                        activitiesPanel.refresh();
                    }
                }
            }
        });
    }
}
