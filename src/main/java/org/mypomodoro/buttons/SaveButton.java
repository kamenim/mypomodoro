package org.mypomodoro.buttons;

import javax.swing.JButton;

import org.mypomodoro.gui.SaveListener;
import org.mypomodoro.gui.create.CreatePanel;

public class SaveButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SaveButton(CreatePanel panel) {
		super("Save");
		addActionListener(new SaveListener(panel));
	}

}