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
import org.mypomodoro.gui.reports.ReportListPanel;
import org.mypomodoro.gui.burndownchart.BurndownPanel;
import org.mypomodoro.gui.todo.ToDoListPanel;
import org.mypomodoro.gui.todo.ToDoPanel;
import org.mypomodoro.menubar.FileMenu;
import org.mypomodoro.menubar.HelpMenu;
import org.mypomodoro.menubar.TestMenu;
import org.mypomodoro.menubar.ViewMenu;

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
    //private final ManagerPanel managerPanel = Main.managerPanel;
    private final ReportListPanel reportListPanel = Main.reportListPanel;
    private final ActivitiesPanel activityListPanel = Main.activitiesPanel;
    public final BurndownPanel burndownPanel = Main.burndownPanel;
    private final MyPomodoroMenuBar menuBar = new MyPomodoroMenuBar();
    private final MyPomodoroIconBar iconBar = new MyPomodoroIconBar(this);

    public ToDoPanel getToDoPanel() {
        return toDoPanel;
    }

    /*public ManagerPanel getGeneratePanel() {
     return managerPanel;
     }*/
    public ActivitiesPanel getActivityListPanel() {
        return activityListPanel;
    }

    public ReportListPanel getReportListPanel() {
        return reportListPanel;
    }

    public BurndownPanel getBurndownPanel() {
        return burndownPanel;
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
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        if (SystemTray.isSupported()
                && ControlPanel.preferences.getSystemTray()) {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            SystemTray sysTray = SystemTray.getSystemTray();
            trayIcon = new TrayIcon(ImageIcons.MAIN_ICON.getImage(),
                    "myPomodoro");
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

    public final void setWindow(Container e) {
        updateView();
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
