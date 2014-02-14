package org.mypomodoro.gui.activities;

import javax.swing.JComboBox;
import javax.swing.JPanel;

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
    public <E> ActivitiesComboBoxPanel(E[] data) {
        comboBox = new JComboBox<E>(data);
        setOpaque(true);
        comboBox.setEditable(true);
        add(comboBox);
    }
}
