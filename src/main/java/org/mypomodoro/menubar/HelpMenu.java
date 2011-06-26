package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import org.mypomodoro.util.BareBonesBrowserLaunch;

public class HelpMenu extends JMenu {

    public HelpMenu() {
        super("Help");
        add(new HelpUserGuide());
        add(new HelpPomodoroTechnique());
        add(new HelpPomodoroCheatSheet());
        add(new HelpPomodoroBook());
    }

    class HelpUserGuide extends JMenuItem {

        public HelpUserGuide() {
            super("Download User Guide");
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JTextField urlField = new JTextField(
                        "http://mypomodoro.googlecode.com/files/myPomodoro_User_Doc.pdf");
                BareBonesBrowserLaunch.openURL(urlField.getText().trim());
            }
        }
    }

    class HelpPomodoroTechnique extends JMenuItem {

        public HelpPomodoroTechnique() {
            super("The Pomodoro Technique Website");
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
            super("Download The Pomodoro Technique Cheat Sheet");
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
            super("Download The Pomodoro Technique Book");
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL("http://www.pomodorotechnique.com/resources/ThePomodoroTechnique_v1-3.pdf");
            }
        }
    }
}