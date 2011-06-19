package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.mypomodoro.gui.MyIcon;

import org.mypomodoro.gui.MyPomodoroView;

//View Menu
public class ViewMenu extends JMenu {
	private final MyPomodoroView view;

	public ViewMenu(final MyPomodoroView view) {
		super("View");
		this.view = view;
		add(new ActivityListItem());
		add(new GenerateListItem());
		add(new ToDoListItem());
		add(new ReportListItem());
	}

	class ActivityListItem extends JMenuItem {
		public ActivityListItem() {
			super("Activity List");
			// Adds Keyboard Shortcut Alt-A
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
            @Override
			public void actionPerformed(ActionEvent e) {
				view.updateLists();
                MyIcon activityListIcon = view.getIconBar().getIcon(1);
                view.getIconBar().highlightIcon(activityListIcon);
				view.setWindow(activityListIcon.getPanel());
				view.setWindow(view.getActivityListPanel());
			}
		}
	}

	class ReportListItem extends JMenuItem {
		public ReportListItem() {
			super("Report List");
			// Adds Keyboard Shortcut Alt-R
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
            @Override
			public void actionPerformed(ActionEvent e) {
				view.updateLists();
                MyIcon reportListIcon = view.getIconBar().getIcon(4);
                view.getIconBar().highlightIcon(reportListIcon);
				view.setWindow(reportListIcon.getPanel());
				view.setWindow(view.getReportListPanel());
			}
		}
	}

	class GenerateListItem extends JMenuItem {
		public GenerateListItem() {
			super("Manager");
			// Adds Keyboard Shortcut Alt-M
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
            @Override
			public void actionPerformed(ActionEvent e) {
				view.updateLists();
                MyIcon generateIcon = view.getIconBar().getIcon(2);
                view.getIconBar().highlightIcon(generateIcon);
				view.setWindow(generateIcon.getPanel());
				view.setWindow(view.getGeneratePanel());
			}
		}
	}

	class ToDoListItem extends JMenuItem {
		public ToDoListItem() {
			super("ToDo List");
			// Adds Keyboard Shortcut Alt-T
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
            @Override
			public void actionPerformed(ActionEvent e) {
				view.updateLists();
                MyIcon toDoListIcon = view.getIconBar().getIcon(3);
                view.getIconBar().highlightIcon(toDoListIcon);
				view.setWindow(toDoListIcon.getPanel());
                view.setWindow(view.getToDoListPanel());
			}
		}
	}

}