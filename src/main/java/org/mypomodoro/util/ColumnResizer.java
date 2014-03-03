package org.mypomodoro.util;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.*;

/**
 * Column resizer Get max width for cells in column and make that the preferred
 * width found here :
 * http://niravjavadeveloper.blogspot.com/2011/05/resize-jtable-columns.html
 *
 */
public class ColumnResizer {

    public static void adjustColumnPreferredWidths(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = columnModel.getColumn(col);
            if (column.getPreferredWidth() != 0) { // not hidden
                int maxwidth = 0;
                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer rend = table.getCellRenderer(row, col);
                    Object value = table.getValueAt(table.convertRowIndexToModel(row), col);
                    Component comp = rend.getTableCellRendererComponent(table,
                            value, false, false, row, col);
                    maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
                }
                column.setPreferredWidth(maxwidth);
            }
        }
    }
}
