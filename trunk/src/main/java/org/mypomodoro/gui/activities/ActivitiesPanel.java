package org.mypomodoro.gui.activities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import org.mypomodoro.db.ActivitiesDAO;

import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityEditTableListener;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.create.EditPanel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * GUI for viewing what is in the ActivityList. This can be changed later. Right
 * now it uses a TableModel to build the JTable. Table Listeners can be added to
 * save cell edits to the ActivityCollection which can then be saved to the data
 * layer.
 * 
 * @author Brian Wetzel 
 * @author Phil Karoo
 */
public class ActivitiesPanel extends JPanel {

    JTable table = new JTable(getTableModel());
    private static final String[] columnNames = {"U", Labels.getString("Common.Date"), Labels.getString("Common.Title"), Labels.getString("Common.Estimated Pomodoros"), Labels.getString("Common.Type"), "ID"};
    public static final int ID_KEY = 5;
    private int selectedRowIndex = 0;

    public ActivitiesPanel() {
        setLayout(new GridBagLayout());
        init();

        GridBagConstraints gbc = new GridBagConstraints();

        addActivitiesTable(gbc);
        addTabPane(gbc);
    }

    private void addActivitiesTable(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(table), gbc);

        recordSelectedRowId();
    }

    private void addTabPane(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        JTabbedPane controlPane = new JTabbedPane();
        DetailsPane detailsPane = new DetailsPane(table);
        controlPane.add(Labels.getString("Common.Details"), detailsPane);
        EditPanel edit = new EditPanel();
        JScrollPane editPane = new JScrollPane(edit);
        controlPane.add(Labels.getString("ActivityListPanel.Edit"), editPane);
        add(controlPane, gbc);

        showSelectedItemDetails(detailsPane);
        showSelectedItemEdit(edit);
    }

    private static TableModel getTableModel() {
        AbstractActivitiesTableModel tableModel = new AbstractActivitiesTableModel(columnNames, ActivityList.getList()) {

            @Override
            protected void populateData(AbstractActivities activities) {
                int rowIndex = activities.size();
                int colIndex = columnNames.length;
                tableData = new Object[rowIndex][colIndex];
                Iterator<Activity> iterator = activities.iterator();
                for (int i = 0; iterator.hasNext(); i++) {
                    Activity a = iterator.next();
                    tableData[i][0] = a.isUnplanned();
                    tableData[i][1] = a.getDate();
                    tableData[i][2] = a.getName();
                    String poms = "" + a.getEstimatedPoms();
                    if (a.getOverestimatedPoms() > 0) {
                        poms += " + " + a.getOverestimatedPoms();
                    }
                    tableData[i][3] = poms;
                    tableData[i][4] = a.getType();
                    tableData[i][5] = a.getId();
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (columnIndex == 2 || columnIndex == 4) { // Title, Type columns editable
                    return true;
                } else {
                    return false;
                }
            }
        };

        tableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                TableModel model = (TableModel) e.getSource();
                String columnName = model.getColumnName(column);
                Object data = model.getValueAt(row, column);
                Integer ID = (Integer) model.getValueAt(row, ID_KEY); // ID
                if (columnName.equals(Labels.getString("Common.Title"))) {
                    Activity aa = ActivitiesDAO.getInstance().getActivity(ID.intValue());
                    aa.setName((String) data);
                }
            }
        });

        return tableModel;
    }

    private void recordSelectedRowId() {
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int row = table.getSelectedRow();
                if (row > -1) {
                    selectedRowIndex = row;
                }
            }
        });
    }

    private void showSelectedItemDetails(final DetailsPane detailsPane) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ActivityList.getList(),
                table, detailsPane, ID_KEY));
    }

    private void showSelectedItemEdit(final EditPanel editPane) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityEditTableListener(ActivityList.getList(),
                table, editPane, ID_KEY));
    }

    public void refresh() {
        try {
            table.setModel(getTableModel());
        }
        catch (Exception e) {
            // do nothing
        }
        init();
    }

    private void init() {
        // Centre Estimated pomodoros column
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(ID_KEY - 2).setCellRenderer(dtcr);
        // hide ID column
        table.getColumnModel().getColumn(ID_KEY).setMaxWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setMinWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setPreferredWidth(0);
        // Set width of column Unplanned
        table.getColumnModel().getColumn(0).setMaxWidth(30);
        table.getColumnModel().getColumn(0).setMinWidth(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        // Set width of column Date
        table.getColumnModel().getColumn(1).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setMinWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        // enable sorting
        if (table.getModel().getRowCount() > 0) {
            table.setAutoCreateRowSorter(true);
        }
        if (table.getModel().getRowCount() > 0) {
            if (table.getModel().getRowCount() < selectedRowIndex + 1) {
                selectedRowIndex = selectedRowIndex - 1;
            }
            table.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);
        }
        setBorder(new TitledBorder(new EtchedBorder(), Labels.getString("ActivityListPanel.Activity List") + " (" + ActivityList.getListSize() + ")"));
    }
}