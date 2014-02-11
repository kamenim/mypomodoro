package org.mypomodoro.gui.activities;

import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * Activity information panel
 *
 * @author Phil Karoo
 */
public class ActivityInformationPanel extends JPanel implements ActivityInformation {

    protected final JTextArea informationArea = new JTextArea();
    protected final ArrayList<String> textArray = new ArrayList();
    protected final StringBuffer info = new StringBuffer();

    @Override
    public void selectInfo(Activity activity) {
        textArray.add(Labels.getString("Common.Date") + ": "
                + (activity.isUnplanned() ? "U [" : "")
                + DateUtil.getFormatedDate(activity.getDate())
                + (activity.isUnplanned() ? "]" : ""));
        //textArray.add(" " + DateUtil.getFormatedTime(activity.getDate()));
        textArray.add("\n" + Labels.getString("Common.Title") + ": " + activity.getName()
                + "\n" + Labels.getString("Common.Estimated pomodoros") + ": " + activity.getEstimatedPoms()
                + (activity.getOverestimatedPoms() > 0 ? " + " + activity.getOverestimatedPoms() : ""));
        textArray.add("\n" + Labels.getString("ReportListPanel.Real Pomodoros") + ": " + activity.getActualPoms());
        textArray.add("\n" + Labels.getString("ReportListPanel.Diff I") + ": " + (activity.getActualPoms() - activity.getEstimatedPoms()));
        textArray.add("\n" + Labels.getString("ReportListPanel.Diff II") + ": " + (activity.getOverestimatedPoms() > 0 ? activity.getActualPoms() - activity.getEstimatedPoms() - activity.getOverestimatedPoms() : ""));
        textArray.add("\n" + Labels.getString("ReportListPanel.Internal Interruptions") + ": " + activity.getNumInternalInterruptions());
        textArray.add("\n" + Labels.getString("ReportListPanel.External Interruptions") + ": " + activity.getNumInterruptions());
        textArray.add("\n" + Labels.getString("Common.Type") + ": " + (activity.getType().isEmpty() ? "-" : activity.getType()));
        textArray.add("\n" + Labels.getString("Common.Author") + ": " + (activity.getAuthor().isEmpty() ? "-" : activity.getAuthor()));
        textArray.add("\n" + Labels.getString("Common.Place") + ": " + (activity.getPlace().isEmpty() ? "-" : activity.getPlace()));
        textArray.add("\n" + Labels.getString("Common.Description") + ": " + (activity.getDescription().isEmpty() ? "-" : activity.getDescription()));
    }

    @Override
    public void showInfo() {
        for (String line : textArray) {
            info.append(line);
        }
        informationArea.setText(info.toString());
        // disable auto scrolling
        informationArea.setCaretPosition(0);
    }

    @Override
    public void clearInfo() {
        informationArea.setText("");
    }
}
