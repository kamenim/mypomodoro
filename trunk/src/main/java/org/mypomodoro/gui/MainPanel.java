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
import javax.swing.SwingUtilities;
import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.burndownchart.TabbedPanel;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.reports.ReportsPanel;
import org.mypomodoro.gui.todo.Resize;
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
    public static final String MYPOMODORO_VERSION = "3.4.0";
    public static Resize resize = new Resize();
    public static PreferencesPanel preferencesPanel = new PreferencesPanel();
    public static SplashScreen splashScreen = new SplashScreen();
    public static CreatePanel createPanel = new CreatePanel();
    public static ActivitiesPanel activitiesPanel = new ActivitiesPanel();
    public static ToDoPanel toDoPanel = new ToDoPanel();
    public static ReportsPanel reportListPanel = new ReportsPanel();
    public static TabbedPanel chartTabbedPanel = new TabbedPanel();
    private final MenuBar menuBar = new MenuBar(this);
    private final IconBar iconBar = new IconBar(this);
    private final WindowPanel windowPanel = new WindowPanel(iconBar, this);
    public static ProgressBar progressBar = new ProgressBar();

    public PreferencesPanel getPreferencesPanel() {
        return preferencesPanel;
    }

    public SplashScreen getSplashScreen() {
        return splashScreen;
    }

    public CreatePanel getCreatePanel() {
        return createPanel;
    }

    public ActivitiesPanel getActivityListPanel() {
        return activitiesPanel;
    }

    public ToDoPanel getToDoPanel() {
        return toDoPanel;
    }

    public ReportsPanel getReportListPanel() {
        return reportListPanel;
    }

    public TabbedPanel getChartTabbedPanel() {
        return chartTabbedPanel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public MainPanel() {
        super("myAgilePomodoro " + MYPOMODORO_VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ImageIcons.MAIN_ICON.getImage());
        setJMenuBar(menuBar);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setContentPane(windowPanel);
        // set JOptionPane dialog locale to localize the buttons
        JOptionPane.setDefaultLocale(Labels.getLocale());
        // Set system tray
        if (SystemTray.isSupported()
                && Main.preferences.getSystemTray()) {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            SystemTray sysTray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(ImageIcons.MAIN_ICON.getImage(),
                    "myAgilePomodoro");
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 0) { // single left click
                        if (!isVisible()) {
                            setVisible(true);
                            setExtendedState(JFrame.NORMAL);
                        } else {
                            // Use  dispose() instead of setVisible(false) to make it work on Linux
                            // http://stackoverflow.com/questions/24315952/java-swing-restore-from-system-tray-not-working-in-linux
                            dispose();
                        }
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
                resize.resize();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(maximizeKeyStroke, "Maximize");
        getRootPane().getActionMap().put("Maximize", maximizeAction);
    }

    public final void setWindow(JPanel e) {
        /*if (e instanceof IListPanel) { // this excludes the burndown chart panel which does not implement AbstractActivitiesPanel
         ((IListPanel) e).refresh();
         }*/
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
                    && Main.preferences.getSystemTray()) {
                // kill tray
                SystemTray sysTray = SystemTray.getSystemTray();
                sysTray.remove(trayIcon);
            }
            System.exit(0);
        }
    }

    public static void updateViews() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                activitiesPanel.refresh();
                toDoPanel.refresh();
                reportListPanel.refresh();
            }
        });
    }
}
