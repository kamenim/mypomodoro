package org.mypomodoro.gui.todo;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.mypomodoro.gui.ControlPanel;

class TabPane extends JTabbedPane {

    public TabPane(ToDoListPanel panel, UnplannedPanel unplannedPanel) {
        add(ControlPanel.labels.getString("Common.Details"), panel.getInformationPanel());
        add(ControlPanel.labels.getString("Common.Comment"), panel.getCommentPanel());
        add(ControlPanel.labels.getString("ToDoListPanel.Overestimation"), panel.getOverestimationPanel());
        add(ControlPanel.labels.getString("ToDoListPanel.Unplanned"), new JScrollPane(unplannedPanel));
    }
}