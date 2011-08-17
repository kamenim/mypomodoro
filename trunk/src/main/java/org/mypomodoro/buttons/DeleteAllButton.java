package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * Delete all activities button
 * 
 * @author Phil Karoo
 */
public class DeleteAllButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;

    public DeleteAllButton(final JTable table) {
        super(Labels.getString("Common.Delete all"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = table.getRowCount();
                if (rowCount > 0) {
                    JFrame window = new JFrame();
                    String title = Labels.getString("ActivityListPanel.Delete all activities");
                    String message = Labels.getString("ActivityListPanel.Are you sure to delete all activities?");
                    int reply = JOptionPane.showConfirmDialog(window, message,
                            title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        ActivityList.getList().removeAll();
                    }
                }
            }
        });
    }
}