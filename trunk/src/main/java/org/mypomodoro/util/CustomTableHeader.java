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
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.mypomodoro.Main;

/**
 * Custom header
 *
 */
public class CustomTableHeader extends JXTableHeader {

    public CustomTableHeader(JXTable table, String[] toolTips) {
        setColumnModel(table.getColumnModel());
        setBackground(ColorUtil.BLACK);
        setFont(new Font(Main.font.getName(), Font.BOLD, Main.font.getSize()));
        // add tooltips
        ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
        for (int c = 0; c < table.getColumnCount(); c++) {
            TableColumn col = table.getColumnModel().getColumn(c);
            tips.setToolTip(col, toolTips[c]);
        }
        addMouseMotionListener(tips);
        // Center column title
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        setDefaultRenderer(renderer);
    }
}

class ColumnHeaderToolTips extends MouseMotionAdapter {

    TableColumn curCol;
    Map tips;

    ColumnHeaderToolTips() {
        this.tips = new HashMap();
    }

    public void setToolTip(TableColumn col, String tooltip) {
        if (tooltip == null) {
            tips.remove(col);
        } else {
            tips.put(col, tooltip);
        }
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        JXTableHeader header = (JXTableHeader) evt.getSource();
        JTable table = header.getTable();
        TableColumnModel colModel = table.getColumnModel();
        int vColIndex = colModel.getColumnIndexAtX(evt.getX());
        TableColumn col = null;
        if (vColIndex >= 0) {
            col = colModel.getColumn(vColIndex);
        }
        if (col != curCol) {
            header.setToolTipText((String) tips.get(col));
            curCol = col;
        }
    }
}
