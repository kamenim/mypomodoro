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
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import org.mypomodoro.Main;
import org.mypomodoro.db.mysql.MySQLConfigLoader;
import org.mypomodoro.gui.AbstractActivitiesTable;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.gui.create.list.SubTaskTypeList;
import org.mypomodoro.gui.create.list.TaskTypeList;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ColumnResizer;
import org.mypomodoro.gui.TableHeader;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Table for activities
 *
 */
public class ActivitiesTable extends AbstractActivitiesTable {

    private final ActivitiesTableModel model;
    private final ActivitiesPanel panel;

    public ActivitiesTable(final ActivitiesTableModel model, final ActivitiesPanel panel) {
        super(model);

        this.model = model;
        this.panel = panel;

        setTableHeader();

        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRowCount = getSelectedRowCount();
                if (selectedRowCount > 0) {
                    if (!e.getValueIsAdjusting()) { // ignoring the deselection event
                            System.err.println("selectedRowCount > 0 => " + Thread.currentThread().getStackTrace()[1].getMethodName());
                        // See AbstractActivitiesTable for reason to set WHEN_FOCUSED here
                        setInputMap(JTable.WHEN_FOCUSED, im);

                        if (selectedRowCount > 1) { // multiple selection
                            // diactivate/gray out unused tabs
                            panel.getTabbedPane().setEnabledAt(1, false); // comment
                            panel.getTabbedPane().setEnabledAt(2, false); // edit                                    
                            panel.getTabbedPane().setEnabledAt(3, true); // merging                                    
                            if (panel.getTabbedPane().getSelectedIndex() == 1
                                    || panel.getTabbedPane().getSelectedIndex() == 2) {
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
                                if (index == 3) {
                                    panel.getTabbedPane().setEnabledAt(3, false); // merging
                                    if (panel.getTabbedPane().getSelectedIndex() == 3) {
                                        panel.getTabbedPane().setSelectedIndex(0); // switch to details panel
                                    }
                                } else {
                                    panel.getTabbedPane().setEnabledAt(index, true);
                                }
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
                } else {
                            System.err.println("selectedRowCount == 0 => " + Thread.currentThread().getStackTrace()[1].getMethodName());
                    setTitle();
                }
            }
        });

        init();

        // Listener on editable cells
        // Table model has a flaw: the update table event is fired whenever once click on an editable cell
        // To avoid update overhead, we compare old value with new value
        // (we could also have used solution found at https://tips4java.wordpress.com/2009/06/07/table-cell-listener        
        model.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row != -1
                        && e.getType() == TableModelEvent.UPDATE) {
                    System.err.println("tableChanged => " + Thread.currentThread().getStackTrace()[1].getMethodName());
                    ActivitiesTableModel model = (ActivitiesTableModel) e.getSource();
                    Object data = model.getValueAt(row, column);
                    if (data != null) {
                        Integer ID = (Integer) model.getValueAt(row, AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
                        Activity act = Activity.getActivity(ID.intValue());
                        if (column == AbstractTableModel.TITLE_COLUMN_INDEX) { // Title (can't be empty)
                            String name = data.toString().trim();
                            if (!name.equals(act.getName())) {
                                if (name.length() == 0) {
                                    //model.removeTableModelListener(this);
                                    // reset the original value. Title can't be empty.
                                    model.setValueAt(act.getName(), convertRowIndexToModel(row), AbstractTableModel.TITLE_COLUMN_INDEX);
                                    //model.addTableModelListener(this);
                                } else {
                                    act.setName(name);
                                    act.databaseUpdate();
                                    // The customer resizer may resize the title column to fit the length of the new text
                                    //ColumnResizer.adjustColumnPreferredWidths(this);
                                    revalidate();
                                }
                            }
                        } else if (column == AbstractTableModel.TYPE_COLUMN_INDEX) { // Type
                            String type = data.toString().trim();
                            if (!type.equals(act.getType())) {
                                act.setType(type);
                                act.databaseUpdate();
                                // load template for user stories
                                if (Main.preferences.getAgileMode()) {
                                    panel.getCommentPanel().showInfo(act);
                                }
                                // refresh the combo boxes of all rows to display the new type (if any)
                                String[] types = (String[]) TaskTypeList.getTypes().toArray(new String[0]);
                                if (act.isSubTask()) {
                                    types = (String[]) SubTaskTypeList.getTypes().toArray(new String[0]);
                                }
                                getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setCellRenderer(new ActivitiesTypeComboBoxCellRenderer(types, true));
                                getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setCellEditor(new ActivitiesTypeComboBoxCellEditor(types, true));
                            }
                        } else if (column == AbstractTableModel.ESTIMATED_COLUMN_INDEX) { // Estimated
                            int estimated = (Integer) data;
                            if (estimated != act.getEstimatedPoms()
                                    && estimated + act.getOverestimatedPoms() >= act.getActualPoms()) {
                                act.setEstimatedPoms(estimated);
                                act.databaseUpdate();
                            }
                        } else if (column == AbstractTableModel.STORYPOINTS_COLUMN_INDEX) { // Story Points
                            Float storypoints = (Float) data;
                            if (storypoints != act.getStoryPoints()) {
                                act.setStoryPoints(storypoints);
                                act.databaseUpdate();
                            }
                        } else if (column == AbstractTableModel.ITERATION_COLUMN_INDEX) { // Iteration 
                            int iteration = Integer.parseInt(data.toString());
                            if (iteration != act.getIteration()) {
                                act.setIteration(iteration);
                                act.databaseUpdate();
                            }
                        }
                        getList().update(act);
                        // Refresh panel border after updating the list
                        setTitle();
                        // Updating details only
                        panel.getDetailsPanel().selectInfo(act);
                        panel.getDetailsPanel().showInfo();
                        //activitiesPanel.getDetailsPanel().showInfo(this);
                    }
                }
            }
        });
    }

    @Override
    protected void init() {
        // set custom render for dates
        getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setCellRenderer(new DateRenderer()); // date (custom renderer)
        getColumnModel().getColumn(AbstractTableModel.TITLE_COLUMN_INDEX).setCellRenderer(new TitleRenderer()); // title
        // type combo box
        String[] types = (String[]) TaskTypeList.getTypes().toArray(new String[0]);
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setCellRenderer(new ActivitiesTypeComboBoxCellRenderer(types, true));
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setCellEditor(new ActivitiesTypeComboBoxCellEditor(types, true));
        // Estimated combo box
        // The values of the combo depends on the activity : see EstimatedComboBoxCellRenderer and EstimatedComboBoxCellEditor
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setCellRenderer(new ActivitiesEstimatedComboBoxCellRenderer(new Integer[0], false));
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setCellEditor(new ActivitiesEstimatedComboBoxCellEditor(new Integer[0], false));
        // Story Point combo box
        Float[] points = new Float[]{0f, 0.5f, 1f, 2f, 3f, 5f, 8f, 13f, 20f, 40f, 100f};
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setCellRenderer(new ActivitiesStoryPointsComboBoxCellRenderer(points, false));
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setCellEditor(new ActivitiesStoryPointsComboBoxCellEditor(points, false));
        // Iteration combo box
        Integer[] iterations = new Integer[102];
        for (int i = 0; i <= 101; i++) {
            iterations[i] = i - 1;
        }
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setCellRenderer(new ActivitiesIterationComboBoxCellRenderer(iterations, false));
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setCellEditor(new ActivitiesIterationComboBoxCellEditor(iterations, false));
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
            getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMaxWidth(60);
            getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMinWidth(60);
            getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setPreferredWidth(60);
            getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMaxWidth(60);
            getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMinWidth(60);
            getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setPreferredWidth(60);
        }
        // hide unplanned and date in Agile mode
        if (Main.preferences.getAgileMode()) {
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMaxWidth(0);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMinWidth(0);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setPreferredWidth(0);
            getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setMaxWidth(0);
            getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setMinWidth(0);
            getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setPreferredWidth(0);
        } else {
            // Set width of columns Unplanned and date
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMaxWidth(30);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMinWidth(30);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setPreferredWidth(30);
            getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setMaxWidth(90);
            getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setMinWidth(90);
            getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setPreferredWidth(90);
        }
        // Set width of column estimated
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setMaxWidth(80);
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setMinWidth(80);
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setPreferredWidth(80);
        // Set width of column type
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setMaxWidth(200);
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setMinWidth(200);
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setPreferredWidth(200);
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

    protected void initTabs() {
        if (getRowCount() == 0) {
            for (int index = 0; index < panel.getTabbedPane().getTabCount(); index++) {
                if (index == 4) { // import tab
                    panel.getTabbedPane().setSelectedIndex(index);
                    continue;
                }
                panel.getTabbedPane().setEnabledAt(index, false);
            }
        } else {
            for (int index = 0; index < panel.getTabbedPane().getTabCount(); index++) {
                panel.getTabbedPane().setEnabledAt(index, index != 3); // merge tab : index == 3                                   
            }
            panel.getTabbedPane().setSelectedIndex(0);
        }
    }

    @Override
    protected void showInfo(int activityId) {
        Activity activity = getActivityById(activityId);
        panel.getDetailsPanel().selectInfo(activity);
        panel.getDetailsPanel().showInfo();
        //panel.getDetailsPanel().showInfo(this);
        panel.getCommentPanel().showInfo(activity);
        //panel.getEditPanel().showInfo(activity, this);
        panel.getEditPanel().showInfo(activity);
        // set table for merge, export panels
        //panel.getMergePanel().setTable(this);
        //panel.getExportPanel().setTable(this);
    }

    @Override
    protected void showInfoForSelectedRow() {
        showInfo(getActivityIdFromSelectedRow());
    }

    @Override
    protected void showInfoForRowIndex(int rowIndex) {
        showInfo(getActivityIdFromRowIndex(rowIndex));
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
    protected ActivityList getList() {
        return ActivityList.getList();
    }

    @Override
    protected ActivityList getTableList() {
        return ActivityList.getTaskList();
    }

    @Override
    protected void setTableHeader() {
        String[] columnToolTips = AbstractTableModel.COLUMN_NAMES.clone();
        columnToolTips[AbstractTableModel.UNPLANNED_COLUMN_INDEX] = Labels.getString("Common.Unplanned");
        columnToolTips[AbstractTableModel.DATE_COLUMN_INDEX] = Labels.getString("Common.Date scheduled");
        columnToolTips[AbstractTableModel.ESTIMATED_COLUMN_INDEX] =  "(" + Labels.getString("Common.Real") + " / ) " + Labels.getString("Common.Estimated") + " (+ " + Labels.getString("Common.Overestimated") + ")";
        TableHeader customTableHeader = new TableHeader(this, columnToolTips);
        setTableHeader(customTableHeader);
    }

    @Override
    protected void setTitle() {
        String titleActivitiesList = Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ActivityListPanel.Activity List");
        int rowCount = getRowCount();
        if (rowCount > 0) {
            int selectedRowCount = getSelectedRowCount();
            ActivityList tableList = getTableList();
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
                titleActivitiesList += " (" + "<span style=\"color:black; background-color:" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + selectedRowCount + "&nbsp;</span>" + "/" + rowCount + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": " + "<span style=\"color:black; background-color:" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + real + " / " + estimated;
                if (overestimated > 0) {
                    titleActivitiesList += " + " + overestimated;
                }
                titleActivitiesList += "&nbsp;</span>";
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + "<span style=\"color:black; background-color:" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + df.format(storypoints) + "&nbsp;</span>";
                }
                // Tool tip
                String toolTipText = Labels.getString("Common.Done") + ": ";
                toolTipText += TimeConverter.getLength(real) + " / ";
                toolTipText += TimeConverter.getLength(estimated);
                if (overestimated > 0) {
                    toolTipText += " + " + TimeConverter.getLength(overestimated);
                }
                getTitlePanel().setToolTipText(toolTipText);
                // Hide buttons of the quick bar
                getTitlePanel().hideSelectedButton();
                getTitlePanel().hideDuplicateButton();
            } else {
                titleActivitiesList += " (" + rowCount + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": ";
                titleActivitiesList += tableList.getNbRealPom();
                titleActivitiesList += " / " + tableList.getNbEstimatedPom();
                if (tableList.getNbOverestimatedPom() > 0) {
                    titleActivitiesList += " + " + tableList.getNbOverestimatedPom();
                }
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(tableList.getStoryPoints());
                }
                // Tool tip
                String toolTipText = Labels.getString("Common.Done") + ": ";
                toolTipText += TimeConverter.getLength(tableList.getNbRealPom()) + " / ";
                toolTipText += TimeConverter.getLength(tableList.getNbEstimatedPom());
                if (tableList.getNbOverestimatedPom() > 0) {
                    toolTipText += " + " + TimeConverter.getLength(tableList.getNbOverestimatedPom());
                }
                getTitlePanel().setToolTipText(toolTipText);
                // Show buttons of the quick bar
                getTitlePanel().showSelectedButton();
                getTitlePanel().showDuplicateButton();
            }
        } else {
            getTitlePanel().hideSelectedButton();
            getTitlePanel().hideDuplicateButton();
        }
        getTitlePanel().showCreateButton();
        if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
            getTitlePanel().showRefreshButton(); // end of the line
        }
        // Update title
        getTitlePanel().setText("<html>" + titleActivitiesList + "</html>");
        //activitiesPanel.getTitlePanel().repaintLabel(); // this is necessary to force stretching of panel
        getTitlePanel().repaint();
    }

    protected void populateSubTable() {
        panel.populateSubTable(getActivityIdFromSelectedRow());
    }

    protected void emptySubTable() {
        panel.emptySubTable();
    }

    @Override
    protected TableTitlePanel getTitlePanel() {
        return panel.getTableTitlePanel();
    }

    @Override
    public void removeRow(int rowIndex) {
        //clearSelection(); // clear the selection so removeRow won't fire valueChanged on ListSelectionListener (especially in case of large selection)
        model.removeRow(convertRowIndexToModel(rowIndex)); // we remove in the Model...
        if (getRowCount() > 0) {
            int currentRow = currentSelectedRow > rowIndex || currentSelectedRow == getRowCount() ? currentSelectedRow - 1 : currentSelectedRow;
            setRowSelectionInterval(currentRow, currentRow); // ...while selecting in the View
            scrollRectToVisible(getCellRect(currentRow, 0, true));
        }
    }

    @Override
    public void insertRow(Activity activity) {
        //clearSelection(); // clear the selection so insertRow won't fire valueChanged on ListSelectionListener (especially in case of large selection)        
        // By default, the row is added at the bottom of the list
        // However, if one of the columns has been previously sorted the position of the row might not be the bottom position...
        model.addRow(activity); // we add in the Model...
        if (getRowCount() == 1) { // refresh tabs as the very first row has just been added to the table
            initTabs();
        }
        int currentRow = convertRowIndexToView(getRowCount() - 1); // ...while selecting in the View
        setRowSelectionInterval(currentRow, currentRow);
        scrollRectToVisible(getCellRect(currentRow, 0, true));
    }

    // no default name
    // cell editing is done by TitleRenderer in AbstractActivitiesTable
    @Override
    public void createNewTask() {
        Activity newActivity = new Activity();
        newActivity.setName(Labels.getString("Common.New task"));
        getList().add(newActivity); // save activity in database
        newActivity.setName(""); // the idea is to insert an empty title so the editing (editCellAt in TitleRenderer) shows an empty field
        insertRow(newActivity);
        panel.getTabbedPane().setSelectedIndex(2); // open edit tab
    }

    // default name: (D) + name
    // cell editing is done by TitleRenderer in AbstractActivitiesTable
    @Override
    public void duplicateTask() {
        if (getSelectedRowCount() == 1) {
            Activity originalCopiedActivity = getActivityFromSelectedRow();
            try {
                Activity copiedActivity = originalCopiedActivity.clone(); // a clone is necessary to remove the reference/pointer to the original task                
                copiedActivity.setName("(D) " + copiedActivity.getName());
                copiedActivity.setActualPoms(0);
                copiedActivity.setOverestimatedPoms(0);
                getList().add(copiedActivity, new Date(), new Date(0)); // save activity in database
                //copiedActivity.setName(""); // the idea is to insert an empty title so the editing (editCellAt in TitleRenderer) shows an empty field
                insertRow(copiedActivity);
                panel.getTabbedPane().setSelectedIndex(2); // open edit tab
            } catch (CloneNotSupportedException ignored) {
            }
        }
    }
}
