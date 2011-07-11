package org.mypomodoro.gui.reports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.text.DefaultCaret;
import org.mypomodoro.buttons.DeleteReportButton;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.model.Activity;

/**
 * Panel that displays information on the current Report
 *
 */
public class InformationArea extends JPanel implements ActivityInformation {

    private final JTextArea informationArea = new JTextArea();
    private final GridBagConstraints gbc = new GridBagConstraints();

    public InformationArea(JTable table) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addInformationArea();
        addDeleteButton(table);
    }

    private void addDeleteButton(JTable table) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        add(new DeleteReportButton(table), gbc);
    }

    private void addInformationArea() {
        // add the information area
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        informationArea.setEditable(false);
        // disable auto scrolling
        DefaultCaret caret = (DefaultCaret) informationArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        add(new JScrollPane(informationArea), gbc);
    }

    @Override
    public void showInfo(Activity activity) {
        String pattern = "dd MMM yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String activityDate = format.format(activity.getDate());
        String text = ControlPanel.labels.getString("Common.Date") + ": ";
        if (activity.isUnplanned()) {
            text += "U [";
        }
        text += activityDate;
        if (activity.isUnplanned()) {
            text += "]";
        }
        pattern = "HH:mm";
        format = new SimpleDateFormat(pattern);
        String timeDate = format.format(activity.getDate());
        text += " " + timeDate;
        text += "\n" + ControlPanel.labels.getString("Common.Title") + ": " + activity.getName()
                + "\n" + ControlPanel.labels.getString("Common.Estimated Pomodoros") + ": " + activity.getEstimatedPoms();
        if (activity.getOverestimatedPoms() > 0) {
            text += " + " + activity.getOverestimatedPoms();
        }
        text += "\n" + ControlPanel.labels.getString("ReportListPanel.Real Pomodoros") + ": " + activity.getActualPoms()
                + "\n" + ControlPanel.labels.getString("ReportListPanel.Diff I") + ": " + ( activity.getActualPoms() - activity.getEstimatedPoms() )
                + "\n" + ControlPanel.labels.getString("ReportListPanel.Diff II") + ": " + ( activity.getOverestimatedPoms() > 0 ? activity.getActualPoms() - activity.getEstimatedPoms() - activity.getOverestimatedPoms() : "" )
                + "\n" + ControlPanel.labels.getString("ReportListPanel.Internal Interruptions") + ": " + activity.getNumInternalInterruptions()
                + "\n" + ControlPanel.labels.getString("ReportListPanel.External Interruptions") + ": " + activity.getNumInterruptions()
                + "\n" + ControlPanel.labels.getString("Common.Type") + ": " + activity.getType()
                + "\n" + ControlPanel.labels.getString("Common.Author") + ": " + activity.getAuthor()
                + "\n" + ControlPanel.labels.getString("Common.Place") + ": " + activity.getPlace()
                + "\n" + ControlPanel.labels.getString("Common.Description") + ": " + activity.getDescription();
        informationArea.setText(text);
    }

    @Override
    public void clearInfo() {
        informationArea.setText("");
    }
}