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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.TextAttribute;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.buttons.MoveButton;
import org.mypomodoro.gui.activities.ActivitiesTableModel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 *
 *
 */
public abstract class AbstractActivitiesTable extends JXTable {

    protected int mouseHoverRow = 0;
    protected int currentSelectedRow = 0;
    protected InputMap im;

    public AbstractActivitiesTable(ActivitiesTableModel model) {
        super(model);

        /*setBackground(ColorUtil.WHITE);// This stays White despite the background or the current theme
         setSelectionBackground(Main.selectedRowColor);
         setForeground(ColorUtil.BLACK);
         setSelectionForeground(ColorUtil.BLACK);*/
        
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

        // Manage mouse hovering
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                if (rowIndex != -1) {
                    if (getSelectedRowCount() <= 1
                            && mouseHoverRow != rowIndex) { // no multiple selection
                        showInfoForRowIndex(rowIndex);
                        mouseHoverRow = rowIndex;
                    } else if (getSelectedRowCount() > 1) { // multiple selection
                        // Display info (list of selected tasks)                            
                        showDetailsForSelectedRows();
                    }
                } else {
                    setToolTipText(null); // this way tooltip won't stick
                    mouseHoverRow = -1;
                }
            }
        });
        // This is to address the case/event when the mouse exit the table
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                // Reset to currently selected task
                if (getSelectedRowCount() == 1) {
                    showInfoForSelectedRow();
                }
                mouseHoverRow = -1;
            }
        });

        // Activate Delete key stroke
        // This is a tricky one : we first use WHEN_IN_FOCUSED_WINDOW to allow the deletion of the first selected row (by default, selected with setRowSelectionInterval not mouse pressed/focus)
        // Then in ListSelectionListener we use WHEN_FOCUSED to prevent the title column to switch to edit mode when pressing the delete key
        // none of table.requestFocus(), transferFocus() and changeSelection(0, 0, false, false) will do any good here to get focus on the first row
        im = getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "Delete"); // for MAC
        } else {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete");
        }
        class deleteAction extends AbstractAction {

            final IListPanel panel;

            public deleteAction(IListPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteButton b = new DeleteButton(Labels.getString("Common.Delete activity"), Labels.getString("Common.Are you sure to delete those activities?"), panel);
                b.doClick();
            }
        }
        //am.put("Delete", new deleteAction(this)); TODO

        // Activate Shift + '>'                
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, KeyEvent.SHIFT_MASK), "Add To ToDo List");
        class moveAction extends AbstractAction {

            final IListPanel panel;

            public moveAction(IListPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                MoveButton moveButton = new MoveButton("", panel);
                moveButton.doClick();
            }
        }
        //am.put("Add To ToDo List", new moveAction(this)); TODO

        // Activate Control A
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), "Control A");
        class selectAllAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll();
            }
        }
        am.put("Control A", new selectAllAction());

        // Activate Control T (create new task)        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK), "Control T");
        class create extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                createNewTask();
            }
        }
        am.put("Control T", new create());

        // Activate Control D (duplicate task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), "Duplicate");
        class duplicate extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateTask();
            }
        }
        am.put("Duplicate", new duplicate());

        // Activate Control R (scroll back to the selected task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK), "Scroll");
        class scrollBackToTask extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                showCurrentSelectedRow();
            }
        }
        am.put("Scroll", new scrollBackToTask());
    }

    public int getActivityIdFromSelectedRow() {
        return (Integer) getModel().getValueAt(convertRowIndexToModel(getSelectedRow()), AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
    }

    public Activity getActivityFromSelectedRow() {
        return getList().getById(getActivityIdFromSelectedRow());
    }

    protected int getActivityIdFromRowIndex(int rowIndex) {
        return (Integer) getModel().getValueAt(convertRowIndexToModel(rowIndex), AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
    }

    protected Activity getActivityFromRowIndex(int rowIndex) {
        return getList().getById(getActivityIdFromRowIndex(rowIndex));
    }

    protected Activity getActivityById(int id) {
        return getList().getById(id);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (isRowSelected(row)) {
            ((JComponent) c).setBackground(Main.selectedRowColor);
            // using ((JComponent) c).getFont() to preserve current font (eg strike through)
            ((JComponent) c).setFont(((JComponent) c).getFont().deriveFont(Font.BOLD));
            ((JComponent) c).setBorder(new MatteBorder(1, 0, 1, 0, Main.rowBorderColor));
        } else if (row == mouseHoverRow) {
            ((JComponent) c).setBackground(Main.hoverRowColor);
            ((JComponent) c).setFont(((JComponent) c).getFont().deriveFont(Font.BOLD));
            Component[] comps = ((JComponent) c).getComponents();
            for (Component comp : comps) { // sub-components (combo boxes)
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
            }
            ((JComponent) c).setBorder(new MatteBorder(1, 0, 1, 0, Main.rowBorderColor));
        } else {
            if (row % 2 == 0) { // odd
                ((JComponent) c).setBackground(Main.oddRowColor); // This stays White despite the background or the current theme
            } else { // even
                ((JComponent) c).setBackground(Main.evenRowColor);
            }
            ((JComponent) c).setBorder(null);
        }
        return c;
    }

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
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
            Activity activity = getList().getById(id);
            if (activity != null && activity.isFinished()) {
                renderer.setForeground(Main.taskFinishedColor);
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
                    int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
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
            String text = (String) value;
            if (!text.isEmpty()) {
                renderer.setToolTipText(text);
            } else {
                // Set the blinking cursor and the ability to type in right away
                table.editCellAt(getSelectedRow(), AbstractTableModel.TITLE_COLUMN_INDEX, null); // edit cell
                table.setSurrendersFocusOnKeystroke(true); // focus
                if (table.getEditorComponent() != null) { // set blinking cursor
                    table.getEditorComponent().requestFocus();
                }
                renderer.setToolTipText(null);
            }
            return renderer;
        }
    }

    @Override
    public ActivitiesTableModel getModel() {
        return (ActivitiesTableModel) super.getModel();
    }

    public abstract void createNewTask();

    public abstract void duplicateTask();

    public abstract void deleteTask(int rowIndex);

    protected abstract void init();

    protected abstract void initTabs();

    protected abstract void showInfo(int activityId);

    protected abstract void showInfoForSelectedRow();

    protected abstract void showDetailsForSelectedRows();

    protected abstract void showInfoForRowIndex(int rowIndex);

    protected abstract void setTitle();

    protected abstract void setTableHeader();

    protected abstract ActivityList getList();

    protected abstract ActivityList getTableList();

    protected abstract TableTitlePanel getTitlePanel();

    protected abstract void removeRow(int rowIndex);

    protected abstract void insertRow(Activity activity);
}
