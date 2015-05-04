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
package org.mypomodoro.gui.reports;

import org.mypomodoro.gui.activities.*;
import org.mypomodoro.gui.TableTitlePanel;
import java.awt.Dimension;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.mypomodoro.Main;
import org.mypomodoro.gui.AbstractTable;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.SubTableTitlePanel;
import org.mypomodoro.gui.TabbedPane;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * GUI for viewing what is in the ActivityList. This can be changed later. Right
 * now it uses a TableModel to build the JTable. Table Listeners can be added to
 * save cell edits to the ActivityCollection which can then be saved to the data
 * layer.
 *
 */
public class ReportsPanel extends JPanel implements IListPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension PANE_DIMENSION = new Dimension(800, 200);
    private static final Dimension TABPANE_DIMENSION = new Dimension(800, 50);
    // List pane: title + table + sub-title + sub-table
    private final JPanel listPane = new JPanel();
    // Split pane: list pane + tabbed pane
    private final JSplitPane splitPane;
    // Title panes: title and sub-title    
    private final TableTitlePanel tableTitlePanel;
    private final SubTableTitlePanel subTableTitlePanel;
    // Table panes: table and sub-table
    private final JScrollPane tableScrollPane;
    private final JScrollPane subTableScrollPane;
    // Tabbed pane: details + ...
    private final TabbedPane tabbedPane;
    // Tab panes: details,...
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final EditPanel editPanel = new EditPanel(detailsPanel);
    // Tables
    private ReportsTable currentTable;
    private ReportsTableModel tableModel;
    private final ReportsTable table;
    private final ReportsSubTableModel subTableModel;
    private final ReportsSubTable subTable;

    public ReportsPanel() {
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
        subTableModel = new ReportsSubTableModel();
        tableModel = new ReportsTableModel();
        subTable = new ReportsSubTable(subTableModel, this); // instance this before table
        table = new ReportsTable(tableModel, this);
        currentTable = table;

        // Init scroll panes
        subTableScrollPane = new JScrollPane(subTable);
        tableScrollPane = new JScrollPane(table);

        // Init title and sub title
        tableTitlePanel = new TableTitlePanel(this, table);
        subTableTitlePanel = new SubTableTitlePanel(this, subTable);

        // select first activity of the table so the selection listener gets fired only now that both tables have been instanciated
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        // Add panes of List pane
        addTableTitlePanel();
        addTable();
        addSubTableTitlePanel();

        // Add Split pane
        add(splitPane);
    }

    ////////////////////////////////////////////////
    // TABBED PANE
    ////////////////////////////////////////////////
    protected void initTabbedPane() {
        tabbedPane.setDetailsTabIndex(0);
        tabbedPane.setCommentTabIndex(1);
        tabbedPane.setEditTabIndex(2);
        tabbedPane.setImportTabIndex(3);
        tabbedPane.setExportTabIndex(4);
        tabbedPane.add(Labels.getString("Common.Details"), detailsPanel);
        tabbedPane.add(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        tabbedPane.add(Labels.getString("Common.Edit"), editPanel);
        ImportPanel importPanel = new ImportPanel(this);
        tabbedPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        tabbedPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
    }

    ////////////////////////////////////////////////
    // TITLE
    ////////////////////////////////////////////////
    @Override
    public void addTableTitlePanel() {
        table.setTitle(); // init title
        listPane.add(tableTitlePanel);
    }

    ////////////////////////////////////////////////
    // TABLE
    ////////////////////////////////////////////////
    @Override
    public void addTable() {
        listPane.add(tableScrollPane);
    }

    ////////////////////////////////////////////////
    // SUB TITLE
    ////////////////////////////////////////////////
    @Override
    public void addSubTableTitlePanel() {
        subTable.setTitle(); // init title
        listPane.add(subTableTitlePanel);
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
                tableModel = new ReportsTableModel();
                table.setModel(tableModel);                
                table.setTableHeader();
                table.setColumnModel();
                table.initTabs();
                if (tableModel.getRowCount() > 0) {
                    table.setCurrentSelectedRow(0);
                    table.setRowSelectionInterval(0, 0);
                } else {
                    emptySubTable();
                }
                table.setTitle();
                subTable.setTitle();
            } catch (Exception ex) {
                logger.error("", ex);
            } finally {
                // Stop wait cursor
                WaitCursor.stopWaitCursor();
            }
        }
    }

    public ReportList getList() {
        return ReportList.getList();
    }

    @Override
    public void emptySubTable() {
        subTableModel.setRowCount(0);
        subTable.setParentId(-1);
        subTable.setColumnModel();
        subTable.setTitle();
    }

    @Override
    public ReportsTable getMainTable() {
        return table;
    }

    @Override
    public ReportsTable getCurrentTable() {
        return currentTable;
    }

    @Override
    public void setCurrentTable(AbstractTable table) {
        currentTable = (ReportsTable) table;
    }

    public ReportsSubTable getSubTable() {
        return subTable;
    }

    @Override
    public void delete(Activity activity) {
        getList().delete(activity);
    }

    @Override
    public void deleteAll() {
        getList().deleteAll();
    }

    /*@Override
     public void completeAll() {
     // no use
     }*/
    @Override
    public void addActivity(Activity activity) { // TODO (put in table)
        getList().add(activity);
    }

    @Override
    public void addActivity(Activity activity, Date date, Date dateCompleted) { // TODO (put in table)
        getList().add(activity, date, dateCompleted);
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

    @Override
    public TabbedPane getTabbedPane() {
        return tabbedPane;
    }

    @Override
    public JPanel getListPane() {
        return listPane;
    }

    @Override
    public JSplitPane getSplitPane() {
        return splitPane;
    }

    @Override
    public TableTitlePanel getTableTitlePanel() {
        return tableTitlePanel;
    }

    public SubTableTitlePanel getSubTableTitlePanel() {
        return subTableTitlePanel;
    }

    @Override
    public JScrollPane getTableScrollPane() {
        return tableScrollPane;
    }

    @Override
    public JScrollPane getSubTableScrollPane() {
        return subTableScrollPane;
    }

    @Override
    public void populateSubTable(int parentId) {        
        subTableModel.update(parentId);
        subTable.setParentId(parentId);
        subTable.setColumnModel();
        subTable.setTitle();
        setCurrentTable(table);
    }
}
