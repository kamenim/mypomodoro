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
package org.mypomodoro.gui.burndownchart;

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
import org.jdesktop.swingx.JXTable;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.buttons.TransparentButton;
import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityCommentTableListener;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.activities.CommentPanel;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ColumnResizer;
import org.mypomodoro.util.CustomTableHeader;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;
import org.mypomodoro.util.WaitCursor;

/**
 * GUI for viewing the Chart List.
 *
 */
public class CheckPanel extends JPanel implements IListPanel {

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
        Labels.getString("Agile.Common.Story Points"),
        Labels.getString("Agile.Common.Iteration"),
        "ID"};
    public static int ID_KEY = 7;
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final JSplitPane splitPane;
    private final JTabbedPane controlPane = new JTabbedPane();
    private final JTabbedPane tabbedPane;
    private final CreateChart chart;
    private InputMap im = null;
    private int mouseHoverRow = 0;
    // Title   
    private final JPanel titlePanel = new JPanel();
    private final JLabel titleLabel = new JLabel();   
    private final ImageIcon selectedIcon = new ImageIcon(Main.class.getResource("/images/selected.png"));
    private final TransparentButton selectedButton = new TransparentButton(selectedIcon);
    private final GridBagConstraints cScrollPane = new GridBagConstraints(); // title + table
    // Selected row
    private int currentSelectedRow = 0;

    public CheckPanel(JTabbedPane tabbedPane, CreateChart chart) {
        this.tabbedPane = tabbedPane;
        this.chart = chart;

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
        addTitlePanel();
        addTable();
        addCreateButton();
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
        splitPane.setDividerSize(10);
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

        // Add tooltip to header columns
        String[] cloneColumnNames = columnNames.clone();
        cloneColumnNames[ID_KEY - 7] = Labels.getString("Common.Unplanned");
        cloneColumnNames[ID_KEY - 6] = Labels.getString("Common.Date completed");
        cloneColumnNames[ID_KEY - 3] = Labels.getString("Common.Estimated") + " (+ " + Labels.getString("Common.Overestimated") + ")";
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
                    if (columnIndex == ID_KEY - 5 || columnIndex == ID_KEY - 4) {
                        String value = String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), columnIndex));
                        value = value.length() > 0 ? value : null;
                        table.setToolTipText(value);
                    } else if (columnIndex == ID_KEY - 6) { // date
                        Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(rowIndex), getIdKey());
                        Activity activity = getActivityById(id);
                        String value = "";
                        // The following may happen as tasks of the ToDo list may be mixed with tasks from the Report list
                        if (!DateUtil.isSameDay(activity.getDateCompleted(), new Date(0))) {
                            value = DateUtil.getFormatedDate(activity.getDateCompleted(), "EEE, dd MMM yyyy") + ", " + DateUtil.getFormatedTime(activity.getDateCompleted());
                        }
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
                        Activity activity = ChartList.getList().getById(id);
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
                    Activity activity = ChartList.getList().getById(id);
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
                                    if (controlPane.getSelectedIndex() == 1) {
                                        controlPane.setSelectedIndex(0); // switch to details panel
                                    }
                                    // Hide buttons of the quick bar 
                                    titlePanel.remove(selectedButton);
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
                                    // Show buttons of the quick bar                                    
                                    titlePanel.add(selectedButton, 1);
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
        for (int i = 1; i <= 3; i++) {
            im.put(KeyStroke.getKeyStroke(getKeyEvent(i), KeyEvent.CTRL_DOWN_MASK), "Tab" + i);
            am.put("Tab" + i, new tabAction(i - 1));
        }

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
        table.getColumnModel().getColumn(ID_KEY - 7).setCellRenderer(new UnplannedRenderer()); // unplanned (custom renderer)
        table.getColumnModel().getColumn(ID_KEY - 6).setCellRenderer(new DateRenderer()); // date (custom renderer)        
        table.getColumnModel().getColumn(ID_KEY - 5).setCellRenderer(dtcr); // title
        table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(dtcr); // type
        table.getColumnModel().getColumn(ID_KEY - 3).setCellRenderer(new EstimatedCellRenderer()); // estimated
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
        table.getColumnModel().getColumn(ID_KEY - 6).setMaxWidth(90);
        table.getColumnModel().getColumn(ID_KEY - 6).setMinWidth(90);
        table.getColumnModel().getColumn(ID_KEY - 6).setPreferredWidth(90);
        // Set width of estimated
        table.getColumnModel().getColumn(ID_KEY - 3).setMaxWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 3).setMinWidth(80);
        table.getColumnModel().getColumn(ID_KEY - 3).setPreferredWidth(80);
        // Set min width of type column
        table.getColumnModel().getColumn(ID_KEY - 4).setMinWidth(100);
        // hide ID column
        table.getColumnModel().getColumn(ID_KEY).setMaxWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setMinWidth(0);
        table.getColumnModel().getColumn(ID_KEY).setPreferredWidth(0);
        // enable sorting
        if (table.getModel().getRowCount() > 0) {
            table.setAutoCreateRowSorter(true);
        }

        // diactivate/gray out all tabs
        if (table.getRowCount() == 0) {
            for (int index = 0; index < controlPane.getTabCount(); index++) {
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
        String titleActivitiesList = Labels.getString("BurndownChartPanel.Chart List");
        if (ChartList.getListSize() > 0) {
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
                titleActivitiesList += " (" + "<span bgcolor=\"" + ColorUtil.toHex(ColorUtil.BLUE_ROW) + "\">&nbsp;" + table.getSelectedRowCount() + "&nbsp;</span>" + "/" + ChartList.getListSize() + ")";
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
            } else {
                titleActivitiesList += " (" + ChartList.getListSize() + ")";
                titleActivitiesList += " > " + Labels.getString("Common.Done") + ": ";
                titleActivitiesList += ChartList.getList().getNbRealPom();
                titleActivitiesList += " / " + ChartList.getList().getNbEstimatedPom();
                if (ChartList.getList().getNbOverestimatedPom() > 0) {
                    titleActivitiesList += " + " + ChartList.getList().getNbOverestimatedPom();
                }
                titleActivitiesList += " (" + Labels.getString("ReportListPanel.Accuracy") + ": " + ChartList.getList().getAccuracy() + "%)";
                if (Main.preferences.getAgileMode()) {
                    DecimalFormat df = new DecimalFormat("0.#");
                    titleActivitiesList += " > " + Labels.getString("Agile.Common.Story Points") + ": " + df.format(ChartList.getList().getStoryPoints());
                }
                // Tool tip
                String toolTipText = TimeConverter.getLength(ChartList.getList().getNbEstimatedPom() + ChartList.getList().getNbOverestimatedPom());
                if (Main.preferences.getPlainHours()) {
                    toolTipText += " (" + Labels.getString("Common.Plain hours") + ")";
                } else {
                    toolTipText += " (" + Labels.getString("Common.Effective hours") + ")";
                }
                titleLabel.setToolTipText(toolTipText);
            }
        }
        // Update title   
        titleLabel.setText("<html>" + titleActivitiesList + "</html>");
        titlePanel.repaint(); // this is necessary to force stretching of panel
    }

    private void addTitlePanel() {
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titleLabel.setFont(getFont().deriveFont(Font.BOLD));
        titlePanel.add(titleLabel);        
        selectedButton.setVisible(true); // this is a TransparentButton        
        selectedButton.setMargin(new Insets(0, 15, 0, 15));
        selectedButton.setFocusPainted(false); // removes borders around text
        selectedButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showCurrentSelectedRow();
            }
        });
        selectedButton.setToolTipText("CTRL + G");
        titlePanel.add(selectedButton);
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 0;
        cScrollPane.weightx = 1.0;
        cScrollPane.gridwidth = 2;
        cScrollPane.anchor = GridBagConstraints.WEST;
        cScrollPane.fill = GridBagConstraints.BOTH;
        titlePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        scrollPane.add(titlePanel, cScrollPane);
    }

    private void addTable() {
        cScrollPane.gridx = 0;
        cScrollPane.gridy = 1;
        cScrollPane.weightx = 1.0;
        cScrollPane.weighty = 1.0;
        cScrollPane.gridwidth = 1;
        cScrollPane.fill = GridBagConstraints.BOTH;
        JScrollPane tableScrollPane = new JScrollPane(table);
        scrollPane.add(tableScrollPane, cScrollPane);
    }

    private void addCreateButton() {
        cScrollPane.gridx = 1;
        cScrollPane.gridy = 1;
        cScrollPane.weightx = 0.1;
        cScrollPane.weighty = 1.0;
        cScrollPane.gridwidth = 1;
        JButton checkButton = new AbstractButton(
                Labels.getString("BurndownChartPanel.Create"));
        checkButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!WaitCursor.isStarted()) {
                    if (ChartList.getList().size() > 0) {
                        chart.create();
                        tabbedPane.setEnabledAt(3, true);
                        tabbedPane.setSelectedIndex(3);
                    }
                }
            }
        });
        scrollPane.add(checkButton, cScrollPane);
    }

    private void addTabPane() {
        controlPane.setFocusable(false); // removes borders around tab text
        controlPane.add(Labels.getString("Common.Details"), detailsPanel);
        controlPane.add(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
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
    }

    private AbstractActivitiesTableModel getTableModel() {
        int rowIndex = ChartList.getList().size();
        int colIndex = columnNames.length;
        Object[][] tableData = new Object[rowIndex][colIndex];
        Iterator<Activity> iterator = ChartList.getList().iterator();
        for (int i = 0; i < ChartList.getListSize() && iterator.hasNext(); i++) {
            Activity currentActivity = iterator.next();
            tableData[i][0] = currentActivity.isUnplanned();
            tableData[i][1] = currentActivity.getDateCompleted(); // date completed formated via custom renderer (DateRenderer)
            tableData[i][2] = currentActivity.getName();
            tableData[i][3] = currentActivity.getType();
            Integer poms = new Integer(currentActivity.getActualPoms());
            tableData[i][4] = poms;
            tableData[i][5] = currentActivity.getStoryPoints();
            tableData[i][6] = currentActivity.getIteration();
            tableData[i][7] = currentActivity.getId();
        }

        AbstractActivitiesTableModel tableModel = new AbstractActivitiesTableModel(tableData, columnNames) {

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            // this is mandatory to get columns with integers properly sorted
            @Override
            public Class
                    getColumnClass(int column) {
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

        tableModel.addTableModelListener(
                new TableModelListener() {

                    @Override
                    public void tableChanged(TableModelEvent e
                    ) {
                        // diactivate/gray out all tabs
                        if (table.getRowCount() == 0) {
                            for (int index = 0; index < controlPane.getTabCount(); index++) {
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
    public void insertRow(Activity activity) {
    }

    @Override
    public void move(Activity activity) {
        // no use
    }

    @Override
    public void moveAll() {
        // no use
    }

    @Override
    public Activity getActivityById(int id) {
        return ChartList.getList().getById(id);
    }

    @Override
    public void delete(Activity activity) {
        // no use
    }

    @Override
    public void deleteAll() {
        // no use
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
        // no use
    }

    @Override
    public void addActivity(Activity activity, Date date, Date dateCompleted) {
        // no use
    }

    private void showSelectedItemDetails(DetailsPanel informationPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityInformationTableListener(ChartList.getList(),
                        table, informationPanel, ID_KEY));
    }

    private void showSelectedItemComment(CommentPanel commentPanel) {
        table.getSelectionModel().addListSelectionListener(
                new ActivityCommentTableListener(ChartList.getList(),
                        table, commentPanel, ID_KEY));
    }

    @Override
    public void refresh() {
        if (!WaitCursor.isStarted()) {
            // Start wait cursor
            WaitCursor.startWaitCursor();
            try {
                if (table.getRowCount() == 0) {
                    currentSelectedRow = 0;
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
            Activity activity = ChartList.getList().getById(id);
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
            Activity activity = ChartList.getList().getById(id);
            if (activity != null) {
                String text = activity.getActualPoms() + " / " + activity.getEstimatedPoms();
                Integer overestimatedpoms = activity.getOverestimatedPoms();
                text += overestimatedpoms > 0 ? " + " + overestimatedpoms : "";
                renderer.setText(text);
            }
            return renderer;
        }
    }

    public void showCurrentSelectedRow() {
        table.scrollRectToVisible(table.getCellRect(currentSelectedRow, 0, true));
    }

    @Override
    public void saveComment(String comment) {
        if (table.getSelectedRowCount() == 1) {
            Integer id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), ID_KEY);
            Activity selectedActivity = ChartList.getList().getById(id);
            if (selectedActivity != null) {
                selectedActivity.setNotes(comment);
                selectedActivity.databaseUpdateComment();
            }
        }
    }
}
