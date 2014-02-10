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

        activity.setName(nameField.getText().trim());
        activity.setDescription(descriptionField.getText().trim());
        String type = (String) types.getSelectedItem();
        type = type != null ? type.trim() : "";
        activity.setType(type);
        String author = (String) authors.getSelectedItem();
        author = author != null ? author.trim() : "";
        activity.setAuthor(author);
        String place = (String) places.getSelectedItem();
        place = place != null ? place.trim() : "";
        activity.setPlace(place);
        activity.setEstimatedPoms(estimatedPomodoros.getSelectedIndex() + 1);
        activity.setDate(datePicker.getDate());

        return activity;
    }
}
