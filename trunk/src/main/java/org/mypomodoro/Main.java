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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import org.mypomodoro.db.Database;
import org.mypomodoro.db.mysql.MySQLConfigLoader;
import org.mypomodoro.gui.MainPanel;
import org.mypomodoro.gui.SplashScreen;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.util.ProgressBar;
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
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.RestartMac;

/**
 * Main Application Starter
 *
 */
public class Main {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Main.class);

    // Database
    public static final MySQLConfigLoader mySQLconfig = new MySQLConfigLoader(); // load properties
    public static final Database database = new Database(); // create database if necessary
    // Google drive
    public static final GoogleConfigLoader googleConfig = new GoogleConfigLoader(); // load properties
    // GUI    
    public static SplashScreen splashScreen = new SplashScreen();
    public static PreferencesPanel preferencesPanel;
    public static CreatePanel createPanel;
    public static ActivitiesPanel activitiesPanel;
    public static ToDoPanel toDoPanel;
    public static ReportsPanel reportListPanel;
    public static TabbedPanel chartTabbedPanel;
    public static ProgressBar progressBar;
    public static MainPanel gui;
    public static Font font;

    /**
     * Main
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Set font from font file
        // DroidSansMonoSlashed font (Apache licence) to support unicode characters
        // Supported character sets: Western European, Eastern/Central European, Baltic, Cyrillic, Greek and Turkish
        // Supported languages: Simplified and Traditional Chinese, Japanese, Korean, Arabic, Armenian, Ethiopic, Georgian, Hebrew and Thai
        // Tested: chinese (政府派高层), japanese (施する外), arabic (راديو مباشر), hebrew(מוטיבציה לר), hindi (भूत-प्रेत की कहानियाँ)
        // Also tested: russian (Поездка), greek (δημοφιλέστερα), thai (ทำเนียบรัฐบาล), viet (yêu thích nhấ), korean (한국관광공사;)
        // Tested but not working: Tamil
        // http://www.cosmix.org/software/
        try {
            font = Font.createFont(Font.TRUETYPE_FONT,
                    Main.class.getResourceAsStream("/fonts/DroidSansMonoSlashed.ttf")).deriveFont(Font.PLAIN, 15f);
        } catch (FontFormatException ex) {
            font = new JLabel().getFont().deriveFont(Font.PLAIN, 14f);
            logger.error("Ubuntu-R TTF file not found. Replaced with default System font.", ex);
        } catch (IOException ex) {
            font = new JLabel().getFont().deriveFont(Font.PLAIN, 14f);
            logger.error("Ubuntu-R TTF file not found. Replaced with default System font.", ex);
        }
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
                // Substance look and feel not that nice... (enable dependency in pom.xml)  
                /*try {
                 JFrame.setDefaultLookAndFeelDecorated(true);
                 UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel());
                 updateComponentTreeUI(gui);
                 } catch (UnsupportedLookAndFeelException ex) {*/
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                    // cross platform look and feel is used by default by the JVM
                    logger.error(ex.toString());
                } catch (InstantiationException ex) {
                    // cross platform look and feel is used by default by the JVM
                    logger.error(ex.toString());
                } catch (IllegalAccessException ex) {
                    // cross platform look and feel is used by default by the JVM
                    logger.error(ex.toString());
                } catch (UnsupportedLookAndFeelException ex) {
                    // cross platform look and feel is used by default by the JVM
                    logger.error(ex.toString());
                }
                //}
                // Set global font (before intanstiating the components and the gui)
                // This must be done AFTER the setLookAndFeel for the font to be also set on OptionPane dialog... (don't ask)
                setUIFont(new FontUIResource(font.getName(), font.getStyle(), font.getSize()));
                // Set progress bar font (before intanstiating the progress bar)
                UIManager.put("ProgressBar.background", ColorUtil.YELLOW_ROW); // colour of the background // this does not work
                UIManager.put("ProgressBar.foreground", ColorUtil.BLUE_ROW); // colour of progress bar
                UIManager.put("ProgressBar.selectionBackground", ColorUtil.BLACK); // colour of percentage counter on background
                UIManager.put("ProgressBar.selectionForeground", ColorUtil.BLACK); // colour of precentage counter on progress bar
                // init the gui AFTER setting the UIManager and font
                preferencesPanel = new PreferencesPanel();
                createPanel = new CreatePanel();
                activitiesPanel = new ActivitiesPanel();
                toDoPanel = new ToDoPanel();
                reportListPanel = new ReportsPanel();
                chartTabbedPanel = new TabbedPanel();
                progressBar = new ProgressBar();
                gui = new MainPanel();
                // Load combo boxes data (type, author...)
                updateComboBoxLists();
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
                if (org.mypomodoro.gui.preferences.PreferencesPanel.preferences.getAlwaysOnTop()) {
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
