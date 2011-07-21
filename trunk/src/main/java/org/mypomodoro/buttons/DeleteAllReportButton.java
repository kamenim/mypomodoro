package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.Labels;

/**
 *
 * @author Phil Karoo
 */
public class DeleteAllReportButton extends MyButton {

    public DeleteAllReportButton(final JTable table) {
        super(Labels.getString("Common.DeleteAll"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int rowCount = table.getRowCount();
                if (rowCount > 0) {
                    JFrame window = new JFrame();
                    String title = Labels.getString("ReportListPanel.Delete all reports");
                    String message = Labels.getString("ReportListPanel.Are you sure to delete all reports?");
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        ReportList.getList().removeAll();
                    }
                }
            }
        });
    }
}