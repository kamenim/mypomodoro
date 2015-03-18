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

/**
 * Table for sub-activities
 *
 */
public class ActivitiesSubTable extends ActivitiesTable {
    
    private final ActivitiesPanel activitiesPanel;

    public ActivitiesSubTable(ActivitiesTableModel model, final ActivitiesPanel activitiesPanel) {
        super(model, activitiesPanel);
        
        this.activitiesPanel = activitiesPanel;
    }
    
    @Override
    protected void showInfo(int activityId) {
        Activity activity = getList().getById(activityId);
        if (activity != null) {
            activitiesPanel.getDetailsPanel().selectInfo(activity);
            activitiesPanel.getDetailsPanel().showInfo();
            activitiesPanel.getCommentPanel().showInfo(activity);
            activitiesPanel.getEditPanel().showInfo(activity);
        }
    }
    
    @Override
    protected void enableTabs() {
        // Do nothing so this doesn't conflict with the main table
    }
}
