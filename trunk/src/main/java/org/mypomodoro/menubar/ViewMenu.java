package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.mypomodoro.gui.PreferencesPanel;

import org.mypomodoro.gui.MyIcon;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.util.Labels;

//View Menu
public class ViewMenu extends JMenu {

    private static final long serialVersionUID = 20110814L;
    private final MyPomodoroView view;

    public ViewMenu(final MyPomodoroView view) {
        super(Labels.getString("MenuBar.View"));
        this.view = view;
        add(new ActivityListItem());
        add(new ToDoListItem());
        add(new ReportListItem());
        if (PreferencesPanel.preferences.getAgileMode()) {
            add(new BurndownChartItem());
        }
    }

    class ActivityListItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public ActivityListItem() {
            super(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ViewMenu.Activity List"));
            // Adds Keyboard Shortcut Alt-A
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                    ActionEvent.ALT_MASK));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.updateView();
                MyIcon activityListIcon = view.getIconBar().getIcon(1);
                view.getIconBar().highlightIcon(activityListIcon);
                view.setWindow(activityListIcon.getPanel());
                view.setWindow(view.getActivityListPanel());
            }
        }
    }

    class ToDoListItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public ToDoListItem() {
            super(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ViewMenu.ToDo List"));
            // Adds Keyboard Shortcut Alt-T
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                    ActionEvent.ALT_MASK));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.updateView();
                MyIcon toDoListIcon = view.getIconBar().getIcon(2);
                view.getIconBar().highlightIcon(toDoListIcon);
                view.setWindow(toDoListIcon.getPanel());
                view.setWindow(view.getToDoPanel());
            }
        }
    }

    class ReportListItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public ReportListItem() {
            super(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ViewMenu.Report List"));
            // Adds Keyboard Shortcut Alt-R
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                    ActionEvent.ALT_MASK));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.updateView();
                MyIcon reportListIcon = view.getIconBar().getIcon(3);
                view.getIconBar().highlightIcon(reportListIcon);
                view.setWindow(reportListIcon.getPanel());
                view.setWindow(view.getReportListPanel());
            }
        }
    }

    class BurndownChartItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public BurndownChartItem() {
            super(Labels.getString("ViewMenu.Burndown Chart"));
            // Adds Keyboard Shortcut Alt-B
            setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
                    ActionEvent.ALT_MASK));
            addActionListener(new MenuItemListener());
        }

        class MenuItemListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.updateView();
                MyIcon burndownChartIcon = view.getIconBar().getIcon(4);
                view.getIconBar().highlightIcon(burndownChartIcon);
                view.setWindow(burndownChartIcon.getPanel());
            }
        }
    }
}
