package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.buttons.MyButton;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel to overestimate initial estimation
 * 
 * @author Phil Karoo
 */
public class OverestimationPanel extends JPanel implements ActivityInformation {

    protected final OverestimationInputForm overestimationInputFormPanel = new OverestimationInputForm();
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final ToDoListPanel panel;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public OverestimationPanel(ToDoListPanel panel) {
        this.panel = panel;

        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addToDoIconPanel();
        addOverestimationInputFormPanel();
        addSaveButton();
    }

    private void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        //gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new MyButton(Labels.getString("Common.Save"));
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveOverestimation(overestimationInputFormPanel.getOverestimationPomodoros().getSelectedIndex() + 1);
            }
        });
        add(changeButton, gbc);
    }

    private void addToDoIconPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        add(iconLabel, gbc);
    }

    private void addOverestimationInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(overestimationInputFormPanel, gbc);
    }

    public void saveOverestimation(int overestimatedPomodoros) {
        Activity selectedToDo = (Activity) panel.getToDoJList().getSelectedValue();
        if (selectedToDo != null) {
            selectedToDo.setOverestimatedPoms(selectedToDo.getOverestimatedPoms() + overestimatedPomodoros);
            selectedToDo.databaseUpdate();
            // refresh info panel
            panel.getInformationPanel().showInfo(selectedToDo);
            // refresh icon label
            if (panel.getPomodoro().getCurrentToDo().equals(selectedToDo)) {
                ToDoIconLabel.showIconLabel(panel.getIconLabel(), selectedToDo);
            }
            ToDoIconLabel.showIconLabel(panel.getInformationPanel().getIconLabel(), selectedToDo);
            ToDoIconLabel.showIconLabel(panel.getCommentPanel().getIconLabel(), selectedToDo);
            ToDoIconLabel.showIconLabel(iconLabel, selectedToDo);
            if (panel.getPomodoro().getCurrentToDo().equals(selectedToDo)) {
                ToDoIconLabel.showIconLabel(panel.getUnplannedPanel().getIconLabel(), selectedToDo);
            }
            // refresh remaining Pomodoros label
            PomodorosRemainingLabel.showRemainPomodoros(panel.getPomodorosRemainingLabel(), panel.getToDoList());
            JFrame window = new JFrame();
            String title = Labels.getString("ToDoListPanel.Overestimate ToDo");
            String message = Labels.getString("ToDoListPanel.Nb of estimated pomodoros increased by {0}", overestimatedPomodoros);
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }

    @Override
    public void showInfo(Activity activity) {
        ToDoIconLabel.showIconLabel(iconLabel, activity);
    }

    @Override
    public void clearInfo() {
        ToDoIconLabel.clearIconLabel(iconLabel);
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }
}