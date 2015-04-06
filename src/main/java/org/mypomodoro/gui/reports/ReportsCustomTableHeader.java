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

package org.mypomodoro.gui.reports;

import javax.swing.JTable;
import org.mypomodoro.gui.AbstractTableModel;
import org.mypomodoro.util.CustomTableHeader;
import org.mypomodoro.util.Labels;

/**
 *
 * Changing estimated colum name to 'real'
 */
public class ReportsCustomTableHeader extends CustomTableHeader {
    
    public ReportsCustomTableHeader(JTable table, String[] toolTips) {
        super(table, toolTips);
        table.getColumnModel().getColumn(AbstractTableModel.ESTIMATED_COLUMN_INDEX).setHeaderValue(Labels.getString("Common.Real"));
    }    
}
