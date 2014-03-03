package org.mypomodoro.gui.activities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.mypomodoro.Main;
import org.mypomodoro.gui.AbstractActivitiesPanel;

import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityEditTableListener;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.create.list.TypeList;
import org.mypomodoro.gui.reports.export.ImportPanel;
import org.mypomodoro.gui.reports.export.ExportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ColumnResizer;
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
    private static final Dimension PANE_DIMENSION = new Dimension(400, 50);
    private AbstractActivitiesTableModel activitiesTableModel = getTableModel();
    private JTable table;
    private static final String[] columnNames = {"U",
        Labels.getString("Common.Date"),
        Labels.getString("Common.Title"),
        Labels.getString("Common.Type"),
        Labels.getString("Common.Estimated"),
        Labels.getString("Common.Overestimated"),
        Labels.getString("Agile.Common.Story Points"),
        Labels.getString("Agile.Common.Iteration"),
        "ID"};
    public static int ID_KEY = 8;
    private int selectedActivityId = 0;
    private int selectedRowIndex = 0;

    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final JTabbedPane controlPane = new JTabbedPane();

    public ActivitiesPanel() {
        setLayout(new GridBagLayout());
        table = new JTable(activitiesTableModel) {

            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    ((JComponent) c).setBackground(ColorUtil.BLUE_ROW);
                } else {
                    ((JComponent) c).setBackground(row % 2 == 0 ? Color.white : ColorUtil.YELLOW_ROW); // rows with even/odd number
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
        table.getColumnModel().getColumn(ID_KEY - 7).setCellRenderer(new DateRenderer()); // date (custom renderer)
        table.getColumnModel().getColumn(ID_KEY - 6).setCellRenderer(new CustomTableRenderer()); // title
        // type combo box
        String[] types = (String[]) TypeList.getTypes().toArray(new String[0]);
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(new ComboBoxCellRenderer(types, true));
        table.getColumnModel().getColumn(ID_KEY - 5).setCellEditor(new ComboBoxCellEditor(types, true));
        // Estimated combo box
        Integer[] poms = new Integer[ControlPanel.preferences.getMaxNbPomPerActivity() + 1];
        for (int i = 0; i <= ControlPanel.preferences.getMaxNbPomPerActivity(); i++) {
            poms[i] = i;
        }
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(new EstimatedComboBoxCellRenderer(poms, false));
        table.getColumnModel().getColumn(ID_KEY - 4).setCellEditor(new EstimatedComboBoxCellEditor(poms, false));
        // Story Point combo box
        Float[] points = new Float[]{0f, 0.5f, 1f, 2f, 3f, 5f, 8f, 13f, 20f, 40f, 100f};
        table.getColumnModel().getColumn(ID_KEY - 2).setCellRenderer(new StoryPointsComboBoxCellRenderer(points, false));
        table.getColumnModel().getColumn(ID_KEY - 2).setCellEditor(new StoryPointsComboBoxCellEditor(points, false));
        // Iteration combo box
        Integer[] iterations = new Integer[102];
        for (int i = 0; i <= 101; i++) {
            iterations[i] = i - 1;
        }
        table.getColumnModel().getColumn(ID_KEY - 1).setCellRenderer(new ComboBoxCellRenderer(iterations, false));
        table.getColumnModel().getColumn(ID_KEY - 1).setCellEditor(new ComboBoxCellEditor(iterations, false));
        // hide story points and iteration in 'classic' mode
        if (!ControlPanel.preferences.getAgileMode()) {
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
        // hide unplanned and date in Agile mode (does not make sense to have unplanned task in the backlog anyway)
        if (ControlPanel.preferences.getAgileMode()) {
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 7).setMaxWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 7).setMinWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 7).setPreferredWidth(0);
        } else {
            // Set width of columns Unplanned and date
            table.getColumnModel().getColumn(0).setMaxWidth(30);
            table.getColumnModel().getColumn(0).setMinWidth(30);
            table.getColumnModel().getColumn(0).setPreferredWidth(30);
            table.getColumnModel().getColumn(ID_KEY - 7).setMaxWidth(80);
            table.getColumnModel().getColumn(ID_KEY - 7).setMinWidth(80);
            table.getColumnModel().getColumn(ID_KEY - 7).setPreferredWidth(80);
        }
        // Set width of column estimated
        table.getColumnModel().getColumn(ID_KEY - 4).setMaxWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 4).setMinWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 4).setPreferredWidth(80);
        // Set minimum width of column type so the custom resizer won't 'shrink' it
        table.getColumnModel().getColumn(ID_KEY - 5).setMinWidth(100);
        // hide ID column
        table.getColumnModel().getColumn(ID_KEY).setMaxWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setMinWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setPreferredWidth(0);
        // hide Overstimated column
        table.getColumnModel().getColumn(ID_KEY - 3).setMaxWidth(0);
        table.getColumnModel().getColumn(ID_KEY - 3).setMinWidth(0);
        table.getColumnModel().getColumn(ID_KEY - 3).setPreferredWidth(0);
        // enable sorting
        if (table.getModel().getRowCount() > 0) {
            table.setAutoCreateRowSorter(true);
        }
        // Add tooltip 
        table.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                try {
                    int rowIndex = table.rowAtPoint(p);
                    int columnIndex = table.columnAtPoint(p);
                    if (columnIndex == ID_KEY - 6 || columnIndex == ID_KEY - 5) {
                        String value = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), columnIndex));
                        value = value.length() > 0 ? value : null;
                        table.setToolTipText(value);
                    } else if (columnIndex == ID_KEY - 4) { // estimated
                        String value = getLength(Integer.parseInt(String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), columnIndex))));
                        table.setToolTipText(value);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    // do nothing. This may happen when removing rows and yet using the mouse
                }
            }
        });
        // select first activity
        selectActivity();
        // Refresh panel border
        setPanelBorder();

        // Make sure column title will fit long titles
        ColumnResizer.adjustColumnPreferredWidths(table);
        table.revalidate();
    }

    @Override
    public void setPanelBorder() {
        String titleActivitiesList = Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "ActivityListPanel.Activity List")
                + " (" + ActivityList.getListSize() + ")";
        if (org.mypomodoro.gui.ControlPanel.preferences.getAgileMode()
                && ActivityList.getListSize() > 0) {
            titleActivitiesList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + ActivityList.getList().getStoryPoints();
        }
        setBorder(new TitledBorder(new EtchedBorder(), titleActivitiesList));
    }

    private void addActivitiesTable(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(table), gbc);

        // Add listener to record selected row id
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        int[] rows = table.getSelectedRows();
                        if (rows.length > 1) { // multiple selection
                            // diactivate/grey out unused tabs
                            controlPane.setEnabledAt(1, false); // edit
                            controlPane.setEnabledAt(2, false); // comment
                            if (controlPane.getSelectedIndex() == 1
                            || controlPane.getSelectedIndex() == 2) {
                                controlPane.setSelectedIndex(0); // switch to details panel
                            }
                        } else if (rows.length == 1) {
                            // activate panels
                            controlPane.setEnabledAt(1, true); // edit
                            controlPane.setEnabledAt(2, true); // comment

                            selectedActivityId = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rows[0]), ID_KEY); // ID
                            selectedRowIndex = rows[0];
                        }
                    }
                });
    }

    private void addTabPane(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        controlPane.setMinimumSize(PANE_DIMENSION);
        controlPane.setPreferredSize(PANE_DIMENSION);
        controlPane.add(Labels.getString("Common.Details"), detailsPanel);
        EditPanel editPanel = new EditPanel(this);
        controlPane.add(Labels.getString("Common.Edit"), editPanel);
        controlPane.add(Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        ImportPanel importPanel = new ImportPanel(true);
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
            Integer overestimatedpoms = new Integer(a.getOverestimatedPoms());
            tableData[i][5] = overestimatedpoms;
            tableData[i][6] = points;
            Integer iteration = new Integer(a.getIteration());
            tableData[i][7] = iteration;
            tableData[i][8] = a.getId();
        }

        AbstractActivitiesTableModel tableModel = new AbstractActivitiesTableModel(tableData, columnNames) {

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == ID_KEY - 6 || columnIndex == ID_KEY - 5 || columnIndex == ID_KEY - 4 || columnIndex == ID_KEY - 2 || columnIndex == ID_KEY - 1;
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
                        return Integer.class;
                    case 6:
                        return Float.class;
                    case 7:
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
                    if (column == ID_KEY - 6 && data.toString().length() > 0) { // Title (can't be empty)
                        act.setName(data.toString());
                        act.databaseUpdate();
                        // The customer resizer may resize the title column to fit the length of the new text
                        ColumnResizer.adjustColumnPreferredWidths(table);
                        table.revalidate();
                    } else if (column == ID_KEY - 5) { // Type
                        act.setType(data.toString());
                        act.databaseUpdate();
                        // load template for user stories
                        if (ControlPanel.preferences.getAgileMode()) {
                            commentPanel.selectInfo(act);
                            commentPanel.showInfo();
                        }
                        // refresh the combo boxes of all rows to display the new type (if any)
                        String[] types = (String[]) TypeList.getTypes().toArray(new String[0]);
                        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(new ComboBoxCellRenderer(types, true));
                        table.getColumnModel().getColumn(ID_KEY - 5).setCellEditor(new ComboBoxCellEditor(types, true));
                    } else if (column == ID_KEY - 4) { // Estimated
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

    // use convertRowIndexToModel to avoid the sorting of columns to mess with the move/deletion
    @Override
    public void removeRow(int rowIndex) {
        activitiesTableModel.removeRow(table.convertRowIndexToModel(rowIndex));
    }

    @Override
    public void move(Activity activity) {
        ActivityList.getList().move(activity);
    }

    @Override
    public Activity getActivityById(int id) {
        return ActivityList.getList().getById(id);
    }

    @Override
    public void delete(Activity activity) {
        ActivityList.getList().remove(activity);
    }

    @Override
    public void deleteAll() {
        ActivityList.getList().removeAll();
    }

    @Override
    public void complete(Activity activity) {
        // no use
    }

    @Override
    public void completeAll() {
        // no use
    }

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
    static class CustomTableRenderer extends DefaultTableCellRenderer {

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

    static class DateRenderer extends CustomTableRenderer {

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

    @Override
    public void selectActivity() {
        int index = 0;
        if (!ActivityList.getList().isEmpty()) {
            // Activity deleted (removed from the list)
            if (ActivityList.getList().getById(selectedActivityId) == null) {
                index = selectedRowIndex;
                // Activity deleted (end of the list)
                if (ActivityList.getListSize() < selectedRowIndex + 1) {
                    --index;
                }
            } else if (selectedActivityId != 0) {
                Iterator<Activity> iActivity = ActivityList.getList().iterator();
                while (iActivity.hasNext()) {
                    if (iActivity.next().getId() == selectedActivityId) {
                        break;
                    }
                    index++;
                }
            }
        }
        if (!ActivityList.getList().isEmpty()) {
            index = index > ActivityList.getListSize() ? 0 : index;
            table.setRowSelectionInterval(index, index);
        }
    }
}
