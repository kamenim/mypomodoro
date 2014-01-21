package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import org.mypomodoro.Main;

import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.menubar.help.AboutPanel;
import org.mypomodoro.util.BareBonesBrowserLaunch;
import org.mypomodoro.util.Labels;

public class HelpMenu extends JMenu {

    private static final long serialVersionUID = 20110814L;    

    public HelpMenu(final MyPomodoroView view) {
        super(Labels.getString("MenuBar.Help"));        
        add(new HelpUserGuide());
        add(new HelpPomodoroMenu());
        add(new JSeparator());
        add(new ReportIssues());
        add(new CheckUpdates());
        add(new JSeparator());
        add(new HelpAbout());
    }

    class HelpUserGuide extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

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

        private static final long serialVersionUID = 20110814L;

        public HelpPomodoroMenu() {
            super(Labels.getString("HelpMenu.Pomodoro Technique"));
            add(new HelpPomodoroTechnique());
            add(new HelpPomodoroBook());
        }
    }

    class HelpPomodoroTechnique extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public HelpPomodoroTechnique() {
            super(Labels.getString("HelpMenu.Pomodoro Technique Official Website"));
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
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL("https://code.google.com/p/mypomodoro/issues/list");
            }
        }
    }

    class CheckUpdates extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

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

        private static final long serialVersionUID = 20110814L;

        public HelpAbout() {
            super(Labels.getString("HelpMenu.About myPomodoro"));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                AboutPanel dialog = new AboutPanel(new JDialog(), Labels.getString("HelpMenu.About myPomodoro"));
                dialog.pack();
                dialog.setLocationRelativeTo(Main.gui); // center component on top panel (gui)
                dialog.setVisible(true);
                dialog.setModal(true); // modal except when Main.gui is set to be always on top (won't work)
                if (org.mypomodoro.gui.ControlPanel.preferences.getAlwaysOnTop()) {
                    dialog.setAlwaysOnTop(true);
                }
            }
        }
    }
}
