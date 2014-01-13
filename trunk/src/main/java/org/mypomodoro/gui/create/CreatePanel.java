package org.mypomodoro.gui.create;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.mypomodoro.Main;

import org.mypomodoro.buttons.AbstractPomodoroButton;
import org.mypomodoro.buttons.SaveButton;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * GUI for creating a new Activity and store to data layer.
 * 
 * @author Brian Wetzel 
 * @author Phil Karoo
 */
public class CreatePanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
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
        Component[] fields = inputFormPanel.getComponents();
        for (int i = 0; i < fields.length; i++) {
            fields[i].addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    enableSaveButton();
                    clearValidation(); // clear validation message when clicking on text fields
                }
            });
        }
        add(inputFormPanel, gbc);
    }

    protected void addSaveButton() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        //gbc.fill = GridBagConstraints.NONE;
        disableSaveButton();
        add(saveButton, gbc);
    }

    protected void addClearButton() {
        JButton clearButton = new AbstractPomodoroButton(Labels.getString("Common.Reset"));
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                disableSaveButton();
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
        validation.setVisible(false);
        add(validation, gbc);
    }

    protected void validActivityAction(Activity newActivity) {
        ActivityList.getList().add(newActivity);
        newActivity.databaseInsert();             
        clearForm();
        validation.setForeground(ColorUtil.BLACK);
        validation.setFont(new Font(validation.getFont().getName(), Font.BOLD, validation.getFont().getSize()));
        validation.setText(Labels.getString("CreatePanel.Activity added to Activity List"));        
    }

    public void saveActivity(Activity newActivity) {
        if (!newActivity.isValid()) {
            invalidActivityAction();
            validation.setVisible(true);
            disableSaveButton();
        } else if (newActivity.alreadyExists()) {            
            String title = Labels.getString("Common.Warning");
            String message = Labels.getString("CreatePanel.An activity with the same date and title already exists. Proceed anyway?");
            int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                disableSaveButton();
                validActivityAction(newActivity);
                validation.setVisible(true);
            }
        } else {
            disableSaveButton();
            validActivityAction(newActivity);
            validation.setVisible(true);
        }
    }

    protected void invalidActivityAction() {
        validation.setForeground(ColorUtil.RED);
        validation.setFont(new Font(validation.getFont().getName(), Font.BOLD, validation.getFont().getSize()));
        validation.setText(Labels.getString("Common.Title is mandatory"));
    }

    public ActivityInputForm getFormPanel() {
        return inputFormPanel;
    }

    public void clearForm() {
        inputFormPanel.setNameField("");
        inputFormPanel.setEstimatedPomodoro(1);
        inputFormPanel.setDescriptionField("");        
        inputFormPanel.setTypeField("");
        inputFormPanel.setAuthorField("");
        inputFormPanel.setPlaceField("");
        inputFormPanel.setDate(new Date());
        clearValidation();
    }

    public void fillOutInputForm(Activity activity) {
    }

    private void clearValidation() {
        validation.setText("");
        validation.setVisible(false);
    }

    public void enableSaveButton() {
        saveButton.setEnabled(true);
        saveButton.setOpaque(true);
        saveButton.setForeground(ColorUtil.BLACK);
    }

    public void disableSaveButton() {
        saveButton.setEnabled(false);
        saveButton.setOpaque(false);
        saveButton.setForeground(Color.GRAY);
    }
}