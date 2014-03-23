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
public class CreateInputPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private CreateInputForm createInputForm;
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JTabbedPane burdownChartPane;
    private Chart chart;

    public CreateInputPanel(JTabbedPane burdownChartPane, Chart chart) {
        this.burdownChartPane = burdownChartPane;
        this.chart = chart;

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
        createInputForm = new CreateInputForm();
        add(new JScrollPane(createInputForm), gbc);
    }

    private void addCreateButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        // gbc.fill = GridBagConstraints.NONE;
        JButton createButton = new AbstractPomodoroButton(
                Labels.getString("BurndownChartPanel.Create"));
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // The image is created only if the name of the image is set
                if (createInputForm.getImageName().length() != 0) {                    
                    //chart = new Chart(createInputForm.getStartDate(), createInputForm.getEndDate());
                    chart.make(createInputForm.getStartDate(), createInputForm.getEndDate());
                    chart.saveImageChart(createInputForm.getImageName() + ".png");                    
                }
                // Switch to burndown chart pane
                burdownChartPane.setSelectedIndex(1);
            }
        });
        add(createButton, gbc);
    }
}
