package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.MyIcon;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.gui.create.CreatePanel;

public class FileMenu extends JMenu {

    private final MyPomodoroView view;

    public FileMenu(final MyPomodoroView view) {
        super("File");
        this.view = view;
        add(new ControlPanelItem());
        add(new CreateActivityItem());
        add(new ExitItem());
    }

    public class CreateActivityItem extends JMenuItem {

        public CreateActivityItem() {
            super("New Activity");
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
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

    public class ExitItem extends JMenuItem {

        public ExitItem() {
            super("Exit");
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }
    }

    class ControlPanelItem extends JMenuItem {

        public ControlPanelItem() {
            super("Preferences");
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
}