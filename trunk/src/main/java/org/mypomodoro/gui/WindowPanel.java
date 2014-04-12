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

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JPanel;

public class WindowPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;

    public WindowPanel(JPanel iconBar, JPanel progressBar, Container panel) {
        setLayout(new BorderLayout());
        setOpaque(true);
        add(iconBar, BorderLayout.NORTH);
        add(progressBar);
        add(panel, BorderLayout.CENTER);
    }
}
