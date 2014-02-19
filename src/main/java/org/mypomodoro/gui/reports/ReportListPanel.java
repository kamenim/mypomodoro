package org.mypomodoro.gui.reports;

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
import javax.swing.JLabel;
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
import javax.swing.table.DefaultTableCellRenderer;
import org.mypomodoro.Main;

import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityEditTableListener;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.reports.export.ExportPanel;
import org.mypomodoro.gui.reports.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import static org.mypomodoro.util.TimeConverter.getLength;

/**
 * GUI for viewing the Report List.
 *
 */
public class ReportListPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension PANE_DIMENSION = new Dimension(400, 50);
    AbstractActivitiesTableModel activitiesTableModel = getTableModel();
    private final JTable table = new JTable(activitiesTableModel);
    private final static String[] columnNames = {"U",
        Labels.getString("Common.Date"),
        Labels.getString("Common.Title"),
        Labels.getString("Common.Type"),
        Labels.getString("Common.Estimated"),
        Labels.getString("ReportListPanel.Real"),
        Labels.getString("ReportListPanel.Diff I"),
        Labels.getString("ReportListPanel.Diff II"),
        Labels.getString("Agile.Common.Story Points"),
        Labels.getString("Agile.Common.Iteration"), "ID"};
    //Labels.getString("ReportListPanel.Time")
    public static final int ID_KEY = 10;
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
        controlPane.setMinimumSize(PANE_DIMENSION);
        controlPane.setPreferredSize(PANE_DIMENSION);
        InformationPanel informationArea = new InformationPanel(table);
        controlPane.add(Labels.getString("Common.Details"), informationArea);
        EditPanel editPanel = new EditPanel();
        controlPane.add(Labels.getString("Common.Edit"), editPanel);
        CommentPanel commentPanel = new CommentPanel(this);
        controlPane.add(Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        ImportPanel importPanel = new ImportPanel();
        controlPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(ReportList.getList());
        controlPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
        /*JTabbedPane burdownChartPane = new JTabbedPane();
         BurndownChartInputPanel burndownChartInputPanel = new BurndownChartInputPanel(burdownChartPane);
         burdownChartPane.add(Labels.getString("ReportListPanel.Chart.Create"), burndownChartInputPanel);
         BurndownChartPanel burndownChart = new BurndownChartPanel(this);        
         burdownChartPane.add(Labels.getString("ReportListPanel.Chart.Chart"), new JScrollPane(burndownChart));

         controlPane.add(Labels.getString("ReportListPanel.Chart.Burndown Chart"), burdownChartPane);*/
        add(controlPane, gbc);

        showSelectedItemDetails(informationArea);
        showSelectedItemEdit(editPanel);
        showSelectedItemComment(commentPanel);
    }

    private static AbstractActivitiesTableModel getTableModel() {
        int rowIndex = ReportList.getList().size();
        int colIndex = columnNames.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = ReportList.getList().iterator();
        for (int i = 0; i < ReportList.getList().size() && iterator.hasNext(); i++) {
            Activity currentActivity = iterator.next();
            tableData[i][0] = currentActivity.isUnplanned();
            tableData[i][1] = currentActivity.getDate(); // date formated via custom renderer (DateRenderer)
            //tableData[i][2] = DateUtil.getFormatedTime(currentActivity.getDate());
            tableData[i][2] = currentActivity.getName();
            tableData[i][3] = currentActivity.getType();
            /*String poms = "" + currentActivity.getEstimatedPoms();
             if (currentActivity.getOverestimatedPoms() > 0) {
             poms += " + " + currentActivity.getOverestimatedPoms();
             }*/
            Integer poms = new Integer(currentActivity.getEstimatedPoms());
            tableData[i][4] = poms;
            tableData[i][5] = currentActivity.getActualPoms();
            tableData[i][6] = currentActivity.getActualPoms()
                    - currentActivity.getEstimatedPoms();
            tableData[i][7] = currentActivity.getOverestimatedPoms() > 0 ? currentActivity.getActualPoms()
                    - currentActivity.getEstimatedPoms()
                    - currentActivity.getOverestimatedPoms()
                    : "";
            tableData[i][8] = currentActivity.getStoryPoints();
            tableData[i][9] = currentActivity.getIteration();
            tableData[i][10] = currentActivity.getId();
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
                    case 4:
                        return Integer.class;
                    case 8:
                        return Float.class;
                    case 9:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
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

    private void showSelectedItemDetails(InformationPanel informationPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ReportList.getList(),
                        table, informationPanel, ID_KEY));
    }

    private void showSelectedItemEdit(EditPanel editPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityEditTableListener(ReportList.getList(), table,
                        editPanel, ID_KEY));
    }

    private void showSelectedItemComment(CommentPanel commentPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ReportList.getList(),
                        table, commentPanel, ID_KEY));
    }

    public void refresh() {
        try {
            activitiesTableModel = getTableModel();
            table.setModel(activitiesTableModel);
        } catch (Exception e) {
            // do nothing
        }
        init();
    }

    static class CustomTableRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            JLabel renderer = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setFont(isSelected ? new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize()) : table.getFont());
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            return renderer;
        }
    }

    static class DateRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            JLabel renderer = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setFont(isSelected ? new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize()) : table.getFont());
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            renderer.setText((value == null) ? "" : DateUtil.getFormatedDate((Date) value));
            return renderer;
        }
    }

    static class StoryPointsRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            JLabel renderer = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setFont(isSelected ? new Font(table.getFont().getName(), Font.BOLD, table.getFont().getSize()) : table.getFont());
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
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

    private void init() {
        // Centre columns
        CustomTableRenderer dtcr = new CustomTableRenderer();
        // set custom render for dates
        table.getColumnModel().getColumn(ID_KEY - 9).setCellRenderer(new DateRenderer()); // date (custom renderer)
        //table.getColumnModel().getColumn(ID_KEY - 7).setCellRenderer(dtcr); // time
        table.getColumnModel().getColumn(ID_KEY - 8).setCellRenderer(dtcr); // title
        table.getColumnModel().getColumn(ID_KEY - 7).setCellRenderer(dtcr); // type
        table.getColumnModel().getColumn(ID_KEY - 6).setCellRenderer(dtcr); // estimated
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(dtcr);
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(dtcr);
        table.getColumnModel().getColumn(ID_KEY - 3).setCellRenderer(dtcr);
        table.getColumnModel().getColumn(ID_KEY - 2).setCellRenderer(new StoryPointsRenderer());
        table.getColumnModel().getColumn(ID_KEY - 1).setCellRenderer(dtcr);
        // hide story points and iteration if not Agile mode
        if (!ControlPanel.preferences.getAgileMode()) {
            table.getColumnModel().getColumn(ID_KEY - 2).setMaxWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 2).setMinWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 2).setPreferredWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 1).setMaxWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 1).setMinWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 1).setPreferredWidth(0);
        }
        // hide ID column
        table.getColumnModel().getColumn(ID_KEY).setMaxWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setMinWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setPreferredWidth(0);
        // hide unplanned in Agile mode
        if (ControlPanel.preferences.getAgileMode()) { 
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);
        } else {
            // Set width of column Unplanned
            table.getColumnModel().getColumn(0).setMaxWidth(30);
            table.getColumnModel().getColumn(0).setMinWidth(30);
            table.getColumnModel().getColumn(0).setPreferredWidth(30);
        }
        // Set width of column Date
        table.getColumnModel().getColumn(1).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setMinWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        // Set width of column Time
        /*table.getColumnModel().getColumn(2).setMaxWidth(60);
         table.getColumnModel().getColumn(2).setMinWidth(60);
         table.getColumnModel().getColumn(2).setPreferredWidth(60);*/
        // enable sorting
        if (table.getModel().getRowCount() > 0) {
            table.setAutoCreateRowSorter(true);
        }

        // Add tooltip for Title and Type colums 
        table.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = table.rowAtPoint(p);
                int columnIndex = table.columnAtPoint(p);
                if (columnIndex == ID_KEY - 8 || columnIndex == ID_KEY - 7) {
                    String value = String.valueOf(table.getModel().getValueAt(rowIndex, columnIndex));
                    value = value.length() > 0 ? value : null;
                    table.setToolTipText(value);
                } else if (columnIndex == ID_KEY - 6) { // estimated
                    String value = getLength(Integer.parseInt(String.valueOf(table.getModel().getValueAt(rowIndex, columnIndex))));
                    table.setToolTipText(value);
                } else if (columnIndex == ID_KEY - 9) { // date and time
                    String value = DateUtil.getFormatedDate((Date) table.getModel().getValueAt(rowIndex, columnIndex));
                    value += " " + DateUtil.getFormatedTime((Date) table.getModel().getValueAt(rowIndex, columnIndex));
                    table.setToolTipText(value);
                }
            }
        });
        selectReport();
        String title = Labels.getString("ReportListPanel.Report List") + " ("
                + ReportList.getListSize() + ")";
        if (ReportList.getListSize() > 0) {
            title += " - " + Labels.getString("ReportListPanel.Accuracy") + ": " + getAccuracy() + "%";
        }
        if (ControlPanel.preferences.getAgileMode()
                && ReportList.getListSize() > 0) {
            title += " - " + Labels.getString("Agile.Common.Story Points") + ": " + getStoryPoints();
        }
        setBorder(new TitledBorder(new EtchedBorder(), title));
    }

    private int getStoryPoints() {
        Iterator<Activity> act = ReportList.getList().iterator();
        int storyPoints = 0;
        int real = 0;
        Activity activity;
        while (act.hasNext()) {
            activity = act.next();
            if (activity.getStoryPoints() > 0) {
                storyPoints += activity.getStoryPoints();
            }
        }
        return storyPoints;
    }

    private int getAccuracy() {
        Iterator<Activity> act = ReportList.getList().iterator();
        int estover = 0;
        int real = 0;
        Activity activity;
        while (act.hasNext()) {
            activity = act.next();
            estover += activity.getEstimatedPoms() + activity.getOverestimatedPoms();
            real += activity.getActualPoms();
        }
        int accuracy = Math.round(((float) real / (float) estover) * 100);
        return accuracy;
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
                String title = Labels.getString("Common.Add comment");
                String message = Labels.getString("Common.Comment saved");
                JOptionPane.showConfirmDialog(Main.gui, message, title,
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void selectReport() {
        int index = 0;
        if (!ReportList.getList().isEmpty()) {
            // Report deleted (removed from the list)
            if (ReportList.getList().getById(selectedReportId) == null) {
                index = selectedRowIndex;
                // Report deleted (end of the list)
                if (ReportList.getListSize() < selectedRowIndex + 1) {
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
