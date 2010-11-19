package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.mypomodoro.gui.MyPomodoroView;

import db.Database;

public class TestMenu extends JMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TestMenu(final MyPomodoroView view) {
		super("Data");
		add(new ResetDataItem(view));
		add(new TestDataItem(view));
	}

	// resets all the data files.
	class ResetDataItem extends JMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ResetDataItem(final MyPomodoroView view) {
			super("Clear All Data");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Database().resetData();
					view.updateLists();
				}
			});
		}
	}

	class TestDataItem extends JMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TestDataItem(final MyPomodoroView view) {
			super("Populate Test Data");
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Database().createTestData();
					view.updateLists();
				}
			});
		}
	}
}
