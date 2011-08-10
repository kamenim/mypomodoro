package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 *  Delete activity button
 * 
 * @author Phil Karoo
 */
public class DeleteButton extends MyButton {

    public DeleteButton(final JTable table) {
        super(Labels.getString("Common.Delete"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row > -1) {
                    JFrame window = new JFrame();
                    String title = Labels.getString("ActivityListPanel.Delete activity");
                    String message = Labels.getString("ActivityListPanel.Are you sure to delete this activity?");
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        Integer id = (Integer) table.getModel().getValueAt(row, ActivitiesPanel.ID_KEY);
                        ActivityList.getList().removeById(id);
                    }
                }
            }
        });
    }
}