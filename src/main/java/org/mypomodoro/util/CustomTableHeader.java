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
package org.mypomodoro.util;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Custom header
 *
 */
public class CustomTableHeader extends JTableHeader {

    private final String[] toolTips;
    private final JTable customTable;

    public CustomTableHeader(JTable table, String[] toolTips) {
        this.customTable = table; // setTable(table) / getTable() won't do any good
        this.toolTips = toolTips;
        setColumnModel(table.getColumnModel());
        Border border = BorderFactory.createLineBorder(ColorUtil.BLACK);
        setBorder(border);       
        setFont(new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize()));
        
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
        
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        setDefaultRenderer(renderer);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Point p = e.getPoint();
        int viewColumnIndex = columnAtPoint(p);
        int modelColumnIndex = customTable.convertColumnIndexToModel(viewColumnIndex);
        if (toolTips[modelColumnIndex].length() == 0) {
            return super.getToolTipText(e);
        } else {
            return toolTips[modelColumnIndex];
        }
    }
}
