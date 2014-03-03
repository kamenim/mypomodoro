package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.MuteButton;
import org.mypomodoro.gui.AbstractActivitiesPanel;

import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.reports.export.ExportPanel;
import org.mypomodoro.gui.reports.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;
import static org.mypomodoro.util.TimeConverter.getLength;

/**
 * GUI for viewing what is in the ToDoList. This can be changed later. Right now
 * it uses a DefaultTableModel to build the JTable. Table Listeners can be added
 * to save cell edits to the ActivityCollection which can then be saved to the
 * data layer.
 *
 */
public class ToDoPanel extends JPanel implements AbstractActivitiesPanel {

    private static final long serialVersionUID = 20110814L;
    //private static final Dimension PANE_DIMENSION = new Dimension(400, 50);
    private AbstractActivitiesTableModel activitiesTableModel = getTableModel();
    private JTable table;
    private static final String[] columnNames = {Labels.getString("Common.Priority"),
        Labels.getString("Common.Title"),
        Labels.getString("Common.Estimated"),
        Labels.getString("Agile.Common.Story Points"),
        Labels.getString("Agile.Common.Iteration"),
        "ID"};
    public static int ID_KEY = 5;
    private int selectedActivityId = 0;
    private int selectedRowIndex = 0;

    private final JLabel pomodoroTime = new JLabel();
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final OverestimationPanel overestimationPanel = new OverestimationPanel(this);
    private final UnplannedPanel unplannedPanel = new UnplannedPanel(this);
    private final MergingPanel mergingPanel = new MergingPanel(this);
    private final JLabel iconLabel = new JLabel("", JLabel.CENTER);
    private final Pomodoro pomodoro = new Pomodoro(this);
    //private final ToDoJList toDoJList = new ToDoJList(toDoList, pomodoro);
    private final JTabbedPane controlPane = new JTabbedPane();
    private final JLabel pomodorosRemainingLabel = new JLabel("", JLabel.LEFT);

    public ToDoPanel() {
        setLayout(new GridBagLayout());

        /*toDoJList.addListSelectionListener(new ListSelectionListener() {

         @Override
         public void valueChanged(ListSelectionEvent e) {
         JList list = (JList) e.getSource();
         Activity selectedToDo = (Activity) list.getSelectedValue();
         if (selectedToDo != null) {
         if (!pomodoro.inPomodoro()) {
         pomodoro.setCurrentToDo(selectedToDo);
         }
         toDoJList.setSelectedRowIndex(selectedToDo.getId());
         } else if (toDoList.isEmpty()) { // empty list
         refreshIconLabels();
         unplannedPanel.clearForm();
         if (pomodoro.inPomodoro()) { // completed or moved to
         // Activity List
         pomodoro.stop();
         pomodoro.getTimerPanel().setStart();
         }
         pomodoro.setCurrentToDo(null);
         // refresh remaining Pomodoros label
         PomodorosRemainingLabel.showRemainPomodoros(
         pomodorosRemainingLabel, toDoList);
         }
         }
         });*/
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

        addToDoTable(gbc);
        addTimerPanel(gbc);
        addRemainingPomodoroPanel(gbc);
        addToDoIconPanel(gbc);
        addTabPane(gbc);
    }

    private void init() {
        table.setRowHeight(30);

        // Make table allowing multiple selections
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // set custom render for title
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(new CustomTableRenderer()); // priority
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(new CustomTableRenderer()); // title                
        table.getColumnModel().getColumn(ID_KEY - 3).setCellRenderer(new EstimatedCellRenderer()); // estimated                
        table.getColumnModel().getColumn(ID_KEY - 2).setCellRenderer(new StoryPointsCellRenderer()); // Story Point
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
        // Set width of column priority
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(0).setMinWidth(40);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        // Set width of column estimated
        table.getColumnModel().getColumn(ID_KEY - 3).setMaxWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 3).setMinWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 3).setPreferredWidth(80);
        // hide ID column
        table.getColumnModel().getColumn(ID_KEY).setMaxWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setMinWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setPreferredWidth(0);
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
                    if (columnIndex == ID_KEY - 4) { // title
                        String value = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), columnIndex));
                        value = value.length() > 0 ? value : null;
                        table.setToolTipText(value);
                    } else if (columnIndex == ID_KEY - 3) { // estimated
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
    }

    @Override
    public void setPanelBorder() {
        String titleActivitiesList = Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List")
                + " (" + ToDoList.getListSize() + ")";
        if (ControlPanel.preferences.getAgileMode()
                && ToDoList.getListSize() > 0) {
            titleActivitiesList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + ToDoList.getList().getStoryPoints();
        }
        setBorder(new TitledBorder(new EtchedBorder(), titleActivitiesList));
    }

    private void addToDoTable(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 0.7;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(table), gbc);

        // Add listener to record selected row id and manage pomodoro timer
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        int[] rows = table.getSelectedRows();
                        if (rows.length > 1) { // multiple selection
                            // diactivate/gray out unused tabs
                            controlPane.setEnabledAt(1, false); // comment
                            controlPane.setEnabledAt(2, false); // overestimation
                            controlPane.setEnabledAt(3, false); // unplanned
                            if (controlPane.getSelectedIndex() == 1
                            || controlPane.getSelectedIndex() == 2
                            || controlPane.getSelectedIndex() == 3) {
                                controlPane.setSelectedIndex(0); // switch to details panel
                            }

                            if (!pomodoro.getTimer().isRunning()) {
                                // disable start button
                                pomodoro.getTimerPanel().setStartColor(ColorUtil.GRAY);
                            }
                        } else if (rows.length == 1) {
                            // activate panels
                            controlPane.setEnabledAt(1, true); // comment
                            controlPane.setEnabledAt(2, true); // overestimation
                            controlPane.setEnabledAt(3, true); // unplanned

                            if (!pomodoro.getTimer().isRunning()) {
                                // enable start button
                                pomodoro.getTimerPanel().setStartColor(Color.BLACK);
                            }

                            selectedActivityId = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rows[0]), ID_KEY); // ID
                            selectedRowIndex = rows[0];

                            // Manage pomodoro timer
                            Activity selectedToDo = getActivityById(selectedActivityId);
                            if (selectedToDo != null) {
                                if (!pomodoro.inPomodoro()) {
                                    pomodoro.setCurrentToDoId(selectedToDo.getId());
                                }
                            }
                        }
                        /*if (ToDoList.getList().isEmpty()) { // empty list
                         // diactivate/gray out panels
                         for (Component component : controlPane.getComponents()) {
                         component.setEnabled(false);
                         }

                         // disable start button
                         pomodoro.getTimerPanel().setStartColor(ColorUtil.GREEN);                            
                            
                         refreshIconLabels();
                         unplannedPanel.clearForm();
                         if (pomodoro.inPomodoro()) { // when completing or moving the whole list
                         // Activity List
                         pomodoro.stop();
                         pomodoro.getTimerPanel().setStart();
                         }
                         pomodoro.setCurrentToDo(null);
                         // refresh remaining Pomodoros label
                         PomodorosRemainingLabel.showRemainPomodoros(pomodorosRemainingLabel);
                         }*/
                    }
                });
    }

    private void addTimerPanel(GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 0.6;
        gbc.gridheight = 1;
        TimerPanel timerPanel = new TimerPanel(pomodoro, pomodoroTime, this);
        add(wrapInBackgroundImage(
                timerPanel,
                ControlPanel.preferences.getTicking() ? new MuteButton(pomodoro) : new MuteButton(pomodoro, false),
                new ImageIcon(Main.class.getResource("/images/myPomodoroIconNoTime250.png")),
                JLabel.TOP, JLabel.LEADING), gbc);
        pomodoro.setTimerPanel(timerPanel);
    }

    private void addRemainingPomodoroPanel(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        add(pomodorosRemainingLabel, gbc);
        PomodorosRemainingLabel.showRemainPomodoros(pomodorosRemainingLabel);
    }

    private void addToDoIconPanel(GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        add(iconLabel, gbc);
    }

    private void addTabPane(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        //controlPane.setMinimumSize(PANE_DIMENSION);
        //controlPane.setPreferredSize(PANE_DIMENSION);
        controlPane.add(Labels.getString("Common.Details"), detailsPanel);
        controlPane.add(Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Overestimation"), overestimationPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Unplanned"), unplannedPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Merging"), mergingPanel);
        ImportPanel importPanel = new ImportPanel(true);
        controlPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
        add(controlPane, gbc);

        showIconList();
        showSelectedItemDetails(detailsPanel);
        showSelectedItemComment(commentPanel);
        showSelectedMergeList(mergingPanel);
    }

    private AbstractActivitiesTableModel getTableModel() {
        int rowIndex = ToDoList.getList().size();
        int colIndex = columnNames.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = ToDoList.getList().iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Activity a = iterator.next();
            tableData[i][0] = a.getPriority();
            tableData[i][1] = a.getName();
            Integer poms = new Integer(a.getEstimatedPoms());
            tableData[i][2] = poms;
            Float points = new Float(a.getStoryPoints());
            tableData[i][3] = points;
            Integer iteration = new Integer(a.getIteration());
            tableData[i][4] = iteration;
            tableData[i][5] = a.getId();
        }

        AbstractActivitiesTableModel tableModel = new AbstractActivitiesTableModel(tableData, columnNames) {

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == ID_KEY - 4;
            }

            // this is mandatory to get columns with integers properly sorted
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 2:
                        return Integer.class;
                    case 3:
                        return Float.class;
                    case 4:
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
                    if (column == ID_KEY - 4 && data.toString().length() > 0) { // Title (can't be empty)
                        act.setName(data.toString());
                        act.databaseUpdate();
                    }
                    ToDoList.getList().update(act);
                    // Refresh panel border
                    //setPanelBorder();
                    // update info
                    //detailsPane.selectInfo(act);
                    //detailsPane.showInfo();
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
        ToDoList.getList().move(activity);
    }

    @Override
    public Activity getActivityById(int id) {
        return ToDoList.getList().getById(id);
    }

    @Override
    public void delete(Activity activity) {
        ToDoList.getList().remove(activity);
    }

    @Override
    public void deleteAll() {
        // no use
    }

    @Override
    public void complete(Activity activity) {
        ToDoList.getList().complete(activity);
        if (ToDoList.getList().isEmpty()) {
            pomodoro.stop();
            pomodoro.getTimerPanel().setStart();
        }
    }

    @Override
    public void completeAll() {
        ToDoList.getList().completeAll();
        pomodoro.stop();
        pomodoro.getTimerPanel().setStart();
    }

    private void showIconList() {
        table.getSelectionModel().addListSelectionListener(new ToDoIconListListener(this));
    }

    private void showSelectedItemDetails(DetailsPanel detailsPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ToDoInformationTableListener(ToDoList.getList(),
                        table, detailsPanel, ID_KEY, pomodoro));
    }

    private void showSelectedItemComment(CommentPanel commentPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ToDoList.getList(),
                        table, commentPanel, ID_KEY));
    }

    private void showSelectedMergeList(MergingPanel mergingPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ToDoMergingListListener(ToDoList.getList(), table,
                        mergingPanel, ID_KEY, pomodoro));
    }

    //new ToDoMergingListListener(mergingPanel, pomodoro)
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
            Activity activity = ToDoList.getList().getById(id);
            if (activity != null && activity.isFinished()) {
                renderer.setForeground(ColorUtil.GREEN);
            }
            return renderer;
        }
    }

    static class StoryPointsCellRenderer extends CustomTableRenderer {

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

    static class EstimatedCellRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
            Activity activity = ToDoList.getList().getById(id);
            String text = activity.getActualPoms() + " / ";
            text += value.toString();
            Integer overestimatedpoms = activity.getOverestimatedPoms();
            text += overestimatedpoms > 0 ? " + " + overestimatedpoms : "";
            renderer.setText(text);
            return renderer;
        }
    }

    private JPanel wrapInBackgroundImage(TimerPanel timerPanel,
            MuteButton muteButton, Icon backgroundIcon, int verticalAlignment,
            int horizontalAlignment) {

        // make the passed in swing component transparent
        timerPanel.setOpaque(false);

        // create wrapper JPanel
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        if (ControlPanel.preferences.getTicking()
                || ControlPanel.preferences.getRinging()) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;
            muteButton.setOpaque(true);
            muteButton.setBorder(new LineBorder(ColorUtil.BLACK, 2));
            backgroundPanel.add(muteButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            backgroundPanel.add(timerPanel, gbc);
        } else {
            gbc.gridx = 0;
            gbc.gridy = 0;
            // add the passed in swing component first to ensure that it is in
            // front
            backgroundPanel.add(timerPanel, gbc);
        }

        // create a label to paint the background image
        JLabel backgroundImage = new JLabel(backgroundIcon);

        // set minimum and preferred sizes so that the size of the image
        // does not affect the layout size
        backgroundImage.setPreferredSize(new Dimension(250, 240));
        backgroundImage.setMinimumSize(new Dimension(260, 250));

        backgroundImage.setVerticalAlignment(verticalAlignment);
        backgroundImage.setHorizontalAlignment(horizontalAlignment);

        backgroundPanel.add(backgroundImage, gbc);

        return backgroundPanel;
    }

    public void saveComment(String comment) {
        int row = table.getSelectedRow();
        if (row > -1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
            Activity selectedActivity = ToDoList.getList().getById(id);
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
        if (!ToDoList.getList().isEmpty()) {
            // Activity deleted (removed from the list)
            if (ToDoList.getList().getById(selectedActivityId) == null) {
                index = selectedRowIndex;
                // Activity deleted (end of the list)
                if (ToDoList.getListSize() < selectedRowIndex + 1) {
                    --index;
                }
            } else if (selectedActivityId != 0) {
                Iterator<Activity> iActivity = ToDoList.getList().iterator();
                while (iActivity.hasNext()) {
                    if (iActivity.next().getId() == selectedActivityId) {
                        break;
                    }
                    index++;
                }
            }
        }
        if (!ToDoList.getList().isEmpty()) {
            index = index > ToDoList.getListSize() ? 0 : index;
            table.setRowSelectionInterval(index, index);
        }
    }

    public Pomodoro getPomodoro() {
        return pomodoro;
    }

    public JLabel getPomodoroTime() {
        return pomodoroTime;
    }

    public void refreshIconLabels() {
        int[] rows = table.getSelectedRows();
        if (rows.length > 0) {
            Activity currentToDo = pomodoro.getCurrentToDo();
            if (pomodoro.inPomodoro()) {
                ToDoIconLabel.showIconLabel(iconLabel, currentToDo, ColorUtil.RED);
                ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), currentToDo, ColorUtil.RED);
                ToDoIconLabel.showIconLabel(detailsPanel.getIconLabel(), currentToDo, ColorUtil.RED);
                ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), currentToDo, ColorUtil.RED);
                ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), currentToDo, ColorUtil.RED);
            }
            if (rows.length == 1) { // one selected only
                Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(rows[0]), ID_KEY);
                Activity selectedToDo = getActivityById(id);
                if (pomodoro.inPomodoro() && selectedToDo.getId() != currentToDo.getId()) {
                    ToDoIconLabel.showIconLabel(detailsPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                } else {
                    if (currentToDo != null && selectedToDo.getId() != currentToDo.getId()) {
                        ToDoIconLabel.showIconLabel(iconLabel, currentToDo, currentToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                        ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), currentToDo, currentToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    } else if (currentToDo != null) {
                        ToDoIconLabel.showIconLabel(iconLabel, currentToDo, currentToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                        ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), currentToDo, currentToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                        ToDoIconLabel.showIconLabel(detailsPanel.getIconLabel(), currentToDo, currentToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                        ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), currentToDo, currentToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                        ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), currentToDo, currentToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    } else {
                        ToDoIconLabel.showIconLabel(iconLabel, selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                        ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                        ToDoIconLabel.showIconLabel(detailsPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                        ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                        ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    }
                }
            } else { // empty list or multiple selection
                if (!pomodoro.getTimer().isRunning()) {
                    ToDoIconLabel.clearIconLabel(iconLabel);
                    ToDoIconLabel.clearIconLabel(unplannedPanel.getIconLabel());
                }
                ToDoIconLabel.clearIconLabel(detailsPanel.getIconLabel());
                ToDoIconLabel.clearIconLabel(commentPanel.getIconLabel());
                ToDoIconLabel.clearIconLabel(overestimationPanel.getIconLabel());
            }
        }
    }

    /*private class cellRenderer implements ListCellRenderer {

     @Override
     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
     JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

     if (isSelected) {
     renderer.setBackground(ColorUtil.BLUE_ROW);
     } else {
     renderer.setBackground(index % 2 == 0 ? Color.white : ColorUtil.YELLOW_ROW); // rows with even/odd number
     }

     Activity toDo = (Activity) value;
     renderer.setText((toDo.isUnplanned() ? "(" + "U" + ") " : "") + toDo.getName() + " (" + toDo.getActualPoms() + "/" + toDo.getEstimatedPoms() + (toDo.getOverestimatedPoms() > 0 ? " + " + toDo.getOverestimatedPoms() : "") + ")");

     Activity currentToDo = pomodoro.getCurrentToDo();
     if (isSelected) {
     renderer.setFont(new Font(renderer.getFont().getName(), Font.BOLD, renderer.getFont().getSize()));
     }
     if (pomodoro.inPomodoro()) {
     if (toDo.getId() == currentToDo.getId()) {
     renderer.setForeground(ColorUtil.RED);
     } else {
     if (toDo.isFinished()) {
     renderer.setForeground(ColorUtil.GREEN);
     } else {
     renderer.setForeground(ColorUtil.BLACK);
     }
     }
     } else {
     if (toDo.isFinished()) {
     renderer.setForeground(ColorUtil.GREEN);
     } else {
     renderer.setForeground(ColorUtil.BLACK);
     }
     }
     return renderer;
     }
     }*/
}
