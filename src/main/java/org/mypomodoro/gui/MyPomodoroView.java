package org.mypomodoro.gui;

import java.awt.AWTException;
import java.awt.Container;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
 * @author Phil Karoo
 */
public class MyPomodoroView extends JFrame {

    public static final int FRAME_WIDTH = 677;
    public static final int FRAME_HEIGHT = 700;
    public static TrayIcon trayIcon;
    public static final String MYPOMODORO_VERSION = "2.0";
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
        super("myPomodoro " + MYPOMODORO_VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ImageIcons.MAIN_ICON.getImage());
        setJMenuBar(menuBar);
        setWindow(new SplashScreen());
        setSize(FRAME_HEIGHT, FRAME_WIDTH);
        if (SystemTray.isSupported() && ControlPanel.preferences.getSystemTray()) {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            SystemTray sysTray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(ImageIcons.MAIN_ICON.getImage(), "myPomodoro");
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
            }
            catch (AWTException e) {
                // do nothing
            }
        }
    }

    public void updateView() {
        Main.updateView();
    }

    public final void setWindow(Container e) {
        updateView();
        setContentPane(new WindowPanel(iconBar, e));
        menuBar.revalidate();
    }

    class MyPomodoroMenuBar extends JMenuBar {

        public MyPomodoroMenuBar() {
            add(new FileMenu(MyPomodoroView.this));
            add(new ViewMenu(MyPomodoroView.this));
            add(new TestMenu(MyPomodoroView.this));
            add(new HelpMenu(MyPomodoroView.this));
            setBorder(null);
        }
    }

    public MyPomodoroIconBar getIconBar() {
        return iconBar;
    }
}