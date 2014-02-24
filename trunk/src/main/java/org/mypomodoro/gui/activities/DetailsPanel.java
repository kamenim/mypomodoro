package org.mypomodoro.gui.activities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.buttons.DeleteAllButton;
import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.gui.ActivityInformation;

/**
 * Panel that displays information on the current Pomodoro
 *
 */
public class DetailsPanel extends ActivityInformationPanel implements ActivityInformation {

    private static final long serialVersionUID = 20110814L;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public DetailsPanel(ActivitiesPanel activitiesPanel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addInformationArea();
        addDeleteButton(activitiesPanel);
        addDeleteAllButton(activitiesPanel);
    }

    private void addDeleteButton(ActivitiesPanel activitiesPanel) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 1;
        // gbc.fill = GridBagConstraints.NONE;
        add(new DeleteButton(activitiesPanel), gbc);
    }

    private void addDeleteAllButton(ActivitiesPanel activitiesPanel) {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.gridheight = 1;
        // gbc.fill = GridBagConstraints.NONE;
        add(new DeleteAllButton(activitiesPanel), gbc);
    }

    private void addInformationArea() {
        // add the information area
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 2;
        informationArea.setEditable(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        add(new JScrollPane(informationArea), gbc);
    }
}
