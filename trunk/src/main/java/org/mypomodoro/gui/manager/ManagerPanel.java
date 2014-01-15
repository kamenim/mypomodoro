package org.mypomodoro.gui.manager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

/**
 * 
 * @author Brian Wetzel
 * @author Phil Karoo
 */
public class ManagerPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private final ToDoList toDoList = ToDoList.getList();
    private final ActivityList activityList = ActivityList.getList();
    private final ListPane todoPane;
    private final ListPane activitiesPane;

    public ManagerPanel() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        
        activitiesPane = new ListPane(activityList,
                Labels.getString("ActivityListPanel.Activity List"));
        todoPane = new ListPane(toDoList,
                Labels.getString("ToDoListPanel.ToDo List"));        

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