package org.mypomodoro.gui.manager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ToDoList;

/**
 * Manager panel
 *
 */
public class ManagerPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private final ToDoList toDoList = ToDoList.getList();
    private ActivityList activityList = ActivityList.getList();
    private final ToDoPanel todoPane;
    private final ActivitiesPanel activitiesPane;

    public ManagerPanel() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        if (org.mypomodoro.gui.ControlPanel.preferences.getAgileMode()) {
            activityList = activityList.getListIteration(0); // subset of activities : by default, activities with iteration = 0
        }
        activitiesPane = new ActivitiesPanel(activityList);
        todoPane = new ToDoPanel(toDoList);

        activitiesPane.addListMouseListener(new ListMoverMouseListener(
                activitiesPane, todoPane));
        todoPane.addListMouseListener(new ListMoverMouseListener(todoPane,
                activitiesPane));
        add(activitiesPane);
        add(new ControlPanel(activitiesPane, todoPane));
        add(todoPane);

        activitiesPane.setPanelBorder();
        todoPane.setPanelBorder();
    }

    public void refresh() {
        todoPane.update();
        activitiesPane.update();
    }
}
