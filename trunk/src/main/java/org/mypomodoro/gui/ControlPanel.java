package org.mypomodoro.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import org.mypomodoro.buttons.MyButton;
import org.mypomodoro.buttons.RestartButton;

import org.mypomodoro.model.Preferences;
import org.mypomodoro.util.Labels;

public class ControlPanel extends JPanel {

    public static Preferences preferences = new Preferences();
    public static Labels labels;
    public JButton saveButton;
    public JButton resetButton;
    protected JLabel validation = new JLabel();
    protected JPanel validPanel = new JPanel();
    public JButton restartButton;
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected final PreferencesInputForm preferencesInputFormPanel;

    public ControlPanel() {
        preferences.loadPreferences();
        labels = new Labels(new Locale(preferences.getLocale().getLanguage(), preferences.getLocale().getCountry(), preferences.getLocale().getVariant()));
        saveButton = new MyButton(Labels.getString("Common.Save"));
        preferencesInputFormPanel = new PreferencesInputForm(this);
        resetButton = new MyButton(Labels.getString("PreferencesPanel.Reset"));
        restartButton = new RestartButton();
        restartButton.setVisible(false);

        setLayout(new GridBagLayout());
        disableSaveButton();

        addPreferencesInputFormPanel();
        addSaveButton();
        addResetButton();
        addValidation();
    }

    protected void addPreferencesInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.80;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(preferencesInputFormPanel, gbc);
    }

    protected void addSaveButton() {
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                setValidation(Labels.getString("PreferencesPanel.Preferences saved.") + " ");
                updatePreferences();
                disableSaveButton();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        //gbc.fill = GridBagConstraints.NONE;
        add(saveButton, gbc);
    }

    protected void addResetButton() { // values recommended by the Pomodoro Technique
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                preferencesInputFormPanel.pomodoroSlider.setSliderValue(preferences.PLENGTH);
                preferencesInputFormPanel.shortBreakSlider.setSliderValue(preferences.SBLENGTH);
                preferencesInputFormPanel.longBreakSlider.setSliderValue(preferences.LBLENGTH);
                preferencesInputFormPanel.maxNbPomPerActivitySlider.setSliderValue(preferences.MNPPACTIVITY);
                preferencesInputFormPanel.maxNbPomPerDaySlider.setSliderValue(preferences.MNPPDAY);
                preferencesInputFormPanel.nbPomPerSetSlider.setSliderValue(preferences.NPPSet);
                preferencesInputFormPanel.tickingBox.setSelected(true);
                preferencesInputFormPanel.ringingBox.setSelected(true);
                // no reset for locale
                preferencesInputFormPanel.systemTrayBox.setSelected(true);
                setValidation(Labels.getString("PreferencesPanel.Preferences reset.") + " ");
                updatePreferences();
                disableSaveButton();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        //gbc.fill = GridBagConstraints.NONE;
        add(resetButton, gbc);
    }

    protected void addValidation() {
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        validation.setForeground(Color.black);
        validation.setFont(new Font(validation.getFont().getName(), Font.BOLD, validation.getFont().getSize()));        
        GridBagConstraints vgbc = new GridBagConstraints();
        vgbc.gridx = 0;
        vgbc.gridy = 0;
        vgbc.fill = GridBagConstraints.NONE;
        validPanel.add(validation, vgbc);
        vgbc.gridx = 1;
        vgbc.gridy = 0;
        vgbc.fill = GridBagConstraints.NONE;
        validPanel.add(restartButton, vgbc);
        validPanel.setLayout(new GridBagLayout());
        add(validPanel, gbc);
    }

    protected void setValidation(String validationText) {
        validation.setText(validationText);
        restartButton.setVisible(true);
    }

    public void disableSaveButton() {
        saveButton.setEnabled(false);
        saveButton.setOpaque(false);
        saveButton.setForeground(Color.GRAY);
    }

    public void enableSaveButton() {
        saveButton.setEnabled(true);
        saveButton.setForeground(Color.black);
    }

    public void clearValidation() {
        validation.setText("");
        restartButton.setVisible(false);
    }

    private void updatePreferences() {
        preferences.setPomodoroLength(preferencesInputFormPanel.pomodoroSlider.getSliderValue());
        preferences.setShortBreakLength(preferencesInputFormPanel.shortBreakSlider.getSliderValue());
        preferences.setLongBreakLength(preferencesInputFormPanel.longBreakSlider.getSliderValue());
        preferences.setMaxNbPomPerActivity(preferencesInputFormPanel.maxNbPomPerActivitySlider.getSliderValue());
        preferences.setMaxNbPomPerDay(preferencesInputFormPanel.maxNbPomPerDaySlider.getSliderValue());
        preferences.setNbPomPerSet(preferencesInputFormPanel.nbPomPerSetSlider.getSliderValue());
        preferences.setTicking(preferencesInputFormPanel.tickingBox.isSelected());
        preferences.setRinging(preferencesInputFormPanel.ringingBox.isSelected());
        preferences.setLocale(( (ItemLocale) preferencesInputFormPanel.localesComboBox.getSelectedItem() ).getLocale());
        preferences.setSystemTray(preferencesInputFormPanel.systemTrayBox.isSelected());
        preferences.updatePreferences();
    }
}