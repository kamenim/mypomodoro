package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.model.ActivityList;

public class DeleteAllButton extends myButton {

    public DeleteAllButton(final JTable table) {
        super(ControlPanel.labels.getString("Common.DeleteAll"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = table.getRowCount();
                if (rowCount > 0) {
                    JFrame window = new JFrame();
                    String title = ControlPanel.labels.getString("ActivityListPanel.Delete all activities");
                    String message = ControlPanel.labels.getString("ActivityListPanel.Are you sure to delete all activities?");
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        ActivityList.getList().removeAll();
                    }
                }
            }
        });
    }
}