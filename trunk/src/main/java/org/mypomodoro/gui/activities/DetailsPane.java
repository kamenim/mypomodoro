package org.mypomodoro.gui.activities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;

public class DetailsPane extends JPanel implements ActivityInformation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JTextArea information;

	public DetailsPane(JTable table) {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = GridBagConstraints.REMAINDER;
		gbc.weightx = 0.8;
		information = new JTextArea();
		information.setEditable(false);
		add(information, gbc);

		// Add the delete button
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.2;
		add(new DeleteButton(table), gbc);
	}

	public void showInfo(Activity activity) {
		String text = "Place: " + activity.getPlace() + "\nAuthor's Name: "
				+ activity.getName() + "\nDate Created: " + activity.getDate()
				+ "\nActivity Name: " + activity.getName()
				+ "\nType of Activity: " + activity.getType()
				+ "\nDescription:" + activity.getDescription()
				+ "\nEstimated Pomodoros: " + activity.getEstimatedPoms();
		information.setText(text);
	}

	public void clearInfo() {
		information.setText("");
	}
}
