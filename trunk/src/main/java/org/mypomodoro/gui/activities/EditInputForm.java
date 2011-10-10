package org.mypomodoro.gui.activities;

import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.model.Activity;

public class EditInputForm extends ActivityInputForm {

    private static final long serialVersionUID = 20110814L;

    public EditInputForm() {
    }

    /**
     * Returns an updated activity from the class fields
     * 
     * @return activity
     */
    @Override
    public Activity getActivityFromFields() {
        Activity activity = Activity.getActivity(activityId);

        activity.setPlace(placeField.getText().trim());
        activity.setAuthor(authorField.getText().trim());
        activity.setName(nameField.getText().trim());
        activity.setDescription(descriptionField.getText().trim());
        activity.setType(((String)types.getSelectedItem()).trim());
        activity.setEstimatedPoms(estimatedPomodoros.getSelectedIndex() + 1);
        activity.setDate(datePicker.getDate());

        return activity;
    }
}