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

/**
 * Pin/unpin = set mAP on top of other app
 *
 *
 */
public class PinButton extends TransparentButton {

    private final ImageIcon pinIcon = new ImageIcon(Main.class.getResource(Main.iconsSetPath + "pin.png"));
    private final ImageIcon unpinIcon = new ImageIcon(Main.class.getResource(Main.iconsSetPath + "unpin.png"));
    private boolean isPinIcon = true;

    public PinButton() {
        setPinIcon();
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (isPinIcon) {
                    setUnpinIcon();
                    Main.gui.setAlwaysOnTop(true);
                } else {
                    setPinIcon();
                    Main.gui.setAlwaysOnTop(false);
                }
            }
        });
    }

    private void setPinIcon() {
        setIcon(pinIcon);
        isPinIcon = true;
    }

    public void setUnpinIcon() {
        setIcon(unpinIcon);
        isPinIcon = false;
    }
}
