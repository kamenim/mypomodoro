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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
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
    // Title    
    private final ActivitiesTableTitlePanel tableTitlePanel;
    private final ActivitiesTableTitlePanel subTableTitlePanel;
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
        // Init title and sub title
        tableTitlePanel = new ActivitiesTableTitlePanel(this, table);
        subTableTitlePanel = new ActivitiesTableTitlePanel(this, subTable);

        // select first activity of the table so the selection listener gets fired only now that both tables have been instanciated
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        // Add components        
        addTableTitlePanel();
        addTable();
        addSubTableTitlePanel();
        addSubTable();

        table.setPanelBorder();
        
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
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        for (int i = 1; i <= 6; i++) {
            im.put(KeyStroke.getKeyStroke(getKeyEvent(i), KeyEvent.CTRL_DOWN_MASK), "Tab" + i);
            am.put("Tab" + i, new tabAction(i - 1));
        }
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
        final JScrollPane subTableScrollPane = new JScrollPane(subTable);
        // One click actions
        class CustomMouseAdapter extends MouseAdapter {

            private Component comp;
            
            public CustomMouseAdapter(Component comp) {
                this.comp = comp;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // single click
                    if (subTableScrollPane.isShowing() && !(comp instanceof DefaultButton)) { // fold: excluding buttons
                        scrollPane.remove(subTableScrollPane);
                    } else if (table.getSelectedRowCount() == 1) { // expand
                        scrollPane.add(subTableScrollPane, cScrollPane);
                    }
                    scrollPane.revalidate();
                    scrollPane.repaint();
                    if (table.getSelectedRowCount() == 1) {
                        table.showCurrentSelectedRow();
                    }
                }
            }
        }
        subTableTitlePanel.addMouseListener(new CustomMouseAdapter(subTableTitlePanel));
        Component[] comps = subTableTitlePanel.getComponents();
        for (final Component comp : comps) {
            comp.addMouseListener(new CustomMouseAdapter(comp));
        }
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
        // Implement one-click action on selected tabs
        // Tab already selected = one click to expand
        // Tab not selected = double click to expand
        class CustomChangeListener implements ChangeListener {

            private boolean stateChanged = false;

            @Override
            public void stateChanged(ChangeEvent e) {
                stateChanged = true;
            }

            public boolean getStateChanged() {
                return stateChanged;
            }

            public void setStateChanged(boolean stateChanged) {
                this.stateChanged = stateChanged;
            }
        }
        final CustomChangeListener customChangeListener = new CustomChangeListener();
        controlPane.addChangeListener(customChangeListener);
        controlPane.addMouseListener(new MouseAdapter() {
            private int dividerLocation;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1
                        || (e.getClickCount() == 1 && !customChangeListener.getStateChanged())) {
                    // Expand
                    if (splitPane.getDividerLocation() != 0) {
                        dividerLocation = splitPane.getDividerLocation();
                        splitPane.setDividerLocation(0.0);
                    } else { // back to original position
                        splitPane.setDividerLocation(dividerLocation);
                    }
                } else {
                    customChangeListener.setStateChanged(false);
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
    }

    @Override
    public void insertRow(Activity activity) {
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
