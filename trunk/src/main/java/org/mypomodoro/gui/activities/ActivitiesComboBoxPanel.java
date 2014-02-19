package org.mypomodoro.gui.activities;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import static org.mypomodoro.util.TimeConverter.getLength;

/**
 *
 *
 */
class ActivitiesComboBoxPanel extends JPanel {

    protected JComboBox comboBox;

    // Generic constructor
    public <E> ActivitiesComboBoxPanel(E[] data, boolean editable) {
        comboBox = new JComboBox<E>(data);
        setOpaque(true);
        comboBox.setEditable(editable);
        add(comboBox);
    }

    class ComboBoxFloatRenderer extends DefaultListCellRenderer {

        public ComboBoxFloatRenderer() {
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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

        public ComboBoxEstimatedLengthRenderer() {
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String length = getLength(Integer.parseInt(value.toString()));
            setToolTipText(length);
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
