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
package org.mypomodoro.gui.todo;

import org.mypomodoro.gui.TableTitlePanel;
import java.text.DecimalFormat;
import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.mypomodoro.Main;
import org.mypomodoro.db.mysql.MySQLConfigLoader;
import org.mypomodoro.gui.AbstractTable;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ColumnResizer;
import org.mypomodoro.gui.TableHeader;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Table for activities
 *
 */
public class ToDoTable extends AbstractTable  {

    private final ToDoPanel panel;

    public ToDoTable(final ToDoTableModel model, final ToDoPanel panel) {
        super(model);

        // Drag and drop
        setDragEnabled(true);
        setDropMode(DropMode.INSERT_ROWS);
        setTransferHandler(new ToDoTransferHandler(panel));

        this.panel = panel;

        setTableHeader();

        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                panel.setCurrentTable(ToDoTable.this); // set current table
                //System.err.println("method name = " + Thread.currentThread().getStackTrace()[1].getMethodName());
                int selectedRowCount = getSelectedRowCount();
                if (selectedRowCount > 0) {
                    if (!e.getValueIsAdjusting()) { // ignoring the deselection event                        
                        // See AbstractActivitiesTable for reason to set WHEN_FOCUSED here
                        setInputMap(JTable.WHEN_FOCUSED, im);

                        if (selectedRowCount > 1) { // multiple selection
                            // diactivate/gray out unused tabs
                            panel.getTabbedPane().disableCommentTab();
                            panel.getTabbedPane().disableEditTab();
                            panel.getTabbedPane().disableOverestimationTab();
                            panel.getTabbedPane().disableUnplannedTab();
                            if ((panel.getPomodoro().inPomodoro() && getSelectedRowCount() > 2) || !panel.getPomodoro().inPomodoro()) {
                                panel.getTabbedPane().enableMergeTab();
                            }
                            if (panel.getTabbedPane().getSelectedIndex() == panel.getTabbedPane().getCommentTabIndex()
                                    || panel.getTabbedPane().getSelectedIndex() == panel.getTabbedPane().getEditTabIndex()
                                    || panel.getTabbedPane().getSelectedIndex() == panel.getTabbedPane().getOverestimateTabIndex()
                                    || panel.getTabbedPane().getSelectedIndex() == panel.getTabbedPane().getUnplannedTabIndex()) {
                                panel.getTabbedPane().setSelectedIndex(0); // switch to details panel
                            }
                            if (!panel.getPomodoro().getTimer().isRunning()) {
                                panel.getPomodoro().setCurrentToDoId(-1); // this will disable the start button
                            }
                            currentSelectedRow = getSelectedRows()[0]; // always selecting the first selected row (otherwise removeRow will fail)
                            // Display info (list of selected tasks)                            
                            showDetailsForSelectedRows();
                            // populate subtable
                            emptySubTable();
                        } else if (selectedRowCount == 1) {
                            // activate all panels
                            for (int index = 0; index < panel.getTabbedPane().getTabCount(); index++) {
                                if (index == panel.getTabbedPane().getMergeTabIndex()) {
                                    panel.getTabbedPane().disableMergeTab();
                                    if (panel.getTabbedPane().getSelectedIndex() == panel.getTabbedPane().getMergeTabIndex()) {
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
                        panel.setIconLabels();
                        setTitle();
                    }
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
                //System.err.println("method name = " + Thread.currentThread().getStackTrace()[1].getMethodName());                                                    
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row != -1
                        && e.getType() == TableModelEvent.UPDATE) {
                    ToDoTableModel sourceModel = (ToDoTableModel) e.getSource();
                    Object data = sourceModel.getValueAt(row, column);
                    if (data != null) {
                        if (column >= 0) { // This needs to be checked : the moveRow method (see ToDoTransferHandler) fires tableChanged with column = -1 
                            Activity act = getActivityFromRowIndex(row);
                            if (column == AbstractTableModel.TITLE_COLUMN_INDEX) { // Title (can't be empty)
                                String name = data.toString().trim();
                                if (!name.equals(act.getName())) {
                                    if (name.length() == 0) {
                                        // reset the original value. Title can't be empty.
                                        sourceModel.setValueAt(act.getName(), convertRowIndexToModel(row), AbstractTableModel.TITLE_COLUMN_INDEX);
                                    } else {
                                        act.setName(name);
                                        act.databaseUpdate();
                                        // The customer resizer may resize the title column to fit the length of the new text
                                        //ColumnResizer.adjustColumnPreferredWidths(this);
                                        revalidate();
                                    }
                                }
                            } else if (column == AbstractTableModel.ESTIMATED_COLUMN_INDEX) { // Estimated
                                int estimated = (Integer) data;
                                if (estimated != act.getEstimatedPoms()
                                        && estimated + act.getOverestimatedPoms() >= act.getActualPoms()) {
                                    int diffEstimated = estimated - act.getEstimatedPoms();
                                    act.setEstimatedPoms(estimated);
                                    act.databaseUpdate();
                                    if (act.isSubTask()) { // update parent activity
                                        addEstimatedPomsToParent(0, diffEstimated, 0);
                                    }
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
                            // Updating details only
                            panel.getDetailsPanel().selectInfo(act);
                            panel.getDetailsPanel().showInfo();
                            //activitiesPanel.getDetailsPanel().showInfo(this);
                        }
                    }
                }
                // Refresh title either there has been a change in the data (estimation, story points) or a change in the number of rows
                //setTitle();
            }
        });
    }

    @Override
    public ToDoTableModel getModel() {
        return (ToDoTableModel) super.getModel();
    }

    @Override
    protected void init() {
        getColumnModel().getColumn(AbstractTableModel.PRIORITY_COLUMN_INDEX).setCellRenderer(new CustomTableRenderer()); // priority
        getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        getColumnModel().getColumn(AbstractTableModel.TITLE_COLUMN_INDEX).setCellRenderer(new TitleRenderer()); // title           
        // The values of the combo depends on the activity : see EstimatedComboBoxCellRenderer and EstimatedComboBoxCellEditor
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setCellRenderer(new ToDoEstimatedComboBoxCellRenderer(new Integer[0], false));
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setCellEditor(new ToDoEstimatedComboBoxCellEditor(new Integer[0], false));
        // Story Point combo box
        Float[] points = new Float[]{0f, 0.5f, 1f, 2f, 3f, 5f, 8f, 13f, 20f, 40f, 100f};
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setCellRenderer(new ToDoStoryPointsComboBoxCellRenderer(points, false));
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setCellEditor(new ToDoStoryPointsComboBoxCellEditor(points, false));
        // Iteration combo box
        Integer[] iterations = new Integer[102];
        for (int i = 0; i <= 101; i++) {
            iterations[i] = i - 1;
        }
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setCellRenderer(new ToDoIterationComboBoxCellRenderer(iterations, false));
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setCellEditor(new ToDoIterationComboBoxCellEditor(iterations, false));
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
        // hide unplanned in Agile mode
        if (Main.preferences.getAgileMode()) {
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMaxWidth(0);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMinWidth(0);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setPreferredWidth(0);
        } else {
            // Set width of columns Unplanned
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMaxWidth(30);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setMinWidth(30);
            getColumnModel().getColumn(AbstractTableModel.UNPLANNED_COLUMN_INDEX).setPreferredWidth(30);
        }
        // Set width of column priority
        getColumnModel().getColumn(AbstractTableModel.PRIORITY_COLUMN_INDEX).setMaxWidth(40);
        getColumnModel().getColumn(AbstractTableModel.PRIORITY_COLUMN_INDEX).setMinWidth(40);
        getColumnModel().getColumn(AbstractTableModel.PRIORITY_COLUMN_INDEX).setPreferredWidth(40);
        // Set width of column estimated
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setMaxWidth(80);
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setMinWidth(80);
        getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setPreferredWidth(80);
        // hide date, type, diffI and diff II columns
        getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.DATE_COLUMN_INDEX).setPreferredWidth(0);
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setPreferredWidth(0);
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

    // This method is empty in sub class    
    @Override
    protected void initTabs() {
        panel.getTabbedPane().initTabs(getModel().getRowCount());
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
        //panel.getMergePanel().setTable(this); TODO
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
    protected ToDoList getList() {
        return ToDoList.getList();
    }

    @Override
    protected ToDoList getTableList() {
        return ToDoList.getTaskList();
    }

    @Override
    protected void setTableHeader() {
        String[] columnToolTips = AbstractTableModel.COLUMN_NAMES.clone();
        columnToolTips[AbstractTableModel.UNPLANNED_COLUMN_INDEX] = Labels.getString("Common.Unplanned");
        columnToolTips[AbstractTableModel.DATE_COLUMN_INDEX] = Labels.getString("Common.Date scheduled");
        columnToolTips[AbstractTableModel.ESTIMATED_COLUMN_INDEX] = "(" + Labels.getString("Common.Real") + " / ) " + Labels.getString("Common.Estimated") + " (+ " + Labels.getString("Common.Overestimated") + ")";
        TableHeader customTableHeader = new TableHeader(this, columnToolTips);
        setTableHeader(customTableHeader);
    }

    @Override
    protected void setTitle() {
        String title = Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List");
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
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    title += " > " + Labels.getString("Agile.Common.Story Points") + ": " + "<span style=\"color:black; background-color:" + ColorUtil.toHex(Main.selectedRowColor) + "\">&nbsp;" + df.format(storypoints) + "&nbsp;</span>";
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
                getTitlePanel().hideOverestimationButton();
                getTitlePanel().hideDuplicateButton();
            } else {
                title += " (" + rowCount + ")";
                title += " > " + Labels.getString("Common.Done") + ": ";
                title += tableList.getNbRealPom();
                title += " / " + tableList.getNbEstimatedPom();
                if (tableList.getNbOverestimatedPom() > 0) {
                    title += " + " + tableList.getNbOverestimatedPom();
                }
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
                getTitlePanel().setToolTipText(toolTipText);
                // Show buttons of the quick bar
                if (getSelectedRowCount() == 1) {
                    // Show buttons of the quick bar
                    // Hide overestimation options when estimated == 0 or real < estimated
                    getTitlePanel().showSelectedButton();
                    Activity selectedActivity = getActivityFromSelectedRow();
                    if (selectedActivity.getEstimatedPoms() != 0
                            && selectedActivity.getActualPoms() >= selectedActivity.getEstimatedPoms()) {
                        panel.getTabbedPane().enableOverestimationTab();
                        getTitlePanel().showOverestimationButton();
                    } else {
                        panel.getTabbedPane().disableOverestimationTab();
                        getTitlePanel().hideOverestimationButton();
                    }
                    /*TODO remove these test lines
                     getTitlePanel().showOverestimationButton();
                     getTitlePanel().showExternalButton();
                     getTitlePanel().showInternalButton();*/
                }
            }
            getTitlePanel().showUnplannedButton();
        } else {
            getTitlePanel().hideSelectedButton();
            getTitlePanel().hideOverestimationButton();
            getTitlePanel().hideExternalButton();
            getTitlePanel().hideInternalButton();
        }
        if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
            getTitlePanel().showRefreshButton(); // end of the line
        }
        // Update title
        getTitlePanel().setText("<html>" + title + "</html>");
        //activitiesPanel.getTitlePanel().repaintLabel(); // this is necessary to force stretching of panel
        getTitlePanel().repaint();
    }

    // This method is empty in sub class
    protected void populateSubTable() {
        panel.populateSubTable(getActivityIdFromSelectedRow());
    }

    // This method is empty in sub class
    protected void emptySubTable() {
        panel.emptySubTable();
    }

    @Override
    protected TableTitlePanel getTitlePanel() {
        return panel.getTableTitlePanel();
    }

    @Override
    public void createNewTask() {
        // not used
    }

    @Override
    public void duplicateTask() {
        // nto used}
    }

    // To delete tasks from sub table, move them to Activity List then delete
    @Override
    public void deleteTask(int rowIndex) {
        // not used
    }
    
    @Override
    public void moveTask(int rowIndex) {
        Activity activity = getActivityFromRowIndex(rowIndex);
        if (activity.isSubTask()) {            
            removeSubTaskEstimatedPomsFromParent(activity);
        }
        getList().move(activity); // move to ActivityList
        removeRow(rowIndex);
        if (getList().isEmpty()
                && panel.getPomodoro().getTimer().isRunning()) { // break running
            panel.getPomodoro().stop();
            panel.getPomodoro().getTimerPanel().setStartEnv();
        }
    }
    
    @Override
    public void completeTask(int rowIndex) {
        Activity activity = getActivityFromRowIndex(rowIndex);
        if (activity.isSubTask()) {
            removeSubTaskEstimatedPomsFromParent(activity);
        }
        getList().complete(activity);
        removeRow(rowIndex);
        if (getList().isEmpty()
                && panel.getPomodoro().getTimer().isRunning()) { // break running
            panel.getPomodoro().stop();
            panel.getPomodoro().getTimerPanel().setStartEnv();
        }
    }

    @Override
    public void createUnplannedTask() {
        Activity unplannedToDo = new Activity();
        unplannedToDo.setEstimatedPoms(0);
        unplannedToDo.setIsUnplanned(true);
        unplannedToDo.setName("(U) " + Labels.getString("Common.Unplanned"));
        getList().add(unplannedToDo);
        unplannedToDo.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
        insertRow(unplannedToDo);
        panel.getTabbedPane().selectEditTab(); // open edit tab
    }

    @Override
    public void createInternalInterruption() {
        // Interruptions : update current/running pomodoro
        Activity currentToDo = panel.getPomodoro().getCurrentToDo();
        if (currentToDo != null && panel.getPomodoro().inPomodoro()) {
            currentToDo.incrementInternalInter();
            currentToDo.databaseUpdate();
            Activity interruption = new Activity();
            interruption.setEstimatedPoms(0);
            interruption.setIsUnplanned(true);
            interruption.setName("(I) " + Labels.getString("ToDoListPanel.Internal"));
            getList().add(interruption);
            interruption.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
            insertRow(interruption);
            panel.getTabbedPane().selectEditTab(); // open edit tab
        }
    }

    @Override
    public void createExternalInterruption() {
        // Interruptions : update current/running pomodoro
        Activity currentToDo = panel.getPomodoro().getCurrentToDo();
        if (currentToDo != null && panel.getPomodoro().inPomodoro()) {
            currentToDo.incrementInter();
            currentToDo.databaseUpdate();
            Activity interruption = new Activity();
            interruption.setEstimatedPoms(0);
            interruption.setIsUnplanned(true);
            interruption.setName("(E) " + Labels.getString("ToDoListPanel.External"));
            getList().add(interruption);
            interruption.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
            insertRow(interruption);
            panel.getTabbedPane().selectEditTab(); // open edit tab
        }
    }

    @Override
    public void overestimateTask(int poms) {
        panel.getOverestimationPanel().overestimateTask(poms);
        addEstimatedPomsToParent(0, 0, poms);
    }

    @Override
    public void reorderByPriority() {
        getTableList().reorderByPriority();
        for (int row = 0; row < getModel().getRowCount(); row++) {
            Activity activity = getActivityFromRowIndex(row);
            getModel().setValueAt(activity.getPriority(), convertRowIndexToModel(row), AbstractTableModel.PRIORITY_COLUMN_INDEX);
        }
    }
    
    protected void removeSubTaskEstimatedPomsFromParent(Activity activity) {
        Activity parentActivity = panel.getMainTable().getActivityFromSelectedRow();
        addEstimatedPomsToParent(-parentActivity.getActualPoms(), 
                -parentActivity.getEstimatedPoms(), 
                -parentActivity.getOverestimatedPoms());
    }
    
    protected void addSubTaskEstimatedPomsToParent(Activity activity) {
        Activity parentActivity = panel.getMainTable().getActivityFromSelectedRow();
        addEstimatedPomsToParent(parentActivity.getActualPoms(), 
                parentActivity.getEstimatedPoms(), 
                parentActivity.getOverestimatedPoms());
    }

    protected void addEstimatedPomsToParent(int realPoms, int estimatedPoms, int overestimatedPoms) {
        Activity parentActivity = panel.getMainTable().getActivityFromSelectedRow();
        parentActivity.setActualPoms(parentActivity.getActualPoms() + realPoms);
        parentActivity.setEstimatedPoms(parentActivity.getEstimatedPoms() + estimatedPoms);
        parentActivity.setOverestimatedPoms(parentActivity.getOverestimatedPoms() + overestimatedPoms);
        parentActivity.databaseUpdate();
        getList().update(parentActivity);
        // getSelectedRow must not be converted (convertRowIndexToModel)
        panel.getMainTable().getModel().setValueAt(parentActivity.getEstimatedPoms(), panel.getMainTable().getSelectedRow(), AbstractTableModel.ESTIMATED_COLUMN_INDEX);
    }
}
