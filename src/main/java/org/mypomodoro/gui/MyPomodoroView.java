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
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.burndownchart.BurndownTabbedPanel;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.gui.reports.ReportsPanel;
import org.mypomodoro.gui.todo.ToDoPanel;
import org.mypomodoro.menubar.FileMenu;
import org.mypomodoro.menubar.HelpMenu;
import org.mypomodoro.menubar.TestMenu;
import org.mypomodoro.menubar.ViewMenu;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;

/**
 * Application GUI for myPomodoro.
 *
 */
public class MyPomodoroView extends JFrame {

    private static final long serialVersionUID = 20110814L;
    public static final int FRAME_WIDTH = 780;
    public static final int FRAME_HEIGHT = 580;
    public static TrayIcon trayIcon;
    public static final String MYPOMODORO_VERSION = "3.0";
    private final ToDoPanel toDoPanel = Main.toDoPanel;
    private final CreatePanel createPanel = new CreatePanel();
    private final ReportsPanel reportListPanel = Main.reportListPanel;
    private final ActivitiesPanel activityListPanel = Main.activitiesPanel;
    public final BurndownTabbedPanel burndownPanel = Main.burndownPanel;
    private final MyPomodoroMenuBar menuBar = new MyPomodoroMenuBar();
    private final MyPomodoroIconBar iconBar = new MyPomodoroIconBar(this);

    public ToDoPanel getToDoPanel() {
        return toDoPanel;
    }

    public ActivitiesPanel getActivityListPanel() {
        return activityListPanel;
    }

    public ReportsPanel getReportListPanel() {
        return reportListPanel;
    }

    public BurndownTabbedPanel getBurndownPanel() {
        return burndownPanel;
    }

    public CreatePanel getCreatePanel() {
        return createPanel;
    }

    public MyPomodoroView() {
        super("myAgilePomodoro " + MYPOMODORO_VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ImageIcons.MAIN_ICON.getImage());
        setJMenuBar(menuBar);
        setWindow(new SplashScreen());
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
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

    public void updateView() {
        Main.updateView();
    }

    public final void setWindow(JPanel e) {
        if (e instanceof AbstractActivitiesPanel) { // this excludes the burndown chart panel which does not implement AbstractActivitiesPanel
            // Refresh from database
            if (e instanceof ActivitiesPanel) {
                ActivityList.getList().refresh();
            } else if (e instanceof ToDoPanel) {
                ToDoList.getList().refresh();                
            } else if (e instanceof ReportsPanel) {
                ReportList.getList().refresh();                
            }                    
            ((AbstractActivitiesPanel) e).refresh();
        }
        setContentPane(new WindowPanel(iconBar, e));
        menuBar.revalidate();
    }

    class MyPomodoroMenuBar extends JMenuBar {

        private static final long serialVersionUID = 20110814L;

        public MyPomodoroMenuBar() {
            add(new FileMenu(MyPomodoroView.this));
            add(new ViewMenu(MyPomodoroView.this));
            add(new TestMenu(MyPomodoroView.this));
            add(new HelpMenu());
            setBorder(null);
        }
    }

    public MyPomodoroIconBar getIconBar() {
        return iconBar;
    }
}
