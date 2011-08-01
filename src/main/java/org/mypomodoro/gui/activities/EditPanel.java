package org.mypomodoro.gui.activities;

import org.mypomodoro.gui.create.*;
import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * GUI for editing an existing activity and store to data layer.
 *
 * @author Phil Karoo
 */
public class EditPanel extends CreatePanel {

    protected EditInputForm editInputForm;

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
        editInputForm = new EditInputForm();
        add(editInputForm, gbc);
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        //gbc.fill = GridBagConstraints.NONE;
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
        currentActivity.databaseUpdate();
        ActivityList.getList().update();
        JFrame window = new JFrame();
        String title = Labels.getString("ActivityListPanel.Edit activity");
        String message = Labels.getString("ActivityListPanel.Activity updated");
        JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
    }

    @Override
    public void saveActivity(Activity activity) {
        if (ActivityList.getList().size() > 0) {
            // no check for existing activities with same name and date
            if (!activity.isValid()) {
                invalidActivityAction();
            } else {
                validActivityAction(activity);
            }
        }
    }

    @Override
    protected void invalidActivityAction() {
        JFrame window = new JFrame();
        String title = Labels.getString("Common.Error");
        String message = Labels.getString("Common.Title is mandatory");
        JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return editInputForm;
    }

    @Override
    public void fillOutInputForm(Activity activity) {
        editInputForm.setPlaceField(activity.getPlace());
        editInputForm.setAuthorField(activity.getAuthor());
        editInputForm.setNameField(activity.getName());
        editInputForm.setDescriptionField(activity.getDescription());
        editInputForm.setTypeField(activity.getType());
        editInputForm.setEstimatedPomodoros(activity.getEstimatedPoms());
        editInputForm.setDate(activity.getDate());
        editInputForm.setActivityId(activity.getId());
    }
}