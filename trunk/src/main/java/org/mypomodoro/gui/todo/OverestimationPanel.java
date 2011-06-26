package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;

/**
 * Panel to overestimate initial estimation
 * 
 * @author Phil Karoo
 */
public class OverestimationPanel extends JPanel implements ActivityInformation {

    protected final OverestimationInputForm overestimationInputFormPanel = new OverestimationInputForm();
    private final GridBagConstraints gbc = new GridBagConstraints();

    public OverestimationPanel(ToDoListPanel panel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addOverestimationInputFormPanel();
        addSaveButton(panel);
    }

    private void addSaveButton(final ToDoListPanel panel) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new JButton("Save");
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveOverestimation(overestimationInputFormPanel.getOverestimationPomodoros().getSelectedIndex() + 1);
            }
        });
        add(changeButton, gbc);
    }

    protected void addOverestimationInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(overestimationInputFormPanel, gbc);
    }

    @Override
    public void showInfo(Activity activity) {
    }

    @Override
    public void clearInfo() {
    }
}