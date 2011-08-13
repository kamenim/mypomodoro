package org.mypomodoro.gui.create;

import java.awt.Dimension;

import javax.swing.JLabel;

public class FormLabel extends JLabel {
	private static final long serialVersionUID = 20110814L;
	
    public FormLabel(String str) {
        super(str);
        Dimension labelDimension = new Dimension(150, 25);
        setPreferredSize(labelDimension);
        setMinimumSize(labelDimension);
        setMaximumSize(labelDimension);
        setAlignmentX(LEFT_ALIGNMENT);
    }
}