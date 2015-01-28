/* 
 * Copyright (C) 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.gui.preferences;

import java.awt.Dimension;
import java.awt.Font;
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
import org.mypomodoro.gui.ItemLocale;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;
import org.mypomodoro.util.Labels;

/**
 * Preferences input form
 *
 */
public class PreferencesInputForm extends JPanel {

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
    protected final JCheckBox alwaysOnTopBox;
    protected final JCheckBox agileModeBox;
    protected final JCheckBox pomodoroModeBox;
    protected final JCheckBox plainHoursBox;
    protected final JCheckBox effectiveHoursBox;

    public PreferencesInputForm(final PreferencesPanel controlPanel) {
        TitledBorder titledborder = new TitledBorder(new EtchedBorder(), Labels.getString("PreferencesPanel.Preferences"));
        titledborder.setTitleFont(getFont().deriveFont(Font.BOLD));
        setBorder(titledborder);

        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.CENTER;
        int unitMinute = 0;
        int unitPomodoro = 1;
        pomodoroSlider = new TimerValueSlider(controlPanel, 10, 45,
                PreferencesPanel.preferences.getPomodoroLength(),
                Labels.getString("PreferencesPanel.Pomodoro Length") + ": ",
                25, 30, unitMinute);
        shortBreakSlider = new TimerValueSlider(controlPanel, 1, 15,
                PreferencesPanel.preferences.getShortBreakLength(),
                Labels.getString("PreferencesPanel.Short Break Length") + ": ",
                3, 5, unitMinute);
        longBreakSlider = new TimerValueSlider(controlPanel, 5, 45,
                PreferencesPanel.preferences.getLongBreakLength(),
                Labels.getString("PreferencesPanel.Long Break Length") + ": ",
                15, 30, unitMinute);
        final int maxNbPomPerActivity = 7;
        final int initMaxNbPomPerActivity = 5;
        final int maxNbPomPerActivityAgileMode = 24; // In the Agile world, a task may last up to 2 days (2 times the max nb of pom per day)
        final int initMaxNbPomPerActivityAgileMode = 20;
        final int maxNbPomPerDay = 12;
        final int initMaxNbPomPerDay = 10;
        maxNbPomPerActivitySlider = new TimerValueSlider(controlPanel, 1, maxNbPomPerActivityAgileMode,
                PreferencesPanel.preferences.getMaxNbPomPerActivity(),
                Labels.getString("PreferencesPanel.Max nb pom/activity") + ": ",
                1, initMaxNbPomPerActivityAgileMode, unitPomodoro);
        maxNbPomPerDaySlider = new TimerValueSlider(controlPanel, 1, maxNbPomPerDay,
                PreferencesPanel.preferences.getMaxNbPomPerDay(),
                Labels.getString("PreferencesPanel.Max nb pom/day") + ": ", 1,
                initMaxNbPomPerDay, unitPomodoro);
        nbPomPerSetSlider = new TimerValueSlider(controlPanel, 3, 5,
                PreferencesPanel.preferences.getNbPomPerSet(),
                Labels.getString("PreferencesPanel.Nb pom/set") + ": ", 4, 4,
                unitPomodoro);
        tickingBox = new JCheckBox(
                Labels.getString("PreferencesPanel.ticking"),
                PreferencesPanel.preferences.getTicking());
        ringingBox = new JCheckBox(
                Labels.getString("PreferencesPanel.ringing"),
                PreferencesPanel.preferences.getRinging());
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
                    PreferencesPanel.preferences.getLocale())) {
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
                PreferencesPanel.preferences.getSystemTray());
        systemTrayMessageBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Popup Message"),
                PreferencesPanel.preferences.getSystemTrayMessage());
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
        alwaysOnTopBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Always On Top"),
                PreferencesPanel.preferences.getAlwaysOnTop());
        alwaysOnTopBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        agileModeBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Agile.Agile Mode"),
                PreferencesPanel.preferences.getAgileMode());
        agileModeBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                // In the Agile world, a task may last up to 2 days (2 times the max nb of pom per day)
                maxNbPomPerActivitySlider.changeSlider(maxNbPomPerActivityAgileMode);
                maxNbPomPerActivitySlider.setSliderValue(maxNbPomPerDaySlider.getSliderValue() * 2);
                maxNbPomPerDaySlider.setVisible(false);
                agileModeBox.setSelected(true);
                pomodoroModeBox.setSelected(false);
            }
        });
        if (agileModeBox.isSelected()) {
            maxNbPomPerDaySlider.setVisible(false);
        }
        pomodoroModeBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Agile.Pomodoro Mode"),
                !PreferencesPanel.preferences.getAgileMode());
        pomodoroModeBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                maxNbPomPerActivitySlider.changeSlider(maxNbPomPerActivity);
                maxNbPomPerActivitySlider.setSliderValue(maxNbPomPerDaySlider.getSliderValue() < initMaxNbPomPerActivity ? maxNbPomPerDaySlider.getSliderValue() : initMaxNbPomPerActivity);
                maxNbPomPerDaySlider.setVisible(true);
                pomodoroModeBox.setSelected(true);
                agileModeBox.setSelected(false);
            }
        });
        plainHoursBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Plain hours"),
                PreferencesPanel.preferences.getPlainHours());
        plainHoursBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                plainHoursBox.setSelected(true);
                effectiveHoursBox.setSelected(false);

            }
        });
        effectiveHoursBox = new JCheckBox(
                Labels.getString("PreferencesPanel.Effective hours"),
                !PreferencesPanel.preferences.getPlainHours());
        effectiveHoursBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
                effectiveHoursBox.setSelected(true);
                plainHoursBox.setSelected(false);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        addAgileMode(gbc);
        gbc.gridy = 1;
        gbc.weighty = .5;
        gbc.fill = GridBagConstraints.BOTH;
        add(pomodoroSlider, gbc);
        gbc.gridy = 2;
        add(shortBreakSlider, gbc);
        gbc.gridy = 3;
        add(longBreakSlider, gbc);
        gbc.gridy = 4;
        add(maxNbPomPerActivitySlider, gbc);
        gbc.gridy = 5;
        add(maxNbPomPerDaySlider, gbc);
        gbc.gridy = 6;
        add(nbPomPerSetSlider, gbc);
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        addSounds(gbc);
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        addLocales(gbc);
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        if (SystemTray.isSupported()) {
            addSystemTray(gbc);
        }
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        addPlainHours(gbc);
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        addAlwaysOnTop(gbc);
    }

    private void addAgileMode(GridBagConstraints gbc) {
        JPanel agileMode = new JPanel();
        agileMode.setLayout(new GridBagLayout());
        GridBagConstraints gbcAgileMode = new GridBagConstraints();
        gbcAgileMode.fill = GridBagConstraints.HORIZONTAL;
        gbcAgileMode.anchor = GridBagConstraints.NORTH;
        gbcAgileMode.gridx = 0;
        gbcAgileMode.gridy = 0;
        agileMode.add(agileModeBox, gbcAgileMode);
        gbcAgileMode.gridx = 1;
        gbcAgileMode.gridy = 0;
        agileMode.add(pomodoroModeBox, gbcAgileMode);
        add(agileMode, gbc);
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
        localesComboBox.setRenderer(new AbstractComboBoxRenderer());
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

    private void addPlainHours(GridBagConstraints gbc) {
        JPanel plainHours = new JPanel();
        plainHours.setLayout(new GridBagLayout());
        GridBagConstraints gbcSystemTray = new GridBagConstraints();
        gbcSystemTray.fill = GridBagConstraints.HORIZONTAL;
        gbcSystemTray.anchor = GridBagConstraints.NORTH;
        gbcSystemTray.gridx = 0;
        gbcSystemTray.gridy = 0;
        plainHours.add(plainHoursBox, gbcSystemTray);
        gbcSystemTray.gridx = 1;
        gbcSystemTray.gridy = 0;
        plainHours.add(effectiveHoursBox, gbcSystemTray);
        add(plainHours, gbc);
    }

    private void addAlwaysOnTop(GridBagConstraints gbc) {
        JPanel alwaysOnTop = new JPanel();
        alwaysOnTop.setLayout(new GridBagLayout());
        GridBagConstraints gbcAgileMode = new GridBagConstraints();
        gbcAgileMode.fill = GridBagConstraints.HORIZONTAL;
        gbcAgileMode.anchor = GridBagConstraints.NORTH;
        gbcAgileMode.gridx = 0;
        gbcAgileMode.gridy = 0;
        alwaysOnTop.add(alwaysOnTopBox, gbcAgileMode);
        add(alwaysOnTop, gbc);
    }
}
