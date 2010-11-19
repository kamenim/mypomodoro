package org.mypomodoro.gui.reports;

import java.awt.Dimension;

import javax.swing.JTextArea;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;

public class InformationArea extends JTextArea implements ActivityInformation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Dimension d = new Dimension(450, 125);

	public InformationArea() {
		setEditable(false);
		setMinimumSize(d);
		setPreferredSize(d);
	}

	public void clearInfo() {
		setText("");
	}

	public void showInfo(Activity activity) {
		double accuracy = 1.0 * activity.getEstimatedPoms()
				/ activity.getActualPoms();
		String text = "Author's Name: " + activity.getName()
				+ "\nDate Created: " + activity.getDate() + "\nActivity Name: "
				+ activity.getName() + "\nType of Activity: "
				+ activity.getType() + "\nDescription:"
				+ activity.getDescription() + "\nEstimated Pomodoros: "
				+ activity.getEstimatedPoms() + "\nActual Pomodoros: "
				+ activity.getActualPoms() + "\nEstimation Accuracy: "
				+ String.valueOf(accuracy) + "\nThis task had "
				+ activity.getNumInterruptions() + " Interruptions. ";
		setText(text);
	}
}