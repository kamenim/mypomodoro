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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import javax.swing.JFrame;
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
    private Robot robot = null; // used to move the cursor
            
    public Resize() {
        try {
            robot = new Robot();
        } catch (AWTException ignored) {
        }
    }

    public void resize() {
        if ((!Main.gui.getToDoPanel().isShowing() || viewCount == 0) && Main.gui.getExtendedState() != (Main.gui.getExtendedState() | JFrame.MAXIMIZED_BOTH)) { // maximize gui            
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
            if (Main.gui.getToDoPanel().isShowing()) { // only when the ToDo panel is visible                
                if (viewCount == 1) { // timer only                    
                    // timer fix size
                    size = new Dimension(300, 360);
                    // record location after the location of the upper right corner
                    // whatever the original size, the reference point is now the upper right corner
                    Dimension screenSize = gui.getToolkit().getScreenSize();
                    double timerXLocation = guiRecordedLocation.getX() + guiRecordedSize.getWidth() - size.getWidth();
                    // prevent the timer to disappear on the right side of the screen 
                    timerXLocation = timerXLocation > screenSize.getWidth() ? screenSize.getWidth() : timerXLocation;
                    // set timer location as new recorded location
                    guiRecordedLocation.setLocation(timerXLocation, guiRecordedLocation.getY());
                    // remove menu and icon bar
                    Main.gui.removeMenuBar();
                    Main.gui.removeIconBar();
                    // Remove components from ToDoPanel
                    Main.gui.getToDoPanel().removeTitlePanel();
                    Main.gui.getToDoPanel().removeScrollPane();
                    Main.gui.getToDoPanel().removeControlPane();
                    // hide divider
                    //Main.gui.getToDoPanel().hideSplitPaneDivider();
                    // we migth have lost focus when previously editing, overstimating... tasks 
                    // and therefore ESC and ALT+M sortcuts in MainPanel might not work
                    Main.gui.getRootPane().requestFocus();
                    // MAC OSX Java transparency effect
                    //getRootPane().putClientProperty("Window.alpha", new Float(0.4f)); // this is a MAC OSX Java transparency effect
                    viewCount = 2;
                } else if (viewCount == 2) { // timer + list
                    size = new Dimension(800, 360);
                    // get location : the timer window may have been moved around
                    guiRecordedLocation = Main.gui.getLocation();
                    double timerWidth = 300; // ignoring any resize of timer
                    guiRecordedLocation.setLocation(guiRecordedLocation.getX() + timerWidth - 800, guiRecordedLocation.getY());
                    // put components back in place
                    Main.gui.getToDoPanel().addTitlePanel();
                    Main.gui.getToDoPanel().setPanelBorder();
                    Main.gui.getToDoPanel().addTable();
                    // MAC OSX Java transparency effect : 1.0f = opaque
                    //getRootPane().putClientProperty("Window.alpha", new Float(1.0f));                           
                    viewCount = 3;
                } else { // timer + list + tabs
                    // original size
                    size = guiRecordedSize;
                    // get location : the timer + list window may have been moved around
                    guiRecordedLocation = Main.gui.getLocation();
                    guiRecordedLocation.setLocation(guiRecordedLocation.getX() + 800 - size.getWidth(), guiRecordedLocation.getY());
                    // show menu and icon bar
                    Main.gui.setJMenuBar(Main.gui.getJMenuBar());
                    Main.gui.addIconBar();
                    // put component back in place
                    Main.gui.getToDoPanel().addControlPane();
                    // show divider
                    //Main.gui.getToDoPanel().showSplitPaneDivider();
                    viewCount = 0;
                }
                Main.gui.setSize(size);
            } else { // create, activities... panels
                size = guiRecordedSize;
                viewCount = 0;
            }
            // set up size icon for resize button
            ToDoPanel.getResizeButton().setUpSizeIcon();
            Dimension dGUI = new Dimension(Math.max(800, gui.getWidth()), Math.max(600, gui.getHeight()));
            Main.gui.setPreferredSize(dGUI);
            Main.gui.setSize(size);
            Main.gui.setLocation(guiRecordedLocation);            
        }
        // The two following lines are required to:
        // Maximize size: move the cursor correctly
        // Other sizes: repaint after resizing and move the cursor correctly
        Main.gui.validate();
        Main.gui.repaint();            
        // Center cursor on resize button
        if (robot != null
                && ToDoPanel.getResizeButton().isShowing()) {               
            Point p = ToDoPanel.getResizeButton().getLocationOnScreen(); // location on screen
            // Center cursor in the middle of the button
            robot.mouseMove((int) p.getX() + ToDoPanel.getResizeButton().getWidth()/2, (int) p.getY()+ ToDoPanel.getResizeButton().getHeight()/2);

        }
        // we make sure the selected task appears on screen despite the resizing
        Main.gui.getActivityListPanel().showCurrentSelectedRow();
        Main.gui.getToDoPanel().showCurrentSelectedRow(); // this doesn't work when viewCount = 3 (timer + list + tabs) with tasks selected at the bottom of the (long) list
        Main.gui.getReportListPanel().showCurrentSelectedRow();
        Main.gui.getChartTabbedPanel().showCurrentSelectedRow();
    }

    /**
     * Force resizing to original size
     */
    public void resizeToOriginalSize() {
        if (Main.gui.getToDoPanel().isVisible()) {
            if (viewCount == 2) { // resize two times
                resize();
                resize();
            } else if (viewCount == 3) { // resize one time
                resize();
            }
        }
    }
}
