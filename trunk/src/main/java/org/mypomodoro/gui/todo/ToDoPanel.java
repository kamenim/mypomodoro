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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;
import org.mypomodoro.buttons.DiscontinuousButton;
import org.mypomodoro.buttons.MuteButton;
import org.mypomodoro.buttons.PinButton;
import org.mypomodoro.buttons.ResizeButton;
import org.mypomodoro.gui.AbstractTable;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.TabbedPane;
import org.mypomodoro.gui.activities.CommentPanel;
import org.mypomodoro.gui.export.ExportPanel;
import org.mypomodoro.gui.export.ImportPanel;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;
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

    private static final Dimension LIST_TIMER_PANE_DIMENSION = new Dimension(800, 200);
    private static final Dimension PANE_DIMENSION = new Dimension(500, 200);
    private static final Dimension TABPANE_DIMENSION = new Dimension(800, 50);
    // List and Timer Pane : listPane + Timer
    private final JPanel listPaneAndTimer = new JPanel();
    private final GridBagConstraints gbcListPaneAndTimer = new GridBagConstraints();
    // List pane: title + table + sub-title + sub-table
    private final JPanel listPane = new JPanel();
    // Split pane: list pane + tabbed pane
    private final JSplitPane splitPane;
    // Title panes: title and sub-title    
    private final ToDoTitlePanel tableTitlePanel;
    private final ToDoSubTableTitlePanel subTableTitlePanel;
    // Table panes: table and sub-table
    private final JScrollPane tableScrollPane;
    private final JScrollPane subTableScrollPane;
    // Tabbed pane: details + ...
    private final TabbedPane tabbedPane;
    // Tab panes: details,...
    private final DetailsPanel detailsPanel = new DetailsPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final EditPanel editPanel = new EditPanel(detailsPanel);
    private final OverestimationPanel overestimationPanel = new OverestimationPanel(this, detailsPanel);
    private final UnplannedPanel unplannedPanel = new UnplannedPanel(this);
    private final MergingPanel mergingPanel = new MergingPanel(this);
    // Pomodoro
    private final JLabel pomodoroTime = new JLabel();
    private final Pomodoro pomodoro = new Pomodoro(this, detailsPanel, unplannedPanel, pomodoroTime);
    final ImageIcon pomodoroIcon = new ImageIcon(Main.class.getResource("/images/mAPIconTimer.png"));
    // Tables
    private ToDoTable currentTable;
    private ToDoTableModel tableModel;
    private final ToDoTable table;
    private final ToDoSubTableModel subTableModel;
    private final ToDoSubTable subTable;
    // Discontinuous and Resize buttons
    private final DiscontinuousButton discontinuousButton = new DiscontinuousButton(pomodoro);
    public static final ResizeButton RESIZEBUTTON = new ResizeButton();

    public ToDoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Init List and Timer Pane
        listPaneAndTimer.setMinimumSize(LIST_TIMER_PANE_DIMENSION);
        listPaneAndTimer.setPreferredSize(LIST_TIMER_PANE_DIMENSION);
        listPaneAndTimer.setLayout(new GridBagLayout());
        gbcListPaneAndTimer.fill = GridBagConstraints.BOTH;

        // Init List pane
        listPane.setMinimumSize(PANE_DIMENSION);
        listPane.setPreferredSize(PANE_DIMENSION);
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));

        // Init Tabbed pane        
        tabbedPane = new TabbedPane(this);
        tabbedPane.setMinimumSize(TABPANE_DIMENSION);
        tabbedPane.setPreferredSize(TABPANE_DIMENSION);
        initTabbedPane();

        // Init Split pane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPaneAndTimer, tabbedPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setDividerSize(0); // remove divider by hiding it

        // Init table and sub table (data model and rendering)
        subTableModel = new ToDoSubTableModel();
        tableModel = new ToDoTableModel();
        subTable = new ToDoSubTable(subTableModel, this); // instanciate this before table
        table = new ToDoTable(tableModel, this);
        currentTable = table;

        // Init scroll panes
        subTableScrollPane = new JScrollPane(subTable);
        tableScrollPane = new JScrollPane(table);

        // Init title and sub title
        tableTitlePanel = new ToDoTitlePanel(this, table);
        subTableTitlePanel = new ToDoSubTableTitlePanel(this, subTable);

        // select first activity of the table so the selection listener gets fired only now that both tables have been instanciated
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }

        // Add panes of List pane
        addTableTitlePanel();
        addTable();
        addSubTableTitlePanel();

        // Add Split pane
        add(splitPane);

        // Add list pane to ListAndTimerPane
        addListPane();
        // Add timer to ListAndTimerPane
        addTimerPanel();
    }

    ////////////////////////////////////////////////
    // TABBED PANE
    ////////////////////////////////////////////////
    private void initTabbedPane() {
        tabbedPane.setDetailsTabIndex(0);
        tabbedPane.setCommentTabIndex(1);
        tabbedPane.setEditTabIndex(2);
        tabbedPane.setOverestimateTabIndex(3);
        tabbedPane.setUnplannedTabIndex(4);
        tabbedPane.setMergeTabIndex(5);
        tabbedPane.setImportTabIndex(6);
        tabbedPane.setExportTabIndex(7);
        tabbedPane.add(Labels.getString("Common.Details"), detailsPanel);
        tabbedPane.add(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"), commentPanel);
        tabbedPane.add(Labels.getString("Common.Edit"), editPanel);
        tabbedPane.add(Labels.getString("ToDoListPanel.Overestimate"), overestimationPanel);
        tabbedPane.add(Labels.getString("Common.Unplanned") + " / " + Labels.getString("ToDoListPanel.Interruption"), unplannedPanel);
        tabbedPane.add(Labels.getString("ToDoListPanel.Merge"), mergingPanel);
        ImportPanel importPanel = new ImportPanel(this);
        tabbedPane.add(Labels.getString("ReportListPanel.Import"), importPanel);
        ExportPanel exportPanel = new ExportPanel(this);
        tabbedPane.add(Labels.getString("ReportListPanel.Export"), exportPanel);
    }

    ////////////////////////////////////////////////
    // TITLE
    ////////////////////////////////////////////////
    @Override
    public void addTableTitlePanel() {
        table.setTitle(); // init title
        listPane.add(tableTitlePanel);
    }

    ////////////////////////////////////////////////
    // TABLE
    ////////////////////////////////////////////////
    @Override
    public void addTable() {
        listPane.add(tableScrollPane);
    }

    ////////////////////////////////////////////////
    // SUB TITLE
    ////////////////////////////////////////////////
    @Override
    public void addSubTableTitlePanel() {
        subTable.setTitle(); // init title
        listPane.add(subTableTitlePanel);
    }

    public void addListPane() {
        gbcListPaneAndTimer.gridx = 0;
        gbcListPaneAndTimer.gridy = 0;
        gbcListPaneAndTimer.weighty = 1.0;
        gbcListPaneAndTimer.weightx = 1.0;
        listPaneAndTimer.add(listPane, gbcListPaneAndTimer);
    }

    private void addTimerPanel() {
        gbcListPaneAndTimer.gridx = 1;
        gbcListPaneAndTimer.gridy = 0;
        gbcListPaneAndTimer.weighty = 1.0;
        gbcListPaneAndTimer.weightx = 1.0;
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
        listPaneAndTimer.add(wrap, gbcListPaneAndTimer);
        pomodoro.setTimerPanel(timerPanel);
    }

    ////////////////////////////////////////////////
    // REFRESH
    ////////////////////////////////////////////////
    @Override
    public void refresh() {
        refresh(false);
    }

    @Override
    public void refresh(boolean fromDatabase) {
        if (!WaitCursor.isStarted()) {
            // Start wait cursor
            WaitCursor.startWaitCursor();
            try {
                if (fromDatabase) {
                    getList().refresh();
                }
                tableModel = new ToDoTableModel();                
                table.setModel(tableModel);
                table.setTableHeader();               
                table.setColumnModel();
                table.initTabs();
                if (tableModel.getRowCount() > 0) {
                    table.setRowSelectionInterval(0, 0);
                } else {
                    emptySubTable();
                }
                table.setTitle();
                subTable.setTitle();
            } catch (Exception ex) {
                logger.error("", ex);
            } finally {
                // Stop wait cursor
                WaitCursor.stopWaitCursor();
            }
        }
    }

    public ToDoList getList() {
        return ToDoList.getList();
    }

    @Override
    public void emptySubTable() {
        subTableModel.setRowCount(0);
        subTable.setColumnModel();
        subTable.setTitle();
    }

    @Override
    public ToDoTable getMainTable() {
        return table;
    }

    @Override
    public ToDoTable getCurrentTable() {
        return currentTable;
    }

    @Override
    public void setCurrentTable(AbstractTable table) {
        currentTable = (ToDoTable) table;
    }

    public ToDoSubTable getSubTable() {
        return subTable;
    }

    /////////////////// NEW
    public DetailsPanel getDetailsPanel() {
        return detailsPanel;
    }

    public CommentPanel getCommentPanel() {
        return commentPanel;
    }

    public EditPanel getEditPanel() {
        return editPanel;
    }

    @Override
    public TabbedPane getTabbedPane() {
        return tabbedPane;
    }

    @Override
    public JPanel getListPane() {
        return listPane;
    }

    @Override
    public JSplitPane getSplitPane() {
        return splitPane;
    }

    @Override
    public ToDoTitlePanel getTableTitlePanel() {
        return tableTitlePanel;
    }

    public ToDoSubTableTitlePanel getSubTableTitlePanel() {
        return subTableTitlePanel;
    }

    @Override
    public JScrollPane getTableScrollPane() {
        return tableScrollPane;
    }

    @Override
    public JScrollPane getSubTableScrollPane() {
        return subTableScrollPane;
    }

    @Override
    public void populateSubTable(int parentId) {        
        subTableModel.update(parentId);
        subTable.setColumnModel();        
        subTable.setTitle();
        setCurrentTable(table);
    }

    ////////////////////////////
    //  Specific to ToDoPanel
    ///////////////////////////
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
        RESIZEBUTTON.setVisible(true); // this is a TransparentButton       
        RESIZEBUTTON.setMargin(new Insets(1, 1, 1, 1));
        RESIZEBUTTON.setFocusPainted(false); // removes borders around text
        toolBar.add(RESIZEBUTTON, wc);
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

    public Pomodoro getPomodoro() {
        return pomodoro;
    }    

    public void removeTabbedPane() {
        splitPane.remove(tabbedPane);
    }

    public void removeListPane() {
        listPaneAndTimer.remove(listPane);
    }
    
    public void addTabbedPane() {
        splitPane.setRightComponent(tabbedPane); // bottom
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

    public OverestimationPanel getOverestimationPanel() {
        return overestimationPanel;
    }
    
    public UnplannedPanel getUnplannedPanel() {
        return unplannedPanel;
    }    
}
