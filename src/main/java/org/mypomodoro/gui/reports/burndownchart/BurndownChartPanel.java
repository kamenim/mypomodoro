package org.mypomodoro.gui.reports.burndownchart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.gui.reports.ReportListPanel;

/**
 * Panel that displays a burndown chart
 *
 * @author Phil Karoo
 */
public class BurndownChartPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private BurndownChart burndownChart;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public BurndownChartPanel(ReportListPanel panel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addBurndownChart();
    }

    private void addBurndownChart() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        burndownChart = new BurndownChart();
        add(burndownChart, gbc);
    }
}
