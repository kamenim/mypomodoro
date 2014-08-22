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

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.mypomodoro.gui.IActivityInformation;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.HtmlEditor;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Activity information panel
 *
 */
public class ActivityInformationPanel extends JPanel implements IActivityInformation {

    protected final HtmlEditor informationArea = new HtmlEditor();
    protected LinkedHashMap<String, String> textMap = new LinkedHashMap<String, String>();

    public ActivityInformationPanel() {
    }

    @Override
    public void selectInfo(Activity activity) {
        textMap = new LinkedHashMap<String, String>();
        textMap.put("date", "<b>" + (PreferencesPanel.preferences.getAgileMode() ? Labels.getString("Common.Date created") : Labels.getString("Common.Date scheduled")) + ":</b> "
                + (activity.isUnplanned() ? "U [" : "")
                + DateUtil.getFormatedDate(activity.getDate(), "EEE, dd MMM yyyy")
                + (activity.isUnplanned() ? "]" : "") + "<br>");
        textMap.put("date_completed", "<b>" + Labels.getString("Common.Date completed") + ":</b> "
                + (activity.isUnplanned() ? "U [" : "")
                + DateUtil.getFormatedDate(activity.getDateCompleted(), "EEE, dd MMM yyyy") + ", " + DateUtil.getFormatedTime(activity.getDateCompleted())
                + (activity.isUnplanned() ? "]" : "") + "<br>");
        // Date reopened
        textMap.put("date_reopened", "<b>" + Labels.getString("Common.Date reopened") + ":</b> "
                + (activity.isUnplanned() ? "U [" : "")
                + DateUtil.getFormatedDate(activity.getDateCompleted(), "EEE, dd MMM yyyy") + ", " + DateUtil.getFormatedTime(activity.getDateCompleted())
                + (activity.isUnplanned() ? "]" : "") + "<br>");
        textMap.put("title", "<b>" + Labels.getString("Common.Title") + ":</b> " + activity.getName() + "<br>");
        textMap.put("type", "<b>" + Labels.getString("Common.Type") + ":</b> " + (activity.getType().isEmpty() ? "-" : activity.getType()) + "<br>");
        textMap.put("estimated", "<b>" + Labels.getString("Common.Estimated pomodoros") + ":</b> "
                + activity.getActualPoms() + " / "
                + activity.getEstimatedPoms()
                + (activity.getOverestimatedPoms() > 0 ? " + " + activity.getOverestimatedPoms() : "")
                + " (" + TimeConverter.getLength(activity.getActualPoms()) + " / " + TimeConverter.getLength(activity.getEstimatedPoms() + activity.getOverestimatedPoms()) + ")" + "<br>");
        if (PreferencesPanel.preferences.getAgileMode()) {
            textMap.put("storypoints", "<b>" + Labels.getString("Agile.Common.Story Points") + ":</b> " + displayStoryPoint(activity.getStoryPoints()) + "<br>");
            textMap.put("iteration", "<b>" + Labels.getString("Agile.Common.Iteration") + ":</b> " + (activity.getIteration() == -1 ? "-" : activity.getIteration()) + "<br>");
        }
        textMap.put("author", "<b>" + Labels.getString("Common.Author") + ":</b> " + (activity.getAuthor().isEmpty() ? "-" : activity.getAuthor()) + "<br>");
        textMap.put("place", "<b>" + Labels.getString("Common.Place") + ":</b> " + (activity.getPlace().isEmpty() ? "-" : activity.getPlace()) + "<br>");
        textMap.put("description", "<b>" + Labels.getString("Common.Description") + ":</b> " + (activity.getDescription().isEmpty() ? "-" : activity.getDescription()) + "<br>");
    }

    @Override
    public void showInfo() {
        clearInfo();
        Iterator<String> keySetIterator = textMap.keySet().iterator();
        String text = "";
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            text += textMap.get(key);
        }
        informationArea.setText(text);
        // disable auto scrolling
        informationArea.setCaretPosition(0);
    }

    @Override
    public void showInfo(String newInfo) {
        informationArea.setText(newInfo);
        scrollToBottom();
        // disable auto scrolling
        //informationArea.setCaretPosition(0); // only for textArea        
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

    public void scrollToBottom() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            
         @Override
         public void run() {
        try {
            int endPosition = informationArea.getDocument().getLength();
            Rectangle bottom = informationArea.modelToView(endPosition);
            informationArea.scrollRectToVisible(bottom);
            System.err.println("endPosition = " + endPosition);
        } catch (BadLocationException e) {
            System.err.println("Could not scroll to " + e);
        }
        }
         });
    }

    public static void centerLineInScrollPane(JTextComponent component) {
        Container container = SwingUtilities.getAncestorOfClass(JViewport.class, component);

        if (container == null) {
            return;
        }

        try {
            Rectangle r = component.modelToView(component.getCaretPosition());
            JViewport viewport = (JViewport) container;

            int extentWidth = viewport.getExtentSize().width;
            int viewWidth = viewport.getViewSize().width;

            int x = Math.max(0, r.x - (extentWidth / 2));
            x = Math.min(x, viewWidth - extentWidth);

            int extentHeight = viewport.getExtentSize().height;
            int viewHeight = viewport.getViewSize().height;

            int y = Math.max(0, r.y - (extentHeight / 2));
            y = Math.min(y, viewHeight - extentHeight);

            viewport.setViewPosition(new Point(x, y));
        } catch (BadLocationException ignored) {
        }
    }
}