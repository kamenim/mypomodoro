package org.mypomodoro.gui.create;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mypomodoro.buttons.SaveButton;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;

/**
 * GUI for creating a new Activity and store to data layer.
 * 
 * @author Brian Wetzel
 */
public class CreatePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final ActivityInputForm inputFormPanel = new ActivityInputForm();
	protected final JLabel validation = new JLabel("");
	protected final SaveButton saveButton = new SaveButton(this);

	public CreatePanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.80;
		c.fill = GridBagConstraints.BOTH;
		add(inputFormPanel, c);
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.weighty = 0.2;
		add(saveButton, c);
		validation.setForeground(Color.red);
		c.gridy = 2;
		add(validation, c);
	}

	protected void validActivityAction(Activity newActivity) {
		ActivityList.getList().add(newActivity);
		newActivity.databaseInsert();
		validation.setForeground(Color.green);
		validation.setText("Activity Added.");
		inputFormPanel.clearForm();
	}

	public final void saveActivity(Activity newActivity) {
		if (newActivity.isValid()) {
			validActivityAction(newActivity);
		} else {
			invalidActivityAction();
		}
	}

	protected void invalidActivityAction() {
		validation.setForeground(Color.red);
		validation.setText("Invalid Input.");
	}

	public ActivityInputForm getFormPanel() {
		return inputFormPanel;
	}

}