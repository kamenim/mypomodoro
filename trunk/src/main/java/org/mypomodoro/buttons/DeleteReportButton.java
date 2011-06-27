package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.mypomodoro.gui.reports.ReportListPanel;
import org.mypomodoro.model.ReportList;

/**
 *
 * @author Phil Karoo
 */
public class DeleteReportButton extends JButton {

    public DeleteReportButton(final JTable table) {
        super("Delete");
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row > -1) {
                    JFrame window = new JFrame();
                    String title = "Delete report";
                    String message = "Are you sure to delete this report?";
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        int id = (Integer) table.getModel().getValueAt(row, ReportListPanel.ID_KEY);
                        ReportList.getList().removeById(id);
                    }
                }
            }
        });
    }
}