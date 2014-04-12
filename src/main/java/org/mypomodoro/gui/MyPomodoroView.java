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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.burndownchart.TabbedPanel;
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
import org.mypomodoro.util.ColorUtil;

/**
 * Application GUI for myPomodoro.
 *
 */
public final class MyPomodoroView extends JFrame {

    private static final long serialVersionUID = 20110814L;
    public static final int FRAME_WIDTH = 780;
    public static final int FRAME_HEIGHT = 580;
    public static TrayIcon trayIcon;
    public static final String MYPOMODORO_VERSION = "3.0";
    private final ToDoPanel toDoPanel = Main.toDoPanel;
    private final CreatePanel createPanel = Main.createPanel;
    private final PreferencesPanel preferencesPanel = Main.preferencesPanel;
    private final ReportsPanel reportListPanel = Main.reportListPanel;
    private final ActivitiesPanel activityListPanel = Main.activitiesPanel;
    private final TabbedPanel chartTabbedPanel = Main.chartTabbedPanel;
    private final MenuBar menuBar = new MenuBar();
    private final IconBar iconBar = new IconBar(this);
    private final ProgressBar progressBar = new ProgressBar();

    public ToDoPanel getToDoPanel() {
        return toDoPanel;
    }

    public ActivitiesPanel getActivityListPanel() {
        return activityListPanel;
    }

    public ReportsPanel getReportListPanel() {
        return reportListPanel;
    }

    public TabbedPanel getChartTabbedPanel() {
        return chartTabbedPanel;
    }

    public CreatePanel getCreatePanel() {
        return createPanel;
    }

    public PreferencesPanel getPreferencesPanel() {
        return preferencesPanel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public MyPomodoroView() {
        super("myAgilePomodoro " + MYPOMODORO_VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ImageIcons.MAIN_ICON.getImage());
        setJMenuBar(menuBar);
        setWindow(new SplashScreen());
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        // Update static lists and views        
        updateLists();
        updateViews();
        updateComboBoxLists();
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
            if (e instanceof ActivitiesPanel) {
                ActivityList.getList().refresh();
            } else if (e instanceof ToDoPanel) {
                ToDoList.getList().refresh();
            } else if (e instanceof ReportsPanel) {
                ReportList.getList().refresh();
            }
            ((AbstractActivitiesPanel) e).refresh();
        }
        setContentPane(new WindowPanel(iconBar, progressBar, e));
        menuBar.revalidate();
    }

    class MenuBar extends JMenuBar {

        private static final long serialVersionUID = 20110814L;

        public MenuBar() {
            add(new FileMenu(MyPomodoroView.this));
            add(new ViewMenu(MyPomodoroView.this));
            add(new TestMenu(MyPomodoroView.this));
            add(new HelpMenu());
            setBorder(null);
        }
    }
    
    public class ProgressBar extends JPanel {

        private static final long serialVersionUID = 20110814L;
        
        private final JProgressBar bar = new JProgressBar();

        public ProgressBar() {
            setVisible(false);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 3), 0, 0);
            gbc.gridx = 0;
            gbc.gridy = 0;
            // Set colors before the 
            UIManager.put("ProgressBar.background", ColorUtil.YELLOW_ROW); //colour of the background
            UIManager.put("ProgressBar.foreground", ColorUtil.BLUE_ROW); //colour of progress bar
            UIManager.put("ProgressBar.selectionBackground", ColorUtil.BLACK); //colour of percentage counter on background
            UIManager.put("ProgressBar.selectionForeground", ColorUtil.BLACK); //colour of precentage counter on progress bar            
            bar.setOpaque(true); // required to get colors being displayed
            bar.setStringPainted(true); // required to get colors being displayed
            //progressBar.setMinimum(0);
            //progressBar.setMaximum(100);
            bar.setBorder(new MatteBorder(1, 0, 1, 0, ColorUtil.BLUE_ROW));
            bar.setFont(getFont().deriveFont(Font.BOLD));            
            add(bar, gbc);
            setBorder(null);
        }
        
        public JProgressBar getBar() {
            return bar;
        }
    }

    public IconBar getIconBar() {
        return iconBar;
    }
}
