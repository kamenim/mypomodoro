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

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

/**
 *
 *
 */
public class TabbedPane extends JTabbedPane {

    private final IListPanel panel;

    public TabbedPane(IListPanel panel) {
        this.panel = panel;
        setFocusable(false); // removes borders around tab text
        // One click action (expand / fold)
        addMouseListener(new CustomMouseAdapter());
        // Keystroke
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        for (int i = 1; i <= getTabCount(); i++) {
            im.put(KeyStroke.getKeyStroke(getKeyEvent(i), KeyEvent.CTRL_DOWN_MASK), "Tab" + i);
            am.put("Tab" + i, new tabAction(i - 1));
        }
    }

    // Implement one-click action on selected tabs
    // Tab already selected = one click to expand
    // Tab not selected = double click to expand
    class CustomMouseAdapter extends MouseAdapter {

        private int dividerLocation;
        //private int selectedIndex = 0;
        //private Robot robot = null; // used to move the cursor

        public CustomMouseAdapter() {
            /*try {
                robot = new Robot();
            } catch (AWTException ignored) {
            }*/
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // make sure the double click action is served first            
            if (e.getClickCount() > 1) {
                move();
            } /*else if (e.getClickCount() == 1 && selectedIndex == getSelectedIndex()) {
                move();
            } else {
                selectedIndex = getSelectedIndex();
            }*/
        }

        private void move() {
            JSplitPane splitPane = panel.getSplitPane();
            // Expand
            if (splitPane.getDividerLocation() != 0) {
                dividerLocation = splitPane.getDividerLocation();
                splitPane.setDividerLocation(0.0);
            } else { // back to original position
                splitPane.setDividerLocation(dividerLocation);
            }
            // Set cursor on splitpane
            // This doesn't work properly
            /*if (robot != null) {
                Point p = splitPane.getLocationOnScreen();
                robot.mouseMove((int) p.getX(), (int) p.getY());
            }*/
        }
    }

    // Keystroke for tab
    class tabAction extends AbstractAction {

        final int index;

        public tabAction(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabledAt(index)) {
                setSelectedIndex(index);
            }
        }
    }

    // Retrieve key event with name
    public int getKeyEvent(int index) {
        int key = 0;
        try {
            Field f = KeyEvent.class.getField("VK_" + index);
            f.setAccessible(true);
            key = (Integer) f.get(null);
        } catch (IllegalAccessException ignored) {
        } catch (IllegalArgumentException ignored) {
        } catch (NoSuchFieldException ignored) {
        } catch (SecurityException ignored) {
        }
        return key;
    }
}
