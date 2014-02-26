package org.mypomodoro.gui.burndownchart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import javax.swing.JScrollPane;
import org.mypomodoro.buttons.AbstractPomodoroButton;
import org.mypomodoro.util.Labels;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;

/**
 * Panel to generate burndown charts
 *
 */
public class BurndownChartInputPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private BurndownChartInputForm burndownChartInputForm;
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JTabbedPane burdownChartPane;

    public BurndownChartInputPanel(JTabbedPane burdownChartPane) {
        this.burdownChartPane = burdownChartPane;

        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addBurndownChartInputForm();
        addCreateButton();
    }

    private void addBurndownChartInputForm() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        burndownChartInputForm = new BurndownChartInputForm();
        add(new JScrollPane(burndownChartInputForm), gbc);
    }

    private void addCreateButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        // gbc.fill = GridBagConstraints.NONE;
        JButton createButton = new AbstractPomodoroButton(
                Labels.getString("ReportListPanel.Chart.Create"));
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Create chart

                // Switch to burndown chart pane
                burdownChartPane.setSelectedIndex(1);

                // The image is created only if the name of the image is set
                if (burndownChartInputForm.getImageName().length() != 0) {
                    //burndownChart.saveImageChart();
                }
            }
        });
        add(createButton, gbc);
    }
}
