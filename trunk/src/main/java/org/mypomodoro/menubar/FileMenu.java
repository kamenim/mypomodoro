package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.MyPomodoroView;

public class FileMenu extends JMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final MyPomodoroView view;

	public FileMenu(final MyPomodoroView view) {
		super("File");
		this.view = view;
		add(new ControlPanelItem());
		add(new CreateActivityItem());
		add(new ExitItem());
	}

	public class CreateActivityItem extends JMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CreateActivityItem() {
			super("New Activity");
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				view.setWindow(view.getCreatePanel());
			}
		}
	}

	public class ExitItem extends JMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ExitItem() {
			super("Exit");
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}
	}

	class ControlPanelItem extends JMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ControlPanelItem() {
			super("Preferences");
			// Adds Keyboard Shortcut Alt-P
			setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
					ActionEvent.ALT_MASK));
			addActionListener(new MenuItemListener());
		}

		class MenuItemListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				view.setWindow(new ControlPanel(view, view.getTodoListPanel()
						.getPomodoro()));
			}
		}
	}

}
