package org.mypomodoro.gui.reports;

import java.util.Date;
import org.mypomodoro.gui.create.*;
import org.mypomodoro.model.Activity;

public class ReportInputForm extends ActivityInputForm {

    public ReportInputForm() {
    }

    @Override
    protected void addForm(int gridy) {
        addName(gridy);
        addType(++gridy);
        addAuthor(++gridy);
        addPlace(++gridy);
        addDescription(++gridy);
    }

    /**
     * Returns a new report from the class fields and null if there was an
     * error while parsing the fields
     * 
     * @return report
     */
    public Activity getActivityFromFields() {
        String place = placeField.getText().trim();
        String author = authorField.getText().trim();
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = typeField.getText().trim();
        int estimatedPoms = estimatedPomodoros.getSelectedIndex() + 1;
        Date dateActivity = datePicker.getDate();

        return new Activity(place, author, name, description, type,
                estimatedPoms, dateActivity, true, activityId);
    }
}