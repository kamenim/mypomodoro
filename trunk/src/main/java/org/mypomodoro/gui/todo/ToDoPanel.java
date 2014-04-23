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
package org.mypomodoro.gui.todo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.MuteButton;
import org.mypomodoro.gui.AbstractActivitiesPanel;
import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ComponentTitledBorder;
import org.mypomodoro.util.CustomTableHeader;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * GUI for viewing what is in the ToDoList. This can be changed later. Right now
 * it uses a DefaultTableModel to build the JTable. Table Listeners can be added
 * to save cell edits to the ActivityCollection which can then be saved to the
 * data layer.
 *
 */
public class ToDoPanel extends JPanel implements AbstractActivitiesPanel {
    // TODO problem drag and drop : row not selected

    private static final long serialVersionUID = 20110814L;
    private static final Dimension PANE_DIMENSION = new Dimension(400, 225);
    private static final Dimension TABPANE_DIMENSION = new Dimension(400, 25);
    private AbstractActivitiesTableModel activitiesTableModel;
    private final JXTable table;
    private final JPanel scrollPane = new JPanel();
    private static final String[] columnNames = {Labels.getString("Common.Priority"),
        "U",
        Labels.getString("Common.Title"),
        Labels.getString("Common.Estimated"),
        Labels.getString("Agile.Common.Story Points"),
        Labels.getString("Agile.Common.Iteration"),
        "ID"};
    public static int ID_KEY = 6;
    private final JLabel pomodoroTime = new JLabel();
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final OverestimationPanel overestimationPanel = new OverestimationPanel(this, detailsPanel);
    private final UnplannedPanel unplannedPanel = new UnplannedPanel(this);
    private final MergingPanel mergingPanel = new MergingPanel(this);
    private final JLabel iconLabel = new JLabel("", JLabel.CENTER);
    private final Pomodoro pomodoro = new Pomodoro(this, detailsPanel);
    private final JTabbedPane controlPane = new JTabbedPane();
    private final JLabel pomodorosRemainingLabel = new JLabel("", JLabel.LEFT);
    private int mouseHoverRow = 0;
    final ImageIcon pomodoroIcon = new ImageIcon(Main.class.getResource("/images/myPomodoroIconNoTime250.png"));
    // Border
    private final JButton titledButton = new JButton();
    private final ComponentTitledBorder titledborder = new ComponentTitledBorder(titledButton, this, new EtchedBorder(), getFont().deriveFont(Font.BOLD));
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));
    // Unplanned
    private final ImageIcon unplannedIcon = new ImageIcon(Main.class.getResource("/images/unplanned.png"));
    // Selected row
    private int currentSelectedRow = 0;

    public ToDoPanel() {
        setLayout(new GridBagLayout());

        activitiesTableModel = getTableModel();

        table = new JXTable(activitiesTableModel) {

            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    ((JComponent) c).setBackground(ColorUtil.BLUE_ROW);
                    ((JComponent) c).setFont(((JComponent) c).getFont().deriveFont(Font.BOLD));
                } else if (row == mouseHoverRow) {
                    ((JComponent) c).setBackground(ColorUtil.YELLOW_ROW);
                    ((JComponent) c).setFont(((JComponent) c).getFont().deriveFont(Font.BOLD));
                    ((JComponent) c).setBorder(new MatteBorder(1, 0, 1, 0, ColorUtil.BLUE_ROW));
                } else {
                    ((JComponent) c).setBorder(null);
                }
                return c;
            }
        };

        // Set up table listeners once anf for all
        setUpTable();

        // Init table (data model and rendering)
        initTable();

        // Set border
        //titledButton.setToolTipText("Refresh from database"); // tooltip doesn't work here
        titledButton.setIcon(refreshIcon);
        titledButton.setBorder(null);
        titledButton.setContentAreaFilled(false);
        titledButton.setOpaque(true);
        titledButton.setHorizontalTextPosition(SwingConstants.LEFT); // text of the left of the icon        
        titledButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                titledButton.setEnabled(false);
                // Refresh from database
                refresh(true);
                titledButton.setEnabled(true);
            }
        });
        setBorder(titledborder);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // Top pane
        scrollPane.setMinimumSize(PANE_DIMENSION);
        scrollPane.setPreferredSize(PANE_DIMENSION);
        scrollPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        addToDoTable(c);
        addTimerPanel(c);
        addRemainingPomodoroPanel(c);
        addToDoIconPanel(c);

        // Bottom pane                
        controlPane.setMinimumSize(TABPANE_DIMENSION);
        controlPane.setPreferredSize(TABPANE_DIMENSION);
        addTabPane();

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, controlPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        add(splitPane, gbc);
    }

    // add all listener once and for all
    private void setUpTable() {
        // add tooltip to header columns
        String[] cloneColumnNames = columnNames.clone();
        cloneColumnNames[ID_KEY - 5] = Labels.getString("Common.Unplanned");
        cloneColumnNames[ID_KEY - 3] = Labels.getString("Common.Real") + " / " + Labels.getString("Common.Estimated") + " (+" + Labels.getString("Common.Overestimated") + ")";
        CustomTableHeader customTableHeader = new CustomTableHeader(table, cloneColumnNames);
        table.setTableHeader(customTableHeader);

        // Add tooltip and drag and drop
        // we had to implement our own MouseInputAdapter in order to manage the Mouse release event
        class CustomInputAdapter extends MouseInputAdapter {

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
                    } else {
                        table.setToolTipText(null); // this way tooltip won't stick
                    }
                    // Change of row
                    if (mouseHoverRow != rowIndex) {
                        if (table.getSelectedRowCount() == 1) {
                            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), ID_KEY);
                            Activity activity = ToDoList.getList().getById(id);
                            detailsPanel.selectInfo(activity);
                            detailsPanel.showInfo();
                            commentPanel.selectInfo(activity);
                            commentPanel.showInfo();
                            setIconLabels(table.convertRowIndexToModel(rowIndex));
                        }
                        mouseHoverRow = rowIndex;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                    // This may happen when removing rows and yet using the mouse outside the table
                } catch (IndexOutOfBoundsException ignored) {
                    // This may happen when removing rows and yet using the mouse outside the table
                }
            }
        }
        table.addMouseMotionListener(new CustomInputAdapter());
        // This is to address the case/event when the mouse exit the table
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                // Reset to currently selected task
                if (table.getSelectedRowCount() == 1) {
                    Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), ID_KEY);
                    Activity activity = ToDoList.getList().getById(id);
                    if (activity != null) {
                        detailsPanel.selectInfo(activity);
                        detailsPanel.showInfo();
                        commentPanel.selectInfo(activity);
                        commentPanel.showInfo();
                    }
                    setIconLabels();
                }
                mouseHoverRow = -1;
            }
        });

        // Add listener to record selected row id and manage pomodoro timer
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (table.getSelectedRowCount() > 0) {
                            //System.err.println(e);
                            if (!e.getValueIsAdjusting()) { // ignoring the deselection event
                                //System.err.println(table.getSelectedRowCount());
                                if (table.getSelectedRowCount() > 1) { // multiple selection
                                    // diactivate/gray out unused tabs
                                    controlPane.setEnabledAt(1, false); // comment
                                    controlPane.setEnabledAt(2, false); // overestimation
                                    controlPane.setEnabledAt(3, false); // unplanned                            
                                    controlPane.setEnabledAt(4, true); // merging
                                    if (controlPane.getSelectedIndex() == 1
                                    || controlPane.getSelectedIndex() == 2
                                    || controlPane.getSelectedIndex() == 3) {
                                        controlPane.setSelectedIndex(0); // switch to details panel
                                    }
                                    if (!pomodoro.getTimer().isRunning()) {
                                        pomodoro.setCurrentToDoId(-1); // this will disable the start button
                                    }
                                    currentSelectedRow = table.getSelectedRows()[0]; // always selecting the first selected row (oterwise removeRow will fail)
                                } else if (table.getSelectedRowCount() == 1) {
                                    // activate all panels
                                    for (int index = 0; index < controlPane.getTabCount(); index++) {
                                        if (index == 4) {
                                            controlPane.setEnabledAt(4, false); // merging
                                            if (controlPane.getSelectedIndex() == 4) {
                                                controlPane.setSelectedIndex(0); // switch to details panel
                                            }
                                        } else {
                                            controlPane.setEnabledAt(index, true);
                                        }
                                    }
                                    if (controlPane.getTabCount() > 0) { // at start-up time not yet initialised (see constructor)
                                        controlPane.setSelectedIndex(0); // switch to details panel
                                    }
                                    if (!pomodoro.inPomodoro()) {
                                        int row = table.getSelectedRow();
                                        pomodoro.setCurrentToDoId((Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), ID_KEY));
                                    }
                                    currentSelectedRow = table.getSelectedRow();
                                    showCurrentSelectedRow(); // when sorting columns, focus on selected row 
                                }
                                setIconLabels();
                                setPanelRemaining();
                                setPanelBorder();
                            }
                        } else {
                            setIconLabels();
                            setPanelRemaining();
                            setPanelBorder();
                        }
                    }
                });

        // Activate Control A
        InputMap im = table.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = table.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK), "Control A");
        class selectAllAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.selectAll();
            }
        }
        am.put("Control A", new selectAllAction());
    }

    private void initTable() {
        table.setRowHeight(30);

        // Enable drag and drop
        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(new ToDoTransferHandler(this));

        // Make table allowing multiple selections
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Centre columns
        CustomTableRenderer dtcr = new CustomTableRenderer();
        // set custom render for title
        table.getColumnModel().getColumn(ID_KEY - 6).setCellRenderer(dtcr); // priority
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(dtcr); // title                
        table.getColumnModel().getColumn(ID_KEY - 3).setCellRenderer(new EstimatedCellRenderer()); // estimated                
        table.getColumnModel().getColumn(ID_KEY - 2).setCellRenderer(new StoryPointsCellRenderer()); // Story Point
        table.getColumnModel().getColumn(ID_KEY - 1).setCellRenderer(new IterationCellRenderer()); // iteration
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
            table.getColumnModel().getColumn(ID_KEY - 2).setMaxWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 2).setMinWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 2).setPreferredWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 1).setMaxWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 1).setMinWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 1).setPreferredWidth(40);
        }
        // hide unplanned in Agile mode
        if (PreferencesPanel.preferences.getAgileMode()) {
            table.getColumnModel().getColumn(ID_KEY - 5).setMaxWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 5).setMinWidth(0);
            table.getColumnModel().getColumn(ID_KEY - 5).setPreferredWidth(0);
        } else {
            // Set width of columns Unplanned
            table.getColumnModel().getColumn(ID_KEY - 5).setMaxWidth(30);
            table.getColumnModel().getColumn(ID_KEY - 5).setMinWidth(30);
            table.getColumnModel().getColumn(ID_KEY - 5).setPreferredWidth(30);
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

        // diactivate/gray out all tabs (except import)
        if (table.getRowCount() == 0) {
            for (int index = 0; index < controlPane.getComponentCount(); index++) {
                if (index == 5) { // import tab
                    controlPane.setSelectedIndex(index);
                    continue;
                }
                controlPane.setEnabledAt(index, false);
            }
        } else {
            //if (pomodoro.inPomodoro()) {
            int currentRow = table.convertRowIndexToView(currentSelectedRow);
            table.setRowSelectionInterval(currentRow, currentRow);
            table.scrollRectToVisible(table.getCellRect(currentRow, 0, true));
            /*} else {
             // select first activity
             table.setRowSelectionInterval(0, 0);
             table.scrollRectToVisible(table.getCellRect(0, 0, true));
             }*/
        }

        // Refresh panel border
        setPanelBorder();

        // Refresh remaining panel
        setPanelRemaining();
    }

    @Override
    public void setPanelBorder() {
        String titleActivitiesList = Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List");
        if (ToDoList.getListSize() > 0) {
            if (table.getSelectedRowCount() > 1) {
                int[] rows = table.getSelectedRows();
                int estimated = 0;
                int overestimated = 0;
                int real = 0;
                float storypoints = 0;
                for (int row : rows) {
                    Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), getIdKey());
                    Activity selectedActivity = getActivityById(id);
                    estimated += selectedActivity.getEstimatedPoms();
                    overestimated += selectedActivity.getOverestimatedPoms();
                    real += selectedActivity.getActualPoms();
                    storypoints += selectedActivity.getStoryPoints();
                }
                titleActivitiesList += " (" + table.getSelectedRowCount() + "/" + ToDoList.getListSize() + ")";
                titleActivitiesList += " : " + Labels.getString("Common.Estimated") + ": " + real + " / " + estimated;
                if (overestimated > 0) {
                    titleActivitiesList += " + " + overestimated;
                }
                if (PreferencesPanel.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(storypoints);
                }
            } else {
                titleActivitiesList += " (" + ToDoList.getListSize() + ")";
                titleActivitiesList += " : " + Labels.getString("Common.Estimated") + ": ";
                titleActivitiesList += ToDoList.getList().getNbRealPom();
                titleActivitiesList += " / " + ToDoList.getList().getNbEstimatedPom();
                if (ToDoList.getList().getNbOverestimatedPom() > 0) {
                    titleActivitiesList += " + " + ToDoList.getList().getNbOverestimatedPom();
                }
                if (PreferencesPanel.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(ToDoList.getList().getStoryPoints());
                }
            }
        }
        // Update titled border          
        titledButton.setText(titleActivitiesList);
        titledborder.repaint();
    }

    private void addToDoTable(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 0.7;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        scrollPane.add(new JScrollPane(table), gbc);
    }

    private void addTimerPanel(GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 0.6;
        gbc.gridheight = 1;
        TimerPanel timerPanel = new TimerPanel(pomodoro, pomodoroTime, this);
        scrollPane.add(wrapInBackgroundImage(
                timerPanel,
                PreferencesPanel.preferences.getTicking() ? new MuteButton(pomodoro) : new MuteButton(pomodoro, false),
                pomodoroIcon,
                JLabel.TOP, JLabel.LEADING), gbc);
        pomodoro.setTimerPanel(timerPanel);
    }

    private void addRemainingPomodoroPanel(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        scrollPane.add(pomodorosRemainingLabel, gbc);
        PomodorosRemainingLabel.showRemainPomodoros(pomodorosRemainingLabel);
    }

    private void addToDoIconPanel(GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        scrollPane.add(iconLabel, gbc);
    }

    private void addTabPane() {
        controlPane.add(Labels.getString("Common.Details"), detailsPanel);
        controlPane.add(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Overestimation"), overestimationPanel);
        controlPane.add(Labels.getString("Common.Unplanned"), unplannedPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Merging"), mergingPanel);
        ImportPanel importPanel = new ImportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
        showSelectedItemDetails(detailsPanel);
        showSelectedItemComment(commentPanel);
    }

    private AbstractActivitiesTableModel getTableModel() {
        int rowIndex = ToDoList.getList().size();
        int colIndex = columnNames.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = ToDoList.getList().iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Activity a = iterator.next();
            tableData[i][0] = a.getPriority();
            tableData[i][1] = a.isUnplanned();
            tableData[i][2] = a.getName();
            Integer poms = new Integer(a.getEstimatedPoms());
            tableData[i][3] = poms;
            Float points = new Float(a.getStoryPoints());
            tableData[i][4] = points;
            Integer iteration = new Integer(a.getIteration());
            tableData[i][5] = iteration;
            tableData[i][6] = a.getId();
        }

        AbstractActivitiesTableModel tableModel = new AbstractActivitiesTableModel(tableData, columnNames) {

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == ID_KEY - 4;
            }

            // this is mandatory to get columns with integers properly sorted
            @Override
            public Class
                    getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return Boolean.class;
                    case 3:
                        return Integer.class;
                    case 4:
                        return Float.class;
                    case 5:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }

        };

        // listener on editable cells
        tableModel.addTableModelListener(
                new TableModelListener() {

                    @Override
                    public void tableChanged(TableModelEvent e) {
                        if (e.getType() == TableModelEvent.UPDATE) {
                            int row = e.getFirstRow();
                            int column = e.getColumn();
                            if (column >= 0) { // This needs to be checked : the moveRow method (see ToDoTransferHandler) fires tableChanged with column = -1
                                AbstractActivitiesTableModel model = (AbstractActivitiesTableModel) e.getSource();
                                Object data = model.getValueAt(row, column); // no need for convertRowIndexToModel
                                Integer ID = (Integer) model.getValueAt(row, ID_KEY); // ID
                                Activity act = Activity.getActivity(ID.intValue());
                                if (column == ID_KEY - 4) { // Title (can't be empty)
                                    if (data.toString().trim().length() == 0) {
                                        // reset the original value. Title can't be empty.
                                        model.setValueAt(act.getName(), table.convertRowIndexToModel(row), ID_KEY - 4);
                                    } else {
                                        act.setName(data.toString());
                                        act.databaseUpdate();
                                        ToDoList.getList().update(act);
                                        setIconLabels();
                                        detailsPanel.selectInfo(act);
                                        detailsPanel.showInfo();
                                    }
                                }
                            }
                        }
                        // diactivate/gray out all tabs (except import)
                        if (table.getRowCount() == 0) {
                            for (int index = 0; index < controlPane.getComponentCount(); index++) {
                                if (index == 3) { // import panel
                                    continue;
                                }
                                controlPane.setEnabledAt(index, false);
                            }
                        }
                    }
                }
        );
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
        table.clearSelection(); // clear the selection so removeRow won't fire valueChanged on ListSelectionListener (especially in case of large selection)
        activitiesTableModel.removeRow(table.convertRowIndexToModel(rowIndex)); // we remove in the Model...
        if (table.getRowCount() > 0) {
            int currentRow = currentSelectedRow > rowIndex || currentSelectedRow == table.getRowCount() ? currentSelectedRow - 1 : currentSelectedRow;
            table.setRowSelectionInterval(currentRow, currentRow); // ...while selecting in the View
            table.scrollRectToVisible(table.getCellRect(currentRow, 0, true));
        }
    }

    @Override
    public Activity getActivityById(int id) {
        return ToDoList.getList().getById(id);
    }

    @Override
    public void delete(Activity activity) {
        ToDoList.getList().delete(activity);
    }

    @Override
    public void deleteAll() {
        // no use
    }

    @Override
    public void move(Activity activity) {
        ToDoList.getList().move(activity);
        if (ToDoList.getList().isEmpty()
                && pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStart();
        }
    }

    // moveAll is used only if no pomodoro is running (see MoveToDoButton)
    @Override
    public void moveAll() {
        ToDoList.getList().moveAll();
        if (pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStart();
        }
    }

    @Override
    public void complete(Activity activity) {
        ToDoList.getList().complete(activity);
        if (ToDoList.getList().isEmpty()
                && pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStart();
        }
    }

    // comleteAll is used only if no pomodoro is running (see CompleteToDoButton)
    @Override
    public void completeAll() {
        ToDoList.getList().completeAll();
        if (pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStart();
        }
    }

    @Override
    public void addActivity(Activity activity) {
        ToDoList.getList().add(activity);
    }

    public void reorderByPriority() {
        ToDoList.getList().reorderByPriority();
        for (int row = 0; row < table.getRowCount(); row++) {
            Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), ID_KEY);
            Activity activity = getActivityById(id);
            activitiesTableModel.setValueAt(activity.getPriority(), table.convertRowIndexToModel(row), 0); // priority column index = 0            
        }
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
                    ToDoList.getList().refresh();
                }
                activitiesTableModel = getTableModel();
                table.setModel(activitiesTableModel);
                initTable();
            } catch (Exception ignored) {
            } finally {
                // Stop wait cursor
                WaitCursor.stopWaitCursor();
            }
        }
    }

    // selected row BOLD
    class CustomTableRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            JLabel renderer = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setForeground(ColorUtil.BLACK);
            renderer.setFont(isSelected ? getFont().deriveFont(Font.BOLD) : getFont());
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
            Activity toDo = ToDoList.getList().getById(id);
            Activity currentToDo = pomodoro.getCurrentToDo();
            if (toDo != null && pomodoro.inPomodoro() && toDo.getId() == currentToDo.getId()) {
                renderer.setForeground(ColorUtil.RED);
            } else if (toDo != null && toDo.isFinished()) {
                renderer.setForeground(ColorUtil.GREEN);
            }
            /* Strikethrough task with no estimation
             if (toDo != null && toDo.getEstimatedPoms() == 0) {
             // underline url
             Map<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
             map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
             renderer.setFont(getFont().deriveFont(map));
             }*/
            return renderer;
        }
    }

    class UnplannedRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if ((Boolean) value) {
                //renderer.setIcon(unplannedIcon);
                renderer.setText("U");
            } else {
                renderer.setText("");
            }
            return renderer;
        }
    }

    class StoryPointsCellRenderer extends CustomTableRenderer {

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

    class IterationCellRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = value.toString();
            if (value.toString().equals("-1")) {
                text = "";
            }
            renderer.setText(text);
            return renderer;
        }
    }

    class EstimatedCellRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
            Activity toDo = ToDoList.getList().getById(id);
            if (toDo != null) {
                String text = toDo.getActualPoms() + " / ";
                text += value.toString();
                Integer overestimatedpoms = toDo.getOverestimatedPoms();
                text += overestimatedpoms > 0 ? " + " + overestimatedpoms : "";
                renderer.setText(text);
            }
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

        if (PreferencesPanel.preferences.getTicking()
                || PreferencesPanel.preferences.getRinging()) {
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

        // Set background image in a button to be able to add a action to it
        final JButton pomodoroButton = new JButton();
        pomodoroButton.setEnabled(true);
        pomodoroButton.setToolTipText(Labels.getString("ToDoListPanel.Show current task")); // tooltip doesn't work here
        pomodoroButton.setIcon(pomodoroIcon);
        pomodoroButton.setBorder(null);
        pomodoroButton.setContentAreaFilled(false);
        pomodoroButton.setOpaque(false);
        pomodoroButton.setFocusPainted(false); // hide border when action is performed (because setOpaque is set to false)
        pomodoroButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (pomodoro.inPomodoro()) {
                    for (int row = 0; row < table.getRowCount(); row++) {
                        Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), ID_KEY);
                        if (pomodoro.getCurrentToDo().getId() == id) {
                            currentSelectedRow = row;
                        }
                    }
                    table.setRowSelectionInterval(currentSelectedRow, currentSelectedRow);
                }
                showCurrentSelectedRow();
            }
        });

        // set minimum and preferred sizes so that the size of the image
        // does not affect the layout size
        pomodoroButton.setPreferredSize(new Dimension(250, 240));
        pomodoroButton.setMinimumSize(new Dimension(260, 250));

        pomodoroButton.setVerticalAlignment(verticalAlignment);
        pomodoroButton.setHorizontalAlignment(horizontalAlignment);

        backgroundPanel.add(pomodoroButton, gbc);

        return backgroundPanel;
    }

    public void saveComment(String comment) {
        if (table.getSelectedRowCount() == 1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), ID_KEY);
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

    public Pomodoro getPomodoro() {
        return pomodoro;
    }

    public JLabel getPomodoroTime() {
        return pomodoroTime;
    }

    public void setIconLabels() {
        setIconLabels(table.getSelectedRow());
    }

    public void setIconLabels(int row) {
        if (ToDoList.getListSize() > 0) {
            Activity currentToDo = pomodoro.getCurrentToDo();
            if (pomodoro.inPomodoro()) {
                ToDoIconLabel.showIconLabel(iconLabel, currentToDo, ColorUtil.RED, false);
                ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), currentToDo, ColorUtil.RED);
                ToDoIconLabel.showIconLabel(detailsPanel.getIconLabel(), currentToDo, ColorUtil.RED);
                ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), currentToDo, ColorUtil.RED);
                ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), currentToDo, ColorUtil.RED);
                detailsPanel.setForegroundColor(ColorUtil.RED);
                commentPanel.setForegroundColor(ColorUtil.RED);
                detailsPanel.disableButtons();
            }
            if (table.getSelectedRowCount() == 1) { // one selected only
                Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), ID_KEY);
                Activity selectedToDo = getActivityById(id);
                if (pomodoro.inPomodoro() && selectedToDo.getId() != currentToDo.getId()) {
                    ToDoIconLabel.showIconLabel(detailsPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    detailsPanel.setForegroundColor(selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    commentPanel.setForegroundColor(selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    detailsPanel.enableButtons();
                } else if (!pomodoro.inPomodoro()) {
                    ToDoIconLabel.showIconLabel(iconLabel, selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK, false);
                    ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(detailsPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    detailsPanel.setForegroundColor(selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    commentPanel.setForegroundColor(selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    detailsPanel.enableButtons();
                }
            } else if (table.getSelectedRowCount() > 1) { // multiple selection
                if (!pomodoro.inPomodoro()) {
                    ToDoIconLabel.clearIconLabel(iconLabel);
                    ToDoIconLabel.clearIconLabel(unplannedPanel.getIconLabel());
                }
                ToDoIconLabel.clearIconLabel(detailsPanel.getIconLabel());
                ToDoIconLabel.clearIconLabel(commentPanel.getIconLabel());
                ToDoIconLabel.clearIconLabel(overestimationPanel.getIconLabel());
                detailsPanel.setForegroundColor(ColorUtil.BLACK);
                commentPanel.setForegroundColor(ColorUtil.BLACK);
                detailsPanel.enableButtons();
            }
        } else { // empty list
            ToDoIconLabel.clearIconLabel(iconLabel);
            ToDoIconLabel.clearIconLabel(unplannedPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(detailsPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(commentPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(overestimationPanel.getIconLabel());
            detailsPanel.setForegroundColor(ColorUtil.BLACK);
            commentPanel.setForegroundColor(ColorUtil.BLACK);
            detailsPanel.enableButtons();
        }
    }

    public void setPanelRemaining() {
        PomodorosRemainingLabel.showRemainPomodoros(pomodorosRemainingLabel);
    }

    public void setCurrentSelectedRow(int row) {
        currentSelectedRow = row;
    }

    public void showCurrentSelectedRow() {
        table.scrollRectToVisible(table.getCellRect(currentSelectedRow, 0, true));
    }
}
