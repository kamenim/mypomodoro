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
package org.mypomodoro.gui.todo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;

import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;
import static org.mypomodoro.util.TimeConverter.calculateEffectiveMinutes;
import static org.mypomodoro.util.TimeConverter.calculatePlainMinutes;

/**
 * Overestimation input form
 *
 */
public class OverestimationInputForm extends JPanel {

    private static final Dimension PANEL_DIMENSION = new Dimension(400, 50);
    private static final Dimension LABEL_DIMENSION = new Dimension(170, 25);
    private static final Dimension COMBO_BOX_DIMENSION = new Dimension(60, 25);
    protected JComboBox overestimatedPomodoros = new JComboBox();
    protected final GridBagConstraints c = new GridBagConstraints();
    protected final JLabel overestimatedLengthLabel = new JLabel("", JLabel.LEFT);

    public OverestimationInputForm() {
        setBorder(new TitledBorder(new EtchedBorder(), ""));
        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        addOverestimatedPoms();
    }

    protected void addOverestimatedPoms() {
        displayLength(1); // default estimate = 1 pomodoro
        // Overestimated Poms
        c.gridx = 0;
        c.gridy = 0;
        FormLabel label = new FormLabel(
                Labels.getString("ToDoListPanel.Overestimated pomodoros")
                + "*: ");
        label.setMinimumSize(LABEL_DIMENSION);
        label.setPreferredSize(LABEL_DIMENSION);
        add(label, c);
        c.gridx = 1;
        c.gridy = 0;
        // In Agile mode you should be able to overestimate your task by half day or even one day
        String items[] = PreferencesPanel.preferences.getAgileMode() ? new String[10] : new String[5];
        for (int i = 0; i < items.length; i++) {
            items[i] = "+ " + (i + 1);
        }
        overestimatedPomodoros = new JComboBox(items);
        overestimatedPomodoros.setBackground(ColorUtil.WHITE);
        overestimatedPomodoros.setMinimumSize(COMBO_BOX_DIMENSION);
        overestimatedPomodoros.setMaximumSize(COMBO_BOX_DIMENSION);
        overestimatedPomodoros.setPreferredSize(COMBO_BOX_DIMENSION);
        overestimatedPomodoros.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                int overestimated = overestimatedPomodoros.getSelectedIndex() + 1;
                displayLength(overestimated);
            }
        });
        JPanel overestimatedPanel = new JPanel();
        overestimatedPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(3, 3, 3, 3); // white space between components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        overestimatedPomodoros.setRenderer(new AbstractComboBoxRenderer());
        overestimatedPanel.add(overestimatedPomodoros, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        overestimatedPanel.add(overestimatedLengthLabel, gbc);
        add(overestimatedPanel, c);
        add(overestimatedPanel, c);
    }

    public JComboBox getOverestimationPomodoros() {
        return overestimatedPomodoros;
    }

    public void reset() {
        overestimatedPomodoros.setSelectedIndex(0);
    }

    private void displayLength(int overestimatedPomodoros) {
        if (PreferencesPanel.preferences.getPlainHours()) {
            String plainHours = TimeConverter.convertMinutesToString(calculatePlainMinutes(overestimatedPomodoros));
            overestimatedLengthLabel.setText(plainHours + " (" + Labels.getString("Common.Plain hours") + ")");
        } else {
            String effectiveHours = TimeConverter.convertMinutesToString(calculateEffectiveMinutes(overestimatedPomodoros));
            overestimatedLengthLabel.setText(effectiveHours + " (" + Labels.getString("Common.Effective hours") + ")");
        }
    }
}
