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

import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
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
public class ToDoTable extends AbstractTable {

    protected final ToDoPanel panel;

    public ToDoTable(final ToDoTableModel model, final ToDoPanel panel) {
        super(model, panel);

        // Drag and drop
        setDragEnabled(true);
        setDropMode(DropMode.INSERT_ROWS);
        setTransferHandler(new ToDoTransferHandler(panel));

        this.panel = panel;

        setTableHeader();

        setColumnModel();

        initTabs();

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
                        // Display info (list of selected tasks)                            
                        showDetailsForSelectedRows();
                        // empty subtable
                        emptySubTable();
                        // hide start button unless timer is running
                        if (!panel.getPomodoro().getTimer().isRunning()) {
                            panel.getTimerPanel().hideStartButton();
                        }
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
                        if (!panel.getPomodoro().getTimer().isRunning()) {
                            panel.getPomodoro().setCurrentToDoId(getActivityIdFromSelectedRow());
                        }
                        // here do not use showCurrentSelectedRow()
                        scrollRectToVisible(getCellRect(getSelectedRow(), 0, true)); // when sorting columns, focus on selected row
                        // Display details                           
                        showInfoForSelectedRow();
                        // populate subtable
                        populateSubTable();
                        // hide start button unless timer is running and task has no subtasks or task is not finished
                        // optimization: isSubtask is not necessary but it's a way to avoid using hasSubTasks as much as possible
                        // we don't check if the task is finished here: we leave it to the Pomodoro object to display the error dialog
                        if (!panel.getPomodoro().getTimer().isRunning()
                                && !getActivityFromSelectedRow().isSubTask()
                                && ToDoList.hasSubTasks(getActivityIdFromSelectedRow())) {
                            panel.getTimerPanel().hideStartButton();
                        } else {
                            panel.getTimerPanel().showStartButton();
                        }
                    }
                    setIconLabels();
                }
            }
        });

        // Listener on editable cells
        // Table model has a flaw: the update table event is fired whenever once click on an editable cell
        // To avoid update overhead, we compare old value with new value
        // (we could also have used solution found at https://tips4java.wordpress.com/2009/06/07/table-cell-listener)        
        getModel().addTableModelListener(new AbstractTableModelListener() {

            @Override
            public void customTableChanged(TableModelEvent e) {
                //System.err.println("method name = " + Thread.currentThread().getStackTrace()[1].getMethodName());                                                    
                int row = e.getFirstRow();
                int column = e.getColumn();
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
                                    ColumnResizer.adjustColumnPreferredWidths(ToDoTable.this);
                                    revalidate();
                                }
                                // Refresh icon label
                                setIconLabels();
                            }
                        } else if (column == AbstractTableModel.ESTIMATED_COLUMN_INDEX) { // Estimated
                            int estimated = (Integer) data;
                            if (estimated != act.getEstimatedPoms()
                                    && estimated + act.getOverestimatedPoms() >= act.getActualPoms()) {
                                int diffEstimated = estimated - act.getEstimatedPoms();
                                act.setEstimatedPoms(estimated);
                                act.databaseUpdate();
                                if (act.isSubTask()) { // update parent activity
                                    panel.getMainTable().addPomsToSelectedRow(0, diffEstimated, 0);
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
                    }
                }
            }
        });
    }

    @Override
    public ToDoTableModel getModel() {
        return (ToDoTableModel) super.getModel();
    }

    @Override
    protected void setColumnModel() {
        getColumnModel().getColumn(AbstractTableModel.PRIORITY_COLUMN_INDEX).setCellRenderer(new CustomRenderer()); // priority
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

        // Make sure column title will fit long titles
        ColumnResizer.adjustColumnPreferredWidths(this);
        revalidate();
    }

    @Override
    protected void showInfo(Activity activity) {
        panel.getDetailsPanel().selectInfo(activity);
        panel.getDetailsPanel().showInfo();
        panel.getCommentPanel().showInfo(activity);
        panel.getEditPanel().showInfo(activity);
        setIconLabels(activity);
    }

    @Override
    protected void showDetailsForSelectedRows() {
        setIconLabels(); // This to adress multiple selection in sub table
        panel.getDetailsPanel().showInfo(getDetailsForSelectedRows());
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
            if (selectedRowCount > 0) {
                getTitlePanel().showSelectedButton();
            }
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
                getTitlePanel().hideOverestimationButton();
                getTitlePanel().hideExternalButton();
                getTitlePanel().hideInternalButton();
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
                    Activity selectedActivity = getActivityFromSelectedRow();
                    if (panel.getPomodoro().inPomodoro()) {
                        getTitlePanel().switchRunningButton();
                    } else {
                        getTitlePanel().switchSelectedButton();
                    }
                    if (selectedActivity.getEstimatedPoms() != 0
                            && selectedActivity.getActualPoms() >= selectedActivity.getEstimatedPoms()
                            && !ToDoList.hasSubTasks(selectedActivity.getId())) {
                        panel.getTabbedPane().enableOverestimationTab();
                        getTitlePanel().showOverestimationButton();
                    } else {
                        panel.getTabbedPane().disableOverestimationTab();
                        getTitlePanel().hideOverestimationButton();
                    }
                }
            }
        } else { // empty table
            getTitlePanel().hideSelectedButton();
            getTitlePanel().hideOverestimationButton();
            getTitlePanel().hideExternalButton();
            getTitlePanel().hideInternalButton();
        }
        if (canCreateUnplannedTask()) {
            getTitlePanel().showUnplannedButton();
        } else {
            getTitlePanel().hideUnplannedButton();
        }
        if (canCreateInterruptions()) {
            getTitlePanel().showExternalButton();
            getTitlePanel().showInternalButton();
        } else {
            getTitlePanel().hideExternalButton();
            getTitlePanel().hideInternalButton();
        }
        if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
            getTitlePanel().showRefreshButton(); // end of the line
        }
        // Update title
        getTitlePanel().setText("<html>" + title + "</html>");
        getTitlePanel().repaint();
    }

    @Override
    public void moveTask(int rowIndex) {
        Activity activity = getActivityFromRowIndex(rowIndex);
        if (activity.isSubTask()) {
            panel.getMainTable().removePomsFromSelectedRow(activity);
        }
        getList().moveToActivtyList(activity); // move to ActivityList
        removeRow(rowIndex);
        if (getList().isEmpty()
                && panel.getPomodoro().getTimer().isRunning()) { // break running
            panel.getPomodoro().stop();
            panel.getTimerPanel().setStartEnv();
        }
    }

    @Override
    public void completeTask(int rowIndex) {
        Activity activity = getActivityFromRowIndex(rowIndex);
        if (activity.isSubTask()) {
            panel.getMainTable().removePomsFromSelectedRow(activity);
        }
        getList().completeToReportList(activity);
        removeRow(rowIndex);
        if (getList().isEmpty()
                && panel.getPomodoro().getTimer().isRunning()) { // break running
            panel.getPomodoro().stop();
            panel.getTimerPanel().setStartEnv();
        }
    }

    @Override
    public void createUnplannedTask() {
        createUnplannedTask(new Activity());
    }

    public void createUnplannedTask(Activity activity) {
        activity.setEstimatedPoms(0);
        activity.setIsUnplanned(true);
        activity.setName("(U) " + Labels.getString("Common.Unplanned"));
        getList().add(activity);
        activity.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
        insertRow(activity);
        panel.getTabbedPane().selectEditTab(); // open edit tab
    }

    @Override
    public void createInternalInterruption() {
        createInternalInterruption(new Activity());
    }

    public void createInternalInterruption(Activity activity) {
        // Interruptions : update current/running pomodoro
        if (canCreateInterruptions()) {
            Activity currentToDo = panel.getPomodoro().getCurrentToDo();
            currentToDo.incrementInternalInter();
            currentToDo.databaseUpdate();
            activity.setEstimatedPoms(0);
            activity.setIsUnplanned(true);
            activity.setName("(I) " + Labels.getString("ToDoListPanel.Internal"));
            getList().add(activity);
            activity.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
            insertRow(activity);
            panel.getTabbedPane().selectEditTab(); // open edit tab
        }
    }

    @Override
    public void createExternalInterruption() {
        createExternalInterruption(new Activity());
    }

    public void createExternalInterruption(Activity activity) {
        // Interruptions : update current/running pomodoro
        if (canCreateInterruptions()) {
            Activity currentToDo = panel.getPomodoro().getCurrentToDo();
            currentToDo.incrementInter();
            currentToDo.databaseUpdate();
            activity.setEstimatedPoms(0);
            activity.setIsUnplanned(true);
            activity.setName("(E) " + Labels.getString("ToDoListPanel.External"));
            getList().add(activity);
            activity.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
            insertRow(activity);
            panel.getTabbedPane().selectEditTab(); // open edit tab
        }
    }

    protected boolean canCreateInterruptions() {
        return getRowCount() > 0 && panel.getPomodoro().inPomodoro() && panel.getPomodoro().getTimer().isRunning();
    }

    protected boolean canCreateUnplannedTask() {
        return getRowCount() > 0;
    }

    @Override
    public void overestimateTask(int poms) {
        panel.getOverestimationPanel().overestimateTask(poms);
    }

    @Override
    public void reorderByPriority() {
        getTableList().reorderByPriority();
        for (int row = 0; row < getModel().getRowCount(); row++) {
            Activity activity = getActivityFromRowIndex(row);
            getModel().setValueAt(activity.getPriority(), convertRowIndexToModel(row), AbstractTableModel.PRIORITY_COLUMN_INDEX);
        }
    }

    public void setIconLabels() {
        setIconLabels(getActivityFromSelectedRow());
    }

    public void setIconLabels(Activity selectedToDo) {
        if (getTableList().size() > 0) {
            Activity currentToDo = panel.getPomodoro().getCurrentToDo();
            Color defaultForegroundColor = getForeground(); // leave it to the theme foreground color 
            if (selectedToDo.getId() == panel.getCurrentTable().getActivityIdFromSelectedRow()) {
                panel.getUnplannedPanel().getIconPanel().setBackground(Main.selectedRowColor);
                panel.getDetailsPanel().getIconPanel().setBackground(Main.selectedRowColor);
                panel.getCommentPanel().getIconPanel().setBackground(Main.selectedRowColor);
                panel.getOverestimationPanel().getIconPanel().setBackground(Main.selectedRowColor);
                panel.getEditPanel().getIconPanel().setBackground(Main.selectedRowColor);
            } else {
                panel.getUnplannedPanel().getIconPanel().setBackground(Main.hoverRowColor);
                panel.getDetailsPanel().getIconPanel().setBackground(Main.hoverRowColor);
                panel.getCommentPanel().getIconPanel().setBackground(Main.hoverRowColor);
                panel.getOverestimationPanel().getIconPanel().setBackground(Main.hoverRowColor);
                panel.getEditPanel().getIconPanel().setBackground(Main.hoverRowColor);
            }
            if (panel.getPomodoro().inPomodoro()) {
                //ToDoIconPanel.showIconPanel(iconPanel, currentToDo, Main.taskRunningColor, false);
                ToDoIconPanel.showIconPanel(panel.getUnplannedPanel().getIconPanel(), currentToDo, Main.taskRunningColor);
                ToDoIconPanel.showIconPanel(panel.getDetailsPanel().getIconPanel(), currentToDo, Main.taskRunningColor);
                ToDoIconPanel.showIconPanel(panel.getCommentPanel().getIconPanel(), currentToDo, Main.taskRunningColor);
                ToDoIconPanel.showIconPanel(panel.getOverestimationPanel().getIconPanel(), currentToDo, Main.taskRunningColor);
                ToDoIconPanel.showIconPanel(panel.getEditPanel().getIconPanel(), currentToDo, Main.taskRunningColor);
                panel.getDetailsPanel().disableButtons();
            }
            if (getSelectedRowCount() <= 1) { // no multiple selection
                //Activity selectedToDo = getActivityFromRowIndex(row);
                if (panel.getPomodoro().inPomodoro() && selectedToDo.getId() != currentToDo.getId()) {
                    ToDoIconPanel.showIconPanel(panel.getDetailsPanel().getIconPanel(), selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(panel.getCommentPanel().getIconPanel(), selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(panel.getOverestimationPanel().getIconPanel(), selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(panel.getEditPanel().getIconPanel(), selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor);
                    panel.getDetailsPanel().enableButtons();
                } else if (!panel.getPomodoro().inPomodoro()) {
                    //ToDoIconPanel.showIconPanel(iconPanel, selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor, false);
                    ToDoIconPanel.showIconPanel(panel.getUnplannedPanel().getIconPanel(), selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(panel.getDetailsPanel().getIconPanel(), selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(panel.getCommentPanel().getIconPanel(), selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(panel.getOverestimationPanel().getIconPanel(), selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(panel.getEditPanel().getIconPanel(), selectedToDo, selectedToDo.isFinished() ? Main.taskFinishedColor : defaultForegroundColor);
                    panel.getDetailsPanel().enableButtons();
                }
            } else if (getSelectedRowCount() > 1) { // multiple selection
                if (!panel.getPomodoro().inPomodoro()) {
                    //ToDoIconPanel.clearIconPanel(iconPanel);
                    ToDoIconPanel.clearIconPanel(panel.getUnplannedPanel().getIconPanel());
                }
                ToDoIconPanel.clearIconPanel(panel.getDetailsPanel().getIconPanel());
                ToDoIconPanel.clearIconPanel(panel.getCommentPanel().getIconPanel());
                ToDoIconPanel.clearIconPanel(panel.getOverestimationPanel().getIconPanel());
                ToDoIconPanel.clearIconPanel(panel.getEditPanel().getIconPanel());
                panel.getDetailsPanel().enableButtons();
            }
        } else { // empty list
            //ToDoIconPanel.clearIconPanel(iconPanel);
            ToDoIconPanel.clearIconPanel(panel.getUnplannedPanel().getIconPanel());
            ToDoIconPanel.clearIconPanel(panel.getDetailsPanel().getIconPanel());
            ToDoIconPanel.clearIconPanel(panel.getCommentPanel().getIconPanel());
            ToDoIconPanel.clearIconPanel(panel.getOverestimationPanel().getIconPanel());
            ToDoIconPanel.clearIconPanel(panel.getEditPanel().getIconPanel());
            panel.getDetailsPanel().enableButtons();
        }
    }

    @Override
    public void showCurrentSelectedRow() {
        if (panel.getPomodoro().inPomodoro()) {
            for (int row = 0; row < getRowCount(); row++) {
                // Scroll to the currentToDo or, if the currentToDo is a subtask, scroll to the parent task
                if (panel.getPomodoro().getCurrentToDo().getId() == getActivityIdFromRowIndex(row)
                        || panel.getPomodoro().getCurrentToDo().getParentId() == getActivityIdFromRowIndex(row)) {
                    scrollRectToVisible(getCellRect(row, 0, true));
                    break;
                }
            }
        } else {
            super.showCurrentSelectedRow();
        }
    }
}
