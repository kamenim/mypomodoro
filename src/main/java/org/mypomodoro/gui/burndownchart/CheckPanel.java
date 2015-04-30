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
package org.mypomodoro.gui.burndownchart;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;
import org.mypomodoro.gui.AbstractTable;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.TabbedPane;
import org.mypomodoro.gui.TableTitlePanel;
import org.mypomodoro.gui.activities.CommentPanel;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * GUI for viewing the Chart List.
 *
 */
public class CheckPanel extends JPanel implements IListPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension PANE_DIMENSION = new Dimension(700, 200);
    private static final Dimension TABPANE_DIMENSION = new Dimension(700, 50);
    private static final Dimension CREATEBUTTON_DIMENSION = new Dimension(100, 250);
    // List pane: title + table + sub-title + sub-table
    private final JPanel listPane = new JPanel();
    // Split pane: list pane + tabbed pane
    private final JSplitPane splitPane;
    // Title panes: title and sub-title    
    private final TableTitlePanel tableTitlePanel;
    // Table panes: table and sub-table
    private final JScrollPane tableScrollPane;
    // Tabbed pane: details + ...
    private final TabbedPane tabbedPane;
    // Tab panes: details,...
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    // Tables
    private CheckTableModel tableModel;
    private final CheckTable table;
    // Selected row
    private int currentSelectedRow = 0;

    private final JTabbedPane headTabbedPane;
    private final CreateChart chart;

    public CheckPanel(JTabbedPane headTabbedPane, CreateChart chart) {

        this.headTabbedPane = headTabbedPane;
        this.chart = chart;

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

        // Init table (data model and rendering)
        tableModel = new CheckTableModel();
        table = new CheckTable(tableModel, this);

        // Init scroll panes
        tableScrollPane = new JScrollPane(table);

        // Init title
        tableTitlePanel = new TableTitlePanel(this, table);

        // select first activity of the table so the selection listener gets fired only now that both tables have been instanciated
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        // Add panes of List pane
        addTableTitlePanel();
        addTable();

        // Add Split pane
        add(splitPane);

        // Create button
        addCreateButton();
    }

    ////////////////////////////////////////////////
    // TABBED PANE
    ////////////////////////////////////////////////
    protected void initTabbedPane() {
        tabbedPane.setDetailsTabIndex(0);
        tabbedPane.setCommentTabIndex(1);
        tabbedPane.setExportTabIndex(2);
        tabbedPane.add(Labels.getString("Common.Details"), detailsPanel);
        tabbedPane.add(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
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
        // not used
    }

    private void addCreateButton() {
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(splitPane, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.weightx = 0.1;
        JButton createButton = new DefaultButton(Labels.getString("BurndownChartPanel.Create"));
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!WaitCursor.isStarted()) {
                    if (getList().size() > 0) {
                        chart.create();
                        headTabbedPane.setEnabledAt(3, true);
                        headTabbedPane.setSelectedIndex(3);
                    }
                }
            }
        });
        createButton.setMinimumSize(CREATEBUTTON_DIMENSION);
        createButton.setMaximumSize(CREATEBUTTON_DIMENSION);
        createButton.setPreferredSize(CREATEBUTTON_DIMENSION);
        add(createButton, gbc);
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
                tableModel = new CheckTableModel();
                table.setModel(tableModel);
                table.init();
                table.setTableHeader();
                if (tableModel.getRowCount() > 0) {
                    table.setCurrentSelectedRow(0);
                    table.setRowSelectionInterval(0, 0);
                }
                table.setTitle();
            } catch (Exception ex) {
                logger.error("", ex);
            } finally {
                // Stop wait cursor
                WaitCursor.stopWaitCursor();
            }
        }
    }

    public ChartList getList() {
        return ChartList.getList();
    }

    @Override
    public CheckTable getMainTable() {
        return table; // not used - only one table
    }

    @Override
    public CheckTable getCurrentTable() {
        return table;
    }

    @Override
    public void setCurrentTable(AbstractTable table) {
        // not used - onle one table
    }

    @Override
    public void delete(Activity activity) {
        // not used
    }

    @Override
    public void deleteAll() {
        // not used
    }

    /*@Override
     public void completeAll() {
     // no use
     }*/
    @Override
    public void addActivity(Activity activity) {
        // not used
    }

    @Override
    public void addActivity(Activity activity, Date date, Date dateCompleted) {
        // not used
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

    /////////////////// NEW
    public DetailsPanel getDetailsPanel() {
        return detailsPanel;
    }

    public CommentPanel getCommentPanel() {
        return commentPanel;
    }

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

    @Override
    public JScrollPane getTableScrollPane() {
        return tableScrollPane;
    }

    @Override
    public JScrollPane getSubTableScrollPane() {
        return null; // not used
    }
}
