package org.mypomodoro.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * 
 * @author Phil Karoo
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
    //protected final JComboBox localesComboBox;

    public PreferencesInputForm(final ControlPanel controlPanel) {
        setBorder(new TitledBorder(new EtchedBorder(), "Preferences"));
        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        pomodoroSlider = new TimerValueSlider(controlPanel, 10, 45, ControlPanel.preferences.getPomodoroLength(), "Pomodoro Length: ", 25, 30, "minute");
        shortBreakSlider = new TimerValueSlider(controlPanel, 1, 10, ControlPanel.preferences.getShortBreakLength(), "Short Break Length: ", 3, 5, "minute");
        longBreakSlider = new TimerValueSlider(controlPanel, 5, 45, ControlPanel.preferences.getLongBreakLength(), "Long Break Length: ", 15, 30, "minute");
        maxNbPomPerActivitySlider = new TimerValueSlider(controlPanel, 1, 7, ControlPanel.preferences.getMaxNbPomPerActivity(), "Max nb pom/activity: ", 1, 5, "pomodoro");
        maxNbPomPerDaySlider = new TimerValueSlider(controlPanel, 1, 12, ControlPanel.preferences.getMaxNbPomPerDay(), "Max nb pom/day: ", 1, 10, "pomodoro");
        nbPomPerSetSlider = new TimerValueSlider(controlPanel, 3, 5, ControlPanel.preferences.getNbPomPerSet(), "Nb pom/set: ", 4, 4, "pomodoro");
        tickingBox = new JCheckBox("ticking", ControlPanel.preferences.getTicking());
        ringingBox = new JCheckBox("ringing", ControlPanel.preferences.getRinging());
        //String locales[] = new String[1];        
        //locales[0] = ControlPanel.preferences.getLocale();
        //localesComboBox = new JComboBox(locales);

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
        add(tickingBox, gbc);
        gbc.gridy = 7;
        add(ringingBox, gbc);        
        //gbc.gridy = 8;
        //add(localesComboBox, gbc);
    }
}