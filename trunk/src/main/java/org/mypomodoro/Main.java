package org.mypomodoro;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.mypomodoro.db.Database;
import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.create.list.AuthorList;
import org.mypomodoro.gui.create.list.PlaceList;
import org.mypomodoro.gui.create.list.TypeList;
import org.mypomodoro.gui.reports.ReportsPanel;
import org.mypomodoro.gui.burndownchart.BurndownPanel;
import org.mypomodoro.gui.todo.ToDoPanel;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.RestartMac;

/**
 * Main Application Starter
 *
 */
public class Main {

    public static final Database database = new Database();
    public final static PreferencesPanel controlPanel = new PreferencesPanel();
    public static final ActivitiesPanel activitiesPanel = new ActivitiesPanel();
    public static final ToDoPanel toDoPanel = new ToDoPanel();
    public static final ReportsPanel reportListPanel = new ReportsPanel();
    public static final BurndownPanel burndownPanel = new BurndownPanel();
    public final static ReentrantLock datalock = new ReentrantLock();
    public static final MyPomodoroView gui = new MyPomodoroView();

    public static void updateView() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                activitiesPanel.refresh();
                toDoPanel.refresh();
                reportListPanel.refresh();
            }
        });
    }

    public static void updateLists() {
        ActivityList.getList().refresh();
        ToDoList.getList().refresh();
        ReportList.getList().refresh();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {
            // deletes files created with RestartMac()
            new RestartMac(1);
            return;
        }
        initLists();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                /* Substance look and feel not fully working with the following lines of code (enable dependency in pom.xml)  
                 try {
                 JFrame.setDefaultLookAndFeelDecorated(true);
                 UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel());
                 updateComponentTreeUI(gui);
                 } catch (UnsupportedLookAndFeelException e) {*/
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                    // cross platform look and feel is used by default by the JVM
                } catch (InstantiationException ex) {
                    // cross platform look and feel is used by default by the JVM
                } catch (IllegalAccessException ex) {
                    // cross platform look and feel is used by default by the JVM
                } catch (UnsupportedLookAndFeelException ex) {
                    // cross platform look and feel is used by default by the JVM
                }
                setUpAndShowGui();
            }
        });
    }

    private static void initLists() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TypeList.initTypes();
                AuthorList.initAuthors();
                PlaceList.initPlaces();
            }
        });
    }

    private static void setUpAndShowGui() {
        /*
         * Old fashion way to center the component onscreen
         * Dimension screenSize
         * = gui.getToolkit().getScreenSize(); int w = (int) ( (
         * screenSize.getWidth() - gui.getSize().width ) / 2 ); int h = (int) (
         * ( screenSize.getHeight() - gui.getSize().height ) / 2 );
         * gui.setLocation(w, h);
         */
        gui.pack();
        gui.setLocationRelativeTo(null); // center the component onscreen
        gui.setVisible(true);
        if (org.mypomodoro.gui.PreferencesPanel.preferences.getAlwaysOnTop()) {
            gui.setAlwaysOnTop(true);
        }
        gui.addComponentListener(new java.awt.event.ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent event) {
                Dimension dGUI = new Dimension(Math.max(780, gui.getWidth()),
                        Math.max(580, gui.getHeight()));
                Dimension mindGUI = new Dimension(780, 580);
                gui.setMinimumSize(mindGUI);
                gui.setPreferredSize(mindGUI);
                gui.setSize(dGUI);
            }
        });
    }
}
