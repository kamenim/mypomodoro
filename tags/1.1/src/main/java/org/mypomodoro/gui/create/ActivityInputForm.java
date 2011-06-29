package org.mypomodoro.gui.create;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComboBox;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.jdesktop.swingx.JXDatePicker;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.model.Activity;

public class ActivityInputForm extends JPanel {

    private static final Dimension PANEL_DIMENSION = new Dimension(400, 200);
    private static final Dimension TEXT_AREA_DIMENSION = new Dimension(300, 50);
    private static final Dimension TEXT_FIELD_DIMENSION = new Dimension(300, 25);
    protected final JTextField placeField = new JTextField();
    protected final JTextField authorField = new JTextField();
    protected final JTextField nameField = new JTextField();
    protected final JTextArea descriptionField = new JTextArea();
    protected final JTextField typeField = new JTextField();
    protected JComboBox estimatedPomodoros = new JComboBox();
    protected final JXDatePicker datePicker = new JXDatePicker();
    private int activityId = -1;

    public ActivityInputForm() {
        setBorder(new TitledBorder(new EtchedBorder(), ""));
        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        final FormLabel dateLabel = new FormLabel("Date*: ");
        datePicker.setDate(new Date());
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        add(dateLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.5;
        add(datePicker, c);

        // Name Label and Text Field
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.5;
        add(new FormLabel("Title*: "), c);
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0.5;
        addTextField(nameField, c);

        // Estimated Poms Description and TextField
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.5;
        add(new FormLabel("Estimated Pomodoros*:"), c);
        c.gridx = 1;
        c.gridy = 2;
        c.weighty = 0.5;
        String items[] = new String[ControlPanel.preferences.getMaxNbPomPerActivity()];
        for (int i = 0; i < ControlPanel.preferences.getMaxNbPomPerActivity(); i++) {
            items[i] = ( i + 1 ) + "";
        }
        estimatedPomodoros = new JComboBox(items);
        add(estimatedPomodoros, c);

        // Type Label and TextField
        c.gridx = 0;
        c.gridy = 3;
        c.weighty = 0.5;
        add(new FormLabel("Type:"), c);
        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 0.5;
        addTextField(typeField, c);

        // Author Label and TextField
        c.gridx = 0;
        c.gridy = 4;
        c.weighty = 0.5;
        add(new FormLabel("Author: "), c);
        c.gridx = 1;
        c.gridy = 4;
        c.weighty = 0.5;
        addTextField(authorField, c);

        // Place label and TextField
        c.gridx = 0;
        c.gridy = 5;
        c.weighty = 0.5;
        c.weightx = 0.0;
        add(new FormLabel("Place: "), c);
        c.gridx = 1;
        c.gridy = 5;
        c.weighty = 0.5;
        placeField.setMinimumSize(TEXT_FIELD_DIMENSION);
        placeField.setPreferredSize(TEXT_FIELD_DIMENSION);
        add(placeField, c);

        // Description Label and TextArea
        c.gridx = 0;
        c.gridy = 6;
        c.weighty = 0.5;
        add(new FormLabel("Description:"), c);
        c.gridx = 1;
        c.gridy = 6;
        c.weighty = 0.5;
        descriptionField.setFont(this.getFont());
        JScrollPane description = new JScrollPane(descriptionField);
        description.setMinimumSize(TEXT_AREA_DIMENSION);
        description.setPreferredSize(TEXT_AREA_DIMENSION);
        description.setFont(getFont());
        add(description, c);
    }

    private void addTextField(JTextField field, GridBagConstraints contraints) {
        field.setMinimumSize(TEXT_FIELD_DIMENSION);
        field.setPreferredSize(TEXT_FIELD_DIMENSION);
        add(field, contraints);
    }

    /**
     * Returns a new activity from the class fields and null if there was an
     * error while parsing the fields
     * 
     * @return activity
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
                estimatedPoms, dateActivity, activityId);
    }

    public void clearForm() {
        placeField.setText("");
        authorField.setText("");
        nameField.setText("");
        descriptionField.setText("");
        typeField.setText("");
        estimatedPomodoros.setSelectedIndex(0);
        //datePicker = new JXDatePicker();
        //datePicker.setDate(new Date());
    }

    /*
     * Setters
     */
    public void setPlaceField(String value) {
        placeField.setText(value);
    }

    public void setAuthorField(String value) {
        authorField.setText(value);
    }

    public void setNameField(String value) {
        nameField.setText(value);
    }

    public void setDescriptionField(String value) {
        descriptionField.setText(value);
    }

    public void setTypeField(String value) {
        typeField.setText(value);
    }

    public void setEstimatedPomodoros(int value) {
        value = value > ControlPanel.preferences.getMaxNbPomPerActivity() ? ControlPanel.preferences.getMaxNbPomPerActivity() : value;
        estimatedPomodoros.setSelectedIndex(value - 1);
    }

    public void setDate(Date dateActivity) {
        datePicker.setDate(dateActivity);
    }

    public void setActivityId(int value) {
        activityId = value;
    }

    public boolean isDateToday() {
        String datePickerFormat = new SimpleDateFormat("dd/MM/yyyy").format(datePicker.getDate());
        String todayFormat = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        return datePickerFormat.equalsIgnoreCase(todayFormat);
    }
}