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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ImageIcon;
import javax.swing.InputMap;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;
import org.mypomodoro.buttons.CompleteToDoButton;
import org.mypomodoro.buttons.DiscontinuousButton;
import org.mypomodoro.buttons.MoveToDoButton;
import org.mypomodoro.buttons.MuteButton;
import org.mypomodoro.buttons.PinButton;
import org.mypomodoro.buttons.ResizeButton;
import org.mypomodoro.db.mysql.MySQLConfigLoader;
import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityCommentTableListener;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.activities.CommentPanel;
import org.mypomodoro.gui.activities.StoryPointsComboBoxCellEditor;
import org.mypomodoro.gui.activities.StoryPointsComboBoxCellRenderer;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;
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
    private static final Dimension LISTPANE_DIMENSION = new Dimension(360, 200);
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
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this, true);
    private InputMap im = null;
    private final OverestimationPanel overestimationPanel = new OverestimationPanel(this, detailsPanel);
    private final EditPanel editPanel = new EditPanel(detailsPanel);
    private final UnplannedPanel unplannedPanel = new UnplannedPanel(this);
    private final MergingPanel mergingPanel = new MergingPanel(this);
    private final JLabel pomodoroTime = new JLabel();
    private final Pomodoro pomodoro = new Pomodoro(this, detailsPanel, unplannedPanel, pomodoroTime);
    private final JSplitPane splitPane;
    private final JTabbedPane controlPane = new JTabbedPane();
    private JScrollPane tableScrollPane;
    //private final JLabel pomodorosRemainingLabel = new JLabel("", JLabel.LEFT);
    private int mouseHoverRow = 0;
    final ImageIcon pomodoroIcon = new ImageIcon(Main.class.getResource("/images/mAPIconTimer.png"));
    // Title
    private final JPanel titlePanel = new JPanel();
    private final JLabel titleLabel = new JLabel();
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));
    private final ImageIcon duplicateIcon = new ImageIcon(Main.class.getResource("/images/duplicate.png"));
    private final ImageIcon unplannedIcon = new ImageIcon(Main.class.getResource("/images/unplanned.png"));
    private final ImageIcon internalIcon = new ImageIcon(Main.class.getResource("/images/internal.png"));
    private final ImageIcon externalIcon = new ImageIcon(Main.class.getResource("/images/external.png"));
    private final ImageIcon overestimateIcon = new ImageIcon(Main.class.getResource("/images/plusone.png"));
    private final ImageIcon selectedIcon = new ImageIcon(Main.class.getResource("/images/selected.png"));
    private final ImageIcon runningIcon = new ImageIcon(Main.class.getResource("/images/running.png"));
    private final DefaultButton refreshButton = new DefaultButton(refreshIcon);
    private final DefaultButton duplicateButton = new DefaultButton(duplicateIcon);
    private final DefaultButton unplannedButton = new DefaultButton(unplannedIcon);
    private final DefaultButton internalButton = new DefaultButton(internalIcon);
    private final DefaultButton externalButton = new DefaultButton(externalIcon);
    private final DefaultButton overestimateButton = new DefaultButton(overestimateIcon);
    private final DefaultButton selectedButton = new DefaultButton(selectedIcon);
    private final DiscontinuousButton discontinuousButton = new DiscontinuousButton(pomodoro);
    private static final ResizeButton resizeButton = new ResizeButton();
    private final GridBagConstraints cScrollPane = new GridBagConstraints(); // title + table + timer    
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
                    // using ((JComponent) c).getFont() to preserve current font (eg strike through)
                    ((JComponent) c).setFont(((JComponent) c).getFont().deriveFont(Font.BOLD));
                } else if (row == mouseHoverRow) {
                    ((JComponent) c).setBackground(ColorUtil.YELLOW_ROW);
                    ((JComponent) c).setFont(((JComponent) c).getFont().deriveFont(Font.BOLD));
                    Component[] comps = ((JComponent) c).getComponents();
                    for (Component comp : comps) { // sub-components (combo boxes)
                        comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                    }
                    ((JComponent) c).setBorder(new MatteBorder(1, 0, 1, 0, ColorUtil.BLUE_ROW));
                } else {
                    if (row % 2 == 0) { // odd
                        ((JComponent) c).setBackground(ColorUtil.WHITE); // This stays White despite the background or the current theme
                    } else { // even
                        ((JComponent) c).setBackground(ColorUtil.BLUE_ROW_LIGHT);
                    }
                    ((JComponent) c).setBorder(null);
                }
                return c;
            }
        };

        // Set up table listeners once and for all
        setUpTable();

        // Scroll pane
        scrollPane.setMinimumSize(PANE_DIMENSION);
        scrollPane.setPreferredSize(PANE_DIMENSION);
        scrollPane.setLayout(new GridBagLayout());

        // Init label title and buttons        
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titlePanel.add(titleLabel);
        Insets buttonInsets = new Insets(0, 10, 0, 10);
        selectedButton.setMargin(buttonInsets);
        selectedButton.setFocusPainted(false); // removes borders around text
        selectedButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                scrollToCurrentTask();
            }
        });
        selectedButton.setToolTipText("CTRL + G");
        overestimateButton.setMargin(buttonInsets);
        overestimateButton.setFocusPainted(false); // removes borders around text
        overestimateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                overestimationPanel.overestimateTask(1);
            }
        });
        duplicateButton.setMargin(buttonInsets);
        duplicateButton.setFocusPainted(false); // removes borders around text
        duplicateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateTask();
            }
        });
        duplicateButton.setToolTipText("CTRL + D");
        unplannedButton.setMargin(buttonInsets);
        unplannedButton.setFocusPainted(false); // removes borders around text
        unplannedButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createUnplannedTask();
            }
        });
        unplannedButton.setToolTipText("CTRL + U");
        internalButton.setMargin(buttonInsets);
        internalButton.setFocusPainted(false); // removes borders around text
        internalButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createInternalInterruption();
            }
        });
        internalButton.setToolTipText("CTRL + I");
        externalButton.setMargin(buttonInsets);
        externalButton.setFocusPainted(false); // removes borders around text
        externalButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createExternalInterruption();
            }
        });
        externalButton.setToolTipText("CTRL + E");
        refreshButton.setMargin(buttonInsets);
        refreshButton.setFocusPainted(false); // removes borders around text
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshButton.setEnabled(false);
                // Refresh from database
                refresh(true);
                refreshButton.setEnabled(true);
            }
        });

        // Add components
        addTitlePanel();
        addTable();
        addTimerPanel();
        //addRemainingPomodoroPanel(c);
        //addToDoIconPanel(c);
        
        // Bottom pane
        // Init before init the table so we can set the default tab at start up time               
        controlPane.setMinimumSize(TABPANE_DIMENSION);
        controlPane.setPreferredSize(TABPANE_DIMENSION);
        addTabPane();
        // Split pane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scrollPane, controlPane); // continuous layout = true: important for resizing (see Resize class)
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        //splitPane.setDividerSize(10);
        splitPane.setDividerSize(0); // remove divider by hiding it
        //BasicSplitPaneDivider divider = (BasicSplitPaneDivider) splitPane.getComponent(2);
        //divider.setBackground(ColorUtil.YELLOW_ROW);
        //divider.setBorder(new MatteBorder(1, 1, 1, 1, ColorUtil.BLUE_ROW));
        // Splitted view
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(splitPane, gbc);

        // Init table (data model and rendering)
        initTable();
    }

    // add all listener once and for all
    private void setUpTable() {
        table.setBackground(ColorUtil.WHITE); // This stays White despite the background or the current theme
        table.setSelectionBackground(ColorUtil.BLUE_ROW);
        table.setForeground(ColorUtil.BLACK);
        table.setSelectionForeground(ColorUtil.BLACK);

        // add tooltip to header columns
        String[] cloneColumnNames = columnNames.clone();
        cloneColumnNames[activitiesTableModel.getColumnCount() - 1 - 5] = Labels.getString("Common.Unplanned");
        cloneColumnNames[activitiesTableModel.getColumnCount() - 1 - 3] = Labels.getString("Common.Real") + " / " + Labels.getString("Common.Estimated") + " (+ " + Labels.getString("Common.Overestimated") + ")";
        CustomTableHeader customTableHeader = new CustomTableHeader(table, cloneColumnNames);
        table.setTableHeader(customTableHeader);

        // Add tooltip and drag and drop
        // we had to implement our own MouseInputAdapter in order to manage the Mouse release event
        class CustomInputAdapter extends MouseInputAdapter {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = table.rowAtPoint(p);
                // Change of row
                if (mouseHoverRow != rowIndex) {
                    if (table.getSelectedRowCount() == 1) {
                        if (rowIndex == -1) {
                            rowIndex = table.getSelectedRow();
                            table.setToolTipText(null); // this way tooltip won't stick
                        }
                        Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), activitiesTableModel.getColumnCount() - 1);
                        Activity activity = getList().getById(id);
                        detailsPanel.selectInfo(activity);
                        detailsPanel.showInfo();
                        commentPanel.showInfo(activity);
                        editPanel.showInfo(activity);
                        setIconLabels(rowIndex);
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
                    Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), activitiesTableModel.getColumnCount() - 1);
                    Activity activity = getList().getById(id);
                    if (activity != null) {
                        detailsPanel.selectInfo(activity);
                        detailsPanel.showInfo();
                        commentPanel.showInfo(activity);
                        editPanel.showInfo(activity);
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
                                    currentSelectedRow = table.getSelectedRows()[0]; // always select the first selected row (otherwise removeRow will fail)
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
                                    int row = table.getSelectedRow();
                                    Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), activitiesTableModel.getColumnCount() - 1);
                                    if (!pomodoro.inPomodoro()) {
                                        pomodoro.setCurrentToDoId(id);
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
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, KeyEvent.SHIFT_MASK), "Complete");
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
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, KeyEvent.SHIFT_MASK), "Move Back To Activity List");
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
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), "Control A");
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
            im.put(KeyStroke.getKeyStroke(getKeyEvent(i), KeyEvent.CTRL_DOWN_MASK), "Tab" + i);
            am.put("Tab" + i, new tabAction(i - 1));
        }

        // Activate Control U (quick unplanned task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK), "Control U");
        class createUnplanned extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                createUnplannedTask();
            }
        }
        am.put("Control U", new createUnplanned());

        // Activate Control I (quick internal interruption)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK), "Control I");
        class createInternal extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                createInternalInterruption();
            }
        }
        am.put("Control I", new createInternal());

        // Activate Control E (quick internal interruption)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "Control E");
        class createExternal extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                createExternalInterruption();
            }
        }
        am.put("Control E", new createExternal());

        // Activate Control D (duplicate task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), "Duplicate");
        class duplicate extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateTask();
            }
        }
        am.put("Duplicate", new duplicate());

        // Activate Control G (scroll back to the current running/selected task
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK), "Scroll");
        class scrollBackToTask extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                scrollToCurrentTask();
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
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 6).setCellRenderer(new CustomTableRenderer()); // priority
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 5).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 4).setCellRenderer(new TitleRenderer()); // title           
        // The values of the combo depends on the activity : see EstimatedComboBoxCellRenderer and EstimatedComboBoxCellEditor
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 3).setCellRenderer(new ToDoEstimatedComboBoxCellRenderer(new Integer[0], false));
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 3).setCellEditor(new ToDoEstimatedComboBoxCellEditor(new Integer[0], false));
        // Story Point combo box
        Float[] points = new Float[]{0f, 0.5f, 1f, 2f, 3f, 5f, 8f, 13f, 20f, 40f, 100f};
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 2).setCellRenderer(new StoryPointsComboBoxCellRenderer(points, false));
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 2).setCellEditor(new StoryPointsComboBoxCellEditor(points, false));
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 1).setCellRenderer(new IterationCellRenderer()); // iteration
        // hide story points and iteration in 'classic' mode
        if (!Main.preferences.getAgileMode()) {
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 2).setMaxWidth(0);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 2).setMinWidth(0);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 2).setPreferredWidth(0);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 1).setMaxWidth(0);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 1).setMinWidth(0);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 1).setPreferredWidth(0);
        } else {
            // Set width of columns story points, iteration
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 2).setMaxWidth(60);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 2).setMinWidth(60);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 2).setPreferredWidth(60);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 1).setMaxWidth(40);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 1).setMinWidth(40);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 1).setPreferredWidth(40);
        }
        // hide unplanned in Agile mode
        if (Main.preferences.getAgileMode()) {
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 5).setMaxWidth(0);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 5).setMinWidth(0);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 5).setPreferredWidth(0);
        } else {
            // Set width of columns Unplanned
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 5).setMaxWidth(30);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 5).setMinWidth(30);
            table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 5).setPreferredWidth(30);
        }
        // Set width of column priority
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(0).setMinWidth(40);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        // Set width of column estimated
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 3).setMaxWidth(80);
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 3).setMinWidth(80);
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1 - 3).setPreferredWidth(80);
        // hide ID column
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1).setMaxWidth(0);
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1).setMinWidth(0);
        table.getColumnModel().getColumn(activitiesTableModel.getColumnCount() - 1).setPreferredWidth(0);
        // enable sorting
        if (table.getModel().getRowCount() > 0) {
            table.setAutoCreateRowSorter(true);
        }

        // diactivate/gray out all tabs (except import)
        if (table.getRowCount() == 0) {
            for (int index = 0; index < controlPane.getTabCount(); index++) {
                if (index == 6) { // import tab
                    controlPane.setSelectedIndex(index);
                    continue;
                }
                controlPane.setEnabledAt(index, false);
            }
        } else {            
            // select first activity
            table.setRowSelectionInterval(0, 0);
            table.scrollRectToVisible(table.getCellRect(0, 0, true)); 
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
        String titleActivitiesList = Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List");
        if (ToDoList.getListSize() > 0) {
            if (table.getSelectedRowCount() > 1) {
                int[] rows = table.getSelectedRows();
                int estimated = 0;
                int overestimated = 0;
                int real = 0;
                float storypoints = 0;
                for (int row : rows) {
                    Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), activitiesTableModel.getColumnCount() - 1);
                    Activity selectedActivity = getActivityById(id);
                    estimated += selectedActivity.getEstimatedPoms();
                    overestimated += selectedActivity.getOverestimatedPoms();
                    real += selectedActivity.getActualPoms();
                    storypoints += selectedActivity.getStoryPoints();
                }
                titleActivitiesList += " (" + "<span style=\"color:black; background-color:" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + table.getSelectedRowCount() + "&nbsp;</span>" + "/" + ToDoList.getListSize() + ")";
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
                titleLabel.setToolTipText(toolTipText);
                // Hide buttons of the quick bar
                titlePanel.remove(selectedButton);
                titlePanel.remove(overestimateButton);
                titlePanel.remove(duplicateButton);
            } else {
                titleActivitiesList += " (" + ToDoList.getListSize() + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": ";
                titleActivitiesList += getList().getNbRealPom();
                titleActivitiesList += " / " + getList().getNbEstimatedPom();
                if (getList().getNbOverestimatedPom() > 0) {
                    titleActivitiesList += " + " + getList().getNbOverestimatedPom();
                }
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(getList().getStoryPoints());
                }
                // Tool tip
                String toolTipText = Labels.getString("Common.Done") + ": ";
                toolTipText += TimeConverter.getLength(getList().getNbRealPom()) + " / ";
                toolTipText += TimeConverter.getLength(getList().getNbEstimatedPom());
                if (getList().getNbOverestimatedPom() > 0) {
                    toolTipText += " + " + TimeConverter.getLength(getList().getNbOverestimatedPom());
                }
                titleLabel.setToolTipText(toolTipText);
                if (table.getSelectedRowCount() == 1) {
                    // Show buttons of the quick bar
                    // Hide overestimation options when estimated == 0 or real < estimated
                    titlePanel.add(selectedButton, 1);
                    int row = table.getSelectedRow();
                    Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), activitiesTableModel.getColumnCount() - 1);
                    Activity selectedActivity = getActivityById(id);
                    if (selectedActivity.getEstimatedPoms() != 0
                            && selectedActivity.getActualPoms() >= selectedActivity.getEstimatedPoms()) {
                        controlPane.setEnabledAt(3, true); // overestimation tab
                        titlePanel.add(overestimateButton, 2);
                        titlePanel.add(duplicateButton, 3);
                    } else {
                        controlPane.setEnabledAt(3, false); // overestimation tab
                        titlePanel.remove(overestimateButton);
                        titlePanel.add(duplicateButton, 2);
                    }
                }
            }
            titlePanel.add(unplannedButton);
            if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
                titlePanel.add(refreshButton); // end of the line
            }
        } else {
            titlePanel.remove(selectedButton);
            titlePanel.remove(overestimateButton);
            titlePanel.remove(duplicateButton);
            titlePanel.remove(internalButton);
            titlePanel.remove(externalButton);
            if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
                titlePanel.remove(refreshButton);
            }
        }
        // Update title        
        titleLabel.setText("<html>" + titleActivitiesList + "</html>");
        titlePanel.repaint(); // this is necessary to force stretching of panel
    }

    public void addTitlePanel() {
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 0;
        cScrollPane.weightx = 1.0;
        cScrollPane.weighty = 0; // this is set only to prevent the resizing (Resize class) to enlarge the panel
        cScrollPane.gridwidth = 2;
        cScrollPane.anchor = GridBagConstraints.WEST;
        cScrollPane.fill = GridBagConstraints.BOTH;
        titlePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        scrollPane.add(titlePanel, cScrollPane);
    }

    public void showQuickInterruptionButtons() {
        titlePanel.add(internalButton);
        titlePanel.add(externalButton);
        setPanelBorder();
    }

    public void hideQuickInterruptionButtons() {
        titlePanel.remove(internalButton);
        titlePanel.remove(externalButton);
        setPanelBorder();
    }

    public void addTable() {
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 1;
        cScrollPane.weightx = 0.7;
        cScrollPane.weighty = 1.0;
        cScrollPane.gridwidth = 1;
        cScrollPane.gridheight = 1;
        cScrollPane.fill = GridBagConstraints.BOTH;
        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setMinimumSize(LISTPANE_DIMENSION);
        tableScrollPane.setPreferredSize(LISTPANE_DIMENSION);
        scrollPane.add(tableScrollPane, cScrollPane);
    }

    public void addTimerPanel() {
        cScrollPane.gridx = 1;
        cScrollPane.gridy = 1;
        cScrollPane.weightx = 0.3;
        cScrollPane.weighty = 1.0;
        cScrollPane.gridwidth = 1;
        cScrollPane.gridheight = 1;
        TimerPanel timerPanel = new TimerPanel(pomodoro, pomodoroTime, this);
        JPanel wrap = wrapInBackgroundImage(timerPanel, pomodoroIcon);
        // Deactivate/activate non-pomodoro options: pause, minus, plus buttons        
        /*wrap.addMouseListener(new MouseAdapter() {

         // click
         @Override
         public void mouseClicked(MouseEvent e) {
         timerPanel.switchPomodoroCompliance();
         }
         });*/
        scrollPane.add(wrap, cScrollPane);
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
        controlPane.add(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        controlPane.add(Labels.getString("Common.Edit"), editPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Overestimate"), overestimationPanel);
        controlPane.add(Labels.getString("Common.Unplanned") + " / " + Labels.getString("ToDoListPanel.Interruption"), unplannedPanel);
        controlPane.add(Labels.getString("ToDoListPanel.Merge"), mergingPanel);
        ImportPanel importPanel = new ImportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        controlPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
        // Implement one-click action on selected tabs
        // Tab already selected = one click to expand
        // Tab not selected = double click to expand
        class CustomChangeListener implements ChangeListener {
            private boolean stateChanged = false;
            
            @Override
            public void stateChanged(ChangeEvent e) {
                stateChanged = true;
            }
            
            public boolean getStateChanged() {                
                return stateChanged;
            }
            
            public void setStateChanged(boolean stateChanged) {
                this.stateChanged = stateChanged;
            }
        }
        final CustomChangeListener customChangeListener = new CustomChangeListener();
        controlPane.addChangeListener(customChangeListener);        
        controlPane.addMouseListener(new MouseAdapter() {
            private int dividerLocation;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1
                        || (e.getClickCount() == 1 && !customChangeListener.getStateChanged())) {
                    // Expand
                    if (splitPane.getDividerLocation() != 0) {
                        dividerLocation = splitPane.getDividerLocation();
                        splitPane.setDividerLocation(0.0);
                    } else { // back to original position
                        splitPane.setDividerLocation(dividerLocation);
                    }
                } else {
                    customChangeListener.setStateChanged(false);
                }
            }
        });
        showSelectedItemDetails(detailsPanel);
        //showSelectedItemEdit(editPanel);
        showSelectedItemComment(commentPanel);
    }

    private AbstractActivitiesTableModel getTableModel() {
        int rowIndex = getList().size();
        int colIndex = columnNames.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = getList().iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Activity a = iterator.next();
            tableData[i][0] = a.getPriority();
            tableData[i][1] = a.isUnplanned();
            tableData[i][2] = a.getName();
            Integer poms = new Integer(a.getActualPoms()); // sorting done on real pom
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
                return columnIndex == activitiesTableModel.getColumnCount() - 1 - 4 || columnIndex == activitiesTableModel.getColumnCount() - 1 - 3 || columnIndex == activitiesTableModel.getColumnCount() - 1 - 2;
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
        // Table model has a flaw: the update table event is fired whenever once click on an editable cell
        // To avoid update overhead, we compare old value with new value
        // (we could also have used solution found at https://tips4java.wordpress.com/2009/06/07/table-cell-listener 
        tableModel.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    AbstractActivitiesTableModel model = (AbstractActivitiesTableModel) e.getSource();
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    try { // catching error : dragging rows around (priorization) throws UPDATE events
                        Object data = model.getValueAt(row, column);
                        if (data != null) {
                            if (column >= 0) { // This needs to be checked : the moveRow method (see ToDoTransferHandler) fires tableChanged with column = -1                        
                                Integer ID = (Integer) model.getValueAt(row, activitiesTableModel.getColumnCount() - 1); // ID
                                Activity act = Activity.getActivity(ID.intValue());
                                if (column == activitiesTableModel.getColumnCount() - 1 - 4) { // Title (can't be empty)
                                    String name = data.toString().trim();
                                    if (!name.equals(act.getName())) {
                                        if (name.length() == 0) {
                                            // reset the original value. Title can't be empty.
                                            model.setValueAt(act.getName(), table.convertRowIndexToModel(row), activitiesTableModel.getColumnCount() - 1 - 4);
                                        } else {
                                            act.setName(name);
                                            act.databaseUpdate();
                                        }
                                    }
                                } else if (column == activitiesTableModel.getColumnCount() - 1 - 3) { // Estimated                            
                                    int estimated = (Integer) data;
                                    if (estimated != act.getEstimatedPoms()
                                            && estimated + act.getOverestimatedPoms() >= act.getActualPoms()) {
                                        act.setEstimatedPoms(estimated);
                                        act.databaseUpdate();
                                    }
                                }
                                getList().update(act);
                                setIconLabels();
                                // Refresh panel border after updating the list
                                setPanelBorder();
                                // update info
                                detailsPanel.selectInfo(act);
                                detailsPanel.showInfo();
                            }
                        }
                    } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                        // error due to priorization
                    }
                }
                // diactivate/gray out all tabs (except import)
                if (table.getRowCount() == 0) {
                    for (int index = 0; index < controlPane.getTabCount(); index++) {
                        if (index == 6) { // select Import panel
                            controlPane.setSelectedIndex(index);
                            continue;
                        }
                        controlPane.setEnabledAt(index, false);
                    }
                } else { // select Details tab
                    controlPane.setSelectedIndex(0);
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
        return activitiesTableModel.getColumnCount() - 1;
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
    public void insertRow(Activity activity) {
        table.clearSelection(); // clear the selection so insertRow won't fire valueChanged on ListSelectionListener (especially in case of large selection)
        Object[] rowData = new Object[7];
        rowData[0] = activity.getPriority();
        rowData[1] = activity.isUnplanned();
        rowData[2] = activity.getName();
        Integer poms = new Integer(activity.getActualPoms());
        rowData[3] = poms;
        Float points = new Float(activity.getStoryPoints());
        rowData[4] = points;
        Integer iteration = new Integer(activity.getIteration());
        rowData[5] = iteration;
        rowData[6] = activity.getId();
        // By default, the row is added at the bottom of the list
        // However, if one of the columns has been previously sorted the position of the row might not be the bottom position...
        activitiesTableModel.addRow(rowData); // we add in the Model...        
        int currentRow = table.convertRowIndexToView(table.getRowCount() - 1); // ...while selecting in the View
        table.setRowSelectionInterval(currentRow, currentRow);
        table.scrollRectToVisible(table.getCellRect(currentRow, 0, true));
    }

    @Override
    public Activity getActivityById(int id) {
        return getList().getById(id);
    }

    @Override
    public void delete(Activity activity) {
        getList().delete(activity);
    }

    @Override
    public void deleteAll() {
        // no use
    }

    @Override
    public void move(Activity activity) {
        getList().move(activity);
        if (getList().isEmpty()
                && pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStartEnv();
        }
    }

    // moveAll is used only if no pomodoro is running (see MoveToDoButton)
    @Override
    public void moveAll() {
        getList().moveAll();
        if (pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStartEnv();
        }
    }

    @Override
    public void complete(Activity activity) {
        getList().complete(activity);
        if (getList().isEmpty()
                && pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStartEnv();
        }
    }

    // comleteAll is used only if no pomodoro is running (see CompleteToDoButton)
    @Override
    public void completeAll() {
        getList().completeAll();
        if (pomodoro.getTimer().isRunning()) { // break running
            pomodoro.stop();
            pomodoro.getTimerPanel().setStartEnv();
        }
    }

    @Override
    public void addActivity(Activity activity) {
        getList().add(activity);
    }

    @Override
    public void addActivity(Activity activity, Date date, Date dateCompleted) {
        getList().add(activity, date, dateCompleted);
    }

    public void reorderByPriority() {
        getList().reorderByPriority();
        for (int row = 0; row < table.getRowCount(); row++) {
            Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), activitiesTableModel.getColumnCount() - 1);
            Activity activity = getActivityById(id);
            activitiesTableModel.setValueAt(activity.getPriority(), table.convertRowIndexToModel(row), 0); // priority column index = 0            
        }
    }

    private void showSelectedItemDetails(DetailsPanel detailsPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ToDoInformationTableListener(getList(),
                        table, detailsPanel, activitiesTableModel.getColumnCount() - 1, pomodoro));
    }

    /*private void showSelectedItemEdit(EditPanel editPane) {
     table.getSelectionModel().addListSelectionListener(
     new ActivityEditTableListener(getList(), table,
     editPane, activitiesTableModel.getColumnCount() - 1));
     }*/
    private void showSelectedItemComment(CommentPanel commentPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityCommentTableListener(getList(),
                        table, commentPanel, activitiesTableModel.getColumnCount() - 1));
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
                    getList().refresh();
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
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), activitiesTableModel.getColumnCount() - 1);
            Activity toDo = getList().getById(id);
            Activity currentToDo = pomodoro.getCurrentToDo();
            if (toDo != null && pomodoro.inPomodoro() && toDo.getId() == currentToDo.getId()) {
                renderer.setForeground(ColorUtil.RED);
            } else if (toDo != null && toDo.isFinished()) {
                renderer.setForeground(ColorUtil.GREEN);
            }
            return renderer;
        }
    }

    class TitleRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setToolTipText((String) value);
            return renderer;
        }
    }

    class UnplannedRenderer extends CustomTableRenderer {

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

    private JPanel wrapInBackgroundImage(final TimerPanel timerPanel, ImageIcon pomodoroIcon) {
        // create wrapper JPanel
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // center the toolbar
        // Toolbar
        JPanel toolBar = new JPanel(new GridBagLayout());
        GridBagConstraints wc = new GridBagConstraints();
        discontinuousButton.setVisible(true); // this is a TransparentButton       
        discontinuousButton.setMargin(new Insets(1, 1, 1, 1));
        discontinuousButton.setFocusPainted(false); // removes borders around text
        toolBar.add(discontinuousButton, wc);
        if (Main.preferences.getTicking()
                || Main.preferences.getRinging()) {
            MuteButton muteButton = Main.preferences.getTicking() ? new MuteButton(pomodoro) : new MuteButton(pomodoro, false);
            muteButton.setVisible(true);
            muteButton.setMargin(new Insets(1, 1, 1, 1));
            muteButton.setFocusPainted(false); // removes borders around text
            toolBar.add(muteButton, wc);
        }
        PinButton pinButton = new PinButton();
        if (Main.preferences.getAlwaysOnTop()) {
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
        gbc.fill = GridBagConstraints.BOTH;// this is very important to center the component (otherwise won't work with some themes such as Metal)        
        gbc.anchor = GridBagConstraints.CENTER; // this is very important to center the component (otherwise won't work with some themes such as Metal)        
        backgroundPanel.add(timerPanel, gbc);
        // Set background image (tomato) in a button to be able to add an action to it
        final DefaultButton pomodoroButton = new DefaultButton(pomodoroIcon, true);
        pomodoroButton.setContentAreaFilled(false); // this is very important to remove borders on Win7 aero
        pomodoroButton.setOpaque(false);
        // Deactivate/activate non-pomodoro options: pause, minus, plus buttons        
        /*pomodoroButton.addMouseListener(new MouseAdapter() {

         // click
         @Override
         public void mouseClicked(MouseEvent e) {
         timerPanel.switchPomodoroCompliance();
         }
         });*/
        backgroundPanel.add(pomodoroButton, gbc);
        return backgroundPanel;
    }

    @Override
    public void saveComment(String comment) {
        if (table.getSelectedRowCount() == 1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), activitiesTableModel.getColumnCount() - 1);
            Activity selectedActivity = getList().getById(id);
            if (selectedActivity != null) {
                selectedActivity.setNotes(comment);
                selectedActivity.databaseUpdateComment();
            }
        }
    }

    public Pomodoro getPomodoro() {
        return pomodoro;
    }

    public void setIconLabels() {
        setIconLabels(table.getSelectedRow());
    }

    public void setIconLabels(int row) {
        if (ToDoList.getListSize() > 0) {
            Activity currentToDo = pomodoro.getCurrentToDo();
            Color defaultForegroundColor = getForeground(); // leave it to the theme foreground color
            if (pomodoro.inPomodoro()) {
                //ToDoIconPanel.showIconPanel(iconPanel, currentToDo, ColorUtil.RED, false);
                ToDoIconPanel.showIconPanel(unplannedPanel.getIconPanel(), currentToDo, ColorUtil.RED);
                ToDoIconPanel.showIconPanel(detailsPanel.getIconPanel(), currentToDo, ColorUtil.RED);
                ToDoIconPanel.showIconPanel(commentPanel.getIconPanel(), currentToDo, ColorUtil.RED);
                ToDoIconPanel.showIconPanel(overestimationPanel.getIconPanel(), currentToDo, ColorUtil.RED);
                ToDoIconPanel.showIconPanel(editPanel.getIconPanel(), currentToDo, ColorUtil.RED);
                detailsPanel.disableButtons();
            }
            if (table.getSelectedRowCount() == 1) { // one selected only
                Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), activitiesTableModel.getColumnCount() - 1);
                Activity selectedToDo = getActivityById(id);
                if (pomodoro.inPomodoro() && selectedToDo.getId() != currentToDo.getId()) {
                    ToDoIconPanel.showIconPanel(detailsPanel.getIconPanel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(commentPanel.getIconPanel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(overestimationPanel.getIconPanel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(editPanel.getIconPanel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor);
                    detailsPanel.enableButtons();
                } else if (!pomodoro.inPomodoro()) {
                    //ToDoIconPanel.showIconPanel(iconPanel, selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor, false);
                    ToDoIconPanel.showIconPanel(unplannedPanel.getIconPanel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(detailsPanel.getIconPanel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(commentPanel.getIconPanel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(overestimationPanel.getIconPanel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor);
                    ToDoIconPanel.showIconPanel(editPanel.getIconPanel(), selectedToDo, selectedToDo.isFinished() ? ColorUtil.GREEN : defaultForegroundColor);
                    detailsPanel.enableButtons();
                }
            } else if (table.getSelectedRowCount() > 1) { // multiple selection
                if (!pomodoro.inPomodoro()) {
                    //ToDoIconPanel.clearIconPanel(iconPanel);
                    ToDoIconPanel.clearIconPanel(unplannedPanel.getIconPanel());
                }
                ToDoIconPanel.clearIconPanel(detailsPanel.getIconPanel());
                ToDoIconPanel.clearIconPanel(commentPanel.getIconPanel());
                ToDoIconPanel.clearIconPanel(overestimationPanel.getIconPanel());
                ToDoIconPanel.clearIconPanel(editPanel.getIconPanel());
                detailsPanel.enableButtons();
            }
        } else { // empty list
            //ToDoIconPanel.clearIconPanel(iconPanel);
            ToDoIconPanel.clearIconPanel(unplannedPanel.getIconPanel());
            ToDoIconPanel.clearIconPanel(detailsPanel.getIconPanel());
            ToDoIconPanel.clearIconPanel(commentPanel.getIconPanel());
            ToDoIconPanel.clearIconPanel(overestimationPanel.getIconPanel());
            ToDoIconPanel.clearIconPanel(editPanel.getIconPanel());
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

    public void removeScrollPane() {
        scrollPane.remove(tableScrollPane);
    }

    public void removeControlPane() {
        splitPane.remove(controlPane);
    }

    public void removeTitlePanel() {
        scrollPane.remove(titlePanel);
    }

    public void addControlPane() {
        splitPane.setRightComponent(controlPane); // bottom
    }

    public void hideDiscontinuousButton() {
        discontinuousButton.setVisible(false);
        pomodoro.continueWorkflow(); // pomodoro strict mode --> force continuous workflow
    }

    public void showDiscontinuousButton() {
        discontinuousButton.setVisible(true);
        if (pomodoro.isDiscontinuous()) {
            pomodoro.discontinueWorkflow();
        }
    }

    public void showSplitPaneDivider() {
        //splitPane.setDividerSize(10);
        splitPane.setDividerSize(0); // remove divider by hiding it
    }

    public void hideSplitPaneDivider() {
        splitPane.setDividerSize(0);
    }

    public static ResizeButton getResizeButton() {
        return resizeButton;
    }

    private void duplicateTask() {
        if (table.getSelectedRowCount() == 1) {
            int row = table.getSelectedRow();
            Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), getIdKey());
            Activity originalCopiedActivity = getActivityById(id);
            try {
                Activity copiedActivity = originalCopiedActivity.clone(); // a clone is necessary to remove the reference/pointer to the original task                
                copiedActivity.setName("(D) " + copiedActivity.getName());
                copiedActivity.setActualPoms(0);
                copiedActivity.setOverestimatedPoms(0);
                copiedActivity.setIteration(-1);
                // Insert the duplicate into the activity list
                ActivityList.getList().add(copiedActivity, new Date(), new Date(0));
                Main.gui.getActivityListPanel().insertRow(copiedActivity);
                String title = Labels.getString("Common.Add Duplicated task");
                String message = Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Duplicated task added to Activity List");
                JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            } catch (CloneNotSupportedException ignored) {
            }
        }
    }

    private void createUnplannedTask() {
        Activity unplannedToDo = new Activity();
        unplannedToDo.setEstimatedPoms(0);
        unplannedToDo.setIsUnplanned(true);
        unplannedToDo.setName(Labels.getString("Common.Unplanned"));
        addActivity(unplannedToDo);
        unplannedToDo.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
        insertRow(unplannedToDo);
        // Set the blinking cursor and the ability to type in right away
        table.editCellAt(table.getSelectedRow(), activitiesTableModel.getColumnCount() - 1 - 4); // edit cell
        table.setSurrendersFocusOnKeystroke(true); // focus
        if (table.getEditorComponent() != null) {
            table.getEditorComponent().requestFocus();
        }
        controlPane.setSelectedIndex(2); // open edit tab
        editPanel.showInfo(unplannedToDo); // set the id of the new task on the edit form
    }

    private void createExternalInterruption() {
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
            interruption.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
            insertRow(interruption);
            // Set the blinking cursor and the ability to type in right away
            table.editCellAt(table.getSelectedRow(), activitiesTableModel.getColumnCount() - 1 - 4); // edit cell
            table.setSurrendersFocusOnKeystroke(true); // focus
            if (table.getEditorComponent() != null) {
                table.getEditorComponent().requestFocus();
            }
            controlPane.setSelectedIndex(2); // open edit tab            
            editPanel.showInfo(interruption); // set the id of the new task on the edit form
        }
    }

    private void createInternalInterruption() {
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
            interruption.setName(""); // the idea is to insert an empty title in the model so the editing (editCellAt) shows an empty field
            insertRow(interruption);
            // Set the blinking cursor and the ability to type in right away
            table.editCellAt(table.getSelectedRow(), activitiesTableModel.getColumnCount() - 1 - 4); // edit cell
            table.setSurrendersFocusOnKeystroke(true); // focus
            if (table.getEditorComponent() != null) {
                table.getEditorComponent().requestFocus();
            }
            controlPane.setSelectedIndex(2); // open edit tab            
            editPanel.showInfo(interruption); // set the id of the new task on the edit form
        }
    }

    private void scrollToCurrentTask() {
        if (pomodoro.inPomodoro()) {
            for (int row = 0; row < table.getRowCount(); row++) {
                Integer id = (Integer) activitiesTableModel.getValueAt(table.convertRowIndexToModel(row), activitiesTableModel.getColumnCount() - 1);
                if (pomodoro.getCurrentToDo().getId() == id) {
                    currentSelectedRow = row;
                }
            }
            table.setRowSelectionInterval(currentSelectedRow, currentSelectedRow);
        }
        showCurrentSelectedRow();
    }

    public void showSelectedButton() {
        selectedButton.setIcon(selectedIcon);
    }

    public void showRunningButton() {
        selectedButton.setIcon(runningIcon);
    }
    
    protected ToDoList getList() {
        return ToDoList.getList();
    }
}
