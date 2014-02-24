package org.mypomodoro.gui.reports.burndownchart;

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
