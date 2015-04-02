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

import org.mypomodoro.gui.AbstractActivitiesTable;

/**
 *
 *
 */
public class ActivitiesTableTitlePanel extends AbstractTableTitlePanel {

    private final ActivitiesPanel activitiesPanel;
    private final AbstractActivitiesTable table;

    public ActivitiesTableTitlePanel(ActivitiesPanel activitiesPanel, AbstractActivitiesTable table) {
        super();
        this.activitiesPanel = activitiesPanel;
        this.table = table;
    }

    @Override
    protected void showCurrentSelectedRow() {
        table.showCurrentSelectedRow();
    }

    @Override
    protected void createNewTask() {
        table.createNewTask();
    }

    @Override
    protected void duplicateTask() {
        table.duplicateTask();
    }

    @Override
    protected void createUnplannedTask() {
    }

    @Override
    protected void createInternalInterruption() {
    }

    @Override
    protected void createExternalInterruption() {
    }

    @Override
    protected void refreshTable(boolean fromDatabase) {
        activitiesPanel.refresh(fromDatabase);
    }
}
