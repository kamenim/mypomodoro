package org.mypomodoro.gui.activities;

import java.awt.Component;
import javax.swing.JTable;

/**
 *
 *
 */
class EstimatedComboBoxCellEditor extends ComboBoxCellEditor {

    public <E> EstimatedComboBoxCellEditor(E[] data, boolean editable) {
        super(data, editable);

        // Custom display hovered item value
        comboBox.setRenderer(new ComboBoxEstimatedLengthRenderer());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        // overestimated
        Integer overestimatedpoms = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), column + 1);
        label.setText(overestimatedpoms > 0 ? " + " + overestimatedpoms : "");
        return this;
    }
}
