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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.mypomodoro.buttons.DefaultButton;
import static org.mypomodoro.gui.burndownchart.ChartTabbedPanel.CHOOSEINPUTFORM;
import static org.mypomodoro.gui.burndownchart.ChartTabbedPanel.CONFIGUREINPUTFORM;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

/**
 * Panel to generate burndown charts
 *
 */
public class ConfigurePanel extends JPanel {

    private static final Dimension PANE_DIMENSION = new Dimension(700, 200);
    private static final Dimension CREATEBUTTON_DIMENSION = new Dimension(100, 250);

    private final JTabbedPane tabbedPane;
    private final CheckPanel checkPanel;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public ConfigurePanel(JTabbedPane tabbedPane, CheckPanel checkPanel) {
        this.tabbedPane = tabbedPane;
        this.checkPanel = checkPanel;

        setLayout(new GridBagLayout());
        //setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        CONFIGUREINPUTFORM = new ConfigureInputForm(); // re-create object

        addConfigureInputForm();
        addCheckButton();
    }

    public void refresh() {
        //CONFIGUREINPUTFORM.refresh();
    }

    private void addConfigureInputForm() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        JScrollPane configureScrollPane = new JScrollPane(CONFIGUREINPUTFORM);
        configureScrollPane.setMinimumSize(PANE_DIMENSION);
        configureScrollPane.setPreferredSize(PANE_DIMENSION);
        add(configureScrollPane, gbc);
    }

    private void addCheckButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.weightx = 0.1;
        JButton checkButton = new DefaultButton(
                Labels.getString("BurndownChartPanel.Check"));
        checkButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!WaitCursor.isStarted()) {
                    tabbedPane.setEnabledAt(2, true);
                    tabbedPane.setSelectedIndex(2);
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (CONFIGUREINPUTFORM.getDatesCheckBox().isSelected()) {
                                ArrayList<Date> datesToBeIncluded = DateUtil.getDatesWithExclusions(CONFIGUREINPUTFORM.getStartDate(),
                                        CONFIGUREINPUTFORM.getEndDate(),
                                        CONFIGUREINPUTFORM.getExcludeSaturdays().isSelected(),
                                        CONFIGUREINPUTFORM.getExcludeSundays().isSelected(),
                                        CONFIGUREINPUTFORM.getExcludedDates());
                                if (CONFIGUREINPUTFORM.getReleaseOnly().isSelected()) { // Tasks and subtasks                                    
                                    ChartList.getList().refreshDateRange(CONFIGUREINPUTFORM.getStartDate(), CONFIGUREINPUTFORM.getEndDate(), datesToBeIncluded, true, CHOOSEINPUTFORM.getDataSubtasksCheckBox().isSelected());
                                } else if (CONFIGUREINPUTFORM.getReleaseAndIteration().isSelected()) { // Tasks and subtasks
                                    ChartList.getList().refreshDateRange(CONFIGUREINPUTFORM.getStartDate(), CONFIGUREINPUTFORM.getEndDate(), datesToBeIncluded, false, CHOOSEINPUTFORM.getDataSubtasksCheckBox().isSelected());
                                } else if (CONFIGUREINPUTFORM.getIterationOnly().isSelected()) { // Tasks only
                                    ChartList.getList().refreshDateRangeAndIteration(CONFIGUREINPUTFORM.getStartDate(), CONFIGUREINPUTFORM.getEndDate(), datesToBeIncluded, CONFIGUREINPUTFORM.getIteration());
                                }
                            } else if (CONFIGUREINPUTFORM.getIterationsCheckBox().isSelected()) { // Tasks only
                                ChartList.getList().refreshIterationRange(CONFIGUREINPUTFORM.getStartIteration(), CONFIGUREINPUTFORM.getEndIteration());
                            }
                        }
                    });
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            checkPanel.refresh();
                        }
                    });
                }
            }
        });
        checkButton.setMinimumSize(CREATEBUTTON_DIMENSION);
        checkButton.setMaximumSize(CREATEBUTTON_DIMENSION);
        checkButton.setPreferredSize(CREATEBUTTON_DIMENSION);
        add(checkButton, gbc);
    }
}
