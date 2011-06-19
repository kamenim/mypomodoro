package org.mypomodoro.gui.create;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mypomodoro.buttons.SaveButton;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;

/**
 * GUI for creating a new Activity and store to data layer.
 * 
 * @author Brian Wetzel
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
		gbc.fill = GridBagConstraints.NONE;        
		add(saveButton, gbc);
	}

    protected void addClearButton() {
        JButton clearButton = new JButton("Clear");
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
		gbc.fill = GridBagConstraints.NONE;
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
        validation.setFont(new Font(validation.getFont().getName(),Font.BOLD,validation.getFont().getSize()));
		validation.setText("Activity added to Activity List");
	}

	public void saveActivity(Activity newActivity) {        
		if (newActivity.isValid()) {
			validActivityAction(newActivity);
		} else {
			invalidActivityAction();
		}
	}

	protected void invalidActivityAction() {
		validation.setForeground(Color.red);
        validation.setFont(new Font(validation.getFont().getName(),Font.BOLD,validation.getFont().getSize()));
		validation.setText("Title is mandatory");
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
        validation.setText("");
    }
}