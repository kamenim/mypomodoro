package org.mypomodoro.gui.todo;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.mypomodoro.util.Labels;

class TabPane extends JTabbedPane {

    public TabPane(ToDoListPanel panel, UnplannedPanel unplannedPanel) {
        add(Labels.getString("Common.Details"), panel.getInformationPanel());
        add(Labels.getString("Common.Comment"), panel.getCommentPanel());
        add(Labels.getString("ToDoListPanel.Overestimation"), panel.getOverestimationPanel());
        add(Labels.getString("ToDoListPanel.Unplanned"), new JScrollPane(unplannedPanel));
    }
}