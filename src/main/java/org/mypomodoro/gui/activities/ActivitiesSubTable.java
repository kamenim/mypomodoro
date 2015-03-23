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

    private final ActivitiesSubTableModel subTableModel;
    private final ActivitiesPanel activitiesPanel;
    
    private int parentId = -1;

    public ActivitiesSubTable(ActivitiesSubTableModel subTableModel, final ActivitiesPanel activitiesPanel) {
        super(subTableModel, activitiesPanel);

        this.subTableModel = subTableModel;
        this.activitiesPanel = activitiesPanel;
    }
    
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    // no story points and no refresh button for subtasks
    @Override
    protected void setPanelBorder() {
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
                    Integer id = (Integer) subTableModel.getValueAt(convertRowIndexToModel(row), getColumnCount() - 1);
                    Activity selectedActivity = activitiesPanel.getActivityById(id);
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
            getTitlePanel().showCreateButton();
        } else {
            getTitlePanel().hideSelectedButton();
            getTitlePanel().hideDuplicateButton();
        }
        // Update title
        getTitlePanel().setText("<html>" + titleActivitiesList + "</html>");
        //activitiesPanel.getTitlePanel().repaintLabel(); // this is necessary to force stretching of panel
        getTitlePanel().repaint();
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
        return ActivityList.getSubTableList(parentId);
    }

    @Override
    protected ActivitiesTableTitlePanel getTitlePanel() {
        return activitiesPanel.getSubTableTitlePanel();
    }
}
