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
package org.mypomodoro.gui.activities;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import static org.mypomodoro.util.TimeConverter.getLength;

/**
 *
 *
 */
class ActivitiesComboBoxPanel extends JPanel {

    protected JComboBox comboBox;
    protected JLabel label = new JLabel();

    // Generic constructor
    public <E> ActivitiesComboBoxPanel(E[] data, boolean editable) {
        setLayout(new GridBagLayout());
        comboBox = new JComboBox();
        for (E d : data) { // jdk 7 : simply use comboBox = new JComboBox<E>(data);
            comboBox.addItem(d);
        }
        setOpaque(true);
        comboBox.setEditable(editable);
        if (data instanceof String[]) {
            add(comboBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 3, 0, 3), 0, 0));
        } else {
            add(comboBox);
        }
        add(label);
    }

    class ComboBoxFloatRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text;
            if (value.toString().equals("0.5")) {
                text = "1/2";
            } else {
                text = Math.round((Float) value) + "";
            }
            setText(text);
            return this;
        }
    }

    class ComboBoxEstimatedLengthRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String length = getLength(Integer.parseInt(value.toString()));
            setToolTipText(length);
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    class ComboBoxIterationRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text = value.toString();
            if (value.toString().equals("-1")) {
                text = " ";
            }
            setText(text);
            return this;
        }
    }
}
