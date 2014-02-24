package org.mypomodoro.gui.todo;

import javax.swing.JTabbedPane;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.reports.export.ExportPanel;
import org.mypomodoro.gui.reports.export.ImportPanel;

import org.mypomodoro.util.Labels;

public class TabPane extends JTabbedPane {

    private static final long serialVersionUID = 20110814L;

    public TabPane(ToDoListPanel panel, UnplannedPanel unplannedPanel) {
        add(Labels.getString("Common.Details"), panel.getInformationPanel());
        add(Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), panel.getCommentPanel());
        add(Labels.getString("ToDoListPanel.Overestimation"), panel.getOverestimationPanel());
        add(Labels.getString("ToDoListPanel.Unplanned"), unplannedPanel);
        add(Labels.getString("ToDoListPanel.Merging"), panel.getMergingPanel());
        ImportPanel importPanel = new ImportPanel(true);
        add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        add(Labels.getString("ReportListPanel.Export"), exportPanel);
    }
}
