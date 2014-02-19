package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.buttons.AbstractPomodoroButton;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.activities.ActivityInformationPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays information on the selected Pomodoro
 *
 */
public class InformationPanel extends ActivityInformationPanel {

    private static final long serialVersionUID = 20110814L;
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final ToDoListPanel panel;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public InformationPanel(ToDoListPanel panel) {
        this.panel = panel;

        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addInformationPanel();
        addCompleteButton();
        addCompleteAllButton();
    }

    private void addInformationPanel() {
        JPanel infoPanel = new JPanel();
        gbc.gridx = 0;
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
        infoPanel.add(iconLabel, igbc);
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

    private void addCompleteButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 1;
        //gbc.fill = GridBagConstraints.NONE;
        JButton completeButton = new AbstractPomodoroButton(Labels.getString("ToDoListPanel.Complete"));
        completeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.completeTaskWithWarning();
            }
        });
        add(completeButton, gbc);
    }

    private void addCompleteAllButton() {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.gridheight = 1;
        //gbc.fill = GridBagConstraints.NONE;
        JButton completeAllButton = new AbstractPomodoroButton(Labels.getString("ToDoListPanel.Complete all"));
        completeAllButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!panel.getToDoList().isEmpty()) {
                    panel.completeAllTasksWithWarning();
                }
            }
        });
        add(completeAllButton, gbc);
    }

    @Override
    public void selectInfo(Activity activity) {
        super.selectInfo(activity);
        textMap.remove("date");
        textMap.remove("title");
        textMap.remove("estimated");
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }
}
