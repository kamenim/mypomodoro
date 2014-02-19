package org.mypomodoro.gui.activities;

import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.model.Activity;

public class EditInputForm extends ActivityInputForm {

    private static final long serialVersionUID = 20110814L;

    public EditInputForm() {
    }

    @Override
    protected void addForm(int gridy) {
        addDate(gridy);
        addAuthor(++gridy);
        addPlace(++gridy);
        addDescription(++gridy);
    }

    /**
     * Returns an updated activity from the class fields
     *
     * @return activity
     */
    @Override
    public Activity getActivityFromFields() {
        Activity activity = Activity.getActivity(activityId);

        activity.setDate(datePicker.getDate());
        String author = (String) authors.getSelectedItem();
        author = author != null ? author.trim() : "";
        activity.setAuthor(author);
        String place = (String) places.getSelectedItem();
        place = place != null ? place.trim() : "";
        activity.setPlace(place);
        activity.setDescription(descriptionField.getText().trim());

        return activity;
    }
}
