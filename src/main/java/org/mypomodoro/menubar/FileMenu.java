package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.MyIcon;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.Restart;

public class FileMenu extends JMenu {

    private final MyPomodoroView view;

    public FileMenu(final MyPomodoroView view) {
        super(Labels.getString("MenuBar.File"));
        this.view = view;
        add(new ControlPanelItem());
        add(new CreateActivityItem());
        add(new JSeparator());
        add(new RestartItem());
        add(new JSeparator());
        add(new ExitItem());
    }

    class ControlPanelItem extends JMenuItem {

        public ControlPanelItem() {
            super(Labels.getString("FileMenu.Preferences"));
            // Adds Keyboard Shortcut Alt-P
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                    ActionEvent.ALT_MASK));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                MyIcon selectedIcon = view.getIconBar().getSelectedIcon();
                if (selectedIcon != null) {
                    view.getIconBar().unHighlightIcon(selectedIcon);
                    view.setWindow(selectedIcon.getPanel());
                }
                view.setWindow(new ControlPanel());
            }
        }
    }

    public class CreateActivityItem extends JMenuItem {

        public CreateActivityItem() {
            super(Labels.getString("FileMenu.New Activity"));
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                    ActionEvent.ALT_MASK));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                MyIcon ii = view.getIconBar().getIcon(0);
                view.getIconBar().highlightIcon(ii);
                view.setWindow(ii.getPanel());
                CreatePanel createPanel = view.getCreatePanel();
                createPanel.clearForm();
                view.setWindow(createPanel);
            }
        }
    }

    public class RestartItem extends JMenuItem {

        public RestartItem() {
            super(Labels.getString("Common.Restart"));
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                    ActionEvent.ALT_MASK));
            addActionListener(new RestartItemListener());
        }

        class RestartItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Restart.restartApplication(null);
                } catch (IOException ex) {
                }
            }
        }
    }

    public class ExitItem extends JMenuItem {

        public ExitItem() {
            super(Labels.getString("FileMenu.Exit"));
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }
    }
}