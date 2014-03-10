package org.mypomodoro.gui.create;

import java.awt.Component;
import org.mypomodoro.gui.create.list.TypeComboBox;
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
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.jdesktop.swingx.JXDatePicker;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.create.list.AuthorComboBox;
import org.mypomodoro.gui.create.list.PlaceComboBox;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import static org.mypomodoro.util.TimeConverter.calculateEffectiveHours;
import static org.mypomodoro.util.TimeConverter.calculatePlainHours;
import static org.mypomodoro.util.TimeConverter.convertToTime;

public class ActivityInputForm extends JPanel {

    private static final long serialVersionUID = 20110814L;
    protected static final Dimension PANEL_DIMENSION = new Dimension(400, 200);
    protected static final Dimension TEXT_AREA_DIMENSION = new Dimension(300, 50);
    protected static final Dimension TEXT_FIELD_DIMENSION = new Dimension(300, 25);
    protected static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 25);
    protected final GridBagConstraints c = new GridBagConstraints();
    protected final JTextField nameField = new JTextField();
    protected final JTextArea descriptionField = new JTextArea();
    protected JComboBox estimatedPomodoros = new JComboBox();
    protected JComboBox storyPoints = new JComboBox();
    protected JComboBox iterations = new JComboBox();
    protected JComboBox types = new JComboBox();
    protected JComboBox authors = new JComboBox();
    protected JComboBox places = new JComboBox();
    protected final JXDatePicker datePicker = new JXDatePicker(
            Labels.getLocale());
    protected int activityId = -1;
    protected final JLabel estimatedLengthLabel = new JLabel("", JLabel.LEFT);

    public ActivityInputForm() {
        this(0);
    }

    public ActivityInputForm(int gridy) {
        setBorder(new TitledBorder(new EtchedBorder(), ""));
        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
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
        if (ControlPanel.preferences.getAgileMode()) {
            addStoryPoints(++gridy);
            addIterations(++gridy);
        }
        addAuthor(++gridy);
        addPlace(++gridy);
        addDescription(++gridy);
    }

    protected void addDate(int gridy) {
        final FormLabel dateLabel = new FormLabel(
                Labels.getString("Common.Date") + "*: ");
        datePicker.setDate(new Date());
        c.gridx = 0;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(dateLabel, c);
        c.gridx = 1;
        c.gridy = gridy;
        c.weighty = 0.5;
        add(datePicker, c);
        if (ControlPanel.preferences.getAgileMode()) {
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
        types = new TypeComboBox();
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
        types.setFont(new Font(types.getFont().getName(), Font.PLAIN, types.getFont().getSize()));
        add(types, c);
    }

    protected void addEstimatedPoms(int gridy) {
        // init estimated Pomodoros combo box
        Integer[] items = new Integer[ControlPanel.preferences.getMaxNbPomPerActivity() + 1];
        for (int i = 0; i <= ControlPanel.preferences.getMaxNbPomPerActivity(); i++) {
            items[i] = i;
        }
        estimatedPomodoros = new JComboBox(items);
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

    class StoryPointsComboBoxRenderer extends BasicComboBoxRenderer {

        public StoryPointsComboBoxRenderer() {
            super();
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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

    protected void addIterations(int gridy) {
        // init iterations combo box
        Integer[] its = new Integer[102];
        for (int i = 0; i <= 101; i++) {
            its[i] = i - 1;
        }
        iterations = new JComboBox(its);
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

    protected void addAuthor(int gridy) {
        authors = new AuthorComboBox();
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
        authors.setFont(new Font(authors.getFont().getName(), Font.PLAIN, authors.getFont().getSize()));
        add(authors, c);
    }

    protected void addPlace(int gridy) {
        places = new PlaceComboBox();
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
        places.setFont(new Font(places.getFont().getName(), Font.PLAIN, places.getFont().getSize()));
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
        descriptionField.setFont(this.getFont());
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setMargin(new Insets(3, 3, 3, 3)); // margin
        JScrollPane description = new JScrollPane(descriptionField);
        description.setMinimumSize(TEXT_AREA_DIMENSION);
        description.setPreferredSize(TEXT_AREA_DIMENSION);
        description.setFont(getFont());
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
        if (ControlPanel.preferences.getAgileMode()) {
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
    }

    public void setActivityId(int value) {
        activityId = value;
    }

    public boolean isDateToday() {
        String datePickerFormat = DateUtil.getFormatedDate(datePicker.getDate());
        String todayFormat = DateUtil.getFormatedDate(new Date());
        return datePickerFormat.equalsIgnoreCase(todayFormat);
    }

    protected void displayLength(int estimatedPomodoros) {
        String effectiveHours = convertToTime(calculateEffectiveHours(estimatedPomodoros));
        String plainHours = convertToTime(calculatePlainHours(estimatedPomodoros));
        estimatedLengthLabel.setText(effectiveHours + " (" + Labels.getString("Common.Effective hours") + ") / " + plainHours + " (" + Labels.getString("Common.Plain hours") + ")");
    }
}
