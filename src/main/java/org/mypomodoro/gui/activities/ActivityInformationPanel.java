package org.mypomodoro.gui.activities;

import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import static org.mypomodoro.util.TimeConverter.getLength;

/**
 * Activity information panel
 *
 */
public class ActivityInformationPanel extends JPanel implements ActivityInformation {

    protected final JTextArea informationArea = new JTextArea();
    protected LinkedHashMap<String, String> textMap = new LinkedHashMap<String, String>();
    protected StringBuilder info = new StringBuilder();

    @Override
    public void selectInfo(Activity activity) {
        textMap.put("date", Labels.getString("Common.Date") + ": "
                + (activity.isUnplanned() ? "U [" : "")
                + DateUtil.getFormatedDate(activity.getDate())
                + (activity.isUnplanned() ? "]" : "") + "\n");
        textMap.put("title", Labels.getString("Common.Title") + ": " + activity.getName() + "\n");
        textMap.put("type", Labels.getString("Common.Type") + ": " + (activity.getType().isEmpty() ? "-" : activity.getType()) + "\n");
        textMap.put("estimated", Labels.getString("Common.Estimated pomodoros") + ": " + activity.getEstimatedPoms()
                + (activity.getOverestimatedPoms() > 0 ? " + " + activity.getOverestimatedPoms() : "")
                + " (" + getLength(activity.getEstimatedPoms() + activity.getOverestimatedPoms()) + ")\n");
        //+ "(" + getLength(activity.getEstimatedPoms())
        //+ (activity.getOverestimatedPoms() > 0 ? " + " + getLength(activity.getOverestimatedPoms()) : "") + ")\n");
        if (ControlPanel.preferences.getAgileMode()) {
            textMap.put("storypoints", Labels.getString("Agile.Common.Story Points") + ": " + displayStoryPoint(activity.getStoryPoints()) + "\n");
            textMap.put("iteration", Labels.getString("Agile.Common.Iteration") + ": " + (activity.getIteration() < 0 ? "-" : activity.getIteration()) + "\n");
        }
        textMap.put("author", Labels.getString("Common.Author") + ": " + (activity.getAuthor().isEmpty() ? "-" : activity.getAuthor()) + "\n");
        textMap.put("place", Labels.getString("Common.Place") + ": " + (activity.getPlace().isEmpty() ? "-" : activity.getPlace()) + "\n");
        textMap.put("description", Labels.getString("Common.Description") + ": " + (activity.getDescription().isEmpty() ? "-" : activity.getDescription()) + "\n");
    }

    @Override
    public void showInfo() {
        info = new StringBuilder();
        Iterator<String> keySetIterator = textMap.keySet().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            info.append(textMap.get(key));
        }
        informationArea.setText(info.toString());
        // disable auto scrolling
        informationArea.setCaretPosition(0);
    }

    @Override
    public void clearInfo() {
        informationArea.setText("");
    }

    private String displayStoryPoint(float points) {
        String text;
        if (points / 0.5 == 1) {
            text = "1/2";
        } else {
            text = Math.round(points) + "";
        }
        return text;
    }
}