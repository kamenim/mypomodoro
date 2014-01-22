package org.mypomodoro.gui.todo;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import org.mypomodoro.Main;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that allows creating unplanned activities and adding interruptions to
 * the current ToDo
 *
 * @author Phil Karoo
 */
public class UnplannedPanel extends CreatePanel {

    private static final long serialVersionUID = 20110814L;
    protected UnplannedActivityInputForm unplannedInputFormPanel;
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final ToDoListPanel panel;

    public UnplannedPanel(ToDoListPanel panel) {
        this.panel = panel;

        addToDoIconPanel();
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        //gbc.fill = GridBagConstraints.NONE;
        disableSaveButton();
        add(saveButton, gbc);
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

    @Override
    protected void addInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        unplannedInputFormPanel = new UnplannedActivityInputForm();
        Component[] fields = unplannedInputFormPanel.getComponents();
        for (int i = 0; i < fields.length; i++) {
            fields[i].addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    enableSaveButton();
                }
            });
        }
        add(new JScrollPane(unplannedInputFormPanel), gbc);
    }

    @Override
    protected void addClearButton() {
    }

    @Override
    protected void addValidation() {
    }

    @Override
    protected void validActivityAction(Activity newActivity) {
        // Interruptions : update current/running pomodoro
        Activity currentToDo = panel.getPomodoro().getCurrentToDo();
        if (currentToDo != null) {
            if (unplannedInputFormPanel.isSelectedInternalInterruption()) {
                currentToDo.incrementInternalInter();
                currentToDo.databaseUpdate();
            } else if (unplannedInputFormPanel.isSelectedExternalInterruption()) {
                currentToDo.incrementInter();
                currentToDo.databaseUpdate();
            }
        }
        newActivity.setIsUnplanned(true);
        String title = Labels.getString("ToDoListPanel.Add Unplanned activity");
        String message;
        if (unplannedInputFormPanel.isDateToday()) {
            message = Labels.getString("ToDoListPanel.Unplanned activity added to ToDo List");
            // Today unplanned interruption/activity
            panel.getToDoList().add(newActivity);
            newActivity.databaseInsert();
            clearForm();
            panel.refresh();
        } else {
            message = Labels.getString("ToDoListPanel.Unplanned activity added to Activity List");
            validation.setVisible(false);
            super.validActivityAction(newActivity);
        }
        JOptionPane.showConfirmDialog(Main.gui, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void invalidActivityAction() {
        String title = Labels.getString("Common.Error");
        String message = Labels.getString("Common.Title is mandatory");
        JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return unplannedInputFormPanel;
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }

    @Override
    public void clearForm() {
        unplannedInputFormPanel.setInterruption(0);
        unplannedInputFormPanel.setNameField("");
        unplannedInputFormPanel.setEstimatedPomodoro(1);
        unplannedInputFormPanel.setDescriptionField("");
        unplannedInputFormPanel.setTypeField("");
        unplannedInputFormPanel.setAuthorField("");
        unplannedInputFormPanel.setPlaceField("");
        unplannedInputFormPanel.setDate(new Date());
    }
}
