package org.mypomodoro;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.mypomodoro.db.Database;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.manager.ManagerPanel;
import org.mypomodoro.gui.reports.ReportListPanel;
import org.mypomodoro.gui.todo.ToDoListPanel;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.RestartMac;

/**
 * Main Application Starter
 * 
 * @author Brian Wetzel
 * @author Phil Karoo
 */
public class Main {

	public static final Database database = new Database();
	public static ControlPanel controlPanel = new ControlPanel();
	public static final ActivitiesPanel activitiesPanel = new ActivitiesPanel();
	public static final ManagerPanel generatePanel = new ManagerPanel();
	public static final ToDoListPanel toDoListPanel = new ToDoListPanel();
	public static final ReportListPanel reportListPanel = new ReportListPanel();
	public static ReentrantLock datalock = new ReentrantLock();

	public static void updateView() {
		new Thread(new Runnable() {

			@Override
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

	public static void updateActivityList() {
		ActivityList.getList().refresh();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {// deletes
																					// files
																					// created
																					// with
																					// RestartMac()
				new RestartMac(1);
			}
		} catch (Exception ex) {
			// Do nothing if the we cannot set a nice ui look and feel
		}
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
		/*
		 * Old fashion way to center the component onscreen Dimension screenSize
		 * = gui.getToolkit().getScreenSize(); int w = (int) ( (
		 * screenSize.getWidth() - gui.getSize().width ) / 2 ); int h = (int) (
		 * ( screenSize.getHeight() - gui.getSize().height ) / 2 );
		 * gui.setLocation(w, h);
		 */
		gui.setLocationRelativeTo(null); // center the component onscreen
		gui.addComponentListener(new java.awt.event.ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent event) {
				Dimension dGUI = new Dimension(Math.max(780, gui.getWidth()),
						Math.max(580, gui.getHeight()));
				Dimension mindGUI = new Dimension(780, 580);
				gui.setMinimumSize(mindGUI);
				gui.setPreferredSize(mindGUI);
				gui.setSize(dGUI);
			}
		});
	}
}