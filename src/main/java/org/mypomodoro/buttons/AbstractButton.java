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

import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Template button
 *
 */
public class AbstractButton extends JButton {

    public AbstractButton(String label) {
        super(label);
        setFocusPainted(false); // removes borders around text
        setRolloverEnabled(true);
        setFont(getFont().deriveFont(Font.BOLD));
    }

    public AbstractButton(ImageIcon icon) {
        super(icon);
        setFocusPainted(false); // removes borders around text
        setRolloverEnabled(true);
    }
}
