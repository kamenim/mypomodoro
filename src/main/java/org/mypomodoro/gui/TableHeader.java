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
package org.mypomodoro.gui;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import org.mypomodoro.util.ColorUtil;

/**
 * Custom header
 *
 */
public class TableHeader extends JTableHeader {

    private final String[] toolTips;

    public TableHeader(JTable table, String[] toolTips) {
        this.table = table; // setTable(table) / getTable() won't do any good
        this.toolTips = toolTips;
        setColumnModel(table.getColumnModel());
        Border border = BorderFactory.createLineBorder(ColorUtil.BLACK);
        setBorder(border);
        setFont(new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize()));
        setForeground(new JTableHeader().getForeground()); // this is necessary for themes such as JTatoo Noire
        /*
         table.getColumnModel().getColumn(AbstractTableModel.PRIORITY_COLUMN_INDEX).setHeaderValue("P");
         table.getColumnModel().getColumn(AbstractTableModel.DIFFI_COLUMN_INDEX).setHeaderValue("D I");
         table.getColumnModel().getColumn(AbstractTableModel.DIFFII_COLUMN_INDEX).setHeaderValue("D II");
         table.getColumnModel().getColumn(AbstractTableModel.STORYPOINTS_COLUMN_INDEX).setHeaderValue("SP");
         table.getColumnModel().getColumn(AbstractTableModel.ITERATION_COLUMN_INDEX).setHeaderValue("Iter");*/
        /* This code sets a black border around each cell of the header but the rendering is not that nice
         final TableCellRenderer render = table.getTableHeader().getDefaultRenderer();
         setDefaultRenderer(new TableCellRenderer() {

         @Override
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
         JLabel label = (JLabel) render.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);                
         Border border = BorderFactory.createLineBorder(ColorUtil.BLACK, 1);                
         label.setBorder(border);                
         label.setHorizontalAlignment(SwingConstants.CENTER);                
         return label;
         }
         });
         */
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Point p = e.getPoint();
        int viewColumnIndex = columnAtPoint(p);
        int modelColumnIndex = table.convertColumnIndexToModel(viewColumnIndex);
        if (toolTips[modelColumnIndex].length() == 0) {
            return super.getToolTipText(e);
        } else {
            return toolTips[modelColumnIndex];
        }
    }
}
