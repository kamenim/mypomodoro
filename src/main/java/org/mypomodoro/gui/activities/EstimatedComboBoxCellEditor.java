package org.mypomodoro.gui.activities;

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
}
