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
package org.mypomodoro.gui.activities;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.gui.AbstractActivitiesPanel;
import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityEditTableListener;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.gui.create.list.TypeList;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ColumnResizer;
import org.mypomodoro.util.CustomTableHeader;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import static org.mypomodoro.util.TimeConverter.getLength;

/**
 * GUI for viewing what is in the ActivityList. This can be changed later. Right
 * now it uses a TableModel to build the JTable. Table Listeners can be added to
 * save cell edits to the ActivityCollection which can then be saved to the data
 * layer.
 *
 */
public class ActivitiesPanel extends JPanel implements AbstractActivitiesPanel {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension PANE_DIMENSION = new Dimension(400, 200);
    private static final Dimension TABPANE_DIMENSION = new Dimension(400, 50);
    private AbstractActivitiesTableModel activitiesTableModel = getTableModel();
    private final JXTable table;
    private static final String[] columnNames = {"U",
        Labels.getString("Common.Date"),
        Labels.getString("Common.Title"),
        Labels.getString("Common.Type"),
        Labels.getString("Common.Estimated"),
        Labels.getString("Agile.Common.Story Points"),
        Labels.getString("Agile.Common.Iteration"),
        "ID"};
    public static int ID_KEY = 7;
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final JTabbedPane controlPane = new JTabbedPane();
    private InputMap im = null;
    private int mouseHoverRow = 0;

    public ActivitiesPanel() {
        setLayout(new GridBagLayout());
        table = new JXTable(activitiesTableModel) {

            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    ((JComponent) c).setBackground(ColorUtil.BLUE_ROW);
                } else if (row == mouseHoverRow) {
                    ((JComponent) c).setBackground(ColorUtil.YELLOW_ROW);
                    ((JComponent) c).setBorder(new MatteBorder(1, 0, 1, 0, ColorUtil.BLUE_ROW));
                    ((JComponent) c).setFont(new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize()));
                } else {
                    ((JComponent) c).setBorder(null);
                }
                return c;
            }
        };
        init();

        GridBagConstraints gbc = new GridBagConstraints();

        addActivitiesTable(gbc);
        addTabPane(gbc);
    }

    private void init() {
        table.setRowHeight(30);

        // Make table allowing multiple selections
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // set custom render for dates
        table.getColumnModel().getColumn(ID_KEY - 6).setCellRenderer(new DateRenderer()); // date (custom renderer)
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(new CustomTableRenderer()); // title
        // type combo box
        String[] types = (String[]) TypeList.getTypes().toArray(new String[0]);
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(new ComboBoxCellRenderer(types, true));
        table.getColumnModel().getColumn(ID_KEY - 4).setCellEditor(new ComboBoxCellEditor(types, true));
        // Estimated combo box
        Integer[] poms = new Integer[PreferencesPanel.preferences.getMaxNbPomPerActivity() + 1];
        for (int i = 0; i <= PreferencesPanel.preferences.getMaxNbPomPerActivity(); i++) {
            poms[i] = i;
        }
        table.getColumnModel().getColumn(ID_KEY - 3).setCellRenderer(new EstimatedComboBoxCellRenderer(poms, false));
        table.getColumnModel().getColumn(ID_KEY - 3).setCellEditor(new EstimatedComboBoxCellEditor(poms, false));
        // Story Point combo box
        Float[] points = new Float[]{0f, 0.5f, 1f, 2f, 3f, 5f, 8f, 13f, 20f, 40f, 100f};
        table.getColumnModel().getColumn(ID_KEY - 2).setCellRenderer(new StoryPointsComboBoxCellRenderer(points, false));
        table.getColumnModel().getColumn(ID_KEY - 2).setCellEditor(new StoryPointsComboBoxCellEditor(points, false));
        // Iteration combo box
        Integer[] iterations = new Integer[102];
        for (int i = 0; i <= 101; i++) {
            iterations[i] = i - 1;
        }
        table.getColumnModel().getColumn(ID_KEY - 1).setCellRenderer(new IterationComboBoxCellRenderer(iterations, false));
        table.getColumnModel().getColumn(ID_KEY - 1).setCellEditor(new IterationComboBoxCellEditor(iterations, false));
        // hide story points and iteration in 'classic' mode
        if (!PreferencesPanel.preferences.getAgileMode()) {
            table.getColumnModel().getColumn(ID_KEY - 2).setMaxWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 2).setMinWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 2).setPreferredWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 1).setMaxWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 1).setMinWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 1).setPreferredWidth(0);
        } else {
            // Set width of columns story points, iteration
            table.getColumnModel().getColumn(ID_KEY - 2).setMaxWidth(80);
            table.getColumnModel().getColumn(ID_KEY - 2).setMinWidth(80);
            table.getColumnModel().getColumn(ID_KEY - 2).setPreferredWidth(80);
            table.getColumnModel().getColumn(ID_KEY - 1).setMaxWidth(80);
            table.getColumnModel().getColumn(ID_KEY - 1).setMinWidth(80);
            table.getColumnModel().getColumn(ID_KEY - 1).setPreferredWidth(80);
        }
        // hide unplanned and date in Agile mode
        if (PreferencesPanel.preferences.getAgileMode()) {
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 6).setMaxWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 6).setMinWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 6).setPreferredWidth(0);
        } else {
            // Set width of columns Unplanned and date
            table.getColumnModel().getColumn(0).setMaxWidth(30);
            table.getColumnModel().getColumn(0).setMinWidth(30);
            table.getColumnModel().getColumn(0).setPreferredWidth(30);
            table.getColumnModel().getColumn(ID_KEY - 6).setMaxWidth(90);
            table.getColumnModel().getColumn(ID_KEY - 6).setMinWidth(90);
            table.getColumnModel().getColumn(ID_KEY - 6).setPreferredWidth(90);
        }
        // Set width of column estimated
        table.getColumnModel().getColumn(ID_KEY - 3).setMaxWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 3).setMinWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 3).setPreferredWidth(80);
        // Set width of column type
        table.getColumnModel().getColumn(ID_KEY - 4).setMaxWidth(200);
        table.getColumnModel().getColumn(ID_KEY - 4).setMinWidth(200);
        table.getColumnModel().getColumn(ID_KEY - 4).setPreferredWidth(200);
        // hide ID column
        table.getColumnModel().getColumn(ID_KEY).setMaxWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setMinWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setPreferredWidth(0);
        // enable sorting
        if (table.getModel().getRowCount() > 0) {
            table.setAutoCreateRowSorter(true);
        }

        // add tooltip to header columns
        CustomTableHeader customTableHeader = new CustomTableHeader(table);
        String[] cloneColumnNames = columnNames.clone();        
        cloneColumnNames[ID_KEY - 7] = Labels.getString("ToDoListPanel.Unplanned");
        cloneColumnNames[ID_KEY - 6] = Labels.getString("Common.Date scheduled");
        cloneColumnNames[ID_KEY - 3] = Labels.getString("Common.Estimated") + " (+" + Labels.getString("Common.Overestimated") + ")";
        customTableHeader.setToolTipsText(cloneColumnNames);
        table.setTableHeader(customTableHeader);

        // Add tooltip 
        table.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                try {
                    int rowIndex = table.rowAtPoint(p);
                    int columnIndex = table.columnAtPoint(p);
                    if (columnIndex == ID_KEY - 5 || columnIndex == ID_KEY - 4) {
                        String value = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), columnIndex));
                        value = value.length() > 0 ? value : null;
                        table.setToolTipText(value);
                    } else if (columnIndex == ID_KEY - 3) { // estimated
                        String value = getLength(Integer.parseInt(String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), columnIndex))));
                        table.setToolTipText(value);
                    } else if (columnIndex == ID_KEY - 6) { // date
                        Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), getIdKey());
                        Activity activity = getActivityById(id);
                        String value = DateUtil.getFormatedDate(activity.getDate(), "EEE dd MMM yyyy");
                        table.setToolTipText(value);
                    } else {
                        table.setToolTipText(""); // this way tooltip won't stick
                    }
                    mouseHoverRow = rowIndex;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    // do nothing. This may happen when removing rows and yet using the mouse
                } catch (IndexOutOfBoundsException ex) {
                    // do nothing. This may happen when removing rows and yet using the mouse
                }
            }
        });
        // diactivate/gray out all tabs (except import)
        if (ActivityList.getListSize() == 0) {
            for (int index = 0; index < controlPane.getComponentCount(); index++) {
                if (index == 3) { // import tab
                    continue;
                }
                controlPane.setEnabledAt(index, false);
            }
        } else {
            //table.requestFocus();
            // select first activity
            table.setRowSelectionInterval(0, 0);
        }

        // Activate Delete key stroke
        // This is a tricky one : we first use WHEN_IN_FOCUSED_WINDOW to allow the deletion of the first selected row (by default, selected with setRowSelectionInterval not mouse pressed/focus)
        // Then in ListSelectionListener we use WHEN_FOCUSED to prevent the title column to switch to edit mode when pressing the delete key
        // none of table.requestFocus(), transferFocus() and changeSelection(0, 0, false, false) will do any good here to get focus on the first row
        im = table.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = table.getActionMap();
        class deleteAction extends AbstractAction {

            final AbstractActivitiesPanel panel;

            public deleteAction(AbstractActivitiesPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteButton b = new DeleteButton(Labels.getString("ActivityListPanel.Delete activity"), Labels.getString("ActivityListPanel.Are you sure to delete those activities?"), panel);
                b.doClick();
            }
        }
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "Delete"); // for MAC
        am.put("Delete", new deleteAction(this));
        // Activate Control A
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK), "Control A");
        class selectAllAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.selectAll();
            }
        }
        am.put("Control A", new selectAllAction());

        // Refresh panel border
        setPanelBorder();

        // Make sure column title will fit long titles
        ColumnResizer.adjustColumnPreferredWidths(table);
        table.revalidate();
    }

    @Override
    public void setPanelBorder() {
        String titleActivitiesList = Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ActivityListPanel.Activity List")
                + " (" + ActivityList.getListSize() + ")";
        if (ActivityList.getListSize() > 0) {
            titleActivitiesList += " - " + Labels.getString("Common.Estimated") + ": " + ActivityList.getList().getNbEstimatedPom();
            if (ActivityList.getList().getNbOverestimatedPom() > 0) {
                titleActivitiesList += " + " + ActivityList.getList().getNbOverestimatedPom();
            }
            if (PreferencesPanel.preferences.getAgileMode()) {
                DecimalFormat df = new DecimalFormat("0.#");
                titleActivitiesList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(ActivityList.getList().getStoryPoints());
            }
            if (table.getSelectedRowCount() > 1) {
                int[] rows = table.getSelectedRows();
                int estimated = 0;
                int overestimated = 0;
                float storypoints = 0;
                for (int row : rows) {
                    Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), getIdKey());
                    Activity selectedActivity = getActivityById(id);
                    estimated += selectedActivity.getEstimatedPoms();
                    overestimated += selectedActivity.getOverestimatedPoms();
                    storypoints += selectedActivity.getStoryPoints();
                }
                titleActivitiesList += " >>> ";
                titleActivitiesList += Labels.getString("Common.Estimated") + ": " + estimated;
                if (overestimated > 0) {
                    titleActivitiesList += " + " + overestimated;
                }
                if (PreferencesPanel.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(storypoints);
                }
            }
        }
        TitledBorder titledborder = new TitledBorder(new EtchedBorder(), titleActivitiesList);
        titledborder.setTitleFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
        setBorder(titledborder);
    }

    private void addActivitiesTable(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setMinimumSize(PANE_DIMENSION);
        scrollPane.setPreferredSize(PANE_DIMENSION);
        add(scrollPane, gbc);

        // Add listener to record selected row id
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {

                        // See above for reason to set WHEN_FOCUSED here
                        table.setInputMap(JTable.WHEN_FOCUSED, im);

                        if (table.getSelectedRowCount() > 1) { // multiple selection
                            // diactivate/gray out unused tabs
                            controlPane.setEnabledAt(1, false); // edit
                            controlPane.setEnabledAt(2, false); // comment
                            if (controlPane.getSelectedIndex() == 1
                            || controlPane.getSelectedIndex() == 2) {
                                controlPane.setSelectedIndex(0); // switch to details panel
                            }
                        } else if (table.getSelectedRowCount() == 1) {
                            // activate all panels
                            for (int index = 0; index < controlPane.getComponentCount(); index++) {
                                controlPane.setEnabledAt(index, true);
                            }
                        }
                        setPanelBorder();
                    }
                });
    }

    private void addTabPane(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        controlPane.setMinimumSize(TABPANE_DIMENSION);
        controlPane.setPreferredSize(TABPANE_DIMENSION);
        controlPane.add(Labels.getString("Common.Details"), detailsPanel);
        EditPanel editPanel = new EditPanel(this, detailsPanel);
        controlPane.add(Labels.getString("Common.Edit"), editPanel);
        controlPane.add(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        ImportPanel importPanel = new ImportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
        add(controlPane, gbc);

        showSelectedItemDetails(detailsPanel);
        showSelectedItemEdit(editPanel);
        showSelectedItemComment(commentPanel);
    }

    private AbstractActivitiesTableModel getTableModel() {
        int rowIndex = ActivityList.getList().size();
        int colIndex = columnNames.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = ActivityList.getList().iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Activity a = iterator.next();
            tableData[i][0] = a.isUnplanned();
            tableData[i][1] = a.getDate();
            tableData[i][2] = a.getName();
            tableData[i][3] = a.getType();
            Integer poms = new Integer(a.getEstimatedPoms());
            tableData[i][4] = poms;
            Float points = new Float(a.getStoryPoints());
            tableData[i][5] = points;
            Integer iteration = new Integer(a.getIteration());
            tableData[i][6] = iteration;
            tableData[i][7] = a.getId();
        }

        AbstractActivitiesTableModel tableModel = new AbstractActivitiesTableModel(tableData, columnNames) {

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == ID_KEY - 5 || columnIndex == ID_KEY - 4 || columnIndex == ID_KEY - 3 || columnIndex == ID_KEY - 2 || columnIndex == ID_KEY - 1;
            }

            // this is mandatory to get columns with integers properly sorted
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 1:
                        return Date.class;
                    case 4:
                        return Integer.class;
                    case 5:
                        return Float.class;
                    case 6:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };

        // listener on editable cells
        tableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() != TableModelEvent.DELETE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    AbstractActivitiesTableModel model = (AbstractActivitiesTableModel) e.getSource();
                    Object data = model.getValueAt(row, column);
                    Integer ID = (Integer) model.getValueAt(row, ID_KEY); // ID
                    Activity act = Activity.getActivity(ID.intValue());
                    if (column == ID_KEY - 5) {
                        if (data.toString().trim().length() == 0) {
                            // reset the original value. Title can't be empty.
                            model.setValueAt(act.getName(), table.convertRowIndexToModel(row), ID_KEY - 5);
                        } else {
                            act.setName(data.toString());
                            act.databaseUpdate();
                            // The customer resizer may resize the title column to fit the length of the new text
                            ColumnResizer.adjustColumnPreferredWidths(table);
                            table.revalidate();
                        }
                    } else if (column == ID_KEY - 4) { // Type
                        act.setType(data.toString());
                        act.databaseUpdate();
                        // load template for user stories
                        if (PreferencesPanel.preferences.getAgileMode()) {
                            commentPanel.selectInfo(act);
                            commentPanel.showInfo();
                        }
                        // refresh the combo boxes of all rows to display the new type (if any)
                        String[] types = (String[]) TypeList.getTypes().toArray(new String[0]);
                        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(new ComboBoxCellRenderer(types, true));
                        table.getColumnModel().getColumn(ID_KEY - 4).setCellEditor(new ComboBoxCellEditor(types, true));
                    } else if (column == ID_KEY - 3) { // Estimated
                        act.setEstimatedPoms((Integer) data);
                        act.databaseUpdate();
                    } else if (column == ID_KEY - 2) { // Story Points
                        act.setStoryPoints((Float) data);
                        act.databaseUpdate();
                        // Refresh panel border
                        setPanelBorder();
                    } else if (column == ID_KEY - 1) { // Iteration                        
                        act.setIteration(Integer.parseInt(data.toString()));
                        act.databaseUpdate();
                    }
                    ActivityList.getList().update(act);
                    // Refresh panel border
                    setPanelBorder();
                    // update info
                    detailsPanel.selectInfo(act);
                    detailsPanel.showInfo();
                }
                // diactivate/gray out all tabs (except import)
                if (ActivityList.getListSize() == 0) {
                    for (int index = 0; index < controlPane.getComponentCount(); index++) {
                        if (index == 3) { // import tab
                            continue;
                        }
                        controlPane.setEnabledAt(index, false);
                    }
                }
            }
        });
        return tableModel;
    }

    @Override
    public JTable getTable() {
        return table;
    }

    @Override
    public int getIdKey() {
        return ID_KEY;
    }

    @Override
    public void removeRow(int rowIndex) {
        activitiesTableModel.removeRow(table.convertRowIndexToModel(rowIndex)); // we remove in the Model...
        if (table.getRowCount() > 0) {
            rowIndex = rowIndex == activitiesTableModel.getRowCount() ? rowIndex - 1 : rowIndex;
            table.setRowSelectionInterval(rowIndex, rowIndex); // ...while selecting in the View           
        }
    }

    @Override
    public void move(Activity activity) {
        ActivityList.getList().move(activity);
    }

    @Override
    public void moveAll() {
        // no use
    }

    @Override
    public Activity getActivityById(int id) {
        return ActivityList.getList().getById(id);
    }

    @Override
    public void delete(Activity activity) {
        ActivityList.getList().delete(activity);
    }

    @Override
    public void deleteAll() {
        ActivityList.getList().deleteAll();
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
        ActivityList.getList().add(activity);
    }

    // TODO listener fired twice when row selected
    private void showSelectedItemDetails(DetailsPanel detailsPane) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ActivityList.getList(),
                        table, detailsPane, ID_KEY));
    }

    private void showSelectedItemEdit(EditPanel editPane) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityEditTableListener(ActivityList.getList(), table,
                        editPane, ID_KEY));
    }

    private void showSelectedItemComment(CommentPanel commentPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ActivityList.getList(),
                        table, commentPanel, ID_KEY));
    }

    @Override
    public void refresh() {
        try {
            activitiesTableModel = getTableModel();
            table.setModel(activitiesTableModel);
        } catch (Exception e) {
            // do nothing
        }
        init();
    }

    // selected row BOLD
    class CustomTableRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            JLabel renderer = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setFont(isSelected ? new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize()) : table.getFont());
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
            Activity activity = ActivityList.getList().getById(id);
            if (activity != null && activity.isFinished()) {
                renderer.setForeground(ColorUtil.GREEN);
            }
            return renderer;
        }
    }

    class DateRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setText((value == null) ? "" : DateUtil.getFormatedDate((Date) value));
            return renderer;
        }
    }

    public void saveComment(String comment) {
        int row = table.getSelectedRow();
        if (row > -1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
            Activity selectedActivity = ActivityList.getList().getById(id);
            if (selectedActivity != null) {
                selectedActivity.setNotes(comment);
                selectedActivity.databaseUpdate();
                String title = Labels.getString("Common.Add comment");
                String message = Labels.getString("Common.Comment saved");
                JOptionPane.showConfirmDialog(Main.gui, message, title,
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
