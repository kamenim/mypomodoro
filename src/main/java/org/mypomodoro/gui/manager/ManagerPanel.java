package org.mypomodoro.gui.manager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ToDoList;

/**
 * 
 * @author Brian Wetzel
 */
public class ManagerPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ToDoList toDoList = ToDoList.getList();
	private final ActivityList activityList = ActivityList.getList();
	private final ListPane todoPane;
	private final ListPane activitiesPane;

	public ManagerPanel() {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		activitiesPane = new ListPane(activityList, "Activity List");
		todoPane = new ListPane(toDoList, "Todo List");

		activitiesPane.addListMouseListener(new ListMoverMouseListener(
				activitiesPane, todoPane));
		todoPane.addListMouseListener(new ListMoverMouseListener(todoPane,
				activitiesPane));

		add(activitiesPane);
		add(new ControlPanel(activitiesPane, todoPane));
		add(todoPane);
	}

	public void refresh() {
		todoPane.update();
		activitiesPane.update();
	}
}
