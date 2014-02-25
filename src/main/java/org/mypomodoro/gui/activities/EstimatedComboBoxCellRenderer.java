package org.mypomodoro.gui.activities;

import java.awt.Component;
import javax.swing.JTable;

/**
 *
 *
 */
class EstimatedComboBoxCellRenderer extends ComboBoxCellRenderer {

    public <E> EstimatedComboBoxCellRenderer(E[] data, boolean editable) {
        super(data, editable);

        // Custom display hovered item value
        comboBox.setRenderer(new ComboBoxEstimatedLengthRenderer());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // overestimated
        Integer overestimatedpoms = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), column + 1);
        label.setText(overestimatedpoms > 0 ? " + " + overestimatedpoms : "");
        return this;
    }
}
