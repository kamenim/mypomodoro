package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mypomodoro.Main;
import org.mypomodoro.gui.create.ActivityInputForm;

import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * Panel that allows the merging of ToDos
 *
 * @author Phil Karoo
 */
public class MergingPanel extends CreatePanel {

    private static final long serialVersionUID = 20110814L;
    private MergingActivityInputForm mergingInputFormPanel;
    private final ToDoListPanel panel;
    private List<Activity> selectedToDos;

    public MergingPanel(ToDoListPanel panel) {
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
        mergingInputFormPanel = new MergingActivityInputForm();
        mergingInputFormPanel.getNameField().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (mergingInputFormPanel.getToDosListTextArea().getText().length() != 0) {
                    enableSaveButton();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (mergingInputFormPanel.getNameField().getText().length() == 0) {
                    disableSaveButton();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (mergingInputFormPanel.getToDosListTextArea().getText().length() != 0) {
                    enableSaveButton();
                }
            }
        });
        mergingInputFormPanel.getToDosListTextArea().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (mergingInputFormPanel.getNameField().getText().length() != 0) {
                    enableSaveButton();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (mergingInputFormPanel.getToDosListTextArea().getText().length() == 0) {
                    disableSaveButton();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (mergingInputFormPanel.getNameField().getText().length() != 0) {
                    enableSaveButton();
                }
            }
        });
        add(new JScrollPane(mergingInputFormPanel), gbc);
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
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
        newActivity.setIsUnplanned(true);
        String title = Labels.getString("ToDoListPanel.Merge ToDos");
        String message;
        StringBuilder comments = new StringBuilder();
        int actualPoms = 0;
        for (Activity deleteToDo : selectedToDos) {
            // aggregate comments
            if (deleteToDo.getNotes() != null && deleteToDo.getNotes().length() > 0) {
                comments.append(deleteToDo.getName());
                comments.append(":\n");
                comments.append(deleteToDo.getNotes());
                comments.append("\n\n");
            }
            ActivityList.getList().removeById(deleteToDo.getId());
            actualPoms += deleteToDo.getActualPoms();
        }
        // set comment
        newActivity.setNotes(comments.toString());
        // set estimate
        // make sure the estimate of the new activity is at least one pomodoro higher than the sum of pomodoros already done (if any)
        if (actualPoms > 0 && newActivity.getEstimatedPoms() <= actualPoms) {
            newActivity.setEstimatedPoms(actualPoms + 1);
        }
        if (actualPoms > 0) {
            newActivity.setActualPoms(actualPoms);
        }
        if (mergingInputFormPanel.isDateToday()) {
            message = Labels.getString("ToDoListPanel.Unplanned ToDo added to ToDo List");
            // Today unplanned merge activity
            panel.getToDoList().add(newActivity);
            newActivity.databaseInsert();
            clearForm();
        } else {
            message = Labels.getString("ToDoListPanel.Unplanned activity added to Activity List");
            validation.setVisible(false);
            selectedToDos = null;
            mergingInputFormPanel.setToDoListTextArea("");
            super.validActivityAction(newActivity); // validation and clear form
        }
        JOptionPane.showConfirmDialog(Main.gui, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        panel.refresh();
    }

    @Override
    protected void invalidActivityAction() {
        String title = Labels.getString("Common.Error");
        String message = Labels.getString("Common.Title is mandatory");
        JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return mergingInputFormPanel;
    }

    @Override
    public void clearForm() {
        selectedToDos = null;
        mergingInputFormPanel.setToDoListTextArea("");
        mergingInputFormPanel.setNameField("");
        mergingInputFormPanel.setEstimatedPomodoro(1);
        mergingInputFormPanel.setDescriptionField("");
        mergingInputFormPanel.setTypeField("");
        mergingInputFormPanel.setAuthorField("");
        mergingInputFormPanel.setPlaceField("");
        mergingInputFormPanel.setDate(new Date());
    }

    public void displaySelectedToDos(List<Activity> selectedToDos) {
        this.selectedToDos = selectedToDos;
        mergingInputFormPanel.displaySelectedToDos(selectedToDos);
    }
}
