package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

public class UnplannedPanel extends CreatePanel implements ActivityInformation {

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
        add(unplannedInputFormPanel, gbc);
    }

    @Override
    protected void addClearButton() {
    }

    @Override
    protected void addValidation() {
    }

    @Override
    protected void validActivityAction(Activity newActivity) {
        Activity currentToDo = panel.getPomodoro().getCurrentToDo();
        if (currentToDo != null) {
            if (unplannedInputFormPanel.isSelectedInternalInterruption()) {
                currentToDo.incrementInternalInter();
                currentToDo.databaseUpdate();
            } else if (unplannedInputFormPanel.isSelectedExternalInterruption()) {
                currentToDo.incrementInter();
                currentToDo.databaseUpdate();
            }
            Activity selectedToDo = (Activity) panel.getToDoJList().getSelectedValue();
            if (selectedToDo != null) {
                if (( unplannedInputFormPanel.isSelectedInternalInterruption() || unplannedInputFormPanel.isSelectedExternalInterruption() )) {
                    ToDoIconLabel.showIconLabel(panel.getIconLabel(), currentToDo.equals(selectedToDo) ? selectedToDo : currentToDo);
                    ToDoIconLabel.showIconLabel(panel.getInformationPanel().getIconLabel(), selectedToDo);
                    ToDoIconLabel.showIconLabel(panel.getCommentPanel().getIconLabel(), selectedToDo);
                    ToDoIconLabel.showIconLabel(panel.getOverestimationPanel().getIconLabel(), selectedToDo);
                    ToDoIconLabel.showIconLabel(panel.getIconLabel(), currentToDo.equals(selectedToDo) ? selectedToDo : currentToDo);
                }
            }
        }
        newActivity.setIsUnplanned(true);        
        JFrame window = new JFrame();
        String title = Labels.getString("ToDoListPanel.Add Unplanned activity");
        String message = "";
        if (unplannedInputFormPanel.isDateToday()) {
            message = Labels.getString("ToDoListPanel.Unplanned activity added to ToDo List");
            panel.getToDoList().add(newActivity); // Today unplanned activity
            newActivity.databaseInsert();
            clearForm();
            // refresh remaining Pomodoros label
            PomodorosRemainingLabel.showRemainPomodoros(panel.getPomodorosRemainingLabel(), panel.getToDoList());
        } else {
            message = Labels.getString("ToDoListPanel.Unplanned activity added to Activity List");
            validation.setVisible(false);
            super.validActivityAction(newActivity);            
        }
        JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
    }

    @Override
    protected void invalidActivityAction() {
        JFrame window = new JFrame();
        String title = Labels.getString("Common.Error");
        String message = Labels.getString("Common.Title is mandatory");
        JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return unplannedInputFormPanel;
    }

    @Override
    public void showInfo(Activity activity) {
        if (panel.getPomodoro().inPomodoro()) {
            activity = panel.getPomodoro().getCurrentToDo();
        }
        ToDoIconLabel.showIconLabel(iconLabel, activity);
    }

    @Override
    public void clearInfo() {
        ToDoIconLabel.clearIconLabel(iconLabel);
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }

    @Override
    public void clearForm() {
        unplannedInputFormPanel.setInterruption(0);
        unplannedInputFormPanel.setNameField("");
        unplannedInputFormPanel.setEstimatedPomodoros(1);
        unplannedInputFormPanel.setDescriptionField("");
        unplannedInputFormPanel.setTypeField("");
        unplannedInputFormPanel.setAuthorField("");
        unplannedInputFormPanel.setPlaceField("");
        unplannedInputFormPanel.setDate(new Date());
    }
}