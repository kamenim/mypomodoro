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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.mypomodoro.Main;
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

    private final ToDoPanel panel;

    private int parentId = -1;

    public ToDoSubTable(ToDoSubTableModel model, final ToDoPanel panel) {
        super(model, panel);

        this.panel = panel;

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
                        showInfo(panel.getMainTable().getActivityIdFromSelectedRow());
                    }
                }
                mouseHoverRow = -1;
            }
        });
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    // no story points and no refresh button for subtasks
    @Override
    protected void setTitle() {
        String title = Labels.getString("Common.Subtasks");
        int rowCount = getRowCount();
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
                getTitlePanel().hideSelectedButton();
                getTitlePanel().hideDuplicateButton();
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
                getTitlePanel().showSelectedButton();
                getTitlePanel().showDuplicateButton();
            }
        } else {
            getTitlePanel().hideSelectedButton();
            getTitlePanel().hideDuplicateButton();
        }
        if (panel.getMainTable().getRowCount() == 0
                || panel.getMainTable().getSelectedRowCount() > 1) {
            getTitlePanel().hideCreateButton();
        } else {
            getTitlePanel().showCreateButton();
        }
        // Update title
        getTitlePanel().setText("<html>" + title + "</html>");
        //activitiesPanel.getTitlePanel().repaintLabel(); // this is necessary to force stretching of panel
        getTitlePanel().repaint(); // this is necessary to force stretching of panel
    }

    @Override
    protected void init() {
        super.init();
        // hide Story Points and Iteration columns
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setPreferredWidth(0);
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMaxWidth(0);
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setMinWidth(0);
        getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setPreferredWidth(0);
    }

    @Override
    protected void initTabs() {
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
        return ToDoList.getSubTaskList(parentId);
    }

    @Override
    protected TableTitlePanel getTitlePanel() {
        return panel.getSubTableTitlePanel();
    }

    // no default name
    // cell editing is done by TitleRenderer in AbstractActivitiesTable
    @Override
    public void createNewTask() {
        Activity newActivity = new Activity();
        newActivity.setName(Labels.getString("Common.New subtask"));
        // Set parent id
        Activity parentActivity = panel.getMainTable().getActivityFromSelectedRow();
        if (getRowCount() == 0) { // first sub-task
            newActivity.setEstimatedPoms(parentActivity.getEstimatedPoms());
            newActivity.setOverestimatedPoms(parentActivity.getOverestimatedPoms());
            newActivity.setActualPoms(parentActivity.getActualPoms());
        }
        newActivity.setParentId(parentActivity.getId());
        getList().add(newActivity); // save activity in database
        newActivity.setName(""); // the idea is to insert an empty title so the editing (editCellAt in TitleRenderer) shows an empty field
        insertRow(newActivity);
        panel.getTabbedPane().selectEditTab(); // open edit tab
    }

    // default name: (D) + name ('(D)' is added by ActivityList)
    // no duplicate for parent table
    @Override
    public void duplicateTask() {
        if (getSelectedRowCount() == 1) {
            Activity activity = getActivityFromSelectedRow();
            try {
                Activity duplicatedActivity = getList().duplicate(activity);
                insertRow(duplicatedActivity);
                addSubTaskEstimatedPomsToParent(duplicatedActivity);
                panel.getTabbedPane().selectEditTab(); // open edit tab
            } catch (CloneNotSupportedException ignored) {
            }
        }
    }
}
