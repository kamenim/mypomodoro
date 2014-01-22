package org.mypomodoro.gui.reports;

import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.model.Activity;

public class ReportInputForm extends ActivityInputForm {

    private static final long serialVersionUID = 20110814L;

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
     * Returns an updated report from the class fields
     *
     * @return report
     */
    @Override
    public Activity getActivityFromFields() {
        Activity report = Activity.getActivity(activityId);

        report.setName(nameField.getText().trim());
        report.setDescription(descriptionField.getText().trim());
        String type = (String) types.getSelectedItem();
        type = type != null ? type.trim() : "";
        report.setType(type);
        String author = (String) authors.getSelectedItem();
        author = author != null ? author.trim() : "";
        report.setAuthor(author);
        String place = (String) places.getSelectedItem();
        place = place != null ? place.trim() : "";
        report.setPlace(place);

        return report;
    }
}
