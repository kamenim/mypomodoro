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
package org.mypomodoro.gui.todo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
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
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
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
import org.mypomodoro.buttons.CompleteToDoButton;
import org.mypomodoro.buttons.DiscontinuousButton;
import org.mypomodoro.buttons.MoveToDoButton;
import org.mypomodoro.buttons.MuteButton;
import org.mypomodoro.buttons.PinButton;
import org.mypomodoro.buttons.ResizeButton;
import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityCommentTableListener;
import org.mypomodoro.gui.ActivityEditTableListener;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.activities.CommentPanel;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ComponentTitledBorder;
import org.mypomodoro.util.CustomTableHeader;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;
import org.mypomodoro.util.WaitCursor;

/**
 * GUI for viewing what is in the ToDoList. This can be changed later. Right now
 * it uses a DefaultTableModel to build the JTable. Table Listeners can be added
 * to save cell edits to the ActivityCollection which can then be saved to the
 * data layer.
 *
 */
public class ToDoPanel extends JPanel implements IListPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension PANE_DIMENSION = new Dimension(400, 200);
    private static final Dimension LISTPANE_DIMENSION = new Dimension(300, 200);
    private static final Dimension TABPANE_DIMENSION = new Dimension(400, 50);
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
    private final CommentPanel commentPanel = new CommentPanel(this, true);
    private InputMap im = null;
    private final OverestimationPanel overestimationPanel = new OverestimationPanel(this, detailsPanel);
    private final EditPanel editPanel = new EditPanel(detailsPanel);
    private final UnplannedPanel unplannedPanel = new UnplannedPanel(this);
    private final MergingPanel mergingPanel = new MergingPanel(this);
    private final JLabel iconLabel = new JLabel("", JLabel.CENTER);
    private final Pomodoro pomodoro = new Pomodoro(this, detailsPanel, unplannedPanel);
    private final JSplitPane splitPane;
    private final JTabbedPane controlPane = new JTabbedPane();
    private JScrollPane todoScrollPane;
    //private final JLabel pomodorosRemainingLabel = new JLabel("", JLabel.LEFT);
    private int mouseHoverRow = 0;
    final ImageIcon pomodoroIcon = new ImageIcon(Main.class.getResource("/images/myPomodoroIconNoTime250.png"));
    // Border
    private final JButton titledButton = new JButton();
    private final ComponentTitledBorder titledborder = new ComponentTitledBorder(titledButton, this, new EtchedBorder(), getFont().deriveFont(Font.BOLD));
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));
    private final DiscontinuousButton discontinuousButton = new DiscontinuousButton(pomodoro);
    private static final ResizeButton resizeButton = new ResizeButton();

    private GridBagConstraints c = new GridBagConstraints();

    // Unplanned
    //private final ImageIcon unplannedIcon = new ImageIcon(Main.class.getResource("/images/unplanned.png"));
    // Selected row
    private int currentSelectedRow = 0;

    public ToDoPanel() {
        setLayout(new GridBagLayout());

        activitiesTableModel = getTableModel();

        table = new JXTable(activitiesTableModel) {

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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // Split pane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scrollPane, controlPane); // continuous layout = true: important for resizing
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setDividerSize(10);
        //BasicSplitPaneDivider divider = (BasicSplitPaneDivider) splitPane.getComponent(2);
        //divider.setBackground(ColorUtil.YELLOW_ROW);
        //divider.setBorder(new MatteBorder(1, 1, 1, 1, ColorUtil.BLUE_ROW));
        add(splitPane, gbc);

        // Init control pane before the table so we can set the default tab at start up time               
        controlPane.setMinimumSize(TABPANE_DIMENSION);
        controlPane.setPreferredSize(TABPANE_DIMENSION);
        addTabPane();

        // Init table (data model and rendering)
        initTable();

        // Set border
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

        // Top pane
        scrollPane.setMinimumSize(PANE_DIMENSION);
        scrollPane.setPreferredSize(PANE_DIMENSION);
        scrollPane.setLayout(new GridBagLayout());

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        addToDoTable();
        addTimerPanel();
        //addRemainingPomodoroPanel(c);
        //addToDoIconPanel(c);
    }

    // add all listener once and for all
    private void setUpTable() {
        // add tooltip to header columns
        String[] cloneColumnNames = columnNames.clone();
        cloneColumnNames[ID_KEY - 5] = Labels.getString("Common.Unplanned");
        cloneColumnNames[ID_KEY - 3] = Labels.getString("Common.Real") + " / " + Labels.getString("Common.Estimated") + " (+ " + Labels.getString("Common.Overestimated") + ")";
        CustomTableHeader customTableHeader = new CustomTableHeader(table, cloneColumnNames);
        table.setTableHeader(customTableHeader);

        // Add tooltip and drag and drop
        // we had to implement our own MouseInputAdapter in order to manage the Mouse release event
        class CustomInputAdapter extends MouseInputAdapter {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = table.rowAtPoint(p);
                int columnIndex = table.columnAtPoint(p);
                if (rowIndex != -1) {
                    if (columnIndex == ID_KEY - 4) { // title
                        String value = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), columnIndex));
                        value = value.length() > 0 ? value : null;
                        table.setToolTipText(value);
                    } else {
                        table.setToolTipText(null); // this way tooltip won't stick
                    }
                }
                // Change of row
                if (mouseHoverRow != rowIndex) {
                    if (table.getSelectedRowCount() == 1) {
                        if (rowIndex == -1) {
                            rowIndex = table.getSelectedRow();
                            table.setToolTipText(null); // this way tooltip won't stick
                        }
                        Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), ID_KEY);
                        Activity activity = ToDoList.getList().getById(id);
                        detailsPanel.selectInfo(activity);
                        detailsPanel.showInfo();
                        commentPanel.showInfo(activity);
                        setIconLabels(table.convertRowIndexToModel(rowIndex));
                    }
                    mouseHoverRow = rowIndex;
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
                        commentPanel.showInfo(activity);
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
                                    controlPane.setEnabledAt(2, false); // edit
                                    controlPane.setEnabledAt(3, false); // overestimation
                                    controlPane.setEnabledAt(4, false); // unplanned
                                    if ((pomodoro.inPomodoro() && table.getSelectedRowCount() > 2) || !pomodoro.inPomodoro()) {
                                        controlPane.setEnabledAt(5, true); // merging
                                    }
                                    if (controlPane.getSelectedIndex() == 1
                                    || controlPane.getSelectedIndex() == 2
                                    || controlPane.getSelectedIndex() == 3
                                    || controlPane.getSelectedIndex() == 4) {
                                        controlPane.setSelectedIndex(0); // switch to details panel
                                    }
                                    if (!pomodoro.getTimer().isRunning()) {
                                        pomodoro.setCurrentToDoId(-1); // this will disable the start button
                                    }
                                    currentSelectedRow = table.getSelectedRows()[0]; // always selecting the first selected row (oterwise removeRow will fail)
                                } else if (table.getSelectedRowCount() == 1) {
                                    // activate all panels
                                    for (int index = 0; index < controlPane.getTabCount(); index++) {
                                        if (index == 5) {
                                            controlPane.setEnabledAt(5, false); // merging
                                            if (controlPane.getSelectedIndex() == 5) {
                                                controlPane.setSelectedIndex(0); // switch to details panel
                                            }
                                        } else {
                                            controlPane.setEnabledAt(index, true);
                                        }
                                    }
                                    if (controlPane.getTabCount() > 0) { // at start-up time not yet initialised (see constructor)
                                        controlPane.setSelectedIndex(controlPane.getSelectedIndex()); // switch to selected panel
                                    }
                                    if (!pomodoro.inPomodoro()) {
                                        int row = table.getSelectedRow();
                                        pomodoro.setCurrentToDoId((Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), ID_KEY));
                                    }
                                    currentSelectedRow = table.getSelectedRow();
                                    showCurrentSelectedRow(); // when sorting columns, focus on selected row 
                                }
                                setIconLabels();
                                //setPanelRemaining();
                                setPanelBorder();
                            }
                        } else {
                            setIconLabels();
                            //setPanelRemaining();
                            setPanelBorder();
                        }
                    }
                });

        im = table.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = table.getActionMap();
        // Activate Shift + '>'
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.SHIFT_MASK), "Complete");
        class completeAction extends AbstractAction {

            final ToDoPanel panel;

            public completeAction(ToDoPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                CompleteToDoButton completeToDoButton = new CompleteToDoButton(Labels.getString("ToDoListPanel.Complete ToDo"), Labels.getString("ToDoListPanel.Are you sure to complete those ToDo?"), panel);
                completeToDoButton.doClick();
            }
        }
        am.put("Complete", new completeAction(this));
        // Activate Shift + '<'
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.SHIFT_MASK), "Move Back To Activity List");
        class moveBackAction extends AbstractAction {

            final ToDoPanel panel;

            public moveBackAction(ToDoPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                MoveToDoButton moveToDoButton = new MoveToDoButton("", panel);
                moveToDoButton.doClick();
            }
        }
        am.put("Move Back To Activity List", new moveBackAction(this));
        // Activate Control A        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "Control A");
        class selectAllAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                table.selectAll();
            }
        }
        am.put("Control A", new selectAllAction());

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
        for (int i = 1; i <= 8; i++) {
            im.put(KeyStroke.getKeyStroke(getKeyEvent(i), InputEvent.CTRL_DOWN_MASK), "Tab" + i);
            am.put("Tab" + i, new tabAction(i - 1));
        }

        // Activate Control U (quick unplanned task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK), "Control U");
        class createUnplanned extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                Activity unplanned = new Activity();
                unplanned.setEstimatedPoms(0);
                unplanned.setIsUnplanned(true);
                unplanned.setName(Labels.getString("Common.Unplanned"));
                addActivity(unplanned);
                // Select new created task at the bottom of the list before refresh
                setCurrentSelectedRow(table.getRowCount());
                refresh();
            }
        }
        am.put("Control U", new createUnplanned());

        // Activate Control I (quick internal interruption)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), "Control I");
        class createInternal extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Interruptions : update current/running pomodoro
                Activity currentToDo = getPomodoro().getCurrentToDo();
                if (currentToDo != null && getPomodoro().inPomodoro()) {
                    currentToDo.incrementInternalInter();
                    currentToDo.databaseUpdate();
                    Activity interruption = new Activity();
                    interruption.setEstimatedPoms(0);
                    interruption.setIsUnplanned(true);
                    interruption.setName(Labels.getString("ToDoListPanel.Internal"));
                    addActivity(interruption);
                    // Select new created task at the bottom of the list before refresh
                    setCurrentSelectedRow(table.getRowCount());
                    refresh();
                }
            }
        }
        am.put("Control I", new createInternal());

        // Activate Control E (quick internal interruption)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "Control E");
        class createExternal extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Interruptions : update current/running pomodoro
                Activity currentToDo = getPomodoro().getCurrentToDo();
                if (currentToDo != null && getPomodoro().inPomodoro()) {
                    currentToDo.incrementInter();
                    currentToDo.databaseUpdate();
                    Activity interruption = new Activity();
                    interruption.setEstimatedPoms(0);
                    interruption.setIsUnplanned(true);
                    interruption.setName(Labels.getString("ToDoListPanel.External"));
                    addActivity(interruption);
                    // Select new created task at the bottom of the list before refresh
                    setCurrentSelectedRow(table.getRowCount());
                    refresh();
                }
            }
        }
        am.put("Control E", new createExternal());

        // Activate Control D (duplicate task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "Duplicate");
        class duplicate extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRowCount() == 1) {
                    int row = table.getSelectedRow();
                    Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), getIdKey());
                    Activity originalCopiedActivity = getActivityById(id);
                    try {
                        Activity copiedActivity = originalCopiedActivity.clone(); // a clone is necessary to remove the reference/pointer to the original task
                        copiedActivity.setId(-1); // new activity
                        copiedActivity.setName("(D) " + copiedActivity.getName());
                        copiedActivity.setActualPoms(0);
                        copiedActivity.setOverestimatedPoms(0);
                        // Insert the duplicate into the activity list
                        ActivityList.getList().add(copiedActivity, new Date(), new Date(0));
                        String title = Labels.getString("Common.Add Duplicated task");
                        String message = Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "Common.Duplicated task added to Activity List");
                        JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    } catch (CloneNotSupportedException ignored) {
                    }
                }
            }
        }
        am.put("Duplicate", new duplicate());

        // Activate Control G (scroll back to the current running/selected task
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), "Scroll");
        class scrollBackToTask extends AbstractAction {

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
        }
        am.put("Scroll", new scrollBackToTask());
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

    private void initTable() {
        table.setRowHeight(30);

        // Enable drag and drop
        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(new ToDoTransferHandler(this));

        // Make table allowing multiple selections
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Prevent key events from editing the cell (this meanly to avoid conflicts with shortcuts)        
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField()) {

            @Override
            public boolean isCellEditable(EventObject e) {
                if (e instanceof KeyEvent) {
                    return false;
                }
                return super.isCellEditable(e);
            }
        };
        table.setDefaultEditor(Object.class, editor);

        // set custom render for title
        table.getColumnModel().getColumn(ID_KEY - 6).setCellRenderer(new CustomTableRenderer()); // priority
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(new TitleRenderer()); // title                
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
                if (index == 6) { // import tab
                    controlPane.setSelectedIndex(index);
                    continue;
                }
                controlPane.setEnabledAt(index, false);
            }
        } else {
            int currentRow = table.convertRowIndexToView(currentSelectedRow);
            table.setRowSelectionInterval(currentRow, currentRow);
            table.scrollRectToVisible(table.getCellRect(currentRow, 0, true));
            // detail tab
            controlPane.setSelectedIndex(0);
        }

        // Refresh panel border
        setPanelBorder();

        // Refresh remaining panel
        //setPanelRemaining();
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
                titleActivitiesList += " (" + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + table.getSelectedRowCount() + "&nbsp;</span>" + "/" + ToDoList.getListSize() + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": " + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + real + " / " + estimated;
                if (overestimated > 0) {
                    titleActivitiesList += " + " + overestimated;
                }
                titleActivitiesList += "&nbsp;</span>";
                if (PreferencesPanel.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + df.format(storypoints) + "&nbsp;</span>";
                }
                // Tool tip
                String toolTipText = TimeConverter.getLength(estimated + overestimated);
                if (PreferencesPanel.preferences.getPlainHours()) {
                    toolTipText += " (" + Labels.getString("Common.Plain hours") + ")";
                } else {
                    toolTipText += " (" + Labels.getString("Common.Effective hours") + ")";
                }
                titledborder.setToolTipText(toolTipText);
            } else {
                titleActivitiesList += " (" + ToDoList.getListSize() + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": ";
                titleActivitiesList += ToDoList.getList().getNbRealPom();
                titleActivitiesList += " / " + ToDoList.getList().getNbEstimatedPom();
                if (ToDoList.getList().getNbOverestimatedPom() > 0) {
                    titleActivitiesList += " + " + ToDoList.getList().getNbOverestimatedPom();
                }
                if (PreferencesPanel.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(ToDoList.getList().getStoryPoints());
                }
                // Tool tip
                String toolTipText = TimeConverter.getLength(ToDoList.getList().getNbEstimatedPom() + ToDoList.getList().getNbOverestimatedPom());
                if (PreferencesPanel.preferences.getPlainHours()) {
                    toolTipText += " (" + Labels.getString("Common.Plain hours") + ")";
                } else {
                    toolTipText += " (" + Labels.getString("Common.Effective hours") + ")";
                }
                titledborder.setToolTipText(toolTipText);
            }
        }
        // Update titled border          
        titledButton.setText("<html>" + titleActivitiesList + "</html>");
        titledborder.repaint();
    }

    public void addToDoTable() {
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.7;
        c.weighty = 0.7;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
        todoScrollPane = new JScrollPane(table);
        todoScrollPane.setMinimumSize(LISTPANE_DIMENSION);
        todoScrollPane.setPreferredSize(LISTPANE_DIMENSION);
        scrollPane.add(todoScrollPane, c);
    }

    public void addTimerPanel() {
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.3;
        c.weighty = 0.6;
        c.gridheight = 1;
        final TimerPanel timerPanel = new TimerPanel(pomodoro, pomodoroTime, this);
        JPanel wrap = wrapInBackgroundImage(
                timerPanel,
                PreferencesPanel.preferences.getTicking() ? new MuteButton(pomodoro) : new MuteButton(pomodoro, false),
                pomodoroIcon,
                JLabel.TOP, JLabel.LEADING);
        // Deactivate/activate non-pomodoro options: pause, minus, plus buttons        
        /*wrap.addMouseListener(new MouseAdapter() {

         // click
         @Override
         public void mouseClicked(MouseEvent e) {
         timerPanel.switchPomodoroCompliance();
         }
         });*/
        scrollPane.add(wrap, c);
        pomodoro.setTimerPanel(timerPanel);
    }

    /*private void addRemainingPomodoroPanel(GridBagConstraints gbc) {
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
     iconLabel.setMinimumSize(ICONLABEL_DIMENSION);
     iconLabel.setPreferredSize(ICONLABEL_DIMENSION);
     scrollPane.add(iconLabel, gbc);
     }*/
    public void addTabPane() {
        controlPane.setFocusable(false); // removes borders around tab text
        controlPane.add(Labels.getString("Common.Details"), detailsPanel);
        controlPane.add(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        controlPane.add(Labels.getString("Common.Edit"), editPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Overestimate"), overestimationPanel);
        controlPane.add(Labels.getString("Common.Unplanned") + " / " + Labels.getString("ToDoListPanel.Interruption"), unplannedPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Merge"), mergingPanel);
        ImportPanel importPanel = new ImportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
        controlPane.addMouseListener(new MouseAdapter() {
            private int dividerLocation;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    // Expand
                    if (splitPane.getDividerLocation() != 0) { // double left click
                        dividerLocation = splitPane.getDividerLocation();
                        splitPane.setDividerLocation(0.0);
                    } else { // back to original position
                        splitPane.setDividerLocation(dividerLocation);
                    }
                }
            }
        });
        showSelectedItemDetails(detailsPanel);
        showSelectedItemComment(commentPanel);
        showSelectedItemEdit(editPanel);
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
        tableModel.addTableModelListener(new TableModelListener() {

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
                        if (index == 6) { // import panel
                            controlPane.setSelectedIndex(index);
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
            pomodoro.getTimerPanel().setStartEnv();
        }
    }

    // moveAll is used only if no pomodoro is running (see MoveToDoButton)
    @Override
    public void moveAll() {
        ToDoList.getList().moveAll();
        if (pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStartEnv();
        }
    }

    @Override
    public void complete(Activity activity) {
        ToDoList.getList().complete(activity);
        if (ToDoList.getList().isEmpty()
                && pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStartEnv();
        }
    }

    // comleteAll is used only if no pomodoro is running (see CompleteToDoButton)
    @Override
    public void completeAll() {
        ToDoList.getList().completeAll();
        if (pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStartEnv();
        }
    }

    @Override
    public void addActivity(Activity activity) {
        ToDoList.getList().add(activity);
    }

    @Override
    public void addActivity(Activity activity, Date date, Date dateCompleted) {
        ToDoList.getList().add(activity, date, dateCompleted);
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

    private void showSelectedItemEdit(EditPanel editPane) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityEditTableListener(ToDoList.getList(), table,
                        editPane, ID_KEY));
    }

    private void showSelectedItemComment(CommentPanel commentPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityCommentTableListener(ToDoList.getList(),
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
                if (table.getRowCount() == 0) {
                    currentSelectedRow = 0;
                }
                if (fromDatabase) {
                    ToDoList.getList().refresh();
                }
                activitiesTableModel = getTableModel();
                table.setModel(activitiesTableModel);
                initTable();
            } catch (Exception ex) {
                logger.error("", ex);
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

    class TitleRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            /*if (!PreferencesPanel.preferences.getAgileMode()) { // Pomodoro mode only
             int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
             Activity toDo = ToDoList.getList().getById(id);
             if (toDo != null && toDo.isOverdue()) {
                    
             }
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
                String text = toDo.getActualPoms() + " / " + toDo.getEstimatedPoms();
                Integer overestimatedpoms = toDo.getOverestimatedPoms();
                text += overestimatedpoms > 0 ? " + " + overestimatedpoms : "";
                renderer.setText(text);
            }
            return renderer;
        }
    }

    private JPanel wrapInBackgroundImage(final TimerPanel timerPanel,
            MuteButton muteButton, Icon backgroundIcon,
            int verticalAlignment, int horizontalAlignment) {
        // transparent
        timerPanel.setOpaque(false);

        // create wrapper JPanel
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel toolBar = new JPanel(new GridBagLayout());
        GridBagConstraints wc = new GridBagConstraints();
        discontinuousButton.setVisible(true); // this is a TransparentButton       
        discontinuousButton.setMargin(new Insets(1, 1, 1, 1));
        discontinuousButton.setFocusPainted(false); // removes borders around text
        toolBar.add(discontinuousButton, wc);
        if (PreferencesPanel.preferences.getTicking()
                || PreferencesPanel.preferences.getRinging()) {
            muteButton.setOpaque(false);
            muteButton.setMargin(new Insets(1, 1, 1, 1));
            muteButton.setFocusPainted(false); // removes borders around text
            toolBar.add(muteButton, wc);
        }
        PinButton pinButton = new PinButton();
        if (org.mypomodoro.gui.preferences.PreferencesPanel.preferences.getAlwaysOnTop()) {
            pinButton.setUnpinIcon();
        }
        pinButton.setVisible(true); // this is a TransparentButton       
        pinButton.setMargin(new Insets(1, 1, 1, 1));
        pinButton.setFocusPainted(false); // removes borders around text
        toolBar.add(pinButton, wc);
        resizeButton.setVisible(true); // this is a TransparentButton       
        resizeButton.setMargin(new Insets(1, 1, 1, 1));
        resizeButton.setFocusPainted(false); // removes borders around text
        toolBar.add(resizeButton, wc);
        backgroundPanel.add(toolBar, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        backgroundPanel.add(timerPanel, gbc);
        // Set background image (tomato) in a button to be able to add an action to it
        final JButton pomodoroButton = new JButton();
        pomodoroButton.setEnabled(true);
        pomodoroButton.setIcon(backgroundIcon);
        pomodoroButton.setBorder(null);
        pomodoroButton.setContentAreaFilled(false); // this is very important to remove borders on Win7 aero
        pomodoroButton.setOpaque(false);
        pomodoroButton.setFocusPainted(false); // hide border when action is performed (because setOpaque is set to false)        

        // Deactivate/activate non-pomodoro options: pause, minus, plus buttons        
        /*pomodoroButton.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {

         timerPanel.switchPomodoroCompliance();
         }
         });*/
        // set minimum and preferred sizes so that the size of the image
        // does not affect the layout size
        pomodoroButton.setPreferredSize(new Dimension(250, 240));
        pomodoroButton.setMinimumSize(new Dimension(260, 250));

        pomodoroButton.setVerticalAlignment(verticalAlignment);
        pomodoroButton.setHorizontalAlignment(horizontalAlignment);

        backgroundPanel.add(pomodoroButton, gbc);

        return backgroundPanel;
    }

    @Override
    public void saveComment(String comment) {
        if (table.getSelectedRowCount() == 1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), ID_KEY);
            Activity selectedActivity = ToDoList.getList().getById(id);
            if (selectedActivity != null) {
                selectedActivity.setNotes(comment);
                selectedActivity.databaseUpdateComment();
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
                ToDoIconLabel.showIconLabel(editPanel.getIconLabel(), currentToDo, ColorUtil.RED);
                detailsPanel.disableButtons();
            }
            if (table.getSelectedRowCount() == 1) { // one selected only
                Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), ID_KEY);
                Activity selectedToDo = getActivityById(id);
                if (pomodoro.inPomodoro() && selectedToDo.getId() != currentToDo.getId()) {
                    ToDoIconLabel.showIconLabel(detailsPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(editPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    detailsPanel.enableButtons();
                } else if (!pomodoro.inPomodoro()) {
                    ToDoIconLabel.showIconLabel(iconLabel, selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK, false);
                    ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(detailsPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
                    ToDoIconLabel.showIconLabel(editPanel.getIconLabel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : ColorUtil.BLACK);
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
                ToDoIconLabel.clearIconLabel(editPanel.getIconLabel());
                detailsPanel.enableButtons();
            }
        } else { // empty list
            ToDoIconLabel.clearIconLabel(iconLabel);
            ToDoIconLabel.clearIconLabel(unplannedPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(detailsPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(commentPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(overestimationPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(editPanel.getIconLabel());
            detailsPanel.enableButtons();
        }
    }

    /*public void setPanelRemaining() {
     PomodorosRemainingLabel.showRemainPomodoros(pomodorosRemainingLabel);
     }*/
    public void setCurrentSelectedRow(int row) {
        currentSelectedRow = row;
    }

    public void showCurrentSelectedRow() {
        table.scrollRectToVisible(table.getCellRect(currentSelectedRow, 0, true));
    }

    public JScrollPane getTodoScrollPane() {
        return todoScrollPane;
    }

    public JTabbedPane getControlPane() {
        return controlPane;
    }

    public void addControlPane() {
        splitPane.setRightComponent(controlPane); // bottom
    }

    public void setTitledBorder() {
        setBorder(titledborder);
    }

    public void hideDiscontinuousButton() {
        discontinuousButton.setVisible(false);
        pomodoro.continueWorkflow(); // pomodoro strict mode --> force continuous workflow
    }

    public void showDiscontinuousButton() {
        discontinuousButton.setVisible(true);
        //if (!discontinuousButton.isDiscontinuous()) {
        if (pomodoro.isDiscontinuous()) {
            pomodoro.discontinueWorkflow();
        }
    }

    public void showSplitPaneDivider() {
        splitPane.setDividerSize(10);
    }

    public void hideSplitPaneDivider() {
        splitPane.setDividerSize(0);
    }

    public static ResizeButton getResizeButton() {
        return resizeButton;
    }
}
