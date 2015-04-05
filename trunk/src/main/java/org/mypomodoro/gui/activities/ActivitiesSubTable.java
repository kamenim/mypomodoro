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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.gui.create.list.SubTaskTypeList;
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

    //private final ActivitiesSubTableModel model;
    private final ActivitiesPanel panel;

    private int parentId = -1;

    public ActivitiesSubTable(ActivitiesSubTableModel model, final ActivitiesPanel panel) {
        super(model, panel);

        //this.model = model;
        this.panel = panel;

        // This is to address the case/event when the mouse exit the table
        // REplacing listener of the ActivtiesTable class constructor
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                // Reset to currently selected task
                if (panel.getTable().getSelectedRowCount() == 1) {
                    if (getSelectedRowCount() == 1) {
                        showInfoForSelectedRow();
                    } else if (getSelectedRowCount() == 0) { // selected row on the main table
                        showInfo(panel.getTable().getActivityIdFromSelectedRow());
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
        String titleActivitiesList = Labels.getString("Common.Subtasks");
        int rowCount = getRowCount();
        if (rowCount > 0) {
            int selectedRowCount = getSelectedRowCount();
            ActivityList tableList = getTableList();
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
                titleActivitiesList += " (" + "<span style=\"color:black; background-color:" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + selectedRowCount + "&nbsp;</span>" + "/" + rowCount + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": " + "<span style=\"color:black; background-color:" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + real + " / " + estimated;
                if (overestimated > 0) {
                    titleActivitiesList += " + " + overestimated;
                }
                titleActivitiesList += "&nbsp;</span>";
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
        if (panel.getTable().getRowCount() == 0
                || panel.getTable().getSelectedRowCount() > 1) {
            getTitlePanel().hideCreateButton();
        } else {
            getTitlePanel().showCreateButton();
        }
        // Update title
        getTitlePanel().setText("<html>" + titleActivitiesList + "</html>");
        //activitiesPanel.getTitlePanel().repaintLabel(); // this is necessary to force stretching of panel
        getTitlePanel().repaint();
    }

    @Override
    protected void init() {
        super.init();
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
    protected void enableTabs() {
        // Do nothing so this doesn't conflict with the main table
    }

    @Override
    protected void setTableHeader() {
        // no table header
        setTableHeader(null);
    }

    @Override
    protected void populateSubTable() {
        // no sub table
    }

    @Override
    protected ActivityList getTableList() {
        return ActivityList.getSubTaskList(parentId);
    }

    @Override
    protected TableTitlePanel getTitlePanel() {
        return panel.getSubTableTitlePanel();
    }

    @Override
    public void createNewTask() {
        Activity newActivity = new Activity();
        newActivity.setEstimatedPoms(0);
        newActivity.setName("(N) " + Labels.getString("Common.Subtask"));
        // Set parent id
        newActivity.setParentId(panel.getTable().getActivityIdFromSelectedRow());
        getList().add(newActivity); // save activity in database
        newActivity.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field        
        insertRow(newActivity);
        // Set the blinking cursor and the ability to type in right away
        editCellAt(getSelectedRow(), AbstractTableModel.TITLE_COLUMN_INDEX); // edit cell
        setSurrendersFocusOnKeystroke(true); // focus
        if (getEditorComponent() != null) {
            getEditorComponent().requestFocus();
        }
        panel.getControlPane().setSelectedIndex(2); // open edit tab
    }
}
