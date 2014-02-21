package org.mypomodoro.gui.reports;

import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.model.Activity;

public class ReportInputForm extends ActivityInputForm {

    private static final long serialVersionUID = 20110814L;

    public ReportInputForm() {
    }

    @Override
    protected void addForm(int gridy) {
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

        String author = (String) authors.getSelectedItem();
        author = author != null ? author.trim() : "";
        report.setAuthor(author);
        String place = (String) places.getSelectedItem();
        place = place != null ? place.trim() : "";
        report.setPlace(place);
        report.setDescription(descriptionField.getText().trim());

        return report;
    }
}
