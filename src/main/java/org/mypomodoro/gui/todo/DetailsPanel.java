package org.mypomodoro.gui.todo;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.buttons.CompleteToDoButton;
import org.mypomodoro.buttons.MoveToDoButton;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.activities.ActivityInformationPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays information on the selected Pomodoro
 *
 */
public class DetailsPanel extends ActivityInformationPanel implements ActivityInformation {

    private static final long serialVersionUID = 20110814L;
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final GridBagConstraints gbc = new GridBagConstraints();

    public DetailsPanel(ToDoPanel todoPanel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addMoveButton(todoPanel);
        addInformationPanel();
        addCompleteButton(todoPanel);
    }

    private void addMoveButton(ToDoPanel todoPanel) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        MoveToDoButton moveButton = new MoveToDoButton("<<<", todoPanel);
        moveButton.setFont(new Font(this.getFont().getName(), Font.BOLD, this.getFont().getSize() + 4));
        add(moveButton, gbc);
    }

    private void addInformationPanel() {
        JPanel infoPanel = new JPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 2;

        GridBagConstraints igbc = new GridBagConstraints();
        infoPanel.setLayout(new GridBagLayout());
        addToDoIconPanel(infoPanel, igbc);
        addInformationArea(infoPanel, igbc);
        add(infoPanel, gbc);
    }

    private void addToDoIconPanel(JPanel infoPanel, GridBagConstraints igbc) {
        igbc.gridx = 0;
        igbc.gridy = 0;
        igbc.fill = GridBagConstraints.BOTH;
        igbc.weightx = 1.0;
        igbc.weighty = 0.1;
        igbc.gridheight = 1;
        igbc.insets = new Insets(0, 3, 0, 0); // margin left
        infoPanel.add(iconLabel, igbc);
        igbc.insets = new Insets(0, 0, 0, 0); // no margin anymore        
    }

    private void addInformationArea(JPanel infoPanel, GridBagConstraints igbc) {
        // add the information area
        igbc.gridx = 0;
        igbc.gridy = 1;
        igbc.fill = GridBagConstraints.BOTH;
        igbc.weightx = 1.0;
        igbc.weighty = 1.0;
        igbc.gridheight = GridBagConstraints.REMAINDER;
        informationArea.setEditable(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        infoPanel.add(new JScrollPane(informationArea), igbc);
    }

    private void addCompleteButton(ToDoPanel todoPanel) {
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        add(new CompleteToDoButton(Labels.getString("ToDoListPanel.Complete ToDo"), Labels.getString("ToDoListPanel.Are you sure to complete those ToDo?"), todoPanel), gbc);
    }

    @Override
    public void selectInfo(Activity activity) {
        super.selectInfo(activity);
        if (ControlPanel.preferences.getAgileMode()) {
            textMap.remove("storypoints");
            textMap.remove("iteration");
        }
        textMap.remove("date_completed");
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }
}
