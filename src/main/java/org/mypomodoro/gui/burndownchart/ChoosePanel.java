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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.util.Labels;

/**
 * Panel to generate burndown charts
 *
 */
public class ChoosePanel extends JPanel {

    private final JTabbedPane tabbedPane;
    private final ChooseInputForm chooseInputForm;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public ChoosePanel(JTabbedPane tabbedPane, ChooseInputForm chooseInputForm) {
        this.tabbedPane = tabbedPane;
        this.chooseInputForm = chooseInputForm;

        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addCreateInputForm();
        addConfigureButton();
    }

    private void addCreateInputForm() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(new JScrollPane(chooseInputForm), gbc);
    }

    private void addConfigureButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        JButton createButton = new AbstractButton(
                Labels.getString("BurndownChartPanel.Configure"));
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setEnabledAt(1, true);
                tabbedPane.setSelectedIndex(1);
            }
        });
        add(createButton, gbc);
    }
}
