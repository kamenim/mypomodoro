package org.mypomodoro.gui.reports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.text.DefaultCaret;

import org.mypomodoro.buttons.DeleteAllReportButton;
import org.mypomodoro.buttons.DeleteReportButton;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays information on the current Report
 *
 */
public class InformationArea extends JPanel implements ActivityInformation {
	private static final long serialVersionUID = 20110814L;
	
    private final JTextArea informationArea = new JTextArea();
    private final GridBagConstraints gbc = new GridBagConstraints();

    public InformationArea(JTable table) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addInformationArea();
        addDeleteButton(table);
        addDeleteAllButton(table);
    }

    private void addDeleteButton(JTable table) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 1;
        //gbc.fill = GridBagConstraints.NONE;
        add(new DeleteReportButton(table), gbc);
    }

    private void addDeleteAllButton(JTable table) {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.gridheight = 1;
        //gbc.fill = GridBagConstraints.NONE;
        add(new DeleteAllReportButton(table), gbc);
    }

    private void addInformationArea() {
        // add the information area
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 2;
        informationArea.setEditable(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        // disable auto scrolling
        DefaultCaret caret = (DefaultCaret) informationArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        add(new JScrollPane(informationArea), gbc);
    }

    @Override
    public void showInfo(Activity activity) {
        String text = Labels.getString("Common.Date") + ": ";
        if (activity.isUnplanned()) {
            text += "U [";
        }
        text += DateUtil.getFormatedDate(activity.getDate());
        if (activity.isUnplanned()) {
            text += "]";
        }
        text += " " + DateUtil.getFormatedTime(activity.getDate());
        text += "\n" + Labels.getString("Common.Title") + ": " + activity.getName()
                + "\n" + Labels.getString("Common.Estimated pomodoros") + ": " + activity.getEstimatedPoms();
        if (activity.getOverestimatedPoms() > 0) {
            text += " + " + activity.getOverestimatedPoms();
        }
        text += "\n" + Labels.getString("ReportListPanel.Real Pomodoros") + ": " + activity.getActualPoms()
                + "\n" + Labels.getString("ReportListPanel.Diff I") + ": " + ( activity.getActualPoms() - activity.getEstimatedPoms() )
                + "\n" + Labels.getString("ReportListPanel.Diff II") + ": " + ( activity.getOverestimatedPoms() > 0 ? activity.getActualPoms() - activity.getEstimatedPoms() - activity.getOverestimatedPoms() : "" )
                + "\n" + Labels.getString("ReportListPanel.Internal Interruptions") + ": " + activity.getNumInternalInterruptions()
                + "\n" + Labels.getString("ReportListPanel.External Interruptions") + ": " + activity.getNumInterruptions()
                + "\n" + Labels.getString("Common.Type") + ": " + activity.getType()
                + "\n" + Labels.getString("Common.Author") + ": " + activity.getAuthor()
                + "\n" + Labels.getString("Common.Place") + ": " + activity.getPlace()
                + "\n" + Labels.getString("Common.Description") + ": " + activity.getDescription();
        informationArea.setText(text);
    }

    @Override
    public void clearInfo() {
        informationArea.setText("");
    }
}