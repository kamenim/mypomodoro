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
import org.mypomodoro.gui.todo.TimerPanel;
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

    // Database
    public static final MySQLConfigLoader mySQLconfig = new MySQLConfigLoader(); // load properties
    public static final GoogleConfigLoader googleConfig = new GoogleConfigLoader(); // load properties
    public static final Database database = new Database(); // create database if necessary
    // GUI
    public static PreferencesPanel preferencesPanel;
    public static ActivitiesPanel activitiesPanel;
    public static ToDoPanel toDoPanel;
    public static ReportsPanel reportListPanel;
    public static TabbedPanel chartTabbedPanel;
    public static CreatePanel createPanel;
    public static MyPomodoroView gui;

    // Default font for the application
    public static Font font = new Font("Ebrima", Font.PLAIN, 13);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Does this work?
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {
            // deletes files created with RestartMac()
            new RestartMac(1);
            return;
        }
        // Display GUI
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // Set global font (before initialising the gui)
                setUIFont(new FontUIResource(font.getName(), font.getStyle(), font.getSize()));
                // Substance look and feel not that nice... (enable dependency in pom.xml)  
                /*try {
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
                //}
                // init the gui AFTER setting the UIManager and font
                preferencesPanel = new PreferencesPanel();
                activitiesPanel = new ActivitiesPanel();
                toDoPanel = new ToDoPanel();
                reportListPanel = new ReportsPanel();
                chartTabbedPanel = new TabbedPanel();
                createPanel = new CreatePanel();
                gui = new MyPomodoroView();
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
        });
    }

    // Set default global font for the application
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

    public static void updateComboBoxLists() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TypeList.refresh();
                AuthorList.refresh();
                PlaceList.refresh();
            }
        });
    }
}
