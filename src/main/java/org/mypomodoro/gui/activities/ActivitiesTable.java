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
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.TextAttribute;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import org.mypomodoro.Main;
import org.mypomodoro.gui.AbstractActivitiesTable;
import org.mypomodoro.gui.create.list.TypeList;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ColumnResizer;
import org.mypomodoro.util.CustomTableHeader;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Table for activities
 *
 */
public class ActivitiesTable extends AbstractActivitiesTable {

    private final ActivitiesTableModel tableModel;
    private final ActivitiesPanel activitiesPanel;

    public ActivitiesTable(final ActivitiesTableModel tableModel, final ActivitiesPanel activitiesPanel) {
        super(tableModel);

        this.tableModel = tableModel;
        this.activitiesPanel = activitiesPanel;

        setTableHeader();

        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRowCount = getSelectedRowCount();
                if (selectedRowCount > 0) {
                    if (!e.getValueIsAdjusting()) { // ignoring the deselection event                                
                        // See above for reason to set WHEN_FOCUSED here
                        setInputMap(JTable.WHEN_FOCUSED, getInputMap());

                        if (selectedRowCount > 1) { // multiple selection
                            // diactivate/gray out unused tabs
                            activitiesPanel.getControlPane().setEnabledAt(1, false); // comment
                            activitiesPanel.getControlPane().setEnabledAt(2, false); // edit                                    
                            activitiesPanel.getControlPane().setEnabledAt(3, true); // merging                                    
                            if (activitiesPanel.getControlPane().getSelectedIndex() == 1
                                    || activitiesPanel.getControlPane().getSelectedIndex() == 2) {
                                activitiesPanel.getControlPane().setSelectedIndex(0); // switch to details panel
                            }
                            currentSelectedRow = getSelectedRows()[0]; // always selecting the first selected row (otherwise removeRow will fail)
                            // Display info (list of selected tasks)                            
                            showInfoForSelectedRows();
                        } else if (selectedRowCount == 1) {
                            // activate all panels
                            for (int index = 0; index < activitiesPanel.getControlPane().getTabCount(); index++) {
                                if (index == 3) {
                                    activitiesPanel.getControlPane().setEnabledAt(3, false); // merging
                                    if (activitiesPanel.getControlPane().getSelectedIndex() == 3) {
                                        activitiesPanel.getControlPane().setSelectedIndex(0); // switch to details panel
                                    }
                                } else {
                                    activitiesPanel.getControlPane().setEnabledAt(index, true);
                                }
                            }
                            if (activitiesPanel.getControlPane().getTabCount() > 0) { // at start-up time not yet initialised (see constructor)
                                activitiesPanel.getControlPane().setSelectedIndex(activitiesPanel.getControlPane().getSelectedIndex()); // switch to selected panel
                            }
                            currentSelectedRow = getSelectedRow();
                            showCurrentSelectedRow(); // when sorting columns, focus on selected row
                            // Display details                           
                            showInfoForSelectedRow();
                            // populate subtable
                            populateSubTable();
                        }
                        setPanelBorder();
                    }
                } else {
                    setPanelBorder();
                }
            }
        });

        init();

        // Listener on editable cells
        // Table model has a flaw: the update table event is fired whenever once click on an editable cell
        // To avoid update overhead, we compare old value with new value
        // (we could also have used solution found at https://tips4java.wordpress.com/2009/06/07/table-cell-listener        
        tableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (row != -1
                        && e.getType() == TableModelEvent.UPDATE) {
                    ActivitiesTableModel model = (ActivitiesTableModel) e.getSource();
                    Object data = model.getValueAt(row, column);
                    if (data != null) {
                        Integer ID = (Integer) model.getValueAt(row, getColumnCount() - 1); // ID
                        Activity act = Activity.getActivity(ID.intValue());
                        if (column == getColumnCount() - 1 - 5) { // Title (can't be empty)
                            String name = data.toString().trim();
                            if (!name.equals(act.getName())) {
                                if (name.length() == 0) {
                                    // reset the original value. Title can't be empty.
                                    model.setValueAt(act.getName(), convertRowIndexToModel(row), getColumnCount() - 1 - 5);
                                } else {
                                    act.setName(name);
                                    act.databaseUpdate();
                                    // The customer resizer may resize the title column to fit the length of the new text
                                    //ColumnResizer.adjustColumnPreferredWidths(this);
                                    revalidate();
                                }
                            }
                        } else if (column == getColumnCount() - 1 - 4) { // Type
                            String type = data.toString().trim();
                            if (!type.equals(act.getType())) {
                                act.setType(type);
                                act.databaseUpdate();
                                // load template for user stories
                                if (Main.preferences.getAgileMode()) {
                                    activitiesPanel.getCommentPanel().showInfo(act);
                                }
                                // refresh the combo boxes of all rows to display the new type (if any)
                                String[] types = (String[]) TypeList.getTypes().toArray(new String[0]);
                                getColumnModel().getColumn(getColumnCount() - 1 - 4).setCellRenderer(new ActivitiesComboBoxCellRenderer(types, true));
                                getColumnModel().getColumn(getColumnCount() - 1 - 4).setCellEditor(new ActivitiesComboBoxCellEditor(types, true));
                            }
                        } else if (column == getColumnCount() - 1 - 3) { // Estimated
                            int estimated = (Integer) data;
                            if (estimated != act.getEstimatedPoms()
                                    && estimated + act.getOverestimatedPoms() >= act.getActualPoms()) {
                                act.setEstimatedPoms(estimated);
                                act.databaseUpdate();
                            }
                        } else if (column == getColumnCount() - 1 - 2) { // Story Points
                            Float storypoints = (Float) data;
                            if (storypoints != act.getStoryPoints()) {
                                act.setStoryPoints(storypoints);
                                act.databaseUpdate();
                            }
                        } else if (column == getColumnCount() - 1 - 1) { // Iteration 
                            int iteration = Integer.parseInt(data.toString());
                            if (iteration != act.getIteration()) {
                                act.setIteration(iteration);
                                act.databaseUpdate();
                            }
                        }
                        getList().update(act);
                        // Refresh panel border after updating the list
                        setPanelBorder();
                        // update info
                        activitiesPanel.getDetailsPanel().selectInfo(act);
                        activitiesPanel.getDetailsPanel().showInfo();
                    }
                }
                enableTabs();
            }
        });
    }

    @Override
    protected void init() {
        // set custom render for dates
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 7).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 6).setCellRenderer(new DateRenderer()); // date (custom renderer)
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 5).setCellRenderer(new TitleRenderer()); // title
        // type combo box
        String[] types = (String[]) TypeList.getTypes().toArray(new String[0]);
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 4).setCellRenderer(new ActivitiesComboBoxCellRenderer(types, true));
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 4).setCellEditor(new ActivitiesComboBoxCellEditor(types, true));
        // Estimated combo box
        // The values of the combo depends on the activity : see EstimatedComboBoxCellRenderer and EstimatedComboBoxCellEditor
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 3).setCellRenderer(new ActivitiesEstimatedComboBoxCellRenderer(new Integer[0], false));
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 3).setCellEditor(new ActivitiesEstimatedComboBoxCellEditor(new Integer[0], false));
        // Story Point combo box
        Float[] points = new Float[]{0f, 0.5f, 1f, 2f, 3f, 5f, 8f, 13f, 20f, 40f, 100f};
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 2).setCellRenderer(new StoryPointsComboBoxCellRenderer(points, false));
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 2).setCellEditor(new StoryPointsComboBoxCellEditor(points, false));
        // Iteration combo box
        Integer[] iterations = new Integer[102];
        for (int i = 0; i <= 101; i++) {
            iterations[i] = i - 1;
        }
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 1).setCellRenderer(new IterationComboBoxCellRenderer(iterations, false));
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 1).setCellEditor(new IterationComboBoxCellEditor(iterations, false));
        // hide story points and iteration in 'classic' mode
        if (!Main.preferences.getAgileMode()) {
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 2).setMaxWidth(0);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 2).setMinWidth(0);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 2).setPreferredWidth(0);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 1).setMaxWidth(0);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 1).setMinWidth(0);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 1).setPreferredWidth(0);
        } else {
            // Set width of columns story points, iteration
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 2).setMaxWidth(80);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 2).setMinWidth(80);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 2).setPreferredWidth(80);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 1).setMaxWidth(80);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 1).setMinWidth(80);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 1).setPreferredWidth(80);
        }
        // hide unplanned and date in Agile mode
        if (Main.preferences.getAgileMode()) {
            getColumnModel().getColumn(0).setMaxWidth(0);
            getColumnModel().getColumn(0).setMinWidth(0);
            getColumnModel().getColumn(0).setPreferredWidth(0);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 6).setMaxWidth(0);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 6).setMinWidth(0);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 6).setPreferredWidth(0);
        } else {
            // Set width of columns Unplanned and date
            getColumnModel().getColumn(0).setMaxWidth(30);
            getColumnModel().getColumn(0).setMinWidth(30);
            getColumnModel().getColumn(0).setPreferredWidth(30);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 6).setMaxWidth(90);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 6).setMinWidth(90);
            getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 6).setPreferredWidth(90);
        }
        // Set width of column estimated
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 3).setMaxWidth(80);
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 3).setMinWidth(80);
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 3).setPreferredWidth(80);
        // Set width of column type
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 4).setMaxWidth(200);
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 4).setMinWidth(200);
        getColumnModel().getColumn(tableModel.getColumnCount() - 1 - 4).setPreferredWidth(200);
        // hide ID column
        getColumnModel().getColumn(tableModel.getColumnCount() - 1).setMaxWidth(0);
        getColumnModel().getColumn(tableModel.getColumnCount() - 1).setMinWidth(0);
        getColumnModel().getColumn(tableModel.getColumnCount() - 1).setPreferredWidth(0);
        // enable sorting
        if (getModel().getRowCount() > 0) {
            setAutoCreateRowSorter(true);
        }

        enableTabs();

        // Make sure column title will fit long titles
        ColumnResizer.adjustColumnPreferredWidths(this);
        revalidate();
    }

    protected void enableTabs() {
        if (getRowCount() == 0) {
            for (int index = 0; index < activitiesPanel.getControlPane().getTabCount(); index++) {
                if (index == 4) { // import tab
                    activitiesPanel.getControlPane().setSelectedIndex(index);
                    continue;
                }
                activitiesPanel.getControlPane().setEnabledAt(index, false);
            }
        } else {
            for (int index = 0; index < activitiesPanel.getControlPane().getTabCount(); index++) {
                activitiesPanel.getControlPane().setEnabledAt(index, index != 3); // merge tab : index == 3                                   
            }
            activitiesPanel.getControlPane().setSelectedIndex(0);
        }
    }

    @Override
    protected void showInfo(int activityId) {
        Activity activity = getList().getById(activityId);
        activitiesPanel.getDetailsPanel().selectInfo(activity);
        activitiesPanel.getDetailsPanel().showInfo();
        activitiesPanel.getCommentPanel().showInfo(activity);
        activitiesPanel.getEditPanel().showInfo(activity);
    }

    @Override
    protected void showInfoForSelectedRow() {
        showInfo(getActivityIdFromSelectedRow());
    }
    
    @Override
    protected void showInfoForRowIndex(int rowIndex) {
        showInfo(getActivityIdFromRowIndex(rowIndex));
    }

    @Override
    protected void showInfoForSelectedRows() {
        String info = "";
        int[] rows = getSelectedRows();
        for (int row : rows) {
            Integer id = getActivityIdFromRowIndex(row);
            info += getList().getById(id).getName() + "<br>";
        }
        activitiesPanel.getDetailsPanel().showInfo(info);
    }

    @Override
    protected ActivityList getList() {
        return ActivityList.getList();
    }

    @Override
    protected ActivityList getTableList() {
        return ActivityList.getTableList();
    }

    @Override
    protected void setTableHeader() {
        String[] cloneColumnNames = tableModel.getColumnNames().clone();
        cloneColumnNames[tableModel.getColumnCount() - 1 - 7] = Labels.getString("Common.Unplanned");
        cloneColumnNames[tableModel.getColumnCount() - 1 - 6] = Labels.getString("Common.Date scheduled");
        cloneColumnNames[tableModel.getColumnCount() - 1 - 3] = Labels.getString("Common.Estimated") + " (+ " + Labels.getString("Common.Overestimated") + ")";
        CustomTableHeader customTableHeader = new CustomTableHeader(this, cloneColumnNames);
        setTableHeader(customTableHeader);
    }

    @Override
    protected void setPanelBorder() {
        String titleActivitiesList = Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ActivityListPanel.Activity List");
        int rowCount = getRowCount();
        if (rowCount > 0) {
            ActivityList tableList = getTableList();
            int selectedRowCount = getSelectedRowCount();
            if (selectedRowCount > 1) {
                int[] rows = getSelectedRows();
                int estimated = 0;
                int overestimated = 0;
                int real = 0;
                float storypoints = 0;
                for (int row : rows) {
                    Integer id = (Integer) tableModel.getValueAt(convertRowIndexToModel(row), getColumnCount() - 1);
                    Activity selectedActivity = activitiesPanel.getActivityById(id);
                    estimated += selectedActivity.getEstimatedPoms();
                    overestimated += selectedActivity.getOverestimatedPoms();
                    storypoints += selectedActivity.getStoryPoints();
                    real += selectedActivity.getActualPoms();
                }
                titleActivitiesList += " (" + "<span style=\"color:black; background-color:" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + selectedRowCount + "&nbsp;</span>" + "/" + rowCount + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": " + "<span style=\"color:black; background-color:" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + real + " / " + estimated;
                if (overestimated > 0) {
                    titleActivitiesList += " + " + overestimated;
                }
                titleActivitiesList += "&nbsp;</span>";
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + "<span style=\"color:black; background-color:" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + df.format(storypoints) + "&nbsp;</span>";
                }
                // Tool tip
                String toolTipText = Labels.getString("Common.Done") + ": ";
                toolTipText += TimeConverter.getLength(real) + " / ";
                toolTipText += TimeConverter.getLength(estimated);
                if (overestimated > 0) {
                    toolTipText += " + " + TimeConverter.getLength(overestimated);
                }
                getTitlePanel().setToolTipText(toolTipText);
                // Hide buttons of the quick bar
                getTitlePanel().hideSelectedButton();
                getTitlePanel().hideDuplicateButton();
            } else {
                titleActivitiesList += " (" + rowCount + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": ";
                titleActivitiesList += tableList.getNbRealPom();
                titleActivitiesList += " / " + tableList.getNbEstimatedPom();
                if (tableList.getNbOverestimatedPom() > 0) {
                    titleActivitiesList += " + " + tableList.getNbOverestimatedPom();
                }
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(tableList.getStoryPoints());
                }
                // Tool tip
                String toolTipText = Labels.getString("Common.Done") + ": ";
                toolTipText += TimeConverter.getLength(tableList.getNbRealPom()) + " / ";
                toolTipText += TimeConverter.getLength(tableList.getNbEstimatedPom());
                if (tableList.getNbOverestimatedPom() > 0) {
                    toolTipText += " + " + TimeConverter.getLength(tableList.getNbOverestimatedPom());
                }
                getTitlePanel().setToolTipText(toolTipText);
                // Show buttons of the quick bar
                getTitlePanel().showSelectedButton();
                getTitlePanel().showDuplicateButton();
            }
            getTitlePanel().showCreateButton();
            //if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
            getTitlePanel().showRefreshButton(); // end of the line
            //}
        } else {
            getTitlePanel().hideSelectedButton();
            getTitlePanel().hideDuplicateButton();
            //if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
            getTitlePanel().hideRefreshButton();
            //}
        }
        // Update title
        getTitlePanel().setText("<html>" + titleActivitiesList + "</html>");
        //activitiesPanel.getTitlePanel().repaintLabel(); // this is necessary to force stretching of panel
        getTitlePanel().repaint();
    }

    protected void populateSubTable() {
        activitiesPanel.populateSubTable(getActivityIdFromSelectedRow());
    }

    @Override
    protected ActivitiesTableTitlePanel getTitlePanel() {
        return activitiesPanel.getTableTitlePanel();
    }

    @Override
    public void removeRow(int rowIndex) {
        clearSelection(); // clear the selection so removeRow won't fire valueChanged on ListSelectionListener (especially in case of large selection)
        tableModel.removeRow(convertRowIndexToModel(rowIndex)); // we remove in the Model...
        if (getRowCount() > 0) {
            int currentRow = currentSelectedRow > rowIndex || currentSelectedRow == getRowCount() ? currentSelectedRow - 1 : currentSelectedRow;
            setRowSelectionInterval(currentRow, currentRow); // ...while selecting in the View
            scrollRectToVisible(getCellRect(currentRow, 0, true));
        }
    }

    @Override
    public void insertRow(Activity activity) {
        clearSelection(); // clear the selection so insertRow won't fire valueChanged on ListSelectionListener (especially in case of large selection)
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
        int currentRow = convertRowIndexToView(getRowCount() - 1); // ...while selecting in the View
        setRowSelectionInterval(currentRow, currentRow);
        scrollRectToVisible(getCellRect(currentRow, 0, true));
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
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), tableModel.getColumnCount() - 1);
            Activity activity = getList().getById(id);
            if (activity != null && activity.isFinished()) {
                renderer.setForeground(ColorUtil.GREEN);
            }
            return renderer;
        }
    }

    protected class TitleRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setToolTipText((String) value);
            return renderer;
        }
    }

    protected class UnplannedRenderer extends CustomTableRenderer {

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

    protected class DateRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!DateUtil.isSameDay((Date) value, new Date(0))) {
                renderer.setText(DateUtil.getShortFormatedDate((Date) value));
                renderer.setToolTipText(DateUtil.getFormatedDate((Date) value, "EEE, dd MMM yyyy"));
                if (!Main.preferences.getAgileMode()) { // Pomodoro mode only
                    int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), tableModel.getColumnCount() - 1);
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
}
