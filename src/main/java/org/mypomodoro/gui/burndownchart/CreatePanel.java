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
import org.mypomodoro.model.ChartList;

/**
 * Panel to generate burndown charts
 *
 */
public class CreatePanel extends JPanel {

    private static final long serialVersionUID = 20110814L;

    private final JTabbedPane tabbedPane;
    private final CreateInputForm createInputForm;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public CreatePanel(JTabbedPane tabbedPane, CreateInputForm createInputForm) {
        this.tabbedPane = tabbedPane;
        this.createInputForm = createInputForm;

        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addCreateInputForm();
        addCreateButton();
    }

    private void addCreateInputForm() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(new JScrollPane(createInputForm), gbc);
    }

    private void addCreateButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        JButton createButton = new AbstractPomodoroButton(
                Labels.getString("BurndownChartPanel.Check"));
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (createInputForm.getBurndownChartCheckBox().isSelected()
                        || createInputForm.getBurnupChartCheckBox().isSelected()) {
                    ChartList.getList().refresh(createInputForm.getEndDate());
                    tabbedPane.setEnabledAt(1, true);
                    tabbedPane.setSelectedIndex(1);
                }
            }
        });
        add(createButton, gbc);
    }
}
