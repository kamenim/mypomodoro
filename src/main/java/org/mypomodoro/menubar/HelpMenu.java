package org.mypomodoro.menubar;

import org.mypomodoro.menubar.help.AboutView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.util.BareBonesBrowserLaunch;

public class HelpMenu extends JMenu {

    public HelpMenu() {
        super(ControlPanel.labels.getString("MenuBar.Help"));
        add(new HelpUserGuide());
        add(new HelpPomodoroTechnique());
        add(new HelpPomodoroCheatSheet());
        add(new HelpPomodoroBook());
        add(new HelpAbout());
    }

    class HelpUserGuide extends JMenuItem {

        public HelpUserGuide() {
            super(ControlPanel.labels.getString("HelpMenu.Download User Guide"));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JTextField urlField = new JTextField(
                        "http://mypomodoro.googlecode.com/files/myPomodoro_User_Doc_1.1.pdf");
                BareBonesBrowserLaunch.openURL(urlField.getText().trim());
            }
        }
    }

    class HelpPomodoroTechnique extends JMenuItem {

        public HelpPomodoroTechnique() {
            super(ControlPanel.labels.getString("HelpMenu.The Pomodoro Technique® Website"));
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

    class HelpPomodoroCheatSheet extends JMenuItem {

        public HelpPomodoroCheatSheet() {
            super(ControlPanel.labels.getString("HelpMenu.Download the Pomodoro Technique® Cheat Sheet"));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JTextField urlField = new JTextField(
                        "http://www.pomodorotechnique.com/resources/pomodoro_cheat_sheet.pdf");
                BareBonesBrowserLaunch.openURL(urlField.getText().trim());
            }
        }
    }

    class HelpPomodoroBook extends JMenuItem {

        public HelpPomodoroBook() {
            super(ControlPanel.labels.getString("HelpMenu.Download the Pomodoro Technique® Book"));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL("http://www.pomodorotechnique.com/resources/ThePomodoroTechnique_v1-3.pdf");
            }
        }
    }

    class HelpAbout extends JMenuItem {

        public HelpAbout() {
            super(ControlPanel.labels.getString("HelpMenu.About myPomodoro"));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutView frame = new AboutView(new JFrame(), ControlPanel.labels.getString("HelpMenu.About myPomodoro"));
                frame.setVisible(true);
            }
        }
    }
}