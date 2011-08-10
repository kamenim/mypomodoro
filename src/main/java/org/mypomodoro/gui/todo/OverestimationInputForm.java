package org.mypomodoro.gui.todo;

import java.awt.Color;
import org.mypomodoro.gui.create.*;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.util.Labels;

/**
 *
 * @author Phil Karoo
 */
public class OverestimationInputForm extends JPanel {

    private static final Dimension PANEL_DIMENSION = new Dimension(400, 50);
    private static final Dimension LABEL_DIMENSION = new Dimension(170, 25);
    private static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 25);
    protected JComboBox overestimatedPomodoros = new JComboBox();

    public OverestimationInputForm() {
        setBorder(new TitledBorder(new EtchedBorder(), ""));
        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        // Overestimated Poms
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        FormLabel label = new FormLabel(Labels.getString("ToDoListPanel.Overestimated pomodoros") + "*: ");
        label.setMinimumSize(LABEL_DIMENSION);
        label.setPreferredSize(LABEL_DIMENSION);
        add(label, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.5;
        String items[] = new String[5];
        for (int i = 0; i < items.length; i++) {
            items[i] = "+ " + ( i + 1 );
        }
        overestimatedPomodoros = new JComboBox(items);
        overestimatedPomodoros.setMinimumSize(COMBO_BOX_DIMENSION);
        overestimatedPomodoros.setPreferredSize(COMBO_BOX_DIMENSION);
        overestimatedPomodoros.setBackground(Color.white);
        add(overestimatedPomodoros, c);
    }

    public JComboBox getOverestimationPomodoros() {
        return overestimatedPomodoros;
    }
}