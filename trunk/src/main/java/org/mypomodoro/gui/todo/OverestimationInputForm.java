package org.mypomodoro.gui.todo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;
import static org.mypomodoro.util.TimeConverter.calculateEffectiveHours;
import static org.mypomodoro.util.TimeConverter.calculatePlainHours;
import static org.mypomodoro.util.TimeConverter.convertToTime;

/**
 * Overestimation input form
 *
 */
public class OverestimationInputForm extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension PANEL_DIMENSION = new Dimension(400, 50);
    private static final Dimension LABEL_DIMENSION = new Dimension(170, 25);
    private static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 25);
    protected JComboBox overestimatedPomodoros = new JComboBox();
    protected final GridBagConstraints c = new GridBagConstraints();
    protected final FormLabel overestimatedLengthLabel = new FormLabel("");

    public OverestimationInputForm() {
        setBorder(new TitledBorder(new EtchedBorder(), ""));
        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        addOverestimatedPoms();
        addOverestimatedLength();
    }

    protected void addOverestimatedPoms() {
        displayLength(1); // default estimate = 1 pomodoro
        // Overestimated Poms
        c.gridx = 0;
        c.gridy = 0;
        FormLabel label = new FormLabel(
                Labels.getString("ToDoListPanel.Overestimated pomodoros")
                + "*: ");
        label.setMinimumSize(LABEL_DIMENSION);
        label.setPreferredSize(LABEL_DIMENSION);
        add(label, c);
        c.gridx = 1;
        c.gridy = 0;
        // In Agile mode you should be able to overestimate your task by half day or even one day
        String items[] = ControlPanel.preferences.getAgileMode() ? new String[ControlPanel.preferences.getMaxNbPomPerDay()] : new String[5];
        for (int i = 0; i < items.length; i++) {
            items[i] = "+ " + (i + 1);
        }
        overestimatedPomodoros = new JComboBox(items);
        overestimatedPomodoros.setMinimumSize(COMBO_BOX_DIMENSION);
        overestimatedPomodoros.setPreferredSize(COMBO_BOX_DIMENSION);
        overestimatedPomodoros.setBackground(ColorUtil.WHITE);
        overestimatedPomodoros.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                int overestimated = overestimatedPomodoros.getSelectedIndex() + 1;
                displayLength(overestimated);
            }
        });
        add(overestimatedPomodoros, c);
    }

    protected void addOverestimatedLength() {
        c.gridx = 0;
        c.gridy = 1;
        //c.weighty = 0.5;
        add(new FormLabel(Labels.getString("ToDoListPanel.Overestimated length") + ": "), c);
        c.gridx = 1;
        c.gridy = 1;
        //c.weighty = 0.5;
        add(overestimatedLengthLabel, c);
    }

    public JComboBox getOverestimationPomodoros() {
        return overestimatedPomodoros;
    }

    public void reset() {
        overestimatedPomodoros.setSelectedIndex(0);
    }

    private void displayLength(int overestimatedPomodoros) {
        String effectiveHours = convertToTime(calculateEffectiveHours(overestimatedPomodoros));
        String plainHours = convertToTime(calculatePlainHours(overestimatedPomodoros));
        overestimatedLengthLabel.setText("+ " + effectiveHours + " (" + Labels.getString("Common.Effective hours") + ") / + " + plainHours + " (" + Labels.getString("Common.Plain hours") + ")");
    }
}
