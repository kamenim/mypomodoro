/* 
 * Copyright (C) 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.gui.activities;

import org.mypomodoro.gui.TableTitlePanel;
import java.awt.Dimension;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import org.mypomodoro.Main;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.SubTableTitlePanel;
import org.mypomodoro.gui.TabbedPane;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * GUI for viewing what is in the ActivityList. This can be changed later. Right
 * now it uses a TableModel to build the JTable. Table Listeners can be added to
 * save cell edits to the ActivityCollection which can then be saved to the data
 * layer.
 *
 */
public class ActivitiesPanel extends JPanel implements IListPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension PANE_DIMENSION = new Dimension(800, 200);
    private static final Dimension TABPANE_DIMENSION = new Dimension(800, 50);
    // List pane: title + table + sub-title + sub-table
    private final JPanel listPane = new JPanel();
    // Split pane: list pane + tabbed pane
    private final JSplitPane splitPane;
    // Title panes: title and sub-title    
    private final TableTitlePanel tableTitlePanel;
    private final ActivitiesSubTableTitlePanel subTableTitlePanel;
    // Table panes: table and sub-table
    private final JScrollPane tableScrollPane;
    private final JScrollPane subTableScrollPane;
    // Tabbed pane: details + ...
    private final TabbedPane tabbedPane;
    // Tab panes: details,...
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final EditPanel editPanel = new EditPanel(this, detailsPanel);
    private final MergingPanel mergingPanel = new MergingPanel(this);
    // Tables
    private final ActivitiesTableModel tableModel;
    private final ActivitiesTable table;
    private final ActivitiesSubTableModel subTableModel;
    private final ActivitiesSubTable subTable;
    // Selected row
    private int currentSelectedRow = 0;

    public ActivitiesPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Init List pane
        listPane.setMinimumSize(PANE_DIMENSION);
        listPane.setPreferredSize(PANE_DIMENSION);
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS)); 

        // Init Tabbed pane        
        tabbedPane = new TabbedPane(this);
        tabbedPane.setMinimumSize(TABPANE_DIMENSION);
        tabbedPane.setPreferredSize(TABPANE_DIMENSION);
        initTabbedPane(); 

        // Init Split pane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPane, tabbedPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setDividerSize(0); // remove divider by hiding it

        // Init table and sub table (data model and rendering)
        subTableModel = new ActivitiesSubTableModel();
        tableModel = new ActivitiesTableModel();
        subTable = new ActivitiesSubTable(subTableModel, this); // instance this before table
        table = new ActivitiesTable(tableModel, this);
        // Init scroll panes
        subTableScrollPane = new JScrollPane(subTable);
        tableScrollPane = new JScrollPane(table);
        // Init title and sub title
        tableTitlePanel = new TableTitlePanel(this, table);
        subTableTitlePanel = new ActivitiesSubTableTitlePanel(this, subTable);
        // select first activity of the table so the selection listener gets fired only now that both tables have been instanciated
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        // Add panes of List pane
        addTableTitlePanel();
        addTable();
        addSubTableTitlePanel();
        addSubTable();

        // Add Split pane
        add(splitPane);
    }

    ////////////////////////////////////////////////
    // TABBED PANE
    ////////////////////////////////////////////////
    private void initTabbedPane() {
        tabbedPane.add(Labels.getString("Common.Details"), detailsPanel);
        tabbedPane.add(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        tabbedPane.add(Labels.getString("Common.Edit"), editPanel);
        tabbedPane.add(Labels.getString("ToDoListPanel.Merge"), mergingPanel);
        ImportPanel importPanel = new ImportPanel(this);
        tabbedPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        tabbedPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);               
    }

    ////////////////////////////////////////////////
    // TITLE
    ////////////////////////////////////////////////
    public void addTableTitlePanel() {
        table.setTitle();
        listPane.add(tableTitlePanel);
    }

    ////////////////////////////////////////////////
    // TABLE
    ////////////////////////////////////////////////
    public void addTable() {
        listPane.add(tableScrollPane);
    }

    ////////////////////////////////////////////////
    // SUB TITLE
    ////////////////////////////////////////////////
    public void addSubTableTitlePanel() {
        subTable.setTitle();
        listPane.add(subTableTitlePanel);
    }

    ////////////////////////////////////////////////
    // SUB TABLE
    ////////////////////////////////////////////////
    public void addSubTable() {

    }

    ////////////////////////////////////////////////
    // REFRESH
    ////////////////////////////////////////////////
    @Override
    public void refresh() {
        refresh(false);
    }

    @Override
    public void refresh(boolean fromDatabase) {
        if (!WaitCursor.isStarted()) {
            // Start wait cursor
            WaitCursor.startWaitCursor();
            try {
                if (fromDatabase) {
                    getList().refresh();
                }
                tableModel.setDataVector(getList());
                table.init();
                if (table.getRowCount() > 0) {
                    table.setCurrentSelectedRow(0);
                    table.setRowSelectionInterval(0, 0);
                } else {
                    emptySubTable();
                }
            } catch (Exception ex) {
                logger.error("", ex);
            } finally {
                // Stop wait cursor
                WaitCursor.stopWaitCursor();
            }
        }
    }

    public ActivityList getList() {
        return ActivityList.getList();
    }

    public void emptySubTable() {
        subTableModel.setRowCount(0);
        subTable.setParentId(-1);
        subTable.init();
        subTable.setTitle();
    }

    @Override
    public void setPanelBorder() {
    }

    @Override
    public ActivitiesTable getTable() {
        return table;
    }

    public ActivitiesSubTable getSubTable() {
        return subTable;
    }

    @Override
    public int getIdKey() {
        return AbstractTableModel.ACTIVITYID_COLUMN_INDEX;
    }

    @Override
    public void removeRow(int rowIndex) {
    }

    @Override
    public void insertRow(Activity activity) {
    }

    @Override
    public void move(Activity activity) {
        getList().move(activity);
    }

    @Override
    public void moveAll() {
        // no use
    }

    @Override
    public Activity getActivityById(int id) {
        return getList().getById(id);
    }

    @Override
    public void delete(Activity activity) {
        getList().delete(activity);
    }

    @Override
    public void deleteAll() {
        getList().deleteAll();
    }

    @Override
    public void complete(Activity activity) {
        // no use
    }

    @Override
    public void completeAll() {
        // no use
    }

    @Override
    public void addActivity(Activity activity) {
        getList().add(activity);
    }

    @Override
    public void addActivity(Activity activity, Date date, Date dateCompleted) {
        getList().add(activity, date, dateCompleted);
    }

    @Override
    public void saveComment(String comment) {
        if (table.getSelectedRowCount() == 1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
            Activity selectedActivity = getList().getById(id);
            if (selectedActivity != null) {
                selectedActivity.setNotes(comment);
                selectedActivity.databaseUpdateComment();
            }
        }
    }

    public void setCurrentSelectedRow(int row) {
        currentSelectedRow = row;
    }

    public void showCurrentSelectedRow() {
        table.scrollRectToVisible(table.getCellRect(currentSelectedRow, 0, true));
    }

    /////////////////// NEW
    public DetailsPanel getDetailsPanel() {
        return detailsPanel;
    }

    public CommentPanel getCommentPanel() {
        return commentPanel;
    }

    public EditPanel getEditPanel() {
        return editPanel;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JPanel getListPane() {
        return listPane;
    }
    
    @Override
    public JSplitPane getSplitPane() {
        return splitPane;
    }    

    public TableTitlePanel getTableTitlePanel() {
        return tableTitlePanel;
    }

    public SubTableTitlePanel getSubTableTitlePanel() {
        return subTableTitlePanel;
    }

    public JScrollPane getTableScrollPane() {
        return tableScrollPane;
    }

    public JScrollPane getSubTableScrollPane() {
        return subTableScrollPane;
    }

    public void populateSubTable(int parentId) {
        subTableModel.setDataVector(ActivityList.getSubTaskList(parentId));
        subTable.setParentId(parentId);
        subTable.init();
        subTable.setTitle();
    }
}
