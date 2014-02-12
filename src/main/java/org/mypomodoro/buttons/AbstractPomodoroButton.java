package org.mypomodoro.buttons;

import javax.swing.JButton;

/**
 * Template button
 *
 */
public class AbstractPomodoroButton extends JButton {

    private static final long serialVersionUID = 20110814L;

    public AbstractPomodoroButton(String label) {
        super(label);
        setFocusPainted(false); // removes borders around text
        setRolloverEnabled(true);
    }
}
