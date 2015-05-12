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

import org.mypomodoro.gui.TitlePanel;
import org.mypomodoro.Main;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.gui.create.list.SubTaskTypeList;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Table for sub-activities
 *
 */
public class ActivitiesSubTable extends ActivitiesTable {

    public ActivitiesSubTable(ActivitiesSubTableModel model, final ActivitiesPanel panel) {
        super(model, panel);
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
    protected void setColumnModel() {
        super.setColumnModel();
        // sub types
        String[] types = (String[]) SubTaskTypeList.getTypes().toArray(new String[0]);
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setCellRenderer(new ActivitiesTypeComboBoxCellRenderer(types, true));
        getColumnModel().getColumn(AbstractTableModel.TYPE_COLUMN_INDEX).setCellEditor(new ActivitiesTypeComboBoxCellEditor(types, true));
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
    protected ActivityList getTableList() {
        return ActivityList.getSubTaskList(panel.getMainTable().getActivityIdFromSelectedRow());
    }

    @Override
    public TitlePanel getTitlePanel() {
        return panel.getSubTableTitlePanel();
    }

    // no default name
    // cell editing is done by TitleRenderer in AbstractActivitiesTable
    @Override
    public void createNewTask() {
        Activity newActivity = new Activity();
        newActivity.setName("(N) " + Labels.getString("Common.New subtask"));
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
}
