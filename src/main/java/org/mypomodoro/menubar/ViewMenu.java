package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.mypomodoro.gui.MyPomodoroView;

//View Menu
public class ViewMenu extends JMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ActivityListItem() {
			super("Activity List");
			// Adds Keyboard Shortcut Alt-A
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				view.updateLists();
				view.setWindow(view.getActivityListPanel());
			}
		}
	}

	class ReportListItem extends JMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ReportListItem() {
			super("Report List");
			// Adds Keyboard Shortcut Alt-R
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				view.updateLists();
				view.setWindow(view.getReportListPanel());
			}
		}
	}

	class GenerateListItem extends JMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public GenerateListItem() {
			super("Manager");
			// Adds Keyboard Shortcut Alt-M
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				view.updateLists();
				view.setWindow(view.getGeneratePanel());
			}
		}
	}

	class ToDoListItem extends JMenuItem {
		

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ToDoListItem() {
			super("ToDo List");
			// Adds Keyboard Shortcut Alt-T
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				view.setWindow(view.getToDoListPanel());
				view.updateLists();
			}
		}
	}

}