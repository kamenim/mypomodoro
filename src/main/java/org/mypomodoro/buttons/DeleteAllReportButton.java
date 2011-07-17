package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.model.ReportList;

/**
 *
 * @author Phil Karoo
 */
public class DeleteAllReportButton extends myButton {

    public DeleteAllReportButton(final JTable table) {
        super(ControlPanel.labels.getString("Common.DeleteAll"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = table.getRowCount();
                if (rowCount > 0) {
                    JFrame window = new JFrame();
                    String title = ControlPanel.labels.getString("ReportListPanel.Delete all reports");
                    String message = ControlPanel.labels.getString("ReportListPanel.Are you sure to delete all reports?");
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        ReportList.getList().removeAll();
                    }
                }
            }
        });
    }
}