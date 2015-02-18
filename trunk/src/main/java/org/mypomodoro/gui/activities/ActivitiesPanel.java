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
package org.mypomodoro.gui.activities;

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
import java.awt.event.MouseMotionAdapter;
import java.awt.font.TextAttribute;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.buttons.MoveButton;
import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityCommentTableListener;
import org.mypomodoro.gui.ActivityEditTableListener;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.create.list.TypeList;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ColumnResizer;
import org.mypomodoro.util.ComponentTitledBorder;
import org.mypomodoro.util.CustomTableHeader;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;
import org.mypomodoro.util.WaitCursor;

/**
 * GUI for viewing what is in the ActivityList. This can be changed later. Right
 * now it uses a TableModel to build the JTable. Table Listeners can be added to
 * save cell edits to the ActivityCollection which can then be saved to the data
 * layer.
 *
 */
public class ActivitiesPanel extends JPanel implements IListPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension PANE_DIMENSION = new Dimension(400, 200);
    private static final Dimension TABPANE_DIMENSION = new Dimension(400, 50);
    private AbstractActivitiesTableModel activitiesTableModel;
    private final JXTable table;
    private final JScrollPane scrollPane;
    private final JTabbedPane controlPane = new JTabbedPane();
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
    private final EditPanel editPanel = new EditPanel(this, detailsPanel);
    private final MergingPanel mergingPanel = new MergingPanel(this);
    private final JSplitPane splitPane;
    private InputMap im = null;
    private int mouseHoverRow = 0;
    // Border
    private final JButton titledButton = new JButton();
    private final ComponentTitledBorder titledborder = new ComponentTitledBorder(titledButton, this, new EtchedBorder(), getFont().deriveFont(Font.BOLD));
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));
    // Selected row
    private int currentSelectedRow = 0;

    public ActivitiesPanel() {
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
        scrollPane = new JScrollPane(table);
        scrollPane.setMinimumSize(PANE_DIMENSION);
        scrollPane.setPreferredSize(PANE_DIMENSION);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // Split pane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, controlPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setDividerSize(10);
        //BasicSplitPaneDivider divider = (BasicSplitPaneDivider) splitPane.getComponent(2);
        //divider.setBackground(ColorUtil.YELLOW_ROW);
        //divider.setBorder(new MatteBorder(1, 1, 1, 1, ColorUtil.BLUE_ROW));
        add(splitPane, gbc);
    }

    // add all listener once and for all
    private void setUpTable() {
        table.setBackground(ColorUtil.WHITE);
        table.setSelectionBackground(ColorUtil.BLUE_ROW);
        table.setForeground(ColorUtil.BLACK);
        table.setSelectionForeground(ColorUtil.BLACK);

        // add tooltip to header columns
        String[] cloneColumnNames = columnNames.clone();
        cloneColumnNames[ID_KEY - 7] = Labels.getString("Common.Unplanned");
        cloneColumnNames[ID_KEY - 6] = Labels.getString("Common.Date scheduled");
        cloneColumnNames[ID_KEY - 3] = Labels.getString("Common.Estimated") + " (+ " + Labels.getString("Common.Overestimated") + ")";
        CustomTableHeader customTableHeader = new CustomTableHeader(table, cloneColumnNames);
        table.setTableHeader(customTableHeader);

        // Add tooltip 
        table.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = table.rowAtPoint(p);
                int columnIndex = table.columnAtPoint(p);
                if (rowIndex != -1) {
                    if (columnIndex == ID_KEY - 5 || columnIndex == ID_KEY - 4) {
                        String value = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), columnIndex));
                        value = value.length() > 0 ? value : null;
                        table.setToolTipText(value);
                    } else if (columnIndex == ID_KEY - 6) { // date
                        Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), getIdKey());
                        Activity activity = getActivityById(id);
                        String value = DateUtil.getFormatedDate(activity.getDate(), "EEE, dd MMM yyyy");
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
                        Activity activity = ActivityList.getList().getById(id);
                        detailsPanel.selectInfo(activity);
                        detailsPanel.showInfo();
                        commentPanel.showInfo(activity);
                    }
                    mouseHoverRow = rowIndex;
                }
            }
        });
        // This is to address the case/event when the mouse exit the table
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                // Reset to currently selected task
                if (table.getSelectedRowCount() == 1) {
                    Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), ID_KEY);
                    Activity activity = ActivityList.getList().getById(id);
                    if (activity != null) {
                        detailsPanel.selectInfo(activity);
                        detailsPanel.showInfo();
                        commentPanel.showInfo(activity);
                    }
                }
                mouseHoverRow = -1;
            }
        });

        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        if (table.getSelectedRowCount() > 0) {
                            //System.err.println(e);
                            if (!e.getValueIsAdjusting()) { // ignoring the deselection event
                                //System.err.println(table.getSelectedRowCount());
                                // See above for reason to set WHEN_FOCUSED here
                                table.setInputMap(JTable.WHEN_FOCUSED, im);

                                if (table.getSelectedRowCount() > 1) { // multiple selection
                                    // diactivate/gray out unused tabs
                                    controlPane.setEnabledAt(1, false); // comment
                                    controlPane.setEnabledAt(2, false); // edit                                    
                                    controlPane.setEnabledAt(3, true); // merging                                    
                                    if (controlPane.getSelectedIndex() == 1
                                    || controlPane.getSelectedIndex() == 2) {
                                        controlPane.setSelectedIndex(0); // switch to details panel
                                    }
                                    currentSelectedRow = table.getSelectedRows()[0]; // always selecting the first selected row (otherwise removeRow will fail)
                                } else if (table.getSelectedRowCount() == 1) {
                                    // activate all panels
                                    for (int index = 0; index < controlPane.getTabCount(); index++) {
                                        if (index == 3) {
                                            controlPane.setEnabledAt(3, false); // merging
                                            if (controlPane.getSelectedIndex() == 3) {
                                                controlPane.setSelectedIndex(0); // switch to details panel
                                            }
                                        } else {
                                            controlPane.setEnabledAt(index, true);
                                        }
                                    }
                                    if (controlPane.getTabCount() > 0) { // at start-up time not yet initialised (see constructor)
                                        controlPane.setSelectedIndex(controlPane.getSelectedIndex()); // switch to selected panel
                                    }
                                    currentSelectedRow = table.getSelectedRow();
                                    showCurrentSelectedRow(); // when sorting columns, focus on selected row
                                }
                                setPanelBorder();
                            }
                        } else {
                            setPanelBorder();
                        }
                    }
                });

        // Activate Delete key stroke
        // This is a tricky one : we first use WHEN_IN_FOCUSED_WINDOW to allow the deletion of the first selected row (by default, selected with setRowSelectionInterval not mouse pressed/focus)
        // Then in ListSelectionListener we use WHEN_FOCUSED to prevent the title column to switch to edit mode when pressing the delete key
        // none of table.requestFocus(), transferFocus() and changeSelection(0, 0, false, false) will do any good here to get focus on the first row
        im = table.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = table.getActionMap();
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "Delete"); // for MAC
        } else {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete");
        }
        class deleteAction extends AbstractAction {

            final IListPanel panel;

            public deleteAction(IListPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                DeleteButton b = new DeleteButton(Labels.getString("Common.Delete activity"), Labels.getString("Common.Are you sure to delete those activities?"), panel);
                b.doClick();
            }
        }
        am.put("Delete", new deleteAction(this));
        // Activate Shift + '>'                
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.SHIFT_MASK), "Add To ToDo List");
        class moveAction extends AbstractAction {

            final IListPanel panel;

            public moveAction(IListPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                MoveButton moveButton = new MoveButton("", panel);
                moveButton.doClick();
            }
        }
        am.put("Add To ToDo List", new moveAction(this));
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
        for (int i = 1; i <= 6; i++) {
            im.put(KeyStroke.getKeyStroke(getKeyEvent(i), InputEvent.CTRL_DOWN_MASK), "Tab" + i);
            am.put("Tab" + i, new tabAction(i - 1));
        }

        // Activate Control T (create new task)        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), "Control T");
        class create extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                Activity newActivity = new Activity();
                newActivity.setEstimatedPoms(0);
                newActivity.setName(Labels.getString("Common.Task"));
                addActivity(newActivity);
                insertRow(newActivity);
            }
        }
        am.put("Control T", new create());

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
                        addActivity(copiedActivity, new Date(), new Date(0));
                        insertRow(copiedActivity);
                    } catch (CloneNotSupportedException ignored) {
                    }
                }
            }
        }
        am.put("Duplicate", new duplicate());

        // Activate Control R (scroll back to the selected task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), "Scroll");
        class scrollBackToTask extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
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

        // set custom render for dates
        table.getColumnModel().getColumn(ID_KEY - 7).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        table.getColumnModel().getColumn(ID_KEY - 6).setCellRenderer(new DateRenderer()); // date (custom renderer)
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(new TitleRenderer()); // title
        // type combo box
        String[] types = (String[]) TypeList.getTypes().toArray(new String[0]);
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(new ComboBoxCellRenderer(types, true));
        table.getColumnModel().getColumn(ID_KEY - 4).setCellEditor(new ComboBoxCellEditor(types, true));
        // Estimated combo box
        // The values of the combo depends on the activity : see EstimatedComboBoxCellRenderer and EstimatedComboBoxCellEditor
        table.getColumnModel().getColumn(ID_KEY - 3).setCellRenderer(new EstimatedComboBoxCellRenderer(new Integer[0], false));
        table.getColumnModel().getColumn(ID_KEY - 3).setCellEditor(new EstimatedComboBoxCellEditor(new Integer[0], false));
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
        if (!Main.preferences.getAgileMode()) {
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
        if (Main.preferences.getAgileMode()) {
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

        // diactivate/gray out all tabs (except import)
        if (table.getRowCount() == 0) {
            for (int index = 0; index < controlPane.getComponentCount(); index++) {
                if (index == 4) { // import tab
                    controlPane.setSelectedIndex(index);
                    continue;
                }
                controlPane.setEnabledAt(index, false);
            }
        } else {
            // select first activity
            int currentRow = table.convertRowIndexToView(currentSelectedRow);
            table.setRowSelectionInterval(currentRow, currentRow);
            table.scrollRectToVisible(table.getCellRect(currentRow, 0, true));
            // detail tab
            controlPane.setSelectedIndex(0);
        }

        // Refresh panel border
        setPanelBorder();

        // Make sure column title will fit long titles
        ColumnResizer.adjustColumnPreferredWidths(table);
        table.revalidate();
    }

    @Override
    public void setPanelBorder() {
        String titleActivitiesList = Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ActivityListPanel.Activity List");
        if (ActivityList.getListSize() > 0) {
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
                    storypoints += selectedActivity.getStoryPoints();
                    real += selectedActivity.getActualPoms();
                }
                titleActivitiesList += " (" + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + table.getSelectedRowCount() + "&nbsp;</span>" + "/" + ActivityList.getListSize() + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": " + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + real + " / " + estimated;
                if (overestimated > 0) {
                    titleActivitiesList += " + " + overestimated;
                }
                titleActivitiesList += "&nbsp;</span>";
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + df.format(storypoints) + "&nbsp;</span>";
                }
                // Tool tip
                String toolTipText = TimeConverter.getLength(estimated + overestimated);
                if (Main.preferences.getPlainHours()) {
                    toolTipText += " (" + Labels.getString("Common.Plain hours") + ")";
                } else {
                    toolTipText += " (" + Labels.getString("Common.Effective hours") + ")";
                }
                titledborder.setToolTipText(toolTipText);
            } else {
                titleActivitiesList += " (" + ActivityList.getListSize() + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": ";
                titleActivitiesList += ActivityList.getList().getNbRealPom();
                titleActivitiesList += " / " + ActivityList.getList().getNbEstimatedPom();
                if (ActivityList.getList().getNbOverestimatedPom() > 0) {
                    titleActivitiesList += " + " + ActivityList.getList().getNbOverestimatedPom();
                }
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(ActivityList.getList().getStoryPoints());
                }
                // Tool tip
                String toolTipText = TimeConverter.getLength(ActivityList.getList().getNbEstimatedPom() + ActivityList.getList().getNbOverestimatedPom());
                if (Main.preferences.getPlainHours()) {
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

    private void addTabPane() {
        controlPane.setFocusable(false); // removes borders around tab text
        controlPane.add(Labels.getString("Common.Details"), detailsPanel);
        controlPane.add(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        controlPane.add(Labels.getString("Common.Edit"), editPanel);
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
                if (e.getType() != TableModelEvent.DELETE && e.getType() != TableModelEvent.INSERT) {
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
                        if (Main.preferences.getAgileMode()) {
                            commentPanel.showInfo(act);
                        }
                        // refresh the combo boxes of all rows to display the new type (if any)
                        String[] types = (String[]) TypeList.getTypes().toArray(new String[0]);
                        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(new ComboBoxCellRenderer(types, true));
                        table.getColumnModel().getColumn(ID_KEY - 4).setCellEditor(new ComboBoxCellEditor(types, true));
                    } else if (column == ID_KEY - 3) { // Estimated
                        int estimated = (Integer) data;
                        if (estimated + act.getOverestimatedPoms() >= act.getActualPoms()) {
                            act.setEstimatedPoms(estimated);
                            act.databaseUpdate();
                        }
                    } else if (column == ID_KEY - 2) { // Story Points
                        act.setStoryPoints((Float) data);
                        act.databaseUpdate();
                    } else if (column == ID_KEY - 1) { // Iteration                        
                        act.setIteration(Integer.parseInt(data.toString()));
                        act.databaseUpdate();
                    }
                    ActivityList.getList().update(act);
                    // Refresh panel border after updating the list
                    setPanelBorder();
                    // update info
                    detailsPanel.selectInfo(act);
                    detailsPanel.showInfo();
                }
                // diactivate/gray out all tabs (except import)
                if (table.getRowCount() == 0) {
                    for (int index = 0; index < controlPane.getComponentCount(); index++) {
                        if (index == 4) { // import panel
                            controlPane.setSelectedIndex(index);
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
        Object[] rowData = new Object[8];
        rowData[0] = activity.isUnplanned();
        rowData[1] = activity.getDate();
        rowData[2] = activity.getName();
        rowData[3] = activity.getType();
        Integer poms = new Integer(activity.getEstimatedPoms());
        rowData[4] = poms;
        Float points = new Float(activity.getStoryPoints());
        rowData[5] = points;
        Integer iteration = new Integer(activity.getIteration());
        rowData[6] = iteration;
        rowData[7] = activity.getId();
        // By default, the row is added at the bottom of the list
        // However, if one of the columns has been previously sorted the position of the row might not be the bottom position...
        activitiesTableModel.addRow(rowData); // we add in the Model...        
        //activitiesTableModel.insertRow(table.getRowCount(), rowData); // we add in the Model... 
        int currentRow = table.convertRowIndexToView(table.getRowCount() - 1); // ...while selecting in the View
        table.setRowSelectionInterval(currentRow, currentRow);
        table.scrollRectToVisible(table.getCellRect(currentRow, 0, true));
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

    @Override
    public void addActivity(Activity activity, Date date, Date dateCompleted) {
        ActivityList.getList().add(activity, date, dateCompleted);
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
                new ActivityCommentTableListener(ActivityList.getList(),
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
                    ActivityList.getList().refresh();
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
            Activity activity = ActivityList.getList().getById(id);
            if (activity != null && activity.isFinished()) {
                renderer.setForeground(ColorUtil.GREEN);
            }
            return renderer;
        }
    }

    class TitleRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            /*if (!Main.preferences.getAgileMode()) { // Pomodoro mode only
             int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
             Activity activity = ActivityList.getList().getById(id);
             if (activity != null && activity.isOverdue()) {
             if (!getFont().canDisplay('\u226b')) { // unicode tick
             renderer.setText(">> " + (String) value);
             } else {
             renderer.setText("\u226b " + (String) value);
             }
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

    class DateRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setText((value == null || DateUtil.isSameDay((Date) value, new Date(0))) ? "" : DateUtil.getShortFormatedDate((Date) value));
            // Strikethrough date when task overdue
            if (!Main.preferences.getAgileMode()) { // Pomodoro mode only
                int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
                Activity activity = ActivityList.getList().getById(id);
                if (activity != null && activity.isOverdue()) {
                    Map<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
                    map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                    //map.put(TextAttribute.FOREGROUND, ColorUtil.RED);
                    renderer.setFont(getFont().deriveFont(map));
                }
            }
            return renderer;
        }
    }

    @Override
    public void saveComment(String comment) {
        if (table.getSelectedRowCount() == 1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), ID_KEY);
            Activity selectedActivity = ActivityList.getList().getById(id);
            if (selectedActivity != null) {
                selectedActivity.setNotes(comment);
                selectedActivity.databaseUpdateComment();
            }
        }
    }

    public void setCurrentSelectedRow(int row) {
        currentSelectedRow = row;
    }

    public void showCurrentSelectedRow() {
        table.scrollRectToVisible(table.getCellRect(currentSelectedRow, 0, true));
    }
}
