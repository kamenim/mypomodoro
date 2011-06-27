package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.model.ActivityList;

public class DeleteButton extends JButton {

    public DeleteButton(final JTable table) {
        super("Delete");
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row > -1) {
                    JFrame window = new JFrame();
                    String title = "Delete activity";
                    String message = "Are you sure to delete this activity?";
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        int id = (Integer) table.getModel().getValueAt(row, ActivitiesPanel.ID_KEY);
                        ActivityList.getList().removeById(id);
                    }
                }
            }
        });
    }
}