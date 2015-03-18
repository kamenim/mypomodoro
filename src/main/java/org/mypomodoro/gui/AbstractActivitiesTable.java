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

import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.gui.activities.ActivitiesTableModel;
import org.mypomodoro.util.ColorUtil;

/**
 *
 *
 */
public class AbstractActivitiesTable extends JXTable {

    protected int mouseHoverRow = 0;
    protected int currentSelectedRow = 0;

    public AbstractActivitiesTable(ActivitiesTableModel model) {
        super(model);

        setBackground(ColorUtil.WHITE);// This stays White despite the background or the current theme
        setSelectionBackground(ColorUtil.BLUE_ROW);
        setForeground(ColorUtil.BLACK);
        setSelectionForeground(ColorUtil.BLACK);

        // Row height
        setRowHeight(30);

        // Make table allowing multiple selections
        setRowSelectionAllowed(true);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Prevent key events from editing the cell (this meanly to avoid conflicts with shortcuts)        
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField()) {

            @Override
            public boolean isCellEditable(EventObject e) {
                if (e instanceof KeyEvent) {
                    return false;
                }
                return super.isCellEditable(e);
            }
        };
        setDefaultEditor(Object.class, editor);
    }

    protected int getActivityIdFromSelectedRow() {
        return (Integer) getModel().getValueAt(convertRowIndexToModel(getSelectedRow()), getModel().getColumnCount() - 1);
    }
    
    protected int getActivityIdFromRowIndex(int rowIndex) {
        return (Integer) getModel().getValueAt(convertRowIndexToModel(rowIndex), getModel().getColumnCount() - 1);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (isRowSelected(row)) {
            ((JComponent) c).setBackground(ColorUtil.BLUE_ROW);
            // using ((JComponent) c).getFont() to preserve current font (eg strike through)
            ((JComponent) c).setFont(((JComponent) c).getFont().deriveFont(Font.BOLD));
        } else if (row == mouseHoverRow) {
            ((JComponent) c).setBackground(ColorUtil.YELLOW_ROW);
            ((JComponent) c).setFont(((JComponent) c).getFont().deriveFont(Font.BOLD));
            Component[] comps = ((JComponent) c).getComponents();
            for (Component comp : comps) { // sub-components (combo boxes)
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
            }
            ((JComponent) c).setBorder(new MatteBorder(1, 0, 1, 0, ColorUtil.BLUE_ROW));
        } else {
            if (row % 2 == 0) { // odd
                ((JComponent) c).setBackground(ColorUtil.WHITE); // This stays White despite the background or the current theme
            } else { // even
                ((JComponent) c).setBackground(ColorUtil.BLUE_ROW_LIGHT);
            }
            ((JComponent) c).setBorder(null);
        }
        return c;
    }

    /*protected int getMouseHoverRow() {
        return mouseHoverRow;
    }

    protected void setMouseHoverRow(int mouseHoverRow) {
        this.mouseHoverRow = mouseHoverRow;
    }

    protected void showInfo(int activityId) {
    }
    
    public void setCurrentSelectedRow(int row) {
        currentSelectedRow = row;
    }*/

    public void showCurrentSelectedRow() {
        scrollRectToVisible(getCellRect(currentSelectedRow, 0, true));
    }
}
