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
import javax.swing.border.EtchedBorder;

/**
 * Panel that displays the charts
 *
 */
public class BurndownChartPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private BurndownChart burndownChart;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public BurndownChartPanel(Date dateStart, Date dateEnd) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addBurndownChart(dateStart, dateEnd);
    }

    private void addBurndownChart(Date dateStart, Date dateEnd) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        burndownChart = new BurndownChart(dateStart, dateEnd);
        add(burndownChart, gbc);
    }
}
