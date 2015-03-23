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
import java.awt.font.TextAttribute;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.ActivitiesTableModel;
import org.mypomodoro.gui.activities.ActivitiesTableTitlePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;

/**
 *
 *
 */
public abstract class AbstractActivitiesTable extends JXTable {

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
     }*/
    public void setCurrentSelectedRow(int row) {
        currentSelectedRow = row;
    }

    public void showCurrentSelectedRow() {
        scrollRectToVisible(getCellRect(currentSelectedRow, 0, true));
    }

    // selected row BOLD
    protected class CustomTableRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            JLabel renderer = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setForeground(ColorUtil.BLACK);
            renderer.setFont(isSelected ? getFont().deriveFont(Font.BOLD) : getFont());
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), table.getModel().getColumnCount() - 1);
            Activity activity = getList().getById(id);
            if (activity != null && activity.isFinished()) {
                renderer.setForeground(ColorUtil.GREEN);
            }
            return renderer;
        }
    }

    public class UnplannedRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if ((Boolean) value) {
                if (!getFont().canDisplay('\u2714')) { // unicode tick
                    renderer.setText("U");
                } else {
                    renderer.setText("\u2714");
                }
            } else {
                renderer.setText("");
            }
            return renderer;
        }
    }

    public class DateRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!DateUtil.isSameDay((Date) value, new Date(0))) {
                renderer.setText(DateUtil.getShortFormatedDate((Date) value));
                renderer.setToolTipText(DateUtil.getFormatedDate((Date) value, "EEE, dd MMM yyyy"));
                if (!Main.preferences.getAgileMode()) { // Pomodoro mode only
                    int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), table.getModel().getColumnCount() - 1);
                    Activity activity = getList().getById(id);
                    if (activity != null && activity.isOverdue()) {
                        Map<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
                        map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                        renderer.setFont(getFont().deriveFont(map));
                    }
                }
            } else {
                renderer.setText(null);
                renderer.setToolTipText(null);
            }
            return renderer;
        }
    }

    public class TitleRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setToolTipText((String) value);
            return renderer;
        }
    }
    
    protected abstract void showInfo(int activityId);
    
    protected abstract void setPanelBorder();

    protected abstract void setTableHeader();
    
    protected abstract ActivityList getList();
    
    protected abstract ActivityList getTableList();
    
    protected abstract ActivitiesTableTitlePanel getTitlePanel();
    
    protected abstract void removeRow(int rowIndex);
    
    protected abstract void insertRow(Activity activity);
}
