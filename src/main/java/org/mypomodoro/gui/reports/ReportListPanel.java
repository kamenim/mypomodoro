package org.mypomodoro.gui.reports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;

/**
 * GUI for viewing the Report List.
 * 
 * @author Brian Wetzel
 */
public class ReportListPanel extends JPanel {

    private final JTable table = new JTable(getTableModel());
    private final static String[] columnNames = {"U", "Date", "Time", "Title",
        "Estimated Pomodoros", "Real Pomodoros", "Difference", "Type", "ID"};
    public static final int ID_KEY = 8;

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
    }

    private void addTabPane(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        JTabbedPane controlPane = new JTabbedPane();
        InformationArea informationArea = new InformationArea(table);
        controlPane.add("Details", informationArea);
        CommentPanel commentPanel = new CommentPanel(this);
        controlPane.add("Comment", commentPanel);
        add(controlPane, gbc);

        showSelectedItemDetails(informationArea);
        showSelectedItemComment(commentPanel);
    }

    private static TableModel getTableModel() {
        return new AbstractActivitiesTableModel(columnNames, ReportList.getList()) {

            @Override
            protected void populateData(AbstractActivities activities) {
                int rowIndex = activities.size();
                int colIndex = columnNames.length;
                tableData = new Object[rowIndex][colIndex];
                Iterator<Activity> iterator = activities.iterator();
                String pattern = "HH:mm";
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                for (int i = 0; i < activities.size() && iterator.hasNext(); i++) {
                    Activity currentActivity = iterator.next();
                    tableData[i][0] = currentActivity.isUnplanned();
                    tableData[i][1] = currentActivity.getDate();
                    tableData[i][2] = format.format(currentActivity.getDate());
                    tableData[i][3] = currentActivity.getName();
                    String poms = "" + currentActivity.getEstimatedPoms();
                    if (currentActivity.getOverestimatedPoms() > 0) {
                        poms += " + " + currentActivity.getOverestimatedPoms();
                    }
                    tableData[i][4] = poms;
                    tableData[i][5] = currentActivity.getActualPoms();
                    tableData[i][6] = currentActivity.getActualPoms() - currentActivity.getEstimatedPoms() - currentActivity.getOverestimatedPoms();
                    tableData[i][7] = currentActivity.getType();
                    tableData[i][8] = currentActivity.getId();
                }
            }
        };
    }

    private void showSelectedItemDetails(InformationArea informationArea) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ReportList.getList(),
                table, informationArea, ID_KEY));
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
        // Centre Estimated, actual and interrupted pomodoros
        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setHorizontalAlignment(SwingConstants.CENTER);
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
        table.getColumnModel().getColumn(2).setMaxWidth(40);
        table.getColumnModel().getColumn(2).setMinWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(40);
        // enable sorting
        if (table.getModel().getRowCount() > 0) {
            table.setAutoCreateRowSorter(true);
        }
        if (table.getModel().getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0); // select first row
        }
        setBorder(new TitledBorder(new EtchedBorder(), "Report List (" + ReportList.getListSize() + ")"));
    }

    public void saveComment(String comment) {
        int row = table.getSelectedRow();
        if (row > -1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
            Activity selectedReport = ReportList.getList().getById(id);
            if (selectedReport != null) {
                selectedReport.setNotes(comment);
                JFrame window = new JFrame();
                String title = "Add comment";
                String message = "Comment saved for report \"" + selectedReport.getName() + "\"";
                JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
            }
        }
    }
}