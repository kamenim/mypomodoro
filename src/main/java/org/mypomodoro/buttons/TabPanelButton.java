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

import java.awt.Dimension;
import java.awt.FontMetrics;

/**
 * Tab panel button On for tab panels such as Details, Edit....
 *
 */
public class TabPanelButton extends AbstractButton {

    public TabPanelButton(String label) {
        super(label);
        // Trying to get rid of ellipses (truncated label with trailing ...)
        FontMetrics fm = getFontMetrics(getFont());
        int labelLength = fm.stringWidth(label);
        setMinimumSize(new Dimension(labelLength > 50 ? labelLength : 50, 50));
        setPreferredSize(new Dimension(labelLength > 50 ? labelLength : 50, 50));
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Short.MAX_VALUE, getPreferredSize().height);
    }
}
