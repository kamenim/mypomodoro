/* 
 * Copyright (C) 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.gui.activities;

import java.awt.Color;
import java.awt.Insets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Activity information panel
 *
 */
public class ActivityInformationPanel extends JPanel implements ActivityInformation {

    protected final JTextArea informationArea = new JTextArea();
    protected LinkedHashMap<String, String> textMap = new LinkedHashMap<String, String>();

    public ActivityInformationPanel() {
        informationArea.setMargin(new Insets(3, 3, 3, 3)); // margin
    }

    @Override
    public void selectInfo(Activity activity) {
        textMap = new LinkedHashMap<String, String>();
        textMap.put("date", (PreferencesPanel.preferences.getAgileMode() ? Labels.getString("Common.Date created") : Labels.getString("Common.Date scheduled")) + ": "
                + (activity.isUnplanned() ? "U [" : "")
                + DateUtil.getFormatedDate(activity.getDate(), "EEE, dd MMM yyyy")
                + (activity.isUnplanned() ? "]" : ""));
        textMap.put("date_completed", Labels.getString("Common.Date completed") + ": "
                + (activity.isUnplanned() ? "U [" : "")
                + DateUtil.getFormatedDate(activity.getDateCompleted(), "EEE, dd MMM yyyy") + ", " + DateUtil.getFormatedTime(activity.getDateCompleted())
                + (activity.isUnplanned() ? "]" : ""));
        // Date reopened
        textMap.put("date_reopened", Labels.getString("Common.Date reopened") + ": "
                + (activity.isUnplanned() ? "U [" : "")
                + DateUtil.getFormatedDate(activity.getDateCompleted(), "EEE, dd MMM yyyy") + ", " + DateUtil.getFormatedTime(activity.getDateCompleted())
                + (activity.isUnplanned() ? "]" : ""));
        textMap.put("title", Labels.getString("Common.Title") + ": " + activity.getName());
        textMap.put("type", Labels.getString("Common.Type") + ": " + (activity.getType().isEmpty() ? "-" : activity.getType()));
        textMap.put("estimated", Labels.getString("Common.Estimated pomodoros") + ": "
                + activity.getActualPoms() + " / "
                + activity.getEstimatedPoms()
                + (activity.getOverestimatedPoms() > 0 ? " + " + activity.getOverestimatedPoms() : "")
                + " (" + TimeConverter.getLength(activity.getActualPoms()) + " / " + TimeConverter.getLength(activity.getEstimatedPoms() + activity.getOverestimatedPoms()) + ")");
        if (PreferencesPanel.preferences.getAgileMode()) {
            textMap.put("storypoints", Labels.getString("Agile.Common.Story Points") + ": " + displayStoryPoint(activity.getStoryPoints()));
            textMap.put("iteration", Labels.getString("Agile.Common.Iteration") + ": " + (activity.getIteration() == -1 ? "-" : activity.getIteration()));
        }
        textMap.put("author", Labels.getString("Common.Author") + ": " + (activity.getAuthor().isEmpty() ? "-" : activity.getAuthor()));
        textMap.put("place", Labels.getString("Common.Place") + ": " + (activity.getPlace().isEmpty() ? "-" : activity.getPlace()));
        textMap.put("description", Labels.getString("Common.Description") + ": " + (activity.getDescription().isEmpty() ? "-" : activity.getDescription()));
    }

    @Override
    public void showInfo() {
        clearInfo();
        Iterator<String> keySetIterator = textMap.keySet().iterator();
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            informationArea.append(textMap.get(key) + "\n");
        }
        // disable auto scrolling
        informationArea.setCaretPosition(0);
    }

    @Override
    public void showInfo(String newInfo) {
        informationArea.setText(newInfo);
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

    @Override
    public boolean isMultipleSelectionAllowed() {
        return true;
    }

    @Override
    public void setForegroundColor(Color color) {
        informationArea.setForeground(color);
    }
}
