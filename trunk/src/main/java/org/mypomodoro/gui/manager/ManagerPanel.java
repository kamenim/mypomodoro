package org.mypomodoro.gui.manager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ToDoList;

/**
 * 
 * @author Brian Wetzel 
 * @author Phil Karoo
 */
public class ManagerPanel extends JPanel {

    private final ToDoList toDoList = ToDoList.getList();
    private final ActivityList activityList = ActivityList.getList();
    private final ListPane todoPane;
    private final ListPane activitiesPane;

    public ManagerPanel() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        activitiesPane = new ListPane(activityList, org.mypomodoro.gui.ControlPanel.labels.getString("ActivityListPanel.Activity List"));
        todoPane = new ListPane(toDoList, org.mypomodoro.gui.ControlPanel.labels.getString("ToDoListPanel.ToDo List"));

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