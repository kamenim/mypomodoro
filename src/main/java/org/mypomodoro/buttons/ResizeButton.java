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
import static org.mypomodoro.gui.MainPanel.resize;
import org.mypomodoro.util.TransparentButton;

/**
 * Up / Downsize the app
 *
 *
 */
public final class ResizeButton extends TransparentButton {

    private final ImageIcon upSizeIcon = new ImageIcon(Main.class.getResource("/images/upsize.png"));
    private final ImageIcon downSizeIcon = new ImageIcon(Main.class.getResource("/images/downsize.png"));

    public ResizeButton() {
        setUpSizeIcon();
        setToolTipText("ALT + M");
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                resize.resize();
            }
        });
    }

    public void setUpSizeIcon() {
        setIcon(upSizeIcon);
    }

    public void setDownSizeIcon() {
        setIcon(downSizeIcon);
    }
}
