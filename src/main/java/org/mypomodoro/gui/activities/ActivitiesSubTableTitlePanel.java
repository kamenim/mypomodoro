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
package org.mypomodoro.gui.activities;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.gui.AbstractActivitiesTable;
import org.mypomodoro.util.ColorUtil;

/**
 *
 *
 */
public class ActivitiesSubTableTitlePanel extends ActivitiesTableTitlePanel {   

    public ActivitiesSubTableTitlePanel(ActivitiesPanel activitiesPanel, AbstractActivitiesTable table) {
        super(activitiesPanel, table);        
        // Manage mouse hovering
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                setBorder(new EtchedBorder(EtchedBorder.LOWERED, ColorUtil.BLUE_ROW, ColorUtil.BLUE_ROW_DARKER));
                setBackground(ColorUtil.YELLOW_ROW);
                titleLabel.setForeground(ColorUtil.BLACK); // this is necessary for themes such as JTatoo Noire
            }
        });
        // This is to address the case/event when the mouse exit the title
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(new EtchedBorder(EtchedBorder.LOWERED));
                JPanel p = new JPanel();
                setBackground(p.getBackground()); // reset default/theme background color
                titleLabel.setForeground(p.getForeground()); // this is necessary for themes such as JTatoo Noire
            }
        });
    }    
}
