package org.mypomodoro.gui.create.list;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;

/**
 * Template list combo box
 * 
 * @author Phil Karoo
 */
public class AbstractComboBox extends JComboBox {

    private static final long serialVersionUID = 20110814L;
    final Object selectedItem = getSelectedItem();
    final ComboboxToolTipRenderer tooltipRenderer = new ComboboxToolTipRenderer();

    protected AbstractComboBox() {
        setRenderer(tooltipRenderer);
    }

    protected class ComboboxToolTipRenderer extends DefaultListCellRenderer {

        ArrayList tooltips;

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            JComponent comp = (JComponent) super.getListCellRendererComponent(list,
                    value, index, isSelected, cellHasFocus);

            if (-1 < index && null != value && null != tooltips) {
                list.setToolTipText((String) tooltips.get(index));
            }
            return comp;
        }

        public void setTooltips(ArrayList tooltips) {
            this.tooltips = tooltips;
        }
    }
}