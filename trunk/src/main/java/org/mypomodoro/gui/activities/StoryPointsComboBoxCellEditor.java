package org.mypomodoro.gui.activities;

/**
 *
 *
 */
class StoryPointsComboBoxCellEditor extends ComboBoxCellEditor {

    public <E> StoryPointsComboBoxCellEditor(E[] data, boolean editable) {
        super(data, editable);
        // Custom display items value
        ComboBoxFloatRenderer renderer = new ComboBoxFloatRenderer();
        comboBox.setRenderer(renderer);
    }
}
