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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.mypomodoro.util.Labels;

/**
 * Burndown Panel
 *
 */
public class BurndownPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;

    public BurndownPanel() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTabbedPane burdownChartPane = new JTabbedPane();
        BurndownChartInputPanel burndownChartInputPanel = new BurndownChartInputPanel(burdownChartPane);
        burdownChartPane.add(Labels.getString("ReportListPanel.Chart.Create"), burndownChartInputPanel);
        BurndownChartPanel burndownChart = new BurndownChartPanel();
        burdownChartPane.add(Labels.getString("ReportListPanel.Chart.Chart"), new JScrollPane(burndownChart));
        add(burdownChartPane, gbc);
    }
}
