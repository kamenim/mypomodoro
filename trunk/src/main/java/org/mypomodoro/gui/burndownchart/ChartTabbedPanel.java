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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mypomodoro.util.Labels;

/**
 * Burndown tabbed Panel
 *
 */
public class ChartTabbedPanel extends JPanel {

    private final ChooseInputForm chooseInputForm = new ChooseInputForm();
    private final ConfigureInputForm configureInputForm = new ConfigureInputForm();
    private final CreateChart chart = new CreateChart(chooseInputForm, configureInputForm);
    private final JTabbedPane chartTabbedPane = new JTabbedPane();
    private final CheckPanel checkPanel = new CheckPanel(chartTabbedPane, chart);

    public ChartTabbedPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        ConfigurePanel configureInputPanel = new ConfigurePanel(chartTabbedPane, configureInputForm, checkPanel);
        ChoosePanel chooseInputPanel = new ChoosePanel(chartTabbedPane, chooseInputForm);
        chartTabbedPane.addTab(Labels.getString("BurndownChartPanel.Choose"), chooseInputPanel);
        chartTabbedPane.addTab(Labels.getString("BurndownChartPanel.Configure"), configureInputPanel);
        chartTabbedPane.addTab(Labels.getString("BurndownChartPanel.Check"), checkPanel);
        CreateChartPanel chartPanel = new CreateChartPanel(chart);
        chartTabbedPane.addTab(Labels.getString("BurndownChartPanel.Create"), new JScrollPane(chartPanel));
        chartTabbedPane.setEnabledAt(1, false);
        chartTabbedPane.setEnabledAt(2, false);
        chartTabbedPane.setEnabledAt(3, false);
        chartTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedTabIndex = chartTabbedPane.getSelectedIndex();
                while (selectedTabIndex < chartTabbedPane.getTabCount() - 1) {
                    chartTabbedPane.setEnabledAt(selectedTabIndex + 1, false);
                    selectedTabIndex++;
                }
            }
        });
        add(chartTabbedPane, gbc);
    }

    public void showCurrentSelectedRow() {
        checkPanel.getCurrentTable().scrollToSelectedRow();
    }

    public CheckPanel getCheckPanel() {
        return checkPanel;
    }
}
