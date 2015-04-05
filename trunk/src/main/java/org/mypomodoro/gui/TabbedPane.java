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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 *
 */
public class TabbedPane extends JTabbedPane {

    private JSplitPane splitPane;

    public TabbedPane() {
        setFocusable(false); // removes borders around tab text
        // One click action (expand / fold)
        CustomChangeListener customChangeListener = new CustomChangeListener();
        addChangeListener(customChangeListener);
        addMouseListener(new CustomMouseAdapter(customChangeListener));
        // Keystroke
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        for (int i = 1; i <= getTabCount(); i++) {
            im.put(KeyStroke.getKeyStroke(getKeyEvent(i), KeyEvent.CTRL_DOWN_MASK), "Tab" + i);
            am.put("Tab" + i, new tabAction(i - 1));
        }
    }
    
    public void setSplitPane(JSplitPane splitPane) {
        this.splitPane = splitPane;
    }

    // Implement one-click action on selected tabs
    // Tab already selected = one click to expand
    // Tab not selected = double click to expand
    // Note: if a tab is selected programatically, only double click will work on that tab
    class CustomChangeListener implements ChangeListener {

        private boolean stateChanged = false;

        @Override
        public void stateChanged(ChangeEvent e) {
            stateChanged = true;
        }

        public boolean getStateChanged() {
            return stateChanged;
        }

        public void setStateChanged(boolean stateChanged) {
            this.stateChanged = stateChanged;
        }
    }

    class CustomMouseAdapter extends MouseAdapter {

        private final CustomChangeListener customChangeListener;
        private int dividerLocation;
        //private Robot robot = null; // used to move the cursor

        public CustomMouseAdapter(CustomChangeListener customChangeListener) {
            this.customChangeListener = customChangeListener;
            /*try {
             robot = new Robot();
             } catch (AWTException ignored) {
             }*/
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1
                    || (e.getClickCount() == 1 && !customChangeListener.getStateChanged())) {
                // Expand
                if (splitPane.getDividerLocation() != 0) {
                    dividerLocation = splitPane.getDividerLocation();
                    splitPane.setDividerLocation(0.0);
                } else { // back to original position
                    splitPane.setDividerLocation(dividerLocation);
                }
                // Center cursor on selected tab
                // This doesn't work
                    /*if (robot != null) {
                 Point p = tabbedPane.getLocationOnScreen(); // location on screen                        
                 robot.mouseMove((int) p.getX(), (int) p.getY());
                 }*/
            } else {
                customChangeListener.setStateChanged(false);
            }
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
