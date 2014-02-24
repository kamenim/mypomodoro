package org.mypomodoro.gui.activities;

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
}
