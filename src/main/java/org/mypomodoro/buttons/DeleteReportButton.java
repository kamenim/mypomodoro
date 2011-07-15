package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.gui.reports.ReportListPanel;
import org.mypomodoro.model.ReportList;

/**
 *
 * @author Phil Karoo
 */
public class DeleteReportButton extends myButton {

    public DeleteReportButton(final JTable table) {
        super(ControlPanel.labels.getString("Common.Delete"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row > -1) {
                    JFrame window = new JFrame();
                    String title = ControlPanel.labels.getString("ReportListPanel.Delete report");
                    String message = ControlPanel.labels.getString("ReportListPanel.Are you sure to delete this report?");
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        Integer id = (Integer) table.getModel().getValueAt(row, ReportListPanel.ID_KEY);
                        ReportList.getList().removeById(id);
                    }
                }
            }
        });
    }
}