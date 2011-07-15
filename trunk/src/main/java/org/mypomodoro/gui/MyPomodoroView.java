package org.mypomodoro.gui;

import java.awt.AWTException;
import java.awt.Container;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.gui.manager.ManagerPanel;
import org.mypomodoro.gui.reports.ReportListPanel;
import org.mypomodoro.gui.todo.ToDoListPanel;
import org.mypomodoro.menubar.FileMenu;
import org.mypomodoro.menubar.HelpMenu;
import org.mypomodoro.menubar.TestMenu;
import org.mypomodoro.menubar.ViewMenu;

/**
 * Application GUI for myPomodoro.
 * 
 * @author Brian Wetzel
 */
public class MyPomodoroView extends JFrame {

    public static final int FRAME_WIDTH = 480;
    public static final int FRAME_HEIGHT = 600;
    private final ToDoListPanel toDoListPanel = Main.toDoListPanel;
    private final CreatePanel createPanel = new CreatePanel();
    private final ManagerPanel generatePanel = Main.generatePanel;
    private final ReportListPanel reportListPanel = Main.reportListPanel;
    private final ActivitiesPanel activityListPanel = Main.activitiesPanel;
    private final MyPomodoroMenuBar menuBar = new MyPomodoroMenuBar();
    private final MyPomodoroIconBar iconBar = new MyPomodoroIconBar(this);

    public ToDoListPanel getToDoListPanel() {
        return toDoListPanel;
    }

    public ManagerPanel getGeneratePanel() {
        return generatePanel;
    }

    public ActivitiesPanel getActivityListPanel() {
        return activityListPanel;
    }

    public ReportListPanel getReportListPanel() {
        return reportListPanel;
    }

    public CreatePanel getCreatePanel() {
        return createPanel;
    }

    public MyPomodoroView() {
        super("myPomodoro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ImageIcons.MAIN_ICON.getImage());
        setJMenuBar(menuBar);
        setWindow(new SplashScreen());
        setSize(FRAME_HEIGHT, FRAME_WIDTH);
        if (isSystemTraySupported()) {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            SystemTray sysTray = SystemTray.getSystemTray();
            PopupMenu menu = new PopupMenu();
            MenuItem open = new MenuItem(ControlPanel.labels.getString("SystemTrayMenu.OpenMyPomodoro"));
            open.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(true);
                }
            });
            menu.add(open);
            MenuItem exit = new MenuItem(ControlPanel.labels.getString("SystemTrayMenu.ExitMyPomodoro"));
            exit.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            menu.add(exit);
            TrayIcon trayIcon = new TrayIcon(ImageIcons.MAIN_ICON.getImage(), "myPomodoro", menu);
            try {
                sysTray.add(trayIcon);
            }
            catch (AWTException e) {
                // System tray not supported
            }
        }
    }

    public void updateLists() {
        Main.updateView();
    }

    public void setWindow(Container e) {
        Main.updateView();
        setContentPane(new WindowPanel(iconBar, e));
        menuBar.revalidate();
    }

    class MyPomodoroMenuBar extends JMenuBar {

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

    private boolean isSystemTraySupported() {
        return SystemTray.isSupported();
    }
}