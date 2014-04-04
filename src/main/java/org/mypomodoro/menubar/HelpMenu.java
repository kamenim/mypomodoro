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
package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.MenuSelectionManager;
import org.mypomodoro.Main;

import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.menubar.help.AboutPanel;
import org.mypomodoro.util.BareBonesBrowserLaunch;
import org.mypomodoro.util.Labels;

public class HelpMenu extends JMenu {

    private static final long serialVersionUID = 20110814L;

    public HelpMenu() {
        super(Labels.getString("MenuBar.Help"));
        add(new HelpUserGuide());
        add(new HelpPomodoroMenu());
        add(new JSeparator());
        add(new ReportIssues());
        add(new CheckUpdates());
        add(new JSeparator());
        add(new HelpAbout());
        addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                // do nothing
            }

            @Override
            public void focusLost(FocusEvent e) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
        });
    }

    class HelpUserGuide extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public HelpUserGuide() {
            super(Labels.getString("HelpMenu.Download User Guide"));
            setFont(Main.font);
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JTextField urlField = new JTextField(
                        "http://sourceforge.net/projects/mypomodoro/files/myPomodoro%20" + MyPomodoroView.MYPOMODORO_VERSION);
                BareBonesBrowserLaunch.openURL(urlField.getText().trim());
            }
        }
    }

    class HelpPomodoroMenu extends JMenu {

        private static final long serialVersionUID = 20110814L;

        public HelpPomodoroMenu() {
            super(Labels.getString("HelpMenu.Pomodoro Technique"));
            setFont(Main.font);
            add(new HelpPomodoroTechnique());
            add(new HelpPomodoroBook());
        }
    }

    class HelpPomodoroTechnique extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public HelpPomodoroTechnique() {
            super(Labels.getString("HelpMenu.Pomodoro Technique Official Website"));
            setFont(Main.font);
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JTextField urlField = new JTextField(
                        "http://www.pomodorotechnique.com/");
                BareBonesBrowserLaunch.openURL(urlField.getText().trim());
            }
        }
    }

    class HelpPomodoroBook extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public HelpPomodoroBook() {
            super(Labels.getString("HelpMenu.Pomodoro Technique Official Book"));
            setFont(Main.font);
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                String url = "http://pomodorotechnique.com/book";
                BareBonesBrowserLaunch.openURL(url);
            }
        }
    }

    class ReportIssues extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public ReportIssues() {
            super(Labels.getString("HelpMenu.Report Issues"));
            setFont(Main.font);
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL("http://sourceforge.net/p/mypomodoro/tickets");
            }
        }
    }

    class CheckUpdates extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public CheckUpdates() {
            super(Labels.getString("AboutPanel.Check for Updates"));
            setFont(Main.font);
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL("http://sourceforge.net/projects/mypomodoro/files");
            }
        }
    }

    class HelpAbout extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public HelpAbout() {
            super(Labels.getString("HelpMenu.About"));
            setFont(Main.font);
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutPanel dialog = new AboutPanel(new JDialog(), Labels.getString("HelpMenu.About myPomodoro"));
                if (!org.mypomodoro.gui.PreferencesPanel.preferences.getAlwaysOnTop()) {
                    dialog.setModal(true); // modal except when Main.gui is set to be always on top (won't work)
                }
                dialog.pack();
                dialog.setLocationRelativeTo(Main.gui); // center component on top panel (gui)
                dialog.setVisible(true);
                if (org.mypomodoro.gui.PreferencesPanel.preferences.getAlwaysOnTop()) {
                    dialog.setAlwaysOnTop(true);
                }
            }
        }
    }
}
