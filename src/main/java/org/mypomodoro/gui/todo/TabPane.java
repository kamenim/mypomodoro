package org.mypomodoro.gui.todo;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

class TabPane extends JTabbedPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TabPane(InformationPanel informationPanel,
			InterruptPanel interruptPanel) {
		setBackground(Color.white);
		add("Details", informationPanel);
		add("Interrupt", new JScrollPane(interruptPanel));
	}
}