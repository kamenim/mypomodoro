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
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import org.mypomodoro.Main;

import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.gui.MyIcon;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.util.Labels;

public class FileMenu extends JMenu {

    private static final long serialVersionUID = 20110814L;
    private final MyPomodoroView view;

    public FileMenu(final MyPomodoroView view) {
        super(Labels.getString("MenuBar.File"));
        this.view = view;
        add(new ControlPanelItem());
        add(new CreateActivityItem());
        add(new JSeparator());
        add(new ExitItem());
    }

    class ControlPanelItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

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
                view.setWindow(new PreferencesPanel());
            }
        }
    }

    public class CreateActivityItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

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

    public class ExitItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public ExitItem() {
            super(Labels.getString("FileMenu.Exit"));
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                String title = Labels.getString("FileMenu.Exit myPomodoro");
                String message = Labels.getString("FileMenu.Are you sure to exit myPomodoro?");
                int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                        title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (reply == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        }
    }
}
