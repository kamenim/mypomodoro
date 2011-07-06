package org.mypomodoro.gui.todo;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

class TabPane extends JTabbedPane {

    public TabPane(ToDoListPanel panel, UnplannedPanel unplannedPanel) {
        add("Details", panel.getInformationPanel());
        add("Comment", panel.getCommentPanel());
        add("Overestimation", panel.getOverestimationPanel());
        add("Unplanned", new JScrollPane(unplannedPanel));
    }
}