package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;

public class UnplannedPanel extends CreatePanel {

    protected UnplannedActivityInputForm unplannedInputFormPanel;
    private final ToDoListPanel panel;

    public UnplannedPanel(ToDoListPanel panel) {
        this.panel = panel;        
    }

    @Override
    protected void addInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        unplannedInputFormPanel = new UnplannedActivityInputForm();
        add(unplannedInputFormPanel, gbc);
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        add(saveButton, gbc);
    }

    @Override
    protected void addClearButton() {
    }

    @Override
    protected void validActivityAction(Activity newActivity) {
        Activity selectedToDo = (Activity) panel.getToDoJList().getSelectedValue();
        if (selectedToDo != null) {
            if (panel.getPomodoro().inPomodoro()) {
                if (unplannedInputFormPanel.isSelectedInternalInterruption()) {
                    panel.getPomodoro().getCurrentToDo().incrementInternalInter();
                } else if (unplannedInputFormPanel.isSelectedExternalInterruption()) {
                    panel.getPomodoro().getCurrentToDo().incrementInter();
                }
                if ((unplannedInputFormPanel.isSelectedInternalInterruption() || unplannedInputFormPanel.isSelectedExternalInterruption()) &&
                        panel.getPomodoro().getCurrentToDo().equals(selectedToDo)) {
                    ToDoIconLabel.showIconLabel(panel.getIconLabel(), selectedToDo);
                    ToDoIconLabel.showIconLabel(panel.getInformationPanel().getIconLabel(), selectedToDo);
                    ToDoIconLabel.showIconLabel(panel.getCommentPanel().getIconLabel(), selectedToDo);                              
                }
            }
        }
        newActivity.setIsUnplanned(true);
        JFrame window = new JFrame();
        String title = "Unplanned activity";
        String message = "";
        if (unplannedInputFormPanel.isDateToday()) {
            message = "Unplanned activity added to ToDo List";
            panel.getToDoList().add(newActivity); // Today unplanned activity
            newActivity.databaseInsert();
        } else {
            message = "Unplanned activity added to Activity List";
            validation.setVisible(false);
            super.validActivityAction(newActivity);
        }
        JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
    }

    @Override
    protected void invalidActivityAction() {
        JFrame window = new JFrame();
        String title = "Error";
        String message = "Title is mandatory";
        JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return unplannedInputFormPanel;
    }
}