package org.mypomodoro.gui.reports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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

import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityEditTableListener;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.reports.export.ExportPanel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * GUI for viewing the Report List.
 * 
 * @author Brian Wetzel
 * @author Phil Karoo
 */
public class ReportListPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private final JTable table = new JTable(getTableModel());
    private final static String[] columnNames = {"U",
        Labels.getString("Common.Date"),
        Labels.getString("ReportListPanel.Time"),
        Labels.getString("Common.Title"),
        Labels.getString("ReportListPanel.Estimated"),
        Labels.getString("ReportListPanel.Real"),
        Labels.getString("ReportListPanel.Diff I"),
        Labels.getString("ReportListPanel.Diff II"),
        Labels.getString("Common.Type"), "ID"};
    public static final int ID_KEY = 9;
    private int selectedReportId = 0;
    private int selectedRowIndex = 0;

    public ReportListPanel() {
        setLayout(new GridBagLayout());
        init();

        GridBagConstraints gbc = new GridBagConstraints();

        addReportsTable(gbc);
        addTabPane(gbc);
    }

    private void addReportsTable(GridBagConstraints gbc) {
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
        InformationArea informationArea = new InformationArea(table);
        controlPane.add(Labels.getString("Common.Details"), informationArea);
        EditPanel edit = new EditPanel();
        JScrollPane editPane = new JScrollPane(edit);
        controlPane.add(Labels.getString("Common.Edit"), editPane);
        CommentPanel commentPanel = new CommentPanel(this);
        controlPane.add(Labels.getString("Common.Comment"), commentPanel);
        ExportPanel exportPanel = new ExportPanel(ReportList.getList());
        controlPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
        add(controlPane, gbc);

        showSelectedItemDetails(informationArea);
        showSelectedItemEdit(edit);
        showSelectedItemComment(commentPanel);
    }

    private static TableModel getTableModel() {
        AbstractActivitiesTableModel tableModel = new AbstractActivitiesTableModel(
                columnNames, ReportList.getList()) {

            private static final long serialVersionUID = 20110814L;

            @Override
            protected void populateData(AbstractActivities activities) {
                int rowIndex = activities.size();
                int colIndex = columnNames.length;
                tableData = new Object[rowIndex][colIndex];
                Iterator<Activity> iterator = activities.iterator();
                for (int i = 0; i < activities.size() && iterator.hasNext(); i++) {
                    Activity currentActivity = iterator.next();
                    tableData[i][0] = currentActivity.isUnplanned();
                    tableData[i][1] = DateUtil.getFormatedDate(currentActivity.getDate());
                    tableData[i][2] = DateUtil.getFormatedTime(currentActivity.getDate());
                    tableData[i][3] = currentActivity.getName();
                    String poms = "" + currentActivity.getEstimatedPoms();
                    if (currentActivity.getOverestimatedPoms() > 0) {
                        poms += " + " + currentActivity.getOverestimatedPoms();
                    }
                    tableData[i][4] = poms;
                    tableData[i][5] = currentActivity.getActualPoms();
                    tableData[i][6] = currentActivity.getActualPoms()
                            - currentActivity.getEstimatedPoms();
                    tableData[i][7] = currentActivity.getOverestimatedPoms() > 0 ? currentActivity.getActualPoms()
                            - currentActivity.getEstimatedPoms()
                            - currentActivity.getOverestimatedPoms()
                            : "";
                    tableData[i][8] = currentActivity.getType();
                    tableData[i][9] = currentActivity.getId();
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (columnIndex == ID_KEY - 6 || columnIndex == ID_KEY - 1) { // make
                    // Title
                    // and
                    // Type
                    // columns
                    // editable
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
                Object data = model.getValueAt(row, column);
                Integer ID = (Integer) model.getValueAt(row, ID_KEY); // ID
                Activity report = Activity.getActivity(ID.intValue());
                String sData = (String) data;
                if (column == ID_KEY - 6 && sData.length() > 0) { // Title
                    // (cannot
                    // be empty)
                    report.setName(sData);
                    report.databaseUpdate();
                } else if (column == ID_KEY - 1) { // Type
                    report.setType(sData);
                    report.databaseUpdate();
                }
                ReportList.getList().update(); // always refresh list
            }
        });

        return tableModel;
    }

    private void recordSelectedRowId() {
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        int row = table.getSelectedRow();
                        if (row > -1) {
                            selectedReportId = (Integer) table.getModel().getValueAt(row, ID_KEY); // ID
                            selectedRowIndex = row;
                        }
                    }
                });
    }

    private void showSelectedItemDetails(InformationArea informationArea) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ReportList.getList(),
                table, informationArea, ID_KEY));
    }

    private void showSelectedItemEdit(EditPanel editPane) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityEditTableListener(ReportList.getList(), table,
                editPane, ID_KEY));
    }

    private void showSelectedItemComment(CommentPanel commentPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ReportList.getList(),
                table, commentPanel, ID_KEY));
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
        // Centre Date, time, estimated, real, Diff I and Diff II columns
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(ID_KEY - 8).setCellRenderer(dtcr);
        table.getColumnModel().getColumn(ID_KEY - 7).setCellRenderer(dtcr);
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(dtcr);
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(dtcr);
        table.getColumnModel().getColumn(ID_KEY - 3).setCellRenderer(dtcr);
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
        // Set width of column Time
        table.getColumnModel().getColumn(2).setMaxWidth(60);
        table.getColumnModel().getColumn(2).setMinWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        // enable sorting
        if (table.getModel().getRowCount() > 0) {
            table.setAutoCreateRowSorter(true);
        }
        selectReport();
        setBorder(new TitledBorder(new EtchedBorder(),
                Labels.getString("ReportListPanel.Report List") + " ("
                + ReportList.getListSize() + ")"));
    }

    public void saveComment(String comment) {
        int row = table.getSelectedRow();
        if (row > -1) {
            Integer id = (Integer) table.getModel().getValueAt(
                    table.convertRowIndexToModel(row), ID_KEY);
            Activity selectedReport = ReportList.getList().getById(id);
            if (selectedReport != null) {
                selectedReport.setNotes(comment);
                selectedReport.databaseUpdate();
                JFrame window = new JFrame();
                String title = Labels.getString("Common.Add comment");
                String message = Labels.getString("Common.Comment saved");
                JOptionPane.showConfirmDialog(window, message, title,
                        JOptionPane.DEFAULT_OPTION);
            }
        }
    }

    private void selectReport() {
        int index = 0;
        if (!ReportList.getList().isEmpty()) {
            if (ReportList.getList().getById(selectedReportId) == null) { // Report
                // deleted
                // (removed
                // from
                // the
                // list)
                index = selectedRowIndex;
                if (ReportList.getListSize() < selectedRowIndex + 1) { // Report
                    // deleted
                    // (end
                    // of
                    // the
                    // list)
                    --index;
                }
            } else if (selectedReportId != 0) {
                Iterator<Activity> iReport = ReportList.getList().iterator();
                while (iReport.hasNext()) {
                    if (iReport.next().getId() == selectedReportId) {
                        break;
                    }
                    index++;
                }
            }
        }
        if (!ReportList.getList().isEmpty()) {
            table.setRowSelectionInterval(index, index);
        }
    }
}