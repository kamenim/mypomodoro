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
package org.mypomodoro.gui.create;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;
import org.mypomodoro.gui.create.list.AuthorComboBox;
import org.mypomodoro.gui.create.list.PlaceComboBox;
import org.mypomodoro.gui.create.list.TypeComboBox;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DatePicker;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;
import static org.mypomodoro.util.TimeConverter.calculateEffectiveMinutes;
import static org.mypomodoro.util.TimeConverter.calculatePlainMinutes;

public class ActivityInputForm extends JPanel {

    protected static final Dimension TEXT_AREA_DIMENSION = new Dimension(300, 50);
    protected static final Dimension TEXT_FIELD_DIMENSION = new Dimension(300, 25);
    protected static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 25);
    protected final GridBagConstraints c = new GridBagConstraints();
    protected final JTextField nameField = new JTextField();
    protected final JTextArea descriptionField = new JTextArea();
    protected JComboBox estimatedPomodoros = new JComboBox();
    protected JComboBox storyPoints = new JComboBox();
    protected JComboBox iterations = new JComboBox();
    protected JComboBox types = new TypeComboBox();
    protected JComboBox authors = new AuthorComboBox();
    protected JComboBox places = new PlaceComboBox();
    protected final DatePicker datePicker = new DatePicker(Labels.getLocale());
    protected int activityId = -1;
    protected final JLabel estimatedLengthLabel = new JLabel("", JLabel.LEFT);

    public ActivityInputForm() {
        this(0);
    }

    public ActivityInputForm(int gridy) {
        setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        addForm(gridy);
    }

    protected void addForm(int gridy) {
        addDate(gridy);
        addName(++gridy);
        addType(++gridy);
        addEstimatedPoms(++gridy);
        if (PreferencesPanel.preferences.getAgileMode()) {
            addStoryPoints(++gridy);
            addIterations(++gridy);
        }
        addAuthor(++gridy);
        addPlace(++gridy);
        addDescription(++gridy);
    }

    protected void addDate(int gridy) {
        final FormLabel dateLabel = new FormLabel(
                Labels.getString("Common.Date scheduled") + "*: ");
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(dateLabel, c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        datePicker.setTodayWithLowerBounds();
        add(datePicker, c);
        if (PreferencesPanel.preferences.getAgileMode()) {
            dateLabel.setVisible(false);
            datePicker.setVisible(false);
        }
    }

    protected void addName(int gridy) {
        // Name Label and Text Field
        ++gridy;
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("Common.Title") + "*: "), c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        addTextField(nameField);
    }

    protected void addType(int gridy) {
        // Type Label and Combo box        
        ++gridy;
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("Common.Type") + ": "), c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        types.setBackground(ColorUtil.WHITE);
        types.setMinimumSize(COMBO_BOX_DIMENSION);
        types.setPreferredSize(COMBO_BOX_DIMENSION);
        types.setEditable(true);
        types.setFont(getFont().deriveFont(Font.PLAIN));
        // Autocompletion
        AutoCompleteDecorator.decorate(types);
        add(types, c);
    }

    protected void addEstimatedPoms(int gridy) {
        // init estimated Pomodoros combo box
        Integer[] items = new Integer[PreferencesPanel.preferences.getMaxNbPomPerActivity() + 1];
        for (int i = 0; i <= PreferencesPanel.preferences.getMaxNbPomPerActivity(); i++) {
            items[i] = i;
        }
        estimatedPomodoros = new JComboBox(items);
        estimatedPomodoros.setRenderer(new AbstractComboBoxRenderer());
        displayLength(0);
        // Estimated Poms Description and TextField
        ++gridy;
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("Common.Estimated pomodoros") + ": "), c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        estimatedPomodoros.setBackground(ColorUtil.WHITE);
        estimatedPomodoros.setMinimumSize(new Dimension(40, 25));
        estimatedPomodoros.setMaximumSize(new Dimension(40, 25));
        estimatedPomodoros.setPreferredSize(new Dimension(40, 25));
        estimatedPomodoros.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                int estimated = (Integer) estimatedPomodoros.getSelectedItem();
                displayLength(estimated);
            }
        });
        JPanel estimatedPanel = new JPanel();
        estimatedPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(3, 3, 3, 3); // white space between components        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        estimatedPanel.add(estimatedPomodoros, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        estimatedPanel.add(estimatedLengthLabel, gbc);
        add(estimatedPanel, c);
    }

    protected void addStoryPoints(int gridy) {
        // init story points combo box
        Float[] points = new Float[]{0f, 0.5f, 1f, 2f, 3f, 5f, 8f, 13f, 20f, 40f, 100f};
        storyPoints = new JComboBox(points);
        storyPoints.setRenderer(new StoryPointsComboBoxRenderer());
        ++gridy;
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("Agile.Common.Story Points") + ": "), c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        storyPoints.setBackground(ColorUtil.WHITE);
        add(storyPoints, c);
    }

    protected void addIterations(int gridy) {
        // init iterations combo box
        Integer[] its = new Integer[102];
        for (int i = 0; i <= 101; i++) {
            its[i] = i - 1;
        }
        iterations = new JComboBox(its);
        iterations.setRenderer(new IterationComboBoxRenderer());
        ++gridy;
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("Agile.Common.Iteration") + ": "), c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        iterations.setBackground(ColorUtil.WHITE);
        add(iterations, c);
    }

    class StoryPointsComboBoxRenderer extends AbstractComboBoxRenderer {

        public StoryPointsComboBoxRenderer() {
            super();
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text;
            if (value.toString().equals("0.5")) {
                text = "1/2";
            } else {
                text = Math.round((Float) value) + "";
            }
            setText(text);
            return this;
        }
    }

    class IterationComboBoxRenderer extends AbstractComboBoxRenderer {

        public IterationComboBoxRenderer() {
            super();
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text = value.toString();
            if (value.toString().equals("-1")) {
                text = " ";
            }
            setText(text);
            return this;
        }
    }

    protected void addAuthor(int gridy) {
        // Author Label and Combo box
        ++gridy;
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("Common.Author") + ": "), c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        authors.setBackground(ColorUtil.WHITE);
        authors.setMinimumSize(COMBO_BOX_DIMENSION);
        authors.setPreferredSize(COMBO_BOX_DIMENSION);
        authors.setEditable(true);
        authors.setFont(getFont().deriveFont(Font.PLAIN));
        // Autocompletion
        AutoCompleteDecorator.decorate(authors);
        add(authors, c);
    }

    protected void addPlace(int gridy) {
        // Place label and Combo box
        ++gridy;
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("Common.Place") + ": "), c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        places.setBackground(ColorUtil.WHITE);
        places.setMinimumSize(COMBO_BOX_DIMENSION);
        places.setPreferredSize(COMBO_BOX_DIMENSION);
        places.setEditable(true);
        places.setFont(getFont().deriveFont(Font.PLAIN));
        // Autocompletion
        AutoCompleteDecorator.decorate(places);
        add(places, c);
    }

    protected void addDescription(int gridy) {
        // Description Label and TextArea
        ++gridy;
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("Common.Description") + ": "), c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setMargin(new Insets(3, 3, 3, 3)); // margin
        JScrollPane description = new JScrollPane(descriptionField);
        description.setMinimumSize(TEXT_AREA_DIMENSION);
        description.setPreferredSize(TEXT_AREA_DIMENSION);
        add(description, c);
    }

    protected void addTextField(JTextField field) {
        field.setMinimumSize(TEXT_FIELD_DIMENSION);
        field.setPreferredSize(TEXT_FIELD_DIMENSION);
        add(field, c);
    }

    /**
     * Returns a new activity from the class fields and null if there was an
     * error while parsing the fields
     *
     * @return activity
     */
    public Activity getActivityFromFields() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String type = (String) types.getSelectedItem();
        type = type != null ? type.trim() : "";
        String author = (String) authors.getSelectedItem();
        author = author != null ? author.trim() : "";
        String place = (String) places.getSelectedItem();
        place = place != null ? place.trim() : "";
        int estimatedPoms = (Integer) estimatedPomodoros.getSelectedItem();
        Date dateActivity = datePicker.getDate();
        Activity activity = new Activity(place, author, name, description, type,
                estimatedPoms, dateActivity, activityId);
        if (PreferencesPanel.preferences.getAgileMode()) {
            float storypoint = (Float) storyPoints.getSelectedItem();
            int iteration = (Integer) iterations.getSelectedItem();
            activity = new Activity(place, author, name, description, type,
                    estimatedPoms, storypoint, iteration, dateActivity, activityId);
        }
        return activity;
    }

    /*
     * Getters
     */
    public JTextField getNameField() {
        return nameField;
    }

    public JTextArea getDescriptionField() {
        return descriptionField;
    }

    /*
     * Setters
     */
    public void setNameField(String value) {
        nameField.setText(value);
    }

    public void setDescriptionField(String value) {
        descriptionField.setText(value);
        // disable auto scrolling
        descriptionField.setCaretPosition(0);
    }

    public void setTypeField(String type) {
        types.setSelectedItem(type);
    }

    public void setType(int index) {
        types.setSelectedIndex(index);
    }

    public void setAuthorField(String author) {
        authors.setSelectedItem(author);
    }

    public void setAuthor(int index) {
        authors.setSelectedIndex(index);
    }

    public void setPlaceField(String place) {
        places.setSelectedItem(place);
    }

    public void setPlace(int index) {
        places.setSelectedIndex(index);
    }

    public void setEstimatedPomodoro(int index) {
        estimatedPomodoros.setSelectedIndex(index);
    }

    public void setStoryPoints(int index) {
        storyPoints.setSelectedIndex(index);
    }

    public void setIterations(int index) {
        iterations.setSelectedIndex(index);
    }

    public void setDate(Date value) {
        datePicker.setDate(value);
        datePicker.setDateWithLowerBounds(value);
    }

    public void setActivityId(int value) {
        activityId = value;
    }

    public boolean isDateToday() {
        return DateUtil.isDateToday(datePicker.getDate());
    }

    protected void displayLength(int estimatedPomodoros) {
        String effectiveHours = TimeConverter.convertMinutesToString(calculateEffectiveMinutes(estimatedPomodoros));
        String plainHours = TimeConverter.convertMinutesToString(calculatePlainMinutes(estimatedPomodoros));
        if (PreferencesPanel.preferences.getPlainHours()) {
            estimatedLengthLabel.setText(" " + plainHours + " (" + Labels.getString("Common.Plain hours") + ")");
        } else {
            estimatedLengthLabel.setText(" " + effectiveHours + " (" + Labels.getString("Common.Effective hours") + ")");
        }
    }
}
