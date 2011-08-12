package org.mypomodoro.menubar;

import org.mypomodoro.menubar.help.AboutPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.util.BareBonesBrowserLaunch;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.util.Labels;

public class HelpMenu extends JMenu {
    
    private final MyPomodoroView view;

    public HelpMenu(final MyPomodoroView view) {
        super(Labels.getString("MenuBar.Help"));
        this.view = view;
        add(new HelpUserGuide());
        add(new HelpPomodoroMenu());        
        add(new JSeparator());
        add(new CheckUpdates());
        add(new JSeparator());
        add(new HelpAbout());
    }

    class HelpUserGuide extends JMenuItem {

        public HelpUserGuide() {
            super(Labels.getString("HelpMenu.Download User Guide"));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JTextField urlField = new JTextField(
                        "http://mypomodoro.googlecode.com/files/myPomodoro_User_Doc_" + MyPomodoroView.MYPOMODORO_VERSION + ".pdf");
                BareBonesBrowserLaunch.openURL(urlField.getText().trim());
            }
        }
    }
    
    class HelpPomodoroMenu extends JMenu {
        
        public HelpPomodoroMenu() {
            super(Labels.getString("HelpMenu.The Pomodoro Technique"));
            add(new HelpPomodoroTechnique());
            add(new HelpPomodoroCheatSheet());
            add(new HelpPomodoroBook());            
        }
    }

    class HelpPomodoroTechnique extends JMenuItem {

        public HelpPomodoroTechnique() {
            super(Labels.getString("HelpMenu.The Pomodoro Technique Website"));
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
            super(Labels.getString("HelpMenu.Download the Pomodoro Technique Cheat Sheet"));
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
            super(Labels.getString("HelpMenu.Download the Pomodoro Technique Book"));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL("http://www.pomodorotechnique.com/resources/ThePomodoroTechnique_v1-3.pdf");
            }
        }
    }
    
    class CheckUpdates extends JMenuItem {

        public CheckUpdates() {
            super(Labels.getString("AboutPanel.Check for Updates"));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL("https://code.google.com/p/mypomodoro/downloads/list");
            }
        }
    }

    class HelpAbout extends JMenuItem {

        public HelpAbout() {
            super(Labels.getString("HelpMenu.About myPomodoro"));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutPanel frame = new AboutPanel(new JFrame(), ControlPanel.labels.getString("HelpMenu.About myPomodoro"));
                frame.setModal(true); // always on top                
                frame.setLocationRelativeTo(view); // center component on top panel (MyPomodoroView)
                frame.setVisible(true);               
            }
        }
    }
}