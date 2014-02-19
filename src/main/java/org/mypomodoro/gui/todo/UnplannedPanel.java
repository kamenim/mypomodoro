package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mypomodoro.Main;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that allows creating unplanned activities and adding interruptions to
 * the current ToDo
 *
 */
public class UnplannedPanel extends CreatePanel {

    private static final long serialVersionUID = 20110814L;
    private UnplannedActivityInputForm unplannedInputFormPanel;
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final ToDoListPanel panel;

    public UnplannedPanel(ToDoListPanel panel) {
        this.panel = panel;

        addToDoIconPanel();
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
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        unplannedInputFormPanel = new UnplannedActivityInputForm();
        unplannedInputFormPanel.getNameField().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableSaveButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (unplannedInputFormPanel.getNameField().getText().length() == 0) {
                    disableSaveButton();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                enableSaveButton();
            }
        });
        add(new JScrollPane(unplannedInputFormPanel), gbc);
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
                // set parent id
                newActivity.setParentId(currentToDo.getId());
            } else if (unplannedInputFormPanel.isSelectedExternalInterruption()) {
                currentToDo.incrementInter();
                currentToDo.databaseUpdate();
                // set parent id
                newActivity.setParentId(currentToDo.getId());
            }
        }
        newActivity.setIsUnplanned(true);
        String title = Labels.getString("ToDoListPanel.Add Unplanned activity");
        String message;
        // In Agile mode, the unplanned/interruption is always added to the Iteration Backlog
        if (unplannedInputFormPanel.isDateToday() || ControlPanel.preferences.getAgileMode()) {
            message = Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.Unplanned task added to ToDo List");
            if (ControlPanel.preferences.getAgileMode()) {
                newActivity.setIteration(-1); // no specific iteration
            }
            // Today unplanned interruption/activity
            panel.getToDoList().add(newActivity);
            newActivity.databaseInsert();
            clearForm();
            panel.refresh();
        } else {
            message = Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.Unplanned task added to Activity List");
            validation.setVisible(false);
            super.validActivityAction(newActivity); // validation and clear form
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
