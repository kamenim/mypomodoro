package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;

/**
 * Panel that displays information on the current Pomodoro...this should be
 * updated when the ToDo list is updated.
 */
public class InformationPanel extends JPanel implements ActivityInformation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JTextArea informationArea = new JTextArea();;
	private final GridBagConstraints gbc = new GridBagConstraints();

	public InformationPanel(ToDoListPanel panel) {
		setLayout(new GridBagLayout());

		addInformationArea();
		addCompleteButton(panel);

	}

	private void addCompleteButton(final ToDoListPanel panel) {
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.1;
		gbc.fill = GridBagConstraints.NONE;
		JButton changeButton = new JButton("Task Complete");
		changeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.completeTask();
			}
		});
		add(changeButton, gbc);
	}

	private void addInformationArea() {
		// add the information area
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		informationArea.setBorder(new EtchedBorder());
		informationArea.setEditable(false);
		add(new JScrollPane(informationArea), gbc);
	}

	public void showInfo(Activity activity) {
		String text = "Name: " + activity.getName() + "\nDescription:"
				+ activity.getDescription() + "\nEstimated Pomodoros: "
				+ activity.getEstimatedPoms() + "\nActual Pomodoros: "
				+ activity.getActualPoms() + "\nInterruptions: "
				+ activity.getNumInterruptions();
		informationArea.setText(text);
	}

	public void clearInfo() {
		informationArea.setText("");
	}
}