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
package org.mypomodoro.gui.activities;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.commons.lang3.SystemUtils;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.buttons.MoveButton;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * GUI for viewing what is in the ActivityList. This can be changed later. Right
 * now it uses a TableModel to build the JTable. Table Listeners can be added to
 * save cell edits to the ActivityCollection which can then be saved to the data
 * layer.
 *
 */
public class ActivitiesPanel extends JPanel implements IListPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension PANE_DIMENSION = new Dimension(400, 200);
    private static final Dimension TABPANE_DIMENSION = new Dimension(400, 50);
    private final JPanel scrollPane = new JPanel();
    private final JTabbedPane controlPane = new JTabbedPane();
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final EditPanel editPanel = new EditPanel(this, detailsPanel);
    private final MergingPanel mergingPanel = new MergingPanel(this);
    private final JSplitPane splitPane;
    private InputMap im = null;
    // Title    
    private final ActivitiesTableTitlePanel tableTitlePanel = new ActivitiesTableTitlePanel(this);
    private final ActivitiesTableTitlePanel subTableTitlePanel = new ActivitiesTableTitlePanel(this);
    private final GridBagConstraints cScrollPane = new GridBagConstraints(); // title + table
    // Tables
    private final ActivitiesTableModel tableModel;
    private final ActivitiesTable table;
    private final ActivitiesSubTableModel subTableModel;
    private final ActivitiesSubTable subTable;
    // Selected row
    private int currentSelectedRow = 0;

    public ActivitiesPanel() {
        setLayout(new GridBagLayout());
        
        // Top pane
        scrollPane.setMinimumSize(PANE_DIMENSION);
        scrollPane.setPreferredSize(PANE_DIMENSION);
        scrollPane.setLayout(new GridBagLayout());
        
        // Bottom pane
        controlPane.setMinimumSize(TABPANE_DIMENSION);
        controlPane.setPreferredSize(TABPANE_DIMENSION);
        addTabPane();
        
        // Split pane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, controlPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        //splitPane.setDividerSize(10);
        splitPane.setDividerSize(0); // remove divider by hiding it
        //BasicSplitPaneDivider divider = (BasicSplitPaneDivider) splitPane.getComponent(2);
        //divider.setBackground(ColorUtil.YELLOW_ROW);
        //divider.setBorder(new MatteBorder(1, 1, 1, 1, ColorUtil.BLUE_ROW));
        
        // Splitted view
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(splitPane, gbc);
        
        // Init table and sub table (data model and rendering)
        subTableModel = new ActivitiesSubTableModel();
        tableModel = new ActivitiesTableModel();
        subTable = new ActivitiesSubTable(subTableModel, this); // instance this before table
        table = new ActivitiesTable(tableModel, this);
        
        // select first activity of the table so the selection listener gets fired only now that both tables have been instanciated
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        // Add components        
        addTableTitlePanel();
        addTable();
        addSubTableTitlePanel();
        addSubTable();
    }

    private void setUpTable() {
        // Activate Delete key stroke
        // This is a tricky one : we first use WHEN_IN_FOCUSED_WINDOW to allow the deletion of the first selected row (by default, selected with setRowSelectionInterval not mouse pressed/focus)
        // Then in ListSelectionListener we use WHEN_FOCUSED to prevent the title column to switch to edit mode when pressing the delete key
        // none of table.requestFocus(), transferFocus() and changeSelection(0, 0, false, false) will do any good here to get focus on the first row
        im = table.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = table.getActionMap();
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
        am.put("Delete", new deleteAction(this));
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
        am.put("Add To ToDo List", new moveAction(this));
        // Activate Control A
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), "Control A");
        class selectAllAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.selectAll();
            }
        }
        am.put("Control A", new selectAllAction());

        // Keystroke for tab
        class tabAction extends AbstractAction {

            final int index;

            public tabAction(int index) {
                this.index = index;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (controlPane.isEnabledAt(index)) {
                    controlPane.setSelectedIndex(index);
                }
            }
        }
        for (int i = 1; i <= 6; i++) {
            im.put(KeyStroke.getKeyStroke(getKeyEvent(i), KeyEvent.CTRL_DOWN_MASK), "Tab" + i);
            am.put("Tab" + i, new tabAction(i - 1));
        }

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

    // Retrieve key event with name
    public int getKeyEvent(int index) {
        int key = 0;
        try {
            Field f = KeyEvent.class.getField("VK_" + index);
            f.setAccessible(true);
            key = (Integer) f.get(null);
        } catch (IllegalAccessException ignored) {
        } catch (IllegalArgumentException ignored) {
        } catch (NoSuchFieldException ignored) {
        } catch (SecurityException ignored) {
        }
        return key;
    }

    @Override
    public void setPanelBorder() {
    }

    ////////////////////////////////////////////////
    // TOP PANE
    ////////////////////////////////////////////////
    
    // TITLE + TABLE
    private void addTableTitlePanel() {
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 0;
        cScrollPane.weightx = 1.0;
        cScrollPane.anchor = GridBagConstraints.WEST;
        cScrollPane.fill = GridBagConstraints.BOTH;        
        scrollPane.add(tableTitlePanel, cScrollPane);
    }

    public void addTable() {
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 1;
        cScrollPane.weightx = 1.0;
        cScrollPane.weighty = 1.0;
        cScrollPane.fill = GridBagConstraints.BOTH;
        JScrollPane tableScrollPane = new JScrollPane(table);
        scrollPane.add(tableScrollPane, cScrollPane);
    }

    // TITLE + SUBTABLE
    private void addSubTableTitlePanel() {
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 2;
        cScrollPane.weightx = 1.0;
        cScrollPane.weighty = 0;
        cScrollPane.anchor = GridBagConstraints.WEST;
        cScrollPane.fill = GridBagConstraints.BOTH;
        subTable.setPanelBorder();
        scrollPane.add(subTableTitlePanel, cScrollPane);
    }

    public void addSubTable() {
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 3;
        cScrollPane.weightx = 1.0;
        cScrollPane.weighty = 1.0;
        cScrollPane.fill = GridBagConstraints.BOTH;
        final JScrollPane tableScrollPane = new JScrollPane(subTable);
        subTableTitlePanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    if (tableScrollPane.isShowing()) {
                        scrollPane.remove(tableScrollPane);
                    } else if (table.getSelectedRowCount() == 1) {
                        scrollPane.add(tableScrollPane, cScrollPane);
                        showCurrentSelectedRow(); // does not work here
                        subTable.setPanelBorder();
                    }
                    scrollPane.revalidate();
                    scrollPane.repaint();
                }
            }
        });
    }

    ////////////////////////////////////////////////
    // BOTTOM PANE
    ////////////////////////////////////////////////
    private void addTabPane() {
        controlPane.setFocusable(false); // removes borders around tab text
        controlPane.add(Labels.getString("Common.Details"), detailsPanel);
        controlPane.add(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        controlPane.add(Labels.getString("Common.Edit"), editPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Merge"), mergingPanel);
        ImportPanel importPanel = new ImportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
        controlPane.addMouseListener(new MouseAdapter() {
            private int dividerLocation;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    // Expand
                    if (splitPane.getDividerLocation() != 0) { // double left click
                        dividerLocation = splitPane.getDividerLocation();
                        splitPane.setDividerLocation(0.0);
                    } else { // back to original position
                        splitPane.setDividerLocation(dividerLocation);
                    }
                }
            }
        });
    }

    @Override
    public ActivitiesTable getTable() {
        return table;
    }
    
    public ActivitiesSubTable getSubTable() {
        return subTable;
    }

    @Override
    public int getIdKey() {
        return tableModel.getColumnCount() - 1;
    }

    @Override
    public void removeRow(int rowIndex) {
        /*table.clearSelection(); // clear the selection so removeRow won't fire valueChanged on ListSelectionListener (especially in case of large selection)
        tableModel.removeRow(table.convertRowIndexToModel(rowIndex)); // we remove in the Model...
        if (table.getRowCount() > 0) {
            int currentRow = currentSelectedRow > rowIndex || currentSelectedRow == table.getRowCount() ? currentSelectedRow - 1 : currentSelectedRow;
            table.setRowSelectionInterval(currentRow, currentRow); // ...while selecting in the View
            table.scrollRectToVisible(table.getCellRect(currentRow, 0, true));
        }*/
    }

    @Override
    public void insertRow(Activity activity) {
        /*table.clearSelection(); // clear the selection so insertRow won't fire valueChanged on ListSelectionListener (especially in case of large selection)
        Object[] rowData = new Object[8];
        rowData[0] = activity.isUnplanned();
        rowData[1] = activity.getDate();
        rowData[2] = activity.getName();
        rowData[3] = activity.getType();
        Integer poms = new Integer(activity.getEstimatedPoms());
        rowData[4] = poms;
        Float points = new Float(activity.getStoryPoints());
        rowData[5] = points;
        Integer iteration = new Integer(activity.getIteration());
        rowData[6] = iteration;
        rowData[7] = activity.getId();
        // By default, the row is added at the bottom of the list
        // However, if one of the columns has been previously sorted the position of the row might not be the bottom position...
        tableModel.addRow(rowData); // we add in the Model...        
        //tableModel.insertRow(table.getRowCount(), rowData); // we add in the Model... 
        int currentRow = table.convertRowIndexToView(table.getRowCount() - 1); // ...while selecting in the View
        table.setRowSelectionInterval(currentRow, currentRow);
        table.scrollRectToVisible(table.getCellRect(currentRow, 0, true));*/
    }

    @Override
    public void move(Activity activity) {
        getList().move(activity);
    }

    @Override
    public void moveAll() {
        // no use
    }

    @Override
    public Activity getActivityById(int id) {
        return getList().getById(id);
    }

    @Override
    public void delete(Activity activity) {
        getList().delete(activity);
    }

    @Override
    public void deleteAll() {
        getList().deleteAll();
    }

    @Override
    public void complete(Activity activity) {
        // no use
    }

    @Override
    public void completeAll() {
        // no use
    }

    @Override
    public void addActivity(Activity activity) {
        getList().add(activity);
    }

    @Override
    public void addActivity(Activity activity, Date date, Date dateCompleted) {
        getList().add(activity, date, dateCompleted);
    }

    @Override
    public void refresh() {
        refresh(false);
    }

    public void refresh(boolean fromDatabase) {
        if (!WaitCursor.isStarted()) {
            // Start wait cursor
            WaitCursor.startWaitCursor();
            try {
                if (fromDatabase) {
                    getList().refresh();
                }
                tableModel.setDataVector(getList());
                table.init();
                table.setCurrentSelectedRow(0);
                subTable.setCurrentSelectedRow(0);
                if (table.getRowCount() != 0) {
                    table.setRowSelectionInterval(0, 0);
                }
            } catch (Exception ex) {
                logger.error("", ex);
            } finally {
                // Stop wait cursor
                WaitCursor.stopWaitCursor();
            }
        }
    }

    @Override
    public void saveComment(String comment) {
        if (table.getSelectedRowCount() == 1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), tableModel.getColumnCount() - 1);
            Activity selectedActivity = getList().getById(id);
            if (selectedActivity != null) {
                selectedActivity.setNotes(comment);
                selectedActivity.databaseUpdateComment();
            }
        }
    }

    public void setCurrentSelectedRow(int row) {
        currentSelectedRow = row;
    }

    public void showCurrentSelectedRow() {
        table.scrollRectToVisible(table.getCellRect(currentSelectedRow, 0, true));
    }

    public void createNewTask() {
        Activity newActivity = new Activity();
        newActivity.setEstimatedPoms(0);
        newActivity.setName(Labels.getString("Common.Task"));
        addActivity(newActivity); // save activity in database
        newActivity.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
        insertRow(newActivity);
        // Set the blinking cursor and the ability to type in right away
        table.editCellAt(table.getSelectedRow(), tableModel.getColumnCount() - 1 - 5); // edit cell
        table.setSurrendersFocusOnKeystroke(true); // focus
        if (table.getEditorComponent() != null) {
            table.getEditorComponent().requestFocus();
        }
        controlPane.setSelectedIndex(2); // open edit tab
    }

    public void duplicateTask() {
        if (table.getSelectedRowCount() == 1) {
            int row = table.getSelectedRow();
            Integer id = (Integer) tableModel.getValueAt(table.convertRowIndexToModel(row), getIdKey());
            Activity originalCopiedActivity = getActivityById(id);
            try {
                Activity copiedActivity = originalCopiedActivity.clone(); // a clone is necessary to remove the reference/pointer to the original task                
                copiedActivity.setName("(D) " + copiedActivity.getName());
                copiedActivity.setActualPoms(0);
                copiedActivity.setOverestimatedPoms(0);
                addActivity(copiedActivity, new Date(), new Date(0));
                copiedActivity.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
                insertRow(copiedActivity);
                // Set the blinking cursor and the ability to type in right away
                table.editCellAt(table.getSelectedRow(), tableModel.getColumnCount() - 1 - 5); // edit cell
                table.setSurrendersFocusOnKeystroke(true); // focus
                if (table.getEditorComponent() != null) {
                    table.getEditorComponent().requestFocus();
                }
                controlPane.setSelectedIndex(2); // open edit tab
            } catch (CloneNotSupportedException ignored) {
            }
        }
    }

    public ActivityList getList() {
        return ActivityList.getList();
    }

    /////////////////// NEW
    public DetailsPanel getDetailsPanel() {
        return detailsPanel;
    }

    public CommentPanel getCommentPanel() {
        return commentPanel;
    }

    public EditPanel getEditPanel() {
        return editPanel;
    }

    public JTabbedPane getControlPane() {
        return controlPane;
    }

    public ActivitiesTableTitlePanel getTableTitlePanel() {
        return tableTitlePanel;
    }

    public ActivitiesTableTitlePanel getSubTableTitlePanel() {
        return subTableTitlePanel;
    }

    public void populateSubTable(int parentId) {        
        subTableModel.setDataVector(ActivityList.getSubTableList(parentId));
        subTable.setParentId(parentId);
        subTable.init();
        subTable.setPanelBorder();
    }
}
