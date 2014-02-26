package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import org.mypomodoro.Main;

import org.mypomodoro.gui.reports.ReportListPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.Labels;

/**
 * Reopen activity button
 * Send report back to activity list
 *
 */
public class ReopenActivityButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;

    public ReopenActivityButton(final ReportListPanel reportsPanel) {
        super(Labels.getString("ReportListPanel.Reopen"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = reportsPanel.getTable().getSelectedRow();
                if (row > -1) {
                    String title = Labels.getString("ReportListPanel.Reopen activty");
                    String message = Labels.getString("ReportListPanel.Are you sure to reopen this activity?");
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        Integer id = (Integer) reportsPanel.getTable().getModel().getValueAt(row, ReportListPanel.ID_KEY);                     
                        ReportList.getList().reopen(id);
                        ReportList.getList().removeById(id);
                        reportsPanel.removeRow(row);
                    }
                }
            }
        });
    }
}
