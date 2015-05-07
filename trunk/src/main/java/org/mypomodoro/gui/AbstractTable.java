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
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.CompleteToDoButton;
import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.buttons.MoveButton;
import org.mypomodoro.buttons.MoveToDoButton;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.reports.ReportsPanel;
import org.mypomodoro.gui.todo.ToDoPanel;
import org.mypomodoro.gui.todo.ToDoTable;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import static org.mypomodoro.util.TimeConverter.getLength;

/**
 *
 *
 */
public abstract class AbstractTable extends JXTable {

    private final IListPanel panel;
    protected int mouseHoverRow = 0;
    protected int currentSelectedRow = 0;
    protected InputMap im;

    public AbstractTable(AbstractTableModel model, final IListPanel panel) {
        super(model);

        this.panel = panel;

        setBackground(Main.tableBackgroundColor);
        /*setSelectionBackground(Main.selectedRowColor);
         setForeground(ColorUtil.BLACK);
         setSelectionForeground(ColorUtil.BLACK);*/

        // Row height
        setRowHeight(30);

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
            
            // Way to select a row of the main table that is already selected in order to trigger AbstractListSelectionListener            
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                if (rowIndex != -1
                        && panel.getMainTable().equals(AbstractTable.this)
                        && rowIndex == getSelectedRow()) {
                    clearSelection();
                    setRowSelectionInterval(rowIndex, rowIndex);
                }
            }            

            @Override
            public void mouseExited(MouseEvent e) {
                if (panel.getCurrentTable().getSelectedRowCount() == 1) { // one selected row either on the main or the sub table
                    showInfo(panel.getCurrentTable().getActivityIdFromSelectedRow());
                } else if (panel.getCurrentTable().getSelectedRowCount() > 1) { // multiple selection
                    // Display info (list of selected tasks)                        
                    panel.getCurrentTable().showDetailsForSelectedRows();
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
        am.put("Delete", new deleteAction(panel));

        // Activate Shift + '>'                
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, KeyEvent.SHIFT_MASK), "Move right"); // move to ToDoList and complete
        class moveRightAction extends AbstractAction {

            final IListPanel panel;

            public moveRightAction(IListPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel instanceof ActivitiesPanel) { // move to ToDo list
                    MoveButton moveButton = new MoveButton("", panel);                    
                    moveButton.doClick();
                } else if (panel instanceof ToDoPanel) { // complete
                    CompleteToDoButton completeToDoButton = new CompleteToDoButton(Labels.getString("ToDoListPanel.Complete ToDo"), Labels.getString("ToDoListPanel.Are you sure to complete those ToDo?"), panel);
                    completeToDoButton.doClick();
                }
            }
        }
        am.put("Move right", new moveRightAction(panel)); 

        // Activate Shift + '<'
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, KeyEvent.SHIFT_MASK), "Move left"); // send back to ActivityList and reopen
        class moveLeftAction extends AbstractAction {

            final IListPanel panel;

            public moveLeftAction(IListPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel instanceof ReportsPanel) { // reopen
                    MoveButton moveButton = new MoveButton("", panel);
                    moveButton.doClick();
                } else if (panel instanceof ToDoPanel) { // send back to ActivityList
                    MoveToDoButton moveToDoButton = new MoveToDoButton("", panel);
                    moveToDoButton.doClick();
                }
            }
        }
        am.put("Move left", new moveLeftAction(panel));

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

        // Activate Control U (quick unplanned task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK), "Control U");
        class createUnplanned extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                createUnplannedTask();
            }
        }
        am.put("Control U", new createUnplanned());

        // Activate Control I (quick internal interruption)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK), "Control I");
        class createInternal extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                createInternalInterruption();
            }
        }
        am.put("Control I", new createInternal());

        // Activate Control E (quick internal interruption)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "Control E");
        class createExternal extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                createExternalInterruption();
            }
        }
        am.put("Control E", new createExternal());
    }

    // List selection listener
    // when a row is selected, the table becomes the 'current' table
    protected abstract class AbstractListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == getSelectionModel() && e.getFirstIndex() >= 0) { // See if this is a valid table selection
                if (!e.getValueIsAdjusting()) { // ignoring the deselection event                    
                    if (!panel.getCurrentTable().equals(AbstractTable.this)) { // switch main table / sub table
                        panel.setCurrentTable(AbstractTable.this); // set new current table 
                    }
                    customValueChanged(e);
                    setTitle();  // reset title
                } else if (getRowCount() == 0) {
                    // Way to select a row in the main table that is already selected
                    if (!panel.getMainTable().equals(AbstractTable.this)) {
                        int rowIndex = panel.getMainTable().getSelectedRow();
                        panel.getMainTable().clearSelection();
                        panel.getMainTable().setRowSelectionInterval(rowIndex, rowIndex);
                    }
                    setTitle();  // reset title
                }
            }
        }

        public abstract void customValueChanged(ListSelectionEvent e);
    }

    protected abstract class AbstractTableModelListener implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getFirstRow() != -1 && e.getType() == TableModelEvent.UPDATE) {
                customTableChanged(e);
                setTitle();  // reset title
            }
        }

        public abstract void customTableChanged(TableModelEvent e);
    }

    public int getActivityIdFromSelectedRow() {
        return (Integer) getModel().getValueAt(convertRowIndexToModel(getSelectedRow()), AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
    }

    public Activity getActivityFromSelectedRow() {
        return getList().getById(getActivityIdFromSelectedRow());
    }

    public int getActivityIdFromRowIndex(int rowIndex) {
        return (Integer) getModel().getValueAt(convertRowIndexToModel(rowIndex), AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
    }

    public Activity getActivityFromRowIndex(int rowIndex) {
        return getList().getById(getActivityIdFromRowIndex(rowIndex));
    }

    public Activity getActivityById(int id) {
        return getList().getById(id);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = null;
        try {
            c = super.prepareRenderer(renderer, row, column);
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
        } catch (ArrayIndexOutOfBoundsException ignored) {
            // do nothing
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
    public class CustomRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            JLabel renderer = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setForeground(ColorUtil.BLACK);
            renderer.setFont(isSelected ? getFont().deriveFont(Font.BOLD) : getFont());
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            Activity activity = getActivityFromRowIndex(row);
            if (activity != null) {
                if (Main.gui != null && table instanceof ToDoTable) {
                    Activity currentToDo = Main.gui.getToDoPanel().getPomodoro().getCurrentToDo();
                    if (currentToDo != null) {
                        if (Main.gui.getToDoPanel().getPomodoro().inPomodoro() && activity.getId() == currentToDo.getId()) {
                            renderer.setForeground(ColorUtil.RED);
                        }
                    }
                }
                if (activity.isFinished()) {
                    renderer.setForeground(Main.taskFinishedColor);
                }
            }
            return renderer;
        }
    }

    public class UnplannedRenderer extends CustomRenderer {

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

    public class DateRenderer extends CustomRenderer {

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

    public class ToolTipRenderer extends CustomRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = (String) value;
            if (!text.isEmpty()) {
                renderer.setToolTipText(text);
            }
            return renderer;
        }
    }

    public class TitleRenderer extends CustomRenderer {

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

    public class EstimatedCellRenderer extends CustomRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
            Activity activity = getList().getById(id);
            if (activity != null) {
                int realpoms = activity.getActualPoms();
                int estimatedpoms = activity.getEstimatedPoms();
                int overestimatedpoms = activity.getOverestimatedPoms();
                String text = activity.getActualPoms() + " / " + activity.getEstimatedPoms() + (overestimatedpoms > 0 ? " + " + overestimatedpoms : "");
                renderer.setText(text);
                renderer.setToolTipText(getLength(realpoms) + " / " + getLength(estimatedpoms) + (overestimatedpoms > 0 ? " + " + getLength(overestimatedpoms) : ""));
            }
            return renderer;
        }
    }

    public class StoryPointsCellRenderer extends CustomRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text;
            if (value.toString().equals("0.5")) {
                text = "1/2";
            } else {
                text = Math.round((Float) value) + "";
            }
            renderer.setText(text);
            return renderer;
        }
    }

    public class IterationCellRenderer extends CustomRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = value.toString();
            if (value.toString().equals("-1")) {
                text = "";
            }
            renderer.setText(text);
            return renderer;
        }
    }

    public class Diff2CellRenderer extends CustomRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), AbstractTableModel.ACTIVITYID_COLUMN_INDEX);
            Activity activity = getList().getById(id);
            String text = value.toString();
            if (activity != null && activity.getOverestimatedPoms() == 0) {
                text = "";
            }
            renderer.setText(text);
            return renderer;
        }
    }

    @Override
    public AbstractTableModel getModel() {
        return (AbstractTableModel) super.getModel();
    }

    protected abstract void setColumnModel();

    // This method is empty in sub table classes
    public void initTabs() {
        panel.getTabbedPane().initTabs(getModel().getRowCount());
    }

    // This method is empty in sub table classs
    protected void populateSubTable() {
        panel.populateSubTable(getActivityIdFromSelectedRow());
    }

    // This method is empty in sub table classs
    protected void emptySubTable() {
        panel.emptySubTable();
    }

    protected TitlePanel getTitlePanel() {
        return panel.getTableTitlePanel();
    }

    protected abstract void showInfo(int activityId);

    protected abstract void showDetailsForSelectedRows();

    protected String getDetailsForSelectedRows() {
        String info = "";
        int[] rows = getSelectedRows();
        for (int row : rows) {
            Integer id = getActivityIdFromRowIndex(row);
            info += getList().getById(id).getName() + "<br>";
        }
        return info;
    }

    protected void showInfoForSelectedRow() {
        showInfo(getActivityIdFromSelectedRow());
    }

    protected void showInfoForRowIndex(int rowIndex) {
        showInfo(getActivityIdFromRowIndex(rowIndex));
    }

    protected abstract void setTitle();

    protected abstract void setTableHeader();

    protected abstract AbstractActivities getList();

    protected abstract AbstractActivities getTableList();

    public void createNewTask() {
        // do nothing by default
    }

    public void duplicateTask() {
        // do nothing by default
    }

    public void deleteTask(int rowIndex) {
        // do nothing by default
    }

    public void moveTask(int rowIndex) {
        // do nothing by default
    }

    public void completeTask(int rowIndex) {
        // do nothing by default
    }

    public void createUnplannedTask() {
        // do nothing by default
    }

    public void createInternalInterruption() {
        // do nothing by default
    }

    public void createExternalInterruption() {
        // do nothing by default
    }

    public void overestimateTask(int poms) {
        // do nothing by default
    }

    public void removeRow(int rowIndex) {
        //clearSelection(); // clear the selection so removeRow won't fire valueChanged on ListSelectionListener (especially in case of large selection) // TODO
        getModel().removeRow(convertRowIndexToModel(rowIndex)); // we remove in the Model...
        int rowCount = getRowCount(); // get row count on the view not the model !
        if (rowCount > 0) {
            int currentRow = currentSelectedRow > rowIndex || currentSelectedRow == rowCount ? currentSelectedRow - 1 : currentSelectedRow;
            setRowSelectionInterval(currentRow, currentRow); // ...while selecting in the View
            scrollRectToVisible(getCellRect(currentRow, 0, true));
        }
    }

    public void insertRow(Activity activity) {
        //clearSelection(); // clear the selection so insertRow won't fire valueChanged on ListSelectionListener (especially in case of large selection) // TODO       
        // By default, the row is added at the bottom of the list
        // However, if one of the columns has been previously sorted the position of the row might not be the bottom position...
        getModel().addRow(activity); // we add in the Model...
        int rowCount = getRowCount(); // get row count on the view not the model !
        if (rowCount == 1) { // refresh tabs as the very first row has just been added to the table
            initTabs();
        }
        int currentRow = convertRowIndexToView(rowCount - 1); // ...while selecting in the View
        setRowSelectionInterval(currentRow, currentRow);
        scrollRectToVisible(getCellRect(currentRow, 0, true));
        /*if (panel.getMainTable().equals(this)) {
         emptySubTable();
         }*/
    }

    // This method does not need to be abstract as it's implemented by the TODO table and sub-tables
    public void reorderByPriority() {
    }

    public void saveComment(String comment) {
        if (getSelectedRowCount() == 1) {
            Activity selectedActivity = getActivityFromSelectedRow();
            if (selectedActivity != null) {
                selectedActivity.setNotes(comment);
                selectedActivity.databaseUpdateComment();
            }
        }
    }

    public void removePomsFromSelectedRow(Activity activity) {
        addPomsToSelectedRow(-activity.getActualPoms(),
                -activity.getEstimatedPoms(),
                -activity.getOverestimatedPoms());
    }

    public void addPomsToSelectedRow(Activity activity) {
        addPomsToSelectedRow(activity.getActualPoms(),
                activity.getEstimatedPoms(),
                activity.getOverestimatedPoms());
    }

    public void addPomsToSelectedRow(int realPoms, int estimatedPoms, int overestimatedPoms) {
        Activity parentActivity = getActivityFromSelectedRow();
        parentActivity.setActualPoms(parentActivity.getActualPoms() + realPoms);
        parentActivity.setEstimatedPoms(parentActivity.getEstimatedPoms() + estimatedPoms);
        parentActivity.setOverestimatedPoms(parentActivity.getOverestimatedPoms() + overestimatedPoms);
        parentActivity.databaseUpdate();
        getList().update(parentActivity);
        // For convenience, we use repaint instead of the following line:
        // panel.getMainTable().getModel().setValueAt(activity.getEstimatedPoms(), panel.getMainTable().getSelectedRow(), AbstractTableModel.ESTIMATED_COLUMN_INDEX);
        repaint(); // trigger row renderers        
    }

    public void addActivity(Activity activity) {
        getList().add(activity);
    }

    public void delete(Activity activity) {
        getList().delete(activity);
    }
}
