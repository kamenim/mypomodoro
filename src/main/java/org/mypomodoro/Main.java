package org.mypomodoro;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.manager.ManagerPanel;
import org.mypomodoro.gui.reports.ReportListPanel;
import org.mypomodoro.gui.todo.ToDoListPanel;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.db.Database;

/**
 * Main Application Starter
 * 
 * @author Brian Wetzel
 */
public class Main {

    public static final Database database = new Database();
    public static ControlPanel controlPanel = new ControlPanel();
    public static final ActivitiesPanel activitiesPanel = new ActivitiesPanel();
    public static final ManagerPanel generatePanel = new ManagerPanel();
    public static final ToDoListPanel toDoListPanel = new ToDoListPanel();
    public static final ReportListPanel reportListPanel = new ReportListPanel();
    public static ReentrantLock datalock = new ReentrantLock();
    public static ResourceBundle labels = ResourceBundle.getBundle("org.mypomodoro.labels.mypomodoro", new Locale(controlPanel.preferences.getLocale().getLanguage(), controlPanel.preferences.getLocale().getCountry(), controlPanel.preferences.getLocale().getVariant()));

    public static void updateView() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                activitiesPanel.refresh();
                toDoListPanel.refresh();
                generatePanel.refresh();
                reportListPanel.refresh();
            }
        }).start();
    }

    public static void updateLists() {
        ActivityList.getList().refresh();
        ToDoList.getList().refresh();
        ReportList.getList().refresh();
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            // Do nothing if the we cannot set a nice ui look and feel
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setUpAndShowGui();
            }
        });
    }

    private static void setUpAndShowGui() {
        final MyPomodoroView gui = new MyPomodoroView();
        gui.setVisible(true);
        Dimension screenSize = gui.getToolkit().getScreenSize();
        int w = (int) ( ( screenSize.getWidth() - gui.getSize().width ) / 2 );
        int h = (int) ( ( screenSize.getHeight() - gui.getSize().height ) / 2 );
        gui.setLocation(w, h);
        gui.addComponentListener(new java.awt.event.ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent event) {
                gui.setSize(Math.max(650, gui.getWidth()), Math.max(550, gui.getHeight()));
            }
        });
    }
}