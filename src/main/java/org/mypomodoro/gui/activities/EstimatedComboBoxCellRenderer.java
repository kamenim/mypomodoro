package org.mypomodoro.gui.activities;

import java.awt.Component;
import javax.swing.JTable;
import org.mypomodoro.model.ActivityList;

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
        int id = (Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), ActivitiesPanel.ID_KEY);        
        int overestimatedpoms = ActivityList.getList().getById(id).getOverestimatedPoms();
        label.setText(overestimatedpoms > 0 ? " + " + overestimatedpoms : "");
        return this;
    }
}
