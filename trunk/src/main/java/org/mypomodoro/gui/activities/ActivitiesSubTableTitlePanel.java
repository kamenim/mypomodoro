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

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;
import org.mypomodoro.gui.AbstractActivitiesTable;
import org.mypomodoro.gui.preferences.PreferencesInputForm;
import org.mypomodoro.util.ColorUtil;

/**
 *
 *
 */
public class ActivitiesSubTableTitlePanel extends ActivitiesTableTitlePanel {

    public ActivitiesSubTableTitlePanel(final ActivitiesPanel activitiesPanel, AbstractActivitiesTable table) {
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
        // One click actions
        class CustomMouseAdapter extends MouseAdapter {

            private Component comp;
            private int viewCount = 0;
            private Robot robot = null; // used to move the cursor

            public CustomMouseAdapter(Component comp) {
                this.comp = comp;
                try {
                    robot = new Robot();
                } catch (AWTException ignored) {
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // single click
                    if (viewCount == 2
                            && !(comp instanceof DefaultButton)) { // fold: excluding buttons
                        activitiesPanel.getListPane().remove(activitiesPanel.getSubTableScrollPane());
                        activitiesPanel.addTableTitlePanel();
                        activitiesPanel.addTable();
                        activitiesPanel.addSubTableTitlePanel(); // put the sub title back at the bottom
                        viewCount = 0;
                    } else if (viewCount == 0 && activitiesPanel.getTable().getSelectedRowCount() == 1) { // expand half way: including buttons
                        activitiesPanel.getListPane().add(activitiesPanel.getSubTableScrollPane());
                        viewCount = 1;
                    } else if (viewCount == 1 && activitiesPanel.getTable().getSelectedRowCount() == 1 && !(comp instanceof DefaultButton)) { // maximize: excluding buttons                        
                        activitiesPanel.getListPane().remove(activitiesPanel.getTableScrollPane());
                        activitiesPanel.getListPane().remove(activitiesPanel.getTableTitlePanel());
                        viewCount = 2;
                    }
                    // The next two lines adress an issue found on NimRod theme with the resizing of titles                    
                    if (Main.preferences.getTheme().equalsIgnoreCase(PreferencesInputForm.NIMROD_LAF)) {
                        setMaximumSize(new Dimension(Main.gui.getSize().width, 30));
                        activitiesPanel.getTableTitlePanel().setMaximumSize(new Dimension(Main.gui.getSize().width, 30));
                    }
                    // The two following lines are required to
                    // repaint after resizing and move the cursor correctly
                    activitiesPanel.getListPane().validate();
                    activitiesPanel.getListPane().repaint();
                    // Center cursor on resize button
                    if (robot != null) {
                        Point p = getLocationOnScreen(); // location on screen
                        // Center cursor in the middle of the component
                        robot.mouseMove((int) p.getX() + getWidth() / 2, (int) p.getY() + getHeight() / 2);
                    }
                    if (activitiesPanel.getTable().getSelectedRowCount() == 1) {
                        activitiesPanel.getTable().showCurrentSelectedRow();
                    }
                }
            }
        }
        addMouseListener(new CustomMouseAdapter(this));
        Component[] comps = getComponents();
        for (final Component comp : comps) {
            comp.addMouseListener(new CustomMouseAdapter(comp));
        }
    }
}
