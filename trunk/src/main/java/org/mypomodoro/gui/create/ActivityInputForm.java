package org.mypomodoro.gui.create;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.model.Activity;

public class ActivityInputForm extends JPanel {
	private static final Dimension PANEL_DIMENSION = new Dimension(400, 200);

	private static final Dimension TEXT_AREA_DIMENSION = new Dimension(300, 50);

	private static final Dimension TEXT_FIELD_DIMENSION = new Dimension(300, 25);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final JTextField placeField = new JTextField();
	protected final JTextField authorField = new JTextField();
	protected final JTextField nameField = new JTextField();
	protected final JTextArea descriptionField = new JTextArea();
	protected final JTextField typeField = new JTextField();
	protected final JTextField estimatedPomodoros = new JTextField();

	public ActivityInputForm() {
		setBorder(new EtchedBorder());
		setMinimumSize(PANEL_DIMENSION);
		setPreferredSize(PANEL_DIMENSION);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;

		// Place label and TextField
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.5;
		c.weightx = 0.0;

		add(new FormLabel("Place: "), c);
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.5;
		placeField.setMinimumSize(TEXT_FIELD_DIMENSION);
		placeField.setPreferredSize(TEXT_FIELD_DIMENSION);
		add(placeField, c);

		// Author Label and TextField
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.5;
		add(new FormLabel("Author: "), c);

		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 0.5;
		addTextField(authorField, c);

		// Name Label and Text Field
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 0.5;
		add(new FormLabel("Name: "), c);
		c.gridx = 1;
		c.gridy = 2;
		c.weighty = 0.5;
		addTextField(nameField, c);

		// Description Label and TextArea
		c.gridx = 0;
		c.gridy = 3;
		c.weighty = 0.5;
		add(new FormLabel("Description:"), c);
		c.gridx = 1;
		c.gridy = 3;
		c.weighty = 0.5;
		JScrollPane description = new JScrollPane(descriptionField);
		description.setMinimumSize(TEXT_AREA_DIMENSION);
		description.setPreferredSize(TEXT_AREA_DIMENSION);
		add(description, c);

		// Type Label and TextField
		c.gridx = 0;
		c.gridy = 4;
		c.weighty = 0.5;
		add(new FormLabel("Type:"), c);
		c.gridx = 1;
		c.gridy = 4;
		c.weighty = 0.5;
		addTextField(typeField, c);

		// Estimated Poms Description and TextField
		c.gridx = 0;
		c.gridy = 5;
		c.weighty = 0.5;
		add(new FormLabel("Estimated Pomodoros:"), c);
		c.gridx = 1;
		c.gridy = 5;
		c.weighty = 0.5;
		addTextField(estimatedPomodoros, c);
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
	 * @return
	 */
	public Activity getActivityFromFields() {
		String place = placeField.getText();
		String author = authorField.getText();
		String name = nameField.getText();
		String description = descriptionField.getText();
		String type = typeField.getText();
		int estimatedPoms = parseEstimatedPomodoros();
		
		return new Activity(place, author, name, description, type,
				estimatedPoms);

	}

	private int parseEstimatedPomodoros() {
		try {
			return (!estimatedPomodoros.getText().equals("")) ? Integer
					.parseInt(estimatedPomodoros.getText()) : 0;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Please provide a valid number of pomodoros!");
			return 0;
		}
	}

	public void clearForm() {
		placeField.setText("");
		authorField.setText("");
		nameField.setText("");
		descriptionField.setText("");
		typeField.setText("");
		estimatedPomodoros.setText("");
	}
}