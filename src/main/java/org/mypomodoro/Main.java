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
package org.mypomodoro;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import org.mypomodoro.db.Database;
import org.mypomodoro.db.mysql.MySQLConfigLoader;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.burndownchart.TabbedPanel;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.gui.create.list.AuthorList;
import org.mypomodoro.gui.create.list.PlaceList;
import org.mypomodoro.gui.create.list.TypeList;
import org.mypomodoro.gui.export.google.GoogleConfigLoader;
import org.mypomodoro.gui.reports.ReportsPanel;
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

    // Default font for the application
    public static Font font = new Font("Ebrima", Font.PLAIN, 13);

    public static final MySQLConfigLoader mySQLconfig = new MySQLConfigLoader(); // load properties
    public static final GoogleConfigLoader googleConfig = new GoogleConfigLoader(); // load properties
    public static final Database database = new Database();
    public static final PreferencesPanel preferencesPanel = new PreferencesPanel();
    public static final ActivitiesPanel activitiesPanel = new ActivitiesPanel();
    public static final ToDoPanel toDoPanel = new ToDoPanel();
    public static final ReportsPanel reportListPanel = new ReportsPanel();
    public static final TabbedPanel chartTabbedPanel = new TabbedPanel();
    public static final CreatePanel createPanel = new CreatePanel();
    public static final ReentrantLock datalock = new ReentrantLock();
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
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ActivityList.getList().refresh();
                ToDoList.getList().refresh();
                ReportList.getList().refresh();
            }
        });
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
        // Set font for dialog boxes
        setUIFont(new FontUIResource(font.getName(), font.getStyle(), font.getSize()));
        // Set font for the containers and their components
        changeFont(gui, font);
        changeFont(preferencesPanel, font);
        changeFont(activitiesPanel, font);
        changeFont(toDoPanel, font);
        changeFont(reportListPanel, font);
        changeFont(chartTabbedPanel, font);
        changeFont(createPanel, font);

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

    // Default font
    public static void setUIFont(FontUIResource f) {
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value != null && value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    public static void changeFont(Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                changeFont(child, font);
            }
        }
    }
}
