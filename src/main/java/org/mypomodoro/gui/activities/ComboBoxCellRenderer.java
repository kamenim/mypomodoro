package org.mypomodoro.gui.activities;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 *
 */
class ComboBoxCellRenderer extends ActivitiesComboBoxPanel implements TableCellRenderer {

    public <E> ComboBoxCellRenderer(E[] data) {
        super(data);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        if (value != null) {
            comboBox.setSelectedItem(value);
        }
        return this;
    }
}
