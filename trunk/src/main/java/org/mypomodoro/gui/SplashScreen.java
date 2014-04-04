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
package org.mypomodoro.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mypomodoro.Main;
import org.mypomodoro.util.ColorUtil;

public class SplashScreen extends JPanel {

    private static final long serialVersionUID = 20110814L;

    public SplashScreen() {
        setBackground(ColorUtil.WHITE);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel title = new JLabel("");
        title.setFont(Main.font.deriveFont(48f));
        title.setForeground(ColorUtil.RED);

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        // add(title, c);
        c.gridy = 1;
        add(new JLabel(ImageIcons.SPLASH_ICON, JLabel.CENTER), c);
    }
}
