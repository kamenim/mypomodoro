package org.mypomodoro.gui.create;

import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * GUI for editing a new Activity and store to data layer.
 *
 * @author Phil Karoo
 */
public class EditPanel extends CreatePanel {

    public EditPanel() {
    }

    @Override
    protected void addInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(inputFormPanel, gbc);
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        //gbc.fill = GridBagConstraints.NONE;
        //Border thickBorder = new LineBorder(Color.BLACK,1);
        //saveButton.setBorder(thickBorder);
        add(saveButton, gbc);
    }

    @Override
    protected void addClearButton() {
    }

    @Override
    protected void addValidation() {
    }

    @Override
    protected void validActivityAction(Activity currentActivity) {
        if (ActivityList.getList().size() > 0) {
            currentActivity.databaseUpdate();
            ActivityList.getList().update();
            JFrame window = new JFrame();
            String title = Labels.getString("ActivityListPanel.Edit activity");
            String message = Labels.getString("ActivityListPanel.Activity updated");
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }

    @Override
    public void saveActivity(Activity newActivity) {
        // no check for existing activities with same name and date
        if (!newActivity.isValid()) {
            invalidActivityAction();
        } else {
            validActivityAction(newActivity);
        }
    }

    @Override
    protected void invalidActivityAction() {
        if (ActivityList.getList().size() > 0) {
            JFrame window = new JFrame();
            String title = Labels.getString("Common.Error");
            String message = Labels.getString("Common.Title is mandatory");
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }

    @Override
    public void fillOutInputForm(Activity activity) {
        inputFormPanel.setPlaceField(activity.getPlace());
        inputFormPanel.setAuthorField(activity.getAuthor());
        inputFormPanel.setNameField(activity.getName());
        inputFormPanel.setDescriptionField(activity.getDescription());
        inputFormPanel.setTypeField(activity.getType());
        inputFormPanel.setEstimatedPomodoros(activity.getEstimatedPoms());
        inputFormPanel.setActivityId(activity.getId());
        inputFormPanel.setDate(activity.getDate());
    }
}