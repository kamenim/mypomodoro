package org.mypomodoro;

import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.manager.ManagerPanel;
import org.mypomodoro.gui.reports.ReportListPanel;
import org.mypomodoro.gui.todo.ToDoListPanel;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;

/**
 * Main Application Starter
 * 
 * @author Brian Wetzel
 */
public class Main {
	public static ActivitiesPanel activitiesPanel;
	public static ManagerPanel generatePanel;
	public static ToDoListPanel toDoListPanel;
	public static ReportListPanel reportListPanel;
	public static ReentrantLock datalock = new ReentrantLock();

	public static void updateView() {
		new Thread(new Runnable() {

			public void run() {
				activitiesPanel.refresh();
				toDoListPanel.refresh();
				generatePanel.refresh();
				reportListPanel.refresh();

			}

		}).start();
	}

	public static void updateLists() {
		ActivityList.getList().refresh();
		ToDoList.getList().refresh();
		ReportList.getList().refresh();
	}

	/**
	 * @param args
	 *            the command line arguments
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws FontFormatException,
			IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		Main.activitiesPanel = new ActivitiesPanel();
		Main.generatePanel = new ManagerPanel();
		Main.toDoListPanel = new ToDoListPanel();
		Main.reportListPanel = new ReportListPanel();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setUpAndShowGui();
			}
		});
	}

	private static void setUpAndShowGui() {
		final MyPomodoroView gui = new MyPomodoroView();
		gui.setVisible(true);
		Dimension screenSize = gui.getToolkit().getScreenSize();
		int w = (int) ((screenSize.getWidth() - gui.getSize().width) / 2);
		int h = (int) ((screenSize.getHeight() - gui.getSize().height) / 2);
		gui.setLocation(w, h);
		gui.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent event) {
				gui.setSize(Math.max(650, gui.getWidth()), Math.max(550, gui
						.getHeight()));
			}
		});
	}
}
