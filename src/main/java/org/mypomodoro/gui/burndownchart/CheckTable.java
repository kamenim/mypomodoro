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

import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import org.mypomodoro.Main;
import org.mypomodoro.db.mysql.MySQLConfigLoader;
import org.mypomodoro.gui.AbstractTable;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ColumnResizer;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Table for activities
 *
 */
public class CheckTable extends AbstractTable {

    private final CheckPanel panel;

    public CheckTable(final CheckTableModel model, final CheckPanel panel) {
        super(model, panel);

        this.panel = panel;

        setTableHeader();

        getSelectionModel().addListSelectionListener(new AbstractListSelectionListener() {

            @Override
            public void customValueChanged(ListSelectionEvent e) {
                //System.err.println("method name = " + Thread.currentThread().getStackTrace()[1].getMethodName());
                int selectedRowCount = getSelectedRowCount();
                if (selectedRowCount > 0) {
                    // See AbstractActivitiesTable for reason to set WHEN_FOCUSED here
                    setInputMap(JTable.WHEN_FOCUSED, im);

                    if (selectedRowCount > 1) { // multiple selection
                        // diactivate/gray out unused tabs
                        panel.getTabbedPane().disableCommentTab();
                        if (panel.getTabbedPane().getSelectedIndex() == panel.getTabbedPane().getCommentTabIndex()) {
                            panel.getTabbedPane().setSelectedIndex(0); // switch to details panel
                        }
                        currentSelectedRow = getSelectedRows()[0]; // always selecting the first selected row (otherwise removeRow will fail)
                        // Display info (list of selected tasks)                            
                        showDetailsForSelectedRows();
                        // populate subtable
                        emptySubTable();
                    } else if (selectedRowCount == 1) {
                        // activate all panels
                        for (int index = 0; index < panel.getTabbedPane().getTabCount(); index++) {
                            panel.getTabbedPane().setEnabledAt(index, true);
                        }
                        if (panel.getTabbedPane().getTabCount() > 0) { // at start-up time not yet initialised (see constructor)
                            panel.getTabbedPane().setSelectedIndex(panel.getTabbedPane().getSelectedIndex()); // switch to selected panel
                        }
                        currentSelectedRow = getSelectedRow();
                        showCurrentSelectedRow(); // when sorting columns, focus on selected row
                        // Display details                           
                        showInfoForSelectedRow();
                        // populate subtable
                        populateSubTable();
                    }
                    setTitle();
                }
            }
        });
    }

    @Override
    public CheckTableModel getModel() {
        return (CheckTableModel) super.getModel();
    }

    @Override
    protected void init() {
        // set custom render for dates
        getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setCellRenderer(new DateRenderer()); // date (custom renderer)
        getColumnModel().getColumn(AbstractTableModel.TITLE_COLUMN_INDEX).setCellRenderer(new ToolTipRenderer()); // title
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setCellRenderer(new ToolTipRenderer()); // type
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setCellRenderer(new EstimatedCellRenderer()); // estimated
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setCellRenderer(new StoryPointsCellRenderer()); // story points
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setCellRenderer(new IterationCellRenderer()); // iteration        

        // hide story points and iteration in 'classic' mode
        if (!Main.preferences.getAgileMode()) {
            getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMaxWidth(0);
            getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMinWidth(0);
            getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setPreferredWidth(0);
            getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMaxWidth(0);
            getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMinWidth(0);
            getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setPreferredWidth(0);
        } else {
            // Set width of columns story points, iteration
            getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMaxWidth(40);
            getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMinWidth(40);
            getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setPreferredWidth(40);
            getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMaxWidth(40);
            getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMinWidth(40);
            getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setPreferredWidth(40);
        }
        // hide unplanned in Agile mode
        if (Main.preferences.getAgileMode()) {
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMaxWidth(0);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMinWidth(0);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setPreferredWidth(0);
        } else {
            // Set width of columns Unplanned and date
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMaxWidth(30);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMinWidth(30);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setPreferredWidth(30);
        }
        // Set width of column Date
        getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setMaxWidth(90);
        getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setMinWidth(90);
        getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setPreferredWidth(90);
        // Set width of column estimated
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setMaxWidth(80);
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setMinWidth(80);
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setPreferredWidth(80);
        // Set min width of column type
        //getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setMaxWidth(200);
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setMinWidth(100);
        //getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setPreferredWidth(200);
        // hide priority, DiffI and DiffII
        getColumnModel().getColumn(AbstractTableModel.PRIORITY_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.PRIORITY_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.PRIORITY_COLUMN_INDEX).setPreferredWidth(0);
        getColumnModel().getColumn(AbstractTableModel.DIFFI_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.DIFFI_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.DIFFI_COLUMN_INDEX).setPreferredWidth(0);
        getColumnModel().getColumn(AbstractTableModel.DIFFII_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.DIFFII_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.DIFFII_COLUMN_INDEX).setPreferredWidth(0);
        // hide ID column
        getColumnModel().getColumn(AbstractTableModel.ACTIVITYID_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.ACTIVITYID_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.ACTIVITYID_COLUMN_INDEX).setPreferredWidth(0);
        // enable sorting
        if (getModel().getRowCount() > 0) {
            setAutoCreateRowSorter(true);
        }

        initTabs();

        // Make sure column title will fit long titles
        ColumnResizer.adjustColumnPreferredWidths(this);
        revalidate();
    }

    @Override
    protected void showInfo(int activityId) {
        Activity activity = getActivityById(activityId);
        panel.getDetailsPanel().selectInfo(activity);
        panel.getDetailsPanel().showInfo();
        //panel.getDetailsPanel().showInfo(this);
        panel.getCommentPanel().showInfo(activity);
        // set table for export panel
        //panel.getExportPanel().setTable(this); TODO
    }

    @Override
    protected void showDetailsForSelectedRows() {
        String info = "";
        int[] rows = getSelectedRows();
        for (int row : rows) {
            Integer id = getActivityIdFromRowIndex(row);
            info += getList().getById(id).getName() + "<br>";
        }
        panel.getDetailsPanel().showInfo(info);
        //panel.getDetailsPanel().showInfo(info, this);
    }

    @Override
    protected ChartList getList() {
        return ChartList.getList();
    }

    @Override
    protected ChartList getTableList() {
        return getList();
    }

    @Override
    protected void setTableHeader() {
        String[] columnToolTips = AbstractTableModel.COLUMN_NAMES.clone();
        columnToolTips[AbstractTableModel.UNPLANNED_COLUMN_INDEX] = Labels.getString("Common.Unplanned");
        columnToolTips[AbstractTableModel.DATE_COLUMN_INDEX] = Labels.getString("Common.Date completed");
        columnToolTips[AbstractTableModel.ESTIMATED_COLUMN_INDEX] = Labels.getString("Common.Real") + " / " + Labels.getString("Common.Estimated") + " (+ " + Labels.getString("Common.Overestimated") + ")";
        CheckTableHeader customTableHeader = new CheckTableHeader(this, columnToolTips);
        setTableHeader(customTableHeader);
    }

    @Override
    protected void setTitle() {
        String title = Labels.getString("BurndownChartPanel.Chart List");
        int rowCount = getModel().getRowCount(); // get row count on the model not the view !
        if (rowCount > 0) {
            int selectedRowCount = getSelectedRowCount();
            AbstractActivities tableList = getTableList();
            if (selectedRowCount > 1) {
                int[] rows = getSelectedRows();
                int estimated = 0;
                int overestimated = 0;
                int real = 0;
                float storypoints = 0;
                for (int row : rows) {
                    Activity selectedActivity = getActivityFromRowIndex(row);
                    estimated += selectedActivity.getEstimatedPoms();
                    overestimated += selectedActivity.getOverestimatedPoms();
                    storypoints += selectedActivity.getStoryPoints();
                    real += selectedActivity.getActualPoms();
                }
                title += " (" + "<span style=\"color:black; background-color:" + ColorUtil.toHex(Main.selectedRowColor) + "\">&nbsp;" + selectedRowCount + "&nbsp;</span>" + "/" + rowCount + ")";
                title += " > " + Labels.getString("Common.Done") + ": " + "<span style=\"color:black; background-color:" + ColorUtil.toHex(Main.selectedRowColor) + "\">&nbsp;" + real + " / " + estimated;
                if (overestimated > 0) {
                    title += " + " + overestimated;
                }
                title += "&nbsp;</span>";
                int accuracy = Math.round(((float) real / ((float) estimated + overestimated)) * 100);
                title += " (" + "<span style=\"color:black; background-color:" + ColorUtil.toHex(Main.selectedRowColor) + "\">&nbsp;" + accuracy + "%" + "&nbsp;</span>" + ")";
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    title += " > " + Labels.getString("Agile.Velocity") + ": " + "<span style=\"color:black; background-color:" + ColorUtil.toHex(Main.selectedRowColor) + "\">&nbsp;" + df.format(storypoints) + "&nbsp;</span>";
                }
                String toolTipText = Labels.getString("Common.Done") + ": ";
                toolTipText += TimeConverter.getLength(real) + " / ";
                toolTipText += TimeConverter.getLength(estimated);
                if (overestimated > 0) {
                    toolTipText += " + " + TimeConverter.getLength(overestimated);
                }
                toolTipText += " (" + Labels.getString("ReportListPanel.Accuracy") + ": " + accuracy + "%)";
                getTitlePanel().setToolTipText(toolTipText);
                // Hide buttons of the quick bar
                getTitlePanel().hideSelectedButton();
            } else {
                title += " (" + rowCount + ")";
                title += " > " + Labels.getString("Common.Done") + ": ";
                title += tableList.getNbRealPom();
                title += " / " + tableList.getNbEstimatedPom();
                if (tableList.getNbOverestimatedPom() > 0) {
                    title += " + " + tableList.getNbOverestimatedPom();
                }
                int accuracy = getList().getAccuracy();
                title += " (" + accuracy + "%)";
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    title += " > " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(tableList.getStoryPoints());
                }
                // Tool tip
                String toolTipText = Labels.getString("Common.Done") + ": ";
                toolTipText += TimeConverter.getLength(tableList.getNbRealPom()) + " / ";
                toolTipText += TimeConverter.getLength(tableList.getNbEstimatedPom());
                if (tableList.getNbOverestimatedPom() > 0) {
                    toolTipText += " + " + TimeConverter.getLength(tableList.getNbOverestimatedPom());
                }
                toolTipText += " (" + Labels.getString("ReportListPanel.Accuracy") + ": " + accuracy + "%)";
                getTitlePanel().setToolTipText(toolTipText);
                // Show buttons of the quick bar
                getTitlePanel().showSelectedButton();
            }
        } else {
            getTitlePanel().hideSelectedButton();
        }
        if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
            getTitlePanel().showRefreshButton(); // end of the line
        }
        // Update title
        getTitlePanel().setText("<html>" + title + "</html>");
        //activitiesPanel.getTitlePanel().repaintLabel(); // this is necessary to force stretching of panel
        getTitlePanel().repaint();
    }

    @Override
    protected void populateSubTable() {
        // not used
    }

    @Override
    protected void emptySubTable() {
        // not used
    }

    @Override
    public void deleteTask(int rowIndex) { // remove !
        Activity activity = getActivityFromRowIndex(rowIndex);
        getList().remove(activity); // remove task
        removeRow(rowIndex);
    }
}
