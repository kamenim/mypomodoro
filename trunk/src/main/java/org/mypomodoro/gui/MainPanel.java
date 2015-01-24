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

import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.util.ProgressBar;
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
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.burndownchart.TabbedPanel;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.gui.reports.ReportsPanel;
import org.mypomodoro.gui.todo.ToDoPanel;
import org.mypomodoro.util.Labels;

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
                    String title = Labels.getString("FileMenu.Exit myPomodoro");
                    String message = Labels.getString("FileMenu.Are you sure to exit myPomodoro?");
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                            title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            };
            addWindowListener(exitListener);
        }

        // Maximize keystoke
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK);
        Action maximizeAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (getExtendedState() != (getExtendedState() | JFrame.MAXIMIZED_BOTH)) { // maximize gui
                    guiRecordedSize = getSize();
                    guiRecordedLocation = getLocation();
                    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    setMaximizedBounds(env.getMaximumWindowBounds());
                    setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
                } else { // back to the original size
                    /*int state = 1;
                    switch (state) {
                        case 1:
                            menuBar.setVisible(false);
                            iconBar.setVisible(false);
                            // Components frame work architecture. Closely the problem'is in the one thing:
                            // each component has one owner. when you are adding component
                            // to container. His owner is changing automatically.
                            JPanel test = new JPanel();
                            //test.add(getToDoPanel().getTimerPanel());
                            test.add(getToDoPanel().getTodoTable());
                            state = 2;
                            break;
                        case 2:
                            menuBar.setVisible(true);
                            iconBar.setVisible(true);
                                //getToDoPanel().addToDoTable();
                            //gui.setSize(guiRecordedSize);
                            //gui.setMinimumSize(new Dimension(50, 100));
                            //gui.setPreferredSize(new Dimension(50, 100));
                            state = 3;
                            break;
                        default:
                            gui.setSize(guiRecordedSize);
                            gui.setLocation(guiRecordedLocation);
                            break;
                    }*/
                    //setSize(new Dimension(50, 100));
                    setSize(guiRecordedSize);
                    setLocation(guiRecordedLocation);
                }
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "Maximize");
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
}
