package org.mypomodoro.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.mypomodoro.util.Labels;

/**
 * 
 * @author Phil Karoo
 */
public class PreferencesInputForm extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension PANEL_DIMENSION = new Dimension(400, 200);
    protected final TimerValueSlider pomodoroSlider;
    protected final TimerValueSlider shortBreakSlider;
    protected final TimerValueSlider longBreakSlider;
    protected final TimerValueSlider maxNbPomPerActivitySlider;
    protected final TimerValueSlider maxNbPomPerDaySlider;
    protected final TimerValueSlider nbPomPerSetSlider;
    protected final JCheckBox tickingBox;
    protected final JCheckBox ringingBox;
    protected final JComboBox localesComboBox;
    protected final JCheckBox systemTrayBox;
    protected final JCheckBox systemTrayMessageBox;

    public PreferencesInputForm(final ControlPanel controlPanel) {
        setBorder(new TitledBorder(new EtchedBorder(),
                Labels.getString("PreferencesPanel.Preferences")));
        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        int unitMinute = 0;
        int unitPomodoro = 1;
        pomodoroSlider = new TimerValueSlider(controlPanel, 10, 45,
                ControlPanel.preferences.getPomodoroLength(),
                Labels.getString("PreferencesPanel.Pomodoro Length") + ": ",
                25, 30, unitMinute);
        shortBreakSlider = new TimerValueSlider(controlPanel, 1, 15,
                ControlPanel.preferences.getShortBreakLength(),
                Labels.getString("PreferencesPanel.Short Break Length") + ": ",
                3, 5, unitMinute);
        longBreakSlider = new TimerValueSlider(controlPanel, 5, 45,
                ControlPanel.preferences.getLongBreakLength(),
                Labels.getString("PreferencesPanel.Long Break Length") + ": ",
                15, 30, unitMinute);
        maxNbPomPerActivitySlider = new TimerValueSlider(
                controlPanel,
                1,
                7,
                ControlPanel.preferences.getMaxNbPomPerActivity(),
                Labels.getString("PreferencesPanel.Max nb pom/activity") + ": ",
                1, 5, unitPomodoro);
        maxNbPomPerDaySlider = new TimerValueSlider(controlPanel, 1, 12,
                ControlPanel.preferences.getMaxNbPomPerDay(),
                Labels.getString("PreferencesPanel.Max nb pom/day") + ": ", 1,
                10, unitPomodoro);
        nbPomPerSetSlider = new TimerValueSlider(controlPanel, 3, 5,
                ControlPanel.preferences.getNbPomPerSet(),
                Labels.getString("PreferencesPanel.Nb pom/set") + ": ", 4, 4,
                unitPomodoro);
        tickingBox = new JCheckBox(
                Labels.getString("PreferencesPanel.ticking"),
                ControlPanel.preferences.getTicking());
        ringingBox = new JCheckBox(
                Labels.getString("PreferencesPanel.ringing"),
                ControlPanel.preferences.getRinging());
        tickingBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        ringingBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        List<ItemLocale> locales = ItemLocale.getLocalesFromPropertiesTitlefiles();
        localesComboBox = new JComboBox(locales.toArray());
        for (int i = 0; i < locales.size(); i++) {
            if (locales.get(i).getLocale().equals(
                    ControlPanel.preferences.getLocale())) {
                localesComboBox.setSelectedIndex(i);
            }
        }
        localesComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        systemTrayBox = new JCheckBox(
                Labels.getString("PreferencesPanel.System Tray"),
                ControlPanel.preferences.getSystemTray());
        systemTrayMessageBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Popup message"),
                ControlPanel.preferences.getSystemTrayMessage());
        systemTrayBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                if (!systemTrayBox.isSelected()) {
                    systemTrayMessageBox.setSelected(false);
                }
            }
        });
        systemTrayMessageBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                if (systemTrayMessageBox.isSelected()) {
                    systemTrayBox.setSelected(true);
                }
            }
        });


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = .5;
        gbc.fill = GridBagConstraints.BOTH;
        add(pomodoroSlider, gbc);
        gbc.gridy = 1;
        add(shortBreakSlider, gbc);
        gbc.gridy = 2;
        add(longBreakSlider, gbc);
        gbc.gridy = 3;
        add(maxNbPomPerActivitySlider, gbc);
        gbc.gridy = 4;
        add(maxNbPomPerDaySlider, gbc);
        gbc.gridy = 5;
        add(nbPomPerSetSlider, gbc);
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        addSounds(gbc);
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        addLocales(gbc);
        if (SystemTray.isSupported()) {
            gbc.gridy = 8;
            gbc.gridwidth = 2;
            addSystemTray(gbc);
        }
    }

    private void addSounds(GridBagConstraints gbc) {
        JPanel sounds = new JPanel();
        sounds.setLayout(new GridBagLayout());
        GridBagConstraints gbcSounds = new GridBagConstraints();
        gbcSounds.fill = GridBagConstraints.HORIZONTAL;
        gbcSounds.anchor = GridBagConstraints.NORTH;
        gbcSounds.gridx = 0;
        gbcSounds.gridy = 0;
        sounds.add(tickingBox, gbcSounds);
        gbcSounds.gridx = 1;
        gbcSounds.gridy = 0;
        sounds.add(ringingBox, gbcSounds);
        add(sounds, gbc);
    }

    private void addLocales(GridBagConstraints gbc) {
        JPanel locales = new JPanel();
        locales.setLayout(new GridBagLayout());
        GridBagConstraints gbcLocales = new GridBagConstraints();
        gbcLocales.fill = GridBagConstraints.HORIZONTAL;
        gbcLocales.anchor = GridBagConstraints.NORTH;
        gbcLocales.gridx = 0;
        gbcLocales.gridy = 0;
        locales.add(localesComboBox, gbcLocales);
        add(locales, gbc);
    }

    private void addSystemTray(GridBagConstraints gbc) {
        JPanel systemTray = new JPanel();
        systemTray.setLayout(new GridBagLayout());
        GridBagConstraints gbcSystemTray = new GridBagConstraints();
        gbcSystemTray.fill = GridBagConstraints.HORIZONTAL;
        gbcSystemTray.anchor = GridBagConstraints.NORTH;
        gbcSystemTray.gridx = 0;
        gbcSystemTray.gridy = 0;
        systemTray.add(systemTrayBox, gbcSystemTray);
        gbcSystemTray.gridx = 1;
        gbcSystemTray.gridy = 0;
        systemTray.add(systemTrayMessageBox, gbcSystemTray);
        add(systemTray, gbc);
    }
}