package org.mypomodoro.gui.activities;

/**
 *
 *
 */
class StoryPointsComboBoxCellRenderer extends ComboBoxCellRenderer {

    public <E> StoryPointsComboBoxCellRenderer(E[] data, boolean editable) {
        super(data, editable);

        // Custom display selected item value        
        comboBox.setRenderer(new ComboBoxFloatRenderer());
    }
}
