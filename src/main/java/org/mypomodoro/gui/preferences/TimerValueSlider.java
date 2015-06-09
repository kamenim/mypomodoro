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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.mypomodoro.Main;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

public class TimerValueSlider extends JPanel {

    private final JSlider slider;
    private final JLabel label = new JLabel();
    private final int unit;

    public TimerValueSlider(final PreferencesPanel controlPanel, int min, int max,
            int val, String name, final int recommendedMin,
            final int recommendedMax, int unit) {
        this.unit = unit;
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        add(new FormLabel(name));
        c.gridx = 1;
        slider = new JSlider(min, max, val);
        setSliderColor(recommendedMin, recommendedMax);
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                setSliderColor(recommendedMin, recommendedMax);
                setTexts();
                controlPanel.enableSaveButton();
                controlPanel.clearValidation();
            }
        });
        add(slider, c);
        c.gridx = 2;
        setTexts();
        add(label, c);
    }

    private void setTexts() {
        int sliderValue = slider.getValue();
        String text = " " + sliderValue + " ";
        if (unit == 0) {
            text += Labels.getString("PreferencesPanel.minutes");
        } else {
            text += Labels.getString("PreferencesPanel.pomodoros")
                    + " (" + TimeConverter.getLengthInHours(sliderValue) + ")";
        }
        label.setText(text);
    }

    public void setTexts(int pomodoroLength, int shortBreakLength, int longBreakLength, int nbPomPerSet, boolean isPlainHours) {
        int sliderValue = slider.getValue();
        String text = " " + sliderValue + " ";
        if (unit == 0) {
            text += Labels.getString("PreferencesPanel.minutes");
        } else {
            text += Labels.getString("PreferencesPanel.pomodoros")
                    + " (" + TimeConverter.getLengthInHours(sliderValue, pomodoroLength, shortBreakLength, longBreakLength, nbPomPerSet, isPlainHours) + ")";
        }
        label.setText(text);
    }

    public JSlider getSlider() {
        return slider;
    }

    public int getSliderValue() {
        return slider.getValue();
    }

    public void setSliderValue(int value) {
        slider.setValue(value);
    }

    public void setSliderColor(int recommendedMin, int recommendedMax) {
        if (getSliderValue() < recommendedMin
                || getSliderValue() > recommendedMax) {
            slider.setBackground(Color.orange);
        } else {
            slider.setBackground(Main.taskFinishedColor);
        }
    }

    public void changeSlider(int max) {
        slider.setMaximum(max);
        slider.repaint();
    }
}
