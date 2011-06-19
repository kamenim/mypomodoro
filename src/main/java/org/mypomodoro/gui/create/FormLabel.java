package org.mypomodoro.gui.create;

import java.awt.Dimension;

import javax.swing.JLabel;

public class FormLabel extends JLabel {
	public FormLabel(String str) {
		super(str);
		Dimension labelDimension = new Dimension(150, 25);
		setPreferredSize(labelDimension);
		setMinimumSize(labelDimension);
		setMaximumSize(labelDimension);
		setAlignmentX(LEFT_ALIGNMENT);
	}
}