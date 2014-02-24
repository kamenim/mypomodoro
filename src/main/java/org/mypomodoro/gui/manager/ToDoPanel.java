package org.mypomodoro.gui.manager;

import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

/**
 * ToDos Panel
 *
 */
public class ToDoPanel extends ListPanel {

    public ToDoPanel(AbstractActivities list) {
        super(list);
    }

    @Override
    public void setPanelBorder() {
        String titleToDoList = Labels.getString((org.mypomodoro.gui.ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List")
                + " (" + ToDoList.getListSize() + ")";
        if (org.mypomodoro.gui.ControlPanel.preferences.getAgileMode()
                && ToDoList.getListSize() > 0) {
            titleToDoList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + ToDoList.getList().getStoryPoints();
        }
        setBorder(new TitledBorder(new EtchedBorder(), titleToDoList));
    }
}
