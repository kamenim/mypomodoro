/*
 * Copyright (C) 2014
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
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Custom header
 *
 */
public class CustomTableHeader extends JTableHeader {

    private String[] toolTipsText;

    public CustomTableHeader(JTable table) {
        super(table.getColumnModel());        
        setTable(table);
        setToolTipText(""); // default tooltip        
        setBackground(ColorUtil.BLACK);
        setFont(new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize()));
        // Center column title
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        setDefaultRenderer(renderer);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Point p = e.getPoint();
        int viewColumnIndex = columnAtPoint(p);
        int modelColumnIndex = table.convertColumnIndexToModel(viewColumnIndex);
        String toolTipText;
        if (toolTipsText[modelColumnIndex].length() == 0) {
            toolTipText = super.getToolTipText(e);
        } else {
            toolTipText = toolTipsText[modelColumnIndex];
        }
        return toolTipText;
    }

    public void setToolTipsText(String[] myToolTipsText) {
        toolTipsText = myToolTipsText;
    }
}
