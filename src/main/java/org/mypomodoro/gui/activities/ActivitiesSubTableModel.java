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

/**
 * Table model for the sub-activities table
 * 
 */
public class ActivitiesSubTableModel extends ActivitiesTableModel {

    public ActivitiesSubTableModel() {
        // Empty model
        int rowIndex = 0;
        int colIndex = columnNames.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        setDataVector(tableData, columnNames);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == getColumnCount() - 1 - 5 || columnIndex == getColumnCount() - 1 - 4 || columnIndex == getColumnCount() - 1 - 3;
    }
}
