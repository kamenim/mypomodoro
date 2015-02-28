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
 * Mute sound button
 *
 */
public class MuteButton extends TransparentButton {

    private final ImageIcon muteIcon = new ImageIcon(Main.class.getResource("/images/mute.png"));
    private final ImageIcon soundIcon = new ImageIcon(Main.class.getResource("/images/sound.png"));
    private boolean isMuteIcon = true;

    public MuteButton(final Pomodoro pomodoro, boolean mute) {
        if (mute) {
            setMuteIcon();
        } else {
            setSoundIcon();
            isMuteIcon = false;
        }

        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isMuteIcon) {
                    setSoundIcon();
                    pomodoro.mute();
                    isMuteIcon = false;
                } else {
                    setMuteIcon();
                    pomodoro.unmute();
                    isMuteIcon = true;
                }
            }
        });
    }

    public MuteButton(final Pomodoro pomodoro) {
        this(pomodoro, true);
    }

    private void setSoundIcon() {
        setIcon(soundIcon);
    }

    private void setMuteIcon() {
        setIcon(muteIcon);
    }
}
