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
import java.awt.event.MouseListener;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;
import org.mypomodoro.gui.AbstractActivitiesTable;
import org.mypomodoro.gui.SubTableTitlePanel;
import org.mypomodoro.gui.preferences.PreferencesInputForm;

/**
 *
 *
 */
public class ActivitiesSubTableTitlePanel extends SubTableTitlePanel {

    private final ActivitiesPanel panel;
    private int viewCount = 0;

    public ActivitiesSubTableTitlePanel(final ActivitiesPanel panel, AbstractActivitiesTable table) {
        super(panel, table);

        this.panel = panel;

        // On click action
        addMouseListener(new CustomMouseAdapter(this));
    }

    // Add listener to possible components
    // Component (buttons) are added dynamically to this panel
    @Override
    public Component add(Component comp) {
        boolean isCustomMouseAdapter = false;
        // make sure the listeners aren't added each the component is added
        for (MouseListener listener : comp.getMouseListeners()) {
            if (listener instanceof CustomMouseAdapter) {
                isCustomMouseAdapter = true;
            }
        }
        if (!isCustomMouseAdapter) {
            comp.addMouseListener(new CustomMouseAdapter(comp));
        }
        return super.add(comp);
    }

    class CustomMouseAdapter extends MouseAdapter {

        private final Component comp;
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
                if (viewCount == 2 && !(comp instanceof DefaultButton)) { // fold: excluding buttons
                    panel.getListPane().remove(panel.getSubTableScrollPane());
                    panel.addTableTitlePanel();
                    panel.addTable();
                    panel.addSubTableTitlePanel(); // put the sub title back at the bottom
                    viewCount = 0;
                } else if (viewCount == 0 && panel.getTable().getSelectedRowCount() == 1) { // expand half way: including buttons                        
                    panel.getListPane().add(panel.getSubTableScrollPane());
                    viewCount = 1;
                } else if (viewCount == 1 && !(comp instanceof DefaultButton)) { // maximize: excluding buttons                        
                    panel.getListPane().remove(panel.getTableScrollPane());
                    panel.getListPane().remove(panel.getTableTitlePanel());
                    viewCount = 2;
                }
                // The next two lines adress an issue found on NimRod theme with the resizing of titles                    
                if (Main.preferences.getTheme().equalsIgnoreCase(PreferencesInputForm.NIMROD_LAF)) {
                    setMaximumSize(new Dimension(Main.gui.getSize().width, 30));
                    panel.getTableTitlePanel().setMaximumSize(new Dimension(Main.gui.getSize().width, 30));
                }
                // The two following lines are required to
                // repaint after resizing and move the cursor correctly
                panel.getListPane().validate();
                panel.getListPane().repaint();
                // Center cursor on panel
                if (robot != null && !(comp instanceof DefaultButton)) {
                    Point p = getLocationOnScreen(); // location on screen
                    // Center cursor in the middle of the component
                    robot.mouseMove((int) p.getX() + getWidth() / 2, (int) p.getY() + getHeight() / 2);
                }
                if (panel.getTable().getSelectedRowCount() == 1) {
                    panel.getTable().showCurrentSelectedRow();
                }
            }
        }
    }
}
