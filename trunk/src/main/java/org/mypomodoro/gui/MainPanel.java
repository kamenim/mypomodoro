/* 
 * Copyright (C) 2014
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.burndownchart.TabbedPanel;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.gui.reports.ReportsPanel;
import org.mypomodoro.gui.todo.ToDoPanel;

/**
 * Application GUI for myPomodoro.
 *
 */
public final class MainPanel extends JFrame {

    private static final long serialVersionUID = 20110814L;
    public static final int FRAME_WIDTH = 780;
    public static final int FRAME_HEIGHT = 580;
    public static TrayIcon trayIcon;
    public static final String MYPOMODORO_VERSION = "3.0";
    private final MenuBar menuBar = new MenuBar(this);
    private final IconBar iconBar = new IconBar(this);
    private final WindowPanel windowPanel = new WindowPanel(iconBar, this);

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
            } catch (AWTException e) {
                // do nothing
            }
        }
    }

    public void updateLists() {
        Main.updateLists();
    }

    public void updateViews() {
        Main.updateViews();
    }

    public void updateComboBoxLists() {
        Main.updateComboBoxLists();
    }

    public final void setWindow(JPanel e) {
        if (e instanceof AbstractActivitiesPanel) { // this excludes the burndown chart panel which does not implement AbstractActivitiesPanel
            // Refresh from database
            /*if (e instanceof ActivitiesPanel) {
             ActivityList.getList().refresh();
             } else if (e instanceof ToDoPanel) {
             ToDoList.getList().refresh();
             } else if (e instanceof ReportsPanel) {
             ReportList.getList().refresh();
             }*/
            ((AbstractActivitiesPanel) e).refresh();
        }
        windowPanel.showPanel(e.getClass().getName());
    }

    public IconBar getIconBar() {
        return iconBar;
    }
}
