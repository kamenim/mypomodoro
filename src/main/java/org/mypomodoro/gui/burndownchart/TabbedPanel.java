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
package org.mypomodoro.gui.burndownchart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.mypomodoro.util.Labels;

/**
 * Burndown tabbed Panel
 *
 */
public class TabbedPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;

    private final Chart chart = new Chart();

    public TabbedPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTabbedPane chartTabbedPane = new JTabbedPane();
        CreateInputPanel createInputPanel = new CreateInputPanel(chartTabbedPane, chart);
        chartTabbedPane.add(Labels.getString("BurndownChartPanel.Create"), createInputPanel);
        JPanel j = new JPanel();
        chartTabbedPane.add(Labels.getString("BurndownChartPanel.Check"), j);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartTabbedPane.add(Labels.getString("BurndownChartPanel.Chart"), new JScrollPane(chartPanel));
        add(chartTabbedPane, gbc);
    }
}
