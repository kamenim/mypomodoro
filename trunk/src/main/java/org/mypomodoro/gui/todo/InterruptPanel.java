package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;

public class InterruptPanel extends CreatePanel {

    private final ToDoList todoList;

    public InterruptPanel(ToDoList todoList) {
        this.todoList = todoList;
        gbc = new GridBagConstraints();
        setLayout(new GridBagLayout());

        addInputFormPanel();
        addSaveButton();
    }

    @Override
    protected void addInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(inputFormPanel, gbc);
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
        newActivity.setIsUnplanned(true);
        JFrame window = new JFrame();
        String title = "Unplanned activity";
        String message = "Unplanned activity \"" + newActivity.getName() + "\" added to ";
        if (inputFormPanel.isDateToday()) {
            message += "ToDo List";
            todoList.getList().add(newActivity); // Today unplanned activity
            newActivity.databaseInsert();
        } else {
            message += "Activity List";
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
}