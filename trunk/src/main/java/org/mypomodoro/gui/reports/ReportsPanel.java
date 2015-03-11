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
package org.mypomodoro.gui.reports;

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
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.EventObject;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;
import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.buttons.MoveButton;
import org.mypomodoro.db.mysql.MySQLConfigLoader;
import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityCommentTableListener;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.activities.CommentPanel;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ColumnResizer;
import org.mypomodoro.util.CustomTableHeader;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;
import static org.mypomodoro.util.TimeConverter.getLength;
import org.mypomodoro.util.WaitCursor;

/**
 * GUI for viewing the Report List.
 *
 */
public class ReportsPanel extends JPanel implements IListPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension PANE_DIMENSION = new Dimension(400, 200);
    private static final Dimension TABPANE_DIMENSION = new Dimension(400, 50);
    private AbstractActivitiesTableModel activitiesTableModel;
    private final JXTable table;
    private final JPanel scrollPane = new JPanel();
    private static final String[] columnNames = {"U",
        Labels.getString("Common.Date"),
        Labels.getString("Common.Title"),
        Labels.getString("Common.Type"),
        Labels.getString("Common.Estimated"),
        Labels.getString("ReportListPanel.Diff I"),
        Labels.getString("ReportListPanel.Diff II"),
        Labels.getString("Agile.Common.Story Points"),
        Labels.getString("Agile.Common.Iteration"),
        "ID"};
    //Labels.getString("ReportListPanel.Time")
    public static int ID_KEY = 9;
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final EditPanel editPanel = new EditPanel(detailsPanel);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final JSplitPane splitPane;
    private final JTabbedPane controlPane = new JTabbedPane();
    private InputMap im = null;
    private int mouseHoverRow = 0;
    // Title  
    private final JPanel titlePanel = new JPanel();
    private final JLabel titleLabel = new JLabel();
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));
    private final ImageIcon duplicateIcon = new ImageIcon(Main.class.getResource("/images/duplicate.png"));
    private final ImageIcon selectedIcon = new ImageIcon(Main.class.getResource("/images/selected.png"));
    private final DefaultButton refreshButton = new DefaultButton(refreshIcon);
    private final DefaultButton duplicateButton = new DefaultButton(duplicateIcon);
    private final DefaultButton selectedButton = new DefaultButton(selectedIcon);
    private final GridBagConstraints cScrollPane = new GridBagConstraints(); // title + table
    // Selected row
    private int currentSelectedRow = 0;

    public ReportsPanel() {
        setLayout(new GridBagLayout());

        activitiesTableModel = getTableModel();

        table = new JXTable(activitiesTableModel) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    ((JComponent) c).setBackground(ColorUtil.BLUE_ROW);
                    ((JComponent) c).setFont(getFont().deriveFont(Font.BOLD));
                } else if (row == mouseHoverRow) {
                    ((JComponent) c).setBackground(ColorUtil.YELLOW_ROW);
                    ((JComponent) c).setFont(getFont().deriveFont(Font.BOLD));
                    Component[] comps = ((JComponent) c).getComponents();
                    for (Component comp : comps) { // sub-components (combo boxes)
                        comp.setFont(getFont().deriveFont(Font.BOLD));
                    }
                    ((JComponent) c).setBorder(new MatteBorder(1, 0, 1, 0, ColorUtil.BLUE_ROW));
                } else {
                    if (row % 2 == 0) { // odd
                        ((JComponent) c).setBackground(ColorUtil.WHITE);
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
                showCurrentSelectedRow();
            }
        });
        selectedButton.setToolTipText("CTRL + G");
        duplicateButton.setMargin(buttonInsets);
        duplicateButton.setFocusPainted(false); // removes borders around text
        duplicateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateTask();
            }
        });
        duplicateButton.setToolTipText("CTRL + D");
        if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)     
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
        }

        // Add components        
        addTitlePanel();
        addTable();
        // Bottom pane
        // Init control pane before the table so we can set the default tab at start up time
        controlPane.setMinimumSize(TABPANE_DIMENSION);
        controlPane.setPreferredSize(TABPANE_DIMENSION);
        addTabPane();
        // Split pane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, controlPane);
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
        table.setBackground(ColorUtil.WHITE);
        table.setSelectionBackground(ColorUtil.BLUE_ROW);
        table.setForeground(ColorUtil.BLACK);
        table.setSelectionForeground(ColorUtil.BLACK);

        // add tooltip to header columns
        String[] cloneColumnNames = columnNames.clone();
        cloneColumnNames[ID_KEY - 9] = Labels.getString("Common.Unplanned");
        cloneColumnNames[ID_KEY - 8] = Labels.getString("Common.Date completed");
        cloneColumnNames[ID_KEY - 5] = Labels.getString("Common.Real") + " / " + Labels.getString("Common.Estimated") + " (+ " + Labels.getString("Common.Overestimated") + ")";
        CustomTableHeader customTableHeader = new CustomTableHeader(table, cloneColumnNames);
        table.setTableHeader(customTableHeader);

        // Add tooltip for Title and Type colums 
        table.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = table.rowAtPoint(p);
                int columnIndex = table.columnAtPoint(p);
                if (rowIndex != -1) {
                    if (columnIndex == ID_KEY - 7 || columnIndex == ID_KEY - 6) {
                        String value = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), columnIndex));
                        value = value.length() > 0 ? value : null;
                        table.setToolTipText(value);
                    } else if (columnIndex == ID_KEY - 8) { // date
                        Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), getIdKey());
                        Activity activity = getActivityById(id);
                        String value = DateUtil.getFormatedDate(activity.getDateCompleted(), "EEE, dd MMM yyyy") + ", " + DateUtil.getFormatedTime(activity.getDateCompleted());
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
                        Activity activity = ReportList.getList().getById(id);
                        detailsPanel.selectInfo(activity);
                        detailsPanel.showInfo();
                        commentPanel.showInfo(activity);
                        editPanel.showInfo(activity);
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
                    Activity activity = ReportList.getList().getById(id);
                    if (activity != null) {
                        detailsPanel.selectInfo(activity);
                        detailsPanel.showInfo();
                        commentPanel.showInfo(activity);
                        editPanel.showInfo(activity);
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
                                    if (controlPane.getSelectedIndex() == 1
                                    || controlPane.getSelectedIndex() == 2) {
                                        controlPane.setSelectedIndex(0); // switch to details panel
                                    }
                                    currentSelectedRow = table.getSelectedRows()[0]; // always selecting the first selected row (oterwise removeRow will fail)
                                } else if (table.getSelectedRowCount() == 1) {
                                    // activate all panels
                                    for (int index = 0; index < controlPane.getTabCount(); index++) {
                                        controlPane.setEnabledAt(index, true);
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
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "Delete"); // for MAC
        } else {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "Delete");
        }
        am.put("Delete", new deleteAction(this));
        // Activate Shift + '<'
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, KeyEvent.SHIFT_MASK), "Reopen");
        class reopenAction extends AbstractAction {

            final IListPanel panel;

            public reopenAction(IListPanel panel) {
                this.panel = panel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                MoveButton moveButton = new MoveButton("", panel);
                moveButton.doClick();
            }
        }
        am.put("Reopen", new reopenAction(this));
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
        for (int i = 1; i <= 5; i++) {
            im.put(KeyStroke.getKeyStroke(getKeyEvent(i), KeyEvent.CTRL_DOWN_MASK), "Tab" + i);
            am.put("Tab" + i, new tabAction(i - 1));
        }

        // Activate Control D (duplicate task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), "Duplicate");
        class duplicate extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateTask();
            }
        }
        am.put("Duplicate", new duplicate());

        // Activate Control R (scroll back to the selected task)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK), "Scroll");
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

        // Centre columns
        CustomTableRenderer dtcr = new CustomTableRenderer();
        // set custom render for dates
        table.getColumnModel().getColumn(ID_KEY - 9).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        table.getColumnModel().getColumn(ID_KEY - 8).setCellRenderer(new DateRenderer()); // date (custom renderer)
        //table.getColumnModel().getColumn(ID_KEY - 7).setCellRenderer(dtcr); // time
        table.getColumnModel().getColumn(ID_KEY - 7).setCellRenderer(dtcr); // title
        table.getColumnModel().getColumn(ID_KEY - 6).setCellRenderer(dtcr); // type
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(new EstimatedCellRenderer()); // estimated        
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(dtcr); // Diff I
        table.getColumnModel().getColumn(ID_KEY - 3).setCellRenderer(new Diff2CellRenderer()); // Diff II
        table.getColumnModel().getColumn(ID_KEY - 2).setCellRenderer(new StoryPointsCellRenderer()); // story points
        table.getColumnModel().getColumn(ID_KEY - 1).setCellRenderer(new IterationCellRenderer()); // iteration
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
            table.getColumnModel().getColumn(ID_KEY - 2).setMaxWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 2).setMinWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 2).setPreferredWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 1).setMaxWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 1).setMinWidth(40);
            table.getColumnModel().getColumn(ID_KEY - 1).setPreferredWidth(40);
        }
        // hide unplanned in Agile mode
        if (Main.preferences.getAgileMode()) {
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
        table.getColumnModel().getColumn(ID_KEY - 8).setMaxWidth(90);
        table.getColumnModel().getColumn(ID_KEY - 8).setMinWidth(90);
        table.getColumnModel().getColumn(ID_KEY - 8).setPreferredWidth(90);
        // Set width of estimated, diff I/II
        table.getColumnModel().getColumn(ID_KEY - 5).setMaxWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 5).setMinWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 5).setPreferredWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 4).setMaxWidth(40);
        table.getColumnModel().getColumn(ID_KEY - 4).setMinWidth(40);
        table.getColumnModel().getColumn(ID_KEY - 4).setPreferredWidth(40);
        table.getColumnModel().getColumn(ID_KEY - 3).setMaxWidth(40);
        table.getColumnModel().getColumn(ID_KEY - 3).setMinWidth(40);
        table.getColumnModel().getColumn(ID_KEY - 3).setPreferredWidth(40);
        // Set width of column Time
        /*table.getColumnModel().getColumn(2).setMaxWidth(60);
         table.getColumnModel().getColumn(2).setMinWidth(60);
         table.getColumnModel().getColumn(2).setPreferredWidth(60);*/
        // Set min width of type column
        table.getColumnModel().getColumn(ID_KEY - 6).setMinWidth(100);
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
            for (int index = 0; index < controlPane.getTabCount(); index++) {
                if (index == 3) { // import tab
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

        // Make sure column title will fit long titles
        ColumnResizer.adjustColumnPreferredWidths(table);
        table.revalidate();
    }

    @Override
    public void setPanelBorder() {
        String titleActivitiesList = Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ReportListPanel.Report List");
        if (ReportList.getListSize() > 0) {
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
                titleActivitiesList += " (" + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + table.getSelectedRowCount() + "&nbsp;</span>" + "/" + ReportList.getListSize() + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": " + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + real + " / " + estimated;
                if (overestimated > 0) {
                    titleActivitiesList += " + " + overestimated;
                }
                titleActivitiesList += "&nbsp;</span>";
                titleActivitiesList += " (" + Labels.getString("ReportListPanel.Accuracy") + ": " + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + Math.round(((float) real / ((float) estimated + overestimated)) * 100) + "%" + "&nbsp;</span>" + ")";
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Velocity") + ": " + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + df.format(storypoints) + "&nbsp;</span>";
                }
                // Tool tip
                String toolTipText = TimeConverter.getLength(estimated + overestimated);
                if (Main.preferences.getPlainHours()) {
                    toolTipText += " (" + Labels.getString("Common.Plain hours") + ")";
                } else {
                    toolTipText += " (" + Labels.getString("Common.Effective hours") + ")";
                }
                titleLabel.setToolTipText(toolTipText);
                // Hide buttons of the quick bar 
                titlePanel.remove(selectedButton);
                titlePanel.remove(duplicateButton);
            } else {
                titleActivitiesList += " (" + ReportList.getListSize() + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": ";
                titleActivitiesList += ReportList.getList().getNbRealPom();
                titleActivitiesList += " / " + ReportList.getList().getNbEstimatedPom();
                if (ReportList.getList().getNbOverestimatedPom() > 0) {
                    titleActivitiesList += " + " + ReportList.getList().getNbOverestimatedPom();
                }
                titleActivitiesList += " (" + Labels.getString("ReportListPanel.Accuracy") + ": " + ReportList.getList().getAccuracy() + "%)";
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(ReportList.getList().getStoryPoints());
                }
                // Tool tip
                String toolTipText = TimeConverter.getLength(ReportList.getList().getNbEstimatedPom() + ReportList.getList().getNbOverestimatedPom());
                if (Main.preferences.getPlainHours()) {
                    toolTipText += " (" + Labels.getString("Common.Plain hours") + ")";
                } else {
                    toolTipText += " (" + Labels.getString("Common.Effective hours") + ")";
                }
                titleLabel.setToolTipText(toolTipText);
                // Show buttons of the quick bar                                    
                titlePanel.add(selectedButton);
                titlePanel.add(duplicateButton);
            }
            if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
                titlePanel.add(refreshButton); // end of the line
            }
        } else {
            titlePanel.remove(selectedButton);
            titlePanel.remove(duplicateButton);
            if (MySQLConfigLoader.isValid()) { // Remote mode (using MySQL database)
                titlePanel.remove(refreshButton);
            }
        }
        // Update title       
        titleLabel.setText("<html>" + titleActivitiesList + "</html>");
        titlePanel.repaint(); // this is necessary to force stretching of panel
    }

    private void addTitlePanel() {
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 0;
        cScrollPane.weightx = 1.0;
        cScrollPane.anchor = GridBagConstraints.WEST;
        cScrollPane.fill = GridBagConstraints.BOTH;
        titlePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        scrollPane.add(titlePanel, cScrollPane);
    }

    public void addTable() {
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 1;
        cScrollPane.weightx = 1.0;
        cScrollPane.weighty = 1.0;
        cScrollPane.fill = GridBagConstraints.BOTH;
        JScrollPane tableScrollPane = new JScrollPane(table);
        scrollPane.add(tableScrollPane, cScrollPane);
    }

    private void addTabPane() {
        controlPane.setFocusable(false); // removes borders around tab text
        controlPane.add(Labels.getString("Common.Details"), detailsPanel);
        controlPane.add(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        controlPane.add(Labels.getString("Common.Edit"), editPanel);
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
        int rowIndex = ReportList.getList().size();
        int colIndex = columnNames.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = ReportList.getList().iterator();
        for (int i = 0; i < ReportList.getList().size() && iterator.hasNext(); i++) {
            Activity a = iterator.next();
            tableData[i][0] = a.isUnplanned();
            tableData[i][1] = a.getDateCompleted(); // date completed formated via custom renderer (DateRenderer)
            //tableData[i][2] = DateUtil.getFormatedTime(a.getDate());
            tableData[i][2] = a.getName();
            tableData[i][3] = a.getType();
            Integer poms = new Integer(a.getActualPoms()); // sorting done on real pom
            tableData[i][4] = poms;
            Integer diffIPoms = new Integer(a.getActualPoms() - a.getEstimatedPoms());
            tableData[i][5] = diffIPoms; // Diff I
            Integer diffIIPoms = new Integer(a.getActualPoms()
                    - a.getEstimatedPoms()
                    - a.getOverestimatedPoms());
            tableData[i][6] = diffIIPoms; // Diff II
            Float points = new Float(a.getStoryPoints());
            tableData[i][7] = points;
            Integer iteration = new Integer(a.getIteration());
            tableData[i][8] = iteration;
            tableData[i][9] = a.getId();
        }

        AbstractActivitiesTableModel tableModel = new AbstractActivitiesTableModel(tableData, columnNames) {

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == ID_KEY - 7;
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
                        return Integer.class;
                    case 7:
                        return Float.class;
                    case 8:
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
                    Object data = model.getValueAt(row, column);
                    if (data != null) {
                        Integer ID = (Integer) model.getValueAt(row, ID_KEY); // ID
                        Activity act = Activity.getActivity(ID.intValue());
                        if (column == ID_KEY - 7) { // Title (can't be empty)
                            String name = data.toString().trim();
                            if (name.length() == 0) {
                                // reset the original value. Title can't be empty.
                                model.setValueAt(act.getName(), table.convertRowIndexToModel(row), ID_KEY - 7);
                            } else {
                                act.setName(name);
                                act.databaseUpdate();
                                // The customer resizer may resize the title column to fit the length of the new text
                                ColumnResizer.adjustColumnPreferredWidths(table);
                                table.revalidate();
                            }
                        }
                        ReportList.getList().update(act);
                        // update info
                        detailsPanel.selectInfo(act);
                        detailsPanel.showInfo();
                    }
                }
                // diactivate/gray out all tabs (except import)
                if (table.getRowCount() == 0) {
                    for (int index = 0; index < controlPane.getTabCount(); index++) {
                        if (index == 3) { // import panel
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
        table.clearSelection(); // clear the selection so removeRow won't fire valueChanged on ListSelectionListener (especially in case of large selection which is time consuming)
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
        Object[] rowData = new Object[10];
        rowData[0] = activity.isUnplanned();
        rowData[1] = activity.getDateCompleted();
        rowData[2] = activity.getName();
        rowData[3] = activity.getType();
        Integer poms = new Integer(activity.getActualPoms());
        rowData[4] = poms;
        Integer diffIPoms = new Integer(activity.getActualPoms() - activity.getEstimatedPoms());
        rowData[5] = diffIPoms; // Diff I
        Integer diffIIPoms = new Integer(activity.getActualPoms()
                - activity.getEstimatedPoms()
                - activity.getOverestimatedPoms());
        rowData[6] = diffIIPoms; // Diff II
        Float points = new Float(activity.getStoryPoints());
        rowData[7] = points;
        Integer iteration = new Integer(activity.getIteration());
        rowData[8] = iteration;
        rowData[9] = activity.getId();
        // By default, the row is added at the bottom of the list
        // However, if one of the columns has been previously sorted the position of the row might not be the bottom position...
        activitiesTableModel.addRow(rowData); // we add in the Model...        
        int currentRow = table.convertRowIndexToView(table.getRowCount() - 1); // ...while selecting in the View
        table.setRowSelectionInterval(currentRow, currentRow);
        table.scrollRectToVisible(table.getCellRect(currentRow, 0, true));
    }

    @Override
    public void move(Activity activity) {
        ReportList.getList().reopen(activity);
    }

    @Override
    public void moveAll() {
        ReportList.getList().reopenAll();
    }

    @Override
    public Activity getActivityById(int id) {
        return ReportList.getList().getById(id);
    }

    @Override
    public void delete(Activity activity) {
        ReportList.getList().delete(activity);
    }

    @Override
    public void deleteAll() {
        ReportList.getList().deleteAll();
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
        ReportList.getList().add(activity);
    }

    @Override
    public void addActivity(Activity activity, Date date, Date dateCompleted) {
        ReportList.getList().add(activity, date, dateCompleted);
    }

    private void showSelectedItemDetails(DetailsPanel detailPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ReportList.getList(),
                        table, detailPanel, ID_KEY));
    }

    private void showSelectedItemEdit(EditPanel editPanel) {
        /*table.getSelectionModel().addListSelectionListener(
         new ActivityEditTableListener(ReportList.getList(), table,
         editPanel, ID_KEY));*/
    }

    private void showSelectedItemComment(CommentPanel commentPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityCommentTableListener(ReportList.getList(),
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
                    ReportList.getList().refresh();
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
            Activity activity = ReportList.getList().getById(id);
            if (activity != null && activity.isFinished()) {
                renderer.setForeground(ColorUtil.GREEN);
            }
            /* Strikethrough task with no estimation
             if (activity != null && activity.getEstimatedPoms() == 0) {
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
            Activity activity = ReportList.getList().getById(id);
            if (activity != null) {
                int realpoms = activity.getActualPoms();
                int estimatedpoms = activity.getEstimatedPoms();
                int overestimatedpoms = activity.getOverestimatedPoms();
                String text = activity.getActualPoms() + " / " + activity.getEstimatedPoms() + (overestimatedpoms > 0 ? " + " + overestimatedpoms : "");
                renderer.setText(text);
                renderer.setToolTipText(getLength(realpoms) + " / " + getLength(estimatedpoms) + (overestimatedpoms > 0 ? " + " + getLength(overestimatedpoms) : ""));
            }
            return renderer;
        }
    }

    class Diff2CellRenderer extends CustomTableRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel renderer = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ID_KEY);
            Activity activity = ReportList.getList().getById(id);
            String text = value.toString();
            if (activity != null && activity.getOverestimatedPoms() == 0) {
                text = "";
            }
            renderer.setText(text);
            return renderer;
        }
    }

    @Override
    public void saveComment(String comment) {
        if (table.getSelectedRowCount() == 1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), ID_KEY);
            Activity selectedReport = ReportList.getList().getById(id);
            if (selectedReport != null) {
                selectedReport.setNotes(comment);
                selectedReport.databaseUpdateComment();
            }
        }
    }

    public void showCurrentSelectedRow() {
        table.scrollRectToVisible(table.getCellRect(currentSelectedRow, 0, true));
    }

    private void duplicateTask() {
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
}
