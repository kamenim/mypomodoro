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

import org.mypomodoro.gui.TitlePanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Table for sub-activities
 *
 */
public class ToDoSubTable extends ToDoTable {

    public ToDoSubTable(ToDoSubTableModel model, final ToDoPanel panel) {
        super(model, panel);

        // This is to address the case/event when the mouse exit the table
        // Replacing listener of the ActivtiesTable class constructor
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                // Reset to currently selected task
                if (panel.getMainTable().getSelectedRowCount() == 1) {
                    if (getSelectedRowCount() == 1) {
                        showInfoForSelectedRow();
                    } else if (getSelectedRowCount() == 0) { // selected row on the main table
                        Activity activity = panel.getMainTable().getActivityFromSelectedRow();
                        // Activity may be null when hovering the cursor over the tasks while deleting/moving it
                        if (activity != null) {
                            showInfo(activity);
                        }
                    }
                }
                mouseHoverRow = -1;
            }
        });
    }

    // no story points and no refresh button for subtasks
    @Override
    protected void setTitle() {
        String title = Labels.getString("Common.Subtasks");
        int rowCount = getModel().getRowCount();
        if (rowCount > 0) {
            int selectedRowCount = getSelectedRowCount();
            AbstractActivities tableList = getTableList();
            if (selectedRowCount > 1) {
                int[] rows = getSelectedRows();
                int estimated = 0;
                int overestimated = 0;
                int real = 0;
                for (int row : rows) {
                    Activity selectedActivity = getActivityFromRowIndex(row);
                    estimated += selectedActivity.getEstimatedPoms();
                    overestimated += selectedActivity.getOverestimatedPoms();
                    real += selectedActivity.getActualPoms();
                }
                title += " (" + "<span style=\"color:black; background-color:" + ColorUtil.toHex(Main.selectedRowColor) + "\">&nbsp;" + selectedRowCount + "&nbsp;</span>" + "/" + rowCount + ")";
                title += " > " + Labels.getString("Common.Done") + ": " + "<span style=\"color:black; background-color:" + ColorUtil.toHex(Main.selectedRowColor) + "\">&nbsp;" + real + " / " + estimated;
                if (overestimated > 0) {
                    title += " + " + overestimated;
                }
                title += "&nbsp;</span>";
                // Tool tip
                String toolTipText = Labels.getString("Common.Done") + ": ";
                toolTipText += TimeConverter.getLength(real) + " / ";
                toolTipText += TimeConverter.getLength(estimated);
                if (overestimated > 0) {
                    toolTipText += " + " + TimeConverter.getLength(overestimated);
                }
                getTitlePanel().setToolTipText(toolTipText);
                // Hide buttons of the quick bar
                getTitlePanel().hideDuplicateButton();
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
                    if (canDuplicateTask()) {
                        getTitlePanel().showDuplicateButton();
                    }
                    if (selectedActivity.getEstimatedPoms() != 0
                            && selectedActivity.getActualPoms() >= selectedActivity.getEstimatedPoms()) {
                        panel.getTabbedPane().enableOverestimationTab();
                        getTitlePanel().showOverestimationButton();
                    } else {
                        panel.getTabbedPane().disableOverestimationTab();
                        getTitlePanel().hideOverestimationButton();
                    }
                } else { // no row selected
                    getTitlePanel().hideDuplicateButton();
                    getTitlePanel().hideOverestimationButton();
                    getTitlePanel().hideExternalButton();
                    getTitlePanel().hideInternalButton();
                }
            }
        } else { // empty table
            getTitlePanel().hideDuplicateButton();
            getTitlePanel().hideOverestimationButton();
            getTitlePanel().hideExternalButton();
            getTitlePanel().hideInternalButton();
        }
        if (canCreateNewTask()) {
            getTitlePanel().showCreateButton();
        } else {
            getTitlePanel().hideCreateButton();
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
        if (canDeleteTasks()) {
            getTitlePanel().showDeleteButton();
        } else {
            getTitlePanel().hideDeleteButton();
        }
        // Update title
        getTitlePanel().setText("<html>" + title + "</html>");
        getTitlePanel().repaint(); // this is necessary to force stretching of panel
    }

    @Override
    protected void setColumnModel() {
        super.setColumnModel();
        // hide Story Points and Iteration columns
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setPreferredWidth(0);
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setPreferredWidth(0);
    }

    @Override
    public void initTabs() {
        // Do nothing so this doesn't conflict with the main table
    }

    @Override
    protected void setTableHeader() {
        // no table header
        setTableHeader(null);
    }

    @Override
    protected void populateSubTable() {
        // no sub table to populate
    }

    @Override
    protected void emptySubTable() {
        // no sub table to empty
    }

    @Override
    protected ToDoList getTableList() {
        return ToDoList.getSubTaskList(panel.getMainTable().getActivityIdFromSelectedRow());
    }

    @Override
    public TitlePanel getTitlePanel() {
        return panel.getSubTableTitlePanel();
    }

    // Create specific to subtasks
    // default name (N) + New subtask
    // cell editing is done by TitleRenderer in AbstractActivitiesTable
    @Override
    public void createNewTask() {
        if (canCreateNewTask()) {
            Activity newActivity = new Activity();
            newActivity.setName("(N) " + Labels.getString("Common.New subtask"));
            // Set parent id
            Activity parentActivity = panel.getMainTable().getActivityFromSelectedRow();
            /*if (getRowCount() == 0) { // first sub-task
                newActivity.setEstimatedPoms(parentActivity.getEstimatedPoms());
                newActivity.setOverestimatedPoms(parentActivity.getOverestimatedPoms());
                newActivity.setActualPoms(parentActivity.getActualPoms());
            }*/
            newActivity.setParentId(parentActivity.getId());
            getList().add(newActivity); // save activity in database
            newActivity.setName(""); // the idea is to insert an empty title so the editing (editCellAt in TitleRenderer) shows an empty field
            insertRow(newActivity);
            panel.getTabbedPane().selectEditTab(); // open edit tab
        }
    }

    // default name: (D) + name ('(D)' is added by ActivityList)
    // no duplicate for parent table
    @Override
    public void duplicateTask() {
        if (canDuplicateTask()) {
            Activity activity = getActivityFromSelectedRow();
            try {
                Activity duplicatedActivity = getList().duplicate(activity);
                insertRow(duplicatedActivity);
                panel.getMainTable().addPomsToSelectedRow(duplicatedActivity);
                panel.getTabbedPane().selectEditTab(); // open edit tab
            } catch (CloneNotSupportedException ignored) {
            }
        }
    }

    // no default name
    // cell editing is done by TitleRenderer in AbstractActivitiesTable
    @Override
    public void createUnplannedTask() {
        if (canCreateUnplannedTask()) {
            Activity activity = new Activity();
            Activity parentActivity = panel.getMainTable().getActivityFromSelectedRow();
            activity.setParentId(parentActivity.getId());
            super.createUnplannedTask(activity);
        }
    }

    @Override
    public void createInternalInterruption() {
        if (canCreateInterruptions()) {
            Activity activity = new Activity();
            Activity parentActivity = panel.getMainTable().getActivityFromSelectedRow();
            activity.setParentId(parentActivity.getId());
            super.createInternalInterruption(activity);
        }
    }

    @Override
    public void createExternalInterruption() {
        if (canCreateInterruptions()) {
            Activity activity = new Activity();
            Activity parentActivity = panel.getMainTable().getActivityFromSelectedRow();
            activity.setParentId(parentActivity.getId());
            super.createExternalInterruption(activity);
        }
    }

    // only new subtask can be created
    // no running task or subtask, or task selected not currently running
    private boolean canCreateNewTask() {
        Activity currentToDo = panel.getPomodoro().getCurrentToDo();
        return panel.getMainTable().getSelectedRowCount() == 1
                && (!panel.getPomodoro().inPomodoro()
                || (currentToDo != null && (currentToDo.isSubTask() || panel.getMainTable().getActivityIdFromSelectedRow() != currentToDo.getId())));
    }

    @Override
    public void deleteTasks() {
        if (canDeleteTasks()) {
            DeleteButton b = new DeleteButton(Labels.getString("Common.Delete activity"), Labels.getString("Common.Are you sure to delete those activities?"), panel);
            b.doClick();
        }
    }
    
    @Override
    public void deleteTask(int rowIndex) {
        Activity activity = getActivityFromRowIndex(rowIndex);        
        getList().delete(activity); // delete tasks and subtasks
        removeRow(rowIndex);
         // set main table as current table when no subtasks anymore
        if (getRowCount() == 0) {
            panel.setCurrentTable(panel.getMainTable());
        }
    }
    
    @Override
    protected boolean canCreateInterruptions() {
        return canCreateNewTask()
                && super.canCreateInterruptions();
    }
    
    @Override
    protected boolean canCreateUnplannedTask() {
        return canCreateNewTask()
                && super.canCreateUnplannedTask();
    }

    // only subtask may be duplicated
    private boolean canDuplicateTask() {
        return canCreateNewTask()
                && getSelectedRowCount() == 1;
    }

    // only subtasks can be delete
    // no running subtask (see DeleteButton)   
    private boolean canDeleteTasks() {
        return getSelectedRowCount() > 0;
    }
    
    // can't complete subtasks
    @Override
    public void completeTask(int rowIndex) {        
    }
    
    // can't move subtasks
    @Override
    public void moveTask(int rowIndex) {
    }
}
