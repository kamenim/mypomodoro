package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.mypomodoro.Main;
import org.mypomodoro.gui.todo.Pomodoro;

/**
 * Time plus button
 *
 * @author Phil Karoo
 */
public class TimePlusButton extends JButton {

    private static final long serialVersionUID = 20110814L;
    private final ImageIcon timePlusIcon = new ImageIcon(Main.class.getResource("/images/timeplus.png"));

    public TimePlusButton(final Pomodoro pomodoro) {
        setIcon(timePlusIcon);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pomodoro.increaseTime();
            }
        });
    }
}
