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
package org.mypomodoro.gui.todo;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.mypomodoro.Main;
import static org.mypomodoro.Main.gui;

/**
 * Resize app either using the shortcut or the resize button
 *
 */
public class Resize {

    private Dimension guiRecordedSize;
    private Point guiRecordedLocation;
    private static int viewCount = 0;

    public void resize() {
        if ((!Main.gui.getToDoPanel().isVisible() || viewCount == 0) && Main.gui.getExtendedState() != (Main.gui.getExtendedState() | JFrame.MAXIMIZED_BOTH)) { // maximize gui
            guiRecordedLocation = Main.gui.getLocation(); // record location of the previous window
            guiRecordedSize = Main.gui.getSize(); // record size of the previous window
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Main.gui.setMaximizedBounds(env.getMaximumWindowBounds());
            Main.gui.setExtendedState(Main.gui.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            // set down size icon for resize button
            ToDoPanel.getResizeButton().setDownSizeIcon();
            viewCount = 1;
        } else { // back to the original location
            Dimension size;
            Main.gui.pack();
            if (Main.gui.getToDoPanel().isVisible()) { // only when the ToDo panel is visible
                JPanel tempPanel = new JPanel();
                if (viewCount == 1) { // timer only                    
                    // timer fix size
                    size = new Dimension(300, 350);
                    // record location after the location of the upper right corner
                    // whatever the original size, the reference point is now the upper right corner
                    Dimension screenSize = gui.getToolkit().getScreenSize();
                    double timerXLocation = guiRecordedLocation.getX() + guiRecordedSize.getWidth() - size.getWidth();
                    // prevent the timer to disappear on the right side of the screen 
                    timerXLocation = timerXLocation > screenSize.getWidth() ? screenSize.getWidth() : timerXLocation;
                    // set timer location as new recorded location
                    guiRecordedLocation.setLocation(timerXLocation, guiRecordedLocation.getY());
                    // hide menu and icon bar
                    Main.gui.getJMenuBar().setVisible(false);
                    Main.gui.getIconBar().setVisible(false);
                    // add component to temp panel so it is removed from ToDoPanel
                    tempPanel.add(Main.gui.getToDoPanel().getTodoScrollPane());
                    tempPanel.add(Main.gui.getToDoPanel().getControlPane());
                    //test.add(Main.gui.getMenuBar()); // this may replace setVisible(false)
                    //test.add(Main.gui.getIconBar()); // this may replace setVisible(false)
                    // remove border
                    Main.gui.getToDoPanel().setBorder(null);
                    // hide divider
                    Main.gui.getToDoPanel().hideSplitPaneDivider();
                    // we migth have lost focus when previously editing, overstimating... tasks 
                    // and therefore ESC and ALT+M sortcuts in MainPanel might not work
                    Main.gui.getRootPane().requestFocus();
                    // MAC OSX Java transparency effect
                    //getRootPane().putClientProperty("Window.alpha", new Float(0.4f)); // this is a MAC OSX Java transparency effect
                    viewCount = 2;
                } else if (viewCount == 2) { // timer + list
                    // fix size; on Win 7 aero graphical/theme, 350 is slightly to short
                    size = new Dimension(780, 360);
                    // get location : the timer window may have been moved around
                    guiRecordedLocation = Main.gui.getLocation();
                    double timerWidth = 300; // ignoring any resize of timer
                    guiRecordedLocation.setLocation(guiRecordedLocation.getX() + timerWidth - 780, guiRecordedLocation.getY());
                    // put components back in place
                    Main.gui.getToDoPanel().addToDoTable();
                    Main.gui.getToDoPanel().setTitledBorder();
                    // MAC OSX Java transparency effect : 1.0f = opaque
                    //getRootPane().putClientProperty("Window.alpha", new Float(1.0f));                           
                    viewCount = 3;
                } else { // timer + list + tabs
                    // original size
                    size = guiRecordedSize;
                    // get location : the timer + list window may have been moved around
                    guiRecordedLocation = Main.gui.getLocation();
                    guiRecordedLocation.setLocation(guiRecordedLocation.getX() + 780 - size.getWidth(), guiRecordedLocation.getY());
                    // show menu and icon bar
                    Main.gui.getJMenuBar().setVisible(true);
                    Main.gui.getIconBar().setVisible(true);
                    // put component back in place
                    Main.gui.getToDoPanel().addControlPane();
                    //setJMenuBar(Main.gui.getMenuBar()); // this may replace setVisible(true)
                    //windowPanel.add(Main.gui.getIconBar(), BorderLayout.NORTH); // this may replace setVisible(true)
                    // show divider
                    Main.gui.getToDoPanel().showSplitPaneDivider();
                    viewCount = 0;
                }
                // garbage collect tempPanel quicker
                tempPanel = null;
                Main.gui.setSize(size);
            } else { // create, activities... panels
                size = guiRecordedSize;
                viewCount = 0;
            }
            // set up size icon for resize button
            ToDoPanel.getResizeButton().setUpSizeIcon();
            Dimension dGUI = new Dimension(Math.max(780, gui.getWidth()), Math.max(580, gui.getHeight()));
            Main.gui.setPreferredSize(dGUI);
            Main.gui.setSize(size);
            Main.gui.setLocation(guiRecordedLocation);
        }
        // we make sure the selected task appears on screen despite the resizing
        Main.gui.getActivityListPanel().showCurrentSelectedRow();
        Main.gui.getToDoPanel().showCurrentSelectedRow(); // this doesn't work when viewCount = 3 (timer + list + tabs) with tasks selected at the bottom of the (long) list
        Main.gui.getReportListPanel().showCurrentSelectedRow();
        Main.gui.getChartTabbedPanel().showCurrentSelectedRow();
    }
}
