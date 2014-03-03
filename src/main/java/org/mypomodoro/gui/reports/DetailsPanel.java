package org.mypomodoro.gui.reports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.buttons.MoveButton;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.activities.ActivityInformationPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays information on the current Report
 *
 */
public class DetailsPanel extends ActivityInformationPanel implements ActivityInformation {

    private static final long serialVersionUID = 20110814L;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public DetailsPanel(ReportListPanel reportsPanel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addReopenButton(reportsPanel);
        addInformationArea();
        addDeleteButton(reportsPanel);
    }

    private void addReopenButton(ReportListPanel reportsPanel) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        add(new MoveButton(Labels.getString("ReportListPanel.Reopen"), reportsPanel), gbc);
    }

    private void addInformationArea() {
        // add the information area
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        informationArea.setEditable(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        add(new JScrollPane(informationArea), gbc);
    }

    private void addDeleteButton(ReportListPanel reportsPanel) {
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        add(new DeleteButton(Labels.getString("ReportListPanel.Delete report"), Labels.getString("ActivityListPanel.Are you sure to delete those reports?"), reportsPanel), gbc);
    }

    @Override
    public void selectInfo(Activity activity) {
        super.selectInfo(activity);
        // replace date
        textMap.put("date", Labels.getString("Common.Date") + ": "
                + (activity.isUnplanned() ? "U [" : "")
                + DateUtil.getFormatedDate(activity.getDate())
                + " " + DateUtil.getFormatedTime(activity.getDate())
                + (activity.isUnplanned() ? "]" : "") + "\n");
        // add additional info
        textMap.put("real", Labels.getString("ReportListPanel.Real Pomodoros") + ": " + activity.getActualPoms() + "\n");
        textMap.put("diffi", Labels.getString("ReportListPanel.Diff I") + ": " + (activity.getActualPoms() - activity.getEstimatedPoms()) + "\n");
        textMap.put("diffii", Labels.getString("ReportListPanel.Diff II") + ": " + (activity.getOverestimatedPoms() > 0 ? activity.getActualPoms() - activity.getEstimatedPoms() - activity.getOverestimatedPoms() : "") + "\n");
        textMap.put("internal", Labels.getString("ReportListPanel.Internal Interruptions") + ": " + activity.getNumInternalInterruptions() + "\n");
        textMap.put("external", Labels.getString("ReportListPanel.External Interruptions") + ": " + activity.getNumInterruptions() + "\n");
    }
}
