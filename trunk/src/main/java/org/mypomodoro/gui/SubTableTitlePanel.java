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
package org.mypomodoro.gui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;
import org.mypomodoro.util.ColorUtil;

/**
 *
 *
 */
public class SubTableTitlePanel extends TableTitlePanel {

    public SubTableTitlePanel(IListPanel panel, AbstractActivitiesTable table) {
        super(panel, table);

        // Manage mouse hovering
        addMouseMotionListener(new CustomMouseMotionAdapter());
        // This is to address the case/event when the mouse exit the title
        addMouseListener(new CustomMouseAdapter());
    }

    // Add listeners to possible components
    // Component (button) are added dynamically to this panel    
    @Override
    public Component add(Component comp) {
        boolean isCustomMouseMotionAdapter = false;
        boolean isCustomMouseAdapter = false;
        // make sure the listeners aren't added each the component is added
        for (MouseListener listener : comp.getMouseListeners()) {
            if (listener instanceof CustomMouseMotionAdapter) {
                isCustomMouseMotionAdapter = true;
            }
            if (listener instanceof CustomMouseAdapter) {
                isCustomMouseAdapter = true;
            }
        }
        if (!isCustomMouseMotionAdapter) {
            comp.addMouseMotionListener(new CustomMouseMotionAdapter());
        }
        if (!isCustomMouseAdapter) {
            comp.addMouseListener(new CustomMouseAdapter());
        }
        return super.add(comp);
    }

    class CustomMouseMotionAdapter extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            setBorder(new EtchedBorder(EtchedBorder.LOWERED, Main.selectedRowColor, Main.rowBorderColor));
            setBackground(Main.hoverRowColor);
            titleLabel.setForeground(ColorUtil.BLACK); // this is necessary for themes such as JTatoo Noire
        }
    }

    class CustomMouseAdapter extends MouseAdapter {

        @Override
        public void mouseExited(MouseEvent e) {
            setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            JPanel p = new JPanel();
            setBackground(p.getBackground()); // reset default/theme background color
            titleLabel.setForeground(p.getForeground()); // this is necessary for themes such as JTatoo Noire
        }
    }
}
