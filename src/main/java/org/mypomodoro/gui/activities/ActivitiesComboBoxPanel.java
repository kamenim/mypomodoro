package org.mypomodoro.gui.activities;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 *
 */
class ActivitiesComboBoxPanel extends JPanel {

    protected JComboBox comboBox;

    /*protected JComboBox<String> comboBox = new JComboBox<String>(m) {
     @Override
     public Dimension getPreferredSize() {
     Dimension d = super.getPreferredSize();
     return new Dimension(40, d.height);
     return new Dimension(40, 20);
     }
     };*/
    // Generic constructor
    public <E> ActivitiesComboBoxPanel(E[] data, boolean editable) {
        comboBox = new JComboBox<E>(data);/*{
         @Override
         public Dimension getPreferredSize() {
         Dimension d = super.getPreferredSize();     
         return new Dimension(d.width, d.height);     
         }
         };*/

        setOpaque(true);
        comboBox.setEditable(editable);
        add(comboBox);
    }

    class ComboBoxFloatRenderer extends JLabel implements ListCellRenderer {

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
}
