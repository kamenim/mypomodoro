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
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.mypomodoro.Main;
import static org.mypomodoro.Main.gui;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.burndownchart.TabbedPanel;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.reports.ReportsPanel;
import org.mypomodoro.gui.todo.ToDoPanel;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.ProgressBar;

/**
 * Application GUI for myPomodoro.
 *
 */
public final class MainPanel extends JFrame {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public static final int FRAME_WIDTH = 780;
    public static final int FRAME_HEIGHT = 580;
    public static TrayIcon trayIcon;
    public static final String MYPOMODORO_VERSION = "3.2.0";
    private final MenuBar menuBar = new MenuBar(this);
    private final IconBar iconBar = new IconBar(this);
    private final WindowPanel windowPanel = new WindowPanel(iconBar, this);
    private static Dimension guiRecordedSize;
    private static Point guiRecordedLocation;
    private static int viewCount = 0;

    public SplashScreen getSplashScreen() {
        return Main.splashScreen;
    }

    public PreferencesPanel getPreferencesPanel() {
        return Main.preferencesPanel;
    }

    public CreatePanel getCreatePanel() {
        return Main.createPanel;
    }

    public ActivitiesPanel getActivityListPanel() {
        return Main.activitiesPanel;
    }

    public ToDoPanel getToDoPanel() {
        return Main.toDoPanel;
    }

    public ReportsPanel getReportListPanel() {
        return Main.reportListPanel;
    }

    public TabbedPanel getChartTabbedPanel() {
        return Main.chartTabbedPanel;
    }

    public ProgressBar getProgressBar() {
        return Main.progressBar;
    }

    public MainPanel() {
        super("myAgilePomodoro " + MYPOMODORO_VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ImageIcons.MAIN_ICON.getImage());
        setJMenuBar(menuBar);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setContentPane(windowPanel);
        // Set system tray
        if (SystemTray.isSupported()
                && PreferencesPanel.preferences.getSystemTray()) {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            SystemTray sysTray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(ImageIcons.MAIN_ICON.getImage(),
                    "myAgilePomodoro");
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 0) { // single left click
                        setVisible(!isVisible());
                    }
                }
            });
            try {
                sysTray.add(trayIcon);
            } catch (AWTException ex) {
                logger.error("", ex);
            }
        } else {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            WindowListener exitListener = new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    exit();
                }
            };
            addWindowListener(exitListener);
        }

        // Exit keystroke
        KeyStroke exitKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Action exitAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(exitKeyStroke, "Exit");
        getRootPane().getActionMap().put("Exit", exitAction);

        // Maximize keystoke
        KeyStroke maximizeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK);
        Action maximizeAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if ((!getToDoPanel().isVisible() || viewCount == 0) && getExtendedState() != (getExtendedState() | JFrame.MAXIMIZED_BOTH)) { // maximize gui
                    guiRecordedLocation = getLocation();
                    guiRecordedSize = getSize();
                    guiRecordedLocation = getLocation();
                    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    setMaximizedBounds(env.getMaximumWindowBounds());
                    setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
                    viewCount = 1;
                } else { // back to the original location
                    Dimension size;
                    pack();
                    if (getToDoPanel().isVisible()) { // only when the ToDo panel is visible
                        JPanel tempPanel = new JPanel();
                        if (viewCount == 1) { // timer only
                            menuBar.setVisible(false);
                            iconBar.setVisible(false);
                            size = new Dimension(300, 350);
                            //test.add(menuBar);
                            //test.add(iconBar);
                            tempPanel.add(getToDoPanel().getTodoScrollPane()); // add component to a panel so it is removed from ToDoPanel
                            tempPanel.add(getToDoPanel().getControlPane()); // add component to a panel so it is removed from ToDoPanel
                            getToDoPanel().setBorder(null); // remove border
                            getToDoPanel().hideSplitPaneDivider();
                            // we migth have lost focus when previously editing, overstimating... tasks 
                            // and therefore ESC and ALT+M sortcuts in MainPanel might not work
                            getRootPane().requestFocus();
                            //getRootPane().putClientProperty("Window.alpha", new Float(0.4f)); // this is a MAC OSX Java transparency effect
                            viewCount = 2;
                        } else if (viewCount == 2) { // timer + list
                            guiRecordedLocation = getLocation();
                            size = new Dimension(780, 360); // on Win 7 aero graphical/theme, 350 is slightly to short
                            getToDoPanel().addToDoTable(); // put component back in its place
                            getToDoPanel().setTitledBorder(); // put border back in its place
                            //getRootPane().putClientProperty("Window.alpha", new Float(1.0f)); // this is a MAC OSX Java transparency effect                           
                            viewCount = 3;
                        } else { // timer + list + tabs
                            guiRecordedLocation = getLocation();
                            size = guiRecordedSize;
                            menuBar.setVisible(true);
                            iconBar.setVisible(true);
                            //setJMenuBar(menuBar);
                            //windowPanel.add(iconBar, BorderLayout.NORTH);
                            getToDoPanel().addControlPane(); // put component back in its place
                            getToDoPanel().showSplitPaneDivider();
                            viewCount = 0;
                        }
                        // garbage collect tempPanel quicker
                        tempPanel = null;
                        setSize(size);
                    } else {
                        size = guiRecordedSize;
                        viewCount = 0;
                    }
                    Dimension dGUI = new Dimension(Math.max(780, gui.getWidth()), Math.max(580, gui.getHeight()));
                    setPreferredSize(dGUI);
                    setSize(size);
                    setLocation(guiRecordedLocation);
                }
                // we make sure the selected task appears on screen despite the resizing
                getActivityListPanel().showCurrentSelectedRow();
                getToDoPanel().showCurrentSelectedRow(); // this doesn't work when viewCount = 3 (timer + list + tabs) with tasks selected at the bottom of the (long) list
                getReportListPanel().showCurrentSelectedRow();
                getChartTabbedPanel().showCurrentSelectedRow();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(maximizeKeyStroke, "Maximize");
        getRootPane().getActionMap().put("Maximize", maximizeAction);
    }

    public void updateLists() {
        Main.updateLists();
    }

    public void updateViews() {
        Main.updateViews();
    }

    public final void setWindow(JPanel e) {
        if (e instanceof IListPanel) { // this excludes the burndown chart panel which does not implement AbstractActivitiesPanel
            ((IListPanel) e).refresh();
        }
        windowPanel.showPanel(e.getClass().getName());
    }

    public IconBar getIconBar() {
        return iconBar;
    }

    public static void exit() {
        String title = Labels.getString("FileMenu.Exit myPomodoro");
        String message = Labels.getString("FileMenu.Are you sure to exit myPomodoro?");
        int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (reply == JOptionPane.YES_OPTION) {
            if (SystemTray.isSupported()
                    && PreferencesPanel.preferences.getSystemTray()) {
                // kill tray
                SystemTray sysTray = SystemTray.getSystemTray();
                sysTray.remove(trayIcon);
            }
            System.exit(0);
        }
    }
}
