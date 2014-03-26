/* 
 * Copyright (C) 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 */
public class TimeMinusButton extends JButton {

    private static final long serialVersionUID = 20110814L;
    private final ImageIcon timeMinusIcon = new ImageIcon(Main.class.getResource("/images/timeminus.png"));

    public TimeMinusButton(final Pomodoro pomodoro) {
        setIcon(timeMinusIcon);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pomodoro.decreaseTime();
            }
        });
    }
}