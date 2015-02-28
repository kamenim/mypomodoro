/* 
 * Copyright (C) 
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
import org.mypomodoro.Main;
import org.mypomodoro.gui.todo.Pomodoro;

/**
 * Worflow interruption
 *
 * Continue/discontine the pomodoro workflow
 *
 * Continuous : pomodoro and breaks run continiously
 *
 * Discontinous : workflow stops after each break
 *
 */
public class DiscontinuousButton extends TransparentButton {

    private final ImageIcon discontinuousIcon = new ImageIcon(Main.class.getResource("/images/discontinuous.png"));
    private final ImageIcon continuousIcon = new ImageIcon(Main.class.getResource("/images/continuous.png"));
    private boolean isDiscontinuousIcon = true;

    public DiscontinuousButton(final Pomodoro pomodoro) {
        setDiscontinuousIcon();
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (isDiscontinuousIcon) {
                    setContinuousIcon();
                    pomodoro.discontinueWorkflow();
                } else {
                    setDiscontinuousIcon();
                    pomodoro.continueWorkflow();
                }
            }
        });
    }

    private void setDiscontinuousIcon() {
        setIcon(discontinuousIcon);
        isDiscontinuousIcon = true;
    }

    private void setContinuousIcon() {
        setIcon(continuousIcon);
        isDiscontinuousIcon = false;
    }
}
