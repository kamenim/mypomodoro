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
    private JTable table;

    public CustomTableHeader(JTable table) {
        super(table.getColumnModel());
        this.table = table;
        setToolTipText(""); // default tooltip        
        setBackground(ColorUtil.BLACK);
        setFont(new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize()));
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        setDefaultRenderer(renderer);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Point p = e.getPoint();
        int viewColumnIndex = columnAtPoint(p);
        int modelColumnIndex = table.convertColumnIndexToModel(viewColumnIndex);
        if (toolTipsText[modelColumnIndex].length() == 0) {
            return super.getToolTipText(e);
        } else {
            return toolTipsText[modelColumnIndex];
        }
    }

    public void setToolTipsText(String[] myToolTipsText) {
        toolTipsText = myToolTipsText;
    }
}
