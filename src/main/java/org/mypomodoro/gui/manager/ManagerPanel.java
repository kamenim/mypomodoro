package org.mypomodoro.gui.manager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

/**
 * Manager panel
 *
 */
public class ManagerPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private final ToDoList toDoList = ToDoList.getList();
    private final ActivityList activityList = ActivityList.getList();
    private final ListPanel todoPane;
    private final ListPanel activitiesPane;

    public ManagerPanel() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        String titleActivitiesList = Labels.getString((org.mypomodoro.gui.ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "ActivityListPanel.Activity List")
                + " (" + activityList.getListSize() + ")";
        if (org.mypomodoro.gui.ControlPanel.preferences.getAgileMode()
                && activityList.getListSize() > 0) {
            titleActivitiesList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + activityList.getStoryPoints();
        }
        activitiesPane = new ListPanel(activityList, titleActivitiesList);

        String titleToDoList = Labels.getString((org.mypomodoro.gui.ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List")
                + " (" + toDoList.getListSize() + ")";
        if (org.mypomodoro.gui.ControlPanel.preferences.getAgileMode()
                && toDoList.getListSize() > 0) {
            titleToDoList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + toDoList.getStoryPoints();
        }
        todoPane = new ListPanel(toDoList, titleToDoList);

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
