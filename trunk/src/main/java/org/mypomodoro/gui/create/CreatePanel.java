package org.mypomodoro.gui.create;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.mypomodoro.buttons.SaveButton;
import org.mypomodoro.buttons.MyButton;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.Labels;

/**
 * GUI for creating a new Activity and store to data layer.
 * 
 * @author Brian Wetzel 
 * @author Phil Karoo
 */
public class CreatePanel extends JPanel {

    protected final ActivityInputForm inputFormPanel = new ActivityInputForm();
    protected final JLabel validation = new JLabel("");
    protected final SaveButton saveButton = new SaveButton(this);
    protected GridBagConstraints gbc = new GridBagConstraints();

    public CreatePanel() {
        setLayout(new GridBagLayout());

        addInputFormPanel();
        addSaveButton();
        addClearButton();
        addValidation();
    }

    protected void addInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.80;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(inputFormPanel, gbc);
    }

    protected void addSaveButton() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        //gbc.fill = GridBagConstraints.NONE;
        add(saveButton, gbc);
    }

    protected void addClearButton() {
        JButton clearButton = new MyButton(Labels.getString("CreatePanel.Clear"));
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        //gbc.fill = GridBagConstraints.NONE;
        add(clearButton, gbc);
    }

    protected void addValidation() {
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        add(validation, gbc);
    }

    protected void validActivityAction(Activity newActivity) {
        ActivityList.getList().add(newActivity);
        newActivity.databaseInsert();
        clearForm();
        validation.setForeground(Color.black);
        validation.setFont(new Font(validation.getFont().getName(), Font.BOLD, validation.getFont().getSize()));
        validation.setText(Labels.getString("CreatePanel.Activity {0} added to Activity List", newActivity.getName()));
    }

    public void saveActivity(Activity newActivity) {
        if (!newActivity.isValid()) {
            invalidActivityAction();
        } else if (newActivity.alreadyExists()) {
            JFrame window = new JFrame();
            String title = Labels.getString("Common.Warning");
            String message = Labels.getString("CreatePanel.An activity with the same date and title already exists. Proceed anyway?");
            int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                validActivityAction(newActivity);
            }
        } else {
            validActivityAction(newActivity);
        }
    }

    protected void invalidActivityAction() {
        validation.setForeground(Color.red);
        validation.setFont(new Font(validation.getFont().getName(), Font.BOLD, validation.getFont().getSize()));
        validation.setText(Labels.getString("Common.Title is mandatory"));
    }

    public ActivityInputForm getFormPanel() {
        return inputFormPanel;
    }

    public void clearForm() {
        inputFormPanel.setNameField("");
        inputFormPanel.setEstimatedPomodoros(1);
        inputFormPanel.setDescriptionField("");
        inputFormPanel.setTypeField("");
        inputFormPanel.setAuthorField("");
        inputFormPanel.setPlaceField("");
        inputFormPanel.setDate(new Date());
        validation.setText("");
    }
    
    public void fillOutInputForm(Activity activity) {
    }
}