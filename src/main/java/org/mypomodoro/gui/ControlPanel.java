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
import org.mypomodoro.buttons.myButton;

import org.mypomodoro.model.Preferences;
import org.mypomodoro.util.Labels;

public class ControlPanel extends JPanel {

    public static Preferences preferences = new Preferences();
    private JLabel validation = new JLabel("");
    public static Labels labels;
    public JButton saveButton;
    public JButton resetButton;
    protected GridBagConstraints gbc = new GridBagConstraints();
    protected final PreferencesInputForm preferencesInputFormPanel;

    public ControlPanel() {
        preferences.loadPreferences();
        labels = new Labels(new Locale(preferences.getLocale().getLanguage(), preferences.getLocale().getCountry(), preferences.getLocale().getVariant()));
        saveButton = new myButton(labels.getString("Common.Save"));
        preferencesInputFormPanel = new PreferencesInputForm(this);
        resetButton = new myButton(labels.getString("PreferencesPanel.Reset"));
        validation.setFont(new Font(validation.getFont().getName(), Font.BOLD, validation.getFont().getSize()));

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
                updatePreferences();
                validation.setText(labels.getString("PreferencesPanel.Preferences saved. Please restart myPomodoro."));
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
                preferencesInputFormPanel.systemTrayMessageBox.setSelected(true);
                updatePreferences();
                validation.setText(labels.getString("PreferencesPanel.Preferences reset. Please restart myPomodoro."));
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
        add(validation, gbc);
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
        preferences.setSystemTrayMessage(preferencesInputFormPanel.systemTrayMessageBox.isSelected());
        preferences.updatePreferences();
    }
}